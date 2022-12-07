import java.util.ArrayList;

public class Bot extends Player {

    private char[][] exploredMap;
    private int goldRequiredToWin;

    private AStarSearch search;

    private ArrayList<String> commandsInQueue;

    public Bot(Map mapObject) {
        super(mapObject);
        exploredMap = mapObject.createHiddenMap();
        search = new AStarSearch();
        commandsInQueue = new ArrayList<>();
        System.out.println(commandsInQueue.isEmpty());
    }

    public void takeTurn(Map mapObject) {

        System.out.println(commandsInQueue.isEmpty());

        System.out.println("Bots turn:");

        /*
        Logic behind bot:

        Look for player, if player, find moves to player with A* and move to player, then look, repeat
        If no player, look for gold, find moves to gold and collect
        If no gold or player, continue exploring map until fully explored
        If map fully explored, move between exits until finds player

        */

        //If there is a command in the queue, do it
        if (!commandsInQueue.isEmpty()) {
            System.out.println("commands are in queue");
            String command = commandsInQueue.get(commandsInQueue.size()-1);
            commandsInQueue.remove(commandsInQueue.size()-1);
            switch (command) {
                case "HELLO":
                    goldRequiredToWin = mapObject.getGoldRequiredToWin();
                    break;
                case "PICKUP":
                    this.pickup(mapObject);
                    break;
                case "MOVE N":
                case "MOVE E":
                case "MOVE S":
                case "MOVE W":
                    //Direction of move comes from last character in command
                    this.move(mapObject, command.charAt(command.length() - 1));
                    break;
                case "LOOK":
                    this.look(mapObject);
                    break;
                case "QUIT":
                    mapObject.attemptQuit(this);
                    break;
            }
        }

        //Otherwise work out what to add to queue
        else {
            if (lookForItem('P') != null) {
                System.out.println("looking for player");
                commandsInQueue = search.GetPathTo(exploredMap, 'P');
            }
            else if (lookForItem('G') != null) {
                System.out.println("looking for gold");
                commandsInQueue = search.GetPathTo(exploredMap, 'G');
            }
            else{
                this.look(mapObject);
            }

            steps++;
        }
        mapObject.printMap(mapObject.map);
    }

    @Override
    public void look (Map mapObject){
        exploredMap = mapObject.updateExploredMap(exploredMap, mapObject.createMapAroundPlayer(this));
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

}

