package org.out.yslf.trueselftv.utils;

import android.content.Context;

/**
 * 键值对存储管理器
 *
 * @author sunyulin01
 * @since 2019-01-29
 */
public class ShareTool {
    private static final String SAVE_PATH = "note_save_path";
    private static final String SAVE_BOOT_KEY = "boot";
    private static final String SAVE_LOCK_KEY = "lock";
    public static final String SAVE_QIYI_TIMES_KEY = "qiyi_open_times";


    public static void setBootEnabled(Context context, boolean value) {
        setBoolean(context, SAVE_BOOT_KEY, value);
    }

    public static boolean getBootEnabled(Context context) {
        return getBoolean(context, SAVE_BOOT_KEY);
    }

    public static void setLockEnabled(Context context, boolean value) {
        setBoolean(context, SAVE_LOCK_KEY, value);
    }

    public static boolean getLockEnabled(Context context) {
        return getBoolean(context, SAVE_LOCK_KEY);
    }

    public static void saveQiyiTimes(Context context, int times) {
        context.getSharedPreferences(SAVE_PATH, Context.MODE_PRIVATE)
                .edit()
                .putInt(SAVE_QIYI_TIMES_KEY, times)
                .apply();
    }

    public static int getQiyiTimes(Context context) {
        return context.getSharedPreferences(SAVE_PATH, Context.MODE_PRIVATE)
                .getInt(SAVE_QIYI_TIMES_KEY, 0);
    }

    private static void setBoolean(Context context, String path, boolean value) {
        context.getSharedPreferences(SAVE_PATH, Context.MODE_PRIVATE)
                .edit()
                .putBoolean(path, value)
                .apply();
    }

    private static boolean getBoolean(Context context, String path) {
        return context.getSharedPreferences(SAVE_PATH, Context.MODE_PRIVATE)
                .getBoolean(path, false);
    }
}
