import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        int[][][] iceDataArray = textDataToArray("./ICESHEETS_F24.TXT");
        assert iceDataArray != null;
        int[][][] weakSpotsResult = weakSpots(iceDataArray);
        int[][] crackResult = cracks(iceDataArray, weakSpotsResult);

        System.out.println("PART A:");
        for (int i = 0; i < weakSpotsResult[3][0].length; i++){
            System.out.printf("Sheet %d has %d weak spots\n", i, weakSpotsResult[3][0][i]);
        }
        System.out.printf("\nTotal weak Spots on all Sheets = %d\n", weakSpotsResult[0][0][0]);
        System.out.printf("Sheet %d has the highest number of weak Spots = %d\n", weakSpotsResult[2][0][0], weakSpotsResult[1][0][0]);

        System.out.println("\nPART B:");
        for (int i = 0; i < crackResult.length; i++){
            System.out.printf("CRACK DETECTED @ [Sheet[%d](%d,%d)]\n", crackResult[i][0], crackResult[i][1], crackResult[i][2]);
        }

        System.out.println("\nSUMMARY");
        System.out.printf("The total number of weak spots that have cracked = %d\n", crackResult.length);
        System.out.printf("The fraction of weak spots that are also cracks is %.3f", (double)crackResult.length/weakSpotsResult[0][0][0]);
    }

    public static int[][][] textDataToArray(String textPath){
        try {
            File textFile = new File(textPath);
            Scanner reader = new Scanner(textFile);

            int iceSheets = reader.nextInt();
            int[][][] iceDataArray = new int[iceSheets][][];
            for (int iceSheet = 0; iceSheet < iceSheets; iceSheet++){
                int rows = reader.nextInt();
                int columns = reader.nextInt();
                iceDataArray[iceSheet] = new int[rows][columns];
                for (int row = 0; row < rows; row++){
                    for(int column = 0; column < columns; column++){
                        iceDataArray[iceSheet][row][column] = reader.nextInt();
                    }
                }
            }
            return iceDataArray;
        }
        catch (FileNotFoundException exception) {
            System.out.println(exception.getMessage());
        }
        return null;
    }

    public static int[][][] weakSpots(int[][][] dataArray){

        int totalWeakSpots = 0;
        int currentSheetTotalWeakSpots = 0;
        int maximumTotalWeakSpotsOnSheet = 0;
        int maximumTotalWeakSpotsSheetNumber = 0;
        int[] maximumTotalWeakSpotsOnSheetArray = new int[dataArray.length];
        int[][] weakSpotsLocationsArray = new int[dataArray.length][];

        for(int iceSheet = 0; iceSheet < dataArray.length; iceSheet++){

            weakSpotsLocationsArray[iceSheet] = new int[((dataArray[iceSheet].length) * (dataArray[iceSheet][0].length) * 2 ) + 2];
            weakSpotsLocationsArray[iceSheet][0] = dataArray[iceSheet].length;
            weakSpotsLocationsArray[iceSheet][1] = dataArray[iceSheet][0].length;

            int weakSpotsLocationsArrayIndex = 1;
            for(int row = 0; row < dataArray[iceSheet].length; row++){
                for(int column = 0; column < dataArray[iceSheet][row].length; column++){

                    if ((dataArray[iceSheet][row][column] <= 200) && (dataArray[iceSheet][row][column] % 50 == 0)){
                        totalWeakSpots++;
                        currentSheetTotalWeakSpots++;
                        weakSpotsLocationsArrayIndex++;
                        weakSpotsLocationsArray[iceSheet][weakSpotsLocationsArrayIndex] = row;
                        weakSpotsLocationsArrayIndex++;
                        weakSpotsLocationsArray[iceSheet][weakSpotsLocationsArrayIndex] = column;
                    }
                }

            }

            weakSpotsLocationsArray[iceSheet] = Arrays.copyOf(weakSpotsLocationsArray[iceSheet], weakSpotsLocationsArrayIndex + 1);

            if (currentSheetTotalWeakSpots > maximumTotalWeakSpotsOnSheet){
                maximumTotalWeakSpotsOnSheet = currentSheetTotalWeakSpots;
                maximumTotalWeakSpotsSheetNumber = iceSheet;
            }
            maximumTotalWeakSpotsOnSheetArray[iceSheet] = currentSheetTotalWeakSpots;
            currentSheetTotalWeakSpots = 0;
        }
//        System.out.println(Arrays.deepToString(weakSpotsLocationsArray));

        return new int[][][]{
                {new int[]{totalWeakSpots}},
                {new int[]{maximumTotalWeakSpotsOnSheet}},
                {new int[]{maximumTotalWeakSpotsSheetNumber}},
                {maximumTotalWeakSpotsOnSheetArray},
                weakSpotsLocationsArray
        };
    }

    public static int[][] cracks(int[][][] dataArray, int[][][] weakSpotsArray){
        int numberOfCracks = 0;
        int[][] cracksArray = new int[weakSpotsArray[0][0][0]][3];
        int[][] locationCoordinates = weakSpotsArray[4];
        for (int iceSheet = 0; iceSheet < locationCoordinates.length; iceSheet++){

            int maximumRowIndex = (locationCoordinates[iceSheet][0] - 1);
            int maximumColumnIndex = (locationCoordinates[iceSheet][1] - 1);

            if(locationCoordinates[iceSheet].length > 2 && maximumRowIndex > 0 && maximumColumnIndex > 0){
                for (int i = 2; i < locationCoordinates[iceSheet].length; i += 2){

                    int rowLocation = locationCoordinates[iceSheet][i];
                    int columnLocation = locationCoordinates[iceSheet][i+1];

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
        cracksArray = Arrays.copyOf(cracksArray, numberOfCracks);
        return cracksArray;
    }
}