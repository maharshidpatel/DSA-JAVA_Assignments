import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Scanner;

/**
 * The IceDataAnalysis class handles the analysis of ice sheet data from a text file.
 * It identifies weak spots and cracks on ice sheets based on certain conditions.
 * Created on September 20, 2024
 * @author Maharshi Patel
 */
public class IceDataAnalysis {

    /**
     * Main method to run the ice sheet analysis program.
     * This method reads the ice sheet data from a file, identifies weak spots and cracks,
     * and prints the results.
     *
     * @param args unused.
     */
    public static void main(String[] args) {

        // Convert the text file data into a 3D array of ice sheets using textDataToArray function
        int[][][] iceDataArray = textDataToArray("./ICESHEETS_F24.TXT");

        // Ensure that the data was loaded successfully
        assert iceDataArray != null;

        // Get the weak spots information from the iceDataArray using weakSpots function
        int[][][] weakSpotsResult = weakSpots(iceDataArray);

        // Calculate the cracks based on weak spots data and the original iceDataArray
        int[][] crackResult = cracks(iceDataArray, weakSpotsResult);

        System.out.println("PART A:");
        // Print the number of weak spots on each sheet
        for (int i = 0; i < weakSpotsResult[3][0].length; i++){
            System.out.printf("Sheet %d has %d weak spots\n", i, weakSpotsResult[3][0][i]);
        }
        // Print the total weak spots across all sheets
        System.out.printf("\nTotal weak Spots on all Sheets = %d\n", weakSpotsResult[0][0][0]);
        // Print the sheet with the highest number of weak spots
        System.out.printf("Sheet %d has the highest number of weak Spots = %d\n", weakSpotsResult[2][0][0], weakSpotsResult[1][0][0]);

        System.out.println("\nPART B:");
        // Print the detected cracks and their locations
        for (int i = 0; i < crackResult.length; i++){
            System.out.printf("CRACK DETECTED @ [Sheet[%d](%d,%d)]\n", crackResult[i][0], crackResult[i][1], crackResult[i][2]);
        }

        System.out.println("\nSUMMARY");
        // Print the total number of weak spots that have cracked
        System.out.printf("The total number of weak spots that have cracked = %d\n", crackResult.length);
        // Print the fraction of weak spots that turned into cracks
        System.out.printf("The fraction of weak spots that are also cracks is %.3f", (double)crackResult.length/weakSpotsResult[0][0][0]);
    }

    /**
     * Reads ice sheet data from a text file and converts it into a 3D array.
     *
     * @param textFilePath The path to the text file containing the ice sheet data.
     * @return A 3D array representing the ice sheets data, or null if an error occurs.
     */
    public static int[][][] textDataToArray(String textFilePath){
        try {
            // Create a new File object using the provided path.
            File textFile = new File(textFilePath);
            // Create a Scanner object to read the file.
            Scanner reader = new Scanner(textFile);

            // Read the number of ice sheets.
            int iceSheets = reader.nextInt();
            // Initialize a 3D array to store the data for all ice sheets.
            int[][][] iceDataArray = new int[iceSheets][][];

            // Loop through each ice sheet.
            for (int iceSheet = 0; iceSheet < iceSheets; iceSheet++){
                // Read the number of rows and columns for this ice sheet.
                int rows = reader.nextInt();
                int columns = reader.nextInt();

                // Initialize a 2D array for the current ice sheet.
                iceDataArray[iceSheet] = new int[rows][columns];

                // Loop through each cell of the ice sheet and store the data.
                for (int row = 0; row < rows; row++){
                    for(int column = 0; column < columns; column++){
                        // Store the data value for each cell.
                        iceDataArray[iceSheet][row][column] = reader.nextInt();
                    }
                }
            }
            // Return the populated 3D array.
            return iceDataArray;
        }
        catch (FileNotFoundException exception) {
            // Print an error message if the file is not found.
            System.out.println(exception.getMessage());
        }
        // Return null if an exception occurs.
        return null;
    }

