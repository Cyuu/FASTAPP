package com.thdz.fast.app;

import android.app.Activity;
import android.app.Application;
import android.text.TextUtils;

import com.thdz.fast.bean.PushBaseBean;
import com.thdz.fast.bean.UserBean;
import com.thdz.fast.util.FileUtil;
import com.thdz.fast.util.Finals;
import com.thdz.fast.util.SpUtil;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;


/**
 * Application
 * ----------------
 * 1 各种code存放在CodeUtils
 * 2 初始化配置：OKHttp，图片加载等
 * 3
 */
public class MyApplication extends Application {

    private static final String TAG = "FAST";

    /**
     * 是否开启了主页
     */
    public static boolean hasCreateMain = false;

    /**
     * 已推送过来的告警list
     */
    private List<PushBaseBean> PushedList;

    private List<PushBaseBean> getPushedList() {
        if (PushedList == null) {
            PushedList = new ArrayList<PushBaseBean>();
        }
        return PushedList;
    }


    /**
     * 是否已推送过改该条告警
     */
    public boolean hasPushed(PushBaseBean push) {
        if (push == null) {
            return false;
        }
        PushedList = getPushedList();
        for (PushBaseBean bean : PushedList) {
            if (push.getAlarmType().equals(bean.getAlarmType()) &&
                    push.getAlarmTime().equals(bean.getAlarmTime())) {
                return true;
            }
        }
        PushedList.add(push);
        return false;
    }

    public static final int notyId = 1; // 通知的index

    public PushBaseBean pushBean = null;

    private int screenWidth = 0;
    private int screenHeight = 0;

    public int getScreenHeight() {
        return screenHeight;
    }

    public void setScreenHeight(int screenHeight) {
        this.screenHeight = screenHeight;
    }

    public int getScreenWidth() {
        return screenWidth;
    }

    public void setScreenWidth(int screenWidth) {
        this.screenWidth = screenWidth;
    }

    private UserBean userBean;

    private String uid;

    private String hostUrl;

    public UserBean getUserBean() {
        return userBean;
    }

    public void setUserBean(UserBean userBean) {
        this.userBean = userBean;
    }

    public String getHostUrl() {
        return hostUrl;
    }

    public void setHostUrl(String hostUrl) {
        this.hostUrl = hostUrl;
    }

    public String getUid() {
        if (TextUtils.isEmpty(uid)) {
            uid = SpUtil.getUid(getApplicationContext());
        }

        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }


    private List<Activity> activityList;       // 全部activity集合
    private static MyApplication application; // 程序全局对象

    public static MyApplication getInstance() {
        if (null == application) {
            application = new MyApplication();
        }
        return application;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        application = this;

        activityList = new LinkedList<Activity>();

        PushedList = new ArrayList<PushBaseBean>();

        FileUtil.createDirectory(Finals.FilePath);
        FileUtil.createDirectory(Finals.VideoPath);
        FileUtil.createDirectory(Finals.ImagePath);

        // 初始化OkHttpClient
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
//                .addInterceptor(new LoggerInterceptor("TAG"))
                .connectTimeout(20000L, TimeUnit.MILLISECONDS)
                .readTimeout(20000L, TimeUnit.MILLISECONDS)
                .writeTimeout(20000l, TimeUnit.MILLISECONDS)
                //其他配置
                .build();

        OkHttpUtils.initClient(okHttpClient);

    }


    public void addActivity(Activity activity) {
        if (activityList == null) {
            activityList = new LinkedList<Activity>();
        }
        activityList.add(activity);
    }


//    public Activity getCurrentActivity() {
//        if (activityList != null) {
//            activityList.get(activityList.size() - 1);
//        }
//        return null;
//    }

    /**
     * 移除Activity到容器中
     */
    public void removeActivity(Activity activity) {
        activityList.remove(activity);
    }

    public void exit() {
        for (Activity activity : activityList) {
            activity.finish();
        }

        activityList.clear();
        activityList = null;
        System.exit(0);
    }

}
