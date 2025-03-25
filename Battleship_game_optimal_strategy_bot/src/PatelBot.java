import battleship.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * The PatelBot class implements the BattleShipBot interface and represents a bot
 * for the BattleShip3 game. The bot uses a multi-phase strategy to search for and sink ships
 * on the game grid. It begins by scanning from the center of the grid, expanding in a cyclonic pattern
 * with specific spacing to optimize ship detection. Once a ship is found, the bot sinks it and removes
 * both the ship's coordinates and its surrounding spots from the search pool. If all ships are not fully sunk
 * after completing the three phases, the bot resorts to making random guesses to find any remaining ships.
 *
 * This bot operates in three phases:
 * Initial scan with large spacing (phase one)
 * Secondary scan with smaller spacing (phase two)
 * Final scan with even smaller spacing for leftover spots (phase three)
 *
 * @author Maharshi Patel (000738366)
 */

public class PatelBot implements BattleShipBot {
    private int gameSize;
    private BattleShip3 battleShip;

    // List of all grid coordinates (x, y)
    private ArrayList<ArrayList<Integer>> gameBoardGrid;
    // Array representation of grid coordinates
    private int[][] gameBoardGridArray;

    private ArrayList<Integer> phaseOneHitsLocations;
    int phaseOneHitsCounter;
    private ArrayList<Integer> phaseTwoHitsLocations;
    int phaseTwoHitsCounter;
    private ArrayList<Integer> phaseThreeHitsLocations;
    int phaseThreeHitsCounter;

    /**
     * Initializes the bot by setting up the game board and phase one, two, and three hit locations.
     *
     * @param battleShipGame Reference to the BattleShip3 game object
     */
    @Override
    public void initialize(BattleShip3 battleShipGame) {

        battleShip = battleShipGame;
        gameSize = BattleShip3.BOARD_SIZE;
        phaseOneHitsCounter = 0;
        phaseTwoHitsCounter = 0;
        phaseThreeHitsCounter = 0;

        gameBoardGrid = new ArrayList<ArrayList<Integer>>();
        gameBoardGrid = getGameBoard();

        gameBoardGridArray = new int[gameSize * gameSize][2];
        gameBoardGridArray = getGameBoardArray();

        phaseOneHitsLocations = new ArrayList<Integer>();
        phaseOneHitsLocations = strategyHits(0,0,8,0,6);

        phaseTwoHitsLocations = new ArrayList<Integer>();
        phaseTwoHitsLocations = strategyHits(-1,-1,4,0,6);

        phaseThreeHitsLocations = new ArrayList<Integer>();
        phaseThreeHitsLocations = strategyHits(-1,0,2,0,6);
    }

