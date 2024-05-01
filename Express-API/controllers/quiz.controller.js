const { GradientLLM } = require("@langchain/community/llms/gradient_ai");

const { accessToken, workspaceId } = require("../config.json");

const db = require("../models");
const Quiz = db.quizzes;

class Question {
    question;
    options = [];
    answer;

    constructor(question) {
        this.question = question;
    }

    AddOption(option) {
        this.options.push(option.trim());
    }

    AddAnswer(answer) {
        this.answer = answer.trim();
    }
}

const sleep = (milliseconds) => new Promise((resolve) => setTimeout(resolve, milliseconds));

exports.findAll = async (req, res) => {
    // await sleep(1000)
    const user = req.user;
    const existingQuizzes = await Quiz.findAll({ where: { userid: user.id } });
    // console.log(existingQuizzes);
    let quizTopics = [];

    let QuizData = [];

    for (let quiz of existingQuizzes) {
        QuizData.push({
            id: quiz.id,
            Topic: quiz.topic,
            Questions: quiz.quiz,
        });
        quizTopics.push(quiz.topic);
    }

    console.log(QuizData);

    return res.json({ message: JSON.stringify({ QuizData }) });
    // return res.json({message: JSON.stringify({Topics:quiz})});
};

// Create and Save a new User
exports.create = async (req, res) => {
    const searchQuery = req.query.query;

    const user = req.user;

    // TODO: filter by time and within x minutes
    const existingQuizzes = await Quiz.findOne({ where: { userid: user.id, topic: searchQuery } });

    if (existingQuizzes && existingQuizzes.quiz == null) {
        // we can assume theres one being generated now
        console.log("one is being generated");
        if (Date.now() / 1000 - existingQuizzes.created > 60) {
            // older than 3 minutes
            await existingQuizzes.destroy();
        } else {
            return res.json({ message: "A quiz is currently being generated" });
        }
    } else if (existingQuizzes && existingQuizzes.quiz != null) {
        console.log("quiz with topic already exists!");
        // return res.json({ message: "A quiz already exists with this topic" });
    }

    const newQuiz = await Quiz.create({
        userid: user.id,
        topic: searchQuery,
        quiz: null,
        created: Date.now() / 1000,
    });

    // https://docs.gradient.ai/docs/models-1
    const model = new GradientLLM({
        gradientAccessKey: accessToken,
        workspaceId,
        modelSlug: "llama3-70b-chat",
        inferenceParameters: {
            maxGeneratedTokenCount: 500,
            temperature: Math.random() * 0.4,
        },
    });

    let quizData;
    let attempt = 0;
    do {
        attempt++;
        quizData = null;
        const query = `[INST] Generate a quiz with 5 questions to test students on the provided topic. For each question, generate 4 options, one of the options should be the correct answer, and then finally a correct answer. Format your response as follows:
        QUESTION: [Your question here]?
        OPTION A: [First option]
        OPTION B: [Second option]
        OPTION C: [Third option]
        OPTION D: [Fourth option]
        ANS: [Correct answer]

        ANS must be a duplicate of the correct answer from the given options. Do not add any asteriks around the questions or options. Do not number the questions. The questions should not have multi lines. The answer should not be a letter.
        Do not repeat the question in each of the answer options, answer options should be short, with few words as possible, this also applies to the correct answer (ANS).
        Ensure text is properly formatted. It needs to start with a question, then the options, and finally the correct answer. Follow this pattern for all questions. Quiz difficulty: ${Math.random()}`+ // same set of questions were previously being generated for the same topic, this math.rand() slightly randomises the questions
        `The topic is most likely related to computer science or IT. Here is the student topic:
        ${searchQuery}
        [/INST]`;

        const result = await model.invoke(query);
        console.log(result);
        console.log("parsing attempt", attempt);
        quizData = ParseQuizResponse(result);
        if (attempt > 3) {
            quizData = null;
            break;
        }
    } while (quizData.length < 5 || HasInvalidAnswers(quizData));

    if (quizData == null || quizData.length < 3) {
        return res.status(400).json({ message: "An error occurred, please try again. (Not enough questions were generated)" });
    }

    await Quiz.update(
        {
            quiz: quizData,
        },
        {
            where: { id: newQuiz.id },
        }
    );

    return res.json({
        message: JSON.stringify({
            QuizData: [
                {
                    id: newQuiz.id,
                    Topic: searchQuery,
                    Questions: quizData,
                },
            ],
        }),
    });
};

