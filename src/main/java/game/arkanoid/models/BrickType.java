package game.arkanoid.models;

public enum BrickType {
    NORMAL(1),
    WOOD(2),
    IRON(3),
    GOLD(4),
    INSANE(100); // can't be destroyed

    // Health points for each brick type
    private final int health;

    // Constructor
    BrickType(int health) {
        this.health = health;
    }

    // Getter
    public int getHealth() {
        return health;
    }
}