    /**
     * Fires a shot based on the current phase's strategy. The bot progresses through each phase,
     * trying to hit pre-determined target locations for that phase. If no targets are left in any phase,
     * the bot switches to random guesses.
     */
    @Override
    public void fireShot() {

        if (phaseOneHitsCounter < phaseOneHitsLocations.size()) {
            // Get the target location from phase one hit locations
            int location = phaseOneHitsLocations.get(phaseOneHitsCounter);

            int x = gameBoardGridArray[location][0];
            int y = gameBoardGridArray[location][1];

            // Create coordinates to match against gameBoardGrid
            ArrayList<Integer> coordinates = new ArrayList<>();
            coordinates.add(x);
            coordinates.add(y);

            // Check if the coordinates exist in the grid and fire the shot
            if (gameBoardGrid.contains(coordinates)) {
                // Fire a shot at the target coordinates
                boolean hit = battleShip.shoot(new Point(x, y));

                if (hit) {
                    hit(x, y);
                } else {
                    miss(x, y);
                }
            }
            // Increment the phase one hit counter
            phaseOneHitsCounter++;
        }

        else if (phaseTwoHitsCounter < phaseTwoHitsLocations.size()) {
            // Get the target location from phase two hit locations
            int location = phaseTwoHitsLocations.get(phaseTwoHitsCounter);

            int x = gameBoardGridArray[location][0];
            int y = gameBoardGridArray[location][1];

            // Create coordinates to match against gameBoardGrid
            ArrayList<Integer> coordinates = new ArrayList<>();
            coordinates.add(x);
            coordinates.add(y);

            // Check if the coordinates exist in the grid and fire the shot
            if (gameBoardGrid.contains(coordinates)) {
                // Check if there are adjacent cells (up, down, left, right) around the target
                if (gameBoardGrid.contains(new ArrayList<>(List.of(x - 1, y))) || gameBoardGrid.contains(new ArrayList<>(List.of(x + 1, y))) || gameBoardGrid.contains(new ArrayList<>(List.of(x, y - 1))) || gameBoardGrid.contains(new ArrayList<>(List.of(x, y + 1)))) {
                    // Fire a shot at the target coordinates
                    boolean hit = battleShip.shoot(new Point(x, y));

                    if (hit) {
                        hit(x, y);
                    } else {
                        miss(x, y);
                    }
                }
            }
            // Increment the phase two hit counter
            phaseTwoHitsCounter++;
        }
        // If both phase one and phase two are exhausted, move to phase three
        else if (phaseThreeHitsCounter < phaseThreeHitsLocations.size()) {
            // Retrieve the next target location from phase three hit locations
            int location = phaseThreeHitsLocations.get(phaseThreeHitsCounter);

            int x = gameBoardGridArray[location][0];
            int y = gameBoardGridArray[location][1];

            // Create coordinates to match against gameBoardGrid
            ArrayList<Integer> coordinates = new ArrayList<>();
            coordinates.add(x);
            coordinates.add(y);

            // Check if the coordinates exist in the grid
            if (gameBoardGrid.contains(coordinates)) {
                // Check if there are adjacent cells (up, down, left, right) around the target
                if (gameBoardGrid.contains(new ArrayList<>(List.of(x - 1, y))) || gameBoardGrid.contains(new ArrayList<>(List.of(x + 1, y))) || gameBoardGrid.contains(new ArrayList<>(List.of(x, y - 1))) || gameBoardGrid.contains(new ArrayList<>(List.of(x, y + 1)))) {
                    // Fire a shot at the target coordinates
                    boolean hit = battleShip.shoot(new Point(x, y));

                    if (hit) {
                        hit(x, y);
                    } else {
                        miss(x, y);
                    }
                }
            }
            // Increment the phase three counter to move to the next target in phase three
            phaseThreeHitsCounter++;
        }

        // If all phases are exhausted, fire a random shot
        else {
            // Randomly select a target from the remaining grid coordinates
            Random random = new Random();
            int randomIndex = random.nextInt(gameBoardGrid.size());

            // Retrieve the selected random target's coordinates
            int x = gameBoardGrid.get(randomIndex).get(0);
            int y = gameBoardGrid.get(randomIndex).get(1);

            // Fire a shot at the random coordinates
            boolean hit = battleShip.shoot(new Point(x, y));

            if (hit) {
                hit(x, y);
            } else {
                miss(x, y);
            }
        }
    }

    /**
     * This method is called when the bot successfully hits a target.
     * It removes the coordinates from the game board grid, sinks the ship, and removes
     * the surrounding spots to fully mark the ship as sunk.
     *
     * @param x The x-coordinate of the shot location.
     * @param y The y-coordinate of the shot location.
     */
    public void hit(int x, int y){
        // Remove the coordinates where the shot was fired from the game board
        gameBoardGrid.remove(new ArrayList<>(List.of(x,y)));

        ArrayList<ArrayList<Integer>> lastSunkShipCoordinates = sinkShip(x,y);
        removeSurroundingSpots(lastSunkShipCoordinates);

        //System.out.println(lastSunkShipCoordinates);

    }

