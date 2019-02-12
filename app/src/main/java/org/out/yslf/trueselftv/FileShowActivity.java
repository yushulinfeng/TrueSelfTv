package org.out.yslf.trueselftv;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 文件管理界面
 *
 * @author SunYuLin
 * @since 2019/2/11
 */
public class FileShowActivity extends Activity implements OnItemClickListener {
    private List<MediaItem> items;
    private FileShowAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_show);

        initView();
    }

    private void initView() {
        ListView listView = findViewById(R.id.file_show_list);
        items = new ArrayList<>();
        adapter = new FileShowAdapter(this, items);
        listView.setAdapter(adapter);

        File sdRoot = Environment.getExternalStorageDirectory();
        for (File file : sdRoot.listFiles()) {
            items.add(new MediaItem(file.getAbsolutePath()));
        }
        Collections.sort(items);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }
}
