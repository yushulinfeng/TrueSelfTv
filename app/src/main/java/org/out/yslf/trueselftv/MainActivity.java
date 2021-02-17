package org.out.yslf.trueselftv;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.text.format.Formatter;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Switch;

import org.out.yslf.trueselftv.utils.EasyDialogBuilder;
import org.out.yslf.trueselftv.utils.FileShowTool;
import org.out.yslf.trueselftv.utils.LockScreenTool;
import org.out.yslf.trueselftv.utils.PermissionsTools;
import org.out.yslf.trueselftv.utils.ShareTool;
import org.out.yslf.trueselftv.utils.ToastTool;

import java.io.File;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PermissionsTools.verifyStoragePermissions(this);

        initView();
    }

    private void initView() {
        Switch switchNote = findViewById(R.id.main_switch);
        switchNote.setChecked(ShareTool.getBootEnabled(this));
        switchNote.setOnCheckedChangeListener((buttonView, isChecked) -> {
            ShareTool.setBootEnabled(this, isChecked);
            if (isChecked) {
                NoteManager.showBootNote(this);
            } else {
                NoteManager.hideBootNote(this);
            }
        });

        Switch switchNoteQq = findViewById(R.id.main_switch_qq);
        switchNoteQq.setChecked(ShareTool.getBootQqEnabled(this));
        switchNoteQq.setOnCheckedChangeListener((buttonView, isChecked) -> {
            ShareTool.setBootQqEnabled(this, isChecked);
            if (isChecked) {
                NoteManager.showBootQqNote(this);
            } else {
                NoteManager.hideBootQqNote(this);
            }
        });

        Switch switchLock = findViewById(R.id.main_switch_lock);
        switchLock.setChecked(ShareTool.getLockEnabled(this));
        switchLock.setOnCheckedChangeListener((buttonView, isChecked) -> {
            ShareTool.setLockEnabled(this, isChecked);
            if (isChecked) {
                NoteManager.showLockNote(this);
            } else {
                NoteManager.hideLockNote(this);
            }
        });
    }

    public void onVipCountClick(View view) {
        int count = ShareTool.getQiyiTimes(this);
        ToastTool.showLongToast(this, "从通知启动爱奇艺次数：" + count);
    }

    public void onVipCountResetClick(View view) {
        ShareTool.saveQiyiTimes(this, 0);
        ToastTool.showToast(this, "从通知启动爱奇艺次数：已重置为0次");
    }

    public void onVipTestClick(View view) {
        startActivity(NoteManager.getQiyiJumpIntent(this));
    }

    public void onBlackTestClick(View view) {
        BlackActivity.startBlackActivity(this);
    }

    public void onLockTestClick(View view) {
        LockScreenTool.lockScreen(this);
    }

    public void onLockManagerClick(View view) {
        LockScreenTool.removeLockSettings(this);
        ToastTool.showToast(this, "已重置设备管理器");
    }

    public void onWriteLaunchLogClick(View view) {
        ShareTool.writeLaunchTimeLog(this);
        ToastTool.showToast(this, "已写入测试的开机时间");
    }

    public void onShowSystemInfoClick(View view) {
        // 获取内存信息
        ActivityManager mActivityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        mActivityManager.getMemoryInfo(memoryInfo);
        long memSize = memoryInfo.totalMem;
        long availMem = memoryInfo.availMem;

        // 获取磁盘信息
        File path = Environment.getExternalStorageDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getBlockCount();
        String totalSdSize = Formatter.formatFileSize(this, blockSize * totalBlocks);
        long availableBlocks = stat.getAvailableBlocks();
        String availableSdSize = Formatter.formatFileSize(this, blockSize * availableBlocks);

        // 屏幕宽高
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        String screenSize = "屏幕尺寸：" + metrics.widthPixels + "x" + metrics.heightPixels + "\n"
                + "像素密度：" + metrics.density;

        // 显示对话框
        String tip = "内存(可用/全部)：" + FileShowTool.formatFileSize(availMem)
                + "/" + FileShowTool.formatFileSize(memSize) + "\n"
                + "磁盘(可用/全部)：" + availableSdSize + "/" + totalSdSize + "\n"
                + screenSize + "\n"
                + "BOARD:" + Build.BOARD + "\n"
                + "BRAND:" + Build.BRAND + "\n"
                + "CPU_ABI:" + Build.CPU_ABI + "\n"
                + "CPU_ABI2:" + Build.CPU_ABI2 + "\n"
                + "DEVICE:" + Build.DEVICE + "\n"
                + "DISPLAY:" + Build.DISPLAY + "\n"
                + "FINGERPRINT:" + Build.FINGERPRINT + "\n"
                + "HARDWARE:" + Build.HARDWARE + "\n"
                + "HOST:" + Build.HOST + "\n"
                + "ID:" + Build.ID + "\n"
                + "MANUFACTURER:" + Build.MANUFACTURER + "\n"
                + "MODEL:" + Build.MODEL + "\n"
                + "PRODUCT:" + Build.PRODUCT + "\n"
                + "TAGS:" + Build.TAGS + "\n"
                + "TIME:" + Build.TIME + "\n"
                + "TYPE:" + Build.TYPE + "\n"
                + "USER:" + Build.USER + "\n"
                + "VERSION.CODENAME:" + Build.VERSION.CODENAME + "\n"
                + "VERSION.RELEASE:" + Build.VERSION.RELEASE + "\n"
                + "VERSION.SDK_INT:" + Build.VERSION.SDK_INT;
        EasyDialogBuilder.builder(this)
                .setTitle("系统信息")
                .setMessage(tip)
                .setPositiveButton("确定", null)
                .show();
    }

    public void onLaunchLogClick(View view) { // 查看开机记录
        String text = ShareTool.readLaunchTimeLog(this).trim();
        EasyDialogBuilder.builder(this)
                .setTitle("开机记录")
                .setMessage(text)
                .setPositiveButton("确定", null)
                .show();
    }

    public void onShowKeyClick(View view) {
        startActivity(new Intent(this, ShowKeyActivity.class));
    }

    public void onAppManagerClick(View view) {
        startActivity(new Intent(this, AppShowActivity.class));
    }

    public void onOpenSettingClick(View view) {
        Intent intent = new Intent();
        intent.setClassName("com.android.settings",
                "com.android.settings.MainSettings");
        try {
            startActivity(intent);
        } catch (Exception e) {
            ToastTool.showToast(this, "打开失败");
        }
    }

    public void onFileManagerClick(View view) {
        startActivity(new Intent(this, FileShowActivity.class));
    }
}
