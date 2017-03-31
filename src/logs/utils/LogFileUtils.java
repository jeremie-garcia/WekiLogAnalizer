package logs.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.channels.FileChannel;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

public class LogFileUtils {

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

}
