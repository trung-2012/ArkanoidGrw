package game.arkanoid.controllers;

import game.arkanoid.player_manager.Player;
import game.arkanoid.managers.SoundManager;
import javafx.scene.control.Slider;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.scene.input.MouseEvent;

import java.io.IOException;

import game.arkanoid.utils.GameSettings;

public class SettingsController {

    private MainController mainController; // Để biết có đang từ game hay không
    private Stage settingsStage; // Stage của Settings window
    private Player currentPlayer;

    // Các ImageView trong Settings
    @FXML
    private ImageView ballImageView;
    @FXML
    private ImageView paddleImageView;

    @FXML
    private ImageView leftBallImageView;
    @FXML
    private ImageView rightBallImageView;
    @FXML
    private ImageView leftPaddleImageView;
    @FXML
    private ImageView rightPaddleImageView;

    @FXML
    private ImageView saveImageView;
    @FXML
    private ImageView previewImageView;
    @FXML
    private ImageView confirmBallImageView;
    @FXML
    private ImageView confirmPaddleImageView;
    @FXML
    private Slider musicSlider;
    @FXML
    private ImageView musicVolumeIcon;
    @FXML
    private Slider sfxSlider;
    @FXML
    private ImageView sfxVolumeIcon;

    // Tạo biến để giữ ảnh icon
    private Image musicOnIcon;
    private Image musicOffIcon;
    private Image sfxOnIcon;
    private Image sfxOffIcon;

    // Dsach Skins
    private final String[] ballSkins = {
            "/game/arkanoid/images/Ball.png",
            "/game/arkanoid/images/Ball1.png",
            "/game/arkanoid/images/Ball2.png",
            "/game/arkanoid/images/Ball3.png",
            "/game/arkanoid/images/Ball4.png"
    };
    private final String[] paddleSkins = {
            "/game/arkanoid/images/Paddle.png",
            "/game/arkanoid/images/Paddle1.png",
            "/game/arkanoid/images/Paddle2.png",
            "/game/arkanoid/images/Paddle3.png",
            "/game/arkanoid/images/Paddle4.png",
            "/game/arkanoid/images/Paddle5.png"
    };

    private int ballIndex = 0;
    private int paddleIndex = 0;

    // Setter để PauseController có thể truyền mainController vào
    public void setMainController(MainController mainController) {
        this.mainController = mainController;
        // Lưu vào GameSettings để giữ context khi navigate
        GameSettings.getInstance().setCurrentMainController(mainController);
    }

    // Setter để PauseController có thể truyền settingsStage vào
    public void setSettingsStage(Stage settingsStage) {
        this.settingsStage = settingsStage;
        // Lưu vào GameSettings để giữ context khi navigate
        GameSettings.getInstance().setCurrentSettingsStage(settingsStage);
    }
    
