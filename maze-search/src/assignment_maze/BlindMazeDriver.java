package assignment_maze;

import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;
import java.util.Iterator;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import assignment_maze.SearchProblem.SearchNode;
import assignment_maze.BlindMazeProblem.Location;
import assignment_maze.BlindMazeProblem.BlindMazeNode;

public class BlindMazeDriver extends Application {

	Maze maze;
	
	// instance variables used for graphical display
	private static final int PIXELS_PER_SQUARE = 32;
	MazeView mazeView;
	List<AnimationPath> animationPathList;
	
	// some basic initialization of the graphics; needs to be done before 
	//  runSearches, so that the mazeView is available
	private void initMazeView() {
		maze = Maze.readFromFile("maze5.maz");
		
		animationPathList = new ArrayList<AnimationPath>();
		// build the board
		mazeView = new MazeView(maze, PIXELS_PER_SQUARE);
	}
	
	// assumes maze and mazeView instance variables are already available
	private void runSearches() {
		BlindMazeProblem bp = new BlindMazeProblem(maze);
		List<SearchNode> blindPath = bp.astarSearch();
		for (SearchNode s : blindPath) {
			BlindMazeNode b = (BlindMazeNode) s;
			System.out.println(b);
		}
		
		animationPathList.add(new AnimationPath(mazeView, blindPath));
		bp.printStats();
	}


	public static void main(String[] args) {
		launch(args);
	}

	// javafx setup of main view window for mazeworld
	@Override
	public void start(Stage primaryStage) {
		
		initMazeView();
	
		primaryStage.setTitle("CS 76 Mazeworld");

		// add everything to a root stackpane, and then to the main window
		StackPane root = new StackPane();
		root.getChildren().add(mazeView);
		primaryStage.setScene(new Scene(root));

		primaryStage.show();

		// do the real work of the driver; run search tests
		runSearches();

		// sets mazeworld's game loop (a javafx Timeline)
		Timeline timeline = new Timeline(1.0);
		timeline.setCycleCount(Timeline.INDEFINITE);
		timeline.getKeyFrames().add(
				new KeyFrame(Duration.seconds(.1), new GameHandler()));
		timeline.playFromStart();

	}

	// every frame, this method gets called and tries to do the next move
	//  for each animationPath.
	private class GameHandler implements EventHandler<ActionEvent> {

		@Override
		public void handle(ActionEvent e) {
			// System.out.println("timer fired");
			for (AnimationPath animationPath : animationPathList) {
				// note:  animationPath.doNextMove() does nothing if the
				//  previous animation is not complete.  If previous is complete,
				//  then a new animation of a piece is started.
				animationPath.doNextMove();
			}
		}
	}

	// each animation path needs to keep track of some information:
	// the underlying search path, the "piece" object used for animation,
	// etc.
	private class AnimationPath {
		private List<SearchNode> searchPath;
		private int currentMove = 1;

		public AnimationPath(MazeView mazeView, List<SearchNode> path) {
			searchPath = path;
			//BlindMazeNode firstNode = (BlindMazeNode) searchPath.get(0);
			//HashSet<Location> allPieces = firstNode.state;
			//drawPieces(allPieces);
		}

		// try to do the next step of the animation. Do nothing if
		// the mazeView is not ready for another step.
		public void doNextMove() {

			// animationDone is an instance variable that is updated
			//  using a callback triggered when the current animation
			//  is complete
			
			mazeView.clearPieces();
			if (currentMove < searchPath.size()) {
				BlindMazeNode mazeNode = (BlindMazeNode) searchPath
						.get(currentMove);
				HashSet<Location> pieces = mazeNode.state;
				drawPieces(pieces);
				currentMove++;
			}
		}
		
		public void drawPieces(HashSet<Location> pieces) {	
			Iterator<Location> iter = pieces.iterator();
			Location l;
			
			while (iter.hasNext()) {
				l = iter.next();
				mazeView.addPiece(l.x, l.y);
			}
		}
	}
}