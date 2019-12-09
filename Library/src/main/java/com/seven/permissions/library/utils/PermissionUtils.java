package com.seven.permissions.library.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

/**
 * Time:2019/12/5
 * <p>
 * Author:wangzhou
 * <p>
 * Description:
 */
public class PermissionUtils {


    public static boolean verifyPermissions(int[] grantResults) {
        for (int grant :
                grantResults) {
            if (grant != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    public static boolean hasSelfPermissions(Context context, String ...permissions) {
        for (String permission :
                permissions) {
            if (!hasSelfPermission(context, permission))
                return false;

        }
        return true;

    }

    public static boolean hasSelfPermission(Context context, String permission) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
    }

    /** 第一次打开时返回false
     * 上次弹出权限点击了禁止（但没有勾选“下次不在询问”）true
     * 上次选择禁止并勾选：下次不在询问  返回false
     *
     * @param activity
     * @param permissions
     * @return
     */
    public static boolean shouldShowRequestPermissionRationale(Activity activity, String... permissions) {
        for (String permission : permissions) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                return true;
            }
        }
        return false;
    }


}
