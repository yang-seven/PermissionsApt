package com.seven.permissions.library.listener;

/**
 * Time:2019/12/5
 * <p>
 * Author:wangzhou
 * <p>
 * Description:
 */
public interface RequestPermission <T> {
    /**
     *  请求权限组
     * @param target
     * @param permission
     */
    void requestPermission(T  target,String []permission);

    /**
     * 授权结果返回
     * @param target
     * @param requestCode
     * @param grantResults
     */
    void onRequestPermissionsResult(T  target,int requestCode,int []grantResults);
}
