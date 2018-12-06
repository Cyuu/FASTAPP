package com.thdz.fast.base;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.thdz.fast.R;
import com.thdz.fast.app.MyApplication;
import com.thdz.fast.util.BusUtil;
import com.thdz.fast.util.NotifyUtil;
import com.thdz.fast.util.StatusBarCompat;
import com.thdz.fast.util.TsUtils;
import com.thdz.fast.util.VUtils;
import com.thdz.fast.view.BlockProgressDialog;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import okhttp3.MediaType;

/**
 * Activity框架 -- include方式， 有toolbar<br/>
 * ----------------- <br/>
 * 调用方法：
 * 【1】 每个需要toolbar的页面，需要include这个common_toolar,
 * 【2】 设置title，  调用：setTitle;
 * 启用返回按钮，调用：setToolbarBackEnable
 * 启用右侧按钮，需要一个声明一个：
 * 【3】 实现 setContentView()
 * 【4】 实现 initView()
 * ----------------- <br/>
 * 特性：
 * 【1】 进行了一系列的初始化封装： 1 公共的base布局；2 初始化 3 butterknife解绑
 * 【2】 并未实现超高级封装title--toolbar
 */
public abstract class BaseActivity extends AppCompatActivity implements View.OnClickListener {

    public String TAG = this.getClass().getSimpleName();

    public final String failTip = "通信异常";
    public MyApplication application;
    public Context context;
    public InputMethodManager imm = null;

    /**
     * 记录上次点击按钮的时间
     **/
    private long lastClickTime;
    /**
     * 按钮连续点击最低间隔时间 单位：毫秒
     **/
    private final static int CLICK_TIME = 600;

//    /** 是否沉浸状态栏 **/
//    private boolean isSetStatusBar = true;
//    /** 是否允许全屏 **/
//    private boolean mAllowFullScreen = true;
//    /** 是否禁止旋转屏幕 **/
//    private boolean isAllowScreenRoate = false;

    private ProgressDialog progressDialog = null;

    private Toolbar mToolbar;

    private Unbinder bind;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = this;
        application = MyApplication.getInstance();// 获取应用程序全局的实例引用
        application.addActivity(this);

        if (application.getScreenWidth() == 0) {
            application.setScreenWidth(getScreenWidth());
        }

        if (application.getScreenHeight() == 0) {
            application.setScreenHeight(getScreenHeight());
        }

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView();

        // 设置沉浸式状态栏
        int staBarColor = ContextCompat.getColor(this, R.color.colorPrimary);
        int navBarColor = ContextCompat.getColor(this, R.color.colorPrimaryDark);
        StatusBarCompat.setStatusBraColor(this, staBarColor, navBarColor);

