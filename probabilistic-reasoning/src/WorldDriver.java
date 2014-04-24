import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class WorldDriver extends Application {
	Robot r;
	WorldView view;
	
	private void initializeWorldView() {
		GridWorld world = new GridWorld("src/grid1.txt", 5, 5);
		this.r = new Robot(world);
		this.view = new WorldView(r);
	}
	
	public void start(Stage primaryStage) {
		initializeWorldView();
		primaryStage.setTitle("CS 76: Probablistic Reasoning");		
		
		// Add everything to root stackpane, then to main window
		StackPane gridView = new StackPane();
		gridView.getChildren().add(view);
		
		Button stepButton = new Button();
		stepButton.setText("Step");
		stepButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				r.update();
				view.updateColorOpacity();
				view.updateRobotLocation();
			}
		});
		
		StackPane buttonPane = new StackPane();
		buttonPane.getChildren().add(stepButton);
		
		BorderPane bp = new BorderPane();
		bp.setCenter(gridView);
		bp.setBottom(buttonPane);
		primaryStage.setScene(new Scene(bp));
		primaryStage.show();
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}