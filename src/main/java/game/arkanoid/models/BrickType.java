package game.arkanoid.models;

public enum BrickType {
    NORMAL(1),
    WOOD(2),
    IRON(3),
    GOLD(4),
    INSANE(100); // Hầu như không thể phá hủy

    // Số máu của loại gạch
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
