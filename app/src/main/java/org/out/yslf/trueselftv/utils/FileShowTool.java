package org.out.yslf.trueselftv.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;

import org.out.yslf.trueselftv.MediaItem;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
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

    /**
     * 移动文件。可以是文件名，也可以是目录。但需要前后一致。
     * 移动某个文件/文件夹为某个新的文件/文件夹
     *
     * @param oldPath String 原文件路径（文件、文件夹均可）
     * @param newPath String 复制后路径（必须是文件夹，即不支持重命名复制）
     * @return boolean 是否移动成功
     */
    public boolean moveFile(String oldPath, String newPath) {
        File oldFile = new File(oldPath);
        if (!oldFile.exists()) return false;
        File temp = new File(newPath).getParentFile();
        if (!temp.exists()) temp.mkdirs();
        return oldFile.renameTo(new File(newPath, oldFile.getName()));
    }

    /**
     * 复制文件
     *
     * @param oldPath String 原文件路径（文件、文件夹均可）
     * @param newPath String 复制后路径（必须是文件夹，即不支持重命名复制）
     * @return boolean 是否复制成功
     */
    public boolean copyFile(String oldPath, String newPath) {
        File oldfile = new File(oldPath);
        File newfile = new File(newPath);
        if (!oldfile.exists()) return false;
        if (newfile.exists() && newfile.isFile()) return false;
        if (oldfile.isFile()) {
            try {
                int bytesum = 0, byteread = 0;
                if (!oldfile.isFile()) return false;
                InputStream inStream = new FileInputStream(oldfile); //读入原文件
                FileOutputStream fs = new FileOutputStream(new File(newfile, oldfile.getName()));
                byte[] buffer = new byte[1444];
                while ((byteread = inStream.read(buffer)) != -1) {
                    bytesum += byteread; //字节数 文件大小
                    System.out.println(bytesum);
                    fs.write(buffer, 0, byteread);
                }
                inStream.close();
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        } else if (oldfile.isDirectory()) {
            File pathFile = new File(newfile, oldfile.getName());
            if (pathFile.exists() && pathFile.isFile()) return false;
            String path = pathFile.getAbsolutePath();
            pathFile.mkdirs();
            File[] subs = oldfile.listFiles();
            if (subs == null || subs.length == 0) return true;
            boolean succ = true;
            for (File file : subs) {
                succ = copyFile(file.getAbsolutePath(), path) && succ; // 先执行复制
            }
            return succ;
        } else {
            return false;
        }
    }

    /**
     * 递归删除文件和文件夹
     *
     * @param file 要删除的根目录
     */
    public boolean deleteFile(File file) {
        if (file.isFile()) {
            return file.delete();
        }
        if (file.isDirectory()) {
            File[] childFile = file.listFiles();
            if (childFile == null || childFile.length == 0) {
                return file.delete();
            }
            boolean succ = true;
            for (File f : childFile) {
                succ = deleteFile(f) && succ;
            }
            return file.delete() && succ;
        }
        return false;
    }

    /**
     * 打开文件
     */
    public void openFile(Context context, String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            ToastTool.showToast(context, "文件不存在");
            return;
        }
        if (file.isDirectory()) {
            ToastTool.showToast(context, "文件夹不支持打开");
            return;
        }
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        // FIXME: 2019/2/14 解决高版本：android.os.FileUriExposedException:问题
        Uri uri = Uri.fromFile(new File(filePath));
        switch (MediaItem.getFileType(filePath)) {
            case MediaItem.TYPE_VIDEO:
                intent.setDataAndType(uri, "video/*");
                break;
            case MediaItem.TYPE_AUDIO:
                intent.setDataAndType(uri, "audio/*");
                break;
            case MediaItem.TYPE_IMAGE:
                intent.setDataAndType(uri, "image/*");
                break;
            case MediaItem.TYPE_LYRIC:
            case MediaItem.TYPE_TEXT:
                intent.setDataAndType(uri, "text/plain");
                break;
            default:
                if (file.getName().endsWith(".apk")) {
                    intent.setDataAndType(uri, "application/vnd.android.package-archive");
                } else {
                    intent.setDataAndType(uri, "*/*");
                }
                break;
        }
        try {
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            ToastTool.showToast(context, "打开文件失败");
        }
    }
}
