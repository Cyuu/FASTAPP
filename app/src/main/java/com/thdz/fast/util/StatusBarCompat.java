package com.thdz.fast.util;

import android.app.Activity;
import android.os.Build;
import android.view.WindowManager;

import com.readystatesoftware.systembartint.SystemBarTintManager;

/**
 * Created by Lx on 2017/12/20.
 */

public class StatusBarCompat {


    private static final int INVALID_VAL = -1;

    /**
     * 设置状态栏颜色 4.4 版本以上使用
     */
    public static void setStatusBraColor(Activity activity, int staBarColor, int navBarColor) {

        if (staBarColor == INVALID_VAL) {
            return;
        }
        //5.0 以上版本
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            //After LOLLIPOP not translucent status bar
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //Then call setStatusBarColor.
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            //
            activity.getWindow().setStatusBarColor(staBarColor);
            activity.getWindow().setNavigationBarColor(navBarColor);

        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {  //4.4-5.0

            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //开启状态栏和导航栏染色
            SystemBarTintManager tintManager = new SystemBarTintManager(activity);
            //激活状态栏设置
            tintManager.setStatusBarTintEnabled(true);
            //激活导航栏设置
            tintManager.setNavigationBarTintEnabled(true);
            // 设置一个状态栏颜色
            //tintManager.setStatusBarTintResource(R.color.colorPrimary);
            //tintManager.setTintColor(staBarColor);
            tintManager.setStatusBarTintColor(staBarColor);
            tintManager.setNavigationBarTintColor(navBarColor);
        }
    }


}