function HasInvalidAnswers(quizData) {
    for (let question of quizData) {
        if (!question.options.includes(question.answer)) {
            console.log(question.options, question.answer);
            return true;
        }
    }
    return false;
}

const ParseQuizResponse = (apiResponse) => {
    let wordsToRemove = [];
    for (let letter of ["A", "B", "C", "D"]) {
        wordsToRemove.push(`OPTION ${letter}: `);
        wordsToRemove.push(`${letter}. `);
        wordsToRemove.push(`${letter}: `);
        wordsToRemove.push(`${letter}) `);
    }

    let questions = [];
    const lines = apiResponse.split("\n");

    let inQuestion = false;

    let question;

    for (let line of lines) {
        line = line.trim();

        if (!inQuestion) {
            if (line.startsWith("QUESTION: ")) {
                inQuestion = true;
                question = new Question(line.substr("QUESTION: ".length));
            }
            continue;
        }

        if (inQuestion) {
            if (line.startsWith("OPTION ")) {
                question.AddOption(line.substr("OPTION X:".length));
                continue;
            }
            if (line.startsWith("ANS: ")) {
                let answer = line.substr("ANS: ".length);
                for (let filter of wordsToRemove) {
                    answer = answer.replace(filter, "");
                }
                question.AddAnswer(answer);
                questions.push(question);

                question = null;
                inQuestion = false;
            }
        }
    }

    return questions;
};

exports.getFeedback = async (req, res) => {
    const data = { ...req.body };
    try {
        const response = await ExplainQuizQuestion(data.question, JSON.parse(data.options), data.correctAnswer, data.usersGuess);
        return res.status(200).json({ message: response });
    } catch (e) {
        console.log(" an error ocurred", response ? response : "N/A", e);
        return res.status(400).json({ message: "An error occurred" });
    }
};

async function ExplainQuizQuestion(question, options, answer, usersGuess) {
    const model = new GradientLLM({
        gradientAccessKey: accessToken,
        workspaceId,
        modelSlug: "llama3-70b-chat",
        inferenceParameters: {
            maxGeneratedTokenCount: 500,
            temperature: Math.random() * 0.4,
        },
    });

    console.log(`[${options.join(", ")}]`);
    const query = `[INST]
    A student has attempted the following quiz question.
    The question was: "${question}".
    If you are unsure what the question's topic is about, assume it is something computer science/IT related.
    The available options to choose from were [${options.join(", ")}]
    The correct answer is "${answer}", and the student selected "${usersGuess}"

    You need to provide feedback based on whether the student is correct or wrong.
    If the student's answer matches the correct answer, confirm the student is correct, and explain why the answer is correct.
    If the student's answer is incorrect, explain why the student's answer is incorrect, and explain the correct answer.
    Your response should not be any longer than 2 sentences. Your response should contain nothing but the feedback.
    Do not refer to the student and directly address the question and answer.
    [/INST]`;

    const result = await model.invoke(query);
    console.log(result);
    return result;
}

// ExplainQuizQuestion("Discrete math", "What is the number of subsets of a set with n elements?", ["2^n","n^2","2n","n!"], "2^n", "n!");
// ExplainQuizQuestion("What is the principle of inclusion-exclusion used for?", ["Counting permutations","Counting combinations","Counting the number of elements in a union of sets","Counting the number of elements in an intersection of sets"], "Counting the number of elements in a union of sets", "Counting the number of elements in an intersection of sets");
