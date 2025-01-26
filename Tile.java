public class Tile {
    private String colour, type;
    private int number; // Non-jokers only
    private String numberID; // Each colour-number combination has 2 tiles
    private String location = "pool"; // Pool, p(1-4) or play
    
    public Tile (String colour, String type) {// Jokers only
        this.colour = colour; // Red or black
        this.type = type; // The value will be "joker"
    }

    public Tile (String colour, String type, int number, String numberID) {// Non-jokers only
        this.colour = colour; // Blue, orange, red or black
        this.type = type; // The value will be "non-joker"
        this.number = number; // Possible values are in the range [1, 13]
        this.numberID = numberID; // Either "a" or "b"
    }

    public void setColour(String colour) { this.colour = colour; }
    public String getColour() { return colour; }
    
    public void setType(String type) { this.type = type; }
    public String getType() { return type; }

    public void setNumber(int number) { this.number = number; }
    public int getNumber() { return number; }
    
    public void setNumberID(String numberID) { this.numberID = numberID; }
    public String getNumberID() { return numberID; }
    
    public void setLocation(String location) { this.location = location; }
    public String getLocation() { return location; }
}