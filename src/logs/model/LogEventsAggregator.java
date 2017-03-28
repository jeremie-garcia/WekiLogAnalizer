package logs.model;

import java.util.ArrayList;

public class LogEventsAggregator {

	public static LogEvent aggregateLogEvents(LogEvent startEvent, LogEvent endEvent) {

		String label = startEvent.getLabel() + " " + endEvent.getLabel();
		long start = startEvent.getTimeStamp();
		long duration = endEvent.getTimeStamp() - start;
		ArrayList<String> args = startEvent.getArgs();
		args.addAll(endEvent.getArgs());
		String source = "user";
		return new LogEvent(label, start, duration, args, source);
	}

}
