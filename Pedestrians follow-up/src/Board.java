import java.awt.Color;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Random;

import javax.swing.JComponent;
import javax.swing.event.MouseInputListener;

public class Board extends JComponent implements MouseInputListener, ComponentListener {
    private static final long serialVersionUID = 1L;
    private Point[][] points;
    private int size = 10;
    public int editType = 0;
    private boolean antialiasing = true;
    private boolean wallRepulsion = true;
    //	0 - Moores, 1 - VonNeumann neighbours
    protected int neighType = 0;
    private int maxStaticField = 1000;
    static int RST_PATH_DELTA = 10;
    static int RST_ANTIALIAS_DELTA = 15;
    static int RST_ANTIALIAS_RADIUS = 20;
    static int EXIT_FIELD_DELTA = 1;
    static int WALL_REPULSION_STRENGTH = 10;
    static int WALL_REPULSION_RADIUS = 2;

    public Board(int length, int height) {
        addMouseListener(this);
        addComponentListener(this);
        addMouseMotionListener(this);
        setBackground(Color.WHITE);
        setOpaque(true);
    }

    public void iteration() {
        for (int x = 1; x < points.length - 1; ++x)
            for (int y = 1; y < points[x].length - 1; ++y)
                points[x][y].hasMoved = false;

        for (int x = 1; x < points.length - 1; ++x)
            for (int y = 1; y < points[x].length - 1; ++y)
                points[x][y].move();

        this.repaint();
    }

    public void clear() {
        for (int x = 0; x < points.length; ++x)
            for (int y = 0; y < points[x].length; ++y) {
                points[x][y].clear();
            }
        calculateField();
        this.repaint();
    }

    private void initialize(int length, int height) {
        points = new Point[length][height];

        for (int x = 0; x < points.length; ++x)
            for (int y = 0; y < points[x].length; ++y)
                points[x][y] = new Point();
        fillNeighbors();
//        points[20][30].type = 2;
//        points[60][40].type = 2;
//        points[10][60].type = 2;
//        for (int i = 0; i < 50; i++) {
//            points[70][i+10].type = 1;
//        }
//        for (int i = 0; i < 50; i++) {
//            points[90][i+10].type = 3;
//            points[90][i+10].isPedestrian = true;
//        }
        calculateField();
        this.repaint();

    }

    private void clearNeighbors() {
        for (int x = 1; x < points.length - 1; ++x) {
            for (int y = 1; y < points[x].length - 1; ++y) {
                points[x][y].neighbors.clear();
            }
        }
    }

    private void fillNeighbors() {
        for (int x = 1; x < points.length - 1; ++x) {
            for (int y = 1; y < points[x].length - 1; ++y) {
                if (neighType == 0) {
                    setupMooreNeighbours(x, y);
                } else {
                    setupVonNeumannNeighbours(x, y);
                }
            }
        }
    }


    private void calculateField() {
        int npsf = new Point().staticField;
        for (Point[] value : points) {
            for (Point point : value) {
                point.staticField = npsf;
                if (point.type == 2) {
                    point.staticField = 0;
                }
            }
        }
        rapidlyExploringSearchTree(300);
        if (wallRepulsion){
            for (int x = 1; x < points.length - 1; ++x) {
                for (int y = 1; y < points[x].length - 1; ++y) {
                    if (points[x][y].type == 1) {
                        staticFieldAroundObstacle(x, y, WALL_REPULSION_RADIUS, WALL_REPULSION_STRENGTH);
                    }
                }
            }
        }
    }

    double distance(int x1, int y1, int x2, int y2) {
        return Math.sqrt(Math.pow((x1 - x2), 2) + Math.pow((y1 - y2), 2));
    }

    private void rapidlyExploringSearchTree(int iterations) {
        double currentTime = System.currentTimeMillis();
        ArrayList<int[]> nodes = new ArrayList<>();
        Random random = new Random();

        for (int x = 0; x < points.length; ++x) {
            for (int y = 0; y < points[x].length; ++y) {
                if (points[x][y].type == 2) {
                    nodes.add(new int[]{x, y});
                }
            }
        }

        for (int i = 0; i < iterations; i++) {
            int px = random.nextInt(points.length);
            int py = random.nextInt(points[0].length);
            while (points[px][py].type != 0) {
                px = random.nextInt(points.length);
                py = random.nextInt(points[0].length);
            }
            int minX = 0;
            int minY = 0;
            double min_dist = 100000;
            int[][] shortestPath = null;
            for (int j = 0; j < nodes.size(); j++) {
                int candX = nodes.get(j)[0];
                int candY = nodes.get(j)[1];
                int[][] candPath = linePath(candX, candY, px, py);
                if (distance(candX, candY, px, py) < min_dist && isPathValid(candPath)) {
                    shortestPath = candPath.clone();
                    min_dist = distance(nodes.get(j)[0], nodes.get(j)[1], px, py);
                    minX = candX;
                    minY = candY;
                }
            }

            if (shortestPath != null) {
                drawRstLine(points[minX][minY], shortestPath);
                nodes.addAll(Arrays.asList(shortestPath));
            }
        }
        flipAndAntialiasRstLines();
    }

