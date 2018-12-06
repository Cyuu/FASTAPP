package com.thdz.fast.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;

import com.thdz.fast.R;
import com.thdz.fast.app.MyApplication;

/**
 * 推送监听页面
 * -------------
 * 登录 --> this -->主页
 * ---------
 * 该页面有两种打开方式
 * 1 登录跳转， 需要传递一个标识
 * 2 点推送打开，不登录
 */
public class PushListenActivity extends Activity implements View.OnClickListener {

    private static final String TAG = "PushListenActivity";
    public MyApplication application;
    private Context context = this;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_push_listen);
        getWindow().setLayout(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);
        application = MyApplication.getInstance();
//        application.addActivity(this);

        initView(savedInstanceState);
    }


    public void initView(Bundle savedInstanceState) {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null && bundle.getBoolean("fromLogin")) { // 从登录来，直接登录
            goActivity(MainActivity.class, null);
            finish();
        } else {
            if (application.pushBean != null && !TextUtils.isEmpty(application.pushBean.getAlarmType())) {
                dealPush();
            } else {
                goActivity(MainActivity.class, null);
                finish();
            }
        }
    }

    /**
     * application里缓存着uid，才认为是已登录
     */
    private void dealPush() {
        if (TextUtils.isEmpty(application.getUid())) {
            Intent intent = new Intent(context, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        } else { // 已登录
            goMain();
        }
        application.pushBean = null;
    }


    /**
     * 跳转至首页
     */
    private void goMain() {
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }


    private void goActivity(Class<?> cls, Bundle bundle) {
        Intent intent = new Intent(context, cls);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {

    }

}
