package com.starrtc.demo.utils;

/**
 * Created by zhangjt on 2018/3/16.
 */

import android.app.Activity;
import android.content.ContentResolver;
import android.content.pm.ActivityInfo;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.view.OrientationEventListener;

/**
 * 该类可以对Activity旋转和方向进行更加灵活的控制。
 * 注意，使用该类进行方向控制的Activity不要在清单文件中添加：
 * android:configChanges="orientation"
 *
 * 典型的应用场景：
 * 视频播放器的屏幕方向锁功能。
 * 当锁住屏幕方向后，Activity就不会随着手机方向的旋转而改变方向。一旦打开锁，Activity将会立即随着屏幕的方向而改变。
 *
 * 一般调用代码：
 *
 * 默认打开锁
 * ActivityRotationController controller=new ActivityRotationController(this);
 *
 * 打开锁
 * controller.openActivityRotation();
 *
 * 关闭锁
 * controller.closeActivityRotation();
 *
 * 关闭监听，恢复到系统之前旋转设定
 * controller.disable()
 *
 * 要求的权限
 * @permission android.permission.WRITE_SETTINGS
 */

public class ActivityRotationController extends OrientationEventListener {
    private int systemRotation;
    private boolean activityRotation;
    private int activityOrientation;
    private Activity activity;

    public ActivityRotationController(Activity activity) {
        super(activity);
        this.activity = activity;
        activityOrientation = activity.getResources().getConfiguration().orientation;
        try {
            systemRotation = getScreenRotation(activity.getContentResolver());
        } catch (SettingNotFoundException e) {
            e.printStackTrace();
            systemRotation = -1;
        }

        openActivityRotation();
        enable();
    }

    /**
     * 打开Activity旋转。
     * 如果打开了屏幕旋转，Activity将接收屏幕旋转事件并执行onConfigurationChanged方法。
     */
    public void openActivityRotation() {
        activityRotation = true;
    }

    /**
     * 关闭Activity旋转。
     * 无论是否打开屏幕旋转，Activity都不能接收到屏幕旋转事件。
     */
    public void closeActivityRotation() {
        activityRotation = false;
    }

    /**
     * 检查Activity能否旋转
     */
    public boolean isActivityRotationEnabled() {
        return activityRotation;
    }

    /**
     * 获取Activity当前方向。
     * 注意，Activity方向不是屏幕方向。只有打开Activity旋转，Activity方向才和屏幕方向保持一致。
     */
    public int getActivityOrientation() {
        return activityOrientation;
    }

    /**
     * 打开对屏幕旋转的监听，并设置屏幕为可旋转。
     */
    @Override
    public void enable() {
        super.enable();
        setScreenRotation(activity.getContentResolver(), 0);
    }

    /**
     * 关闭对屏幕旋转的监听，并恢复到系统之前旋转设定。
     */
    @Override
    public void disable() {
        super.disable();
        if (systemRotation == -1) {
            return;
        }
        setScreenRotation(activity.getContentResolver(), systemRotation);
    }

    @Override
    public void onOrientationChanged(int orientation) {
        if (orientation < 0) {
            return;
        }

        int newOrientation= ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED;
        if (orientation >= 0 && orientation <= 60) {
            newOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        }else if (orientation >60 && orientation <120) {
            newOrientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
        }else if (orientation >=120 && orientation <=240) {
            newOrientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
        }else if (orientation >240 && orientation <300) {
            newOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
        }else if (orientation >=300 && orientation <=360) {
            newOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        }else{
            return;
        }

        if ((newOrientation != orientation) && activityRotation) {
            activity.setRequestedOrientation(newOrientation);
            activityOrientation = newOrientation;
        }
    }

    private void setScreenRotation(ContentResolver cr, int rotation) {
        Settings.System.putInt(cr, Settings.System.ACCELEROMETER_ROTATION,
                rotation);
    }

    private int getScreenRotation(ContentResolver cr)
            throws SettingNotFoundException {
        return Settings.System.getInt(cr,
                Settings.System.ACCELEROMETER_ROTATION);
    }

}
