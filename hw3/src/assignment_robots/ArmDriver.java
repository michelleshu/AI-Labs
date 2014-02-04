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
		

		//double a[][] = {{40, 310}, {80, 310}, {80, 100}, {40, 100}};
		//Poly obstacle1 = new Poly(a);
		
		//double b[][] = {{220, 190}, {400, 190}, {400, 150}, {220, 150}};
		//Poly obstacle2 = new Poly(b);
		
		double c[][] = {{290, 50}, {300, 120}, {370, 50}};
		Poly obstacle3 = new Poly(c);
		
		double d[][] = {{370, 280}, {520, 280}, {460, 230}};
		Poly obstacle4 = new Poly(d);
		
		double e[][] = {{200, 280}, {350, 280}, {260, 230}};
		Poly obstacle5 = new Poly(e);
		
		double bb[][] = {{0, 0}, {0, 2}, {600, 2}, {600, 0}};
		Poly bottom_border = new Poly(bb);
		
		double lb[][] = {{0, 400}, {2, 400}, {2, 0}, {0, 0}};
		Poly left_border = new Poly(lb);
		
		double rb[][] = {{598, 400}, {600, 400}, {600, 0}, {598, 0}};
		Poly right_border = new Poly(rb);
		
		// Declaring a world; 
		World w = new World();
		// Add obstacles to the world
		//w.addObstacle(obstacle1);
		//w.addObstacle(obstacle2);
		w.addObstacle(obstacle3);
		w.addObstacle(obstacle4);
		w.addObstacle(obstacle5);
		w.addObstacle(bottom_border);
		w.addObstacle(left_border);
		w.addObstacle(right_border);
		
		plotWorld(g, w);
		
		ArmRobot arm1 = new ArmRobot(4);
		ArmRobot arm2 = new ArmRobot(4);
		
		double[] config1 = {Math.PI/3, Math.PI + 0.5, Math.PI - 0.5, Math.PI + 0.5};
		double[] config2 = {2 * Math.PI/3, Math.PI/5, Math.PI/5, 0};
		
		arm1.set(config1);
		arm2.set(config2);
		
		HashMap<ArmRobot, ArrayList<ArmRobot>> PRM = ArmPRM.buildPRM(w);
		
		plotArmRobot(g, arm1, Color.WHITE);
		plotArmRobot(g, arm2, Color.BLUE);
		
		ArmRobot startNode = ArmPRM.getClosestPRMNode(PRM, arm1);
		ArmRobot goalNode = ArmPRM.getClosestPRMNode(PRM, arm2);
		
		plotArmRobot(g, startNode, Color.WHITE);
		plotArmRobot(g, goalNode, Color.BLUE);
				
		LinkedList<ArmRobot> path2 = ArmPRM.getBFSPath(PRM, startNode, goalNode);
		if (path2 == null) {
			System.out.println("No path found");
			return;
		}
		
		int intensity = 200;
		for (ArmRobot r : path2) {
			System.out.println(r);
		plotArmRobot(g, r, Color.rgb(intensity, intensity, 255));
			intensity -= 10;
			if (intensity < 1) {
				intensity = 200;
			}
		}
				
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