    /**
     * Analyzes the 3D data array to find weak spots on the ice sheets.
     * Weak spots are locations where the value is less than or equal to 200 and divisible by 50.
     *
     * @param dataArray The 3D array containing ice sheet data.
     * @return A 3D array containing the total number of weak spots, the sheet number with the most weak spots, the number of weak spots on each sheet, and a 2D array of weak spot locations.
     */
    public static int[][][] weakSpots(int[][][] dataArray){

        // Initialize variables to store total weak spots, current sheet's weak spots,
        // maximum weak spots on any sheet, and the sheet number with the most weak spots.
        int totalWeakSpots = 0;
        int currentSheetTotalWeakSpots = 0;
        int maximumTotalWeakSpotsOnSheet = 0;
        int maximumTotalWeakSpotsSheetNumber = 0;

        // Array to store the number of weak spots on each sheet.
        int[] maximumTotalWeakSpotsOnSheetArray = new int[dataArray.length];
        // 2D array to store the weak spots locations.
        int[][] weakSpotsLocationsArray = new int[dataArray.length][];

        // Loop through each ice sheet in given 3D array (dataArray).
        for(int iceSheet = 0; iceSheet < dataArray.length; iceSheet++){

            // Initialize the weak spots location array for the current sheet.
            // The array size is twice the number of cells (row * column * 2) + 2 to include dimensions(number of rows and columns for current ice sheet).
            // Assuming the worst-case scenario where every cell is a weak spot, row numbers will be stored at even positions and column numbers at odd positions in the weakSpotsLocationsArray[iceSheet].
            weakSpotsLocationsArray[iceSheet] = new int[((dataArray[iceSheet].length) * (dataArray[iceSheet][0].length) * 2 ) + 2];

            // Store the dimensions of the ice sheet (rows and columns) at the start of the array.
            weakSpotsLocationsArray[iceSheet][0] = dataArray[iceSheet].length;
            weakSpotsLocationsArray[iceSheet][1] = dataArray[iceSheet][0].length;

            // Initialize the index for the weak spots location array (start from position 1, next data will be stored after increasing counter since, first two position is used for dimensions).
            int weakSpotsLocationsArrayIndex = 1;

            // Loop through each row of the current ice sheet.
            for(int row = 0; row < dataArray[iceSheet].length; row++){
                // Loop through each column in the current row.
                for(int column = 0; column < dataArray[iceSheet][row].length; column++){

                    // Check if the current cell is a weak spot (value <= 200 and divisible by 50).
                    if ((dataArray[iceSheet][row][column] <= 200) && (dataArray[iceSheet][row][column] % 50 == 0)){
                        // Increment total weak spots counter.
                        totalWeakSpots++;

                        // Update the index and store the row and column of the weak spot.
                        currentSheetTotalWeakSpots++;
                        weakSpotsLocationsArrayIndex++;
                        weakSpotsLocationsArray[iceSheet][weakSpotsLocationsArrayIndex] = row;
                        weakSpotsLocationsArrayIndex++;
                        weakSpotsLocationsArray[iceSheet][weakSpotsLocationsArrayIndex] = column;
                    }
                }

            }
            // Resize the weak spots locations array for the current sheet to the actual number of weak spots.
            weakSpotsLocationsArray[iceSheet] = Arrays.copyOf(weakSpotsLocationsArray[iceSheet], weakSpotsLocationsArrayIndex + 1);

            // If the current sheet has more weak spots than the previous maximum, update the max values.
            if (currentSheetTotalWeakSpots > maximumTotalWeakSpotsOnSheet){
                maximumTotalWeakSpotsOnSheet = currentSheetTotalWeakSpots;
                // Store the sheet number with the most weak spots.
                maximumTotalWeakSpotsSheetNumber = iceSheet;
            }
            // Store the weak spots count for the current sheet in the array.
            maximumTotalWeakSpotsOnSheetArray[iceSheet] = currentSheetTotalWeakSpots;

            // Reset the weak spots counter for the next sheet.
            currentSheetTotalWeakSpots = 0;
        }
        //  System.out.println(Arrays.deepToString(weakSpotsLocationsArray));

        // Return the result as a 3D array containing:
        // 1. Total weak spots.
        // 2. Maximum weak spots on a single sheet.
        // 3. The sheet number with the most weak spots.
        // 4. Array of weak spots per sheet.
        // 5. 2D array of weak spots locations.
        return new int[][][]{
                {new int[]{totalWeakSpots}},
                {new int[]{maximumTotalWeakSpotsOnSheet}},
                {new int[]{maximumTotalWeakSpotsSheetNumber}},
                {maximumTotalWeakSpotsOnSheetArray},
                weakSpotsLocationsArray
        };
    }

