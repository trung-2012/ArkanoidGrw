package game.arkanoid.controllers;

import game.arkanoid.models.Player;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class NicknameController {

    private final String FILE_PATH = "players.dat";
    private Player currentPlayer;
    private ArrayList<Player> players;
    @FXML
    private TextField nicknameField;

    @FXML
    private Label messageLabel;

    public void setPlayer(Player player) {
        this.currentPlayer = player;
    }

    public void setPlayersList(ArrayList<Player> players) {
        this.players = players;
    }

    @FXML
    public void initialize() {
        // limited 12 characters
        nicknameField.setTextFormatter(new javafx.scene.control.TextFormatter<>(change -> {
            if (change.isContentChange() &&
                    change.getControlNewText().length() > 12) {
                return null;
            }
            return change;
        }));
    }

    @FXML
    private void handleContinue(ActionEvent event) {

        // Got het space first/last
        String nickname = nicknameField.getText().trim();

        // Nickname rong sau trim
        if (nickname.isEmpty()) {
            messageLabel.setText("Please enter a nickname!");
            return;
        }

        //  Nickname qua 12 ky tu (backup check)
        if (nickname.length() > 12) {
            messageLabel.setText("Nickname must be 12 characters or less.");
            return;
        }

        // check nickname duplicates
        for (Player p : players) {
            if (p.getNickname() != null &&
                    p.getNickname().equalsIgnoreCase(nickname)) {

                messageLabel.setText("Nickname already exists!");
                return;
            }
        }

        // If vailid → save nickname
        currentPlayer.setNickname(nickname);

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            oos.writeObject(players);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // back to StartMenu
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/game/arkanoid/fxml/StartMenu.fxml"));
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
