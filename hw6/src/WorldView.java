import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.layout.Pane;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class WorldView extends Application {
	private Color[][] colors;
	private double[][] locPr;	// probabilities of being at each location in grid
	private int pixelsPerSquare = 24;
	
	
	public static void main(String[] args) {
		launch(args);
	}
	
	@Override
	public void start(Stage primaryStage) {
		primaryStage.setTitle("CS 76: Probabilistic Reasoning");
		Pane root = new Pane();
		
		
		// Test colors
		Color[][] colors = new Color[4][4];
		colors[0][0] = Color.RED;
		colors[0][1] = Color.YELLOW;
		colors[0][2] = Color.BLUE;
		colors[0][3] = Color.RED;
		colors[1][0] = Color.RED;
		colors[1][1] = Color.GREEN;
		colors[1][2] = Color.GREEN;
		colors[1][3] = Color.YELLOW;
		colors[2][0] = Color.YELLOW;
		colors[2][1] = Color.YELLOW;
		colors[2][2] = Color.RED;
		colors[2][3] = Color.RED;
		colors[3][0] = Color.BLUE;
		colors[3][1] = Color.BLUE;
		colors[3][2] = Color.BLUE;
		colors[3][3] = Color.GREEN;
		
		for (int c = 0; c < 4; c++) {
			for (int r = 0; r < 4; r++) {

				int x = c * pixelsPerSquare;
				int y = (4 - r - 1) * pixelsPerSquare;

				Rectangle square = new Rectangle(x, y, pixelsPerSquare,
						pixelsPerSquare);

				square.setStroke(Color.BLACK);
				square.setFill(colors[c][r]);

				root.getChildren().add(square);	
			}
		}
		
		primaryStage.setScene(new Scene(root, 4*pixelsPerSquare, 4*pixelsPerSquare));
		primaryStage.show();
	}
}