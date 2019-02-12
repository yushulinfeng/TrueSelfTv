package org.out.yslf.trueselftv.utils;

import android.app.Activity;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.os.storage.StorageManager;

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;

/**
 * Created by YuShuLinFeng on 2016/12/24.
 */

public class MediaTool {//考虑添加一个长按强制分类型打开
    private static final String SD_PATH_PHONE = "/storage/";
    private static final String SD_PATH_TV = "/mnt/usb/";
    //    Android官方公布的文档显示MediaPlayer支持如下视频格式：
    //    Video H.263 X X 3GPP (.3gp) and MPEG-4 (.mp4)
    //    H.264 AVC X 3GPP (.3gp) and MPEG-4 (.mp4)
    //    MPEG-4 SP X 3GPP (.3gp)
    //    已测试支持以下格式
    private static final String[] AUDIO = {".aac", ".amr", ".flac",
            ".m4a", ".m4r", ".wma", ".mp3", ".mp3", ".ogg", ".midi", ".tsa"};
    private static final String[] VIDEO = {".3gp", ".avi", ".flv",
            ".mkv", ".mov", ".mp4", ".vob", ".wmv", ".rmvb", ".tsv"};
    //Android源码发现BitmapFactory可以解析6种：ICO,BMP,JPEG,WBMP,GIF,PNG.
    private static final String[] IMAGE = {".jpg", ".jpeg", ".png",
            "bmp", "wbmp", "ico", "gif", "tsi"};

    // 下面方法是通过反射，调用StorageManager的隐藏接口getVolumePaths()，实现获取存储器列表。
    public static String[] getExternalPaths(Activity actvity) {
        StorageManager mStorageManager = null;
        Method mMethodGetPaths = null;
        String[] paths = null;
        mStorageManager = (StorageManager) actvity
                .getSystemService(Activity.STORAGE_SERVICE);
        try {
            mMethodGetPaths = mStorageManager.getClass().getMethod(
                    "getVolumePaths");
            paths = (String[]) mMethodGetPaths.invoke(mStorageManager);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return paths;
    }

    public static ArrayList<String> dealExternalPaths(String[] paths) {
        ArrayList<String> all_path = new ArrayList<String>();
        String inner_sd_path = Environment.getExternalStorageDirectory()
                .getAbsolutePath();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2)
            return all_path;
        if (paths != null)
            for (String path : paths) {
                try {
                    // 容量大于0 && 不是内置存储卡
                    if (new StatFs(path).getBlockCountLong() > 0
                            && !path.equals(inner_sd_path)) {
                        String temp = path.substring(path
                                .lastIndexOf("/") + 1);// 取最后一层
                        all_path.add(temp);
                    }
                } catch (Exception ignore) {
                }
            }
        return all_path;
    }

    public static boolean isPhone(ArrayList<String> all_path) {
        boolean is_phone = false;
        for (int i = 0; i < all_path.size(); i++) {
            if (all_path.get(i).contains("sdcard")) {
                is_phone = true;
                break;
            }
        }
        return is_phone;
    }

    public static ArrayList<String> getRealExternalPaths(Activity activity) {
        ArrayList<String> final_path = new ArrayList<String>();
        String[] paths = getExternalPaths(activity);
        ArrayList<String> all_path = dealExternalPaths(paths);
        boolean is_phone = isPhone(all_path);
        String front_str = is_phone ? SD_PATH_PHONE : SD_PATH_TV;
        for (int i = 0; i < all_path.size(); i++) {
            final_path.add(front_str + all_path.get(i));
        }
        return final_path;
    }


    // 读取数据
    public static String readLrcFromFile(String file_name) {
        String all_todo = "";
        try {
            File file = new File(file_name);
            FileInputStream fis = new FileInputStream(file);
            byte[] buff = new byte[fis.available()];
            fis.read(buff);
            all_todo = new String(buff, "GBK");
            fis.close();
            return all_todo;
        } catch (Exception e) {// 文件不存在
            return "";
        }
    }


    public static boolean isAudioFile(String file_name) {
        file_name = dealFileName(file_name);
        for (String last_name : AUDIO) {
            if (file_name.endsWith(last_name))
                return true;
        }
        return false;
    }

    public static boolean isLyricFile(String file_name) {
        file_name = dealFileName(file_name);
        if (file_name.endsWith(".lrc"))
            return true;
        return false;
    }

    public static boolean isTextFile(String file_name) {
        file_name = dealFileName(file_name);
        if (file_name.endsWith(".txt"))
            return true;
        return false;
    }

    public static boolean isVideoFile(String file_name) {
        file_name = dealFileName(file_name);
        for (String last_name : VIDEO) {
            if (file_name.endsWith(last_name))
                return true;
        }
        return false;
    }

    public static boolean isImageFile(String file_name) {
        file_name = dealFileName(file_name);
        for (String last_name : IMAGE) {
            if (file_name.endsWith(last_name))
                return true;
        }
        return false;
    }

    private static String dealFileName(String file_name) {
        file_name = file_name.toLowerCase();
        if (file_name.endsWith("1"))//个人的简单后缀名处理
            file_name = file_name.substring(0, file_name.length() - 1);
        return file_name;
    }


}
