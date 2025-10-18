package game.arkanoid.controllers;

import javafx.animation.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.Random;

public class StartMenuController {
    @FXML
    private StackPane root;

    private static final int STAR_COUNT = 300;
    private final Random random = new Random();

    @FXML
    public void initialize() {
        createStarField();
    }

    private void createStarField() {
        root.setStyle("-fx-background-color: black;");

        Random random = new Random();

        // Timeline tạo sao liên tục
        Timeline starSpawner = new Timeline(new KeyFrame(Duration.millis(100), e -> {
            // Tạo ngôi sao mới
            double x = random.nextDouble() * 800 - 400;
            double startY = -320 - random.nextDouble() * 100;
            double radius = random.nextDouble() * 1.5 + 0.5;

            Circle star = new Circle(radius, Color.WHITE);
            star.setTranslateX(x);
            star.setTranslateY(startY);

            // Hiệu ứng nhấp nháy
            FadeTransition twinkle = new FadeTransition(Duration.seconds(1 + random.nextDouble() * 1.5), star);
            twinkle.setFromValue(0.4);
            twinkle.setToValue(1.0);
            twinkle.setCycleCount(Animation.INDEFINITE);
            twinkle.setAutoReverse(true);
            twinkle.play();

            // Hiệu ứng rơi
            TranslateTransition move = new TranslateTransition(Duration.seconds(15 + random.nextDouble() * 4), star);
            move.setByY(700);
            move.setInterpolator(Interpolator.LINEAR);
            move.setOnFinished(ev -> root.getChildren().remove(star)); // Xóa sao khi rơi xong
            move.play();

            // Thêm vào nền
            root.getChildren().add(0, star);
        }));

        starSpawner.setCycleCount(Animation.INDEFINITE);
        starSpawner.play();
    }


    @FXML
    private void startGame(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/game/arkanoid/fxml/MainView.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, 800, 600));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void openSettings() {
        System.out.println("Open settings");
    }

    @FXML
    private void exitGame() {
        System.out.println("Exit game");
        System.exit(0);
    }
}
