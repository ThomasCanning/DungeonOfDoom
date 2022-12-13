import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

public class Map {

    public char[][] map;

    private int verticalMapDimension;
    private int horizontalMapDimension;
    private int possibleStartingPlaces=0;
    private int goldRequiredToWin;

    private int[] humanPlayerPosition;
    private int[] botPlayerPosition;

    //Map constructor
    public Map() {
        chooseMap();
    }

    private void setPlayerPosition(int[] newPosition, Player player){
        if(player.getClass()==Bot.class){
            this.botPlayerPosition=newPosition;
        }
        else if(player.getClass()==HumanPlayer.class){
            this.humanPlayerPosition=newPosition;
        }
    }

    public int getGoldRequiredToWin(){
        return goldRequiredToWin;
    }

    public int[] getBotPlayerPosition() {
        return botPlayerPosition;
    }

   //Gets user input to chose map text file, and converts it to a 2D char array
    private void chooseMap() {

        //String used to add "maps/" to start of file name when file object created if a maps subdirectory exists
        String directory;

        //Checks if a maps subdirectory exists, and if it does prints of each map in that directory
        if(Files.exists(Path.of("maps"))){
            directory = "maps/";
            File folder = new File("maps");
            File[] listOfFiles = folder.listFiles();
            for (File listOfFile : listOfFiles) {
                System.out.println(listOfFile.getName());
            }
        }
        else{
            directory = "src/";
            File folder = new File("src");
            File[] listOfFiles = folder.listFiles();
            for (File listOfFile : listOfFiles) {
                if(listOfFile.getName().endsWith(".txt")&&!listOfFile.getName().equals("README.txt")){
                    System.out.println(listOfFile.getName());
                }
            }
        }

        System.out.println("Enter a map name:");

        Scanner scanner = new Scanner(System.in);
        String chosenMap = scanner.nextLine();

        //If player leaves .txt of end of map name, adds it for them
        if(!chosenMap.endsWith(".txt")){
            chosenMap = chosenMap+".txt";
        }

        //Creates file object which is then parsed by scanner
        File mapFile = new File(directory + chosenMap);

        //Array list that mapFile is added to for easier processing
        ArrayList<String> mapFileAsArray = new ArrayList<>();

        /*Counter used to work out vertical dimension of map,
        map is later surrounded by 1 layer of hashes to make sure
        player never sees out of map, so 2 would be added to vertical dimension,
        but the first 2 lines of the map file is extraneous information so left
        out of count, so starting at 0 is valid
        */
        verticalMapDimension = 0;
        //horizontalMapDimension starts at 2 to account for surrounding hashes later
        //added to map which add 1 either side to horizontal dimension
        horizontalMapDimension = 2;

        try {
            //For each line scanned through in file, increments vertical map dimension
            Scanner scanThroughFile = new Scanner(mapFile);
            while (scanThroughFile.hasNextLine()) {
                //Reads text file an assigns each line in text file to a String array with each line as an element in the array
                mapFileAsArray.add(scanThroughFile.nextLine());
                verticalMapDimension++;
            }
            //Horizontal map dimension is just length of any line in the map
            // added to staring value of 2, so line 3 is used
            horizontalMapDimension += mapFileAsArray.get(2).length();
            scanThroughFile.close();
        }


        /*If the map the player enters doesn't exist,
        reruns method to give player another chance to pick*/
        catch (FileNotFoundException e) {
            System.out.println("The map you have chosen does not exist");
            chooseMap();
            return;
        }



        //A 2d char array is used to represent map
        char[][] map = new char[verticalMapDimension][horizontalMapDimension];
        for (int row = 0; row < verticalMapDimension; row++) {
            for (int elementPos = 0; elementPos < horizontalMapDimension; elementPos++) {
                //The first and last row in the map is set to a row of hashes
                //by checking if at first or last row or column
                //If so, sets position in char array to a hash
                if(row==0||row==verticalMapDimension-1){
                    map[row][elementPos]='#';
                }
                else if(elementPos==0||elementPos==horizontalMapDimension-1){
                    map[row][elementPos]='#';
                }

                else {
                    //line 0 and line 1 in mapFileAsArray are not part of the map, but map[0][] is already a row of hashes
                    //so line 2 in mapFileAsArray should be assigned to map[1][], and line 3 to map[2][] etc hence .get(row+1)
                    //map[][0] is also a column of hashes, so element 0 in each line in mapFileAsArray should be assigned to map[][1]
                    //hence .charAt(elementPos-1)
                    map[row][elementPos] = mapFileAsArray.get(row + 1).charAt(elementPos-1);
                    if (map[row][elementPos] == '.' || map[row][elementPos] == 'E') {
                        //possibleStartingPlaces is incremented each element in the map which is valid as a starting point
                        this.possibleStartingPlaces++;
                    }
                }
            }
        }

        this.map=map;

        //2nd line in text file contains a string and a digit, digit part is taken and set as goldRequiredToWin
        this.goldRequiredToWin = Integer.parseInt(mapFileAsArray.get(1).replaceAll("\\D+",""));
    }