    /**
     * This method is called when the bot misses a target (no ship at the fired coordinates).
     * It removes the coordinates from the game board grid to indicate that the spot has been shot at.
     *
     * @param x The x-coordinate of the shot location.
     * @param y The y-coordinate of the shot location.
     */
    public void miss(int x, int y){
        gameBoardGrid.remove(new ArrayList<>(List.of(x,y)));
    }

    /**
     * Initializes the game board with all possible coordinates (from (0,0) to (gameSize-1, gameSize-1)).
     * It populates the `gameBoardGrid` with a list of coordinate pairs representing all spots on the board.
     *
     * @return The `gameBoardGrid` with all the coordinate pairs representing the grid.
     */
    public ArrayList<ArrayList<Integer>> getGameBoard() {
        // Initialize the grid with rows and columns
        for (int row = 0; row < gameSize; row++) {
            for (int column = 0; column < gameSize; column++) {
                // Create a new inner ArrayList for each coordinate pair
                ArrayList<Integer> coordinates = new ArrayList<>();
                coordinates.add(row);
                coordinates.add(column);

                // Add the new inner list to the gameBoardGrid
                gameBoardGrid.add(coordinates);
            }
        }
        return gameBoardGrid;
    }

    /**
     * Initializes the game board and stores all the coordinates in a 2D array (`gameBoardGridArray`).
     * This array represents all possible grid coordinates with their x and y values.
     *
     * @return The 2D array (`gameBoardGridArray`) containing all possible coordinates for the grid.
     */
    public int[][] getGameBoardArray(){

        int counter = 0;

        for (int row = 0; row < gameSize; row++) {
            for (int column = 0; column < gameSize; column++) {
                // Assign row and column values to the gameBoardGridArray
                gameBoardGridArray[counter][0] = row;
                gameBoardGridArray[counter][1] = column;
                counter++;
;            }
        }
        return gameBoardGridArray;
    }

