package com.github.vjgorla.astarjava;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.equalTo;

import java.util.Iterator;

import org.junit.Test;

public class AStarTest {

    @Test
    public void testPathExists() {
        AStar astar = new AStar(8, 6);
        astar.setOrigin(2, 2);
        astar.setDestination(6, 2);
        astar.setBlocked(4, 0);
        astar.setBlocked(4, 1);
        astar.setBlocked(4, 2);
        astar.setBlocked(4, 3);
        astar.setBlocked(5, 3);
        astar.setBlocked(5, 4);
        astar.setBlocked(3, 3);
        astar.setBlocked(2, 3);
        astar.setBlocked(1, 3);
        astar.setBlocked(1, 2);
        astar.setBlocked(1, 1);
        astar.setBlocked(2, 1);
        astar.findPath();
        assertTrue(astar.isPathFound());
        Iterator<Node> nodeIterator = astar.iterator();
        assertNode(nodeIterator, 2, 2);
        assertNode(nodeIterator, 3, 2);
        assertNode(nodeIterator, 3, 1);
        assertNode(nodeIterator, 3, 0);
        assertNode(nodeIterator, 2, 0);
        assertNode(nodeIterator, 1, 0);
        assertNode(nodeIterator, 0, 0);
        assertNode(nodeIterator, 0, 1);
        assertNode(nodeIterator, 0, 2);
        assertNode(nodeIterator, 0, 3);
        assertNode(nodeIterator, 0, 4);
        assertNode(nodeIterator, 1, 4);
        assertNode(nodeIterator, 2, 4);
        assertNode(nodeIterator, 3, 4);
        assertNode(nodeIterator, 4, 5);
        assertNode(nodeIterator, 5, 5);
        assertNode(nodeIterator, 6, 5);
        assertNode(nodeIterator, 6, 4);
        assertNode(nodeIterator, 6, 3);
        assertNode(nodeIterator, 6, 2);
    }

    @Test
    public void testPathNotExists() {
        AStar astar = new AStar(8, 6);
        astar.setOrigin(2, 2);
        astar.setDestination(6, 2);
        astar.setBlocked(4, 0);
        astar.setBlocked(4, 1);
        astar.setBlocked(4, 2);
        astar.setBlocked(4, 3);
        astar.setBlocked(5, 3);
        astar.setBlocked(5, 4);
        astar.setBlocked(3, 3);
        astar.setBlocked(2, 3);
        astar.setBlocked(1, 3);
        astar.setBlocked(1, 2);
        astar.setBlocked(1, 1);
        astar.setBlocked(2, 1);
        astar.setBlocked(2, 0);
        astar.findPath();
        assertFalse(astar.isPathFound());
    }

    private void assertNode(Iterator<Node> nodeIterator, int x, int y) {
        assertTrue(nodeIterator.hasNext());
        Node nextNode = nodeIterator.next();
        assertThat(x, equalTo(nextNode.getX()));
        assertThat(y, equalTo(nextNode.getY()));
    }
}