    /**
     * Identifies cracks in the ice sheets based on weak spots.
     * A crack is detected if any neighboring spot of a weak spot has a value divisible by 10.
     *
     * @param dataArray The 3D array containing ice sheet data.
     * @param weakSpotsArray The array containing weak spot locations.
     * @return A 2D array listing the cracks detected with sheet, row, and column coordinates.
     */
    public static int[][] cracks(int[][][] dataArray, int[][][] weakSpotsArray){

        // Initialize the number of cracks and create a 2D array with the length of weak spots,
        // assuming the worst-case scenario where every weak spot results in a crack.
        // Each entry will store the crack location in the format [iceSheet, row, column].
        int numberOfCracks = 0;
        int[][] cracksArray = new int[weakSpotsArray[0][0][0]][3];

        // Weak spots' coordinates array
        int[][] locationCoordinates = weakSpotsArray[4];

        // Loop through each ice sheet of locationCoordinates array
        for (int iceSheet = 0; iceSheet < locationCoordinates.length; iceSheet++){

            // Get the maximum row and column index for the current ice sheet subtracting 1 from dimensions values
            int maximumRowIndex = (locationCoordinates[iceSheet][0] - 1);
            int maximumColumnIndex = (locationCoordinates[iceSheet][1] - 1);

            // Check if the ice sheet has weak spots and valid 2D data for current ice sheet.
            if(locationCoordinates[iceSheet].length > 2 && maximumRowIndex > 0 && maximumColumnIndex > 0){

                // Iterate through the weak spots, skipping the first two elements (dimensions)
                // Check neighboring cells
                // If any neighboring cell is divisible by 10, store the crack location
                for (int i = 2; i < locationCoordinates[iceSheet].length; i += 2){

                    // Get the row and column of the current weak spot
                    int rowLocation = locationCoordinates[iceSheet][i];
                    int columnLocation = locationCoordinates[iceSheet][i+1];

                    // Check for a crack in the top-left corner (0,0)
                    if(rowLocation == 0 && columnLocation == 0){

                        int right = dataArray[iceSheet][rowLocation][columnLocation + 1];
                        int bottomRight = dataArray[iceSheet][rowLocation + 1][columnLocation + 1];
                        int bottom = dataArray[iceSheet][rowLocation + 1][columnLocation];

                        if(right % 10 == 0 || bottomRight % 10 == 0 || bottom % 10 == 0){
                            cracksArray[numberOfCracks][0] = iceSheet;
                            cracksArray[numberOfCracks][1] = rowLocation;
                            cracksArray[numberOfCracks][2] = columnLocation;
                            numberOfCracks++;
                        }
                    }

                    // Check for a crack in the top-right corner (0, max column)
                    else if(rowLocation == 0 && columnLocation == maximumColumnIndex) {

                        int bottom = dataArray[iceSheet][rowLocation + 1][columnLocation];
                        int left = dataArray[iceSheet][rowLocation][columnLocation - 1];
                        int bottomLeft = dataArray[iceSheet][rowLocation + 1][columnLocation - 1];

                        if(left % 10 == 0 || bottomLeft % 10 == 0 || bottom % 10 == 0){
                            cracksArray[numberOfCracks][0] = iceSheet;
                            cracksArray[numberOfCracks][1] = rowLocation;
                            cracksArray[numberOfCracks][2] = columnLocation;
                            numberOfCracks++;
                        }
                    }

                    // Check for a crack in the bottom-left corner (max row, 0)
                    else if(rowLocation == maximumRowIndex && columnLocation == 0){

                        int right = dataArray[iceSheet][rowLocation][columnLocation + 1];
                        int top = dataArray[iceSheet][rowLocation - 1][columnLocation];
                        int topRight = dataArray[iceSheet][rowLocation - 1][columnLocation + 1];

                        if(right % 10 == 0 || topRight % 10 == 0 || top % 10 == 0){
                            cracksArray[numberOfCracks][0] = iceSheet;
                            cracksArray[numberOfCracks][1] = rowLocation;
                            cracksArray[numberOfCracks][2] = columnLocation;
                            numberOfCracks++;
                        }
                    }

                    // Check for a crack in the bottom-right corner (max row, max column)
                    else if(rowLocation == maximumRowIndex && columnLocation == maximumColumnIndex) {

                        int top = dataArray[iceSheet][rowLocation - 1][columnLocation];
                        int topLeft = dataArray[iceSheet][rowLocation - 1][columnLocation - 1];
                        int left = dataArray[iceSheet][rowLocation][columnLocation - 1];

                        if(left % 10 == 0 || topLeft % 10 == 0 || top % 10 == 0){
                            cracksArray[numberOfCracks][0] = iceSheet;
                            cracksArray[numberOfCracks][1] = rowLocation;
                            cracksArray[numberOfCracks][2] = columnLocation;
                            numberOfCracks++;
                        }
                    }

                    // Check for a crack along the top edge (row 0, but not corners)
                    else if(rowLocation == 0) {

                        int left = dataArray[iceSheet][rowLocation][columnLocation - 1];
                        int right = dataArray[iceSheet][rowLocation][columnLocation + 1];
                        int bottom = dataArray[iceSheet][rowLocation + 1][columnLocation];
                        int bottomLeft = dataArray[iceSheet][rowLocation + 1][columnLocation - 1];
                        int bottomRight = dataArray[iceSheet][rowLocation + 1][columnLocation + 1];


                        if(left % 10 == 0 || right % 10 == 0 || bottom % 10 == 0 || bottomLeft % 10 == 0 || bottomRight % 10 == 0){
                            cracksArray[numberOfCracks][0] = iceSheet;
                            cracksArray[numberOfCracks][1] = rowLocation;
                            cracksArray[numberOfCracks][2] = columnLocation;
                            numberOfCracks++;
                        }
                    }

                    // Check for a crack along the bottom edge (max row, but not corners)
                    else if(rowLocation == maximumRowIndex) {

                        int left = dataArray[iceSheet][rowLocation][columnLocation - 1];
                        int right = dataArray[iceSheet][rowLocation][columnLocation + 1];
                        int top = dataArray[iceSheet][rowLocation - 1][columnLocation];
                        int topLeft = dataArray[iceSheet][rowLocation - 1][columnLocation - 1];
                        int topRight = dataArray[iceSheet][rowLocation - 1][columnLocation + 1];

                        if(left % 10 == 0 || right % 10 == 0 || top % 10 == 0 || topLeft % 10 == 0 || topRight % 10 == 0){
                            cracksArray[numberOfCracks][0] = iceSheet;
                            cracksArray[numberOfCracks][1] = rowLocation;
                            cracksArray[numberOfCracks][2] = columnLocation;
                            numberOfCracks++;
                        }
                    }

                    // Check for a crack along the left edge (column 0, but not corners)
                    else if(columnLocation == 0) {

                        int bottom = dataArray[iceSheet][rowLocation + 1][columnLocation];
                        int right = dataArray[iceSheet][rowLocation][columnLocation + 1];
                        int top = dataArray[iceSheet][rowLocation - 1][columnLocation];
                        int topRight = dataArray[iceSheet][rowLocation - 1][columnLocation + 1];
                        int bottomRight = dataArray[iceSheet][rowLocation + 1][columnLocation + 1];

                        if(bottom % 10 == 0 || right % 10 == 0 || top % 10 == 0 || bottomRight % 10 == 0 || topRight % 10 == 0){
                            cracksArray[numberOfCracks][0] = iceSheet;
                            cracksArray[numberOfCracks][1] = rowLocation;
                            cracksArray[numberOfCracks][2] = columnLocation;
                            numberOfCracks++;
                        }
                    }

                    // Check for a crack along the right edge (max column, but not corners)
                    else if(columnLocation == maximumColumnIndex) {

                        int bottom = dataArray[iceSheet][rowLocation + 1][columnLocation];
                        int top = dataArray[iceSheet][rowLocation - 1][columnLocation];
                        int left = dataArray[iceSheet][rowLocation][columnLocation - 1];
                        int topLeft = dataArray[iceSheet][rowLocation - 1][columnLocation - 1];
                        int bottomLeft = dataArray[iceSheet][rowLocation + 1][columnLocation - 1];

                        if(bottom % 10 == 0 || left % 10 == 0 || top % 10 == 0 || bottomLeft % 10 == 0 || topLeft % 10 == 0){
                            cracksArray[numberOfCracks][0] = iceSheet;
                            cracksArray[numberOfCracks][1] = rowLocation;
                            cracksArray[numberOfCracks][2] = columnLocation;
                            numberOfCracks++;
                        }
                    }

                    // Check for cracks in the middle area
                    else {

                        int top = dataArray[iceSheet][rowLocation - 1][columnLocation];
                        int bottom = dataArray[iceSheet][rowLocation + 1][columnLocation];
                        int left = dataArray[iceSheet][rowLocation][columnLocation - 1];
                        int topLeft = dataArray[iceSheet][rowLocation - 1][columnLocation - 1];
                        int bottomLeft = dataArray[iceSheet][rowLocation + 1][columnLocation - 1];
                        int right = dataArray[iceSheet][rowLocation][columnLocation + 1];
                        int topRight = dataArray[iceSheet][rowLocation - 1][columnLocation + 1];
                        int bottomRight = dataArray[iceSheet][rowLocation + 1][columnLocation + 1];

                        if(bottom % 10 == 0 || left % 10 == 0 || top % 10 == 0 || bottomLeft % 10 == 0 || topLeft % 10 == 0 || right % 10 == 0 || bottomRight % 10 == 0 || topRight % 10 == 0){
                            cracksArray[numberOfCracks][0] = iceSheet;
                            cracksArray[numberOfCracks][1] = rowLocation;
                            cracksArray[numberOfCracks][2] = columnLocation;
                            numberOfCracks++;
                        }
                    }
                }
            }
        }
        // Resize the cracksArray to fit the actual number of cracks found
        cracksArray = Arrays.copyOf(cracksArray, numberOfCracks);
        return cracksArray;
    }
}