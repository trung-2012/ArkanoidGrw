package game.arkanoid.controllers;

import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.util.Duration;
import game.arkanoid.player_manager.Player;

import java.io.IOException;

public class LoadingController {

    @FXML
    private Label noteLabel;

    private Player currentPlayer;
    private int targetLevel;
    private boolean loadFromSave;

    /**
     * Hiển thị note tương ứng với level
     */
    public void setLevel(int level) {
        this.targetLevel = level;
        String note = getLevelNote(level);
        if (noteLabel != null) {
            noteLabel.setText(note);
        }
    }

    /**
     * Set player để truyền sang MainController
     */
    public void setPlayer(Player player) {
        this.currentPlayer = player;
    }

    /**
     * Set flag để biết load từ save hay không
     */
    public void setLoadFromSave(boolean loadFromSave) {
        this.loadFromSave = loadFromSave;
    }

    /**
     * Bắt đầu loading (hiển thị 2 giây rồi chuyển sang game)
     */
    public void startLoading() {
        PauseTransition pause = new PauseTransition(Duration.seconds(2));
        pause.setOnFinished(event -> {
            try {
                loadGameView();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        pause.play();
    }

    /**
     * Load MainView (màn hình game)
     */
    private void loadGameView() throws IOException {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/game/arkanoid/fxml/MainView.fxml"));
        Parent root = loader.load();

        MainController controller = loader.getController();
        controller.setPlayer(currentPlayer);
        controller.setLoadFromSave(loadFromSave);
        
        // Nếu không load từ save và có target level cụ thể, set target level
        if (!loadFromSave && targetLevel > 1) {
            controller.setTargetLevel(targetLevel);
        }

        Stage stage = (Stage) noteLabel.getScene().getWindow();
        stage.setScene(new Scene(root, 800, 600));
    }

    /**
     * Lấy note tương ứng với level
     */
    private String getLevelNote(int level) {
        switch (level) {
            case 1:
                return "*Note: \"Thể thao là không ngừng bỏ cuộc\"";
            case 2:
                return "*Note: \"Chơi game 180p mỗi ngày sẽ có hại cho sức khỏe\"";
            case 3:
                return "*Note: \"Trò chơi chỉ là giả định, xa rời thực tại\"";
            case 4:
                return "*Note: \"Se co nhung con cho phai tra gia\"";
            default:
                return "*Note: \"Loading...\"";
        }
    }
}
