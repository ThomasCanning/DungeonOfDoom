import java.util.Scanner;

public abstract class Player {
    private int goldCollected=0;

    protected int steps=0;
    public Player(Map mapObject){
        mapObject.calculatePlayerStartingPoint(this);
    }

    public int getGoldCollected(){
        return goldCollected;
    }
    public int hello(Map mapObject){
        return mapObject.getGoldRequiredToWin();
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
        mapObject.movePlayer(this, direction);
    }
    
    public boolean quit(Map mapObject, Scanner scanner){
        return mapObject.attemptQuit(this);
    }

    public void incrementSteps() {
        this.steps++;
    }

    public abstract void look(Map mapObject);

}
