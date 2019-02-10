package org.out.yslf.trueselftv;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;

/**
 * 黑屏界面
 *
 * @author sunyulin01
 * @since 2019-02-06
 */
public class BlackActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_black);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        finish();
        return super.onKeyDown(keyCode, event);
    }

    public static void startBlackActivity(Context context) {
        Intent intent = new Intent(context, BlackActivity.class);
        context.startActivity(intent);
    }
}
