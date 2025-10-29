package game.arkanoid.models;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class PowerUp {
    private final ImageView view;
    private final PowerUpType type;
    private double speedY = 2;

    public PowerUp(double x, double y, PowerUpType type) {
        this.type = type;

        String imagePath = switch (type) {
            case EXTRA_LIFE -> "game/arkanoid/images/heart_item.png";
            case LASER -> "game/arkanoid/images/laser_item.png";
        };

        Image image = new Image(imagePath);
        view = new ImageView(image);
        view.setX(x);
        view.setY(y);
        view.setFitWidth(32);
        view.setFitHeight(32);
    }

    public void update() {
        view.setY(view.getY() + speedY);
    }

    public ImageView getView() {
        return view;
    }

    public PowerUpType getType() {
        return type;
    }

    public boolean isOutOfBounds(double sceneHeight) {
        return view.getY() > sceneHeight;
    }
}
