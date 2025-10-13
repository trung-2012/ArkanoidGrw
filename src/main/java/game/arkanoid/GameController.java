package game.arkanoid;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class GameController {
    @FXML
    private Canvas gameCanvas;

    @FXML
    public void initialize() {
        GraphicsContext gc = gameCanvas.getGraphicsContext2D();
        gc.setFill(Color.DARKBLUE);
        gc.fillRect(0, 0, gameCanvas.getWidth(), gameCanvas.getHeight());

        gc.setFill(Color.WHITE);
        gc.fillText("🎮 Game is running!", 350, 300);
    }
}
