public class HumanPlayer extends Player{

    public HumanPlayer(Map mapObject) {
        super(mapObject);
    }

    //Overrides look method as human player method prints while bot look doesn't print
    @Override
    public void look(Map mapObject, HumanPlayer humanPlayer) {
        mapObject.printAroundPlayer(this);
    }

    public void incrementSteps(){
        this.steps++;
    }
    public int getSteps(){
        return this.steps;
    }
}
