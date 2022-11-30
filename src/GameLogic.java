import java.util.Scanner;

public class GameLogic {

    public static void main(String[] args) {

        //Scanner object used throughout for user input
        Scanner scanner = new Scanner(System.in);
        /*Creates an instance of the map object which has methods to: allow user to
        choose a map from a list of map files, stores map as 2d char array, print map,
        randomise player start position.*/
        Map mapObject = new Map();

        System.out.println("Welcome to Dungeon of Doom");


        /*Takes user input an assigns corresponding map from text file
        as a 2d array to the map attribute of the mapObject.*/
        mapObject.chooseMap();

        //Instantiates human player, passing in a starting position on the map which is
        //randomly generated using a method called on mapObject class
        HumanPlayer humanPlayer = new HumanPlayer(mapObject);

        Bot bot = new Bot(mapObject);

        //Starts recursive loop of getting and processing user commands
        //???mapObject.map[humanPlayer.playerPosition[0]][humanPlayer.playerPosition[1]] = 'E';
        processCommand(scanner, mapObject, humanPlayer, bot);

    }

    //Function that takes in player input, player object and map, and processes user input according to rules
    public static void processCommand(Scanner scanner, Map mapObject, HumanPlayer humanPlayer, Bot bot){
        String command = scanner.nextLine().toUpperCase();
        switch (command) {
            case "HELLO":
                humanPlayer.hello(mapObject);
                break;
            case "GOLD":
                humanPlayer.gold(humanPlayer);
                break;
            case "PICKUP":
                humanPlayer.pickup(mapObject);
                break;
            case "MOVE N":
            case "MOVE E":
            case "MOVE S":
            case "MOVE W":
                //Takes in map, humanPlayer, and a direction and moves players position accordingly
                humanPlayer.move(mapObject, Character.toUpperCase(command.charAt(command.length() - 1)));
                break;
            case "LOOK":
                humanPlayer.look(mapObject, humanPlayer);
                break;
            case "QUIT":
                //Checks if player current position is an exit and gold requirement reached
                if (mapObject.map[humanPlayer.playerPosition[0]][humanPlayer.playerPosition[1]] == 'E' && humanPlayer.getGoldCollected() >= mapObject.getGoldRequiredToWin()) {
                    System.out.println("WIN");
                    System.out.println("Well done, you have won in " + humanPlayer.getSteps()+" steps");
                    System.out.println("Play again? (Y/N)");

                    //Player given choice to play again
                    if(scanner.nextLine().equalsIgnoreCase("Y"))mapObject.chooseMap();
                    else System.exit(0);
                }
                else {
                    //If you attempt to quit without enough gold or in wrong spot, you lose and end game
                    System.out.println("LOSE");
                    System.exit(0);
                }
                break;

            default:
                System.out.println("Fail");
        }
        humanPlayer.incrementSteps();

        bot.takeTurn(mapObject, humanPlayer);

        //Continuously processes player input through recursion
        processCommand(scanner, mapObject, humanPlayer, bot);
    }
}