    //    Finds an array of points which represents a "straight" line from start to target
    private int[][] linePath(int startX, int startY, int targetX, int targetY) {
        int dx = targetX - startX;
        int dy = targetY - startY;
        int total_steps = Math.abs(dx) + Math.abs(dy);
        int[][] path = new int[total_steps][2];
        int curr_x = startX;
        int curr_y = startY;
        if (Math.abs(dx) >= Math.abs(dy)) {
            int xsPerY;
            if (dy == 0)
                xsPerY = 1000000000;
            else
                xsPerY = Math.abs(dx) / Math.abs(dy);
            for (int i = 1; i < total_steps + 1; i++) {
                if (i % (xsPerY + 1) != 0 || curr_y == targetY) {
                    curr_x += Math.signum(dx);
                } else {
                    curr_y += Math.signum(dy);
                }
                path[i - 1][0] = curr_x;
                path[i - 1][1] = curr_y;
            }
        } else {
            int ysPerX;
            if (dx == 0)
                ysPerX = 10000000;
            else
                ysPerX = Math.abs(dy) / Math.abs(dx);
            for (int i = 1; i < total_steps + 1; i++) {
                if (i % (ysPerX + 1) != 0 || curr_x == targetX) {
                    curr_y += Math.signum(dx);
                } else {
                    curr_x += Math.signum(dy);
                }
                path[i - 1][0] = curr_x;
                path[i - 1][1] = curr_y;
            }
        }
        return path;
    }

    private void drawRstLine(Point targetPoint, int[][] path) {
        int field = targetPoint.staticField;
        for (int[] p : path) {
            if (!points[p[0]][p[1]].equals(targetPoint)) {
                field -= RST_PATH_DELTA;
                points[p[0]][p[1]].staticField = field;
            }
        }
    }
    //  Finds all rstLines, and changes their static field to positive, also applies antialiasing
    private void flipAndAntialiasRstLines() {
        ArrayList<int[]> toAntialias = new ArrayList<>();
        for (int x = 0; x < points.length; ++x) {
            for (int y = 0; y < points[x].length; ++y) {
                if (points[x][y].staticField < 0) {
                    points[x][y].staticField = -points[x][y].staticField;
                    toAntialias.add(new int[]{x, y});
                }
            }
        }
        if (antialiasing){
            for (int[] p : toAntialias) {
                bfsAntialiasingFromPoint(points[p[0]][p[1]], RST_ANTIALIAS_DELTA);
            }
        }
    }

    private void bfsAntialiasingFromPoint(Point start, int delta) {

        LinkedList<Point> queue = new LinkedList<>();
        queue.add(start);
        while (queue.size() > 0) {
            Point current = queue.pop();
            for (Point neighbor : current.neighbors) {
                int currentField = neighbor.staticField;
                int newField = current.staticField + delta;
                if (newField < currentField && neighbor.type != 1 && neighbor.type != 2) {
                    neighbor.staticField = newField;
                    queue.add(neighbor);
                }

            }
        }

    }

    private boolean isPathValid(int[][] path) {
        if (path.length == 0) {
            return false;
        }
        for (int[] p : path) {
            if (p[0] < 0 || p[0] >= points.length || p[1] < 0 || p[1] >= points[0].length) {
                return false;
            }
            Point point = points[p[0]][p[1]];
            if (point.type == 1 || point.staticField < 0) {
                return false;
            }
        }
        return true;
    }

    //    Modifies the static field around a given point so that the static field = pointStaticField + (delta * distance + 1)
//    Doesnt apply to fields that have lower static field if trying to attract and to points that have higher static field
//    If trying to repulse (it makes sense I promise)
    private void staticFieldCircle(int x, int y, int radius, int delta) {
        int startField = points[x][y].staticField;
        for (int i = y - radius; i < y + radius + 1; ++i) {
            for (int j = x - radius; j < x + radius + 1; j++) {
                if ((i == x && j == y) || i < 0 || i >= points[0].length || j < 0 || j >= points.length) {
                    continue;
                }
                double distance = distance(x, y, j, i);
                int newField = (int) (startField + (distance * delta));
                int currentField = points[j][i].staticField;
                if (newField < currentField && delta < 0 || newField > currentField && delta > 0) {
                    continue;
                }
                if (distance <= radius) {
                    points[j][i].staticField = newField;
                    if (points[j][i].staticField < 0) {
                        points[j][i].staticField = 0;
                    }
                }
            }
        }
    }

    private void staticFieldAroundObstacle(int x, int y, int radius, int strength) {
        for (int i = y - radius; i < y + radius + 1; ++i) {
            for (int j = x - radius; j < x + radius + 1; j++) {
                if ((i == x && j == y) || i < 0 || i >= points[0].length || j < 0 || j >= points.length) {
                    continue;
                }
                double distance = distance(x, y, j, i);
                if (distance <= radius) {
                    points[j][i].staticField += strength * (radius - distance + 1);
                    if (points[j][i].staticField < 0) {
                        points[j][i].staticField = 0;
                    }
                }
            }
        }
    }


