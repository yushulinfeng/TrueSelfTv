package org.out.yslf.trueselftv;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.ScrollView;
import android.widget.TextView;

import org.out.yslf.trueselftv.utils.KeyMapTool;

import java.util.Locale;

/**
 * 显示按键的界面
 *
 * @author SunYuLin
 * @since 2019/2/11
 */
public class ShowKeyActivity extends Activity {
    private static final String KEY_MSG_FORMAT = "%s : %d";
    private ScrollView scrollView;
    private TextView textView;
    private KeyMapTool keyMapTool;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_key);
        scrollView = findViewById(R.id.show_key_scroll);
        textView = findViewById(R.id.show_key_tv);
        keyMapTool = new KeyMapTool();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        showKey(keyCode);
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return super.onKeyDown(keyCode, event);
        }
        return true;
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return super.onKeyUp(keyCode, event);
        }
        return true;
    }

    private void showKey(int keyCode) {
        String name = String.format(Locale.CHINA, KEY_MSG_FORMAT,
                keyMapTool.getKeyName(keyCode), keyCode);
        textView.append("\n");
        textView.append(name);
        scrollView.post(() -> scrollView.fullScroll(ScrollView.FOCUS_DOWN));
    }
}
