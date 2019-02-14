package org.out.yslf.trueselftv;

import org.out.yslf.trueselftv.utils.MediaTool;

import java.io.File;
import java.io.FileInputStream;
import java.text.DecimalFormat;

/**
 * Created by YuShuLinFeng on 2016/12/24.
 */

public class MediaItem implements Comparable<MediaItem> {
    //不要用-1，与ITEM_IMAGES对应，并且将用于排序
    public static final int TYPE_BACK = 0, TYPE_FOLDER = 1,
            TYPE_VIDEO = 2, TYPE_AUDIO = 3,
            TYPE_IMAGE = 4, TYPE_LYRIC = 5,
            TYPE_TEXT = 6, TYPE_NONE = 7;
    private static final int[] ITEM_IMAGES = {R.drawable.icon_back, R.drawable.icon_jia,
            R.drawable.icon_shi, R.drawable.icon_yue,
            R.drawable.icon_tu, R.drawable.icon_ci,
            R.drawable.icon_wen, R.drawable.icon_no};
    private String name;
    private String realPath;
    private String info;
    private int type;//便于分类与排序。0文件夹
    private boolean clicked;

    public MediaItem() {
    }

    public MediaItem(String realPath) {
        if (realPath != null && !realPath.equals("/") && realPath.contains("/")) {
            if (realPath.endsWith("/")) {
                realPath = realPath.substring(0, realPath.length() - 1);
            }
            name = realPath.substring(realPath.lastIndexOf("/") + 1);
        } else {
            name = realPath;
        }
        this.realPath = realPath;
        this.clicked = false;
        initType();
        initInfo();
    }

    public MediaItem(String name, String realPath) {
        this.name = name;
        this.realPath = realPath;
        this.clicked = false;
        initType();
        initInfo();
    }

    public MediaItem(String name, String realPath, int type) {
        this.name = name;
        this.realPath = realPath;
        this.type = type;
        this.clicked = false;
        initInfo();
    }

    private void initType() {
        type = getFileType(realPath);
    }

    private void initInfo() {
        info = "";
        if (type == TYPE_BACK) {
            info = "···";
        } else if (type == TYPE_FOLDER) {
            try {
                info = String.valueOf(new File(realPath).list().length) + "项";
            } catch (Exception ignore) {
            }
        } else {
            try {
                FileInputStream temp_stream = new FileInputStream(getRealPath());
                info = formatSize(temp_stream.available());
                temp_stream.close();
            } catch (Exception ignore) {
            }
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRealPath() {
        return realPath;
    }

    public void setRealPath(String realPath) {
        this.realPath = realPath;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public boolean isClicked() {
        return clicked;
    }

    public void setClicked(boolean clicked) {
        this.clicked = clicked;
    }

    //MY
    public int getImageRes() {
        try {
            return ITEM_IMAGES[type];
        } catch (Exception ignore) {
            return 0;
        }
    }

    public String getInfo() {
        if (info != null) return info;
        return "";
    }

    public boolean isFolder() {
        return type == MediaItem.TYPE_FOLDER;
    }

    @Override
    public int compareTo(MediaItem o) {
        //首选类型排序，次选名称排序
        if (type < o.type)
            return -1;
        else if (type > o.type)
            return 1;
        else
            return name.compareTo(o.getName());
    }

    private String formatSize(long size) {
        DecimalFormat df = new DecimalFormat("#.00");
        if (size <= 0) return "0B";
        String res = "";
        if (size < 1024) res = df.format(size) + "B";
        else if (size < 1048576) res = df.format(size / 1024.0) + "KB";
        else if (size < 1073741824) res = df.format(size / 1048576.0) + "MB";
        else res = df.format(size / 1073741824.0) + "GB";
        return res;
    }

    /**
     * 获取文件类型
     */
    public static int getFileType(String realPath) {
        File realFile = new File(realPath);
        String name = realFile.getName();
        if (realFile.isDirectory())
            return MediaItem.TYPE_FOLDER;
        else if (MediaTool.isVideoFile(name))
            return MediaItem.TYPE_VIDEO;
        else if (MediaTool.isAudioFile(name))
            return MediaItem.TYPE_AUDIO;
        else if (MediaTool.isImageFile(name))
            return MediaItem.TYPE_IMAGE;
        else if (MediaTool.isLyricFile(name))
            return MediaItem.TYPE_LYRIC;
        else if (MediaTool.isTextFile(name))
            return MediaItem.TYPE_TEXT;
        else
            return MediaItem.TYPE_NONE;
    }
}
