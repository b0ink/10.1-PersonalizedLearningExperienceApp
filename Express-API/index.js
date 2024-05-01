const express = require("express");
const jwt = require("jsonwebtoken");

const app = express();
const PORT = process.env.PORT || 3000;
var bodyParser = require("body-parser");

const {secretKey} = require('./config.json');
const API_VERSION = "v0";


app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: false }));

require("./routes/user.routes")(app, API_VERSION);
require("./routes/quiz.routes")(app, API_VERSION);

app.get("/protected", authenticateToken, (req, res) => {
    console.log("WE ARE AUTHORISED", req.user);
    res.json({ message: "Protected resource" });
});

// Middleware to authenticate JWT token
function authenticateToken(req, res, next) {
    const authHeader = req.headers["authorization"];
    const token = authHeader && authHeader.split(" ")[1];
    if (!token) return res.status(401).json({ message: "Unauthorized" });

    jwt.verify(token, secretKey, (err, user) => {
        if (err) return res.status(403).json({ message: "Forbidden" });
        req.user = user;
        next();
    });
}


// Start the Express server
app.listen(PORT, () => {
    console.log(`Server is running on port ${PORT}`);
});
