package logs.ui;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import logs.model.LogEventsManager;
import logs.ui.events.LogEventNode;

/**
 * FusionManager is a class that displays all the elements allowing the fusion of LogEvent
 *
 * @author marie, clement and charlelie
 *
 */
public class FusionManager extends VBox{
	
	private LogEventsManager logEventsManager;
	private TimelinesExplorer tlexp;
	
	private Button buttonFusion;
	
	public FusionManager(TimelinesExplorer tlexp){
		super();
		this.tlexp= tlexp;
		this.setPrefSize(300, 0);

		Label title = new Label("Interaction");
		
		buttonFusion= new Button("fusion");
		buttonFusion.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				tlexp.checkFusion();
			}
		});
		
		this.setPadding(new Insets(5, 10, 5, 10));
		this.getChildren().addAll(title,buttonFusion);
	}

}
