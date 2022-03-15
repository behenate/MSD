public class Point {

	public Point nNeighbor;
	public Point wNeighbor;
	public Point eNeighbor;
	public Point sNeighbor;
	public float nVel;
	public float eVel;
	public float wVel;
	public float sVel;
	public float pressure;
	private double c = 0.5;
	public static Integer []types ={0,1,2};
	int type;
	int sinInput;

	public Point() {
		clear();
		type=0;
	}

	public void clicked() {
		pressure = 1;
	}
	
	public void clear() {
		nVel = 0;
		eVel = 0;
		wVel = 0;
		sVel = 0;
		pressure = 0;
	}

	public void updateVelocity() {
		if (type == 0){
			nVel = nVel - (nNeighbor.pressure - pressure);
			sVel = sVel - (sNeighbor.pressure - pressure);
			wVel = wVel - (wNeighbor.pressure - pressure);
			eVel = eVel - (eNeighbor.pressure - pressure);
		}
	}

	public void updatePressure() {
		if (type == 0){
			pressure = (float) (pressure - Math.pow(c,2) * (nVel+sVel+wVel+eVel));
		}
		else if (type == 2){
			double radians = Math.toRadians(sinInput);
			pressure = (float) (Math.sin(radians));
			System.out.println("HAlo");
		}
	}


	public float getPressure() {
		return pressure;
	}
}