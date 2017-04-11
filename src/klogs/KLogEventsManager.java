package klogs;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import klogs.ui.events.ModelUpdatedLogEvent;
import klogs.utils.KLogFileUtils;
import logs.model.LogEvent;
import logs.model.LogEventsManager;

/**
 * This classes offers methods to load log files from wekinator as Lists and
 * Maps of DiscreteLogEvents
 *
 * @author jeremiegarcia
 *
 */
public class KLogEventsManager extends LogEventsManager {

	@Override
	protected ArrayList<LogEvent> extractEventsAsList(File logFileToLoad) {
		ArrayList<LogEvent> events = new ArrayList<>();

		FileInputStream fstream;
		try {
			fstream = new FileInputStream(logFileToLoad);

			BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
			String line, label;
			long timeStamp;
			String[] args;
			while ((line = br.readLine()) != null) {
				label = KLogFileUtils.getLogLineLabel(line);
				timeStamp = KLogFileUtils.getLogLineTime(line);
				args = KLogFileUtils.getLogLineArgs(line);
				LogEvent event = logEventFactory(label, timeStamp, 0, new ArrayList<String>(Arrays.asList(args)),
						logFileToLoad.getPath());
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
	 * this method create the appropriate type of LogEvent class
	 *
	 * @param label
	 * @param timeStamp
	 * @param duration
	 * @param arrayList
	 * @param path
	 * @return
	 */
	private LogEvent logEventFactory(String label, long timeStamp, long duration, ArrayList<String> args,
			String source) {

		if (label.toLowerCase().contains("model_num")) {
			return new ModelUpdatedLogEvent(label, timeStamp, duration, args, source);

		} else {
			return new LogEvent(label, timeStamp, 0, args, source);
		}

	}
}
