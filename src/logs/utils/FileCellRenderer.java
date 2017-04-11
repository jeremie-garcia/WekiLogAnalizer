package logs.utils;

import java.io.File;

import javafx.scene.control.ListCell;

/**
 * Utilisties to render a logFIle in a ListView
 *
 * @author jeremiegarcia
 *
 */
public class FileCellRenderer extends ListCell<File> {

	@Override
	public void updateItem(File item, boolean empty) {
		super.updateItem(item, empty);

		if (empty || item == null) {
			setText(null);
			setGraphic(null);
		} else {
			setText(item.getName());
		}
	}
}
