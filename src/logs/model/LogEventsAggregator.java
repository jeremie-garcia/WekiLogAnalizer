package logs.model;

import java.util.ArrayList;

/**
 * This class aggregates two events into an aggregated one with a duration
 *
 * @author jeremiegarcia
 *
 */
public class LogEventsAggregator {

	public static LogEvent aggregateLogEvents(LogEvent startEvent, LogEvent endEvent) {

		String label = startEvent.getLabel() + " " + endEvent.getLabel();
		long start = startEvent.getTimeStamp();
		long duration = endEvent.getTimeStamp() - start;
		System.out.println(start);
		System.out.println(endEvent.getTimeStamp());
		ArrayList<String> args = startEvent.getArgs();
		args.addAll(endEvent.getArgs());
		String source = "user";
		return new LogEvent(label, start, duration, args, source);
	}

}
