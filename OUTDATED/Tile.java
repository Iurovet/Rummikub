public class Tile {
    private String colour;
    private int number; // 0 = joker, 1-13 = non-joker
    
    public Tile (String colour) {// Jokers only
        this.colour = colour; // Red or black (no effect on other tile colours)
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
}