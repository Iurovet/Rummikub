import math, random # Floor function and random number, respectively
from Objects import * # Class definitions
    
def allocateTiles(numPlayers):
    pool, player1Rack, player2Rack, player3Rack, player4Rack = [], [], [], [], []
    
    # Add non-jokers
    for i in range(1, 13+1): # Tile numbers are in range[1, 13] but end index is exclusive
        for c1 in ["Blue", "Orange", "Black", "Red"]:
            for j in range(2):
                pool.append(Tile(c1, i))

    # # Add jokers
    # pool.append(Tile("Black"))
    # pool.append(Tile("Red"))

    for i in range(0, 14 * numPlayers): # Add 14 tiles to each player by random assignment
        newTile = pool.pop(random.randint(0, len(pool) - 1)) # Analogous to Ctrl+X
        
        # By cycling between the number of tiles (as opposed to that of the players), we avoid assigning tiles to extra players (and potentially in an uneven fashion)
        match math.floor(i / 14):
            case 0:
                player1Rack.append(newTile)
            case 1:
                player2Rack.append(newTile)
            case 2:
                player3Rack.append(newTile)
            case 3:
                player4Rack.append(newTile)

    return pool, [player1Rack, player2Rack, player3Rack, player4Rack] # Easier if the racks are in one array

# Returns true if the sequence contains 3 or 4 tiles of varying colour (containing the same numbers and/or jokers)
def checkGroup(sequence):
    if len(sequence) > 4: # A 5 (or more)-tile sequence is guaranteed to repeat colours, irrespective of jokers
        return False

    # Get separate lists of the numbers and colours found among the non-jokers
    (numbersList, coloursList) = [(x.getNumber(), x.getColour()) for x in sequence if x.getNumber() > 0]
    
    if len(set(numbersList)) > 1: # Must only find one unique number besides jokers
        return False    
    if len(coloursList) != len(set(coloursList)): # Any colour repeats will only show up in the 1st list.
        return False
    
    return True # If all checks have passed

# Returns true if the sequence is strictly increasing and of the same colour
def checkRun(sequence):
    # Get separate lists of the numbers and colours found among the non-jokers
    (numbersList, coloursList) = [(x.getNumber(), x.getColour()) for x in sequence if x.getNumber() > 0]

    if len(set(coloursList)) > 1: # Must only find one unique colour besides jokers
        return False    
    if len(numbersList) != len(set(numbersList)): # Any number repeats will only show up in the 1st list.
        return False

    # Check if the non-jokers are in the right order
    non_zeros = [x for x in numbersList if x != 0]
    if non_zeros != sorted(non_zeros):
        return False
    
    # Check if there are missing numbers not covered by jokers (may not be possible using list manipulations/comprehensions from above)
    currNumber = numbersList[0]
    for i in range(1, len(numbersList)):
        if numbersList[i] not in (numbersList[i-1] + 1, 0): # Perform 2 comparisons in one go
            return False
        currNumber += 1
    
    # If duplicate numbers have been caught earlier, there has to be a joker at at least one edge index
    if any([1, 13]) in numbersList[1:-1]:
        return False
    
    return True # If all checks have passed


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
        print("\n" + listName + ":")
        if listName == "Board":            
            for i in range(len(tileList)):
                print("Sequence", str(i+1), end = ": [")
                for j in range(len(tileList[i])):
                    print(tileList[i][j], end = ']\n' if j == len(tileList[i]) - 1 else ", ")
        else:
            for i in range(len(tileList)):
                if i == 0:
                    print("[", end = "")
                
                print(tileList[i], end = ", " if i < len(tileList) - 1 else "]\n\n")

def printUserCommands(numPoolTiles, numSequences):
    print("Choose from one of the following commands (case insensitive):")
    print("Abort: Revert game state back to the last move")
    print("Finish: Finish current move and save current game state.")
    
    if numSequences > 0:
        # print("Split: Split a chosen sequence at the specified turn. ")
        # print("Note: Currently assumes there will be no gaps left in the middle")
        pass
    
    if numPoolTiles > 0:
        print("Pool: Grab 1 tile from the pool (forfeits turn)")

    # Extra blank line that need not be moved around as a result of building this method.
    print()   

def validBoard(board):
    # # These are the following possible errors (if a sequence is valid, only one of 2a and 2b will be triggered):
    # # 1. A sequence containing < 3 tiles
    # # 2a. A sequence is not a group (i.e. the pattern is not entirely colour-based)
    # # 2b. A sequence is not a run (i.e. the pattern is not entirely number-based)

    # errorMessageList = []
    # for index, sequence in board:
    #     # Error 1 from above
    #     if len(sequence) < 3:
    #         errorMessageList.add(ErrorMessage("Sequence", index, "has < 3 tiles"))
    #         continue
               
    #     # Error 2a, 2b from above
    #     isGroup, isRun = checkGroup(sequence), checkRun(sequence)
    #     if not(isGroup ^ isRun): # All valid sequences are either groups or runs, not neither nor both.
    #         errorMessageList.append(ErrorMessage("Sequence", index, "must strictly increase or change colours, not neither nor both"))
    #         continue

    # for em in errorMessageList: print("Error:", em)
    # return len(errorMessageList) == 0
    return True # Temporary placeholder whilst the above hasn't been finished

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

    # Allocate tiles to the pool and all players
    pool, playerRacks = allocateTiles(numPlayers) # Again, it's easier if the player racks are all under one array
    if numPlayers < 4: # The respective lists should be empty anyway, but destroy them just in case
        del playerRacks[3]
        if numPlayers < 3:
            del playerRacks[2]

    board = [] # 2D-array of sequences
    finished = False
    while not finished: # Game-wide flow
        draftRack = playerRacks[currPlayer - 1].copy() # Copy the current rack so that it can be returned to later if need be
        poppedTiles = [] # Keep track of tiles grabbed from the pool
        
        printTileList("Board", board) # Print the board
        printTileList("Player " + str(currPlayer) + "'s rack", playerRacks[currPlayer - 1]) # Print the current rack
        while True: # Turn-wide flow
            printUserCommands(len(pool), sum(len(sequence) for sequence in board)) # Number of tiles in the pool and board, respectively
            command = input().lower() # Could also use upper(), since case insensitivity is the goal

            # If there is a simple condition like empty list that can invalidate a command, the user cannot see it nor will
            # they be able to enter it regardless. However, it's up to the user to check if the board is valid.
            match command:
                case "abort": # Discard all changes
                    pool.extend(poppedTiles) # Return any required tiles back to the pool
                    break
                case "finish": # Finalise all changes, assuming the board is in a legal state.
                    if validBoard(board):
                        playerRacks[currPlayer - 1] = draftRack.copy() # Copy the draft rack back to the working copy.
                        break
                    continue
                case "pool" if len(pool) > 0: # Randomly assign a tile from the pool if possible
                    randTile = pool.pop(random.randint(0, len(pool) - 1))
                    draftRack.append(randTile)
                    poppedTiles.append(randTile)

                    printTileList("Player " + str(currPlayer) + "'s rack", draftRack) # Print the current rack
                    continue
                case _:
                    print("Error: Command not recognised")

        if len(playerRacks[currPlayer - 1]) == 0: # Current player emptied all their tiles
            print("Player", currPlayer, "won!")
            finished = True
        else: # Next player
            currPlayer = 1 if currPlayer == numPlayers else currPlayer + 1