    //Randomly generates a starting position out of all valid starting positions in the map and returns it as an array
    public void calculatePlayerStartingPoint(Player player){
        Random rand = new Random();
        int randomValidPosition = rand.nextInt(this.possibleStartingPlaces);
        int iteratedThroughValidPositions = 0;
        for (int row = 0; row < verticalMapDimension; row++) {
            for (int elementPos = 0; elementPos < horizontalMapDimension; elementPos++) {

                //Increase iteratedThroughValidPositions if element is path(.) or exit(E)
                if(map[row][elementPos]=='.'||map[row][elementPos]=='E'){
                    iteratedThroughValidPositions++;
                }

                //If the number of valid positions looked at == the randomly generated valid position, then chose that position in array as starting position
                if(iteratedThroughValidPositions==randomValidPosition){
                    setPlayerPosition(new int[]{row,elementPos},player);

                    //Checks to make sure that human and bot are not starting in same position, if they are finds a new starting position for the player
                    if(humanPlayerPosition==botPlayerPosition){
                        System.out.println("error");
                        calculatePlayerStartingPoint(player);
                    }

                    System.out.println(row+" start pos "+elementPos);
                    return;
                }
            }
        }
    }

    public boolean attemptPickup(Player player){
        int[] activePlayerPosition=getActivePlayerPosition(player);

        if(this.map[activePlayerPosition[0]][activePlayerPosition[1]]=='G'){
            this.map[activePlayerPosition[0]][activePlayerPosition[1]]='.';
            return true;
        }
        return false;
    }

    public char[][] createMapAroundPlayer(Player player){

        //creates a new 5x5 array which will be returned as the map around player
        char[][] localMap = new char[5][5];

        int[] activePlayerPosition = this.getActivePlayerPosition(player);
        int[] currentCoordinate;
        for(int row=0; row<5;row++){
            for (int elementPos=0; elementPos<5;elementPos++) {

                //current coordinate is updated after each iteration, starts at top left of 5x5 and ends at bottom right
                currentCoordinate = new int[]{activePlayerPosition[0] - 2 + row, activePlayerPosition[1] - 2 + elementPos};

                localMap[row][elementPos]=map[currentCoordinate[0]][currentCoordinate[1]];
                if (Arrays.equals(currentCoordinate, humanPlayerPosition)) {
                    localMap[row][elementPos]='P';
                }
                if (Arrays.equals(currentCoordinate, botPlayerPosition)) {
                    localMap[row][elementPos]='B';
                }
            }
        }
        return localMap;
    }

    public char[][] createHiddenMap(){
        char[][] hiddenMap = new char[verticalMapDimension][horizontalMapDimension];
        for (char[] row: hiddenMap)
            Arrays.fill(row, '?');
        return hiddenMap;
    }

    private int[] getActivePlayerPosition(Player player){
        int[] activePlayerPosition=null;
        if(player.getClass()==HumanPlayer.class) {
            activePlayerPosition = humanPlayerPosition;
        }
        else if(player.getClass()==Bot.class){
            activePlayerPosition = botPlayerPosition;
        }
        return activePlayerPosition;
    }

    public boolean movePlayer(Player player, char direction) {
        int[] activePlayerPosition=getActivePlayerPosition(player);
        boolean successfullMove = true;
        switch(direction){
            case 'N':
                if(this.map[activePlayerPosition[0]-1][activePlayerPosition[1]]!='#'){
                    activePlayerPosition[0]--;
                    System.out.println("Success");
                }
                else{
                    System.out.println("Fail");
                    successfullMove = false;
                }
                break;

            case 'E':
                if(this.map[activePlayerPosition[0]][activePlayerPosition[1]+1]!='#'){
                    activePlayerPosition[1]++;
                    System.out.println("Success");
                }
                else{
                    System.out.println("Fail");
                    successfullMove = false;
                }
                break;

            case 'S':
                if(this.map[activePlayerPosition[0]+1][activePlayerPosition[1]]!='#'){
                    activePlayerPosition[0]++;
                    System.out.println("Success");
                }
                else{
                    System.out.println("Fail");
                    successfullMove = false;
                }
                break;

            case 'W':
                if(this.map[activePlayerPosition[0]][activePlayerPosition[1]-1]!='#'){
                    activePlayerPosition[1]--;
                    System.out.println("Success");
                }
                else{
                    System.out.println("Fail");
                    successfullMove = false;
                }
                break;

            default:
                System.out.println("Fail");
                successfullMove = false;

        }
        this.setPlayerPosition(activePlayerPosition, player);
        return successfullMove;
    }

