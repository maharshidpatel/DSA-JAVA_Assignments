import battleship.BattleShip3;

import java.util.Arrays;

/**
 * Starting code for COMP10205 - Assignment#5 - Version 3 of BattleShip
 * @author mark.yendt@mohawkcollege.ca (November 2024)
 */

public class A5 {
    public static void main(String[] args) {

        // DO NOT add any logic to this code
        // All logic must be added to your Bot implementation
        // see fireShot in the ExampleBot class

        final int NUMBEROFGAMES = 10000;
        System.out.println(BattleShip3.getVersion());
        BattleShip3 battleShip = new BattleShip3(NUMBEROFGAMES, new PatelBot());
        int [] gameResults = battleShip.run();

        // You may add some analysis code to look at all the game scores that are returned in gameResults
        // This can be useful for debugging purposes.

        battleShip.reportResults();
    }
}
