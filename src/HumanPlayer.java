import java.util.Scanner;

public class HumanPlayer extends Player {

    public HumanPlayer(Map mapObject) {
        super(mapObject);
    }

    public boolean quit(Map mapObject, Scanner scanner) {
        if (super.quit(mapObject, scanner)) {
            System.out.println("WIN");
            System.out.println("Well done, you have won in " + this.steps + " steps");
            System.out.println("Play again? (Y/N)");

            //Player given choice to play again
            if (scanner.nextLine().equalsIgnoreCase("Y"))GameLogic.newGame(scanner);
            else System.exit(0);
        } else {
            //If you attempt to quit without enough gold or in wrong spot, you lose and end game
            System.out.println("LOSE");
            System.exit(0);
        }
        return false;
    }

    //Overrides look method as human player method prints while bot look doesn't print
    @Override
    public void look(Map mapObject) {
        char[][] map = mapObject.createMapAroundPlayer(this);
        mapObject.printMap(map);
    }
}
