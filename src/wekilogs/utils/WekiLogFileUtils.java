package wekilogs.utils;

import java.io.File;

import logs.model.LogEvent;

public class WekiLogFileUtils {

	public static long getLogLineTime(String line) {
		try {
			return Long.parseLong(line.split(",")[0]);
		} catch (Exception e) {
			return 0;
		}

	}

	public static String getLogLineLabel(String line) {
		return line.split(",")[2];
	}

	public static String[] getLogLineArgs(String line) {
		String[] lineSplit = line.split(",");
		int n = lineSplit.length - 3;
		String[] newArray = new String[n];
		System.arraycopy(lineSplit, 3, newArray, 0, n);
		return newArray;
	}

	// retriev the path for the model indicated in the event
	public static String getFileNameForModelFromEvent(LogEvent event) {
		if (event.getLabel().toLowerCase().contains("model_num=")) {
			File f = new File(event.getSource());
			File dir = f.getParentFile();
			if (dir.isDirectory()) {
				return dir.getPath() + "/" + event.getArgs().get(0);
			}
		}
		return null;

	}

	public static String getAssignementStringFromFile(String logFile) {
		File f = new File(logFile);
		String name = f.getName();
		String exercice = name.substring(name.indexOf("_") - 1, name.indexOf('.'));
		return exercice;
	}

}
