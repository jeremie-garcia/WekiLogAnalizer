package logs.ui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * This is the main class for the GUI It opens a folder by default and displays
 * the content of the first file.
 *
 * @author jeremiegarcia
 *
 */
public class Main extends Application {

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		primaryStage.setTitle("LogVisualizer");

		// Border layout that contains
		// Center : Scene (timelines, time scale and ruler)
		// TOP : menu bar
		// right : Log Inspector
		// left : list of files to display and configuration Inspector
		BorderPane root = new BorderPane();

		// MenuBar
		Menu fileMenu, aboutMenu;
		MenuItem openMenuItem, saveMenuItem, quitMenuItem;
		MenuItem aboutMenuItem;

		fileMenu = new Menu("File");

		openMenuItem = new MenuItem("Open");
		openMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.O, KeyCombination.META_DOWN));

		saveMenuItem = new MenuItem("Save");
		saveMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.META_DOWN));
		saveMenuItem.setDisable(true);

		quitMenuItem = new MenuItem("Quit");
		quitMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.Q, KeyCombination.META_DOWN));
		fileMenu.getItems().addAll(openMenuItem, saveMenuItem, quitMenuItem);

		aboutMenu = new Menu("About");
		MenuBar menuBar = new MenuBar(fileMenu, aboutMenu);
		root.setTop(menuBar);
		//

		// Show the scene containing the root layout.
		Scene scene = new Scene(root);
		primaryStage.setScene(scene);
		primaryStage.show();
	}
}
