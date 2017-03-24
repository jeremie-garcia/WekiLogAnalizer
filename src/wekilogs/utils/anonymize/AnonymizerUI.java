package wekilogs.utils.anonymize;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class AnonymizerUI extends Application {

	AnonymizeConfiguration currentConfig = new AnonymizeConfiguration();
	String configFileName = "";

	TextField rawDataFolderBox;
	TextField correspFileBox;
	TextField zipNameBox;
	TextField assignementNameBox;
	TextField destinationFolderBox;
	CheckBox cleanCb;

	@Override
	public void start(Stage primaryStage) throws Exception {
		primaryStage.setTitle("Anonymizer");

		BorderPane border = new BorderPane();

		GridPane grid = new GridPane();
		grid.setAlignment(Pos.CENTER);
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(25, 25, 25, 25));

		border.setCenter(grid);

		Scene scene = new Scene(border, 400, 400);
		primaryStage.setScene(scene);

		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("JSon files (*.json)", "*.json");

		HBox topBar = new HBox();
		topBar.setPadding(new Insets(15, 12, 15, 12));

		Text scenetitle = new Text("Anonymizer");
		scenetitle.setFont(Font.font("Helvetica", FontWeight.NORMAL, 20));

		Button loadBtn = new Button("Load");
		loadBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent e) {
				final FileChooser fileChooser = new FileChooser();
				fileChooser.getExtensionFilters().add(extFilter);
				fileChooser.setInitialDirectory(new File("./configurations"));
				final File selectedFile = fileChooser.showOpenDialog(primaryStage);
				if (selectedFile != null) {
					openConfigFromFile(selectedFile);
				}
			}
		});
		Button saveBtn = new Button("Save As");
		saveBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent e) {
				final FileChooser fileChooser = new FileChooser();
				fileChooser.getExtensionFilters().add(extFilter);
				fileChooser.setInitialDirectory(new File("./configurations"));
				final File selectedFile = fileChooser.showSaveDialog(primaryStage);
				if (selectedFile != null) {
					try {
						saveConfigToFile(selectedFile);
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
		});
		HBox hbBtn = new HBox(10);
		hbBtn.setAlignment(Pos.CENTER_RIGHT);
		hbBtn.getChildren().add(loadBtn);
		hbBtn.getChildren().add(saveBtn);

		Region spacer = new Region();
		HBox.setHgrow(spacer, Priority.ALWAYS);
		topBar.getChildren().addAll(scenetitle, spacer, hbBtn);
		border.setTop(topBar);

		int rowCnt = 0;

		Label pw = new Label("Raw data folder:");
		grid.add(pw, 0, rowCnt);
		GridPane.setHalignment(pw, HPos.RIGHT);
		rawDataFolderBox = new TextField();
		rawDataFolderBox.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				currentConfig.rawDataFolder = newValue;
			}
		});
		grid.add(rawDataFolderBox, 1, rowCnt);
		Button browseButton = new Button("...");
		browseButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent e) {
				final DirectoryChooser directoryChooser = new DirectoryChooser();
				final File selectedDirectory = directoryChooser.showDialog(primaryStage);
				if (selectedDirectory != null) {
					String dir = selectedDirectory.getAbsolutePath();
					rawDataFolderBox.setText(dir);
				}
			}
		});
		grid.add(browseButton, 2, rowCnt);
		rowCnt++;

		Label correspLbl = new Label("Correspondence File:");
		grid.add(correspLbl, 0, rowCnt);
		GridPane.setHalignment(correspLbl, HPos.RIGHT);
		correspFileBox = new TextField();
		correspFileBox.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				currentConfig.correspondenceFile = newValue;
			}
		});
		grid.add(correspFileBox, 1, rowCnt);
		rowCnt++;

		Label zipLbl = new Label("Zip file name:");
		grid.add(zipLbl, 0, rowCnt);
		GridPane.setHalignment(zipLbl, HPos.RIGHT);
		zipNameBox = new TextField();
		zipNameBox.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				currentConfig.zipFileName = newValue;
			}
		});
		grid.add(zipNameBox, 1, rowCnt);
		rowCnt++;

		Label assignementNameLbl = new Label("Assignement file name:");
		grid.add(assignementNameLbl, 0, rowCnt);
		GridPane.setHalignment(assignementNameLbl, HPos.RIGHT);
		assignementNameBox = new TextField();
		assignementNameBox.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				currentConfig.assignementFileName = newValue;
			}
		});
		grid.add(assignementNameBox, 1, rowCnt);
		rowCnt++;

		Label destinationLbl = new Label("Destination Folder:");
		grid.add(destinationLbl, 0, rowCnt);
		GridPane.setHalignment(destinationLbl, HPos.RIGHT);
		destinationFolderBox = new TextField();
		destinationFolderBox.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				currentConfig.outputFolder = newValue;
			}
		});
		grid.add(destinationFolderBox, 1, rowCnt);
		rowCnt++;

		Label cleanLbl = new Label("Remove unvalid files?");
		grid.add(cleanLbl, 0, rowCnt);
		GridPane.setHalignment(cleanLbl, HPos.RIGHT);
		cleanCb = new CheckBox("");
		cleanCb.selectedProperty().addListener(new ChangeListener<Boolean>() {
			public void changed(ObservableValue<? extends Boolean> ov, Boolean old_val, Boolean new_val) {
				currentConfig.cleaning = new_val;
			}
		});
		grid.add(cleanCb, 1, rowCnt);
		rowCnt++;

		HBox footer = new HBox(20);
		footer.setMinHeight(20);
		footer.setPadding(new Insets(15, 12, 15, 12));
		footer.setAlignment(Pos.CENTER);
		border.setBottom(footer);

		// add anonymize button
		int counter = rowCnt;
		Button btn = new Button("Anonymize");
		btn.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				grid.setDisable(true);
				ProgressBar progressBar = new ProgressBar(0.0);
				Label progressLbl = new Label("");
				footer.getChildren().addAll(progressBar, progressLbl);
				AnonymizeTask anonymizeTask = new AnonymizeTask(currentConfig);
				anonymizeTask.setOnSucceeded(new EventHandler() {
					@Override
					public void handle(Event event) {
						grid.setDisable(false);
						footer.getChildren().removeAll(progressBar, progressLbl);
					}
				});
				progressBar.progressProperty().unbind();
				progressBar.progressProperty().bind(anonymizeTask.progressProperty());
				anonymizeTask.messageProperty().addListener(new ChangeListener<String>() {
					public void changed(ObservableValue<? extends String> observable, String oldValue,
							String newValue) {
						progressLbl.setText(newValue);
					}
				});
				Thread t = new Thread(anonymizeTask);
				t.start();
			}
		});

		HBox hbBtn2 = new HBox(10);
		hbBtn2.setAlignment(Pos.BOTTOM_RIGHT);
		hbBtn2.getChildren().add(btn);
		grid.add(hbBtn2, 1, rowCnt);
		rowCnt++;

		updateFromConfig();
		primaryStage.show();

	}

	protected void saveConfigToFile(File selectedFile) throws IOException {
		if (!selectedFile.exists()) {
			selectedFile.createNewFile();
		}

		if (selectedFile.exists()) {
			String jsString = currentConfig.toJSon();
			FileWriter file = new FileWriter(selectedFile);
			file.write(jsString);
			file.flush();
			file.close();
		}
	}

	protected void openConfigFromFile(File selectedFile) {
		try {
			AnonymizeConfiguration config = AnonymizeConfiguration.fromJSon(selectedFile);
			this.currentConfig = config;
			updateFromConfig();
		} catch (JsonSyntaxException | JsonIOException | FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void updateFromConfig() {
		this.assignementNameBox.setText(this.currentConfig.assignementFileName);
		this.correspFileBox.setText(this.currentConfig.correspondenceFile);
		this.destinationFolderBox.setText(this.currentConfig.outputFolder);
		this.rawDataFolderBox.setText(this.currentConfig.rawDataFolder);
		this.zipNameBox.setText(this.currentConfig.zipFileName);
		this.cleanCb.setSelected(this.currentConfig.cleaning);
	}

	public static void main(String[] args) {
		launch(args);
	}

}
