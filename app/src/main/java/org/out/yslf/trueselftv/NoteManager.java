package org.out.yslf.trueselftv;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

/**
 * 通知管理器
 *
 * @author sunyulin01
 * @since 2019-01-29
 */
public class NoteManager {
    private static final String NAME_PKG = "com.gitvdemo.video";
    private static final String NAME_CLS = "com.gala.video.app.epg.HomeActivity";
    private static final int NOTE_BOOT_ID = "org.out.yslf.tvnote".hashCode(); // 避免重复
    private static final int NOTE_LOCK_ID = NOTE_BOOT_ID + 1; // 避免重复
    private static final String NOTE_CHANNEL_ID = "tvnote_vip";
    private static final String NOTE_CHANNEL_NAME = "爱奇艺VIP";
    private static final String NOTE_TITLE = "爱奇艺VIP";
    private static final String NOTE_TEXT = "按下OK键进入:爱奇艺";
    private static final String NOTE_LOCK_TITLE = "手动锁屏";
    private static final String NOTE_LOCK_TEXT = "";

    public static Intent getQiyiIntent() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setClassName(NAME_PKG, NAME_CLS);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }

    public static void showBootNote(Context context) {
        Notification.Builder builder = new Notification.Builder(context);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, getQiyiIntent(), 0);
        builder.setContentIntent(pendingIntent);
        builder.setSmallIcon(R.drawable.ic_iqiyi);
        builder.setContentTitle(NOTE_TITLE);
        builder.setAutoCancel(false);
        builder.setOngoing(true);
        builder.setContentText(NOTE_TEXT);
        builder.setPriority(Notification.PRIORITY_HIGH);
        showNote(context, builder, NOTE_BOOT_ID);
    }

    public static void hideBootNote(Context context) {
        hideNote(context, NOTE_BOOT_ID);
    }

    public static void showLockNote(Context context) {
        Notification.Builder builder = new Notification.Builder(context);
        Intent intent = new Intent(context, BlackActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        builder.setContentIntent(pendingIntent);
        builder.setSmallIcon(R.drawable.shape_null);
        builder.setContentTitle(NOTE_LOCK_TITLE);
        builder.setAutoCancel(false);
        builder.setOngoing(true);
        builder.setContentText(NOTE_LOCK_TEXT);
        builder.setPriority(Notification.PRIORITY_LOW);
        showNote(context, builder, NOTE_LOCK_ID);
    }

    public static void hideLockNote(Context context) {
        hideNote(context, NOTE_LOCK_ID);
    }

    /**
     * 获取通知管理器
     */
    private static NotificationManager getNoteManager(Context context) {
        return (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    /**
     * 显示通知
     */
    private static void showNote(Context context, Notification.Builder builder, int noteId) {
        NotificationManager notificationManager = getNoteManager(context);
        if (notificationManager == null) {
            ToastManager.showToast(context, "无法获取通知管理器");
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //适配安卓8.0的消息渠道
            NotificationChannel channel = new NotificationChannel(NOTE_CHANNEL_ID,
                    NOTE_CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
            builder.setChannelId(NOTE_CHANNEL_ID);
        }
        Notification notification = builder.build();
        notificationManager.notify(noteId, notification);
    }

    /**
     * 隐藏通知
     */
    private static void hideNote(Context context, int noteId) {
        NotificationManager notificationManager = getNoteManager(context);
        if (notificationManager == null) {
            ToastManager.showToast(context, "无法获取通知管理器");
            return;
        }
        notificationManager.cancel(noteId);
    }
}
