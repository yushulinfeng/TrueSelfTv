package org.out.yslf.trueselftv;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.out.yslf.trueselftv.utils.EasyDialogBuilder;
import org.out.yslf.trueselftv.utils.FileShowTool;
import org.out.yslf.trueselftv.utils.ToastTool;

import java.io.File;
import java.util.List;

/**
 * 文件管理界面
 *
 * @author SunYuLin
 * @since 2019/2/11
 */
public class FileShowActivity extends Activity
        implements OnItemClickListener, OnItemLongClickListener {
    private ListView fileListView;
    private LinearLayout pastePanel;
    private TextView pathTextView;
    private TextView emptyTipView;
    private TextView copyPathView;
    private List<MediaItem> items;
    private FileShowAdapter adapter;
    private FileShowTool fileTool;
    private String currentPath;
    private String copyPath;
    private boolean isClip;

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
        pastePanel = findViewById(R.id.file_show_panel);
        copyPathView = findViewById(R.id.file_show_copy_path);
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
            // TODO: 2019/2/13 试试4.4手机，能否读取到根目录
            showPathFiles(item.getRealPath());
        } else {
            fileTool.openFile(this, item.getRealPath());
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        if (currentPath == null) {
            return true; // 根目录禁止操作
        }
        MediaItem item = items.get(position);
        final String fileName = item.getName();
        final String filePath = item.getRealPath();
        EasyDialogBuilder.builder(this)
                .setTitle("[ " + item.getName() + " ]")
                .addItem(item.getType() == MediaItem.TYPE_AUDIO,
                        "打开", () -> openAudioFile(filePath))
                .addItem("复制", () -> startCopyFile(filePath, false))
                .addItem("剪切", () -> startCopyFile(filePath, true))
                .addItem("删除", () -> EasyDialogBuilder.builder(this)
                        .setTitle("删除：" + item.getName())
                        .setMessage("确认删除？操作不可恢复。")
                        .setPositiveButton("确定", () -> {
                            boolean success = fileTool.deleteFile(new File(filePath));
                            showPathFiles(currentPath);
                            ToastTool.showToast(this, fileName + " : "
                                    + (success ? "删除成功" : "删除失败"));
                        })
                        .setNegativeButton("取消", null)
                        .show())
                .show();
        return true;
    }

    public void onDoPasteClick(View view) {
        if (copyPath == null) {
            onCancelPasteClick(null);
            ToastTool.showToast(this, "系统错误");
            return;
        }
        if (TextUtils.isEmpty(currentPath)) {
            ToastTool.showToast(this, "根目录不支持粘贴");
            return;
        }
        if (currentPath.startsWith(copyPath)) {
            ToastTool.showToast(this, "不能粘贴到同一目录");
            return;
        }
        boolean succ = isClip
                ? fileTool.moveFile(copyPath, currentPath)
                : fileTool.copyFile(copyPath, currentPath);
        ToastTool.showToast(this, succ ? "粘贴成功" : "操作失败");
        onCancelPasteClick(null);
        showPathFiles(currentPath);
    }

    public void onCancelPasteClick(View view) {
        pastePanel.setVisibility(View.GONE);
        copyPath = null;
        isClip = false;
    }

    private void startCopyFile(String path, boolean clip) {
        if (TextUtils.isEmpty(path)) {
            return;
        }
        copyPathView.setText(clip ? "已剪切：\n" : "已复制：\n");
        copyPathView.append(path);
        pastePanel.setVisibility(View.VISIBLE);
        copyPath = path;
        isClip = clip;
    }

    private void openAudioFile(String filePath) {
        // TODO: 2019/2/15 打开音乐文件
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
        if (!TextUtils.isEmpty(currentPath)) {
            showPathFiles(fileTool.getParentPath(currentPath));
        } else if (copyPath != null) {
            onCancelPasteClick(null);
        } else {
            finish();
        }
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
