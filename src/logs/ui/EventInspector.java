package logs.ui;

import java.awt.Dimension;
import java.util.Date;

import javax.swing.Box;
import javax.swing.JLabel;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import logs.model.LogEvent;
import wekilogs.trainingdata.ui.SimpleTrainingVizKNN;

public class EventInspector extends VBox {

	public static EventInspector singleton = null;

	private TextField dateTxt = new TextField();
	private TextField typeTxt = new TextField();
	private TextArea argsTxt = new TextArea();

	private LogEvent currentEvent = null;

	private Node eventNode;

	private EventInspector() {
		super();
		this.setPrefSize(200, 0);

		Label title = new Label("Inspector");

		Label date = new Label("Date");
		dateTxt.setMaxSize(200, 40);

		Label type = new Label("Type");
		typeTxt.setMaxSize(200, 40);

		Label args = new Label("Args");

		this.setPadding(new Insets(5, 10, 5, 10));

		this.getChildren().addAll(title, date, dateTxt, type, typeTxt, args, argsTxt);

	}

	public static EventInspector getInstance() {
		if (singleton == null)
			singleton = new EventInspector();
		return singleton;
	}

	public void update(LogEvent event) {

		if (event != currentEvent) {
			this.dateTxt.setText(new Date(event.getTimeStamp()).toString());
			this.typeTxt.setText(event.getLabel());
			this.argsTxt.setText(event.getArgs().toString());
			currentEvent = event;

			if (this.eventNode != null) {
				this.getChildren().remove(eventNode);
				this.eventNode = null;
			}

			if (event.hasInspectorNode()) {
				this.eventNode = event.getInspectorNode();
				this.getChildren().add(this.eventNode);
			}
		}

	}
}
