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
        for (int i = 0; i < sequences.size(); ++i) {
            if (sequences.get(i).size() < 3) {
                System.out.println("Sequence " + (i + 1) + " is not long enough");
                return false;
            }
        }

        return true;
    }

    public static boolean checkBoardSequences(ArrayList<ArrayList<Tile>> sequences) {
        /* Only the 1st group- and run-related errors are captured for simplicity.
         * Also, each element must be specifically initialised as an object.
         */
        ErrorMessage[] groupRunErrors = new ErrorMessage[sequences.size()];
        for (int i = 0; i < groupRunErrors.length; ++i) {
            groupRunErrors[i] = new ErrorMessage();
        }
        
        // Groups
        for (int i = 0; i < sequences.size(); ++i) {
            ArrayList<Tile> currSequence = sequences.get(i);
            for (int j = 0; j < currSequence.size(); ++j) {
                // Make sure colours aren't duplicated
                ArrayList<String> usedColours = new ArrayList<String>();
                usedColours.add(currSequence.get(j).getColour());

                if (j == 4) { // Group length cannot exceed 4 (incl. jokers)
                    groupRunErrors[i].setGroupError("Cannot have a group of 5 or more tiles");
                    break;
                }
    
                // Colours must not be used more than once, bar jokers.
                if ((usedColours.contains(currSequence.get(j).getColour())) &&
                    (currSequence.get(j).getNumber() != 0)) {
                    groupRunErrors[i].setGroupError("Cannot have 2 non-jokers of the same colour per group");
                    break;
                }

                // Numbers are only allowed to differ in the context of a joker.
                if (j <= currSequence.size() - 1) { // Check for out of bounds access
                    if ((currSequence.get(j).getNumber() != currSequence.get(j + 1).getNumber()) &&
                        ((currSequence.get(j).getNumber() != 0) || (currSequence.get(j + 1).getNumber() != 0))) {
                        groupRunErrors[i].setGroupError("Cannot have 2 non-jokers with different numbers per group");
                        break;
                    }
                }
            }
        }

        // Runs
        for (int i = 0; i < sequences.size(); ++i) {
            ArrayList<Tile> currSequence = sequences.get(i);
            for (int j = 0; j < currSequence.size(); ++j) {
                // Check for out of bounds numbers and where a joker is in between 2 offending tiles
                if ((j > 0) && (j < currSequence.size() - 1)) {
                    switch (currSequence.get(j).getNumber()) {
                        case 0: // Jokers, noting that there may be 2 of them in a row
                            if (!(currSequence.get(j-1).getColour().equals(currSequence.get(j+1).getColour())) ||
                                 (currSequence.get(j-1).getColour().equals(currSequence.get(j+2).getColour())) ||
                                 (currSequence.get(j-2).getColour().equals(currSequence.get(j+1).getColour()))) {
                                groupRunErrors[i].setRunError("Colour mismatch overlapping with a joker/s");
                                break;
                            }

                            if (!((currSequence.get(j-1).getNumber() == currSequence.get(j+1).getNumber()-2) ||
                                  (currSequence.get(j-2).getNumber() == currSequence.get(j+1).getNumber()-3) ||
                                  (currSequence.get(j-1).getNumber() == currSequence.get(j+2).getNumber()-3))) {
                                groupRunErrors[i].setRunError("Number mismatch overlapping with a joker/s");
                                break;
                            }
                        case 1:
                            groupRunErrors[i].setRunError("Cannot have a run with a 1 as a non-first tile");
                            break;
                        case 13:
                            groupRunErrors[i].setRunError("Cannot have a run with a 13 as a non-last tile");
                            break;
                        default:
                            break;
                    }

                    // Don't capture multiple errors per group (or run) check per sequence.
                    if (!groupRunErrors[i].getRunError().equals("")) { continue; }
                }
                else if (j < currSequence.size() - 1) { // Either of the above conditions would have done.
                    if ((currSequence.get(j+1).getNumber() != 0) &&
                        (currSequence.get(j+1).getNumber() != currSequence.get(j).getNumber() + 1)) {
                       groupRunErrors[i].setRunError("Cannot have a broken run sequence");
                    }
                    
                    // Don't capture multiple errors per group (or run) check per sequence.
                    if (!groupRunErrors[i].getRunError().equals("")) { continue; }

                    if (!currSequence.get(j+1).getColour().equals(currSequence.get(j).getColour())) {
                       groupRunErrors[i].setRunError("Cannot have multiple colours per run sequence");
                    }
                }
            }
        }
        
        int numFalse = 0;

        for (int i = 0; i < groupRunErrors.length; ++i) { // Count number of, and print errors.
            // A sequence should never be both a group and a run, but might be neither.
            if (!((groupRunErrors[i].getGroupError().equals("")) ^ (groupRunErrors[i].getRunError().equals("")))) {
                numFalse++;

                System.out.println("Sequence " + (i + 1) + " errors");
                System.out.println(groupRunErrors[i].getGroupError());
                System.out.println(groupRunErrors[i].getRunError());
                System.out.println();
            }        
        }

        return numFalse == 0;
    }

    /* A group is a 3- or 4-tile row of same-numbered tiles but different colours.
     * A run is a 3-tile (or more) row of same-coloured tiles in ascending order.
     */
    public static boolean checkBoardValidity(ArrayList<ArrayList<Tile>> sequences) {
        // Separate from other console output regardless of what might/mightn't be outputted.
        System.out.println();
        
        // TODO: Needs reworking
        boolean lengthOK = checkBoardLength(sequences); // All sequences have at least 3 tiles
        boolean groupRunOK = checkBoardSequences(sequences); // All sequences are either groups or runs

        // The 2nd function checks for run bounds.
        return lengthOK && groupRunOK;
    }

    // Only checks against runs.
    public static boolean checkRunBounds(ArrayList<Tile> currSequence, int currNum) {
        int currSize = currSequence.size();

        for (int j = 0; j < currSequence.size(); ++j) {
            if ((j > 0) && (currSequence.get(j).getNumber() == 1) && (currSequence.get(0).getNumber() != 1)) {
                System.out.print("Sequence " + (currNum + 1) + ": The number 1 must only be on ");
                System.out.println("the first tile, unless part of a group");
                return false;
            }

            if ((j < currSize - 1) && (currSequence.get(j).getNumber() == 13) && (currSequence.get(currSize - 1).getNumber() != 13)) {
                System.out.print("Sequence " + (currNum + 1) + ": The number 13 must only be on ");
                System.out.println("the last tile, unless part of a group");
                return false;
            }
        }

        return true;
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
                    System.out.println(">");
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

    public static void printUserCommands(int numPoolTiles, int numSequences) {
        System.out.println("\nChoose from one of the following commands (case insensitive):");
        
        System.out.println("Abort: Revert game state back to the last move");
        System.out.println("Finish: Finish current move and save current game state.");
        
        if (numSequences > 0) {
            System.out.print("Split: Split a chosen sequence at the specified turn. ");
            System.out.println("Note: Currently assumes there will be no gaps left in the middle");
        }
        
        if (numPoolTiles > 0) {
            System.out.println("Pool: Grab 1 tile from the pool (forfeits turn)");
        }

        // Extra blank line that need not be moved around as a result of building this method.
        System.out.println();
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
        
        while ((numPlayers < 2) || (numPlayers > 4)) {
            System.out.println("Error: Must select between 2-4 players");
            numPlayers = scnr.nextInt();
        }

        return numPlayers;
    }

    public static int setStartPlayer(Scanner scnr, int numPlayers) {
        System.out.println("Choose a starting player (or 5 for random)");
        int startPlayer = scnr.nextInt();
        
        // Escape to the next line, as this will be used once again for the next user input.
        scnr.nextLine();
        
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
            startPlayer = getRandom(1, numPlayers);
            System.out.println("Player " + startPlayer + " is starting");
        }
        
        return startPlayer;
    }

    // Currently assumes that the sequence will be split so as to not leave empty spaces
    public static ArrayList<ArrayList<Tile>> splitSequence(ArrayList<ArrayList<Tile>> sequences, Scanner scnr) {
        boolean validInput = false;
        int sequenceNumber = 0;
        int splittingPosition = 0;

        while (!validInput) {
            // Declare false once rather than declare true multiple times
            validInput = true;
        
            // Defined as the first number to go into the new array
            System.out.println("\nEnter the sequence number followed by the number of the 1st element to be moved");
            sequenceNumber = scnr.nextInt();
            splittingPosition = scnr.nextInt();
            
            // Indices are [1, n] in human readable format, stored as [0, n)
            if ((sequenceNumber < 1) || (sequenceNumber > sequences.size())) {
                System.out.println("Error: Sequence number not found");
                validInput = false;
                
                /* Not only would splittingPosition not have a valid value for
                 * a non-existent sequence, continuing with the iteration would
                 * also cause an out of bounds exception
                 */
                continue;
            }

            if (splittingPosition < 1) {
                System.out.println("Error: Cannot split at a non-positive position");
                validInput = false;
            }
            else if (splittingPosition == 1) {
                System.out.println("Error: Cannot split an entire sequence");
                validInput = false;
            }
            else if (splittingPosition > sequences.get(sequenceNumber - 1).size()) {
                System.out.println("Error: Sequence " + (sequenceNumber - 1) + " contains fewer than " + splittingPosition + " numbers");
                validInput = false;
            }
        }

        /* Copy the tiles to a new ArrayList, then put it after the original sequence
         * (everything gets shifted by 1). The sequence number must be decremented
         * to convert from [1, n] to [0, n) style, then incremented so that the new
         * sequence comes directly after. However, doing so in the loop will affect
         * the results prematurely.
         */
        sequenceNumber--;
        splittingPosition--;
        
        /* Do things in reverse order to avoid messing up the indices and double loops.
         * Instead of adding at the next available index, must add at the beginning to
         * preserve order. Anything pre-existing will be shifted by 1.
         */
        ArrayList<Tile> newSequence = new ArrayList<Tile>();
        for (int i = sequences.get(sequenceNumber).size() - 1; i >= splittingPosition; --i) {
            newSequence.add(0, sequences.get(sequenceNumber).get(i));
            sequences.get(sequenceNumber).remove(i);
        }
        sequences.add(++sequenceNumber, newSequence);
        
        scnr.nextLine(); // Escape in preparation for the next command
        return sequences;
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
        
        /* Each player must put down 30 or more points worth of their
         * own tiles (without touching any other tiles).
         */
        HashMap<String, Boolean> firstMoves = new HashMap<String, Boolean>();
        for (int i = 1; i <= numPlayers; ++i) {
            firstMoves.put("Player " + i, false);
        }

        sequences.add(tileLists.get("Player 1 tiles"));

        boolean gameFinished = false;
        while (!gameFinished) {
            printBoard(sequences);
            printPlayerTiles(tileLists, currPlayer);
            printUserCommands(tileLists.get("Pool tiles").size(), sequences.size());
            
            boolean validInput = false;
            while (!validInput) {
                /* Declare false once rather than declare true multiple times.
                 * Note that this represents whether the command is recognised,
                 * as well as if it could be executed given the current game state.
                 * Printing the game state and/or user commands would be too much
                 * directly following invalid input.
                 */
                validInput = true;

                // Store the game state as of the most recent move in case of desire to abort move.
                final ArrayList<ArrayList<Tile>> SEQUENCES_LAST_VERSION = sequences;
                final HashMap<String, ArrayList<Tile>> TILELISTS_LAST_VERSION = tileLists;

                String userInput = scnr.nextLine();
                switch (userInput.toUpperCase()){
                    // case "ADD":
                    case "ABORT": // Revert game state to original state.
                        sequences = SEQUENCES_LAST_VERSION;
                        tileLists = TILELISTS_LAST_VERSION;

                        System.out.println("Move successfully aborted.");
                        break;
                    case "FINISH": // Finish current move. The inner while-loop will 
                        if (checkBoardValidity(sequences)) {
                            System.out.println("Move successfully finished.");
                        }
                        else {
                            validInput = false;
                            System.out.println("Please use the above commands to any mistakes and try again");
                        }

                        break;
                    case "SPLIT": // Should only appear where upon execution, the if-statement would be true.
                        if (sequences.size() > 0) {
                            sequences = splitSequence(sequences, scnr);
                        }
                        else {
                            validInput = false;
                            System.out.println("Error: No sequences to split.");
                        }

                        break;
                    case "POOL": // Should only appear where upon execution, the if-statement would be true.
                        if (tileLists.get("Pool tiles").size() > 0) {
                            tileLists = randomPoolTile(tileLists, currPlayer);
                        }
                        else {
                            validInput = false;
                            System.out.println("Error: No tiles left in the pool.");
                        }
                        
                        break;
                    // case "SWAP":
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