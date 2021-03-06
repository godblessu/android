package com.tym.shortvideo.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;


public class PermissionUtils {

    private PermissionUtils() {}

    /**
     * 检查某个权限是否授权
     * @param permission
     * @return
     */
    public static boolean permissionChecking(Context context, String permission) {
        if (ContextCompat.checkSelfPermission(context, permission)
                == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }
}
