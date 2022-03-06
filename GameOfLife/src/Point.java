import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class Point {
	private ArrayList<Point> neighbors;
	private int currentState;
	private int nextState;
	private int numStates = 6;
	private String mode = "standard";

//	Rules
	private final int[][] standard = {{2,3}, {3}};
	private final int[][] cities = {{2,3,4,5}, {4,5,6,7,8}};
	private final int[][] coral = {{4,5,6,7,8},{3}};

	private int[][] currentRule = standard;
	public Point() {
		currentState = 0;
		nextState = 0;
		neighbors = new ArrayList<Point>();
	}

	public void clicked() {
		currentState=(++currentState)%numStates;	
	}
	
	public int getState() {
		return currentState;
	}

	public void setState(int s) {
		currentState = s;
	}
	public void setNextState(int s){nextState = s;}

	public void calculateNewState() {
//		Calculate state based on current mode - rain or standard
		if (Objects.equals(this.mode, "rain")){
			if (currentState > 0 && neighbors.size() > 0 && neighbors.get(0).currentState == 0){
				neighbors.get(0).setNextState(6);
			} else if (currentState > 0){
				nextState = currentState-1;
			}
		}else {
			int cnt = countAliveNeighbours();
			if (currentState == 0 && valueIn(cnt, currentRule[1])){
				nextState = 1;
			}else if (currentState == 1 && valueIn(cnt, currentRule[0])){
				nextState = 1;
			}else {
				nextState = 0;
			}
		}

	}

	public void changeState() {
		currentState = nextState;
	}
	
	public void addNeighbor(Point nei) {
		neighbors.add(nei);
	}


	private int countAliveNeighbours(){
		int cnt = 0;
		for (Point neighbour: neighbors) {
			if (neighbour.currentState == 1){
				cnt+=1;
			}
		}
		return cnt;
	}

//	Randomly drops a raindrop
	public void drop(){
		if (Math.random() < 0.05){
			nextState = 6;
		}
	}

//	Sets point mode
	public void setMode(String mode){
		this.mode = mode;
	}

//	Clears the neighbours
	public void clearNeighbours(){
		neighbors.clear();
	}

//	Sets current rules based on input string
	public void setRules(String rule){
		if (Objects.equals(rule, "cities")){
			currentRule = cities;
		}else if (Objects.equals(rule, "coral")){
			currentRule = coral;
		}else {
			currentRule = standard;
		}
	}
//  Helper, checks if provided int is in int array
	private static boolean valueIn(int value, int[] arr){
		for (int i = 0; i < arr.length; i++) {
			if (arr[i]==value){
				return true;
			}
		}
		return false;
	}

//	Sets rule based on provided string
	public void setRule(String rule){
		if (Objects.equals(rule, "cities")){
			this.currentRule = cities;
		}else if (Objects.equals(rule, "coral")){
			this.currentRule = coral;
		}else {
			this.currentRule = standard;
		}
	}
	//TODO: write method counting all active neighbors of THIS point
}
