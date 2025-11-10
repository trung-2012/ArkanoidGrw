package game.arkanoid.controllers;

import game.arkanoid.managers.SoundManager;
import game.arkanoid.player_manager.Player;
import game.arkanoid.player_manager.PlayerData;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.event.ActionEvent;
import javafx.scene.control.Label;

import java.io.IOException;
import java.util.ArrayList;

public class GameOverController {
    @FXML
    private Label scoreLabel;
    @FXML
    private javafx.scene.image.ImageView restartImageView;
    @FXML
    private javafx.scene.image.ImageView menuImageView;
    
    private Player currentPlayer;

    @FXML
    public void initialize() {
        // Phát nhạc nền game over
        SoundManager.getInstance().playBackgroundMusic(
            "src/main/resources/game/arkanoid/sounds/endgame.mp3", false);
    }

    // Xử lý sự kiện khi di chuột vào button
    @FXML
    private void onButtonMouseEntered(javafx.scene.input.MouseEvent event) {
        javafx.scene.control.Button sourceButton = (javafx.scene.control.Button) event.getSource();
        String buttonId = sourceButton.getId();
        javafx.scene.image.Image hoverImage = null;

        switch (buttonId) {
            case "restartButton":
                hoverImage = new javafx.scene.image.Image(
                        getClass().getResource("/game/arkanoid/images/PlayAgain c.png").toExternalForm());
                restartImageView.setImage(hoverImage);
                break;
            case "menuButton":
                hoverImage = new javafx.scene.image.Image(
                        getClass().getResource("/game/arkanoid/images/MainMenu c.png").toExternalForm());
                menuImageView.setImage(hoverImage);
                break;
        }
    }

    // Xử lý sự kiện khi di chuột ra khỏi button
    @FXML
    private void onButtonMouseExited(javafx.scene.input.MouseEvent event) {
        javafx.scene.control.Button sourceButton = (javafx.scene.control.Button) event.getSource();
        String buttonId = sourceButton.getId();
        javafx.scene.image.Image normalImage = null;

        switch (buttonId) {
            case "restartButton":
                normalImage = new javafx.scene.image.Image(
                        getClass().getResource("/game/arkanoid/images/PlayAgain.png").toExternalForm());
                restartImageView.setImage(normalImage);
                break;
            case "menuButton":
                normalImage = new javafx.scene.image.Image(
                        getClass().getResource("/game/arkanoid/images/MainMenu.png").toExternalForm());
                menuImageView.setImage(normalImage);
                break;
        }
    }

    // Chơi lại
    @FXML
    private void restartGame(ActionEvent event) {
        try {
            // Dừng nhạc game over trước khi chuyển scene
            SoundManager.getInstance().stopBackgroundMusic();
            
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/game/arkanoid/fxml/MainView.fxml"));
            Parent root = loader.load();
            
            // Truyền player vào MainController
            MainController controller = loader.getController();
            controller.setPlayer(currentPlayer);
            
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, 800, 600));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Thiết lập điểm số cuối cùng để hiển thị
    public void setFinalScore(int score) {
        if (scoreLabel != null) {
            scoreLabel.setText("Final Score: " + score);
        }
        
        // Cập nhật high score nếu điểm mới cao hơn
        if (currentPlayer != null && score > currentPlayer.getHighScore()) {
            currentPlayer.setHighScore(score);
            
            // Lưu vào file
            ArrayList<Player> players = PlayerData.loadPlayers();
            for (int i = 0; i < players.size(); i++) {
                if (players.get(i).getUsername().equals(currentPlayer.getUsername())) {
                    players.set(i, currentPlayer);
                    break;
                }
            }
            PlayerData.savePlayers(players);
            
            System.out.println("New high score saved: " + score + " for player " + currentPlayer.getNickname());
        }
    }
    
    // Thiết lập player
    public void setPlayer(Player player) {
        this.currentPlayer = player;
    }

    // Trở về menu chính
    @FXML
    private void returnToMenu(ActionEvent event) {
        try {
            // Dừng nhạc game over trước khi chuyển scene
            SoundManager.getInstance().stopBackgroundMusic();
            
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/game/arkanoid/fxml/StartMenu.fxml"));
            Parent root = loader.load();
            
            // Truyền player vào StartMenuController
            StartMenuController controller = loader.getController();
            controller.setPlayer(currentPlayer);
            
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, 800, 600));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
