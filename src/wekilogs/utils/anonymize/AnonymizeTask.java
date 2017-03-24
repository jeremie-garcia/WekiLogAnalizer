package wekilogs.utils.anonymize;

import javafx.concurrent.Task;
import wekilogs.utils.LogFileUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;

public class AnonymizeTask extends Task {

	AnonymizeConfiguration config;

	public AnonymizeTask(AnonymizeConfiguration config) {
		this.config = config;
	}

	public void anonymizeLogs(AnonymizeConfiguration config) {
		anonymizeLogs(config.correspondenceFile, config.rawDataFolder, config.getUnzippedLogsFolder(),
				config.getLogsFolder(), config.zipFileName, config.assignementFileName, config.cleaning);
	}

	/*
	 * This method creates a correspondence file between emails (IDs) and
	 * numbers to anonymise the logs folder is the path to the data folder
	 * containing raw data res_dir_path is the path to the output folder
	 *
	 */
	public void anonymizeLogs(String corresp_path, String data_folder, String out_dir_path, String logs_folder,
			String zip_file_name, String assignment_file_name, boolean clean) {

		updateProgress(0, 100);

		// 0 possibly clean
		if (clean) {
			this.removeNotValidFolders(data_folder, zip_file_name);
		}

		updateProgress(10, 100);

		// 1 Create the correspondence file that contains IDS (Numbers) and
		// email address
		updateMessage("Creating Correspondance File");
		File corresp = new File(corresp_path);
		if (!corresp.exists()) {
			try {
				corresp.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		File files = new File(data_folder);
		String[] directories = files.list(new FilenameFilter() {
			@Override
			public boolean accept(File current, String name) {
				return (new File(current, name).isDirectory()) && name.contains("@");
			}
		});
		// System.out.println("Number of directories found in: " +
		// files.getPath());
		// System.out.println(directories.length);
		// System.out.println();

		// Create the correspondence file for anonymity
		try {
			FileWriter fw = new FileWriter(corresp);
			for (int i = 0; i < directories.length; i++) {
				fw.write(String.format("%03d", i) + ", " + directories[i] + System.lineSeparator());
			}
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		updateProgress(20, 100);

		// 2 Unzip all the files in their subfolder
		// create the result folder if it does not exist
		// File res_dir = new File(Configuration.RESULT);

		File res_dir = new File(out_dir_path);
		res_dir.mkdir();
		updateMessage("Anonymised data folder created");

		String dest = "";
		String source;
		int nbDirectories = directories.length;

		for (int i = 0; i < directories.length; i++) {
			// find the zip source file.
			source = data_folder + "/" + directories[i] + "/" + zip_file_name;
			dest = res_dir.getPath() + "/" + String.format("%03d", i);

			// unzip the source file to destination file (numbered folder)
			LogFileUtils.unzip(source, dest, null);
			updateMessage("unzipping " + (i + 1) + "/" + nbDirectories);
			double progress = 20 + 70 * (i + 1) / nbDirectories;
			updateProgress(progress, 100);
		}
		updateMessage("Done unzipping files");

		// 3 Gather all event logs files in the outfolder and Alllogs
		String[] res_directories = res_dir.list(new FilenameFilter() {
			@Override
			public boolean accept(File current, String name) {
				return new File(current, name).isDirectory();
			}
		});

		File logDir = new File(logs_folder);
		logDir.mkdir();

		updateMessage("Gathering all log files");

		for (int i = 0; i < res_directories.length; i++) {

			// depending on the plateform used to create the zip archive, logs
			// can be in folders or with longer filenames
			// eg. res_folder/000/assignment1/assignement1.txt
			// or res_folder/000/assignment1\assignement1.txt

			String logFileName = res_dir.getPath() + "/" + res_directories[i] + "/assignment1/" + assignment_file_name;
			File f = new File(logFileName);

			if (!f.exists()) {
				// try to change it to the other possible formatting (windows...
				// :p )
				logFileName = res_dir.getPath() + "/" + res_directories[i] + "/assignment1\\" + assignment_file_name;
				f = new File(logFileName);
			}

			String log_dest = logDir.getPath() + "/" + String.format("%03d", i) + ".txt";
			File destFile = new File(log_dest);
			// copy the file to a new one
			try {
				LogFileUtils.copyFileUsingFileChannels(f, destFile);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		updateMessage("Anonymization OK");

		updateProgress(100, 100);
	}

	public void removeNotValidFolders(String data_folder, String zip_file_name) {

		// System.out.println("Cleaning the files: removing folder with not
		// valid archive");
		File files = new File(data_folder);
		String[] directories = files.list(new FilenameFilter() {
			@Override
			public boolean accept(File current, String name) {
				return (new File(current, name).isDirectory()) && name.contains("@");
			}
		});
		// System.out.println("Number of directories found in: " +
		// files.getPath());
		System.out.println(directories.length);

		String source;
		int count = 0;
		for (int i = 0; i < directories.length; i++) {
			// find the zip source file.
			source = data_folder + "/" + directories[i] + "/" + zip_file_name;
			if (!new File(source).exists()) {
				File folder = new File(data_folder + "/" + directories[i]);
				if (folder.exists() && folder.isDirectory()) {
					LogFileUtils.deleteDirectory(folder);
					System.out.println("Folder " + directories[i] + "removed");
					count++;
				}
			}
		}
		System.out.println("Deleted " + count + " folders");
		updateMessage("Deleted " + count + " folders");
	}

	@Override
	protected Object call() throws Exception {
		anonymizeLogs(config);
		return true;
	}
}
