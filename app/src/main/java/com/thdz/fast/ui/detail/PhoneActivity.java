package com.thdz.fast.ui.detail;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.thdz.fast.R;
import com.thdz.fast.base.BaseActivity;

/**
 * desc:    手机微信号检测 -- 详情页面  --  展示有告警的微信号数据
 * author:  Administrator
 * date:    2018/11/1  11:24
 */
public class PhoneActivity extends BaseActivity {
    @Override
    public void setContentView() {
        setContentView(R.layout.activity_detail_fiber);
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        String title = "";
        try {
            title = getIntent().getExtras().getString("name");
        } catch (Exception e) {
            e.printStackTrace();
        }
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(title + "微信号监控");
        setSupportActionBar(toolbar);
        setToolbarEnable(toolbar);

        requestData();

    }

    // 获取接口数据
    private void requestData() {


        updateAlarmInfo();
    }

    // 更新告警列表
    private void updateAlarmInfo() {

    }

    @Override
    public void onClick(int resId) {

    }


}
