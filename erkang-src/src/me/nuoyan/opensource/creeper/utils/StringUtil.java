package me.nuoyan.opensource.creeper.utils;


public class StringUtil {
	
	public static int getSimilarity(String str1, String str2) {
		int similarity = 0;
		if (str1 == null || str1.length() == 0 || str2 == null || str2.length() == 0) {
			return similarity;
		}
		int len1 = str1.length();
		int len2 = str2.length();
		if (len1 > len2) {
			
		}
		
		return similarity;
	}
	
	private static String emojiRegexString;
	static {
		StringBuffer sb = new StringBuffer();
		for (char i = '\uDF00';  i<= '\uDFFF'; i++) {
			sb.append(new String(new char[]{'\uD83C', i}));
		}
		for (char i = '\uDC00';  i<= '\uDDFF'; i++) {
			sb.append(new String(new char[]{'\uD83D', i}));
		}
		for (char i = '\uE000';  i<= '\uF8FF'; i++) {
			sb.append(new String(new char[]{i}));
		}
		sb.append("😚😑😢😉🛀😢😣😞😈😇😃😥😈😸😽😛🙋🙈😿🙈😴😺🌿🌼😳😄👿😻😔😍😫😂🙊😋😭😡😫😜😘😓😊❗😌😝😱😩😏😁🚽😰😒😎😆😬😪");
		emojiRegexString = "[" + sb.toString() + "]";
	}
	public static String removeOuterUnicodeChar (String input) {
        return input.replaceAll(emojiRegexString, "");
	}

}
