package game.arkanoid;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.scene.control.Button;

import java.util.ArrayList;
import java.util.List;

public class SkinSelectController {

    @FXML private ImageView previewImage;
    @FXML private Button prevBtn, nextBtn, selectBtn, backBtn;

    private List<String> ballSkins;
    private int currentIndex = 0;
    private String selectedSkinPath;

    @FXML
    public void initialize() {
        // Danh sách skin
        ballSkins = new ArrayList<>();
        ballSkins.add("/game/arkanoid/images/balls/ball.png");
        ballSkins.add("/game/arkanoid/images/paddles/paddle.png");
        ballSkins.add("/game/arkanoid/images/balls/ball.png");

        updatePreview();

        prevBtn.setOnAction(e -> {
            currentIndex = (currentIndex - 1 + ballSkins.size()) % ballSkins.size();
            updatePreview();
        });

        nextBtn.setOnAction(e -> {
            currentIndex = (currentIndex + 1) % ballSkins.size();
            updatePreview();
        });

        selectBtn.setOnAction(e -> {
            selectedSkinPath = ballSkins.get(currentIndex);
            MenuControll.setSelectedBallImage(new Image(getClass().getResourceAsStream(selectedSkinPath)));
            goBack();
        });

        backBtn.setOnAction(e -> goBack());
    }

    private void updatePreview() {
        previewImage.setImage(new Image(getClass().getResourceAsStream(ballSkins.get(currentIndex))));
    }

    private void goBack() {
        try {
            Stage stage = (Stage) previewImage.getScene().getWindow();
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("MenuView.fxml"));
            stage.getScene().setRoot(loader.load());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
