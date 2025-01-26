import java.util.HashMap;

public class Rummikub {
    public static HashMap<String, Tile> setupTiles() {
        HashMap<String, Tile> tiles = new HashMap<String, Tile>();

        final String[] colourList = {"Blue", "Orange", "Red", "Black"};
        final String[] identifiers = {"a", "b"};

        for (String s1 : colourList) {
            for (int i = 1; i <= 13; i++) {
                for (String s2 : identifiers) {
                    Tile t1 = new Tile(s1, "non-joker", i, s2);
                    tiles.put(s1.substring(0, 1) + " " + i + " " + s2, t1);
                }
            }
        }

        tiles.put("Black joker", new Tile("black", "joker"));
        tiles.put("Red joker", new Tile("red", "joker"));

        return tiles;
    }

    public static void main(String[] args) {
        HashMap<String, Tile> tiles = setupTiles();
    }
}