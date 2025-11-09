package game.arkanoid.player_manager;

import game.arkanoid.controllers.StartMenuController;
import game.arkanoid.player_manager.Player;
import game.arkanoid.player_manager.PlayerData;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LeaderboardController {

    @FXML private Label playerHighScoreLabel;
    @FXML private Label rank1Label;
    @FXML private Label rank2Label;
    @FXML private Label rank3Label;

    private Player currentPlayer;

    public void setCurrentPlayer(Player p) {
        this.currentPlayer = p;
        loadLeaderboard();
    }

    private void loadLeaderboard() {
        ArrayList<Player> players = PlayerData.loadPlayers();

        if (currentPlayer != null)
            playerHighScoreLabel.setText("Your High Score: " + currentPlayer.getHighScore());

        players.sort((a, b) -> Integer.compare(b.getHighScore(), a.getHighScore()));
        List<Player> top3 = players.stream().limit(3).toList();

        rank1Label.setText(top3.size() > 0 ?
                "1. " + top3.get(0).getNickname() + " — " + top3.get(0).getHighScore() : "1. ---");

        rank2Label.setText(top3.size() > 1 ?
                "2. " + top3.get(1).getNickname() + " — " + top3.get(1).getHighScore() : "2. ---");

        rank3Label.setText(top3.size() > 2 ?
                "3. " + top3.get(2).getNickname() + " — " + top3.get(2).getHighScore() : "3. ---");
    }

    @FXML
    private void backToMenu() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/game/arkanoid/fxml/StartMenu.fxml")
            );
            Parent root = loader.load();

            StartMenuController controller = loader.getController();
            controller.setPlayer(currentPlayer);

            Stage stage = (Stage) playerHighScoreLabel.getScene().getWindow();
            stage.setScene(new Scene(root, 800, 600));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
