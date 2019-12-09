package com.seven.permissions.library.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.text.TextUtils;


/**
 * Time:2019/12/4
 * <p>
 * Author:wangzhou
 * <p>
 * Description:
 */
public class AppSettingDialog implements DialogInterface.OnClickListener {

    public static final int ACTIVITY_SETTING_CODE = 333;
    private Activity activity;
    private String positiveButton, negativeButton, title, message;
    private DialogInterface.OnClickListener listener;
    private int requestCode;

    @Override
    public void onClick(DialogInterface dialog, int which) {
        Intent intent=new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri=Uri.fromParts("package",activity.getPackageName(),null);
        intent.setData(uri);
        activity.startActivityForResult(intent,requestCode);
    }

    private AppSettingDialog(Builder builder) {
        this.title = builder.title;
        this.message = builder.message;
        this.positiveButton = builder.positiveButton;
        this.negativeButton = builder.negativeButton;
        this.listener = builder.listener;
        this.requestCode = builder.requestCode;
        this.activity=builder.activity;

    }

    public void show() {
        if (listener != null) {
            showDialog();
        } else {
            throw new IllegalArgumentException("对话框监听不能为空！");
        }
    }

    private void showDialog() {
        new AlertDialog.Builder(activity)
                .setCancelable(false)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(positiveButton, this)
                .setNegativeButton(negativeButton, listener)
                .create()
                .show();
    }

    public static class Builder {
        private Activity activity;
        private String positiveButton, negativeButton, title, message;
        private DialogInterface.OnClickListener listener;
        private int requestCode;


        public Builder(Activity activity) {
            this.activity = activity;

        }

        public Builder setMessage(String msg) {
            this.message = msg;
            return this;
        }

        public Builder setRequestCode(int requestCode) {
            this.requestCode = requestCode;
            return this;
        }

        public Builder setListener(DialogInterface.OnClickListener listener) {
            this.listener = listener;
            return this;
        }


        public AppSettingDialog build() {
            this.title = "重要的权限";
            this.message = TextUtils.isEmpty(message) ? "打开设置，启动权限" : message;
            this.positiveButton =activity.getString(android.R.string.ok);
            this.negativeButton ="取消";
            this.requestCode = requestCode > 0 ? requestCode : ACTIVITY_SETTING_CODE;
            return new AppSettingDialog(this);
        }
    }
}
