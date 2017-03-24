package wekilogs.model;

import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import wekilogs.trainingdata.model.TrainingDataSet;

public class ModelProcessor {

	public static ArrayList<Point2D> extractPointsFromModel(String fileName) {
		if (fileName == null) {
			return null;
		}

		File f = new File(fileName);

		if (!f.exists()) {
			return null;
		} else {
			ArrayList<Point2D> res = new ArrayList<Point2D>();

			FileInputStream fstream;
			try {
				fstream = new FileInputStream(f);

				BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
				String line, token;
				long timeStamp;
				boolean isData = false;
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss:SSS");

				Point2D p;
				while ((line = br.readLine()) != null) {
					if (line.contains("@data")) {
						isData = true;
					} else if (isData) {
						// System.out.println(line);
						String[] values = line.split(",");
						String dateStr = values[1];
						dateStr = dateStr.substring(1, dateStr.length() - 1);
						// System.out.println(dateStr);
						try {
							int idx = Integer.parseInt(values[2]);
							int i1 = Integer.parseInt(values[3]);
							int i2 = Integer.parseInt(values[4]);
							int o1 = Integer.parseInt(values[5]);
							p = new Point2D.Double(i1, i2);
							res.add(p);
						} catch (Exception e) {
							System.out.println("number format exception in line " + line);
							// e.printStackTrace();
						}
						// System.out.println(idx + " " + i1 + " " + i2 + " " +
						// o1);

						Date date;
						try {
							date = formatter.parse(dateStr);
							timeStamp = date.getTime();
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							// e.printStackTrace();
							System.err.println("line avoided");
						}

					}
				}
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return res;
		}
	}

	/*
	 * First get the ...filename.._m.xml to extract meta data and build dataSet
	 * Second get the file and extract data
	 */
	public static TrainingDataSet extractDataSetFromKNNModelFile(String fileName) {
		if (fileName == null) {
			return null;
		}

		File metadataFile = new File(fileName.replace(".xml", "_m.xml"));
		File dataFile = new File(fileName);

		if (!dataFile.exists() && !metadataFile.exists()) {
			return null;
		} else {

			TrainingDataSet examples = getTrainingSetFromMetaDataFile(metadataFile);
			FileInputStream fstream;
			try {
				fstream = new FileInputStream(dataFile);
				BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
				String line;
				boolean isData = false;
				while ((line = br.readLine()) != null) {
					if (line.contains("@data")) {
						isData = true;
					} else if (isData) {
						// System.out.println(line);
						examples.addExampleFromLine(line);
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

			return examples;
		}
	}

	private static TrainingDataSet getTrainingSetFromMetaDataFile(File metadataFile) {

		FileInputStream fstream;
		try {
			fstream = new FileInputStream(metadataFile);

			BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
			String line;
			int numIn = 0;
			int numOut = 0;
			String dateFormat = "";
			ArrayList<Integer> outputs = new ArrayList<Integer>();

			while ((line = br.readLine()) != null) {

				if (line.contains("numInputs")) {
					int start = line.indexOf(">");
					String sub = line.substring(start + 1, line.indexOf("<", start));
					try {
						numIn = Integer.parseInt(sub);
					} catch (Exception e) {
						// TODO: handle exception
					}

				} else if (line.contains("numOutputs")) {
					int start = line.indexOf(">");
					String sub = line.substring(start + 1, line.indexOf("<", start));
					try {
						numOut = Integer.parseInt(sub);
					} catch (Exception e) {
						// TODO: handle exception
					}

				} else if (line.contains("@attribute")) {
					String[] elements = line.split(" ");
					if (elements[1].equals("Time")) {
						dateFormat = elements[3] + " " + elements[4];
						dateFormat = dateFormat.replace("\'", "");
					} else if (elements[1].contains("outputs")) {
						String values = elements[2];
						String[] val = values.substring(1, values.length() - 1).split(",");

						for (String v : val) {
							try {
								outputs.add(Integer.parseInt(v));
							} catch (Exception e) {
								// TODO: handle exception
							}

						}
					}

				}

			}
			br.close();
			return new TrainingDataSet(numIn, numOut, outputs, dateFormat);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		}
		return null;

	}

}
