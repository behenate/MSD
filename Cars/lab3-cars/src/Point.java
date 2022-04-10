import javax.xml.stream.FactoryConfigurationError;

public class Point {

    int[] speeds = {0,3,5,7,0,0,0,0,0};
    public static Integer[] types = {0, 1, 2, 3, 5};
    private int type = 0;
    private boolean moved = false;
    private int speed = 0;
    protected int[] distanceSameLane = {1000,1000};
    protected int[] distanceSideLane = {1000,1000};
    protected Point[] neighsSameLane = {null, null};
    protected Point[] neighsSideLane = {null, null};
    protected int lane = 1;
    private Point nextPosition;

    public void move() {
        if ((this.type == 1 || this.type == 2 || this.type == 3) && nextPosition!=null && nextPosition.type == 0 && !this.moved) {
            if (!nextPosition.equals(this)) {
                nextPosition.type = this.type;
                this.type = 0;
                nextPosition.speed = speed;
                this.speed = 0;
                nextPosition.moved = true;
            }
            this.moved = true;
        }
    }

    public void clicked() {
        this.type = 0;
    }

    public void clear() {
        this.type = 0;
    }

    //    If speed lower than treshold speed up
    private void speedUp() {
        if (speed < speeds[type]) {
            speed++;
        }
    }

    //    If speed higher than distance to next car slow wodn
    private void slowDown() {
        if (speed >= distanceSameLane[1]) {
            speed = distanceSameLane[1]-1;
        }
    }

    //    p - how probable (0 to 1) is the car to randomly slow down by 1
    private void randomSlowDown(double p) {
        if (Math.random() > (1 - p) && speed > 0) {
            speed -= 1;
        }
    }

    public void routine(double p) {
        if ((this.type == 1 || this.type == 2 || this.type ==3) && !this.moved ) {
            speedUp();
            slowDown();
//            randomSlowDown(p);<-- not used in the extended model
        }

    }

    public boolean getMoved(){
        return this.moved;
    }
    public void setMoved(boolean moved) {
        this.moved = moved;
    }

    public int getType() {
        return type;
    }
    public void setType(int type){
        this.speed = speeds[type];
        this.type = type;
    }

    public void setDistanceToNextCar(int distance) {
        this.distanceSameLane[1] = distance;
    }

    public void setNextPosition(Point nextPosition) {
        this.nextPosition = nextPosition;
    }

    public int getSpeed() {
        return speed;
    }
    public int getMaxSpeed(){
        return speeds[type];
    }
    public void setSpeed(int speed){this.speed = speed;}
}

