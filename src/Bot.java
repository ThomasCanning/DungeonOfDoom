import java.util.LinkedList;
import java.util.Queue;

public class Bot extends Player{

    private char[][] exploredMap;
    private int goldRequiredToWin;

    private Queue<String> commandsInQueue = new LinkedList<>();

    public Bot(Map mapObject) {
        super(mapObject);
        exploredMap = mapObject.createHiddenMap();
    }

    public void takeTurn(Map mapObject){

        /*
        Logic behind bot:

        Look for player, if player, find moves to player with A* and move to player, then look, repeat
        If no player, look for gold, find moves to gold and collect
        If no gold or player, continue exploring map until fully explored
        If map fully explored, move between exits until finds player

        */

        //If there is a command in the queue, do it
        if(!commandsInQueue.isEmpty()){
            String command = commandsInQueue.remove();
            switch (command){
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
                    this.move(mapObject, command.charAt(command.length()-1));
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
        else{

        }

        steps++;
    }

    @Override
    public void look(Map mapObject) {
        exploredMap = mapObject.updateExploredMap(exploredMap, mapObject.createMapAroundPlayer(this));
    }

    private int[] lookForInMap(char )

}

