package org.out.yslf.trueselftv;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Switch;

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

    public void onVipTestClick(View view) {
        try {
            startActivity(NoteManager.getQiyiIntent());
        } catch (Exception e) {
            ToastTool.showToast(this, "请先安装相关应用");
        }
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

    public void onShowKeyClick(View view) {
        startActivity(new Intent(this, ShowKeyActivity.class));
    }

    public void onAppManagerClick(View view) {
        startActivity(new Intent(this, AppShowActivity.class));
    }

    public void onFileManagerClick(View view) {
        startActivity(new Intent(this, FileShowActivity.class));
    }
}
