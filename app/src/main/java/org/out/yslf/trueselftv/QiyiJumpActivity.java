package org.out.yslf.trueselftv;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import org.out.yslf.trueselftv.utils.ShareTool;
import org.out.yslf.trueselftv.utils.ToastTool;

/**
 * 爱奇艺跳转界面，用于打点统计某些数据
 *
 * @author sunyulin01
 * @since 2020/4/13
 */
public class QiyiJumpActivity extends Activity {
    public static final String KEY_TYPE = "type";
    public static final String VALUE_QQ_VIDEO = "QqVideo";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        doUbcRecord();
        Log.e("HelloWorld",getIntent().getStringExtra(KEY_TYPE)+"");

        if (TextUtils.equals(getIntent().getStringExtra(KEY_TYPE), VALUE_QQ_VIDEO)) {
            startQqVideo();
        } else {
            startQiyi();
        }
        finish();
    }

    private void doUbcRecord() {
        ShareTool.saveQiyiTimes(this, ShareTool.getQiyiTimes(this) + 1);
    }

    private void startQiyi() {
        try {
            startActivity(NoteManager.getQiyiIntent());
        } catch (Exception e) {
            e.printStackTrace();
            ToastTool.showToast(this, "请先安装相关应用");
        }
    }

    private void startQqVideo() {
        try {
            startActivity(NoteManager.getQqVideoIntent());
        } catch (Exception e) {
            e.printStackTrace();
            ToastTool.showToast(this, "请先安装相关应用");
        }
    }
}
