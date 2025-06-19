import math, random

class ErrorMessage: # Use to easily print out all error messages along with their associated titles.
    def __init__(self):
        self.text = ""
        self.associatedTiles = []
    
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
    
def allocateTiles(numPlayers):
    pool, player1Tiles, player2Tiles, player3Tiles, player4Tiles = [], [], [], [], []
    
    # Add non-jokers
    for i in range(1, 13+1): # Tile numbers are in range[1, 13] but end index is exclusive
        for c1 in ["Blue", "Orange", "Black", "Red"]:
            for j in range(2):
                pool.append(Tile(c1, i))

    # Add jokers
    pool.append(Tile("Black"))
    pool.append(Tile("Red"))

    for i in range(0, 14 * numPlayers): # Add 14 tiles to each player by random assignment
        newTile = pool.pop(random.randint(0, len(pool) - 1)) # Analogous to Ctrl+X
        
        # By cycling between the number of tiles (as opposed to that of the players), we avoid assigning tiles to extra players (and potentially in an uneven fashion)
        match math.floor(i / 14):
            case 0:
                player1Tiles.append(newTile)
            case 1:
                player2Tiles.append(newTile)
            case 2:
                player3Tiles.append(newTile)
            case 3:
                player4Tiles.append(newTile)

    return pool, player1Tiles, player2Tiles, player3Tiles, player4Tiles

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

def printTileList(listName, tileList):
    if len(tileList) == 0:
        print("\n" + listName, "is empty")
    else:
        print("\n" + listName + ":", end = " ")
        if listName == "Board":
            for i in range(len(tileList)):
                print("Sequence", str(i+1), end = ": [")
                for j in range(len(tileList[i])):
                    print(tileList[i][j], end = ']\n' if j == len(tileList[i]) - 1 else ", ")
        else:
            for i in range(len(tileList)):
                if i == 0:
                    print("[", end = "")
                
                print(tileList[i], end = ", " if i < len(tileList) - 1 else "]")

if __name__ == '__main__': # Main method
    print("Welcome to Rummikub!", end = " ")
    
    # Get number of players
    numPlayers = getNumPlayers()
    while (numPlayers < 2 or numPlayers > 4):
        print("Error: Number of players must be between 2 and 4.", end = " ")
        numPlayers = getNumPlayers()
    
    # Choose the starting player explicitly or randomly
    currPlayer = getStartPlayer()
    while (currPlayer < 1 or (currPlayer > numPlayers and currPlayer != 5)):
        print("Error: Player number must be between 1 and " + str(numPlayers) + " (or 5 for random)", end = " ")
        currPlayer = getStartPlayer()
    
    # Randomly assign starting player (if necessary) and print out the final decision
    if currPlayer == 5:
        currPlayer = random.randint(1, numPlayers)
    print("Player", currPlayer, "is starting")

    pool, player1Tiles, player2Tiles, player3Tiles, player4Tiles = allocateTiles(numPlayers)
    if numPlayers < 4: # The respective lists should be empty anyway, but destroy them just in case
        del player4Tiles
        if numPlayers < 3:
            del player3Tiles

    finished = False
    board = [] # 2D-array of sequences
    while not finished: # Game-wide flow
        printTileList("Board", board)
        match currPlayer: # Print the current player's tile rack.
            case 1:
                printTileList("Player 1's rack", player1Tiles)
            case 2:
                printTileList("Player 2's rack", player2Tiles)
            case 3:
                printTileList("Player 3's rack", player3Tiles)
            case 4:
                printTileList("Player 4's rack", player4Tiles)

        # currPlayer = 1 if currPlayer == numPlayers else currPlayer + 1
        finished = True