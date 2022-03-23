public class Point {


    private int type = 0;
    private boolean moved = false;
    private int speed = 0;
    private int distanceToNextCar = 0;
    private Point nextPosition;

    public void move() {
        if (this.type == 1 && nextPosition.type == 0 && !this.moved) {
            if (!nextPosition.equals(this)) {
                nextPosition.type = 1;
                this.type = 0;
                nextPosition.speed = speed;
                this.speed = 0;
                nextPosition.moved = true;
            }
            this.moved = true;
        }
    }

    public void clicked() {
        this.type = 1;
    }

    public void clear() {
        this.type = 0;
    }

    //    If speed lower than treshold speed up
    private void speedUp() {
        if (speed < 5) {
            speed++;
        }
    }

    //    If speed higher than distance to next car slow wodn
    private void slowDown() {
        if (speed > distanceToNextCar) {
            speed = distanceToNextCar - 1;
        }
    }

    //    p - how probable (0 to 1) is the car to randomly slow down by 1
    private void randomSlowDown(double p) {
        if (Math.random() > (1 - p) && speed > 0) {
            speed -= 1;
        }
    }

    public void routine(double p) {
        if (this.type == 1) {
            speedUp();
            slowDown();
            randomSlowDown(p);
        }

    }

    public void setMoved(boolean moved) {
        this.moved = moved;
    }

    public int getType() {
        return type;
    }

    public void setDistanceToNextCar(int distance) {
        this.distanceToNextCar = distance;
    }

    public void setNextPosition(Point nextPosition) {
        this.nextPosition = nextPosition;
    }

    public int getSpeed() {
        return speed;
    }
    public void setSpeed(int speed){this.speed = speed;}
}

