package org.out.yslf.trueselftv.utils;

import android.content.Context;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 键值对存储管理器
 *
 * @author sunyulin01
 * @since 2019-01-29
 */
public class ShareTool {
    private static final String SAVE_PATH = "note_save_path";
    private static final String LAUNCH_SAVE_PATH = "launch_save_path";
    private static final String LAUNCH_TIME_KEY = "launch_time_key";
    private static final String SAVE_BOOT_KEY = "boot";
    private static final String SAVE_BOOT_QQ_KEY = "boot_qq";
    private static final String SAVE_LOCK_KEY = "lock";
    public static final String SAVE_QIYI_TIMES_KEY = "qiyi_open_times";

    public static void setBootEnabled(Context context, boolean value) {
        setBoolean(context, SAVE_BOOT_KEY, value);
    }

    public static boolean getBootEnabled(Context context) {
        return getBoolean(context, SAVE_BOOT_KEY);
    }

    public static void setBootQqEnabled(Context context, boolean value) {
        setBoolean(context, SAVE_BOOT_QQ_KEY, value);
    }

    public static boolean getBootQqEnabled(Context context) {
        return getBoolean(context, SAVE_BOOT_QQ_KEY);
    }

    public static void setLockEnabled(Context context, boolean value) {
        setBoolean(context, SAVE_LOCK_KEY, value);
    }

    public static boolean getLockEnabled(Context context) {
        return getBoolean(context, SAVE_LOCK_KEY);
    }

    // 清除开机时间记录
    public static void clearLaunchTimeLog(Context context) {
        context.getSharedPreferences(LAUNCH_SAVE_PATH, Context.MODE_PRIVATE).edit().clear().apply();
    }

    // 写入开机时间记录
    public static void writeLaunchTimeLog(Context context) {
        String oldText = readLaunchTimeLog(context);
        String time = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.CHINESE)
                .format(new Date(System.currentTimeMillis()));
        context.getSharedPreferences(LAUNCH_SAVE_PATH, Context.MODE_PRIVATE)
                .edit()
                .putString(LAUNCH_TIME_KEY, oldText + "\n" + time)
                .apply();
    }

    // 读取开机时间记录
    public static String readLaunchTimeLog(Context context) {
        return context.getSharedPreferences(LAUNCH_SAVE_PATH, Context.MODE_PRIVATE)
                .getString(LAUNCH_TIME_KEY, "");
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
