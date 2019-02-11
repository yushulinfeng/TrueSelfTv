package org.out.yslf.trueselftv;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Switch;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
    }

    private void initView() {
        Switch switchNote = findViewById(R.id.main_switch);
        switchNote.setChecked(ShareManager.getBootEnabled(this));
        switchNote.setOnCheckedChangeListener((buttonView, isChecked) -> {
            ShareManager.setBootEnabled(this, isChecked);
            if (isChecked) {
                NoteManager.showBootNote(this);
            } else {
                NoteManager.hideBootNote(this);
            }
        });

        Switch switchLock = findViewById(R.id.main_switch_lock);
        switchLock.setChecked(ShareManager.getLockEnabled(this));
        switchLock.setOnCheckedChangeListener((buttonView, isChecked) -> {
            ShareManager.setLockEnabled(this, isChecked);
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
            ToastManager.showToast(this, "请先安装相关应用");
        }
    }

    public void onBlackTestClick(View view) {
        BlackActivity.startBlackActivity(this);
    }

    public void onLockTestClick(View view) {
        LockManager.lockScreen(this);
    }

    public void onLockManagerClick(View view) {
        LockManager.removeLockSettings(this);
        ToastManager.showToast(this, "已重置设备管理器");
    }

    public void onShowKeyClick(View view) {
        startActivity(new Intent(this, ShowKeyActivity.class));
    }

    public void onAppManagerClick(View view) {
    }

    public void onFileManagerClick(View view) {
    }
}
