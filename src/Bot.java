import java.util.Arrays;

public class Bot extends Player {

    private char[][] currentVisibility;
    private char[][] unhiddenMap;
    private int[] mapDimensions;

    public Bot(Map mapObject) {
        super(mapObject);
        createHiddenMap(mapObject);
    }

    private void createHiddenMap(Map mapObject){
        mapDimensions = mapObject.getMapDimensions();
        unhiddenMap = new char[mapDimensions[0]][mapDimensions[1]];
        for (char[] row: unhiddenMap)
            Arrays.fill(row, '?');
    }

    public void takeTurn(Map mapObject, HumanPlayer humanPlayer){
        if(this.steps==0&&5==6){
            look(mapObject, humanPlayer);
            steps++;
            return;
        }
        System.out.println("Bot Look:");
        this.look(mapObject, humanPlayer);


        steps++;
    }

    @Override
    public void look(Map mapObject, HumanPlayer humanPlayer) {
        char[][] mapUncoveredByLook = mapObject.createMapAroundPlayer(this);

        for (int row = 0; row < 5; row++) {
            for (int elementPos = 0; elementPos <5; elementPos++) {

                //Iterates through each position in the 5x5 area surrounding bot and adds to the total uncovered map
                unhiddenMap[this.playerPosition[0] - 2+row][this.playerPosition[1] - 2+elementPos] = mapUncoveredByLook[row][elementPos];
            }
        }
        mapObject.printAroundPlayer(this);

    }
}

/*



 */
