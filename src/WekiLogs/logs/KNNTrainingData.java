package WekiLogs.logs;

import java.util.ArrayList;

public class KNNTrainingData {

	public ArrayList<Integer> inputs;
	public int output;
	public long date;

	public KNNTrainingData(long date, ArrayList<Integer> inputs, int classe) {
		this.inputs = inputs;
		this.output = classe;
		this.date = date;
	}
}
