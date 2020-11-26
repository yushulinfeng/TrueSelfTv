package org.out.yslf.trueselftv;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Switch;

import org.out.yslf.trueselftv.utils.EasyDialogBuilder;
import org.out.yslf.trueselftv.utils.LockScreenTool;
import org.out.yslf.trueselftv.utils.PermissionsTools;
import org.out.yslf.trueselftv.utils.ShareTool;
import org.out.yslf.trueselftv.utils.ToastTool;

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
