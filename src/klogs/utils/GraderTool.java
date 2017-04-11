package klogs.utils;

import java.io.File;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import logs.config.Configuration;
import wekimini.kadenze.Assignment2Grader_Feb6;
import wekimini.kadenze.Grade;

public class GraderTool {

	public static Grade unzipAndGetGradeForFile(File fZip) {
		if (fZip.exists()) {
			return Assignment2Grader_Feb6.gradeAssignment2(fZip.getPath(), Configuration.TMP_DIR);
		}
		return null;
	}

	public static double getScoreGradeForAssignment(Grade grade, String logFile) {

		JSONObject json = new JSONObject(grade.toJSONString());
		String name = KLogFileUtils.getAssignementStringFromFile(logFile);
		HashMap<String, Double> map = gradeToScoreMap(grade);

		if (name.contains("2_1A")) {
			return map.get("part1a_experimented");

		} else if (name.contains("2_1B")) {
			return map.get("part1b_classifier_quality");

		} else if (name.contains("2_1C")) {
			return map.get("part1c_classifier_quality");

		} else if (name.contains("2_1D")) {
			return map.get("part1d_classifier_quality");

		} else if (name.contains("2_2")) {
			return map.get("part2_experimented");

		} else if (name.contains("2_3A")) {
			return map.get("part3a_built_classifier");

		} else if (name.contains("2_3B")) {
			return map.get("part3b_classifier_accuracy");
		}
		return -1;
	}

	public static HashMap<String, Double> gradeToScoreMap(Grade grade) {
		HashMap<String, Double> map = new HashMap<String, Double>();

		JSONObject json = new JSONObject(grade.toJSONString());
		JSONArray results = json.getJSONArray("grade_results");
		for (int i = 0; i < results.length(); i++) {
			JSONObject obj = results.getJSONObject(i);
			String featureId = obj.getString("feature_id");
			double score = obj.getDouble("score");

			map.put(featureId, score);
		}
		return map;
	}
}
