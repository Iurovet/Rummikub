import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class Rummikub {
    public static HashMap<String, Tile> allocateTilesAtStart(HashMap<String, Tile> tiles, int numPlayers) {
        ArrayList<String> freeTiles = new ArrayList<String>(); // keys only
        for (String s1 : tiles.keySet()) {
            freeTiles.add(s1);
        }
        
        for (int i = 0; i < numPlayers; ++i) {
            for (int j = 0; j < 14; ++j) {
                String chosenKey = freeTiles.get(getRandom(0, freeTiles.size() - 1));
                tiles.get(chosenKey).setLocation("P" + (i + 1));
                freeTiles.remove(chosenKey);
            }
        }
        
        return tiles;
    }
    
    // Used for selecting both the starting player and tiles from pool
    public static int getRandom(int min, int max) {
        /* 
         * Since Math.random() returns a real number in range [0, 1), the multiplier
         * acts as the number of possible numbers to choose from. As the list of possible
         * numbers is contiguous (with both parameters inclusive), the minimum value
         * can be set by adding it to the result. Therefore, a number in range [min, max]
         * gets returned.
         */
        return (int)(Math.random() * (max - min + 1)) + min;
    }

    public static int setNumPlayers(Scanner scnr) {
        System.out.println("Welcome to Rummikub. Select between 2-4 players");
        int numPlayers = scnr.nextInt();
        while ((numPlayers < 2) || (numPlayers > 4)) {
            System.out.println("Error: Must select between 2-4 players");
            numPlayers = scnr.nextInt();
        }
        return numPlayers;
    }

    public static int setStartPlayer(Scanner scnr, int numPlayers) {
        System.out.println("Choose a starting player (or 5 for random)");
        int startPlayer = scnr.nextInt();
        
        /*
         * Accepted values are in range [1, numPlayers] and 5. Cannot simply check for
         * 2 sides of a range because numPlayers is in range [2, 4] i.e. not always 4.
         */
        while ((startPlayer < 1) || ((startPlayer > numPlayers) && (startPlayer != 5))) {
            System.out.println("Error: Must select a valid number (or 5 for random)");
            startPlayer = scnr.nextInt();
        }

        if (startPlayer == 5) {// Inform of starting player, if randomly selected
            startPlayer = getRandom(1, 4);
            System.out.println("Player " + startPlayer + " is starting");
        }
        
        return startPlayer;
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

        // 2nd parameter in setStartPlayer() allows a random player to start
        int numPlayers = setNumPlayers(scnr);
        int startPlayer = setStartPlayer(scnr, numPlayers);

        tiles = allocateTilesAtStart(tiles, numPlayers);
    }
}