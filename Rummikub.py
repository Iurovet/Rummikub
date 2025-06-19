import random

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

def getNumPlayers():
    numPlayers = None
    
    try:
        numPlayers = int(input("Please enter the number of players (2-4)\n"))
    except Exception as e: # Wrong type
        numPlayers = 0 # Ensure wrong input doesn't get treated as valid
    
    return numPlayers

def getStartPlayer():
    startPlayer = None
    
    try:
        startPlayer = int(input("Please choose the player number (or 5 for random)\n"))
    except Exception as e: # Wrong type
        startPlayer = 0 # Ensure wrong input doesn't get treated as valid
    
    return startPlayer

if __name__ == '__main__': # Main method
    print("Welcome to Rummikub!", end = " ")
    
    # Get number of players
    numPlayers = getNumPlayers()
    while (numPlayers < 2 or numPlayers > 4):
        print("Error: Number of players must be between 2 and 4.", end = " ")
        numPlayers = getNumPlayers()
    
    # Choose explicitly or randomly
    startPlayer = getStartPlayer()
    while (startPlayer < 1 or (startPlayer > numPlayers and startPlayer != 5)):
        print("Error: Player number must be between 1 and " + str(numPlayers) + " (or 5 for random)", end = " ")
        startPlayer = getStartPlayer()
    
    # Randomly assign starting player (if necessary) and print out the final decision
    if startPlayer == 5:
        startPlayer = random.randint(1, numPlayers)
    print("Player", startPlayer, "is starting")