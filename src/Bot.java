import java.util.ArrayList;

public class Bot extends Player {

    private char[][] exploredMap;
    private int[] botPosition;
    private int goldRequiredToWin;

    private AStarSearch search;

    private ArrayList<String> commandsInQueue;

    private boolean readyToExit = false;

    private int movesSinceLastLook = 0;
    private String directionOfCurrentMovement;

    public Bot(Map mapObject) {
        super(mapObject);
        exploredMap = mapObject.createHiddenMap();
        this.botPosition = mapObject.getBotPlayerPosition();
        this.search = new AStarSearch();
        this.directionOfCurrentMovement = mapObject.getRandomClearDirection(this);
        commandsInQueue = new ArrayList<>();
        //The bot should always do look as it's first move
        commandsInQueue.add("LOOK");
        goldRequiredToWin = -1;
    }

    public void takeTurn(Map mapObject) {

        System.out.println("Bots turn:");

        /*
        Logic behind bot:

        Look for player, if player, find moves to player with A* and move to player, then look, repeat
        If no player, look for gold, find moves to gold and collect
        If gold required to win reached, head to exit and quit
        If no gold or player, continue exploring map

        Explore map by picking a direction and look after moving 5 places in a straight line, or move fails
        if move fails, bot reaches what is likely a wall(5#s), or bot reaches where at least 2 rows have already been explored, change direction
        when changing direction, continually go clockwise, or anticlockwise, randomised at start
        chose initial direction by randomly choosing out of directions that are not blocked in the 5x5 by a wall


        */

        //If there is nothing in the queue of commands, work out what to add to queue
        if (commandsInQueue.isEmpty()) {
            if (lookForItem('P') != null) {
                System.out.println("looking for player");
                commandsInQueue = search.getPathTo(exploredMap, 'P');
                commandsInQueue.add("LOOK");
            }
            else if (lookForItem('G') != null) {
                System.out.println("looking for gold");
                commandsInQueue = search.getPathTo(exploredMap, 'G');
                commandsInQueue.add("PICKUP");
                commandsInQueue.add("LOOK");

                //If bot doesn't already know how much gold is required to win, find out after first gold collected
                if(goldRequiredToWin!=-1){
                    commandsInQueue.add("HELLO");
                }

                else if(getGoldCollected()>=goldRequiredToWin){
                    //If bot has collected enough gold, bot knows to quit at next oppourtunity
                    readyToExit = true;
                    //If bot already knows where an exit is, then moves to it and quits
                    if(lookForItem('E')!=null){
                        System.out.println("Moving to exit");
                        commandsInQueue = search.getPathTo(exploredMap, 'E');
                        commandsInQueue.add("QUIT");
                    }
                }
            }

            else if(movesSinceLastLook>=5){
                commandsInQueue.add("LOOK");
            }

            //If nothing else, continue moving in the current direction of movement
            else{
                commandsInQueue.add(directionOfCurrentMovement);
            }

            steps++;
            }

        //Once there is something in the queue, execute the next command in queue
        System.out.println("commands are in queue");
        for(String command:commandsInQueue){
            System.out.println(command);
        }

        String command = commandsInQueue.get(0);
        commandsInQueue.remove(0);
        switch (command) {
            case "HELLO":
                goldRequiredToWin = mapObject.getGoldRequiredToWin();
                break;
            case "PICKUP":
                System.out.println("bot pickup");
                this.pickup(mapObject);
                break;
            case "MOVE N":
            case "MOVE E":
            case "MOVE S":
            case "MOVE W":
                //Direction of move comes from last character in command
                System.out.println("bot move");
                //If bot cant move because of wall, clear the queue of commands and make next move a look
                if(!this.move(mapObject, command.charAt(command.length() - 1))){
                    commandsInQueue.clear();
                    commandsInQueue.add("LOOK");
                }
                movesSinceLastLook++;
                break;
            case "LOOK":
                this.look(mapObject);
                movesSinceLastLook=0;
                break;
            case "QUIT":
                mapObject.attemptQuit(this);
                break;
        }
        mapObject.printMap(this.exploredMap);
        mapObject.checkIfPlayerCaught();
    }

    @Override
    public void look (Map mapObject){
        exploredMap = mapObject.updateExploredMap(exploredMap, mapObject.createMapAroundPlayer(this));
        //If bot is approaching wall or somewhere it has already explored, then change direction
        if(mapObject.checkIfTowardsWall(directionOfCurrentMovement)||mapObject.checkIfTowardsExplored(directionOfCurrentMovement, exploredMap)){
            changeDirection();
        }
    }

    private int[] lookForItem (char item){
        for (int row = 0; row < exploredMap.length; row++) {
            for (int elementPosition = 0; elementPosition < exploredMap[0].length; elementPosition++) {
                if (exploredMap[row][elementPosition] == item) {
                    return new int[]{row, elementPosition};
                }
            }
        }
        return null;
    }

    private void changeDirection() {
        //Changes directionOfCurrentMovement to next clockwise direction
        switch (directionOfCurrentMovement) {
            case "MOVE N":
                directionOfCurrentMovement = "MOVE E";
                break;
            case "MOVE E":
                directionOfCurrentMovement = "MOVE S";
                break;
            case "MOVE S":
                directionOfCurrentMovement = "MOVE W";
                break;
            case "MOVE W":
                directionOfCurrentMovement = "MOVE N";
                break;
        }
    }

}

