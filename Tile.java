import java.util.ArrayList;

public class Tile {
    private String colour, type;
    private int number; // Non-jokers only
    private ArrayList<String> location = new ArrayList<String>(); // Store where the tile has been
    private int lastMoveIndex = 0; // Store the index of where the tile was as of the last move.
    
    public Tile (String colour, String type) {// Jokers only
        this.colour = colour; // Red or black
        this.type = type; // The value will be "Joker"
    }

    public Tile (String colour, String type, int number) {// Non-jokers only
        this.colour = colour; // Blue, orange, red or black
        this.type = type; // The value will be "Non-joker"
        this.number = number; // Possible values are in the range [1, 13]
    }

    public String toString() {
        switch(type) {
            case "Non-joker":
                return colour + " " + number;
            case "Joker":
                return colour + " " + type;
            default:
                return "";
        }
    }

    public void setColour(String colour) { this.colour = colour; }
    public String getColour() { return colour; }
    
    public void setType(String type) { this.type = type; }
    public String getType() { return type; }

    public void setNumber(int number) { this.number = number; }
    public int getNumber() { return number; }
    
    public void addLocation(String newLocation) { location.add(newLocation); }
    public ArrayList<String> getLocation() { return location; }

    public void setLastMoveIndex(int lastMoveIndex) { this.lastMoveIndex = lastMoveIndex; }
    public int getLastMoveIndex() { return lastMoveIndex; }
}