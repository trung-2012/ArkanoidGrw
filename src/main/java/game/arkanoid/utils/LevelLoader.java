package game.arkanoid.utils;

import game.arkanoid.models.Brick;
import game.arkanoid.models.BrickType;
import game.arkanoid.utils.Vector2D;
import game.arkanoid.utils.GameConstants;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class LevelLoader {

    /**
     * Load level from resources/levels/levelFile
     * Returns list of Brick (model-only). Does NOT create any Node/UI.
     */
    public static List<Brick> loadLevel(String levelFile) {
        List<Brick> bricks = new ArrayList<>();

        try (InputStream is = LevelLoader.class.getResourceAsStream("/game/arkanoid/levels/" + levelFile);
             BufferedReader br = new BufferedReader(new InputStreamReader(is))) {

            String line;
            int row = 0;

            // layout parameters
            int bw = GameConstants.BRICK_WIDTH;
            int bh = GameConstants.BRICK_HEIGHT;
            int pad = GameConstants.BRICK_PADDING;
            int startX = 50;
            int startY = 50;

            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) {
                    row++;
                    continue;
                }
                for (int col = 0; col < line.length(); col++) {
                    char c = line.charAt(col);
                    BrickType type = charToBrickType(c);
                    if (type != null) {
                        double x = startX + col * (bw + pad);
                        double y = startY + row * (bh + pad);
                        bricks.add(new Brick(type, new Vector2D(x, y)));
                    }
                }
                row++;
            }
        } catch (Exception e) {
            System.err.println("Failed to load level: " + levelFile);
            e.printStackTrace();
        }

        return bricks;
    }
    // phan loai gach
    private static BrickType charToBrickType(char c) {
        switch (c) {
            case '1': return BrickType.NORMAL;
            case '2': return BrickType.WOOD;
            case '3': return BrickType.IRON;
            case '4': return BrickType.GOLD;
            case '9': return BrickType.INSANE;
            case '0':
            default: return null;
        }
    }
}
