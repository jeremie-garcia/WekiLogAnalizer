package WekiLogs.logs;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class TrainingDataSet {

	private long timeStamp = 0;
	public String type = "KNN";
	public int numInputs = 1;
	public int numOutputs = 1;
	public ArrayList<Integer> possibleOutputs;
	public String dateFormat = "";
	SimpleDateFormat formater;
	public double grade = -1; // between 0 and 100% -1
								// means not available

	public int[] inputMinValues;
	public int[] inputMaxValues;

	public ArrayList<TrainingData> examples;

	public TrainingDataSet(int numInputs, int numOuputs, ArrayList<Integer> possibleOutputs, String dateFormat) {
		this.numInputs = numInputs;
		this.inputMinValues = new int[numInputs];
		this.inputMaxValues = new int[numInputs];

		for (int i = 0; i < numInputs; i++) {
			this.inputMaxValues[i] = Integer.MIN_VALUE;
			this.inputMinValues[i] = Integer.MAX_VALUE;
		}

		this.numOutputs = numOuputs;
		this.possibleOutputs = possibleOutputs;
		this.dateFormat = dateFormat;
		// System.out.println(dateFormat);
		this.formater = new SimpleDateFormat(this.dateFormat);
		this.examples = new ArrayList<TrainingData>();
	}

	public void addExampleFromLine(String line) {

		try {
			// format is the following
			// ID, date, id, in1...inn, out1...outn

			int index = 0;
			String[] values = line.split(",");

			// forget the first ID
			index++;

			// date
			String dateStr = values[index];
			dateStr = dateStr.substring(1, dateStr.length() - 1);
			Date date = this.formater.parse(dateStr);
			long timeStamp = date.getTime();
			index++;

			// ID
			int id = Integer.parseInt(values[index]);
			index++;

			// inputs
			ArrayList<Integer> inputs = new ArrayList<Integer>();
			int rank = 0;
			while (index <= (2 + numInputs)) {
				int value = Integer.parseInt(values[index]);
				this.updateMinMax(value, rank);
				inputs.add(value);
				index++;
				rank++;
			}

			// outputs
			int classe = Integer.parseInt(values[index]);

			TrainingData example = new TrainingData(timeStamp, inputs, classe);
			this.examples.add(example);
		} catch (ParseException e) {
			System.err.println(e.getLocalizedMessage() + " line " + line);
		} catch (NumberFormatException e) {

		}
	}

	private void updateMinMax(int value, int rank) {
		if (value < this.inputMinValues[rank]) {
			this.inputMinValues[rank] = value;
		}

		if (value > this.inputMaxValues[rank]) {
			this.inputMaxValues[rank] = value;
		}

	}

	public Date formatDate(String dateStr) {
		Date date = null;
		try {
			date = this.formater.parse(dateStr);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			System.err.println("line avoided");
		}
		return date;
	}

	public long getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(long timeStamp) {
		this.timeStamp = timeStamp;
	}

}