    public boolean attemptQuit(Player player) {
        int[] activePlayerPosition = getActivePlayerPosition(player);
        //Checks if player current position is an exit and gold requirement reached
        return this.map[activePlayerPosition[0]][activePlayerPosition[1]] == 'E' && player.getGoldCollected() >= this.getGoldRequiredToWin();
    }

    public char[][] updateExploredMap(char[][] exploredMap, char[][] mapUncoveredByLook) {

        for (int row = 0; row < 5; row++) {
            //Iterates through each position in the 5x5 area surrounding bot and adds to the total uncovered map
            System.arraycopy(mapUncoveredByLook[row], 0, exploredMap[botPlayerPosition[0] - 2 + row], botPlayerPosition[1] - 2, 5);
        }
        return exploredMap;
    }

    public void printMap(char[][] map) {
        for(int row=0; row<map.length;row++){
            for(int elementPos = 0; elementPos<map[0].length;elementPos++){

                System.out.print(map[row][elementPos]);

            }
            System.out.println();
        }
    }

    public void checkIfPlayerCaught() {
        if(humanPlayerPosition==botPlayerPosition){
            System.out.println("LOSE");
            System.out.println("You were caught by the bot!");
            System.exit(0);
        }
    }

    //Look in each of the 4 directions surrounding the bot and adds the ones which aren't blocked by a wall to a list
    public ArrayList<String> getClearDirections(Player player) {
        ArrayList<String> directionOptions = new ArrayList<>();
        int[] activePlayerPosition = getActivePlayerPosition(player);
        //Checks both positions north of bot
        if(map[activePlayerPosition[0]-1][activePlayerPosition[1]]=='.' && map[activePlayerPosition[0]-2][activePlayerPosition[1]]=='.'){
            directionOptions.add("Move N");
        }
        if(map[activePlayerPosition[0]][activePlayerPosition[1]+1]=='.' && map[activePlayerPosition[0]][activePlayerPosition[1]+1]=='.'){
            directionOptions.add("Move E");
        }
        if(map[activePlayerPosition[0]+1][activePlayerPosition[1]]=='.' && map[activePlayerPosition[0]+2][activePlayerPosition[1]]=='.'){
            directionOptions.add("Move S");
        }
        if(map[activePlayerPosition[0]][activePlayerPosition[1]-1]=='.' && map[activePlayerPosition[0]][activePlayerPosition[1]-2]=='.'){
            directionOptions.add("Move W");
        }
        return directionOptions;
    }

    public boolean checkIfTowardsWall(Player player, String movementDirection) {
        int[] activePlayerPosition = getActivePlayerPosition(player);
        char[] column;
        //Runs the checkIfWall method on the corresponding direcion, if direction is E or W then gets the column in respective direction and passes that to checkIfWall
        switch(movementDirection){
            case "Move N":
                if(this.checkIfWall(map[activePlayerPosition[0]-2])){
                    return true;
                }
                break;
            case "Move E":
                column = this.getColumn(map, activePlayerPosition, activePlayerPosition[1]+2);
                if(this.checkIfWall(column)){
                    return true;
                }
                break;
            case "Move S":
                if(this.checkIfWall(map[activePlayerPosition[0]+2])){
                    return true;
                }
                break;
            case "Move W":
                column = this.getColumn(map, activePlayerPosition, activePlayerPosition[1]-2);
                if(this.checkIfWall(column)){
                    return true;
                }
                break;
        }
        return false;
    }
    //Checks through every element in an array and returns true if they are all equal to a '#' wall element
    private boolean checkIfWall(char[] map){
        boolean isWall = true;
        for (char element:map) {
            if(element!='#'){
                isWall=false;
                return isWall;
            }
        }
        return isWall;
    }

    private char[] getColumn(char[][] map, int[] playerPosition, int columnIndex){
        char[] column = new char[5]; // Here I assume a rectangular 2D array!
        for(int i=playerPosition[1]-2; i<=playerPosition[1]+2; i++){
            column[i] = map[i][columnIndex];
        }
        return column;
    }
}
