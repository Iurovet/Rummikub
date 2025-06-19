class ErrorMessage: # Use to easily print out all error messages along with their associated titles.
    def __init__(self):
        self.text = ""
        self.associatedTiles = []
    
    def __str__(self): # Print it out
        return "Error: " + self.text + " for the following tiles: " + self.associatedTiles

class Tile:
    def __init__(self, colour): # Jokers only
        self.colour = colour # Red or black (no effect on other tile colours)
        self.number = 0

    def __init__(self, colour, number): # Non-jokers only
        self.colour = colour # Blue, orange, red or black
        self.number = number # Possible values are in the range [1, 13]
    
    def __str__(self): # Print the object
        match self.number:
            case 0:
                return self.colour + " joker"
            case _: # Default case
                return self.colour + " " + self.number
            
    def setColour(self, colour):
        self.colour = colour
    def getColour(self):
        return self.colour

    def setNumber(self, number):
        self.number = number
    def getNumber(self):
        return self.number

if __name__ == '__main__': # Main method
        
    pass