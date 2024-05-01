const express = require("express");
const jwt = require("jsonwebtoken");

const app = express();
const PORT = process.env.PORT || 3000;
var bodyParser = require("body-parser");

const API_VERSION = "v0";

app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: false }));

require("./routes/user.routes")(app, API_VERSION);
require("./routes/quiz.routes")(app, API_VERSION);

// Start the Express server
app.listen(PORT, () => {
    console.log(`Server is running on port ${PORT}`);
});
