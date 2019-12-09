package com.seven.permissions.library;


import android.util.Log;

import com.seven.permissions.library.listener.RequestPermission;

import java.util.HashMap;
import java.util.Map;


/**
 * Time:2019/12/5
 * <p>
 * Author:wangzhou
 * <p>
 * Description:
 */
public class PermissionManager {
    private static String TAG = "PermissionManager";
    private static Map<String, Object> map = new HashMap<>();

    public static void requestPermission(Object target, String[] permissions) {
        Class<?> targetClass = target.getClass();
        String className = targetClass.getName() + "$Permissions";
        try {
            Class<?> forName = Class.forName(className);

            RequestPermission requestPermission = (RequestPermission) forName.newInstance();
            map.put(className, requestPermission);
            requestPermission.requestPermission(target, permissions);

            Log.d(TAG, "requestPermission: "+requestPermission);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }


    }

    public static void onRequestPermissionResult(Object target, int requestCode, int[] grantResults) {

        Class<?> targetClass = target.getClass();
        String className = targetClass.getName() + "$Permissions";
        try {
            Class<?> forName = Class.forName(className);
            RequestPermission requestPermission = null;
            if (map.get(className) != null) {
                requestPermission = (RequestPermission) map.get(className);
            } else {
                requestPermission = (RequestPermission) forName.newInstance();
            }
            Log.d(TAG, " onRequestPermissionResult : "+requestPermission);
            requestPermission.onRequestPermissionsResult(target, requestCode, grantResults);

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
    }
}
