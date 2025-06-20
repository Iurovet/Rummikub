class ErrorMessage: # Use to easily print out all error messages along with their associated titles.
    def __init__(self, text, associatedTiles):
        self.text = text
        self.associatedTiles = associatedTiles
    
    def __str__(self): # Print it out
        return "Error: " + self.text + " for the following tiles: " + self.associatedTiles

class Tile:
    def __init__(self, colour, number=None): # Only non-jokers will have the 2nd parameter (cannot overload constructors)
        self.colour = colour # Blue, orange, red or black (the latter 2 for jokers only)
        self.number = number if number is not None else 0 # [1, 13] for the former case
    
    def __str__(self): # Print the object
        match self.number:
            case 0:
                return str(self.colour) + " joker"
            case _: # Default case
                return str(self.colour) + " " + str(self.number)
            
    def setColour(self, colour):
        self.colour = colour
    def getColour(self):
        return self.colour

    def setNumber(self, number):
        self.number = number
    def getNumber(self):
        return self.number