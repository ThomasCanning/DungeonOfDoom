public abstract class Player {
    private int goldCollected=0;

    protected int steps=0;
    public Player(Map mapObject){
        mapObject.calculatePlayerStartingPoint(this);
    }

    public int getGoldCollected(){
        return goldCollected;
    }
    public void hello(Map mapObject){
        System.out.println("Gold to win: "+ mapObject.getGoldRequiredToWin());
    }
    public void gold(Player player){
        System.out.println(player.goldCollected);
    }


    public void pickup(Map mapObject){

        if(mapObject.attemptPickup(this)){
            System.out.print("Success. ");
            this.goldCollected++;
        }
        else{
            System.out.print("Fail. ");
        }
        System.out.println("Gold owned: "+this.goldCollected);
    }

    //Moves player by 1 position in chosen direction if adjacent position is valid, if not valid prints fail
    public void move(Map mapObject, char direction){
        switch(direction){

            case 'N':
                System.out.println(this.playerPosition[0]-1);
                if(mapObject.map[this.playerPosition[0]-1][this.playerPosition[1]]!='#'){
                    this.playerPosition[0]--;
                    System.out.println("Success");
                }
                else{
                    System.out.println("Fail");
                }
                break;

            case 'E':
                if(mapObject.map[this.playerPosition[0]][this.playerPosition[1]+1]!='#'){
                    this.playerPosition[1]++;
                    System.out.println("Success");
                }
                else{
                    System.out.println("Fail");
                }
                break;

            case 'S':
                if(mapObject.map[this.playerPosition[0]+1][this.playerPosition[1]]!='#'){
                    this.playerPosition[0]++;
                    System.out.println("Success");
                }
                else{
                    System.out.println("Fail");
                }
                break;

            case 'W':
                if(mapObject.map[this.playerPosition[0]][this.playerPosition[1]-1]!='#'){
                    this.playerPosition[1]--;
                    System.out.println("Success");
                }
                else{
                    System.out.println("Fail");
                }
                break;

            default:
                System.out.println("Fail");

        }
        mapObject.setPlayerPosition(this.playerPosition, this);
    }

    public abstract void look(Map mapObject, HumanPlayer humanPlayer);
}
