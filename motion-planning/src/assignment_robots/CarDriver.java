package assignment_robots;


import java.util.HashMap;
import java.util.LinkedList;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Line;
import javafx.scene.Group;
import javafx.stage.Stage;
import javafx.scene.paint.Color;

public class CarDriver extends Application {
	// default window size
	protected int window_width = 600;
	protected int window_height = 400;
	
	// Draw a polygon;
	public void addPolygon(Group g, Double[] points) {
		Polygon p = new Polygon();
	    p.getPoints().addAll(points);
	    
	    g.getChildren().add(p);
	}
	
	// plot a car robot
	public void plotCarRobot(Group g, CarRobot car, CarState s, Color c) {
		//System.out.println(car);
		//System.out.println(s);
		car.set(s);
		double[][] current = car.get();
		Double[] to_add = new Double[2*current.length];
		for (int j = 0; j < current.length; j++) {
			//System.out.println(current[j][0] + ", " + current[j][1]);
			to_add[2*j] = current[j][0];
			//to_add[2*j+1] = current[j][1];
			to_add[2*j+1] = window_height - current[j][1];
		}
		Polygon p = new Polygon();
		p.getPoints().addAll(to_add);
		
		p.setStroke(Color.RED);
		p.setFill(c);
		g.getChildren().add(p);
	}
		
	// plot the World with all the obstacles;
	public void plotWorld(Group g, World w) {
		int len = w.getNumOfObstacles();
		double[][] current;
		Double[] to_add;
		Polygon p;
		for (int i = 0; i < len; i++) {
			current = w.getObstacle(i);
			to_add = new Double[2*current.length];
			for (int j = 0; j < current.length; j++) {
				to_add[2*j] = current[j][0];
				//to_add[2*j+1] = current[j][1];
				to_add[2*j+1] = window_height - current[j][1];
			}
			p = new Polygon();
			p.getPoints().addAll(to_add);
			g.getChildren().add(p);
		}
	}
	
	// The start function; will call the drawing;
	// You can run your PRM or RRT to find the path; 
	// call them in start; then plot the entire path using
	// interfaces provided;
	@Override
	public void start(Stage primaryStage) {
		
		primaryStage.setTitle("CS 76 2D world");
		Group root = new Group();
		Scene scene = new Scene(root, window_width, window_height);

		primaryStage.setScene(scene);
		
		Group g = new Group();
		
		//double a[][] = {{10, 400}, {150, 300}, {100, 210}};
		//Poly obstacle1 = new Poly(a);
		
		//double b[][] = {{350, 10}, {400, 300}, {430, 100}};
		//Poly obstacle2 = new Poly(b);
		
		double c[][] = {{110, 220}, {120, 380}, {300, 380}, {320, 220}};
		Poly obstacle3 = new Poly(c);
		
		double d[][] = {{10, 150}, {150, 150}, {150, 100}, {0, 100}};
		Poly obstacle4 = new Poly(d);
		
		double e[][] = {{300, 120}, {500, 120}, {500, 0}, {300, 0}};
		Poly obstacle5 = new Poly(e);
		
		double f[][] = {{0, 400}, {1, 400}, {1, 0}, {0, 0}};
		Poly obstacle6 = new Poly(f);
		
		// Declaring a world; 
		World w = new World();
		// Add obstacles to the world;
		//w.addObstacle(obstacle1);
		//w.addObstacle(obstacle2);
		w.addObstacle(obstacle3);
		w.addObstacle(obstacle4);
		w.addObstacle(obstacle5);
		w.addObstacle(obstacle6);
			
		plotWorld(g, w);
		
		CarRobot start = new CarRobot();
		CarRobot goal = new CarRobot();
		CarState ss = new CarState(100, 20, 0);
		CarState gs = new CarState(50, 330, Math.PI/2);
		
		start.set(ss);
		goal.set(gs);
		
		plotCarRobot(g, start, start.getCarState(), Color.PINK);
		plotCarRobot(g, goal, goal.getCarState(), Color.PINK);
		
		HashMap<CarRobot, CarRobot> pred = CarRRT.buildRRT(w, start, goal);
		LinkedList<CarRobot> path = CarRRT.backchain(start, goal, pred);
		
		int intensity = 250;
		
		CarRobot prev = path.get(0);
		plotCarRobot(g, prev, prev.getCarState(), Color.rgb(255, intensity, intensity));
		for (int i = 1; i < path.size(); i++) {
			CarRobot car = path.get(i);
			System.out.println(car);
			Line l = new Line();
			l.setStartX(prev.getCarState().getX());
			l.setStartY(400 - prev.getCarState().getY());
			l.setEndX(car.getCarState().getX());
			l.setEndY(400 - car.getCarState().getY());
			l.setStroke(Color.BLACK);
			g.getChildren().add(l);
			plotCarRobot(g, car, car.getCarState(), Color.rgb(255, intensity, intensity));
			intensity -= 3;
			if (intensity < 1) {
				intensity = 200;
			}
			prev = car;
		}
		
//		for (CarRobot car : path) {
//			plotCarRobot(g, car, car.getCarState());
//		}
		
//		int intensity = 200;
//		for (ArmRobot r : path2) {
//			System.out.println(r);
//		plotArmRobot(g, r, Color.rgb(intensity, intensity, 255));
//			intensity -= 10;
//			if (intensity < 1) {
//				intensity = 200;
//			}
//		}
		
	    scene.setRoot(g);
	    primaryStage.show();
		
	}
	public static void main(String[] args) {
		launch(args);
	}
}
