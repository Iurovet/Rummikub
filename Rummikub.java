import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class Rummikub {
    // Might need to return 2-4 player's racks
    public static HashMap<String, ArrayList<Tile>> allocateTilesAtStart(int numPlayers) {
        HashMap<String, ArrayList<Tile>> tileLists = new HashMap<String, ArrayList<Tile>>();
        
        ArrayList<Tile> poolTiles = putTilesInPool();
        ArrayList<Tile> p1Tiles = new ArrayList<Tile>();
        ArrayList<Tile> p2Tiles = new ArrayList<Tile>();
        ArrayList<Tile> p3Tiles = new ArrayList<Tile>();
        ArrayList<Tile> p4Tiles = new ArrayList<Tile>();

        // Allocate 14 random tiles to each player.
        for (int i = 0; i < 14 * numPlayers; ++i) {
            Tile t1 = poolTiles.get(getRandom(0, poolTiles.size() - 1));
            
            switch(i / 14) {
                case 0:
                    p1Tiles.add(t1);
                    break;
                case 1:
                    p2Tiles.add(t1);
                    break;
                case 2:
                    p3Tiles.add(t1);
                    break;
                case 3:
                    p4Tiles.add(t1);
                    break;
                default:
                    break;
            }

            t1.addLocation("Player");
            poolTiles.remove(t1);
        }

        // Only return the non-empty lists (always includes the 1st 3 lists).
        tileLists.put("Pool tiles", poolTiles);
        if (p1Tiles.size() != 0) { tileLists.put("Player 1 tiles", p1Tiles); };
        if (p2Tiles.size() != 0) { tileLists.put("Player 2 tiles", p2Tiles); };
        if (p3Tiles.size() != 0) { tileLists.put("Player 3 tiles", p3Tiles); };
        if (p4Tiles.size() != 0) { tileLists.put("Player 4 tiles", p4Tiles); };

        return tileLists;
    }

    public static boolean emptyRack(HashMap<String, ArrayList<Tile>> tiles, int currPlayer) {
        // The keys are (Pool | Player 1-4) tiles
        switch(currPlayer){
            case 1:
                return tiles.get("Player 1 tiles").size() == 0;
            case 2:
                return tiles.get("Player 2 tiles").size() == 0;
            case 3:
                return tiles.get("Player 3 tiles").size() == 0;
            case 4:
                return tiles.get("Player 4 tiles").size() == 0;
            default: // Assume that some invalid player number has no rack
                return false;
        }
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

    public static void printBoard (ArrayList<ArrayList<Tile>> sequences) {
        System.out.println();
        
        int i = 0;
        for (i = 0; i < sequences.size(); ++i) {           
            System.out.print("Sequence " + (i + 1) + ": <");
         
            for (int j = 0; j < sequences.get(i).size(); j++) {
                System.out.print(sequences.get(i).get(j).toString());
                if (j < sequences.get(i).size() - 1) {
                    System.out.print(", ");
                }
                else {
                    System.out.println(">\n");
                }
            }
        }

        // Simple way of checking the size. Obviously requires pre-loop initalisation
        if (i == 0) { System.out.println("Board is empty"); }
    }

    public static void printPlayerTiles (HashMap<String, ArrayList<Tile>> tiles, int currPlayer) {        
        // The keys are (Pool | Player 1-4) tiles
        ArrayList<Tile> currTiles = new ArrayList<Tile>();
        
        switch(currPlayer){
            case 1:
                currTiles = tiles.get("Player 1 tiles");
                break;
            case 2:
                currTiles = tiles.get("Player 2 tiles");
                break;
            case 3:
                currTiles = tiles.get("Player 3 tiles");
                break;
            case 4:
                currTiles = tiles.get("Player 4 tiles");
                break;
            default: // Assume that some invalid player number has no rack
                break;
        }
        
        // TODO: Sort by colour (numbers are not yet touched).
        // currTiles.sort(null);

        System.out.println("\nPlayer " + currPlayer + " has the following tiles:");
        for (int i = 0; i < currTiles.size(); ++i) {
            System.out.print(currTiles.get(i) + (i < currTiles.size() - 1 ? ", " : ""));
        }
        System.out.println();
    }

    public static void printUserCommands() {
        System.out.println("\nChoose from one of the following commands (case insensitive):");
        System.out.println("Pool: Grab 1 tile from the pool (forfeits turn)\n");
    }

    public static ArrayList<Tile> putTilesInPool() {
        ArrayList<Tile> poolTiles = new ArrayList<Tile>();
        final String[] COLOURS = {"Blue", "Orange", "Black", "Red"};

        // Add non-jokers
        for (int i = 1; i <= 13; ++i) {
            for (String c1 : COLOURS) {
                for (int j = 0; j < 2; ++j) {
                    poolTiles.add(new Tile(c1, i));
                    poolTiles.get(poolTiles.size() - 1).addLocation("Pool");
                }
            }
        }

        // Add jokers
        poolTiles.add(new Tile("Black"));
        poolTiles.get(poolTiles.size() - 1).addLocation("Pool");

        poolTiles.add(new Tile("Red"));
        poolTiles.get(poolTiles.size() - 1).addLocation("Pool");
        
        return poolTiles;
    }

    public static HashMap<String, ArrayList<Tile>> randomPoolTile(HashMap<String, ArrayList<Tile>> tileLists, int currPlayer) {
        if (tileLists.get("Pool tiles").size() == 0) {
            System.out.println("Sorry, no more tiles left. Your move has still been forfeited");
        }
        
        Tile t1 = tileLists.get("Pool tiles").get(getRandom(0, tileLists.get("Pool tiles").size() - 1));
        
        tileLists.get("Player " + currPlayer + " tiles").add(t1);
        t1.addLocation("Player");
        tileLists.get("Pool tiles").remove(t1);
        
        return tileLists;
    }

    public static int setNumPlayers(Scanner scnr) {
        System.out.println("Welcome to Rummikub. Select between 2-4 players");
        int numPlayers = scnr.nextInt();
        scnr.nextLine(); // Escape to the next line, see main for more details.
        
        while ((numPlayers < 2) || (numPlayers > 4)) {
            System.out.println("Error: Must select between 2-4 players");
            numPlayers = scnr.nextInt();
        }

        return numPlayers;
    }

    public static int setStartPlayer(Scanner scnr, int numPlayers) {
        System.out.println("Choose a starting player (or 5 for random)");
        int startPlayer = scnr.nextInt();
        scnr.nextLine(); // Escape to the next line, see main for more details.
        
        /*
         * Accepted values are in range [1, numPlayers] and 5. Cannot simply check for
         * 2 sides of a range because numPlayers is in range [2, 4] i.e. not always 4.
         */
        while ((startPlayer < 1) || ((startPlayer > numPlayers) && (startPlayer != 5))) {
            System.out.println("Error: Must select a valid number (or 5 for random)");
            startPlayer = scnr.nextInt();
            scnr.nextLine(); // Escape to the next line, see main for more details.
        }

        if (startPlayer == 5) {// Inform of starting player, if randomly selected
            startPlayer = getRandom(1, 4);
            System.out.println("Player " + startPlayer + " is starting");
        }
        
        return startPlayer;
    }

    public static void main(String[] args) {
        ArrayList<ArrayList<Tile>> sequences = new ArrayList<ArrayList<Tile>>();
        Scanner scnr = new Scanner(System.in);

        // 2nd parameter in setStartPlayer() allows a random player to start
        int numPlayers = setNumPlayers(scnr);
        int currPlayer = setStartPlayer(scnr, numPlayers); // Initialised then cycles through.
        
        /* The keys are (Pool | Player 1-4) tiles. Cannot include the sequences list as it is
         * 2D (must be able to store which seqeuence a tile belongs to)
         */
        HashMap<String, ArrayList<Tile>> tileLists = allocateTilesAtStart(numPlayers);
        
        boolean gameFinished = false;
        while (!gameFinished) {
            printBoard(sequences);
            printPlayerTiles(tileLists, currPlayer);

            while (true) {
                boolean validInput = true;
                printUserCommands();

                /* Next user input after 2 nextInt()'s, so they must be escaped
                 * using nextLine() straight after.
                 */
                String userInput = scnr.nextLine();
                
                switch(userInput.toUpperCase()){
                    case "POOL":
                        tileLists = randomPoolTile(tileLists, currPlayer);
                        break;
                    default:
                        validInput = false;
                        System.out.println("Error: Command unrecognised");
                        break;
                }

                if (validInput) { break; }
            }

            if (emptyRack(tileLists, currPlayer)) {
                gameFinished = true;
            }
            else {
                // Increment player number (or set to 1 if maxed out)
                currPlayer = (currPlayer < numPlayers) ? currPlayer + 1 : 1;
            }
        }

        System.out.println("\nPlayer " + currPlayer + " won!");
    }
}