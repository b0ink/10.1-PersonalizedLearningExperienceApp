const { DataTypes } = require("sequelize");

module.exports = (sequelize, Sequelize) => {
    const Quiz = sequelize.define(
        "quiz",
        {
            id: {
                type: DataTypes.INTEGER,
                primaryKey: true,
                autoIncrement: true,
            },
            userid: {
                type: DataTypes.INTEGER,
                allowNull: false,
            },
            topic: {
                type: DataTypes.STRING(32),
                allowNull: false,
            },
            quiz: {
                type: DataTypes.JSON,
                allowNull: true,
                defaultValue: null
            },
            created: {
                type: DataTypes.INTEGER
            }
        },
        {
            timestamps: false,
            tableName: 'quizzes'
        }
    );

    return Quiz;
};
