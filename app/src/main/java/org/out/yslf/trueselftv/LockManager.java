package org.out.yslf.trueselftv;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

/**
 * 锁屏管理器
 *
 * @author sunyulin01
 * @since 2019-02-06
 */
public class LockManager {

    /**
     * 锁屏
     */
    public static void lockScreen(Context context) {
        DevicePolicyManager policyManager =
                (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        if (policyManager == null) {
            ToastManager.showToast(context, "设备管理器为空");
            return;
        }
        ComponentName componentName = new ComponentName(context, LockReceiver.class);
        if (!policyManager.isAdminActive(componentName)) {
            ToastManager.showToast(context, "请提供锁屏授权");
            showLockSettings(context);
            return;
        }
        try {
            policyManager.lockNow();
        } catch (Exception e) {
            ToastManager.showToast(context, "锁屏失败");
        }
    }

    /**
     * 显示设备管理器
     */
    private static void showLockSettings(Context context) {
        ComponentName componentName = new ComponentName(context, LockReceiver.class);
        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName);
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "激活后才能使用锁屏功能");
        try {
            context.startActivity(intent);
        } catch (Exception e) {
            ToastManager.showToast(context, "错误：" + e.getMessage());
        }
    }

    /**
     * 移除设备管理器
     */
    public static void removeLockSettings(Context context) {
        DevicePolicyManager policyManager =
                (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        if (policyManager == null) {
            ToastManager.showToast(context, "设备管理器为空");
            return;
        }
        ComponentName componentName = new ComponentName(context, LockReceiver.class);
        policyManager.removeActiveAdmin(componentName);
    }
}
