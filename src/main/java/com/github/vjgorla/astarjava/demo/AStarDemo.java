package com.github.vjgorla.astarjava.demo;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

import com.github.vjgorla.astarjava.AStar;
import com.github.vjgorla.astarjava.Node;

/**
 * <ul>
 * <li>Double click in a box to make it the origin (green)</li>
 * <li>CTRL + Double click makes a box as destination (red)</li>
 * <li>Single clicking on a box, or dragging over a series of boxes creates walls</li>
 * <li>Click the Find Path button to find the nearest path between origin and destination</li>
 * </ul>
 * 
 * @author Vijaya Gorla
 */
@SuppressWarnings("serial")
public class AStarDemo extends JApplet {

    private static final int GRID_WIDTH = 30;
    private static final int GRID_HEIGHT = 20;

    private static final int NODE_PANEL_WIDTH = 15;
    private static final int NODE_PANEL_HEIGHT = 15;

    private static final LineBorder NODE_BORDER = new LineBorder(new Color(90, 90, 90), 1);

    private NodePanel[][] nodePanels = new NodePanel[GRID_WIDTH][GRID_HEIGHT];
    private NodePanel origin;
    private NodePanel destination;
    private JButton findButton;
    private JButton clearButton;

    private boolean pathPainted;

    private AStar astar;

    public void init() {
        setSize(450, 360);
        setLayout(null);
        for (int x = 0; x < GRID_WIDTH; x++) {
            for (int y = 0; y < GRID_HEIGHT; y++) {
                NodePanel nodePanel = new NodePanel(x, y);
                nodePanel.setLocation(x * NODE_PANEL_WIDTH, y * NODE_PANEL_HEIGHT);
                add(nodePanel);
                nodePanels[x][y] = nodePanel;
            }
        }
        findButton = new JButton("Find Path");
        findButton.setLocation(10, 320);
        findButton.setSize(90, 25);
        findButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                astar = new AStar(GRID_WIDTH, GRID_HEIGHT);
                astar.setOrigin(origin.x, origin.y);
                astar.setDestination(destination.x, destination.y);
                for (int x = 0; x < GRID_WIDTH; x++) {
                    for (int y = 0; y < GRID_HEIGHT; y++) {
                        NodePanel nodePanel = nodePanels[x][y];
                        if (!nodePanel.walkable) {
                            astar.setBlocked(nodePanel.x, nodePanel.y);
                        }
                    }
                }
                astar.findPath();
                if (astar.isPathFound()) {
                    for (Node node : astar) {
                        NodePanel nodePanel = nodePanels[node.getX()][node.getY()];
                        if (nodePanel != origin && nodePanel != destination) {
                            nodePanel.setBackground(Color.ORANGE);
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(AStarDemo.this, "No path found!");
                }
                pathPainted = astar.isPathFound();
                enableButtons();
            }
        });
        add(findButton);
        clearButton = new JButton("Clear");
        clearButton.setLocation(160, 320);
        clearButton.setSize(90, 25);
        clearButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (astar.isPathFound()) {
                    for (Node node : astar) {
                        NodePanel nodePanel = nodePanels[node.getX()][node.getY()];
                        if (nodePanel != origin && nodePanel != destination) {
                            nodePanel.updateBackground();
                        }
                    }
                }
                pathPainted = false;
                enableButtons();
            }
        });
        add(clearButton);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        enableButtons();
    }

    private void enableButtons() {
        findButton.setEnabled(origin != null && destination != null && !pathPainted);
        clearButton.setEnabled(origin != null && destination != null && pathPainted);
    }

    class NodePanel extends JPanel {
        boolean walkable = true;
        int x, y;

        NodePanel(int x, int y) {
            this.x = x;
            this.y = y;
            setSize(NODE_PANEL_WIDTH, NODE_PANEL_HEIGHT);
            setBackground(Color.WHITE);
            setBorder(NODE_BORDER);
            addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() == 1) {
                        if (NodePanel.this != origin && NodePanel.this != destination) {
                            walkable = !walkable;
                            updateBackground();
                        }
                    } else if (e.getClickCount() == 2) {
                        if ((e.getModifiers() & InputEvent.CTRL_MASK) != 0) {
                            if (destination != null) {
                                destination.walkable = true;
                                destination.updateBackground();
                            }
                            destination = NodePanel.this;
                            destination.walkable = true;
                            destination.setBackground(Color.RED);
                        } else {
                            if (origin != null) {
                                origin.walkable = true;
                                origin.updateBackground();
                            }
                            origin = NodePanel.this;
                            origin.walkable = true;
                            origin.setBackground(Color.GREEN);
                        }
                        enableButtons();
                    }
                }
            });
            addMouseMotionListener(new MouseMotionAdapter() {
                public void mouseDragged(MouseEvent e) {
                    int x = (e.getX() + getX()) / NODE_PANEL_WIDTH;
                    int y = (e.getY() + getY()) / NODE_PANEL_HEIGHT;
                    if (x >= 0 && x < GRID_WIDTH && y >= 0 && y < GRID_HEIGHT) {
                        NodePanel nodePanel = nodePanels[x][y];
                        if (nodePanel != origin && nodePanel != destination) {
                            nodePanel.walkable = false;
                            nodePanel.updateBackground();
                        }
                    }
                }
            });
        }

        void updateBackground() {
            if (walkable) {
                setBackground(Color.WHITE);
            } else {
                setBackground(Color.GRAY);
            }
        }
    }
}
