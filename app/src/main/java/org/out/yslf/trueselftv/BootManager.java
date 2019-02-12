package org.out.yslf.trueselftv;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import org.out.yslf.trueselftv.utils.ShareTool;

/**
 * 启动管理器
 *
 * @author sunyulin01
 * @since 2019-01-29
 */
public class BootManager extends BroadcastReceiver {
    private static final String BOOT_COMPLETE = "android.intent.action.BOOT_COMPLETED";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null || !BOOT_COMPLETE.equals(intent.getAction())) {
            return;
        }
        if (ShareTool.getBootEnabled(context)) {
            NoteManager.showBootNote(context);
        }
        if (ShareTool.getLockEnabled(context)) {
            NoteManager.showLockNote(context);
        }
    }

}
