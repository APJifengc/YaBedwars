package com.yallage.yabedwars.utils;

import java.util.regex.Pattern;

public final class UnicodeUtil {
    private static final String singlePattern = "[0-9|a-f|A-F]";

    private static final String pattern = "[0-9|a-f|A-F][0-9|a-f|A-F][0-9|a-f|A-F][0-9|a-f|A-F]";

    private static String ustartToCn(String str) {
        Integer codeInteger = Integer.decode("0x" + str.substring(2, 6));
        int code = codeInteger;
        char c = (char) code;
        return String.valueOf(c);
    }

    private static boolean isStartWithUnicode(String str) {
        if (str == null || str.length() == 0)
            return false;
        if (!str.startsWith("\\u"))
            return false;
        if (str.length() < 6)
            return false;
        String content = str.substring(2, 6);
        return Pattern.matches("[0-9|a-f|A-F][0-9|a-f|A-F][0-9|a-f|A-F][0-9|a-f|A-F]", content);
    }

    public static String unicodeToCn(String str) {
        StringBuilder sb = new StringBuilder();
        int length = str.length();
        for (int i = 0; i < length; ) {
            String tmpStr = str.substring(i);
            if (isStartWithUnicode(tmpStr)) {
                sb.append(ustartToCn(tmpStr));
                i += 6;
                continue;
            }
            sb.append(str.charAt(i));
            i++;
        }
        return sb.toString();
    }
}
