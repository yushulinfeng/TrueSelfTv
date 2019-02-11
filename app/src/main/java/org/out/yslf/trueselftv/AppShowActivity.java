package org.out.yslf.trueselftv;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * 所有软件管理
 *
 * @author sunyulin01
 * @since 2019-02-11
 */
public class AppShowActivity extends Activity implements OnItemClickListener, Runnable {
    private List<ResolveInfo> apps;
    private GridView drawer_grid;
    private List<HashMap<String, Object>> imagelist;
    private SimpleAdapter simpleAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_show);
        initView();

        new Thread(this).start();
    }


    private void initView() {
        TextView pathView = findViewById(R.id.app_show_back_path);
        pathView.setText("（备份路径：" + BackupManager.getBackupPath() + "）");
        drawer_grid = findViewById(R.id.activity_app_grid_allapp);
        imagelist = Collections.synchronizedList(new ArrayList<>());
        simpleAdapter = new SimpleAdapter(this,
                imagelist, R.layout.activity_app_show_item,
                new String[]{"image", "text"},
                new int[]{R.id.app_icon, R.id.app_title});
        simpleAdapter.setViewBinder(new BinderTool());
        drawer_grid.setAdapter(simpleAdapter);
        drawer_grid.setOnItemClickListener(this);
    }

    private void loadApps() {
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        apps = getPackageManager().queryIntentActivities(intent, 0);
        for (ResolveInfo ri : apps) {
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("image", ri.activityInfo.loadIcon(getPackageManager()));
            map.put("text", ri.activityInfo.loadLabel(getPackageManager()));
            imagelist.add(map);
            runOnUiThread(() -> simpleAdapter.notifyDataSetChanged());
        }
    }

    private String getAppInfoShow(ActivityInfo info) {
        StringBuilder builder = new StringBuilder();
        builder.append("包名： ");
        builder.append(info.packageName);
        builder.append("\n");
        builder.append("类名： ");
        builder.append(info.name);
        builder.append("\n");
        PackageInfo pkgInfo;
        try {
            pkgInfo = getPackageManager().getPackageInfo(info.packageName, 0);
        } catch (Exception e) {
            e.printStackTrace();
            return builder.toString().trim();
        }
        builder.append("版本： ");
        builder.append(pkgInfo.versionName);
        builder.append("\n");
        builder.append("版号： ");
        builder.append(pkgInfo.versionCode);
        builder.append("\n");
        return builder.toString().trim();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final ActivityInfo info = apps.get(position).activityInfo;
        new AlertDialog.Builder(this)
                .setTitle(info.loadLabel(getPackageManager()))
                .setMessage(getAppInfoShow(info))
                .setPositiveButton("更多操作", (dialog, which) -> moreOperation(info))
                .show();
    }

    private void moreOperation(final ActivityInfo info) {
        String[] menuItem = {"启动", "备份", "卸载"};
        new AlertDialog.Builder(this)
                .setTitle("更多操作：" + info.loadLabel(getPackageManager()))
                .setItems(menuItem, (dialog, which) -> {
                    switch (which) {
                        case 0:
                            startActivityComplete(this, info.packageName, info.name);
                            break;
                        case 1:
                            BackupManager.backupApp(this, info, getPackageManager());
                            break;
                        case 2:
                            uninstallApk(info.packageName);
                            break;
                    }
                })
                .show();
    }

    @Override
    public void run() {
        loadApps();
    }

    private void startActivityComplete(Context context, String pkg, String name) {
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        intent.setClassName(pkg, name);
        try {
            context.startActivity(intent);
        } catch (Exception e) {
            ToastManager.showToast(this, "启动失败");
        }
    }

    private void uninstallApk(String pkgName) {
        try {
            Uri uri = Uri.parse("package:" + pkgName);
            Intent intent = new Intent(Intent.ACTION_DELETE, uri);
            startActivity(intent);
        } catch (Exception e) {
            ToastManager.showToast(this, "卸载失败");
        }

    }

    public class BinderTool implements SimpleAdapter.ViewBinder {
        @Override
        public boolean setViewValue(View view, Object data,
                                    String textRepresentation) {
            if (view instanceof ImageView) {
                ImageView iv = (ImageView) view;
                if (data instanceof BitmapDrawable) {
                    Bitmap bmp = ((BitmapDrawable) data).getBitmap();
                    iv.setImageBitmap(bmp);
                    return true;
                }
                if (data instanceof Bitmap) {
                    Bitmap bmp = (Bitmap) data;
                    iv.setImageBitmap(bmp);
                    return true;
                }
            }
            return false;
        }
    }
}
