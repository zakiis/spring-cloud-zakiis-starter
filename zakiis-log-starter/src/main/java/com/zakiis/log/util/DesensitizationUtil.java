package com.zakiis.log.util;

import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DesensitizationUtil {
	
	
	private static Set<String> replaceFields;
	private static Set<String> eraseFields;
	private static Set<String> dropFields;
	
	private static Pattern fieldPattern = Pattern.compile("[ '\"]?([a-zA-Z_$][\\w_$]*)['\"]?\\s*[:：=]+\\s*(['\"]?[\\w@._\\-$]+['\" ]?)");
	
	public static void init(Set<String> replaceFields, Set<String> eraseFields, Set<String> dropFields) {
		DesensitizationUtil.replaceFields = replaceFields;
		DesensitizationUtil.eraseFields = eraseFields;
		DesensitizationUtil.dropFields = dropFields;
	}

	public static String convert(String msg) {
		Matcher matcher = fieldPattern.matcher(msg);
		StringBuilder builder = new StringBuilder();
		int startIndex = 0;
		while (matcher.find()) {
			builder.append(msg.substring(startIndex, matcher.start(2)));
			String fieldName = matcher.group(1);
			String fieldValue = matcher.group(2);
			if (replaceFields != null && replaceFields.contains(fieldName)) {
				fieldValue = replaceValue(fieldValue);
			}else if (eraseFields != null && eraseFields.contains(fieldName)) {
				fieldValue = eraseValue(fieldValue);
			}else if (dropFields != null && dropFields.contains(fieldName)) {
				fieldValue = dropValue(fieldValue);
			}
			builder.append(fieldValue);
			startIndex = matcher.end(2);
		}
		if (startIndex < msg.length()) {
			builder.append(msg.substring(startIndex, msg.length()));
		}
		return builder.toString();
	}

	private static String dropValue(String fieldValue) {
		Character start = null;
		if (fieldValue.charAt(0) == '"') {
			start = '"';
			fieldValue = fieldValue.substring(1);
		} else if (fieldValue.charAt(0) == '\'') {
			start = '\'';
			fieldValue = fieldValue.substring(1);
		}
		Character end = null;
		if (fieldValue.charAt(fieldValue.length() - 1) == '"') {
			end = '"';
			fieldValue = fieldValue.substring(0, fieldValue.length() - 1);
		} else if (fieldValue.charAt(fieldValue.length() - 1) == '\'') {
			end = '\'';
			fieldValue = fieldValue.substring(0, fieldValue.length() - 1);
		}
		return concat(start, "", end);
	}

	private static String eraseValue(String fieldValue) {
		Character start = null;
		if (fieldValue.charAt(0) == '"') {
			start = '"';
			fieldValue = fieldValue.substring(1);
		} else if (fieldValue.charAt(0) == '\'') {
			start = '\'';
			fieldValue = fieldValue.substring(1);
		}
		Character end = null;
		if (fieldValue.charAt(fieldValue.length() - 1) == '"') {
			end = '"';
			fieldValue = fieldValue.substring(0, fieldValue.length() - 1);
		} else if (fieldValue.charAt(fieldValue.length() - 1) == '\'') {
			end = '\'';
			fieldValue = fieldValue.substring(0, fieldValue.length() - 1);
		}
		String content = repeat("*", fieldValue.length());
		return concat(start, content, end);
	}

	private static String replaceValue(String fieldValue) {
		Character start = null;
		if (fieldValue.charAt(0) == '"') {
			start = '"';
			fieldValue = fieldValue.substring(1);
		} else if (fieldValue.charAt(0) == '\'') {
			start = '\'';
			fieldValue = fieldValue.substring(1);
		}
		Character end = null;
		if (fieldValue.charAt(fieldValue.length() - 1) == '"') {
			end = '"';
			fieldValue = fieldValue.substring(0, fieldValue.length() - 1);
		} else if (fieldValue.charAt(fieldValue.length() - 1) == '\'') {
			end = '\'';
			fieldValue = fieldValue.substring(0, fieldValue.length() - 1);
		}
		if (fieldValue.length() == 1) {
			return concat(start, "*", end);
		} else if (fieldValue.length() == 2) {
			String content = fieldValue.charAt(0) + "*";
			return concat(start, content, end);
		} else {
			int startReplace = fieldValue.length() / 3;
			int endReplace = fieldValue.length() - startReplace;
			String content = fieldValue.substring(0, startReplace) + repeat("*", endReplace - startReplace) + fieldValue.substring(endReplace);
			return concat(start, content, end);
		}
	}

	private static String concat(Character start, String content, Character end) {
		if (start != null && end != null) {
			return start + content + end;
		} else if (start == null && end == null) {
			return content;
		} else if (end != null) {
			return content + end;
		} else {
			return start + content;
		}
	}

	private static String repeat(String str, int repeatCount) {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < repeatCount; i++) {
			builder.append(str);
		}
		return builder.toString();
	}
}
