package com.thdz.fast.ui;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;

import com.thdz.fast.R;
import com.thdz.fast.base.BaseActivity;
import com.thdz.fast.view.TouchImageView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.BitmapCallback;

import butterknife.BindView;
import okhttp3.Call;

/**
 * 大图展示页面
 */
public class ImageActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.image_zoom)
    TouchImageView image_zoom; // 可放大拉伸图片控件

    private String imgPath;


    @Override
    public void setContentView() {
        setContentView(R.layout.activity_image);
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        toolbar.setTitle("查看大图");
        setToolbarEnable(toolbar);

        try {
            imgPath = getIntent().getExtras().getString("path");
            showBmpByUrl(imgPath);
            if (TextUtils.isEmpty(imgPath)) {
                toast("图片不存在");
                finish();
            }
        }catch (Exception e) {
            e.printStackTrace();
            toast("图片不存在");
            finish();
        }
    }


    private void showBmpByUrl(String imgPath) {
        OkHttpUtils
                .get()
                .url(imgPath)
                .build()
                .execute(new BitmapCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        e.printStackTrace();
                        toast("图片不存在");
                    }

                    @Override
                    public void onResponse(final Bitmap response, int id) {
                        image_zoom.setImageBitmap(response);
                        image_zoom.setZoom(1f);
                    }
                });
    }

    @Override
    public void onClick(int resId) {

    }

}
