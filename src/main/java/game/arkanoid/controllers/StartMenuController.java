package game.arkanoid.controllers;

import game.arkanoid.models.Player;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;

public class StartMenuController {

    @FXML
    private javafx.scene.image.ImageView startImageView;
    @FXML
    private javafx.scene.image.ImageView settingsImageView;
    @FXML
    private javafx.scene.image.ImageView exitImageView;
    @FXML
    private javafx.scene.image.ImageView logoutImageView;

    @FXML
    private Label nicknameLabel;

    private Player currentPlayer;

    @FXML
    public void initialize() {
        javafx.scene.text.Font.loadFont(
                getClass().getResourceAsStream("/game/arkanoid/fonts/Orbitron-VariableFont_wght.ttf"), 24
        );
    }

    public void setPlayer(Player p) {
        this.currentPlayer = p;

        nicknameLabel.setText("Hi, " + p.getNickname());
        nicknameLabel.setStyle(
                "-fx-font-family: 'Orbitron';" +
                        "-fx-font-size: 20;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;"
        );

        nicknameLabel.getTransforms().clear();
        nicknameLabel.getTransforms().add(new javafx.scene.transform.Shear(-0.25, 0));
    }


    @FXML
    private void onButtonMouseEntered(javafx.scene.input.MouseEvent event) {
        javafx.scene.control.Button btn = (javafx.scene.control.Button) event.getSource();
        String id = btn.getId();
        javafx.scene.image.Image img = null;

        switch (id) {
            case "startButton":
                img = new javafx.scene.image.Image(getClass().getResource("/game/arkanoid/images/start c.png").toExternalForm());
                startImageView.setImage(img);
                break;
            case "settingsButton":
                img = new javafx.scene.image.Image(getClass().getResource("/game/arkanoid/images/settings c.png").toExternalForm());
                settingsImageView.setImage(img);
                break;
            case "exitButton":
                img = new javafx.scene.image.Image(getClass().getResource("/game/arkanoid/images/exit c.png").toExternalForm());
                exitImageView.setImage(img);
                break;
        }
    }

    @FXML
    private void onButtonMouseExited(javafx.scene.input.MouseEvent event) {
        javafx.scene.control.Button btn = (javafx.scene.control.Button) event.getSource();
        String id = btn.getId();
        javafx.scene.image.Image img = null;

        switch (id) {
            case "startButton":
                img = new javafx.scene.image.Image(getClass().getResource("/game/arkanoid/images/start.png").toExternalForm());
                startImageView.setImage(img);
                break;
            case "settingsButton":
                img = new javafx.scene.image.Image(getClass().getResource("/game/arkanoid/images/settings.png").toExternalForm());
                settingsImageView.setImage(img);
                break;
            case "exitButton":
                img = new javafx.scene.image.Image(getClass().getResource("/game/arkanoid/images/exit.png").toExternalForm());
                exitImageView.setImage(img);
                break;
        }
    }

    @FXML
    private void startGame(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/game/arkanoid/fxml/MainView.fxml"));
            Parent root = loader.load();

            MainController controller = loader.getController();
            controller.setPlayer(currentPlayer);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, 800, 600));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void openSettings(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/game/arkanoid/fxml/SettingsView.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, 800, 600));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void exitGame() {
        System.exit(0);
    }

    @FXML
    private void logout(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/game/arkanoid/fxml/LoginView.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, 800, 600));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
