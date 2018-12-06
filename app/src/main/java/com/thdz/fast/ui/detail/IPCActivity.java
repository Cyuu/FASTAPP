package com.thdz.fast.ui.detail;

import android.app.Activity;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.andrew.datechoosewheelviewdemo.DateChooseWheelViewDialog;
import com.thdz.fast.R;
import com.thdz.fast.base.BaseActivity;
import com.thdz.fast.bean.IpcBean;
import com.thdz.fast.hk.CrashUtil;
import com.thdz.fast.hk.HkHelper;
import com.thdz.fast.ui.hk.HkDemoActivity;
import com.thdz.fast.util.Finals;

import org.MediaPlayer.PlayM4.Player;

import butterknife.BindView;

/**
 * desc:    网络摄像机监控页面 - 展示：具体到某一通道的ipc信息，预览，回放
 * author:  Administrator
 * date:    2018/11/1  11:24
 */
public class IPCActivity extends BaseActivity implements SurfaceHolder.Callback {

    // ---------- 海康SDK  begin -----------

    @BindView(R.id.tv_ipc_name)
    TextView tv_ipc_name;

    @BindView(R.id.btn_show_preview)
    TextView btn_show_preview;

    @BindView(R.id.btn_stop_preview)
    TextView btn_stop_preview;

    @BindView(R.id.btn_show_placback)
    TextView btn_show_placback;

    @BindView(R.id.btn_stop_placback)
    TextView btn_stop_placback;

    @BindView(R.id.tv_begin_time)
    TextView tv_begin_time;

    @BindView(R.id.tv_end_time)
    TextView tv_end_time;

    @BindView(R.id.sur_player)
    SurfaceView sur_player;

    private String beginTime; // 开始时间
    private String endTime;  // 结束时间

    DateChooseWheelViewDialog beginDialog = null; // 起始时间控件
    DateChooseWheelViewDialog endDialog = null;  // 结束时间控件

    private IpcBean ipcBean = null;

    private HkHelper hkHelper = null;
    private int iPort = -1; // Player的成员变量

    // ---------- 海康SDK  end -----------


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        sur_player.getHolder().setFormat(PixelFormat.TRANSLUCENT);
        Log.i(TAG, "surface is created" + iPort);
        if (-1 == iPort) {
            return;
        }
        Surface surface = holder.getSurface();
        if (surface.isValid()) {
            if (!Player.getInstance().setVideoWindow(iPort, 0, holder)) {
                Log.e(TAG, "Player setVideoWindow failed!");
            }
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.i(TAG, "Player setVideoWindow release!" + iPort);
        if (-1 == iPort) {
            return;
        }
        if (holder.getSurface().isValid()) {
            if (!Player.getInstance().setVideoWindow(iPort, 0, null)) {
                Log.e(TAG, "Player setVideoWindow failed!");
            }
        }
    }


