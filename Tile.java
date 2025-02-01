import java.util.ArrayList;

public class Tile {
    private String colour;
    private int number; // Non-jokers only
    private ArrayList<String> location = new ArrayList<String>(); // Store where the tile has been (player number does not matter)
    private int lastMoveIndex = 0; // Store the index of where the tile was as of the last move.
    
    public Tile (String colour) {// Jokers only
        this.colour = colour; // Red or black
        this.number = 0;
    }

    public Tile (String colour, int number) {// Non-jokers only
        this.colour = colour; // Blue, orange, red or black
        this.number = number; // Possible values are in the range [1, 13]
    }

    public String toString() {
        switch(number) {
            case 0:
                return colour + " joker";
            default:
                return colour + " " + number;
        }
    }

    public void setColour(String colour) { this.colour = colour; }
    public String getColour() { return colour; }
    
    public void setNumber(int number) { this.number = number; }
    public int getNumber() { return number; }
    
    public void addLocation(String newLocation) { location.add(newLocation); }
    public ArrayList<String> getLocation() { return location; }

    public void setLastMoveIndex(int lastMoveIndex) { this.lastMoveIndex = lastMoveIndex; }
    public int getLastMoveIndex() { return lastMoveIndex; }
}