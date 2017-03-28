package logs.model;

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

import logs.utils.LogFileUtils;

/**
 * This classes offers methods to load log files from wekinator as Lists and
 * Maps of DiscreteLogEvents
 *
 * @author jeremiegarcia
 *
 */
public abstract class LogEventsLoader {

	/**
	 * Returns all events from a log file as a list of events
	 *
	 * @param logFile
	 * @return
	 */
	public abstract ArrayList<LogEvent> extractEventsFromLogFileAsArrayList(File logFile);

	/**
	 * Returns the events of a log file as a dictionary with label as the keys
	 *
	 * @param logFile
	 * @return
	 */
	public abstract HashMap<String, ArrayList<LogEvent>> extractEventsFromLogFileAsHashMap(File logFile);

	/**
	 * returns the first time from the map It assumes that the first element is
	 * always STARTLOG...
	 *
	 * @param map
	 * @return
	 */
	public abstract long getFirstTimeFromMap(HashMap<String, ArrayList<LogEvent>> map);

	/**
	 * returns the last time found in the Map It assumes that the last element
	 * is always STOPLOG
	 *
	 * @param map
	 * @return
	 */
	public abstract long getLastTimeFromMap(HashMap<String, ArrayList<LogEvent>> map);
}
