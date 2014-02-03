package assignment_robots;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.shape.Polygon;
import javafx.scene.Group;
import javafx.stage.Stage;
import javafx.scene.paint.Color;
import assignment_robots.ArmPRM;

public class ArmDriver extends Application {
	// default window size
	protected int window_width = 600;
	protected int window_height = 400;
	
	public void addPolygon(Group g, Double[] points) {
		Polygon p = new Polygon();
	    p.getPoints().addAll(points);
	    
	    g.getChildren().add(p);
	}
	
	// plot a ArmRobot;
	public void plotArmRobot(Group g, ArmRobot arm, Color c) {
		double[][] current;
		Double[] to_add;
		Polygon p;
		for (int i = 1; i <= arm.getLinks(); i++) {
			current = arm.getLinkBox(i);
			
			to_add = new Double[2*current.length];
			for (int j = 0; j < current.length; j++) {
				to_add[2*j] = current[j][0];
				to_add[2*j+1] = window_height - current[j][1];
			}
			p = new Polygon();
			p.getPoints().addAll(to_add);
			p.setStroke(Color.BLUE);
			p.setFill(c);
			g.getChildren().add(p);
		}
		
	}
	
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
		
		
		// setting up javafx graphics environments;
		primaryStage.setTitle("CS 76 2D world");

		Group root = new Group();
		Scene scene = new Scene(root, window_width, window_height);

		primaryStage.setScene(scene);
		
		Group g = new Group();

		// setting up the world;
		
		// creating polygon as obstacles;
		

		double a[][] = {{10, 400}, {150, 300}, {100, 210}};
		Poly obstacle1 = new Poly(a);
		
		double b[][] = {{350, 30}, {300, 200}, {430, 125}};

		Poly obstacle2 = new Poly(b);
		
		double c[][] = {{110, 220}, {250, 380}, {320, 220}};
		Poly obstacle3 = new Poly(c);
		
		double bb[][] = {{0, 0}, {0, 2}, {600, 2}, {600, 0}};
		Poly bottom_border = new Poly(bb);
		
		double lb[][] = {{0, 400}, {2, 400}, {2, 0}, {0, 0}};
		Poly left_border = new Poly(lb);
		
		// Declaring a world; 
		World w = new World();
		// Add obstacles to the world;
		w.addObstacle(obstacle1);
		w.addObstacle(obstacle2);
		w.addObstacle(obstacle3);
		w.addObstacle(bottom_border);
		w.addObstacle(left_border);
		
		plotWorld(g, w);
		
		ArmRobot arm1 = new ArmRobot(2);
		ArmRobot arm2 = new ArmRobot(2);
		
		double[] config1 = {0, 0};
		double[] config2 = {0, 1};
		
		arm1.set(config1);
		arm2.set(config2);
		
		HashMap<ArmRobot, ArrayList<ArmRobot>> PRM = ArmPRM.buildPRM(w);
		System.out.println(PRM.size());
		
		ArmRobot startNode = ArmPRM.getClosestPRMNode(PRM, arm1);
		ArmRobot goalNode = ArmPRM.getClosestPRMNode(PRM, arm2);
		
		//LinkedList<ArmRobot> path1 = new LinkedList<ArmRobot>();
		//path1.addFirst(arm1);
		//ArmPRM.appendLocalPath(path1, arm1, startNode);
		//path1.addFirst(startNode);
		
		//System.out.println(startNode);
		//System.out.println(goalNode);
				
		LinkedList<ArmRobot> path2 = ArmPRM.getBFSPath(PRM, startNode, goalNode);
		
		int intensity = 200;
		for (ArmRobot r : path2) {
			System.out.println(r);
			plotArmRobot(g, r, Color.rgb(intensity, intensity, 255));
			intensity -= 10;
		}
		
		//plotArmRobot(g, startNode);
		//plotArmRobot(g, goalNode);
		
		
	    scene.setRoot(g);
	    primaryStage.show();
	}
	
	
	public static void main(String[] args) {
		launch(args);
	}
}


//// Plan path between two configurations;
//ArmLocalPlanner ap = new ArmLocalPlanner();
//
//// get the time to move from config1 to config2;
//double time = ap.moveInParallel(config1, config2);
//System.out.println(time);
//
//boolean result;
//result = w.armCollisionPath(arm, config1, config2);
//System.out.println(result);
// plot robot arm

//plotArmRobot(g, arm, config2);
//plotArmRobot(g, arm, config1); 