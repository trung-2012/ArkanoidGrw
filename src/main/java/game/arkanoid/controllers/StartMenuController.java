package game.arkanoid.controllers;

import game.arkanoid.player_manager.GameDataManager;
import game.arkanoid.player_manager.Player;
import game.arkanoid.managers.SoundManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.event.ActionEvent;
import javafx.scene.control.Label;

import java.io.IOException;

public class StartMenuController {

    @FXML
    private javafx.scene.image.ImageView startImageView;
    @FXML
    private javafx.scene.image.ImageView continueImageView;
    @FXML
    private javafx.scene.image.ImageView settingsImageView;
    @FXML
    private javafx.scene.image.ImageView exitImageView;
    @FXML
    private javafx.scene.image.ImageView logoutImageView;

    @FXML
    private Label nicknameLabel;

    @FXML
    private javafx.scene.image.ImageView leaderboardImageView;

    private Player currentPlayer;

    @FXML
    private void initialize() {
        // Phát nhạc menu
        SoundManager.getInstance().playBackgroundMusic(
            "src/main/resources/game/arkanoid/sounds/menu_music.mp3", true);
    }

    public void setPlayer(Player p) {
        this.currentPlayer = p;
        if (nicknameLabel != null && p != null) {
            nicknameLabel.setText("Hi, " + p.getNickname());
        }
    }

    // Xử lý sự kiện khi di chuột vào button
    @FXML
    private void onButtonMouseEntered(javafx.scene.input.MouseEvent event) {
        javafx.scene.control.Button sourceButton = (javafx.scene.control.Button) event.getSource();
        String buttonId = sourceButton.getId();
        javafx.scene.image.Image hoverImage = null;

        switch (buttonId) {
            case "startButton":
                hoverImage = new javafx.scene.image.Image(
                        getClass().getResource("/game/arkanoid/images/start c.png").toExternalForm());
                startImageView.setImage(hoverImage);
                break;
            case "continueButton":
                hoverImage = new javafx.scene.image.Image(
                        getClass().getResource("/game/arkanoid/images/continue c.png").toExternalForm());
                continueImageView.setImage(hoverImage);
                break;
            case "settingsButton":
                hoverImage = new javafx.scene.image.Image(
                        getClass().getResource("/game/arkanoid/images/settings c.png").toExternalForm());
                settingsImageView.setImage(hoverImage);
                break;
            case "exitButton":
                hoverImage = new javafx.scene.image.Image(
                        getClass().getResource("/game/arkanoid/images/exit c.png").toExternalForm());
                exitImageView.setImage(hoverImage);
                break;
            case "leaderboardButton":
                hoverImage = new javafx.scene.image.Image(
                        getClass().getResource("/game/arkanoid/images/leaderboardDemo c.png").toExternalForm()
                );
                leaderboardImageView.setImage(hoverImage);
                break;
            case "logoutButton":
                hoverImage = new javafx.scene.image.Image(
                        getClass().getResource("/game/arkanoid/images/logout c.png").toExternalForm()
                );
                logoutImageView.setImage(hoverImage);
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
            case "startButton":
                normalImage = new javafx.scene.image.Image(
                        getClass().getResource("/game/arkanoid/images/start.png").toExternalForm());
                startImageView.setImage(normalImage);
                break;
            case "continueButton":
                normalImage = new javafx.scene.image.Image(
                        getClass().getResource("/game/arkanoid/images/continue.png").toExternalForm());
                continueImageView.setImage(normalImage);
                break;
            case "settingsButton":
                normalImage = new javafx.scene.image.Image(
                        getClass().getResource("/game/arkanoid/images/settings.png").toExternalForm());
                settingsImageView.setImage(normalImage);
                break;
            case "exitButton":
                normalImage = new javafx.scene.image.Image(
                        getClass().getResource("/game/arkanoid/images/exit.png").toExternalForm());
                exitImageView.setImage(normalImage);
                break;
            case "leaderboardButton":
                normalImage = new javafx.scene.image.Image(
                        getClass().getResource("/game/arkanoid/images/leaderboardDemo.png").toExternalForm());
                leaderboardImageView.setImage(normalImage);
                break;
            case "logoutButton":
                normalImage = new javafx.scene.image.Image(
                        getClass().getResource("/game/arkanoid/images/logout.png").toExternalForm());
                logoutImageView.setImage(normalImage);
                break;
        }
    }

    // Bắt đầu trò chơi
    @FXML
    private void startGame(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/game/arkanoid/fxml/MainView.fxml"));
            Parent root = loader.load();

            // truyền player sang MainController
            MainController controller = loader.getController();
            controller.setPlayer(currentPlayer);
            controller.setLoadFromSave(false); // Start game mới

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, 800, 600));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Continue game từ save
    @FXML
    private void continueGame(ActionEvent event) {
        try {
            // Kiểm tra xem có save game không
            if (!GameDataManager.hasSaveGame()) {
                // Nếu không có save game, start game mới từ level 1
                System.out.println("No save game found. Starting new game from level 1.");
                startGame(event);
                return;
            }

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/game/arkanoid/fxml/MainView.fxml"));
            Parent root = loader.load();

            // truyền player sang MainController
            MainController controller = loader.getController();
            controller.setPlayer(currentPlayer);
            controller.setLoadFromSave(true); // Load từ save

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, 800, 600));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Mở cửa sổ Cài đặt (Settings)
    @FXML
    private void openSettings(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/game/arkanoid/fxml/SettingsView.fxml"));
            Parent root = loader.load();

            SettingsController settingsController = loader.getController();
            settingsController.setMainController(null);   //  báo cho Settings biết là mở từ menu
            settingsController.setPlayer(currentPlayer);  //  truyền Player vào SettingsController

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, 800, 600));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Thoát khỏi ứng dụng hoàn toàn.
    @FXML
    private void exitGame() {
        System.out.println("Exit game");
        System.exit(0);
    }

    @FXML
    private void logout(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(
                    getClass().getResource("/game/arkanoid/fxml/LoginView.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, 800, 600));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void openLeaderboard(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/game/arkanoid/fxml/Leaderboard.fxml")
            );
            Parent root = loader.load();

            // truyền player vào LeaderboardController (nếu cần highlight rank)
            game.arkanoid.player_manager.LeaderboardController controller = loader.getController();
            controller.setCurrentPlayer(currentPlayer);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, 800, 600));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
