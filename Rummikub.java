import java.util.ArrayList;
import java.util.Arrays;
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

    public static boolean checkBoardLength(ArrayList<ArrayList<Tile>> sequences) {
        int numFalse = 0;
        
        for (int i = 0; i < sequences.size(); ++i) {
            if (sequences.get(i).size() < 3) {
                System.out.println("Sequence " + (i + 1) + " is not long enough");
                numFalse++;
            }
        }

        return numFalse == 0;
    }

    public static boolean checkBoardJokers(ArrayList<ArrayList<Tile>> sequences) {
        int numFalse = 0;
        
        for (int i = 0; i < sequences.size(); ++i) {
            ArrayList<Tile> currSequence = sequences.get(i);
            for (int j = 0; j < currSequence.size() - 1; ++j) {
                if ((currSequence.get(j).getNumber() == 0) && (currSequence.get(j + 1).getNumber() == 1)) {
                    System.out.println("Sequence " + (i + 1) + " contains a joker before 1 (positions" + j + " and " + (j + 1) + ").");
                }
                
                numFalse++;
            }

            for (int j = 1; j < currSequence.size(); ++j) {
                if ((currSequence.get(j).getNumber() == 13) && (currSequence.get(j + 1).getNumber() == 0)) {
                    System.out.println("Sequence " + (i + 1) + " contains a joker after 13 (positions" + j + " and " + (j + 1) + ").");
                }

                numFalse++;
            }
        }

        return numFalse == 0;
    }

    public static boolean checkBoardSequences(ArrayList<ArrayList<Tile>> sequences) {
        /* Groups and runs take inner indices 0 and 1, respectively (initialise to -1 for no errors found).
         * Note that only the 1st group- and run-related errors are captured for simplicity.
         */
        int[][] groupRunErrors = new int[sequences.size()][2];
        for (int[] row: groupRunErrors) { Arrays.fill(row, -1); }
        
        // Groups
        for (int i = 0; i < sequences.size(); ++i) {
            ArrayList<Tile> currSequence = sequences.get(i);
            for (int j = 0; j < currSequence.size(); ++j) {
                // Make sure colours aren't duplicated
                ArrayList<String> usedColours = new ArrayList<String>();
                usedColours.add(currSequence.get(j).getColour());

                if (j == 4) { // Group length cannot exceed 4 (incl. jokers)
                    groupRunErrors[i][0] = 4;
                    break;
                }
    
                // Colours must not be used more than once, bar jokers.
                if ((usedColours.contains(currSequence.get(j).getColour())) &&
                    (currSequence.get(j).getNumber() != 0)) {
                    groupRunErrors[i][0] = j;
                    break;
                }

                // Numbers are only allowed to differ in the context of a joker.
                if (j <= currSequence.size() - 1) { // Check for out of bounds access
                    if ((currSequence.get(j).getNumber() != currSequence.get(j + 1).getNumber()) &&
                        ((currSequence.get(j).getNumber() != 0) || (currSequence.get(j + 1).getNumber() != 0))) {
                        groupRunErrors[i][0] = j + 1;
                        break;
                    }
                }
            }
        }

        // Runs
        for (int i = 0; i < sequences.size(); ++i) {
            ArrayList<Tile> currSequence = sequences.get(i);
            for (int j = 0; j < currSequence.size() - 1; ++j) {
                switch (currSequence.get(j).getNumber()) {
                    case 0: // Jokers
                        /* If the current tile is a joker, then the numbers on either side must differ by 2
                         * and share the same colour, though this makes no difference for an edge tile.
                         * Having said that, checking edge tiles would lead to an out of bounds error, 
                         * though the inner loop's declaration handles the upper-bound case (leaving just the
                         * lower bound case to be checked). The joker's colour does not matter either way.
                         */
                        if (j > 0) {
                            if ((currSequence.get(j + 1).getNumber() != currSequence.get(j - 1).getNumber() + 2) ||
                                (currSequence.get(j + 1).getColour() != currSequence.get(j - 1).getColour())) {
                               groupRunErrors[i][0] = j;
                               break;
                            }
                        }
                    default: // Non-jokers
                        /* If the current tile is not a joker, either the following tile must be a joker
                         * or anoher tile of the same colour (with the next number in the sequence).
                         */
                        if ((currSequence.get(j + 1).getNumber() != 0) &&
                            ((currSequence.get(j).getNumber() != currSequence.get(j + 1).getNumber() - 1) ||
                             (currSequence.get(j).getColour() != currSequence.get(j + 1).getColour()))
                           ) {
                            groupRunErrors[i][0] = j + 1;
                            break;
                        }
                    break;
                }
            }
        }
        
        int numFalse = 0;

        for (int i = 0; i < groupRunErrors.length; ++i) { // Count number of, and print errors.
            // A sequence should never be both a group and a run, but might be neither.
            if (!((groupRunErrors[i][0] == -1) ^ (groupRunErrors[i][1] == -1))) {
                numFalse++;

                if (groupRunErrors[i][0] > -1) {
                    System.out.print("Group-related error at sequence " + (i + 1) + " position " + groupRunErrors[i][0] + ".");
                    System.out.print("Make sure that there are 3 or 4 tiles of separate colours ");
                    System.out.println("but of the same number (excluding jokers in both senses).");
                }

                if (groupRunErrors[i][1] > -1) {
                    System.out.print("Run-related error at sequence " + (i + 1) + " position " + groupRunErrors[i][1] + ".");
                    System.out.println("Make sure that there are 3 or more tiles of the same colour in an ");
                    System.out.println("increasing sequence (excluding jokers in both sense). ");
                }
            }            
        }

        return numFalse == 0;
    }

    /* A group is a 3- or 4-tile row of same-numbered tiles but different colours.
     * A run is a 3-tile (or more) row of same-coloured tiles in ascending order.
     */
    public static boolean checkBoardValidity(ArrayList<ArrayList<Tile>> sequences) {
        System.out.println(); // Separate from other console output
        boolean lengthOK = checkBoardLength(sequences); // All sequences have at least 3 tiles
        boolean jokerOK = checkBoardJokers(sequences); // No joker comes before 1 or after 13
        boolean groupRunOK = checkBoardSequences(sequences); // All sequences are either groups or runs

        return lengthOK && jokerOK && groupRunOK;
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
                }
            }
        }

        // Add jokers
        poolTiles.add(new Tile("Black"));
        poolTiles.add(new Tile("Red"));
       
        return poolTiles;
    }

    public static HashMap<String, ArrayList<Tile>> randomPoolTile(HashMap<String, ArrayList<Tile>> tileLists, int currPlayer) {
        if (tileLists.get("Pool tiles").size() == 0) {
            System.out.println("Sorry, no more tiles left. Your move has still been forfeited");
        }
        
        Tile t1 = tileLists.get("Pool tiles").get(getRandom(0, tileLists.get("Pool tiles").size() - 1));
        
        tileLists.get("Player " + currPlayer + " tiles").add(t1);
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
            boolean validInput = false;

            while (!validInput) {
                // Declare false once rather than declare true multiple times
                validInput = true;
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