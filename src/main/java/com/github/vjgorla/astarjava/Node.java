package com.github.vjgorla.astarjava;

/**
 * If the search area is thought of as a grid, this represents 
 * a square on the grid.
 * 
 * @author Vijay Gorla 
 */
public final class Node {
    
    /** Coordinates. */
    private final int x, y;
    
    /** Is this node walkable?. For example Walls, water, or other illegal 
     * terrain in a game are not walkable. */
	private boolean walkable = true;
	
    /** The movement cost to move from the starting point to a given square 
     * on the grid, following the path generated to get there. */
	private int g;
	
	/** The heuristic. In other words, the estimated movement cost to move 
	 * from that given square on the grid to the final destination. We use 
	 * manhattan method in this implementation. */
	private int h;
	
	/** Parent of this node. */
	private Node parent;
	
    /** Next node in the final path. Null if no path is found. */
	private Node nextNode;
    
    Node(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}
	
	void setH(int h) {
		this.h = h;
	}

    int getG() {
		return g;
	}

	void setG(int g) {
		this.g = g;
	}

	Node getParent() {
		return parent;
	}

	void setParent(Node parent) {
		this.parent = parent;
	}

	Node getNextNode() {
		return nextNode;
	}

	void setNextNode(Node nextNode) {
		this.nextNode = nextNode;
	}

	boolean isWalkable() {
		return walkable;
	}

	void setWalkable(boolean walkable) {
		this.walkable = walkable;
	}

	/** 
	 *  Total cost of movement.
	 *  F = G + H. 
     *  where
     *  G = the movement cost to move from the starting point to a given square on the grid, 
     *      following the path generated to get there. 
     *  H = the estimated movement cost to move from that given square on the grid to the 
     *      final destination, referred to as the heuristic.
     */
	int getCost() {
        return g + h;
    }
    
    public String toString() {
        String str = x + ":" + y + "-" + getCost();
        if (parent != null) {
            str = str + "   " + parent.x + ":" + parent.y;
        }
        return str;
    }
}
