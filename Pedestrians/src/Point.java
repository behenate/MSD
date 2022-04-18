import javax.swing.table.TableRowSorter;
import java.util.ArrayList;

public class Point {

    public ArrayList<Point> neighbors;
    public static Integer[] types = {0, 1, 2, 3};
    public int type;
    public int staticField;
    public boolean isPedestrian;
    protected boolean hasMoved = false;

    public Point() {
        type = 0;
        staticField = 100000;
        neighbors = new ArrayList<Point>();
    }

    public void clear() {
        staticField = 100000;
    }

    public boolean calcStaticField() {
        int minStaticField = findMinNeigh(true).staticField;
        if (staticField > minStaticField + 1) {
            staticField = minStaticField + 1;
            return true;
        }
        return false;
    }



//    Find neighbour with the lowest staticField value
    private Point findMinNeigh(boolean canBeOccupied){
        Point decoy = new Point();
        decoy.staticField = Integer.MAX_VALUE-1;
        if (neighbors.size() == 0){

            return decoy;
        }
        Point minNeigh = decoy;
        for (Point neighbour : neighbors) {
            if (isPointOccupied(neighbour) && !canBeOccupied)
                continue;
            if (neighbour.staticField < minNeigh.staticField)
                minNeigh = neighbour;
        }
        return minNeigh;
    }
    private boolean isPointOccupied(Point point){
        return point.isPedestrian || (point.type == 1) || (point.type == 3);
    }
    public void move() {
        if (!isPedestrian || this.hasMoved)
            return;
        Point minNeigh = findMinNeigh(false);
        this.isPedestrian = false;
        if (minNeigh.type != 2){
            this.type = 0;
            minNeigh.type = 3;
            minNeigh.isPedestrian = true;
        }
        minNeigh.hasMoved = true;
    }

    public void addNeighbor(Point nei) {
        neighbors.add(nei);
    }
}