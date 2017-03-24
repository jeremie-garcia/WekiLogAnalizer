package logs.ui.oldswing;

import java.awt.Color;
import java.awt.Component;
import java.io.File;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

public class FileNameListCellRenderer extends JLabel implements ListCellRenderer {

	@Override
	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
			boolean cellHasFocus) {
		setText(((File) value).getName());

		Color background;
		Color foreground;

		if (isSelected) {
			background = Color.BLUE;
			foreground = Color.BLUE;
		} else {
			background = Color.BLACK;
			foreground = Color.BLACK;
		}

		setBackground(background);
		setForeground(foreground);
		return this;
	}
}
