package com.thdz.fast.ui.detail;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.thdz.fast.R;
import com.thdz.fast.base.BaseActivity;
import com.thdz.fast.bean.FaceBean;
import com.thdz.fast.hk.CrashUtil;
import com.thdz.fast.ui.ImageActivity;

import butterknife.BindView;

/**
 * desc:    光纤监控页面 -- 详情页面  --  展示有告警的光纤数据
 * author:  Administrator
 * date:    2018/11/1  11:24
 */
public class FaceActivity extends BaseActivity {


    @BindView(R.id.iv_face_1)
    ImageView iv_face_1;

    @BindView(R.id.iv_face_2)
    ImageView iv_face_2;

    @BindView(R.id.tv_person_name)
    TextView tv_person_name;

    @BindView(R.id.tv_sex)
    TextView tv_sex;

    @BindView(R.id.tv_match)
    TextView tv_match;

    @BindView(R.id.tv_cardno)
    TextView tv_cardno;

    @BindView(R.id.tv_agegroup)
    TextView tv_agegroup;

    @BindView(R.id.tv_glasses)
    TextView tv_glasses;

    @BindView(R.id.tv_recog_addr)
    TextView tv_recog_addr;

    @BindView(R.id.tv_recog_time)
    TextView tv_recog_time;

    private FaceBean faceBean;


    @Override
    public void setContentView() {
        CrashUtil crashUtil = CrashUtil.getInstance();
        crashUtil.init(this);
        setContentView(R.layout.activity_detail_face);
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        Toolbar toolbar = findViewById(R.id.toolbar);
        String title = "脸谱监控";
        toolbar.setTitle(title);
        setToolbarEnable(toolbar);

        iv_face_1.setOnClickListener(this);
        iv_face_2.setOnClickListener(this);

        try {
            faceBean = (FaceBean) getIntent().getExtras().getSerializable("bean");
            showView();
        } catch (Exception e) {
            toast("无此脸谱数据~");
            finish();
            return;
        }
    }

    // 展示ipc相关信息
    private void showView() {
        tv_person_name.setText(faceBean.getName());
        tv_sex.setText(faceBean.getSex());
        int matchStr = faceBean.getMatchOk();
        if (matchStr == 1) {
            tv_match.setText("匹配成功");
            tv_match.setTextColor(Color.GREEN);
        } else {
            tv_match.setText("匹配失败");
            tv_match.setTextColor(Color.RED);
        }

        tv_cardno.setText(faceBean.getIdCardNo());
        tv_agegroup.setText(faceBean.getAgeGroup());
        tv_glasses.setText(faceBean.getGlasses());
        tv_recog_addr.setText(faceBean.getAreaName());
        tv_recog_time.setText(faceBean.getAlarmTime());


        iv_face_1.setLayoutParams(new LinearLayout.LayoutParams(
                application.getScreenWidth() / 2, application.getScreenWidth() / 2 * 6 / 5));
        iv_face_2.setLayoutParams(new LinearLayout.LayoutParams(
                application.getScreenWidth() / 2, application.getScreenWidth() / 2 * 6 / 5));

        String url1 = faceBean.getFileUrl2();
        if (!TextUtils.isEmpty(url1)) {
            Picasso.with(context).load(faceBean.getFileUrl2()).into(iv_face_1);
        }

        String url2 = faceBean.getFileUrl3();
        if (!TextUtils.isEmpty(url2)) {
            Picasso.with(context).load(faceBean.getFileUrl3()).into(iv_face_2);
        }

    }

    @Override
    public void onClick(int resId) {
        switch (resId) {
            case R.id.iv_face_1:
                Bundle bundle = new Bundle();
                bundle.putString("path", faceBean.getFileUrl2());
                goActivity(ImageActivity.class, bundle);
                break;
            case R.id.iv_face_2:
                Bundle bundle2 = new Bundle();
                bundle2.putString("path", faceBean.getFileUrl3());
                goActivity(ImageActivity.class, bundle2);
                break;
            default:
                break;
        }
    }

}
