package klogs.trainingdata.model;

import java.util.ArrayList;

public class TrainingData {

	public ArrayList<Integer> inputs;
	public int output;
	public long date;

	public TrainingData(long date, ArrayList<Integer> inputs, int classe) {
		this.inputs = inputs;
		this.output = classe;
		this.date = date;
	}

	@Override
	public String toString() {
		return "date: " + date + " class: " + this.output + " inputs: " + inputs;
	}
}
