import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class Map {

    private final File[] listOfFiles;

    public char[][] map;

    private int verticalMapDimension;
    private int horizontalMapDimension;
    private int possibleStartingPlaces=0;
    private int goldRequiredToWin;

    private int[] humanPlayerPosition;
    private int[] botPlayerPosition;

    //Map constructor
    public Map() {
        /*Creates an array of all the files listed in the res folder,
        so they can be printed out and the player can choose a map*/
        File folder = new File("res");
        listOfFiles = folder.listFiles();

        printMapChoices();
    }

    public void setPlayerPosition(int[] newPosition, Player player){
        if(player.getClass()==Bot.class){
            this.botPlayerPosition=newPosition;
        }
        else if(player.getClass()==HumanPlayer.class){
            this.humanPlayerPosition=newPosition;
        }
    }

    //Prints all map choices in the
    private void printMapChoices() {
        //For each prints out all the choices of maps contained within res folder
        for (File listOfFile : listOfFiles) {
            if (listOfFile.isFile()) {
                System.out.println(listOfFile.getName());
            } else if (listOfFile.isDirectory()) {
                System.out.println(listOfFile.getName() + " is not a valid file");
            }
        }
    }

    public int[] getMapDimensions(){
        return new int[]{verticalMapDimension, horizontalMapDimension};
    }

    public int getGoldRequiredToWin(){
        return goldRequiredToWin;
    }

   //Gets user input to chose map text file, and converts it to a 2D char array
    public void chooseMap() {

        System.out.println("Enter a map name:");

        Scanner scanner = new Scanner(System.in);
        String chosenMap = scanner.nextLine();

        //Creates file object which is then parsed by scanner
        File mapFile = new File("res/" + chosenMap);

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
                //If the number of valid positions looked at == the randomly generated valid position, then chose that position in array as starting position
                if(iteratedThroughValidPositions==randomValidPosition){
                    setPlayerPosition(new int[]{row,elementPos},player);
                }
                //Only increase iteratedThroughValidPositions if element is path(.) or exit(E)
                else if(map[row][elementPos]=='.'||map[row][elementPos]=='E'){
                    iteratedThroughValidPositions++;
                }
            }
        }
    }


    //Function that prints entire map used for testing purposes
    public void printEntireMap() {
        for(int row=0; row<verticalMapDimension;row++){
            for (char element : map[row]) {
                System.out.print(element);
            }
            System.out.println();
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

    //Prints a 5x5 square surrounding the player
    public void printAroundPlayer(Player player){
        int[] activePlayerPosition=getActivePlayerPosition(player);
        for(int row=player.playerPosition[0]-2; row<=player.playerPosition[0]+2;row++){
            for (int elementPos=player.playerPosition[1]-2; elementPos<=player.playerPosition[1]+2;elementPos++) {
                if(row==player.playerPosition[0]&&elementPos==player.playerPosition[1]){
                    System.out.print('P');
                }
                /*
                else if(elementPos==botPosition[1]){
                    */
                else{
                    System.out.print(map[row][elementPos]);
                }
            }
            System.out.println();
        }
    }

    public char[][] createMapAroundPlayer(Player player){
        char[][] localMap = new char[5][5];
        System.out.println(player.playerPosition[0]+" "+player.playerPosition[1]);
        for(int row=0; row<5;row++){
            for (int elementPos=0; elementPos<5;elementPos++) {
                localMap[row][elementPos]=map[player.playerPosition[0]-2+row][player.playerPosition[1]-2+elementPos];
                if(player.getClass()==HumanPlayer.class){
                    localMap[2][2]='P';
                }
                else if(player.getClass()==Bot.class){
                    localMap[2][2]='B';
                }
            }
        }
        return localMap;
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

}
