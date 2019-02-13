package org.out.yslf.trueselftv.utils;

import android.app.Activity;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import org.out.yslf.trueselftv.MediaItem;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author SunYuLin
 * @since 2019/2/13
 */
public class FileShowTool {
    private List<String> rootPath;

    public FileShowTool(Activity activity) {
        rootPath = MediaTool.getRealExternalPaths(activity);
        rootPath.add(0, Environment.getExternalStorageDirectory().getAbsolutePath());
        rootPath.add(0, File.separator);
    }

    /**
     * 获取父目录
     * 返回 null 表示根目录
     */
    public String getParentPath(String path) {
        if (TextUtils.isEmpty(path)) {
            return null;
        }
        if (!path.contains(File.separator)) {
            return null;
        }
        if (rootPath.contains(path)) {
            return null;
        }
        return path.substring(0, path.lastIndexOf("/"));
    }

    /**
     * 获取当前文件夹下的所有文件
     * 返回空表示当前不是文件夹
     */
    @Nullable
    public List<MediaItem> getPathFiles(String path) {
        List<MediaItem> items = new ArrayList<>();
        if (TextUtils.isEmpty(path)) { // 根目录
            for (String item : rootPath) {
                items.add(new MediaItem(item));
            }
            return items;
        }
        File nowFile = new File(path);
        if (!nowFile.isDirectory()) {
            return null;
        }
        File[] subFiles = nowFile.listFiles();
        if (subFiles == null || subFiles.length == 0) {
            return items;
        }
        for (File file : subFiles) {
            items.add(new MediaItem(file.getAbsolutePath()));
        }
        Collections.sort(items);
        return items;
    }
}
