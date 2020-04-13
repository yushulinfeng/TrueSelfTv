package org.out.yslf.trueselftv;

import android.app.Activity;
import android.os.Bundle;

import org.out.yslf.trueselftv.utils.ShareTool;
import org.out.yslf.trueselftv.utils.ToastTool;

/**
 * 爱奇艺跳转界面，用于打点统计某些数据
 *
 * @author sunyulin01
 * @since 2020/4/13
 */
public class QiyiJumpActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        doUbcRecord();
        startQiyi();
        finish();
    }

    private void doUbcRecord() {
        ShareTool.saveQiyiTimes(this, ShareTool.getQiyiTimes(this) + 1);
    }

    private void startQiyi() {
        try {
            startActivity(NoteManager.getQiyiIntent());
        } catch (Exception e) {
            ToastTool.showToast(this, "请先安装相关应用");
        }
    }
}
