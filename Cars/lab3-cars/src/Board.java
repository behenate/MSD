import javax.swing.*;
import javax.swing.event.MouseInputListener;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;

public class Board extends JComponent implements MouseInputListener, ComponentListener {
    private static final long serialVersionUID = 1L;
    private Point[][] points = {};
    private int size = 10;
    public int editType = 0;
//    0 - just move, 1 - Nagel-Schreckenberg
    private int mode = 1;

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
            }
        }
    }


    public void iteration() {
//        Calculate distances to next car and reset moved stat
        for (int y = 0; y < points[0].length; ++y) {
            int first_car_pos = -1;
            int last_car_pos = -1;
            for (int x = 0; x < points.length; ++x) {
                //  Reset moved
                points[x][y].setMoved(false);
                if(points[x][y].getType() == 0)
                    continue;
                if (first_car_pos == -1) {
                    first_car_pos = x;
                    last_car_pos = x;
                    continue;
                }
                points[last_car_pos][y].setDistanceToNextCar(x-last_car_pos);
                last_car_pos = x;


            }
            if (last_car_pos != -1){
                points[last_car_pos][y].setDistanceToNextCar(points.length-last_car_pos + first_car_pos);
            }
        }

        if (mode == 1){
            for (int x = 0; x < points.length; ++x) {
                for (int y = 0; y < points[x].length; ++y) {
                    points[x][y].routine(0.2);
                }
            }
        }

        for (int x = 0; x < points.length; ++x) {
            for (int y = 0; y < points[x].length; ++y) {
                if (points[x][y].getType() != 0){
//                    Next pos = pos + speed ( for const speed = 1 )
                    int nextPos = (mode == 1) ? (x + points[x][y].getSpeed()) % points.length : (x + 1) % points.length;
                    points[x][y].setNextPosition(points[nextPos][y]);
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
        this.repaint();
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
                if (points[x][y].getType() == 0){
                    g.setColor(new Color(255,255,255));

                }else if(points[x][y].getType() == 1){
                    g.setColor(new Color(0, 0, 0));
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
            }
            this.repaint();
        }
    }

    public void setMode(int mode){
        this.mode = mode;
        if (mode== 1){
            for (int x = 0; x < points.length; ++x) {
                for (int y = 0; y < points[x].length; ++y) {
                    points[x][y].setSpeed(1);
                }
            }
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
