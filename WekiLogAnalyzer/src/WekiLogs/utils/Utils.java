package WekiLogs.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.channels.FileChannel;

import WekiLogs.logs.LogEvent;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

public class Utils {

	public static void unzip(String source, String destination, String password) {
		try {
			ZipFile zipFile = new ZipFile(source);
			if (zipFile.isEncrypted()) {
				zipFile.setPassword(password);
			}
			zipFile.extractAll(destination);
		} catch (ZipException e) {
			e.printStackTrace();
		}
	}

	public static void copyFileUsingFileChannels(File source, File dest) throws IOException {

		FileChannel inputChannel = null;
		FileChannel outputChannel = null;
		try {
			FileInputStream fileInputStream = new FileInputStream(source);
			inputChannel = fileInputStream.getChannel();
			FileOutputStream fileOutputStream = new FileOutputStream(dest);
			outputChannel = fileOutputStream.getChannel();
			outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
			fileInputStream.close();
			fileOutputStream.close();
		} finally {
			inputChannel.close();
			outputChannel.close();
		}
	}

	public static String[] getAllLogsFilesFromFolder(File logs_folder) {
		return logs_folder.list(new FilenameFilter() {
			@Override
			public boolean accept(File current, String name) {
				return new File(current, name).getName().contains(".txt");
			}
		});
	}

	public static long getLogLineTime(String line) {
		try {
			return Long.parseLong(line.split(",")[0]);
		} catch (Exception e) {
			return 0;
		}

	}

	public static String getLogLineLabel(String line) {
		return line.split(",")[2];
	}

	public static String[] getLogLineArgs(String line) {
		String[] lineSplit = line.split(",");
		int n = lineSplit.length - 3;
		String[] newArray = new String[n];
		System.arraycopy(lineSplit, 3, newArray, 0, n);
		return newArray;
	}

	static public boolean deleteDirectory(File path) {
		if (path.exists()) {
			File[] files = path.listFiles();
			for (int i = 0; i < files.length; i++) {
				if (files[i].isDirectory()) {
					deleteDirectory(files[i]);
				} else {
					files[i].delete();
				}
			}
		}
		return (path.delete());
	}

	// retriev the path for the model indicated in the event
	public static String getFileNameForModelFromEvent(LogEvent event) {
		if (event.getLabel().toLowerCase().contains("model_num=")) {
			File f = new File(event.getSource());
			File dir = f.getParentFile();
			if (dir.isDirectory()) {
				return dir.getPath() + "/" + event.getArgs().get(0);
			}
		}
		return null;

	}

	public static String getAssignementStringFromFile(String logFile) {
		File f = new File(logFile);
		String name = f.getName();
		String exercice = name.substring(name.indexOf("_") - 1, name.indexOf('.'));
		return exercice;
	}

}
