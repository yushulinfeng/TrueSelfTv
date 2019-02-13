package org.out.yslf.trueselftv.utils;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author SunYuLin (sunyulin_sx@qiyi.com)
 */

public class EasyDialogBuilder {
    private AlertDialog.Builder builder;
    private Map<String, ItemClickListener> map;
    private String[] menu;

    private EasyDialogBuilder(Context context) {
        builder = new AlertDialog.Builder(context);
        map = new LinkedHashMap<>();//有序
    }

    public static EasyDialogBuilder builder(Context context) {
        return new EasyDialogBuilder(context);
    }

    public EasyDialogBuilder setTitle(String title) {
        builder.setTitle(title);
        return this;
    }

    public EasyDialogBuilder setIcon(int iconRes) {
        builder.setIcon(iconRes);
        return this;
    }

    public EasyDialogBuilder setItems(CharSequence[] items, DialogInterface.OnClickListener listener) {
        builder.setItems(items, listener);
        return this;
    }

    public EasyDialogBuilder setMessage(String message) {
        builder.setMessage(message);
        return this;
    }

    public EasyDialogBuilder setCancelable(boolean cancelable) {
        builder.setCancelable(cancelable);
        return this;
    }

    public EasyDialogBuilder setPositiveButton(String name, ItemClickListener listener) {
        builder.setPositiveButton(name, (dialog, which) -> {
            if (listener != null) listener.onItemClick();
        });
        return this;
    }

    public EasyDialogBuilder setNeutralButton(String name, ItemClickListener listener) {
        builder.setNeutralButton(name, (dialog, which) -> {
            if (listener != null) listener.onItemClick();
        });
        return this;
    }

    public EasyDialogBuilder setNegativeButton(String name, ItemClickListener listener) {
        builder.setNegativeButton(name, (dialog, which) -> {
            if (listener != null) listener.onItemClick();
        });
        return this;
    }

    public EasyDialogBuilder addItem(String name, ItemClickListener listener) {
        map.put(name, listener);
        return this;
    }

    public EasyDialogBuilder addConfirmItem(String name, String confirm,
                                            ItemClickListener listener) {
        map.put(name, () -> new AlertDialog.Builder(builder.getContext())
                .setTitle(confirm)
                .setPositiveButton("确定", (dialog, which) -> listener.onItemClick())
                .setNegativeButton("取消", null)
                .show());
        return this;
    }

    // 用于高程度度自定义
    public EasyDialogBuilder doWithBuilder(BuilderListener listener) {
        if (listener != null) {
            listener.doWithBuilder(builder);
        }
        return this;
    }

    public AlertDialog build() {
        if (map.size() != 0) {
            buildMapItems();
        }
        return builder.create();
    }

    public void show() {
        build().show();
    }

    private void buildMapItems() {
        menu = map.keySet().toArray(new String[]{});
        setItems(menu, (dialog, which) -> map.get(menu[which]).onItemClick());
    }

    public interface ItemClickListener {
        void onItemClick();
    }

    public interface BuilderListener {
        void doWithBuilder(AlertDialog.Builder builder);
    }
}