    /**
     * This method generates a list of potential hit locations for the bot based on a spiral search strategy.
     * It begins at a specific point near the center of the grid and expands outward in layers, checking for coordinates
     * that satisfy certain conditions based on the given spacing and expansion parameters.
     * The resulting list of hit locations(index, 0 to 143) is stored in `phaseOneHitsLocations`.
     *
     * The search pattern starts from a specified offset from the center and proceeds in layers. Each layer consists of
     * checking positions along the perimeter of a square, with the spacing determining the interval between valid coordinates.
     * The method ensures that coordinates are within the bounds of the game grid and that the sum of the coordinates
     * satisfies the spacing condition.
     *
     * @param distanceFromCenterPointX The offset distance from the center in the X direction.
     * @param distanceFromCenterPointY The offset distance from the center in the Y direction.
     * @param spacing The interval spacing between valid hit locations.
     * @param layerDistanceFromCenter The minimum distance from the center to start searching.
     * @param expansion The maximum distance from the center to search for hit locations.
     *
     * @return An ArrayList of integers representing the indices of valid hit locations(index, 0 to 143) based on the strategy.
     */
    public ArrayList<Integer> strategyHits(int distanceFromCenterPointX, int distanceFromCenterPointY, int spacing, int layerDistanceFromCenter,int expansion){
        // Initialize an ArrayList to store the calculated hit locations
        ArrayList<Integer> phaseOneHitsLocations = new ArrayList<>();

        // Calculate the center point of the game board and apply the offsets
        int centerX = gameSize/2 + distanceFromCenterPointX;
        int centerY = gameSize/2 + distanceFromCenterPointY;

        // Loop through layers starting from layerDistanceFromCenter to expansion
        for (int layer = layerDistanceFromCenter; layer <= expansion; layer++) {

            // Check horizontal lines (left to right and right to left) at each layer
            for (int x = centerX - layer; x <= centerX + layer; x++) {
                int topY = centerY - layer;
                int bottomY = centerY + layer;

                // Ensure the x coordinate is within the game board boundaries
                if (x >= 0 && x < gameSize) {
                    if (topY >= 0 && (x + topY) % spacing == 0){
                        phaseOneHitsLocations.add(x * gameSize + topY);
                    }
                    if (bottomY < gameSize && topY != bottomY && (x + bottomY) % spacing == 0){
                        phaseOneHitsLocations.add(x * gameSize + bottomY);
                    }
                }
            }

            // Check vertical lines (top to bottom and bottom to top) at each layer
            for (int y = centerY - layer + 1; y <= centerY + layer - 1; y++) {
                int leftX = centerX - layer;
                int rightX = centerX + layer;

                // Ensure y is within valid bounds on the game board
                if (y >= 0 && y < gameSize) {
                    if (leftX >= 0 && (leftX + y) % spacing == 0) {
                        phaseOneHitsLocations.add(leftX * gameSize + y);
                    }
                    if (rightX < gameSize && leftX != rightX && (rightX + y) % spacing == 0) {
                        phaseOneHitsLocations.add(rightX * gameSize + y);
                    }
                }
            }
        }

        return phaseOneHitsLocations;
    }
    /**
     * This method attempts to sink a ship starting from the given coordinates (x, y).
     * It checks in four directions (up, right, down, left) to determine if the ship extends
     * in any of those directions and shoots at the corresponding coordinates to sink the ship.
     * The method continues shooting at adjacent cells in the same direction if the ship is not fully sunk
     * until it either sinks the ship or there are no more valid cells to shoot in that direction.
     *
     * The method returns a list of coordinates representing the entire ship that was sunk.
     * These coordinates are also removed from the game board during the process.
     * If the ship is successfully sunk, the list of sunk ship coordinates is returned.
     *
     * @param x The X coordinate of the first hit cell on the ship.
     * @param y The Y coordinate of the first hit cell on the ship.
     *
     * @return An ArrayList of ArrayLists, where each inner list contains the coordinates of a part of the sunk ship.
     */
    public ArrayList<ArrayList<Integer>> sinkShip(int x, int y){

        ArrayList<ArrayList<Integer>> sinkingShip = new ArrayList<ArrayList<Integer>>();
        ArrayList<Integer> coordinates = new ArrayList<>();
        coordinates.add(x);
        coordinates.add(y);
        sinkingShip.add(coordinates);

        // Flags to track the direction of the ship (vertical or horizontal).
        boolean verticalShip = false;
        boolean horizontalShip = false;

        // Variables to track the movement in X and Y directions.
        int movingX = x;
        int movingY = y;

        // Get the current number of ships sunk to compare later.
        int sunkShips = battleShip.numberOfShipsSunk();
        // A flag to check if the ship is successfully sunk after shooting.
        boolean sunk = (battleShip.numberOfShipsSunk() == sunkShips + 1);

        // Flags to determine if there are valid cells in each direction.
        boolean upperCellHit = true;
        boolean rightCellHit = true;
        boolean lowerCellHit = true;
        boolean leftCellHit = true;

        // Check upwards from the initial hit
        while(upperCellHit){
            ArrayList<Integer> upperCell = new ArrayList<>();
            movingX = movingX - 1;
            upperCell.add(movingX);
            upperCell.add(y);
            upperCellHit = false;

            if (gameBoardGrid.contains(upperCell)) {
               upperCellHit = battleShip.shoot(new Point(movingX, y));
               if(upperCellHit){
                   verticalShip = true;
                   ArrayList<Integer> newCoordinates = new ArrayList<>();
                   newCoordinates.add(movingX);
                   newCoordinates.add(y);
                   sinkingShip.add(newCoordinates);
                   gameBoardGrid.remove(newCoordinates);

                   if(sunk) {return sinkingShip;}
               }
            }
        }

        // Check right from the initial hit if the ship is not vertical
        while(rightCellHit && !verticalShip){
            ArrayList<Integer> rightCell = new ArrayList<>();
            movingY = movingY + 1;
            rightCell.add(x);
            rightCell.add(movingY);
            rightCellHit = false;

            if (gameBoardGrid.contains(rightCell)) {
                rightCellHit = battleShip.shoot(new Point(x, movingY));
                if(rightCellHit){
                    horizontalShip = true;
                    ArrayList<Integer> newCoordinates = new ArrayList<>();
                    newCoordinates.add(x);
                    newCoordinates.add(movingY);
                    sinkingShip.add(newCoordinates);
                    gameBoardGrid.remove(newCoordinates);
                    if(sunk) {return sinkingShip;}
                }
            }
        }

        // Reset the X coordinate before checking downwards
        movingX = x;

        // Check downwards from the initial hit if the ship is not horizontal
        while(lowerCellHit  && !horizontalShip){
            ArrayList<Integer> lowerCell = new ArrayList<>();
            movingX = movingX + 1;
            lowerCell.add(movingX);
            lowerCell.add(y);
            lowerCellHit = false;
            if (gameBoardGrid.contains(lowerCell)) {
                lowerCellHit = battleShip.shoot(new Point(movingX, y));
                if(lowerCellHit){
                    ArrayList<Integer> newCoordinates = new ArrayList<>();
                    newCoordinates.add(movingX);
                    newCoordinates.add(y);
                    sinkingShip.add(newCoordinates);
                    gameBoardGrid.remove(newCoordinates);
                    if(sunk) {return sinkingShip;}
                }
            }
        }

        // Reset the Y coordinate before checking leftwards
        movingY = y;

        // Check left from the initial hit if the ship is not vertical
        while(leftCellHit  && !verticalShip){
            ArrayList<Integer> leftCell = new ArrayList<>();
            movingY = movingY - 1;
            leftCell.add(x);
            leftCell.add(movingY);
            leftCellHit = false;
            if (gameBoardGrid.contains(leftCell)) {
                leftCellHit = battleShip.shoot(new Point(x, movingY));
                if(leftCellHit){
                    ArrayList<Integer> newCoordinates = new ArrayList<>();
                    newCoordinates.add(x);
                    newCoordinates.add(movingY);
                    sinkingShip.add(newCoordinates);
                    gameBoardGrid.remove(newCoordinates);
                    if(sunk) {return sinkingShip;}
                }
            }
        }
        return sinkingShip;
    }

