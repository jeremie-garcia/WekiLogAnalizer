package WekiLogs.logs.gui;

import java.awt.BorderLayout;
import java.awt.Event;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.commons.io.FileUtils;

import WekiLogs.logs.LogEvent;
import WekiLogs.logs.LogProcessor;
import WekiLogs.logs.gui.input.TrainingViz;
import WekiLogs.utils.GraderTool;
import wekimini.kadenze.Grade;

public class LogExplorer extends JFrame {

	private static LogExplorer singleton = null;

	// Create the TimelinePanel
	private LogVisualizer visualizer = new LogVisualizer();
	private String logFile = "";
	private HashMap<String, ArrayList<LogEvent>> eventsMap;
	public static TrainingViz trainingViz;

	private DefaultListModel<File> listModel = new DefaultListModel<File>();
	private JList<File> filesList = new JList<File>(listModel);

	private Grade currentGrade;

	private LogExplorer() {
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

		JPanel leftPanel = new JPanel();
		leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.PAGE_AXIS));

		ConfigurationManager manager = new ConfigurationManager(this.visualizer);
		filesList.setCellRenderer(new FileNameListCellRenderer());
		filesList.addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {
				updateFileFromJList();
			}
		});

		leftPanel.add(new JLabel("LogsFiles for submission"));
		leftPanel.add(new JScrollPane(filesList));
		leftPanel.add(manager);

		this.add(leftPanel, BorderLayout.WEST);

		// create the tmp directory if it does not exists
		File tmpDir = new File(Configuration.TMP_DIR);

		if (!tmpDir.exists()) {
			tmpDir.mkdir();
			tmpDir.deleteOnExit();
			System.out.println("Temp directory created " + tmpDir.getPath());
		}

		// load an initial file
		loadFromZipFile(Configuration.DEFAULT_ZIP_FILE);
		// select first log file from extracted files
		this.filesList.setSelectedIndex(0);

		setSize(1600, 800);
		setVisible(true);
		this.pack();
		trainingViz.updateScreenPosition(this.getX(), this.getY() + this.getHeight());

	}

	public static LogExplorer getInstance() {
		if (LogExplorer.singleton == null)
			LogExplorer.singleton = new LogExplorer();

		return LogExplorer.singleton;
	}

	private void updateFileFromJList() {
		// TODO Auto-generated method stub
		// find the file
		File f = filesList.getSelectedValue();
		buildInterfaceForFile(f);
	}

	private void loadFromZipFile(String defaultZipFile) {

		// extract the grade and unzip in TMP folder
		this.currentGrade = GraderTool.unzipAndGetGradeForFile(defaultZipFile);

		String[] extensions = { "txt" };
		Collection<File> files = FileUtils.listFiles(new File(Configuration.TMP_DIR), extensions, true);
		this.listModel.removeAllElements();
		for (File f : files) {
			this.listModel.addElement(f);
		}
	}

	private void buildInterfaceForFile(File file) {
		this.clearGUI();
		if (file.exists()) {
			this.logFile = file.getPath();

			double grade = GraderTool.getScoreGradeForAssignment(this.currentGrade, this.logFile);

			this.eventsMap = LogProcessor.extractEventsFromLogFileAsHashMap(file);
			this.visualizer.buildEventsPanelsFromMap(eventsMap);
			this.visualizer.resetRuler();

			for (String key : this.eventsMap.keySet()) {
				if (key.toLowerCase().contains("model_num")) {
					// open a new window with the visualization
					if (LogExplorer.trainingViz != null) {
						LogExplorer.trainingViz.setVisible(false);
						LogExplorer.trainingViz.dispose();
					}
					LogExplorer.trainingViz = new TrainingViz(key, this.eventsMap.get(key), grade);
					LogExplorer.trainingViz.updateScreenPosition(Math.max(this.getX(), 0),
							Math.max(this.getY() + this.getHeight(), 600));
				}
			}

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
				fc.setCurrentDirectory(new File("./zipFilesTest"));
				FileNameExtensionFilter filter = new FileNameExtensionFilter("Zip Files", "zip");
				fc.setFileFilter(filter);
				int returnVal = fc.showOpenDialog(LogExplorer.this);

				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File file = fc.getSelectedFile();
					// This is where a real application would open the file.
					loadFromZipFile(file.getPath());
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
				LogExplorer explorer = LogExplorer.getInstance();
				explorer.visualizer.resetRuler();
			}
		});
	}
}
