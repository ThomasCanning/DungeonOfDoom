import java.util.Scanner;

public class GameLogic {

    public static void main(String[] args) {

        //Scanner object used throughout for user input
        Scanner scanner = new Scanner(System.in);

        System.out.println("Welcome to Dungeon of Doom");

        newGame(scanner);
    }

    //Code to create map and player objects then process commands enclosed in static method so a new game can be started after each game finishes
    public static void newGame(Scanner scanner){
        Map mapObject = new Map();

        //Instantiates human player, passing in a starting position on the map which is
        //randomly generated using a method called on mapObject class
        HumanPlayer humanPlayer = new HumanPlayer(mapObject);

        Bot bot = new Bot(mapObject);

        //Starts recursive loop of getting and processing user commands
        processCommand(scanner, mapObject, humanPlayer, bot);
    }

    //Function that takes in player input, player object and map, and processes user input according to rules
    public static void processCommand(Scanner scanner, Map mapObject, HumanPlayer humanPlayer, Bot bot){
        String command = scanner.nextLine().toUpperCase();
        switch (command) {
            case "HELLO":
                System.out.println("Gold to win: "+humanPlayer.hello(mapObject));
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
                humanPlayer.look(mapObject);
                break;
            case "QUIT":
                humanPlayer.quit(mapObject, scanner);
                break;

            default:
                System.out.println("Fail");
        }
        humanPlayer.incrementSteps();

        bot.takeTurn(mapObject);

        //Continuously processes player input through recursion
        processCommand(scanner, mapObject, humanPlayer, bot);
    }
}