    /**
     * This method removes the surrounding spots (not diagonal cells) of the last sunk ship
     * from the game board grid. The method iterates through each coordinate of the
     * last sunk ship and removes its adjacent cells (up, down, left, right) from the grid.
     *
     * The surrounding spots are removed only if they exist in the grid
     *
     * @param lastSunk An ArrayList of ArrayLists, where each inner list contains the
     *                 coordinates of a part of the last sunk ship. These coordinates
     *                 are used to determine which surrounding spots need to be removed.
     */
    public void removeSurroundingSpots(ArrayList<ArrayList<Integer>> lastSunk) {
        if (!lastSunk.isEmpty()) {

            for (ArrayList<Integer> coordinates : lastSunk) {

                int x = coordinates.get(0);
                int y = coordinates.get(1);

                // Remove the surrounding coordinates (up, down, left, right)
                gameBoardGrid.remove(new ArrayList<>(List.of(x - 1, y)));
                gameBoardGrid.remove(new ArrayList<>(List.of(x + 1, y)));
                gameBoardGrid.remove(new ArrayList<>(List.of(x, y - 1)));
                gameBoardGrid.remove(new ArrayList<>(List.of(x, y + 1)));
            }
        }
    }

    /**
     * Returns the name of the author of this class or project.
     *
     * @return A string containing the name of the author.
     */
    @Override
    public String getAuthors() {
        return "Maharshi Patel (000738366)";
    }
}