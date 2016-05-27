package com.github.vjgorla.astarjava;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Implementation of A* algorithm for path finding. For more information
 * see <link>http://www.policyalmanac.org/games/aStarTutorial.htm</link>.
 *
 * @author Vijay Gorla 
 */

public class AStar implements Iterable<Node> {
	
    private final int gridWidth, gridHeight;
    
    /** Array of all nodes in the grid. First dimension is the width and 
     * the second is height. */
    private final Node[][] nodes;
    
    private Node origin;
    private Node destination;
    
    /** List of nodes to be processed at any given point. */
    private final List<Node> openList = new ArrayList<>();
    
    /** List of nodes that are already processed. */
    private final List<Node> closedList = new ArrayList<>();
    
    /**
     * Initialise with the given grid width and height.
     */
    public AStar(int gridWidth, int gridHeight) {
        this.gridWidth = gridWidth;
        this.gridHeight = gridHeight;
        nodes = new Node[gridWidth][gridHeight];
        for (int i = 0; i < gridWidth; i++) {
            for (int j = 0; j < gridHeight; j++) {
                // Node is initialised with its X and Y coordinates, and stored at the
                // corresponding location in the array. This two-way pointer makes it
                // easy to navigate from point -> node object, and node object -> point.
                nodes[i][j] = new Node(i, j);
            }
        }
    }
    
    /** Mark a node as the origin. */
    public void setOrigin(int x, int y) {
        origin = nodes[x][y];
    }
    
    /** Mark a node as the destination. */
    public void setDestination(int x, int y) {
        destination = nodes[x][y];
        // Also, precalculate h (heuristic) for all nodes in the grid.
        // Not a probably good idea if the grid is large and performance is the main goal.
        for (int i = 0; i < gridWidth; i++) {
            for (int j = 0; j < gridHeight; j++) {
                // The method we use here is called the Manhattan method, where you calculate the 
                // total number of squares moved horizontally and vertically to reach the target 
                // square from the current square, ignoring diagonal movement, and ignoring any 
                // obstacles that may be in the way. We then multiply the total by 10, our cost 
                // for moving one square horizontally or vertically.
                nodes[i][j].setH((Math.abs(destination.getX() - nodes[i][j].getX()) 
                    + Math.abs(destination.getY() - nodes[i][j].getY())) * 10);

            }
        }
    }

    /** Set a node as blocked (not walkable). */
    public void setBlocked(int x, int y) {
        nodes[x][y].setWalkable(false);
    }
    
    /**
     * Process the given node, and return lowest F cost node on the open list.
     */
    private Node process(Node node) {
        openList.remove(node);
        closedList.add(node);
        // For each of the 8 nodes adjacent to this current node
        for (int i = (node.getX() - 1); i <= (node.getX() + 1); i++) {
            for (int j = (node.getY() - 1); j <= (node.getY() + 1); j++) {
                if (i >= 0 && i < gridWidth && j >= 0 && j < gridHeight) { 
                    Node checkNode = nodes[i][j];
                    // If it is not walkable or if it is on the closed list, ignore it.
                    if (checkNode.isWalkable() && !isAroundCorner(checkNode, node) 
                    		&& !closedList.contains(checkNode)) {
                        if (!openList.contains(checkNode)) {
                            // If it isn't on the open list, add it to the open list. Make the 
                            // current node the parent of this node. Record the cost of the node. 
                            openList.add(checkNode);
                            checkNode.setParent(node);
                            checkNode.setG(calculateG(checkNode, node));
                        } else {
                            // If it is on the open list already, check to see if this path to that 
                            // node is better, using G cost as the measure. A lower G cost means that 
                            // this is a better path. If so, change the parent of the node to the 
                            // current node, and recalculate the G and F scores of the square.
                            int g = calculateG(checkNode, node);
                            if (g < checkNode.getG()) {
                                checkNode.setParent(node);
                                checkNode.setG(g);
                            }
                        }
                    }
                }
            }
        }
        // Look for the lowest F cost square on the open list
        Node minCostNode = null;
        for (Node iNode : openList) {
            if (minCostNode == null || iNode.getCost() <= minCostNode.getCost()) {
                minCostNode = iNode;
            }
        }
        return minCostNode;
    }
    
    /**
     * Do we need to go around a corner?
     */
    private boolean isAroundCorner(Node node, Node potentialParent) {
    	if (isAdjacent(node, potentialParent)) {
    		return false;
    	} else {
    		return !nodes[node.getX()][potentialParent.getY()].isWalkable() || 
    			!nodes[potentialParent.getX()][node.getY()].isWalkable();
    	}
    }
    
    private boolean isAdjacent(Node node, Node potentialParent) {
    	return node.getX() == potentialParent.getX() || 
    			node.getY() == potentialParent.getY();
    }
    
    /**
     * Calculate the movement cost to move from the <tt>potentialParent</tt> to the 
     * given <tt>node</tt>, including the cost to get to the <tt>potentialParent</tt>.
     */
    private int calculateG(Node node, Node potentialParent) {
        return potentialParent.getG() + (isAdjacent(node, potentialParent) ? 10 : 14);
    }
    
    /**
     * Find the nearest path.
     */
    public void findPath() {
        Node node = origin;
        while (true) {
            node = process(node);
            if (node == null || node == destination) break;
        }
        // Trace backwards from destination and record the found path.
        while (node != null && node != origin) {
            node.getParent().setNextNode(node);
            node = node.getParent();
        }
    }
    
    public boolean isPathFound() {
    	return origin.getNextNode() != null;
    }
    
    public void print() {
        if (isPathFound()) {
            for (Node node : this) {
                System.out.println(node);
            }
        } else {
            System.out.println("No path found !!!");
        }
    }

    /**
     * Return the iterator for final path.
     */
    public Iterator<Node> iterator() {
    	return new Iterator<Node>() {
    		private Node currentNode = origin;
			public boolean hasNext() {
				return currentNode != null;
			}
			public Node next() {
				Node retValue = currentNode;
				currentNode = currentNode.getNextNode();
				return retValue;
			}
			public void remove() {
				throw new IllegalStateException("Can't remove");
			}
		};
	}
}