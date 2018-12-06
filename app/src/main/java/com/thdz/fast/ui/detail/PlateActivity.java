package com.thdz.fast.ui.detail;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.thdz.fast.R;
import com.thdz.fast.base.BaseActivity;
import com.thdz.fast.bean.PlateBean;
import com.thdz.fast.ui.ImageActivity;

import butterknife.BindView;

/**
 * desc:    车牌识别|卡口 -- 详情页面  --  展示有告警的光纤数据
 * author:  Administrator
 * date:    2018/11/1  11:24
 */
public class PlateActivity extends BaseActivity {

    @BindView(R.id.iv_plate)
    ImageView iv_plate;

    @BindView(R.id.tv_plate_no)
    TextView tv_plate_no;

    @BindView(R.id.tv_plate_type)
    TextView tv_plate_type;

    @BindView(R.id.tv_plate_addr)
    TextView tv_plate_addr;

    @BindView(R.id.tv_plate_time)
    TextView tv_plate_time;

    private PlateBean plateBean;


    @Override
    public void setContentView() {
        setContentView(R.layout.activity_detail_plate);
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        Toolbar toolbar = findViewById(R.id.toolbar);
        String title = "车牌识别监控";
        try {
            plateBean = (PlateBean) getIntent().getExtras().getSerializable("bean");
            showView();
        } catch (Exception e) {
            toast("无此车牌识别数据~");
            finish();
            return;
        }

        toolbar.setTitle(title);
        setToolbarEnable(toolbar);

        iv_plate.setOnClickListener(this);

    }

    // 展示ipc相关信息
    private void showView() {
        iv_plate.setLayoutParams(new LinearLayout.LayoutParams(
                application.getScreenWidth(), application.getScreenWidth() * 23 / 40));
        Picasso.with(context).load(plateBean.getFileUrl()).into(iv_plate);

        int licenseLen = plateBean.getLicenseLen();
        if (licenseLen <= 0) {
            tv_plate_no.setText("     未识别     ");
        } else {
            tv_plate_no.setText(plateBean.getLicense()); // .substring(1)
        }

        tv_plate_type.setText(plateBean.getPlateType());
        tv_plate_addr.setText(plateBean.getAreaName());
        tv_plate_time.setText(plateBean.getAlarmTime());

        switch (Integer.parseInt(plateBean.getColor())) {
            case 1: // 黄色
                tv_plate_no.setBackgroundResource(R.drawable.bg_plate_yellow);
                tv_plate_no.setTextColor(context.getResources().getColor(R.color.black));
                break;
            case 2: // 白色
                tv_plate_no.setBackgroundResource(R.drawable.bg_plate_white);
                tv_plate_no.setTextColor(context.getResources().getColor(R.color.black));
                break;
            case 3: // 黑色
                tv_plate_no.setBackgroundResource(R.drawable.bg_plate_black);
                tv_plate_no.setTextColor(context.getResources().getColor(R.color.white));
                break;
            case 4: // 绿色
                tv_plate_no.setBackgroundResource(R.drawable.bg_plate_green);
                tv_plate_no.setTextColor(context.getResources().getColor(R.color.black));
                break;
            case 5: // 民航黑色车牌
                tv_plate_no.setBackgroundResource(R.drawable.bg_plate_black);
                tv_plate_no.setTextColor(context.getResources().getColor(R.color.white));
                break;
            case 6: // 其他，用蓝色代替
            case 0: // 蓝色
            default: // 蓝色
                tv_plate_no.setBackgroundResource(R.drawable.bg_plate_blue);
                tv_plate_no.setTextColor(context.getResources().getColor(R.color.white));
                break;
        }
    }

    // 根据告警信息获取ipc信息
    private void requestData() {

    }


    @Override
    public void onClick(int resId) {
        switch (resId) {
            case R.id.iv_plate:
                Bundle bundle = new Bundle();
                bundle.putString("path", plateBean.getFileUrl());
                goActivity(ImageActivity.class, bundle);
                break;
            default:
                break;
        }
    }

}