    @Override
    public void setContentView() {
        CrashUtil crashUtil = CrashUtil.getInstance();
        crashUtil.init(this);
        setContentView(R.layout.activity_detail_ipc);
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        hkHelper = HkHelper.getInstance(); // 实例化
        if (!hkHelper.initSdk((Activity) context)) { // 初始化HK sdk
            this.finish();
            return;
        }

        hkHelper.addSurfaceView(1);
        try {
            ipcBean = (IpcBean) getIntent().getExtras().getSerializable("bean");
            if (ipcBean != null && ipcBean.isValid()) {
                tv_ipc_name.setText(ipcBean.getIpcName());
            } else {
                toast("无此网络摄像机数据~");
                finish();
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
            toast("无此网络摄像机数据~");
            finish();
            return;
        }
        Toolbar toolbar = findViewById(R.id.toolbar);
        String title = ipcBean.getIpcName();
        if (TextUtils.isEmpty(title)) {
            title = "IPC浏览";
        }
        toolbar.setTitle(title);
        setSupportActionBar(toolbar);
        setToolbarEnable(toolbar);

        sur_player.getHolder().addCallback(this);

        btn_show_preview.setOnClickListener(this);
        btn_stop_preview.setOnClickListener(this);
        btn_show_placback.setOnClickListener(this);
        btn_stop_placback.setOnClickListener(this);
        tv_begin_time.setOnClickListener(this);
        tv_end_time.setOnClickListener(this);

        sur_player.setLayoutParams(new RelativeLayout.LayoutParams(
                application.getScreenWidth(),
                application.getScreenWidth() * 9 / 14));

        if (Finals.IS_TEST) {
            findViewById(R.id.btn_hkdemo).setVisibility(View.VISIBLE);
            findViewById(R.id.btn_hkdemo).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goActivity(HkDemoActivity.class, null);
                }
            });
        } else {
            findViewById(R.id.btn_hkdemo).setVisibility(View.GONE);
        }

        loginIPC();

    }


    @Override
    public void onClick(int resId) {
        switch (resId) {
            case R.id.btn_show_preview:
                previewNVR();
                break;
            case R.id.btn_stop_preview:
                hkHelper.stopPreview();
                showAllBtn();
                break;
            case R.id.btn_show_placback:
                playBack();
                break;
            case R.id.btn_stop_placback:
                hkHelper.stopPlayback();
                showAllBtn();
                break;

            case R.id.tv_begin_time:
                showBeginDialog();
                break;
            case R.id.tv_end_time:
                showEndDialog();
                break;
        }
    }


    private void hideAllBtn() {
        btn_show_preview.setVisibility(View.INVISIBLE);
        btn_stop_preview.setVisibility(View.INVISIBLE);
        btn_show_placback.setVisibility(View.INVISIBLE);
        btn_stop_placback.setVisibility(View.INVISIBLE);
    }

    private void showAllBtn() {
        btn_show_preview.setVisibility(View.VISIBLE);
        btn_stop_preview.setVisibility(View.VISIBLE);
        btn_show_placback.setVisibility(View.VISIBLE);
        btn_stop_placback.setVisibility(View.VISIBLE);
    }


    /**
     * 登录
     */
    private boolean loginIPC() {
        if (ipcBean == null) {
            toast("该摄像机参数为空，登录失败");
            return false;
        }
        boolean flag = hkHelper.login(
                ipcBean.getIp(),
                8000,
                ipcBean.getUserName(),
                ipcBean.getPassword(),
                ipcBean.getChannel());
        return flag;
    }


    /**
     * 单路预览
     */
    private void previewNVR() {
        if (hkHelper.startPreview()) {
            hideAllBtn();
            btn_stop_preview.setVisibility(View.VISIBLE);
        }
    }


    /**
     * 查看录像
     */
    private void playBack() {
        if (hkHelper.startPlayback(beginTime, endTime)) {
            hideAllBtn();
            btn_stop_placback.setVisibility(View.VISIBLE);
        }
    }


    /**
     * 选择开始时间
     */
    private void showBeginDialog() {
        if (beginDialog == null) {
            beginDialog = new DateChooseWheelViewDialog(context,
                    new DateChooseWheelViewDialog.DateChooseInterface() {
                        @Override
                        public void getDateTime(String time, boolean longTimeChecked) {
                            beginTime = time;
                            tv_begin_time.setText(time);
                        }
                    });
            beginDialog.setDateDialogTitle("开始时间");
        }

        if (!TextUtils.isEmpty(beginTime)) {
            beginDialog.setDefaultValue(beginTime);
        }

        beginDialog.showDateChooseDialog();
    }

    /**
     * 选择结束时间
     */
    private void showEndDialog() {
        if (endDialog == null) {
            endDialog = new DateChooseWheelViewDialog(context,
                    new DateChooseWheelViewDialog.DateChooseInterface() {
                        @Override
                        public void getDateTime(String time, boolean longTimeChecked) {
                            endTime = time;
                            tv_end_time.setText(time);
                        }
                    });
            endDialog.setDateDialogTitle("结束时间");
        }

        if (!TextUtils.isEmpty(endTime)) {
            endDialog.setDefaultValue(endTime);
        }

        endDialog.showDateChooseDialog();
    }


    @Override
    protected void onDestroy() {
        hkHelper.stopPreview();
        hkHelper.stopPlayback();
        hkHelper.logout();
        super.onDestroy();
    }
}
