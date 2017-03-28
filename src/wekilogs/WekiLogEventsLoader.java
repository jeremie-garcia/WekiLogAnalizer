package wekilogs;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import logs.model.LogEvent;
import logs.model.LogEventsLoader;
import logs.utils.LogFileUtils;
import wekilogs.utils.WekiLogFileUtils;

/**
 * This classes offers methods to load log files from wekinator as Lists and
 * Maps of DiscreteLogEvents
 *
 * @author jeremiegarcia
 *
 */
public class WekiLogEventsLoader extends LogEventsLoader {

	/**
	 * Returns all events from a log file as a list of events
	 *
	 * @param logFile
	 * @return
	 */
	public ArrayList<LogEvent> extractEventsFromLogFileAsArrayList(File logFile) {
		ArrayList<LogEvent> events = new ArrayList<>();

		FileInputStream fstream;
		try {
			fstream = new FileInputStream(logFile);

			BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
			String line, label;
			long timeStamp;
			String[] args;
			while ((line = br.readLine()) != null) {
				label = WekiLogFileUtils.getLogLineLabel(line);
				timeStamp = WekiLogFileUtils.getLogLineTime(line);
				args = WekiLogFileUtils.getLogLineArgs(line);
				LogEvent event = new LogEvent(label, timeStamp, 0, new ArrayList<String>(Arrays.asList(args)),
						logFile.getPath());
				events.add(event);

			}
			br.close();
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
	 * Returns the events of a log file as a dictionary with label as the keys
	 *
	 * @param logFile
	 * @return
	 */
	public HashMap<String, ArrayList<LogEvent>> extractEventsFromLogFileAsHashMap(File logFile) {
		HashMap<String, ArrayList<LogEvent>> map = new HashMap<String, ArrayList<LogEvent>>();

		FileInputStream fstream;
		try {
			fstream = new FileInputStream(logFile);

			BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
			String line, label;
			long timeStamp;
			String[] args;
			while ((line = br.readLine()) != null) {
				label = WekiLogFileUtils.getLogLineLabel(line);
				timeStamp = WekiLogFileUtils.getLogLineTime(line);
				args = WekiLogFileUtils.getLogLineArgs(line);
				LogEvent event = new LogEvent(label, timeStamp, 0, new ArrayList<String>(Arrays.asList(args)),
						logFile.getPath());
				if (map.containsKey(label)) {
					map.get(label).add(event);
				} else {
					ArrayList<LogEvent> new_list = new ArrayList<LogEvent>();
					new_list.add(event);
					map.put(label, new_list);
				}
			}
			br.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return map;
	}

	/**
	 * Returns the first time contained in the file
	 *
	 * @param logFile
	 * @return
	 */
	public long getFirstTimeForFile(File logFile) {
		FileInputStream fstream;
		long timeStamp = -1;
		try {
			fstream = new FileInputStream(logFile);

			BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
			String line, label;

			String[] args;

			if ((line = br.readLine()) != null) {
				timeStamp = WekiLogFileUtils.getLogLineTime(line);
			}
			br.close();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return timeStamp;
	}

	/**
	 * returns the first time from the map It assumes that the first element is
	 * always STARTLOG...
	 *
	 * @param map
	 * @return
	 */
	public long getFirstTimeFromMap(HashMap<String, ArrayList<LogEvent>> map) {
		return map.get("STARTLOG").get(0).getTimeStamp();
	}

	/**
	 * returns the last time found in the Map It assumes that the last element
	 * is always STOPLOG
	 *
	 * @param map
	 * @return
	 */
	public long getLastTimeFromMap(HashMap<String, ArrayList<LogEvent>> map) {
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
