package game.arkanoid.player_manager;

import game.arkanoid.controllers.StartMenuController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LeaderboardController {

    @FXML private Label playerHighScoreLabel;
    @FXML private Label rank1Label;
    @FXML private Label rank2Label;
    @FXML private Label rank3Label;
    @FXML private Button backButton;
    @FXML private ImageView backImageView;

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
                "🏆 1st " + top3.get(0).getNickname() + " — " + top3.get(0).getHighScore() : "1. ---");

        rank2Label.setText(top3.size() > 1 ?
                "🏆 2nd " + top3.get(1).getNickname() + " — " + top3.get(1).getHighScore() : "2. ---");

        rank3Label.setText(top3.size() > 2 ?
                "🏆 3rd " + top3.get(2).getNickname() + " — " + top3.get(2).getHighScore() : "3. ---");
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

    @FXML
    private void onButtonMouseEntered() {
        if (backImageView != null) {
            backImageView.setImage(new Image(getClass().getResourceAsStream("/game/arkanoid/images/back c.png")));
        }
    }

    @FXML
    private void onButtonMouseExited() {
        if (backImageView != null) {
            backImageView.setImage(new Image(getClass().getResourceAsStream("/game/arkanoid/images/back.png")));
        }
    }
}
