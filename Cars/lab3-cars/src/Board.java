import javax.swing.*;
import javax.swing.event.MouseInputListener;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.util.Arrays;

public class Board extends JComponent implements MouseInputListener, ComponentListener {
    private static final long serialVersionUID = 1L;
    private Point[][] points = {};
    private int size = 25;
    public int editType = 0;

    public Board(int length, int height) {
        addMouseListener(this);
        addComponentListener(this);
        addMouseMotionListener(this);
        setBackground(Color.WHITE);
        setOpaque(true);
    }

    //    Create all points on the map
    private void initialize(int length, int height) {
        points = new Point[length][height];
        for (int x = 0; x < points.length; ++x) {
            for (int y = 0; y < points[x].length; ++y) {
                points[x][y] = new Point();
                if (y <= 1 || y >= 4) {
                    points[x][y].setType(5);
                }
            }
        }
    }

    private boolean canPointOvertake(int x, int y) {
        Point point = points[x][y];
        if (point.lane != 1 || point.neighsSameLane[1] == null || point.neighsSameLane[1].equals(point)) {
            return false;
        }
        boolean inFrontSlower = point.getSpeed() < point.getMaxSpeed();
        boolean distanceInTheBack = point.distanceSameLane[0] - 1 >= point.getMaxSpeed();
        boolean distanceInTheBackLeft = point.distanceSideLane[0] - 1 >= point.getMaxSpeed();
        boolean distanceInTheFrontLeft = point.distanceSideLane[1] - 1 >= point.getSpeed();
        return inFrontSlower && distanceInTheBack && distanceInTheBackLeft && distanceInTheFrontLeft;
    }

    private boolean canPointGoBack(int x, int y) {
        Point point = points[x][y];
        if (point.lane != 0)
            return false;
        boolean distanceInTheBackRight = point.distanceSideLane[0] - 1 >= point.getMaxSpeed();
        boolean distanceInTheBack = point.distanceSameLane[0] - 1 <= point.getMaxSpeed();
        boolean distanceInTheFront = point.distanceSideLane[1] - 1 >= point.getSpeed();
        return distanceInTheBackRight && distanceInTheFront && distanceInTheBack;
    }

    public void iteration() {
        for (int x = 0; x < points.length; ++x) {
            for (int y = 0; y < points[x].length; ++y) {
                points[x][y].setMoved(false);
            }
        }
//        Calculate distances to next car and reset moved stat
        calculateDistances();




        for (int x = 0; x < points.length; ++x) {
            for (int y = 0; y < points[x].length; ++y) {
                if (points[x][y].getType() != 0 && points[x][y].getType() != 5) {
//                    Next pos = pos + speed ( for const speed = 1 )
                    int nextPosX = (x + points[x][y].getSpeed()) % points.length;
                    if (nextPosX == -1)
                        nextPosX = points.length - 1;
                    if (canPointGoBack(x, y)) {
                        System.out.println("Go back " + x + " " + y);
                        points[x][y].setNextPosition(points[nextPosX][y + 1]);
                    } else if (canPointOvertake(x, y)) {
                        System.out.println("Overtake " + x + " " + y);
                        points[x][y].setNextPosition(points[nextPosX][y - 1]);
                    } else {
                        nextPosX = (x + points[x][y].getSpeed()) % points.length;
                        points[x][y].setNextPosition(points[(nextPosX) % points.length][y]);
                        System.out.println("Forwards " + x + " " + y );
                    }
//                    if (points[x][y].getType() != 0 && points[x][y].getType() != 5) {
//                        System.out.println("Dist_fwd: " + points[x][y].distanceSameLane[1] + " " + x + " " + y);
//                        System.out.println("Dist_bckd: " + points[x][y].distanceSameLane[0] + " " + x + " " + y);
//                        System.out.println("Dist_fwd_s: " + points[x][y].distanceSameLane[1] + " " + x + " " + y);
//                        System.out.println("Dist_bckwd_s: " + points[x][y].distanceSameLane[0] + " " + x + " " + y);
//                    }
                }
            }
        }

        for (int x = 0; x < points.length; ++x) {
            for (int y = 0; y < points[x].length; ++y) {
                points[x][y].routine(0.2);
            }
        }

        for (int x = 0; x < points.length; ++x) {
            for (int y = 0; y < points[x].length; ++y) {
                if (points[x][y].getType() != 0 && points[x][y].getType() != 5) {
                    points[x][y].move();
                }
            }
        }
        this.repaint();
    }

    public void clear() {
        for (int x = 0; x < points.length; ++x)
            for (int y = 0; y < points[x].length; ++y) {
                points[x][y].clear();
            }
        initialize(points.length, points[0].length);
        this.repaint();
    }

