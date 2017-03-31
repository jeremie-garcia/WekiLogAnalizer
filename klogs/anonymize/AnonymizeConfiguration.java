package wekilogs.anonymize;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

public class AnonymizeConfiguration {

	// RAW DATA FROM KADENZE
	// in a folder with a list of folders named with the email address
	// Each of these subfolders contain a zip archive named with the assignement
	public String rawDataFolder = "./data1";

	// file to put the correspondence between e-mails and IDs
	public String correspondenceFile = rawDataFolder + "/_corresp.txt";

	// New folder with anonymised logs
	// Assignment Data Folder
	public String outputFolder = "./assignement1";

	public String zipFileName = "assignment1.zip";
	public String assignementFileName = "assignment1.txt";

	public boolean cleaning = false;

	public String getLogsFolder() {
		return outputFolder + "/anonymizedLogs";
	}

	public String getUnzippedLogsFolder() {
		return outputFolder + "/unzipped";
	}

	public String toJSon() {
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		return gson.toJson(this);
	}

	public static AnonymizeConfiguration fromJSon(File jsonFile)
			throws JsonSyntaxException, JsonIOException, FileNotFoundException {
		Gson gson = new Gson();
		return gson.fromJson(new FileReader(jsonFile), AnonymizeConfiguration.class);
	}

}
