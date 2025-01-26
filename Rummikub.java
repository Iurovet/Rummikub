import java.util.HashMap;
import java.util.Scanner;

public class Rummikub {
    public static int setNumPlayers(Scanner scnr) {
        System.out.println("Welcome to Rummikub. Select between 2-4 players");
        int numPlayers = scnr.nextInt();
        while ((numPlayers < 2) || (numPlayers > 4)) {
            System.out.println("Error: Must select between 2-4 players");
            numPlayers = scnr.nextInt();
        }
        return numPlayers;
    }
    
    public static HashMap<String, Tile> setupTiles() {
        HashMap<String, Tile> tiles = new HashMap<String, Tile>();

        final String[] COLOURS = {"Blue", "Orange", "Red", "Black"};
        final String[] IDENTIFIERS = {"a", "b"};

        for (String s1: COLOURS) {
            for (int i = 1; i <= 13; i++) {
                for (String s2 : IDENTIFIERS) {
                    Tile t1 = new Tile(s1, "non-joker", i, s2);
                    tiles.put(s1 + " " + i + s2, t1);
                }
            }
        }

        tiles.put("Black joker", new Tile("black", "joker"));
        tiles.put("Red joker", new Tile("red", "joker"));

        return tiles;
    }

    public static void main(String[] args) {
        HashMap<String, Tile> tiles = setupTiles();
        Scanner scnr = new Scanner(System.in);

        int numPlayers = setNumPlayers(scnr);
    }
}