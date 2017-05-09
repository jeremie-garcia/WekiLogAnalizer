package logs.model;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * This classes contains utilities to open and process logFiles. It stores
 * important and frequently reused informations about the logs. It maintains two
 * representations : a hashmaps by logEvent Types and an ArrayList of events
 * sorted in time
 *
 * @author jeremiegarcia
 *
 */
public abstract class LogEventsManager {

	private long beginTime = 0;
	private long endTime = 1000;

	private HashMap<String, ArrayList<LogEvent>> eventsMap;
	private ArrayList<LogEvent> eventsList;

	private File logFile;
	
	private static HashMap<String,ArrayList<LogEvent>> selectedList=new HashMap();

	public static HashMap<String,ArrayList<LogEvent>> getSelectedList(){
		return selectedList;
	}
	
	/**
	 * Process a logFile and extract the data
	 */
	public void setLogFile(File f) {
		if (this.logFile != null && this.logFile.getPath() != f.getPath()) {
			this.reset();
		}
		this.logFile = f;

		if (this.logFile.exists()) {
			this.eventsList = this.extractEventsAsList(this.logFile);
			this.eventsMap = this.createMapFromList(this.eventsList);
			this.updateTimes(this.eventsList);
		}
	}

	private void updateTimes(ArrayList<LogEvent> eventsList2) {
		this.beginTime = eventsList2.get(0).getTimeStamp();
		this.endTime = eventsList2.get(eventsList2.size() - 1).getTimeStamp();
	}

	private HashMap<String, ArrayList<LogEvent>> createMapFromList(ArrayList<LogEvent> eventsList2) {
		HashMap<String, ArrayList<LogEvent>> map = new HashMap<String, ArrayList<LogEvent>>();
		for (LogEvent evt : eventsList2) {
			if (map.containsKey(evt.getLabel())) {
				map.get(evt.getLabel()).add(evt);
			} else {
				ArrayList<LogEvent> list = new ArrayList<LogEvent>();
				list.add(evt);
				map.put(evt.getLabel(), list);
			}
		}
		return map;
	}

	/**
	 * to be implemented by subclasses
	 *
	 * @return
	 */
	protected abstract ArrayList<LogEvent> extractEventsAsList(File logFile);

	private void reset() {
		this.eventsList.clear();
		this.eventsMap.clear();
	}

	public long getBeginTime() {
		return beginTime;
	}

	public long getEndTime() {
		return endTime;
	}

	public long getDuration() {
		return endTime - beginTime;
	}

	public HashMap<String, ArrayList<LogEvent>> getLogevents() {
		return eventsMap;
	}

	public ArrayList<LogEvent> getTimeSortedLogEventsAsArrayList() {
		return eventsList;
	}

}