    @FXML
    private void initialize() {
        // Khôi phục context từ GameSettings khi controller được tạo lại
        MainController savedMainController = GameSettings.getInstance().getCurrentMainController();
        Stage savedSettingsStage = GameSettings.getInstance().getCurrentSettingsStage();
        
        if (savedMainController != null) {
            this.mainController = savedMainController;
        }
        if (savedSettingsStage != null) {
            this.settingsStage = savedSettingsStage;
        }

        try {
            musicOnIcon = new Image(getClass().getResourceAsStream("/game/arkanoid/images/music_on.png"));
            musicOffIcon = new Image(getClass().getResourceAsStream("/game/arkanoid/images/music_off.png"));
            sfxOnIcon = new Image(getClass().getResourceAsStream("/game/arkanoid/images/sound_on.png"));
            sfxOffIcon = new Image(getClass().getResourceAsStream("/game/arkanoid/images/sound_off.png"));
        } catch (Exception e) {
            System.err.println("Lỗi: Không thể tải hình ảnh icon âm thanh!");
            e.printStackTrace();
        }

        // Lấy instance duy nhất của SoundManager
        SoundManager soundManager = SoundManager.getInstance();

        // --- Cấu hình Music Slider ---
        if (musicSlider != null) {
            // Đặt giá trị ban đầu cho Slider
            musicSlider.setValue(soundManager.getMusicVolume());
            // Đặt icon ban đầu dựa trên âm lượng
            updateMusicIcon(soundManager.getMusicVolume());
            // Thêm Listener để cập nhật SoundManager khi kéo
            musicSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
                soundManager.setMusicVolume(newValue.doubleValue());
                updateMusicIcon(newValue.doubleValue()); // Cập nhật icon khi kéo
            });
        }

        // --- Cấu hình SFX Slider ---
        if (sfxSlider != null) {
            // Đặt giá trị ban đầu cho Slider
            sfxSlider.setValue(soundManager.getSfxVolume());
            // Đặt icon ban đầu dựa trên âm lượng
            updateSfxIcon(soundManager.getSfxVolume());
            // Thêm Listener để cập nhật SoundManager khi kéo
            sfxSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
                soundManager.setSfxVolume(newValue.doubleValue());
                updateSfxIcon(newValue.doubleValue()); // Cập nhật icon khi kéo
            });
        }
    }

    private void updateMusicIcon(double volume) {
        if (musicVolumeIcon != null) {
            if (volume == 0.0) {
                musicVolumeIcon.setImage(musicOffIcon);
            } else {
                musicVolumeIcon.setImage(musicOnIcon);
            }
        }
    }

    private void updateSfxIcon(double volume) {
        if (sfxVolumeIcon != null) {
            if (volume == 0.0) {
                sfxVolumeIcon.setImage(sfxOffIcon);
            } else {
                sfxVolumeIcon.setImage(sfxOnIcon);
            }
        }
    }

    // Xử lý sự kiện khi di chuột vào button
    @FXML
    private void onButtonMouseEntered(MouseEvent event) {
        Button btn = (Button) event.getSource();
        String id = btn.getId();

        switch (id) {
            case "previewButton":
                previewImageView.setImage(
                        new Image(getClass().getResource("/game/arkanoid/images/preview c.png").toExternalForm()));
                break;
            case "saveButton":
                saveImageView.setImage(
                        new Image(getClass().getResource("/game/arkanoid/images/save c.png").toExternalForm()));
                break;
            case "confirmBallButton":
                confirmBallImageView.setImage(
                        new Image(getClass().getResource("/game/arkanoid/images/change c.png").toExternalForm()));
                break;
            case "confirmPaddleButton":
                confirmPaddleImageView.setImage(
                        new Image(getClass().getResource("/game/arkanoid/images/change c.png").toExternalForm()));
                break;
            case "leftBallButton":
                leftBallImageView.setImage(
                        new Image(getClass().getResource("/game/arkanoid/images/left c.png").toExternalForm()));
                break;
            case "rightBallButton":
                rightBallImageView.setImage(
                        new Image(getClass().getResource("/game/arkanoid/images/right c.png").toExternalForm()));
                break;
            case "leftPaddleButton":
                leftPaddleImageView.setImage(
                        new Image(getClass().getResource("/game/arkanoid/images/left c.png").toExternalForm()));
                break;
            case "rightPaddleButton":
                rightPaddleImageView.setImage(
                        new Image(getClass().getResource("/game/arkanoid/images/right c.png").toExternalForm()));
                break;
        }
    }

    // Xử lý sự kiện khi di chuột ra khỏi button
    @FXML
    private void onButtonMouseExited(MouseEvent event) {
        Button btn = (Button) event.getSource();
        String id = btn.getId();

        switch (id) {
            case "previewButton":
                previewImageView.setImage(
                    new Image(getClass().getResource("/game/arkanoid/images/preview.png").toExternalForm()));
                break;
            case "saveButton":
                saveImageView.setImage(
                        new Image(getClass().getResource("/game/arkanoid/images/save.png").toExternalForm()));
                break;
            case "confirmBallButton":
                confirmBallImageView.setImage(
                        new Image(getClass().getResource("/game/arkanoid/images/change.png").toExternalForm()));
                break;
            case "confirmPaddleButton":
                confirmPaddleImageView.setImage(
                        new Image(getClass().getResource("/game/arkanoid/images/change.png").toExternalForm()));
                break;
            case "leftBallButton":
                leftBallImageView.setImage(
                        new Image(getClass().getResource("/game/arkanoid/images/left.png").toExternalForm()));
                break;
            case "rightBallButton":
                rightBallImageView.setImage(
                        new Image(getClass().getResource("/game/arkanoid/images/right.png").toExternalForm()));
                break;
            case "leftPaddleButton":
                leftPaddleImageView.setImage(
                        new Image(getClass().getResource("/game/arkanoid/images/left.png").toExternalForm()));
                break;
            case "rightPaddleButton":
                rightPaddleImageView.setImage(
                        new Image(getClass().getResource("/game/arkanoid/images/right.png").toExternalForm()));
                break;
        }
    }

    // Mở màn hình preview level
    @FXML
    private void openPreview(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/game/arkanoid/fxml/PreviewGame.fxml"));
            Parent root = loader.load();

            PreviewGameController controller = loader.getController();
            controller.setPlayer(currentPlayer);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, 800, 600));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Chuyển đổi skin
    @FXML
    private void prevBall() {
        ballIndex = (ballIndex - 1 + ballSkins.length) % ballSkins.length;
        updateBallImage();
    }

    @FXML
    private void nextBall() {
        ballIndex = (ballIndex + 1) % ballSkins.length;
        updateBallImage();
    }

    @FXML
    private void prevPaddle() {
        paddleIndex = (paddleIndex - 1 + paddleSkins.length) % paddleSkins.length;
        updatePaddleImage();
    }

    @FXML
    private void nextPaddle() {
        paddleIndex = (paddleIndex + 1) % paddleSkins.length;
        updatePaddleImage();
    }

    // Xác nhận skin đã chọn
    @FXML
    private void confirmBall() {
        System.out.println("Ball đã chọn: " + ballSkins[ballIndex]);
        GameSettings.getInstance().setSelectedBall(ballSkins[ballIndex]);

        // Nếu đang trong game, reload skin ngay lập tức để preview
        if (mainController != null) {
            mainController.reloadGameSkins();
        }
    }

    @FXML
    private void confirmPaddle() {
        System.out.println("Paddle đã chọn: " + paddleSkins[paddleIndex]);
        GameSettings.getInstance().setSelectedPaddle(paddleSkins[paddleIndex]);

        // Nếu đang trong game, reload skin ngay lập tức để preview
        if (mainController != null) {
            mainController.reloadGameSkins();
        }
    }

    // Xác nhận thay đổi
    @FXML
    private void saveSettings(ActionEvent event) {
        // Chỉ lưu volume settings (ball/paddle đã được lưu khi bấm confirm)
        System.out.println("Đã lưu volume settings");
        System.out.println("- Music Volume: " + musicSlider.getValue());
        System.out.println("- SFX Volume: " + sfxSlider.getValue());

        goBack(event);
    }

    // Trở về màn hình trước đó (MainView nếu từ game, StartMenu nếu từ menu)
    private void goBack(ActionEvent event) {
        try {
            // Nếu được gọi từ game (qua PauseMenu) và có settingsStage
            if (mainController != null && settingsStage != null) {
                // Đóng Settings modal và quay lại PauseMenu
                settingsStage.close();
                // Clear context sau khi đóng
                GameSettings.getInstance().clearNavigationContext();
            } else {
                // Nếu được gọi từ StartMenu, chuyển Scene về StartMenu
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/game/arkanoid/fxml/StartMenu.fxml"));
                Parent root = loader.load();

                StartMenuController controller = loader.getController();
                if (mainController != null) {
                    controller.setPlayer(mainController.getCurrentPlayer());
                } else {
                    controller.setPlayer(currentPlayer);
                }

                stage.setScene(new Scene(root, 800, 600));
                // Clear context khi về StartMenu
                GameSettings.getInstance().clearNavigationContext();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Cập nhật hình ảnh theo skin đã chọn
    private void updateBallImage() {
        ballImageView.setImage(
                new Image(getClass().getResource(ballSkins[ballIndex]).toExternalForm()));
    }

    private void updatePaddleImage() {
        paddleImageView.setImage(
                new Image(getClass().getResource(paddleSkins[paddleIndex]).toExternalForm()));
    }

    public void setPlayer(Player p) {
        this.currentPlayer = p;
    }
}