    protected void paintComponent(Graphics g) {
        if (isOpaque()) {
            g.setColor(getBackground());
            g.fillRect(0, 0, this.getWidth(), this.getHeight());
        }
        g.setColor(Color.GRAY);
        drawNetting(g, size);
    }

    private void drawNetting(Graphics g, int gridSpace) {
//        Find the max static field that isn't the default static field
//        for (int x = 1; x < points.length - 1; ++x) {
//            for (int y = 1; y < points[x].length - 1; ++y) {
//                if (points[x][y].staticField != new Point().staticField)
//                    maxStaticField = Math.max(maxStaticField, points[x][y].staticField);
//            }
//        }
        Insets insets = getInsets();
        int firstX = insets.left;
        int firstY = insets.top;
        int lastX = this.getWidth() - insets.right;
        int lastY = this.getHeight() - insets.bottom;

        int x = firstX;
        while (x < lastX) {
            g.drawLine(x, firstY, x, lastY);
            x += gridSpace;
        }

        int y = firstY;
        while (y < lastY) {
            g.drawLine(firstX, y, lastX, y);
            y += gridSpace;
        }

        for (x = 1; x < points.length - 1; ++x) {
            for (y = 1; y < points[x].length - 1; ++y) {
                if (points[x][y].type == 0) {
                    float staticField = points[x][y].staticField;
                    float intensity = staticField / maxStaticField;

                    if (intensity > 1.0) {
                        intensity = 1.0f;
                    }
                    if (intensity < 0 || intensity > 1) {
                        System.out.println(intensity + " x:  " + x + "y : " + y + "type: " + points[x][y].staticField);
                    }
                    g.setColor(new Color(intensity, intensity, intensity));
                } else if (points[x][y].type == 1) {
                    g.setColor(new Color(1.0f, 0.0f, 0.0f, 0.7f));
                } else if (points[x][y].type == 2) {
                    g.setColor(new Color(0.0f, 1.0f, 0.0f, 0.7f));
                }
                if (points[x][y].isPedestrian) {
                    g.setColor(new Color(0.0f, 0.0f, 1.0f, 0.7f));
                }
                g.fillRect((x * size) + 1, (y * size) + 1, (size - 1), (size - 1));
            }
        }

    }

    public void setNeighbourhood(int neighType) {
        clearNeighbors();
        this.neighType = neighType;
        fillNeighbors();
        calculateField();
        repaint();
    }

    public void mouseClicked(MouseEvent e) {
        int x = e.getX() / size;
        int y = e.getY() / size;
        if ((x < points.length) && (x > 0) && (y < points[x].length) && (y > 0)) {
            if (editType == 3) {
                points[x][y].isPedestrian = true;
            } else {
                points[x][y].type = editType;
            }
            this.repaint();
        }
    }

    public void componentResized(ComponentEvent e) {
        int length = (this.getWidth() / size) + 1;
        int height = (this.getHeight() / size) + 1;
        initialize(length, height);
    }

    public void mouseDragged(MouseEvent e) {
        int x = e.getX() / size;
        int y = e.getY() / size;
        if ((x < points.length) && (x > 0) && (y < points[x].length) && (y > 0)) {
            if (editType == 3) {
                points[x][y].isPedestrian = true;
            } else {
                points[x][y].type = editType;
            }
            this.repaint();
        }
    }

    public void setAntialiasing(boolean antialiasing){
        this.antialiasing = antialiasing;
        calculateField();
        repaint();
    }

    public void setWallRepulsion(boolean wallRepulsion){
        this.wallRepulsion = wallRepulsion;
        calculateField();
        repaint();
    }

    private void setupMooreNeighbours(int x, int y) {
        Point point = points[x][y];
        for (int i = x - 1; i < x + 2; i++) {
            for (int j = y - 1; j < y + 2; j++) {
                if (x != i || y != j)
                    point.addNeighbor(points[i][j]);
            }
        }
    }

    private void setupVonNeumannNeighbours(int x, int y) {
        Point point = points[x][y];
        point.addNeighbor(points[x + 1][y]);
        point.addNeighbor(points[x - 1][y]);
        point.addNeighbor(points[x][y + 1]);
        point.addNeighbor(points[x][y - 1]);
    }

    public void setMaxStaticField(int maxStaticField) {
        this.maxStaticField = maxStaticField;
        this.repaint();
    }

    public void mouseExited(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void componentShown(ComponentEvent e) {
    }

    public void componentMoved(ComponentEvent e) {
    }

    public void mouseReleased(MouseEvent e) {
    }

    public void mouseMoved(MouseEvent e) {
    }

    public void componentHidden(ComponentEvent e) {
    }

    public void mousePressed(MouseEvent e) {
    }

}
