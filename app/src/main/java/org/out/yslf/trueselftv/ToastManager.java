package org.out.yslf.trueselftv;

import android.content.Context;
import android.widget.Toast;

/**
 * 吐司通知管理器
 *
 * @author sunyulin01
 * @since 2019-01-29
 */
public class ToastManager {

    public static void showToast(Context context, Object msg) {
        Toast.makeText(context, String.valueOf(msg), Toast.LENGTH_SHORT).show();
    }

}
