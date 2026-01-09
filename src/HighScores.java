import java.sql.*;
import java.util.ArrayList;

public class HighScores {
    private static HighScores instance;
    private final Connection connection;

    private HighScores() throws SQLException, ClassNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        this.connection = DriverManager.getConnection(
                "jdbc:mysql://127.0.0.1:3306/leaderboard", "root", ""
        );
    }
    public static HighScores instance() throws SQLException, ClassNotFoundException {
        if (HighScores.instance == null) {
            HighScores.instance = new HighScores();
        }
        return instance;
    }

    public void insertScore(String playerName, int score) throws SQLException {
        PreparedStatement checkPlayer = this.connection.prepareStatement(
                "SELECT * FROM highscores WHERE name = ?"
        );
        checkPlayer.setString(1, playerName);

        ResultSet resultSet = checkPlayer.executeQuery();

        if (resultSet.next()) {
            int currScore = resultSet.getInt("score");
            if(score>currScore) {
                PreparedStatement updateScore = this.connection.prepareStatement(
                        "UPDATE highscores SET score = ? WHERE name = ?"
                );
                updateScore.setInt(1, score);
                updateScore.setString(2, playerName);
                updateScore.executeUpdate();
            }
        } else {
            PreparedStatement insertScore = this.connection.prepareStatement(
                    "INSERT INTO highscores (name, score) VALUES (?, ?)"
            );
            insertScore.setString(1, playerName);
            insertScore.setInt(2, score);
            insertScore.executeUpdate();
        }
    }
    public ArrayList<HighScore> getTopScores() throws SQLException {
        ArrayList<HighScore> topScores = new ArrayList<>();

        // Select the top 10 scores ordered by score descending
        PreparedStatement getTopScores = this.connection.prepareStatement(
                "SELECT name, score FROM highscores ORDER BY score DESC LIMIT 10"
        );

        ResultSet resultSet = getTopScores.executeQuery();

        while (resultSet.next()) {
            String name = resultSet.getString("name");
            int score = resultSet.getInt("score");
            topScores.add(new HighScore(name, score));
        }

        return topScores;
    }
}