package game.arkanoid.player_manager;

import game.arkanoid.controllers.StartMenuController;
import game.arkanoid.managers.SoundManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.*;
import java.util.ArrayList;

public class LoginController {

    private final String FILE_PATH = "players.dat";
    @FXML
    private Label titleLabel;
    @FXML
    private Label messageLabel;
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private TextField passwordFieldVisible;
    @FXML
    private PasswordField confirmPasswordField;
    @FXML
    private TextField confirmPasswordFieldVisible;
    @FXML
    private CheckBox showPasswordCheck;
    @FXML
    private VBox confirmBox;
    @FXML
    private Button mainButton;
    @FXML
    private Button switchButton;
    @FXML
    private Label usernameStatus;
    @FXML
    private Label usernameHint;
    @FXML
    private Label passwordHint;

    private ArrayList<Player> players = new ArrayList<>();
    private boolean isRegisterMode = false;

    @FXML
    public void initialize() {
        loadPlayers();
        SoundManager.getInstance().playBackgroundMusic(
                "src/main/resources/game/arkanoid/sounds/menu_music.mp3", true
        );

        usernameField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (isRegisterMode)
                checkUsername(newVal);
        });

        // Bind 2 chiều giữa passwordField và passwordFieldVisible
        passwordField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (passwordField.isVisible()) {
                passwordFieldVisible.setText(newVal);
                if (isRegisterMode) checkPasswordRealtime(newVal);
            }
        });

        passwordFieldVisible.textProperty().addListener((obs, oldVal, newVal) -> {
            if (passwordFieldVisible.isVisible()) {
                passwordField.setText(newVal);
                if (isRegisterMode) checkPasswordRealtime(newVal);
            }
        });

        // Bind 2 chiều giữa confirmPasswordField và confirmPasswordFieldVisible
        confirmPasswordField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (confirmPasswordField.isVisible()) {
                confirmPasswordFieldVisible.setText(newVal);
            }
        });

        confirmPasswordFieldVisible.textProperty().addListener((obs, oldVal, newVal) -> {
            if (confirmPasswordFieldVisible.isVisible()) {
                confirmPasswordField.setText(newVal);
            }
        });
    }

    @SuppressWarnings("unchecked")
    private void loadPlayers() {
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            players = new ArrayList<>();
            return;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_PATH))) {
            Object obj = ois.readObject();
            if (obj instanceof ArrayList<?>)
                players = (ArrayList<Player>) obj;
            else
                players = new ArrayList<>();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading players: " + e.getMessage());
            players = new ArrayList<>();
        }
    }

    private void savePlayers() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            oos.writeObject(players);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void switchMode() {
        isRegisterMode = !isRegisterMode;

        if (isRegisterMode) {
            titleLabel.setText("CREATE ACCOUNT");
            confirmBox.setVisible(true);
            confirmBox.setManaged(true);
            mainButton.setText("Register");
            switchButton.setText("Back");
        } else {
            titleLabel.setText("LOGIN");
            confirmBox.setVisible(false);
            confirmBox.setManaged(false);
            mainButton.setText("Login");
            switchButton.setText("Register");
        }

        // RESET INPUT FIELDS
        usernameField.clear();
        passwordField.clear();
        passwordFieldVisible.clear();
        confirmPasswordField.clear();
        confirmPasswordFieldVisible.clear();

        usernameStatus.setText("");
        usernameHint.setText("");
        passwordHint.setText("");
        messageLabel.setText("");

        messageLabel.setStyle("-fx-text-fill: red;");
        showPasswordCheck.setSelected(false);

        hidePasswordFields();
    }

    @FXML
    private void togglePassword() {
        if (showPasswordCheck.isSelected()) {
            showPasswordFields();
        } else {
            hidePasswordFields();
        }
    }

    private void showPasswordFields() {
        // LOGIN PASSWORD
        passwordFieldVisible.setText(passwordField.getText());
        passwordFieldVisible.setVisible(true);
        passwordFieldVisible.setManaged(true);
        passwordField.setVisible(false);
        passwordField.setManaged(false);

        // REGISTER CONFIRM PASSWORD
        if (isRegisterMode) {
            confirmPasswordFieldVisible.setText(confirmPasswordField.getText());
            confirmPasswordFieldVisible.setVisible(true);
            confirmPasswordFieldVisible.setManaged(true);

            confirmPasswordField.setVisible(false);
            confirmPasswordField.setManaged(false);
        }
    }

    private void hidePasswordFields() {
        // LOGIN
        passwordField.setText(passwordFieldVisible.getText());
        passwordField.setVisible(true);
        passwordField.setManaged(true);
        passwordFieldVisible.setVisible(false);
        passwordFieldVisible.setManaged(false);

        // REGISTER CONFIRM
        confirmPasswordField.setText(confirmPasswordFieldVisible.getText());
        confirmPasswordField.setVisible(true);
        confirmPasswordField.setManaged(true);
        confirmPasswordFieldVisible.setVisible(false);
        confirmPasswordFieldVisible.setManaged(false);
    }

    // tai khoan toi thieu 4 ki tu va toi da 16
    private void checkUsername(String username) {

        // Reset
        usernameStatus.setText("");
        usernameHint.setText("");

        if (username.isEmpty()) return;

        if (username.length() < 4) {
            usernameHint.setText("❌ Username must be at least 4 characters.");
            usernameHint.setStyle("-fx-text-fill: #ff4d4d;");
            return;
        }

        // 3) > 16 ky tu -> invalid
        if (username.length() > 16) {
            usernameHint.setText("❌ Username cannot exceed 16 characters.");
            usernameHint.setStyle("-fx-text-fill: #ff4d4d;");
            return;
        }

        // 4) 4–16 ky tu → check exist
        boolean exists = players.stream().anyMatch(p -> p.getUsername().equals(username));

        if (exists) {
            usernameStatus.setText("❌");
            usernameHint.setText("❌ Username already taken.");
            usernameHint.setStyle("-fx-text-fill: #ff4d4d;");
        } else {
            usernameStatus.setText("✅");
            usernameHint.setText("✅ This username is available.");
            usernameHint.setStyle("-fx-text-fill: #00ffcc;");
        }
    }

    //  Password realtime (8–20)
    private void checkPasswordRealtime(String pass) {

        // Reset
        passwordHint.setText("");

        if (pass.isEmpty()) return;

        if (pass.length() < 8) {
            passwordHint.setText("❌ Password must be at least 8 characters.");
            passwordHint.setStyle("-fx-text-fill: #ff4d4d;");
            return;
        }

        if (pass.length() > 20) {
            passwordHint.setText("❌ Password cannot exceed 20 characters.");
            passwordHint.setStyle("-fx-text-fill: #ff4d4d;");
            return;
        }

        passwordHint.setText("✅ Password length is valid.");
        passwordHint.setStyle("-fx-text-fill: #00ffcc;");

    }

    // mat khau toi thieu 8 ki tu va toi da 20
    private boolean validatePassword(String password) {

        passwordHint.setText("");

        if (password.length() < 8) {
            passwordHint.setText("❌ Password must be at least 8 characters.");
            passwordHint.setStyle("-fx-text-fill: #ff4d4d;");
            return false;
        }

        if (password.length() > 20) {
            passwordHint.setText("❌ Password cannot exceed 20 characters.");
            passwordHint.setStyle("-fx-text-fill: #ff4d4d;");
            return false;
        }

        passwordHint.setText("✅ Password length is valid.");
        passwordHint.setStyle("-fx-text-fill: #00ffcc;");
        return true;
    }

    @FXML
    private void handleLoginOrRegister(ActionEvent event) {

        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            messageLabel.setText("Please fill in all fields.");
            return;
        }

        if (isRegisterMode) {
            if (!validateUsername(username)) return;
            if (!validatePassword(password)) return;

            String confirm = confirmPasswordField.getText().trim();

            if (!password.equals(confirm)) {
                messageLabel.setText("Passwords do not match!");
                return;
            }

            boolean exists = players.stream().anyMatch(p -> p.getUsername().equals(username));
            if (exists) {
                messageLabel.setText("Username already exists!");
                return;
            }

            players.add(new Player(username, password));
            savePlayers();

            messageLabel.setStyle("-fx-text-fill: #00ffcc;");
            messageLabel.setText("✅ Account created successfully!");
            return;
        }

        // LOGIN MODE
        for (Player p : players) {
            if (p.getUsername().equals(username) && p.getPassword().equals(password)) {

                try {
                    if (p.getNickname() == null || p.getNickname().isEmpty()) {

                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/game/arkanoid/fxml/nickname.fxml"));
                        Parent root = loader.load();

                        NicknameController nicknameController = loader.getController();
                        nicknameController.setPlayer(p);
                        nicknameController.setPlayersList(players);

                        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                        stage.setScene(new Scene(root, 800, 600));

                    } else {

                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/game/arkanoid/fxml/StartMenu.fxml"));
                        Parent root = loader.load();

                        StartMenuController controller = loader.getController();
                        controller.setPlayer(p);

                        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                        stage.setScene(new Scene(root, 800, 600));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return;
            }
        }

        messageLabel.setText("❌ Invalid username or password!");
    }

    private boolean validateUsername(String username) {
        return username.length() >= 4 && username.length() <= 16;
    }
}
