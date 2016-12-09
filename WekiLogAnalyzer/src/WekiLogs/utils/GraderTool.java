package WekiLogs.utils;

import java.io.File;

import com.google.gson.Gson;

import wekimini.kadenze.Assignment2Grader_Feb6;
import wekimini.kadenze.Grade;

public class GraderTool {

	public static void main(String[] args) {

		String zipFilePath = "./zipFilesTest/CreatingClassifiers-Parts1-3/32357/assignment2.zip";
		String tmpDir = "./tmp";
		File f = new File(tmpDir);
		if (!f.exists()) {
			f.mkdir();
		}

		File fZip = new File(zipFilePath);
		System.out.println(fZip.exists());

		System.out.println(f.getAbsolutePath());
		Grade g = Assignment2Grader_Feb6.gradeAssignment2(zipFilePath, tmpDir);
		System.out.println(g.toJSONString());
	}
}
