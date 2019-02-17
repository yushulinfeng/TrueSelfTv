package org.out.yslf.trueselftv;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.widget.TextView;

import org.out.yslf.trueselftv.utils.ToastTool;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

/**
 * 文本文件显示界面
 *
 * @author SunYuLin
 * @since 2019/2/17
 */
public class TextActivity extends Activity {
    private static final int UNKNON_FILE_MAX_KB = 1024; // 未知文件最大解析大小
    private static final String KEY_TEXT_PATH = "text_path";
    private static final String ERROR_TIP = "文件加载失败";
    private static final String CODE_UTF8 = "utf-8", CODE_GBK = "gbk";
    private TextView tvTitle, tvText;
    private String textPath;
    private String currFormt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text);
        textPath = getIntent().getStringExtra(KEY_TEXT_PATH);

        initView();
        loadTitle();
        loadText(CODE_UTF8);
    }

    private void initView() {
        tvTitle = findViewById(R.id.tv_title);
        tvText = findViewById(R.id.tv_text);
    }

    private void loadTitle() {
        String title = TextUtils.isEmpty(textPath)
                ? "文本文件"
                : textPath.substring(textPath.lastIndexOf("/") + 1);
        tvTitle.setText(title);
    }

    private void loadText(String codeFormat) {
        currFormt = codeFormat;
        // 路径保护
        if (TextUtils.isEmpty(textPath)) {
            tvText.setText(ERROR_TIP);
            return;
        }
        File textFile = new File(textPath);
        if (!textFile.exists() && !textFile.isFile()) {
            tvText.setText(ERROR_TIP);
            return;
        }
        // 类型保护，非文本文件，大于 UNKNON_FILE_MAX_KB 直接认为错误
        int fileType = MediaItem.getFileType(textPath);
        if (fileType != MediaItem.TYPE_LYRIC && fileType != MediaItem.TYPE_TEXT) {
            if (MediaItem.getFileSize(textPath) / 1024 > UNKNON_FILE_MAX_KB) {
                tvText.setText(ERROR_TIP);
                return;
            }
        }
        // 加载文件，直接一次加载完毕
        try {
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(new FileInputStream(textFile), codeFormat));
            StringBuilder paragraph = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                paragraph.append(line);
                paragraph.append("\n");
            }
            reader.close();//仅关闭外部流即可
            tvText.setText(paragraph.toString());
        } catch (Exception e) {
            e.printStackTrace();
            tvText.setText(ERROR_TIP);
        }
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            loadText(CODE_UTF8.equals(currFormt) ? CODE_GBK : CODE_UTF8);
            ToastTool.showToast(this, "已通过 " + currFormt + " 重新解析文件");
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    /**
     * 显示文本
     */
    public static void showText(Context context, String path) {
        Intent intent = new Intent(context, TextActivity.class);
        intent.putExtra(KEY_TEXT_PATH, path);
        context.startActivity(intent);
    }
}
