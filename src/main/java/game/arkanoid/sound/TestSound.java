package game.arkanoid.sound;

import javafx.application.Application;
import javafx.stage.Stage;

public class TestSound extends Application {
    @Override
    public void start(Stage stage) {
        SoundManager.playMenuMusic();
        stage.setTitle("Test Menu Music");
        stage.setWidth(300);
        stage.setHeight(200);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
