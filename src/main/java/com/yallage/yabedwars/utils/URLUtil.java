package com.yallage.yabedwars.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class URLUtil {
    public static String getDocumentAt(String urlString) {
        StringBuilder document = new StringBuilder();
        try {
            URL url = new URL(urlString);
            URLConnection conn = url.openConnection();
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null)
                document.append(line).append(" ");
            reader.close();
        } catch (IOException e) {
            return null;
        }
        return document.toString();
    }
}