        bind = ButterKnife.bind(this);
        initView(savedInstanceState);

    }

    /**
     * setContentView
     */
    public abstract void setContentView();


    /**
     * 初始化控件，设置控件监听，初始化数据  ---  在getcontentView()后调用
     */
    public abstract void initView(Bundle savedInstanceState);


    /**
     * 实例化toolbar
     */
    private void findToolbar() {
        if (null == mToolbar) {
            mToolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(mToolbar);
        }
    }


    /**
     * 设置Toolbar 支持返回<br/>
     */
    public void setToolbarBackEnable() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true); //设置返回键可用
            actionBar.setDisplayHomeAsUpEnabled(true); // 显示左上角返回按钮
        }

        // 左上角返回键
        findToolbar();
        if (null != mToolbar) {
            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }
    }

    /**
     * 设置title for Activity
     */
    public void setTitle(String title) {
        findToolbar();
        if (null != mToolbar) {
            mToolbar.setTitle(title);
        }
    }


    /**
     * [沉浸状态栏]
     */
    private void steepStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // 透明状态栏
            getWindow().addFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            // 透明导航栏
            getWindow().addFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
    }

    /**
     * [防止快速点击]  --
     */
    public boolean isClickNotFast() {
        if (System.currentTimeMillis() - lastClickTime <= CLICK_TIME) {
            return false;
        }
        lastClickTime = System.currentTimeMillis();
        return true;
    }


    /**
     * 获得屏幕宽度
     */

    public int getScreenWidth() {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm.widthPixels;
    }

    /**
     * 获得屏幕高度
     */
    public int getScreenHeight() {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm.heightPixels;
    }


    /**
     * get方式
     */
    public void doRequestGet(String url, StringCallback callback) {
        Log.i(TAG, "请求地址：" + url);
        OkHttpUtils
                .get()
                .url(url)
                .build()
                .execute(callback);
    }

    /**
     * post方式
     */
    public void doRequestPost(String url, Map<String ,String> params, StringCallback callback) {
        Log.i(TAG, "请求地址：" + url);

        Map<String,String> map = new HashMap<String, String>();
        map.put("ContentType","application/json");

        OkHttpUtils
                .post()
                .headers(map)
                .url(url)
                .params(params)
                .build()
                .execute(callback);

    }


    /**
     * post方式
     */
    public void doRequestPost(String url, String jsonStr, StringCallback callback) {
        Log.i(TAG, "请求地址：" + url);
        OkHttpUtils
                .postString()
                .url(url)
                .content(jsonStr)
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .build()
                .execute(callback);

    }


    /**
     * 发送数据
     */
    public void sendData(String name, String url, String params, Object data) { // String msg
        Log.i(TAG, name + ", [url + data]：" + url + "+" + new String(params));
//        application.logout(); // boolean res =
//        if (res) {
//            toast("手动发送成功");
//        } else {
//            toast("手动发送失败");
//        }
    }


    public void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new BlockProgressDialog(context);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setCancelable(true);
            progressDialog.setMessage("");
        }

        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }

    }

    public void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    @Override
    public void onClick(View v) {
        if (VUtils.isFastDoubleClick()) {
            toast("先森，您的手速太快了~");
        } else {
            onClick(v.getId());
        }
    }

    public abstract void onClick(int resId);


    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);
        overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, R.anim.push_right_out);
    }


    @Override
    protected void onPause() {
        super.onPause();
        hideInputMethod();
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        BusUtil.unreg(this);
        super.onDestroy();

        if (progressDialog != null) {
            progressDialog.dismiss();
        }
        if (bind != null) {
            bind.unbind();
        }
        application.removeActivity(this); // 把当前Activity从集合中移除

    }

    /**
     * 打开确认对话框
     */
    public void showSureDialog(String tip, DialogInterface.OnClickListener sureListener) {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(context);
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View mView = layoutInflater.inflate(R.layout.dialog_sure, null);
        TextView dialog_sure_tv = (TextView) mView.findViewById(R.id.dialog_sure_tv);
        dialog_sure_tv.setText(tip);
        mBuilder.setView(mView);
        mBuilder.setCancelable(false);
        mBuilder.setPositiveButton("确认", sureListener);

        mBuilder.setNeutralButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                return;
            }
        });
        AlertDialog sureDialog = mBuilder.create();
        sureDialog.show();
    }

    /**
     * 收起键盘
     */
    public void hideInputMethod() {
        View view = getWindow().peekDecorView(); // 用于判断虚拟软键盘是否是显示的
        if (view != null) {
            InputMethodManager inputmanger = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputmanger.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    @Override
    public void onBackPressed() {
        if (progressDialog != null && progressDialog.isShowing()) {
            hideProgressDialog();
            return;
        }

        super.onBackPressed();
    }


    /**
     * 清除app内缓存的告警状态, 并清除消息栏告警
     */
    public void clearAlarmState() {
        MyApplication.getInstance().pushBean = null;
        NotifyUtil.clearNotification();
    }


    public void goActivity(Class<?> cls, Bundle bundle) {
        Intent intent = new Intent(context, cls);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
    }

    public void goActivityForResult(Class<?> cls, Bundle bundle, int requestCode) {
        Intent intent = new Intent(context, cls);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivityForResult(intent, requestCode);
    }


    public void toast(String info) {
        TsUtils.toast(context, info);
    }


    private void showToastOnUIThread(final String msg) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                toast(msg);
            }
        });
    }

    public void setToolbarUnable(Toolbar toolbar) {
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setHomeButtonEnabled(false); //设置返回键可用
            getSupportActionBar().setDisplayHomeAsUpEnabled(false); // 显示左上角返回按钮
        }
    }

    public void setToolbarEnable(Toolbar toolbar) {
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 显示左上角返回按钮
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();//返回
                }
            });
        }
    }

}
