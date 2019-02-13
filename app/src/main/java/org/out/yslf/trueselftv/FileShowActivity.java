package org.out.yslf.trueselftv;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.TextView;

import org.out.yslf.trueselftv.utils.FileShowTool;
import org.out.yslf.trueselftv.utils.ToastTool;

import java.util.List;

/**
 * 文件管理界面
 *
 * @author SunYuLin
 * @since 2019/2/11
 */
public class FileShowActivity extends Activity implements OnItemClickListener, OnItemLongClickListener {
    private ListView fileListView;
    private TextView pathTextView;
    private TextView emptyTipView;
    private List<MediaItem> items;
    private FileShowAdapter adapter;
    private FileShowTool fileTool;
    private String currentPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_show);

        initView();
    }

    private void initView() {
        currentPath = null;
        fileTool = new FileShowTool(this);
        items = fileTool.getPathFiles(currentPath);
        adapter = new FileShowAdapter(this, items);
        emptyTipView = findViewById(R.id.file_show_empty);
        pathTextView = findViewById(R.id.file_show_path);
        fileListView = findViewById(R.id.file_show_list);
        fileListView.setAdapter(adapter);
        fileListView.setOnItemClickListener(this);
        fileListView.setOnItemLongClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (items == null) {
            return;
        }
        MediaItem item = items.get(position);
        if (item.isFolder()) {
            showPathFiles(item.getRealPath());
            return;
        }
        // 文件处理
        // TODO: 2019/2/13 处理文件，音频可直接播放，其他的可考虑调起播放器
        // TODO: 2019/2/13 试试4.4手机，能否读取到根目录
        ToastTool.showToast(this, "文件：" + item.getName());
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        if (currentPath == null) {
            return true; // 根目录禁止操作
        }
        MediaItem item = items.get(position);
        // TODO: 2019/2/13 处理长按事件（即菜单事件）（其他的不必支持了）：打开（音频也走调起），复制，剪切，删除
        ToastTool.showToast(this, "长按：" + item.getName());
        return true;
    }

    private void showPathFiles(String path) {
        List<MediaItem> mediaItems = fileTool.getPathFiles(path);
        if (mediaItems == null) {
            ToastTool.showToast(this, "本条目不是文件夹");
            return;
        }
        currentPath = path;
        pathTextView.setText(TextUtils.isEmpty(path) ? "根目录" : path);
        items.clear();
        items.addAll(mediaItems);
        emptyTipView.setVisibility(items.isEmpty() ? View.VISIBLE : View.GONE);
        adapter.notifyDataSetChanged();
        fileListView.setSelection(0);
    }

    private void onMenuPress() {
        int position = fileListView.getSelectedItemPosition();
        onItemLongClick(null, null, position, 0);
    }

    private void onBackPress() {
        if (TextUtils.isEmpty(currentPath)) {
            finish();
            return;
        }
        showPathFiles(fileTool.getParentPath(currentPath));
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return true;
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            onMenuPress();
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_BACK) {
            onBackPress();
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }
}
