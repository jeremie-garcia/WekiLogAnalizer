package WekiLogs.logs;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;

import WekiLogs.utils.Utils;

public class LogProcessor {

	/**
	 * Returns all events from a log file as a list of event
	 *
	 * @param logFile
	 * @return
	 */
	public static ArrayList<LogEvent> extractEventsFromLogFileAsArrayList(File logFile) {
		ArrayList<LogEvent> events = new ArrayList<>();

		FileInputStream fstream;
		try {
			fstream = new FileInputStream(logFile);

			BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
			String line, label;
			long timeStamp;
			String[] args;
			while ((line = br.readLine()) != null) {
				label = Utils.getLogLineLabel(line);
				timeStamp = Utils.getLogLineTime(line);
				args = Utils.getLogLineArgs(line);
				LogEvent event = new LogEvent(label, timeStamp, new ArrayList<String>(Arrays.asList(args)),
						logFile.getPath());
				events.add(event);
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return events;
	}

	/**
	 * Returns the events of a log file as a dictionary with lael as the keys
	 *
	 * @param logFile
	 * @return
	 */
	public static HashMap<String, ArrayList<LogEvent>> extractEventsFromLogFileAsHashMap(File logFile) {
		HashMap<String, ArrayList<LogEvent>> map = new HashMap<String, ArrayList<LogEvent>>();

		FileInputStream fstream;
		try {
			fstream = new FileInputStream(logFile);

			BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
			String line, label;
			long timeStamp;
			String[] args;
			while ((line = br.readLine()) != null) {
				label = Utils.getLogLineLabel(line);
				timeStamp = Utils.getLogLineTime(line);
				args = Utils.getLogLineArgs(line);
				LogEvent event = new LogEvent(label, timeStamp, new ArrayList<String>(Arrays.asList(args)),
						logFile.getPath());
				if (map.containsKey(label)) {
					map.get(label).add(event);
				} else {
					ArrayList<LogEvent> new_list = new ArrayList<LogEvent>();
					new_list.add(event);
					map.put(label, new_list);
				}
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return map;
	}

	public static long getFirstTimeForFile(File logFile) {
		FileInputStream fstream;
		long timeStamp = -1;
		try {
			fstream = new FileInputStream(logFile);

			BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
			String line, label;

			String[] args;

			if ((line = br.readLine()) != null) {
				timeStamp = Utils.getLogLineTime(line);
			}

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return timeStamp;
	}

	public static long getFirstTimeFromMap(HashMap<String, ArrayList<LogEvent>> map) {
		return map.get("STARTLOG").get(0).getTimeStamp();
	}

	public static long getLastTimeFromMap(HashMap<String, ArrayList<LogEvent>> map) {
		// should be stop log but can also be Project closed (last instance)
		if (map.containsKey("STOPLOG")) {
			return map.get("STOPLOG").get(0).getTimeStamp();
		} else if (map.containsKey("PROJECT_CLOSED")) {
			return map.get("PROJECT_CLOSED").get(map.get("PROJECT_CLOSED").size() - 1).getTimeStamp();
		} else {
			return 1000;
		}

	}
}
