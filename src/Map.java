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
        int iteratedThroughValidPositions = -1;
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

    public boolean movePlayer(Player player, String direction) {
        int[] activePlayerPosition=getActivePlayerPosition(player);
        boolean successfullMove = true;

        if(checkIfDirectionClear(map,direction, activePlayerPosition, 1)){
            switch (direction){
                case "MOVE N":
                    activePlayerPosition[0]--;
                    break;
                case "MOVE E":
                    activePlayerPosition[1]++;
                    break;
                case "MOVE S":
                    activePlayerPosition[0]++;
                    break;
                case "MOVE W":
                    activePlayerPosition[1]--;
                    break;
            }
            System.out.println("Success");
            this.setPlayerPosition(activePlayerPosition, player);
            return true;

        }

        else{
            System.out.println("Fail");
            return false;
        }
    }

    public boolean attemptQuit(Player player) {
        int[] activePlayerPosition = getActivePlayerPosition(player);
        //Checks if player current position is an exit and gold requirement reached
        return this.map[activePlayerPosition[0]][activePlayerPosition[1]] == 'E' && player.getGoldCollected() >= this.getGoldRequiredToWin();
    }

    public char[][] updateExploredMap(char[][] exploredMap, char[][] mapUncoveredByLook) {

        exploredMap[botPlayerPosition[0]][botPlayerPosition[1]]=map[botPlayerPosition[0]][botPlayerPosition[1]];

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
        if(humanPlayerPosition[0]==botPlayerPosition[0]&&humanPlayerPosition[1]==botPlayerPosition[1]){
            System.out.println("LOSE");
            System.out.println("You were caught by the bot!");
            System.exit(0);
        }
    }

    //Look in each of the 4 directions surrounding the bot and returns the best depending on some rules
    public String choseNewDirection(char[][] map, String currentDirection, ArrayList<int[]> previouslyVisitedPositions) {

        //If bot is near a wall in a direction, bot checks 2 adjacent positions in direction clockwise from the direction of the wall
        //If they are both free, then returns the clear direction
        /*For example:
        #....
        #....
        #.B..
        #....
        #....
        Bot is near wall in west, so checks if 2 positions north are free, which they are, so starting direction is north in order to take best guess at moving clockwise around map
         */
        //Checks if a wall is in column 1 place or 2 places west of bot, and if 2 positions north of bot are also clear
        //Also makes sure not to return the opposite direction to what the direction was before
        //Also makes sure to not chose a direction if the adjacent position in that direction has already been visited

        //Each index represents if the adjacent position in each direction has been visited or not, in order {north, east, south, west}
        boolean[] unvisitedDirections = {true,true,true,true};

        for (int[] position:previouslyVisitedPositions){
            if(position[0]==botPlayerPosition[0]-1&&position[1]==botPlayerPosition[1]){
                unvisitedDirections[0]=false;
            }
            else if(position[0]==botPlayerPosition[0]&&position[1]==botPlayerPosition[1]+1){
                unvisitedDirections[1]=false;
            }
            else if(position[0]==botPlayerPosition[0]+1&&position[1]==botPlayerPosition[1]){
                unvisitedDirections[2]=false;
            }
            else if(position[0]==botPlayerPosition[0]&&position[1]==botPlayerPosition[1]-1){
                unvisitedDirections[3]=false;
            }
        }
        System.out.println(unvisitedDirections[0]+" "+unvisitedDirections[1]+" "+unvisitedDirections[2]+" "+unvisitedDirections[3]);

        if(unvisitedDirections[0]&&currentDirection!="MOVE S"&&(checkIfSameTile(getColumn(map, new int[]{2,2}, 1),'#')||checkIfSameTile(getColumn(map, new int[]{2,2}, 0),'#')) && map[1][2]!='#' && map[0][2]!='#'){
            return "MOVE N";
        }
        //Does same as before for all other direction
        else if(unvisitedDirections[1]&&currentDirection!="MOVE W"&&(checkIfSameTile(getRow(map, new int[]{2,2}, 1),'#')||checkIfSameTile(getColumn(map, new int[]{2,2}, 0),'#')) && map[2][3]!='#' && map[2][4]!='#'){
            return "MOVE E";
        }
        else if(unvisitedDirections[2]&&currentDirection!="MOVE N"&&(checkIfSameTile(getColumn(map, new int[]{2,2}, 3),'#')||checkIfSameTile(getColumn(map, new int[]{2,2}, 4),'#')) && map[3][2]!='#' && map[4][2]!='#'){
            return "MOVE S";
        }
        else if(unvisitedDirections[3]&&currentDirection!="MOVE E"&&(checkIfSameTile(getRow(map, new int[]{2,2}, 3),'#')||checkIfSameTile(getColumn(map, new int[]{2,2}, 4),'#')) && map[2][1]!='#' && map[2][0]!='#'){
            return "MOVE W";
        }

        //Otherwise if bot is not near a wall, then will have to randomly pick an option out of best options
        ArrayList<String> directionOptions = new ArrayList<>();

        //Checks the 2 positions in each direction from bot within the 5x5 area, if clear adds to direction options
        if(unvisitedDirections[0]&&currentDirection!="MOVE S"&&map[1][2]!='#' && map[0][2]!='#'){
            directionOptions.add("MOVE N");
        }
        if(unvisitedDirections[1]&&currentDirection!="MOVE W"&&map[2][3]!='#' && map[2][4]!='#'){
            directionOptions.add("MOVE E");
        }
        if(unvisitedDirections[2]&&currentDirection!="MOVE N"&&map[3][2]!='#' && map[4][2]!='#'){
            directionOptions.add("MOVE S");
        }
        if(unvisitedDirections[3]&&currentDirection!="MOVE E"&&map[2][1]!='#' && map[2][0]!='#'){
            directionOptions.add("MOVE W");
        }

        //If at least 1 of the 4 directions is clear for 2 positions, then pick one of them randomly
        if(directionOptions.size()>0) {
            Random rand = new Random();
            return directionOptions.get(rand.nextInt(directionOptions.size()));
        }

        //If there are no directions with 2 positions clear, then repeats each direction check but only for 1 square
        else{
            //Checks the directly adjacent position in each direction from bot within the 5x5 area, if clear adds to direction options
            if(unvisitedDirections[0]&&currentDirection!="MOVE S"&&map[1][2]!='#'){
                directionOptions.add("MOVE N");
            }
            if(unvisitedDirections[1]&&currentDirection!="MOVE W"&&map[2][3]!='#'){
                directionOptions.add("MOVE E");
            }
            if(unvisitedDirections[2]&&currentDirection!="MOVE N"&&map[3][2]!='#'){
                directionOptions.add("MOVE S");
            }
            if(unvisitedDirections[3]&&currentDirection!="MOVE E"&&map[2][1]!='#'){
                directionOptions.add("MOVE W");
            }
            //If some positions are clear, chose 1 randomly and return it
            if(directionOptions.size()>0) {
                Random rand = new Random();
                return directionOptions.get(rand.nextInt(directionOptions.size()));
            }

        }

        //If no directions with clear space adjacent to player, then bot must be at a dead end, and needs to go back in the direction it came from
        return "STUCK";
    }

    public boolean checkIfTowardsWall(String movementDirection) {
        char[] column;
        //checks the row or column at the edge of the 5x5 map around player, and if they are all wall tiles in the chosen direction, returns true
        switch(movementDirection){
            case "MOVE N":
                if(this.checkIfSameTile(map[botPlayerPosition[0]-2],'#')){
                    return true;
                }
                break;
            case "MOVE E":
                column = this.getColumn(map, botPlayerPosition, botPlayerPosition[1]+2);
                if(this.checkIfSameTile(column,'#')){
                    return true;
                }
                break;
            case "MOVE S":
                if(this.checkIfSameTile(map[botPlayerPosition[0]+2],'#')){
                    return true;
                }
                break;
            case "MOVE W":
                column = this.getColumn(map, botPlayerPosition, botPlayerPosition[1]-2);
                if(this.checkIfSameTile(column,'#')){
                    return true;
                }
                break;
        }
        return false;
    }

    //checks the row or column just outside of the 5x5 map around player, and it hasn't yet been fully explored, returns false
    public boolean checkIfTowardsExplored(String movementDirection, char[][] exploredMap) {
        //If bot has already decided it's at a wall, return true to avoid index out of bounds error at edge of map
        if(checkIfTowardsWall(movementDirection))return true;
        char[] column;
        char[] row;
        //Runs the checkIfWall method on the corresponding direction, if direction is E or W then gets the column in respective direction and passes that to checkIfWall
        switch(movementDirection){
            case "MOVE N":
                row = this.getRow(exploredMap, botPlayerPosition, botPlayerPosition[0]-2);
                if(this.checkIfContainTile(row,'?')){
                    return false;
                }
                break;
            case "MOVE E":
                column = this.getColumn(exploredMap, botPlayerPosition, botPlayerPosition[1]+2);
                if(this.checkIfContainTile(column,'?')){
                    return false;
                }
                break;
            case "MOVE S":
                row = this.getRow(exploredMap, botPlayerPosition, botPlayerPosition[0]+2);
                if(this.checkIfContainTile(row,'?')){
                    return false;
                }
                break;
            case "MOVE W":
                column = this.getColumn(exploredMap, botPlayerPosition, botPlayerPosition[1]-2);
                if(this.checkIfContainTile(column,'?')){
                    return false;
                }
                break;
        }
        return true;
    }

    public boolean checkIfDirectionClear(char[][] map, String direction, int[]position,int depth){
        if(direction==null)return false;
        switch(direction){
            case "MOVE N":
                for(int i=1;i<=depth;i++){
                    if(map[position[0]-i][position[1]]=='#') return false;
                }
                break;
            case "MOVE E":
                for(int i=1;i<=depth;i++){
                    if(map[position[0]][position[1]+i]=='#') return false;
                }
                break;
            case "MOVE S":
                for(int i=1;i<=depth;i++){
                    if(map[position[0]+i][position[1]]=='#') return false;
                }
                break;
            case "MOVE W":
                for(int i=1;i<=depth;i++){
                    if(map[position[0]][position[1]-i]=='#') return false;
                }
                break;
        }
        return true;
    }

    //Checks through every element in an array and returns true if they are all equal to a '#' wall element
    private boolean checkIfSameTile(char[] map, char tile){
        for (char element:map) {
            if(element!=tile){
                return false;
            }
        }
        return true;
    }

    private boolean checkIfContainTile(char[] map, char tile){
        for (char element:map) {
            if(element==tile){
                return true;
            }
        }
        return false;
    }

    private char[] getColumn(char[][] map, int[] playerPosition, int columnIndex){
        char[] column = new char[5];
        for(int i=0; i<5; i++){
            column[i] = map[playerPosition[0]-2+i][columnIndex];
        }
        return column;
    }

    private char[] getRow(char[][] map, int[] playerPosition, int rowIndex){
        char[] row = new char[5];
        for(int i=0; i<5; i++){
            row[i] = map[rowIndex][playerPosition[1]-2+i];
        }
        return row;
    }

    boolean checkIfAdjacentToWall(char[][] map, String movementDirection){
        switch(movementDirection){
            case "MOVE N":
                if(map[botPlayerPosition[0]-1][botPlayerPosition[1]]=='#'){
                    return true;
                }
                break;
            case "MOVE E":
                if(map[botPlayerPosition[0]][botPlayerPosition[1]+1]=='#'){
                    return true;
                }
                break;
            case "MOVE S":
                if(map[botPlayerPosition[0]+1][botPlayerPosition[1]]=='#'){
                    return true;
                }
                break;
            case "MOVE W":
                if(map[botPlayerPosition[0]][botPlayerPosition[1]-1]=='#'){
                    return true;
                }
                break;
        }
        return false;
    }


    public void removeBotFromMap(char[][] exploredMap) {
        for (int row = 0; row < verticalMapDimension; row++) {
            for (int elementPos = 0; elementPos < horizontalMapDimension; elementPos++) {

                //Increase iteratedThroughValidPositions if element is path(.) or exit(E)
                if (exploredMap[row][elementPos] == 'B') {
                    exploredMap[row][elementPos] = '.';
                }
            }
        }
    }
}
