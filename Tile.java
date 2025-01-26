public class Tile {
    private String colour, type;
    private int number; // Non-jokers only
    private String numberID; // Each colour-number combination has 2 tiles
    
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
}