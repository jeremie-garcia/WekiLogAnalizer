package WekiLogs.logs.gui;

import java.awt.BorderLayout;
import java.awt.Event;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;

import WekiLogs.logs.LogEvent;
import WekiLogs.logs.LogProcessor;

public class LogExplorer extends JFrame {

	// Create the TimelinePanel
	private LogVisualizer visualizer = new LogVisualizer();
	private JLabel fileLabel = new JLabel();
	private String logFile = "./logfilestest/assignment2/assignment2_1B/assignment2_1B.txt";
	private HashMap<String, ArrayList<LogEvent>> eventsMap;

	public LogExplorer() {
		// TODO Auto-generated constructor stub
		super("Weki - Kadenze Log Explorer");

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new BorderLayout());

		// Creating the Menu
		JMenuBar menuBar = this.createMenu();
		this.setJMenuBar(menuBar);
		this.add(visualizer, BorderLayout.CENTER);

		EventInspector inspector = EventInspector.getInstance();
		this.add(inspector, BorderLayout.EAST);

		ConfigurationManager manager = new ConfigurationManager(this.visualizer);
		this.add(manager, BorderLayout.WEST);

		File f = new File(logFile);

		this.buildInterfaceForFile(f);

		setSize(1600, 800);
		setVisible(true);
		this.pack();
	}

	private void buildInterfaceForFile(File file) {
		this.clearGUI();
		if (file.exists()) {
			this.logFile = file.getPath();
			this.eventsMap = LogProcessor.extractEventsFromLogFileAsHashMap(file);
			this.visualizer.buildEventsPanelsFromMap(eventsMap);
			this.visualizer.resetRuler();
		} else {
			this.logFile = "";
			this.eventsMap = null;
		}
	}

	private void clearGUI() {
		this.visualizer.list.removeAll();
		this.pack();
	}

	private JMenuBar createMenu() {
		JMenuBar menuBar = new JMenuBar();
		JMenu menu = new JMenu("File");
		// menu.setBackground(Color.lightGray);
		JMenuItem open = new JMenuItem("Open", KeyEvent.VK_O);
		open.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, Event.CTRL_MASK, true));
		open.setEnabled(true);
		open.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				final JFileChooser fc = new JFileChooser();
				fc.setCurrentDirectory(new File("."));
				FileNameExtensionFilter filter = new FileNameExtensionFilter("txt Files", "txt");
				fc.setFileFilter(filter);
				int returnVal = fc.showOpenDialog(LogExplorer.this);

				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File file = fc.getSelectedFile();
					// This is where a real application would open the file.
					buildInterfaceForFile(file);
				} else {
					System.out.println("Open command cancelled by user.");
				}
			}
		});
		menu.add(open);
		menuBar.add(menu);
		return menuBar;
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				LogExplorer explorer = new LogExplorer();
				explorer.visualizer.resetRuler();
			}
		});
	}
}
