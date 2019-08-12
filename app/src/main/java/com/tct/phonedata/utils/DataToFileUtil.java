package com.tct.phonedata.utils;

import java.io.File;
import java.io.RandomAccessFile;

import android.os.Build;
import android.os.Environment;

public class DataToFileUtil {
    public final static String FILE_PATH = Environment.getExternalStorageDirectory() + "/PhoneData/";

    public final static String FILE_NAME_VALUE = Build.BRAND + "_" + DateTimeUtil.getSysTime() + "_Data_value.log";

    public final static String FILE_NAME_TYPE = Build.BRAND + "_Data_name.log";

    public final static String FILE_SENSOR_INFO = Build.BRAND + "_SensorInfo.log";

    public static void writeFile(String filePath, String fileName, String content) {
        String strContent = content + "\n";
        try {
            File fileDir = new File(filePath);
            if (!fileDir.exists()) {
                fileDir.mkdirs();
                if (!fileDir.exists()) {
                    return;
                }
            }
            File file = new File(filePath, fileName);
            RandomAccessFile raf = new RandomAccessFile(file, "rw");
            raf.seek(file.length());
            raf.write(strContent.getBytes());
            raf.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isFileSensorExit(){
        File file = new File(FILE_PATH + FILE_SENSOR_INFO);
        if (file.exists()) {
            return true;
        }
        return false;
    }

    public static void writeFileSensorInfo(String content) {
        writeFile(FILE_PATH, FILE_SENSOR_INFO, content);
    }

    public static void writeFileDataTyp(String content) {
        File file = new File(FILE_PATH + FILE_NAME_TYPE);
        if (file.exists()) {
            return;
        }

        writeFile(FILE_PATH, FILE_NAME_TYPE, content);
    }

    public static void writeFileDataValue(String content) {
        writeFile(FILE_PATH, FILE_NAME_VALUE, content);
    }

    public static void writeFileDataValue(String filePath, String fileName, String content) {
        writeFile(filePath, fileName, content);
    }
}
