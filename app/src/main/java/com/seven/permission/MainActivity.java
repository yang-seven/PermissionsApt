package com.seven.permission;

import android.Manifest;
import android.content.DialogInterface;
import android.support.annotation.NonNull;

import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;


import com.seven.annotations.NeedPermissions;
import com.seven.annotations.OnNeverAskPermissions;
import com.seven.annotations.OnPermissionsDenied;
import com.seven.permissions.library.PermissionManager;
import com.seven.permissions.library.dialog.AppSettingDialog;


public class MainActivity extends AppCompatActivity {
    private String TAG = "MainActivity";
    private String permissions[] = new String[]{Manifest.permission.CAMERA};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PermissionManager.requestPermission(this, new String[]{Manifest.permission.CAMERA});
//        new MainActivity$Permissions().requestPermission(this, new String[]{Manifest.permission.CAMERA});
        Log.d(TAG, "onCreate: " + getRationale());

    }

    private boolean getRationale() {
        //默认进来是false 点了禁止变成true 点了禁止且不再询问返回false 所以在onRequestPermissionsResult 这个里面去判断比较好，如果点了禁止且不再询问
        return ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA);
    }


    @NeedPermissions()
    public void hasGranted() {

        Toast.makeText(this, "您已通过此权限，可以自由的玩耍了！", Toast.LENGTH_LONG).show();

    }

    /**
     * 点了禁止且不再询问 弹出对话框 提示用户去设置界面打开
     */
    @OnNeverAskPermissions()
    public void showNeverAskForPermission() {
        Toast.makeText(this, "showNeverAskForPermission！", Toast.LENGTH_LONG).show();
        new AppSettingDialog.Builder(this).setRequestCode(AppSettingDialog.ACTIVITY_SETTING_CODE).setListener(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //当点击了拒绝去请求
                Toast.makeText(MainActivity.this, "您已拒绝某些权限，可能会导致应用某些功能异常！", Toast.LENGTH_LONG).show();
            }
        }).build().show();
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == AppSettingDialog.ACTIVITY_SETTING_CODE) {
//
//            if (PermissionUtils.hasSelfPermissions(this, permissions)) {
//                // 简单的判断处理
//            }
//        }
//    }

    /**
     * 拒绝了
     */
    @OnPermissionsDenied()
    public void showDeniedForPermission() {
        Toast.makeText(this, "您已拒绝某些权限，可能会导致应用某些功能异常！", Toast.LENGTH_LONG).show();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionManager.onRequestPermissionResult(this, requestCode, grantResults);
    }
}
