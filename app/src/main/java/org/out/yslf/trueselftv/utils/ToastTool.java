package org.out.yslf.trueselftv.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * 吐司通知管理器
 *
 * @author sunyulin01
 * @since 2019-01-29
 */
public class ToastTool {
    // 暂时不开率其对内存的影响
    private static Toast toast;

    public static void showToast(Context context, Object msg) {
        if (toast != null) toast.cancel();
        toast = Toast.makeText(context, String.valueOf(msg), Toast.LENGTH_SHORT);
        toast.show();
    }

    public static void showLongToast(Context context, Object msg) {
        if (toast != null) toast.cancel();
        toast = Toast.makeText(context, String.valueOf(msg), Toast.LENGTH_LONG);
        toast.show();
    }
}
