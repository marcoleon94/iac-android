package com.ievolutioned.iac.util;

import android.content.Context;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by Daniel on 04/05/2015.
 */
public class FileUtil {

    public static boolean saveJsonFile(Context c, String name, String json) {
        try {
            //Get cache directory
            String cachePath = c.getCacheDir().getPath();
            FileWriter fileWriter = new FileWriter(cachePath + "/" + name + ".json");
            //Save content
            fileWriter.write(json);
            fileWriter.close();
            return true;
        } catch (Exception e) {
            LogUtil.e(FileUtil.class.getName(), e.getMessage(), e);
            return false;
        }
    }

    public static String readJsonFile(Context c, String name) {
        try {
            //Get cache directory
            String cachePath = c.getCacheDir().getPath();
            //Create cache file
            File file = new File(cachePath + "/" + name + ".json");
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            return getStream(bufferedReader);
        } catch (Exception e) {
            LogUtil.e(FileUtil.class.getName(), e.getMessage(), e);
            return null;
        }
    }

    private static String getStream(BufferedReader bufferedReader) throws IOException {
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            sb.append(line);
        }
        return sb.toString();
    }
}