    private void calculateDistances() {
        for (int y = 2; y < 4; ++y) {
            for (int x = 0; x < points.length; x++) {
//                Offset is equal -1 for right lane and 1 for left lane
                int off = 0;
                if (y == 2) {
                    points[x][y].lane = 0;
                    off = 1;
                } else {
                    points[x][y].lane = 1;
                    off = -1;
                }
                if (points[x][y].getType() == 0)
                    continue;
//                Calculate Same lane forwards distance
                for (int i = 1; i < points.length + 1; i++) {
                    int nextCarPos = (x + i) % points.length;
                    if (points[nextCarPos][y].getType() != 0) {
                        if (nextCarPos > x) {
                            points[x][y].distanceSameLane[1] = nextCarPos - x;
                        } else {
                            points[x][y].distanceSameLane[1] = points.length - x + nextCarPos;
                        }
                        points[x][y].neighsSameLane[1] = points[nextCarPos][y];
                        break;
                    }
                }
//                  Same lane backwards distance
                for (int i = 1; i < points.length + 1; i++) {
                    int prevCarPos;
                    if (x - i >= 0) {
                        prevCarPos = x - i;
                    } else {
                        prevCarPos = points.length + x - i;
                    }
                    if (points[prevCarPos][y].getType() != 0) {
                        if (prevCarPos < x) {
                            points[x][y].distanceSameLane[0] = x - prevCarPos;
                        } else {
                            points[x][y].distanceSameLane[0] = points.length + x - prevCarPos;
                        }
                        points[x][y].neighsSameLane[0] = points[prevCarPos][y];

                        break;
                    }
                }
//                  Side (left or right, depends) lane distance forwards
                for (int i = 0; i < points.length + 1; i++) {
                    if (points[x][y+off].getType() != 0){
                        points[x][y].distanceSideLane[1] = 0;
                        break;
                    }
                    int nextCarPos = (x + i) % points.length;
                    if (points[nextCarPos][y + off].getType() != 0) {
                        if (nextCarPos > x) {
                            points[x][y].distanceSideLane[1] = nextCarPos - x;
                        } else {
                            points[x][y].distanceSideLane[1] = points.length - x + nextCarPos;
                        }
                        points[x][y].neighsSideLane[1] = points[nextCarPos][y + off];
                        break;
                    }
                }
//                Side lane distance backwards
                for (int i = 0; i < points.length + 1; i++) {
                    if (points[x][y+off].getType() != 0){
                        points[x][y].distanceSideLane[0] = 0;
                        break;
                    }
                    int nextCarPos;
                    if (x - i >= 0) {
                        nextCarPos = x - i;
                    } else {
                        nextCarPos = points.length + x - i;
                    }
                    if (points[nextCarPos][y + off].getType() != 0) {
                        if (nextCarPos < x) {
                            points[x][y].distanceSideLane[0] = x - nextCarPos;
                        } else {
                            points[x][y].distanceSideLane[0] = points.length + x - nextCarPos;
                        }
                        points[x][y].neighsSideLane[0] = points[nextCarPos][y + off];
                        break;
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

        for (x = 0; x < points.length; ++x) {
            for (y = 0; y < points[x].length; ++y) {
                float a = 1.0F;

                //g.setColor(new Color(R, G, B, 0.7f));
                if (points[x][y].getType() == 0) {
                    g.setColor(new Color(255, 255, 255));

                } else if (points[x][y].getType() == 1) {
                    g.setColor(new Color(234, 198, 106));
                } else if (points[x][y].getType() == 2) {
                    g.setColor(new Color(113, 207, 216));
                } else if (points[x][y].getType() == 3) {
                    g.setColor(new Color(202, 50, 50));
                } else if (points[x][y].getType() == 5) {
                    g.setColor(new Color(191, 255, 150));
                }
                g.fillRect((x * size) + 1, (y * size) + 1, (size - 1), (size - 1));
            }
        }

    }

    public void mouseClicked(MouseEvent e) {
        int x = e.getX() / size;
        int y = e.getY() / size;
        if ((x < points.length) && (x > 0) && (y < points[x].length) && (y > 0)) {
            if (editType == 0) {
                points[x][y].clicked();
            } else {
                points[x][y].setType(editType);
            }
            this.repaint();
        }
    }

    public void componentResized(ComponentEvent e) {
        int dlugosc = (this.getWidth() / size) + 1;
        int wysokosc = (this.getHeight() / size) + 1;
        initialize(dlugosc, wysokosc);
    }

    public void mouseDragged(MouseEvent e) {
        int x = e.getX() / size;
        int y = e.getY() / size;
        if ((x < points.length) && (x > 0) && (y < points[x].length) && (y > 0)) {
            if (editType == 0) {
                points[x][y].clicked();
            } else {
                points[x][y].setType(editType);
            }
            this.repaint();
        }
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
