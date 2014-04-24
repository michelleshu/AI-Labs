import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class WorldView extends Group {
	public Color[][] colors;
	public Rectangle[][] gridSquares;
	public Robot robot;
	public Circle robotCircle;
	public final int PIXELS_PER_SQUARE = 40;
	
	public WorldView(Robot r) {
		this.robot = r;
		int rows = robot.grid.height;
		int cols = robot.grid.width;
		this.colors = new Color[rows][cols];
		this.gridSquares = new Rectangle[rows][cols];
		int x, y;
		
		// Add colored grid squares
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				colors[i][j] = colorFromChar(robot.grid.colors[i][j]);
				x = j * PIXELS_PER_SQUARE;
				y = i * PIXELS_PER_SQUARE;
				
				Rectangle square = new Rectangle(x, y, PIXELS_PER_SQUARE, PIXELS_PER_SQUARE);
				square.setStroke(Color.BLACK);
				square.setFill(colors[i][j]);
				gridSquares[i][j] = square;

				this.getChildren().add(square);	
			}
		}
		
		// Add robot's actual location as a circle
		robotCircle = new Circle(1, 1, PIXELS_PER_SQUARE / 2, Color.TRANSPARENT);
		robotCircle.setStroke(Color.BLACK);
		this.getChildren().add(robotCircle);
	}
	
	private Color colorFromChar(char c) {
		if (c == 'R') {
			return new Color(1, 0, 0, 1);
		} else if (c == 'G') {
			return new Color(0, 1, 0, 1);
		} else if (c == 'B') {
			return new Color(0, 0, 1, 1);
		} else if (c == 'Y') {
			return new Color(1, 1, 0, 1);
		} else if (c == '.') {
			return new Color(0.75, 0.75, 0.75, 1); // gray
		} else {
			return null;
		}
	}
	
	public void updateColorOpacity() {
		double[][] opacity = robot.belief;
		for (int i = 0; i < colors.length; i++) {
			for (int j = 0; j < colors[0].length; j++) {
				Color c = colors[i][j];
				colors[i][j] = new Color(c.getRed(), c.getGreen(), c.getBlue(), 
						opacity[i][j]);
				gridSquares[i][j].setFill(colors[i][j]);
			}
		}
	}
	
	public void updateRobotLocation() {
		int yLoc = (robot.sensor.y * PIXELS_PER_SQUARE) + (PIXELS_PER_SQUARE / 2);
		int xLoc = (robot.sensor.x * PIXELS_PER_SQUARE) + (PIXELS_PER_SQUARE / 2);
		robotCircle.setCenterX(xLoc);
		robotCircle.setCenterY(yLoc);
	}
}