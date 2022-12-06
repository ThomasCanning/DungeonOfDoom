import java.util.ArrayList;
import java.util.Collections;

public class AStarSearch{

    public void GetShortestDistanceTo(char[][] map, int[] startPosition, int[] endPosition){
        //Creates an array list open nodes that represents the frontier of nodes being considered
        ArrayList<Node> openNodes =new ArrayList<>();

        //Creates start and end node objects which have row and elementPosition attributes which act as coordinates
        //and attributes relating to A* search
        Node startNode = new Node(startPosition[0],startPosition[1]);
        Node endNode = new Node(endPosition[0],endPosition[1]);

        //Adds start position as a node to open nodes
        openNodes.add(startNode);

        //Keeps track of nodes already considered so that they are not looked at again
        ArrayList<Node> closedNodes =new ArrayList<>();

        Node currentNode;
        Node previousNode = null;

        while(openNodes.size()>0){ //Iteratively uses A* algorithm to find the shortest path between startPosition and endPosition
            currentNode=openNodes.get(0);

            //Searches through open nodes for one with lowest f cost to assign as current node
            for(int i=1;i<openNodes.size();i++){
                //updates current node if better than currently selected node from open nodes
                if((openNodes.get(i).fCost())<currentNode.fCost()||(openNodes.get(i).fCost()==currentNode.fCost()&&openNodes.get(i).hCost<currentNode.hCost)){
                    currentNode=openNodes.get(i);
                }
            }

            //Switches current node to closed list
            openNodes.remove(currentNode);
            closedNodes.add(currentNode);

            //checks if reached end node
            if(currentNode.row==endNode.row&&currentNode.elementPosition==endNode.elementPosition){
                //What happens when reached end point
                endNode.parent=previousNode;
                ArrayList<Node> path = retracePath(startNode, previousNode);
                return;
            }

            //checks each neighbour of current node, ignoring walls and nodes on the closed list
            for (Node neighbour:this.getNeighbours(map, currentNode.row, currentNode.elementPosition, closedNodes)){

                //Finds cost of total current route to neighbour
                int newCostToNeighbour = currentNode.gCost+getDistance(currentNode, neighbour);


                //Check to see if new path is better
                if(newCostToNeighbour<neighbour.gCost){
                    //updates values of neighbour to shorter distances
                    neighbour.gCost=newCostToNeighbour;
                    neighbour.hCost=getDistance(neighbour, endNode);
                    neighbour.parent=currentNode;

                    //If not on open list, add it to open list and set g and h costs and assign parent
                    if(!neighbourInList(openNodes, neighbour)) {
                        openNodes.add(neighbour);
                    }
                }
            }
            //Previous node is used to assign parent node of end node when end node is reached
            previousNode = currentNode;
        }

    }

    private ArrayList<Node> getNeighbours(char[][] map, int row, int elementPosition, ArrayList<Node> closedNodes){
        ArrayList<Node> neighbours = new ArrayList<>();

        //Checks adjacent positions to the input position, ensuring it hasn't already been explored (closed nodes)

        //checks north
        if (map[row][elementPosition - 1] != '#' && !closedNodes.contains(new Node(row, elementPosition - 1))) {
                neighbours.add(new Node(row, elementPosition - 1));
            }

        //checks east
        if (map[row][elementPosition + 1] != 1 && !closedNodes.contains(new Node(row, elementPosition + 1))) {
                neighbours.add(new Node(row, elementPosition+1));
            }

        //checks south
        if (map[row + 1][elementPosition] != 1 && !closedNodes.contains(new Node(row + 1, elementPosition))) {
                neighbours.add(new Node(row+1, elementPosition));
        }

        //checks west
        if (map[row][elementPosition - 1] != 1 && !closedNodes.contains(new Node(row, elementPosition - 1))) {
            neighbours.add(new Node( row, elementPosition - 1));
        }
        return neighbours;
    }

    private int getDistance(Node nodeA, Node nodeB) {
        return (int)(Math.sqrt(Math.pow(nodeA.row-nodeB.row,2)+Math.pow(nodeA.elementPosition-nodeB.elementPosition,2))*10);
    }

    //Method that continually adds parent node to a list starting from end node and working to start node
    //list is then reversed to create a list of all nodes visited in the path from start to end
    private ArrayList<Node> retracePath(Node startNode, Node endNode){
        ArrayList<Node> path = new ArrayList<>();
        Node currentNode = endNode;
        while (currentNode!=startNode){
            path.add(currentNode);
            currentNode=currentNode.parent;

        }
        Collections.reverse(path);
        return path;
    }

    private boolean neighbourInList(ArrayList <Node> list, Node neighbour){
        int elementPosition = neighbour.elementPosition;
        int row = neighbour.row;
        boolean equals = false;
        for (Node node : list) {
            if (node.elementPosition == elementPosition && node.row == row) {
                equals = true;
                break;
            }
        }
        return equals;
    }

    private class Node{
        int row;
        int elementPosition;

        //gCost is distance from start to current node
        int gCost;

        //hCost is distance from current node to end
        int hCost;

        //fCost is the heuristic used for A* algorithm = gcost+hcost
        private int fCost(){
            return gCost+hCost;
        }

        //Parent node attribute is used to retrace path once the end node is reached
        Node parent;

        //Constructor for node assigns 'coordinates' of node
        private Node(int row, int elementPosition){
            this.row=row;
            this.elementPosition=elementPosition;
        }
    }

}
