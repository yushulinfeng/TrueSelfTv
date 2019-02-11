package org.out.yslf.trueselftv;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

/**
 * 软件备份管理器
 *
 * @author sunyulin01
 * @since 2019-02-11
 */
public class BackupManager {
    private static final int EVENT_COMPLETE = 0;
    private static final int EVENT_ERROR = -1;

    private static final String BACKUP_PATH = "/sdcard/backup/";
    private static final String APK = ".apk";
    private static final String ODEX = ".odex";
    private static final String OAT32 = "oat/arm";
    private static final String OAT64 = "oat/arm64";

    private static Handler completeHandler;

    /**
     * 软件备份
     */
    public static void backupApp(Context context, ActivityInfo activityInfo, PackageManager mPackageManager) {
        String source = activityInfo.applicationInfo.sourceDir;
        String dest = BACKUP_PATH + activityInfo.loadLabel(mPackageManager) + APK;
        startBackup(context, source, dest);
    }

    public static String getBackupPath() {
        return BACKUP_PATH;
    }

    private static boolean isNotSystemApp(PackageInfo packageInfo) {
        return (packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0;
    }

    private static boolean hasOdexFile(String source) {
        String path = new File(source).getParent();
        String name = new File(source).getName();
        name = name.substring(0, name.indexOf(APK));

        String kitKat = new File(path).getParent() + File.separator + name + ODEX;
        String oat32 = path + File.separator + OAT32 + File.separator + name + ODEX;
        String oat64 = path + File.separator + OAT64 + File.separator + name + ODEX;

        return new File(kitKat).exists() || new File(oat32).exists() || new File(oat64).exists();
    }

    @SuppressLint("HandlerLeak")
    private static void startBackup(Context context, String source, String dest) {
        ToastManager.showToast(context, "开始备份");
        if (completeHandler == null) {
            final Context appContext = context.getApplicationContext();
            completeHandler = new Handler() {
                public void handleMessage(Message msg) {
                    if (msg.what == EVENT_COMPLETE) {
                        ToastManager.showToast(appContext, "备份完成：" + msg.obj);
                    } else if (msg.what == EVENT_ERROR) {
                        ToastManager.showToast(appContext, "备份失败：" + msg.obj);
                    }
                }
            };
        }
        new Thread(new CopyRunnable(source, dest)).start();
    }

    private static class CopyRunnable implements Runnable {

        private String source;
        private String dest;

        private CopyRunnable(String source, String dest) {
            Log.e("EEEEEE", "source:" + source + ",dest:" + dest);
            this.source = source;
            this.dest = dest;
        }

        /**
         * 复制单个文件
         */
        private String copyFile(String oldPath, String newPath) {
            try {
                int byteread = 0;
                File oldfile = new File(oldPath);
                if (!oldfile.exists()) return "source is not exist";
                if (!oldfile.isFile()) return "source is not a file";
                InputStream inStream = new FileInputStream(oldPath); //读入原文件
                FileOutputStream fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1444];
                while ((byteread = inStream.read(buffer)) != -1) {
                    fs.write(buffer, 0, byteread);
                }
                inStream.close();
                return null;
            } catch (Exception e) {
                e.printStackTrace();
                String msg = e.getMessage();
                return msg == null ? "copy error" : msg;
            }
        }

        @Override
        public void run() {
            if (!new File(BACKUP_PATH).exists()) {
                new File(BACKUP_PATH).mkdirs();
            }
            String errMsg = copyFile(source, dest);
            if (completeHandler != null) {
                Message message = completeHandler
                        .obtainMessage(errMsg == null ? EVENT_COMPLETE : EVENT_ERROR);
                message.obj = errMsg == null ? dest : errMsg;
                completeHandler.sendMessage(message);
            }
        }
    }

}
