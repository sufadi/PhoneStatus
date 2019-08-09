package com.tct.phonedata.utils;

import java.io.File;

import android.os.Environment;
import android.os.StatFs;

public class FileUtil {

    /** SD卡地址 */
    public final static String root = Environment.getExternalStorageDirectory().getPath();

    public final static long MIN_SIZE = 1000000;

    /** 获取SD可用容量 */
    public static long getAvailableStorage() {
        StatFs statFs = new StatFs(root);
        long blockSize = statFs.getBlockSizeLong();
        long availableBlocks = statFs.getAvailableBlocksLong();
        long availableSize = blockSize * availableBlocks;
        return availableSize;
    }

    public static boolean isSdCardAvailable() {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) && getAvailableStorage() > MIN_SIZE) {
            return false;
        }

        return true;
    }

    public static boolean isFileExists(String filename) {
        try {
            File f = new File(filename);
            if (!f.exists()) {
                return false;
            }
        } catch (Exception e) {
            // TODO: handle exception
            return false;
        }
        return true;
    }
}
