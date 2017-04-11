package klogs.ui;

import java.io.File;
import java.util.Collection;

import org.apache.commons.io.FileUtils;

import com.sun.webkit.InspectorClient;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.util.Callback;
import klogs.KLogConfiguration;
import klogs.KLogEventsManager;
import klogs.utils.GraderTool;
import logs.config.Configuration;
import logs.model.LogEventsManager;
import logs.ui.EventInspector;
import logs.ui.TimelinesExplorer;
import logs.utils.FileCellRenderer;
import wekimini.kadenze.Grade;

/**
 * This is the main class for the GUI It opens a folder by default and displays
 * the content of the first file.
 *
 * @author jeremiegarcia
 *
 */
public class MainUI extends Application {
	private String logFile = "";
	private LogEventsManager logEventsManager;
	private Grade currentGrade;

	private TimelinesExplorer timelinesExplorer;

	private MainUI app;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		primaryStage.setTitle("Event Logs Visualizer");

		// Border layout that contains
		// Center : Scene (timelines, time scale and ruler)
		// TOP : menu bar
		// right : Log Inspector
		// left : list of files to display and configuration Inspector
		BorderPane root = new BorderPane();

		// MenuBar
		MenuBar menuBar = createMenuBar(primaryStage);
		root.setTop(menuBar);

		// file selector
		VBox fileSelector = createFileSelector();
		root.setLeft(fileSelector);

		// right pane (inspector)
		EventInspector inspector = EventInspector.getInstance();
		root.setRight(inspector);

		// central pane (main scene)
		this.logEventsManager = new KLogEventsManager();
		timelinesExplorer = new TimelinesExplorer(this.logEventsManager);
		root.setCenter(timelinesExplorer);

		// Show the scene containing the root layout.
		Scene scene = new Scene(root);
		primaryStage.setScene(scene);
		primaryStage.show();

		// create the tmp directory if it does not exists
		File tmpDir = new File(Configuration.TMP_DIR);

		if (!tmpDir.exists()) {
			tmpDir.mkdir();
			tmpDir.deleteOnExit();
			System.out.println("Temp directory created " + tmpDir.getPath());
		}

		// load an initial file
		this.loadFromZipFile(new File(KLogConfiguration.DEFAULT_ZIP_FILE));
		// select first log file from extracted files
		this.filesListView.getSelectionModel().select(0);
	}

	ObservableList<File> filesList = FXCollections.observableArrayList();
	ListView<File> filesListView;
	Label selectedZipFileLabel = new Label("");

	/**
	 * open a zip file at the given pat, extract the data to the tmp filder and
	 * get the grade. It also populates the filelist with the resulting files.
	 *
	 * @param zipFilePath
	 */
	private void loadFromZipFile(File zipFile) {

		// extract the grade and unzip in TMP folder
		this.currentGrade = GraderTool.unzipAndGetGradeForFile(zipFile);
		String[] elements = zipFile.getParent().split("/");
		String name = elements[elements.length - 2] + "/" + elements[elements.length - 1];
		this.selectedZipFileLabel.setText(name);
		String[] extensions = { "txt" };
		Collection<File> files = FileUtils.listFiles(new File(Configuration.TMP_DIR), extensions, true);
		this.filesList.clear();
		this.filesList.addAll(files);
	}

	/**
	 * returns a form containing the file selector
	 *
	 * @return
	 */
	private VBox createFileSelector() {
		// leftpane (files and parameters)
		VBox leftBox = new VBox();
		leftBox.setPadding(new Insets(5, 10, 5, 10));
		Label listLabel = new Label("Logs Files");
		this.filesListView = new ListView<File>();
		this.filesListView.setItems(filesList);
		this.filesListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<File>() {
			@Override
			public void changed(ObservableValue<? extends File> observable, File oldValue, File newValue) {
				updateFileFromList(newValue);
			}
		});

		this.filesListView.setCellFactory(new Callback<ListView<File>, ListCell<File>>() {
			@Override
			public ListCell<File> call(ListView<File> param) {
				return new FileCellRenderer();
			}
		});

		leftBox.getChildren().addAll(selectedZipFileLabel, listLabel, filesListView);
		return leftBox;
	}

	/**
	 * update the visualization to reflect a file selection change
	 *
	 * @param file
	 */

	private void updateFileFromList(File file) {
		if (file != null) {
			if (file.exists()) {
				this.logEventsManager.setLogFile(file);
				this.timelinesExplorer.update();
			}
		}
	}

	/**
	 * Create the menu bar and binds the open close events
	 */

	private Menu fileMenu, aboutMenu;
	private MenuItem openMenuItem, saveMenuItem, quitMenuItem, aboutMenuItem;

	private MenuBar createMenuBar(Stage stage) {

		fileMenu = new Menu("File");

		openMenuItem = new MenuItem("Open");
		openMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.O, KeyCombination.META_DOWN));
		openMenuItem.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				FileChooser fileChooser = new FileChooser();
				fileChooser.setTitle("Open Resource File");
				fileChooser.setInitialDirectory(new File("./zipFilesTest"));
				fileChooser.getExtensionFilters().addAll(new ExtensionFilter("Zip Files", "*.zip"));
				File selectedFile = fileChooser.showOpenDialog(stage);
				if (selectedFile != null) {
					loadFromZipFile(selectedFile);
				}

			}
		});

		saveMenuItem = new MenuItem("Save");
		saveMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.META_DOWN));
		saveMenuItem.setDisable(true);

		quitMenuItem = new MenuItem("Quit");
		quitMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.Q, KeyCombination.META_DOWN));
		quitMenuItem.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				Platform.exit();
			}
		});
		fileMenu.getItems().addAll(openMenuItem, saveMenuItem, quitMenuItem);

		aboutMenu = new Menu("About");
		aboutMenuItem = new MenuItem("About");
		aboutMenuItem.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				Alert alert = new Alert(AlertType.INFORMATION);
				alert.setTitle("About WekiLog Visualizer");
				alert.setHeaderText("WekiLog Visualizer - 2016-2017 - ENAC");
				alert.setContentText("developped by....\n" + "blabla");
				alert.showAndWait();
			}
		});
		aboutMenu.getItems().add(aboutMenuItem);

		MenuBar menuBar = new MenuBar(fileMenu, aboutMenu);
		menuBar.useSystemMenuBarProperty().set(true);
		return menuBar;
	}
}
