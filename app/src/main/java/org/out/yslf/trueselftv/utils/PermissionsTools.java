package org.out.yslf.trueselftv.utils;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Process;

/**
 * Created by YuShuLinFeng on 2017/5/16.
 */

public class PermissionsTools {
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {Manifest.permission.WRITE_EXTERNAL_STORAGE};

    public static boolean verifyStoragePermissions(Activity activity) {
        if (Build.VERSION.SDK_INT < 23) {
            return true;
        }
        int permission = activity.checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Process.myPid(), Process.myUid());
        boolean state = permission == PackageManager.PERMISSION_GRANTED;
        if (!state) {
            activity.requestPermissions(PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
        }
        return state;
    }

}
