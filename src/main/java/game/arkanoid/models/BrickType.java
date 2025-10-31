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

    public int getPoint() {
        switch (this) {
            case WOOD:
                return 20;
            case IRON:
                return 40;
            case GOLD:
                return 50;
            case INSANE:
                return 1000;
            case NORMAL:
            default:
                return 10;
        }
    }
}
