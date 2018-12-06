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
import com.thdz.fast.bean.RadarBean;
import com.thdz.fast.hk.CrashUtil;
import com.thdz.fast.hk.HkHelper;
import com.thdz.fast.util.DataUtils;
import com.thdz.fast.util.Finals;
import com.zhy.http.okhttp.callback.StringCallback;

import org.MediaPlayer.PlayM4.Player;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import okhttp3.Call;

/**
 * desc:    雷达监控页面 -- 详情页面  --  展示有告警的雷达数据
 * author:  Administrator
 * date:    2018/11/1  11:24
 */
public class RadarActivity extends BaseActivity  implements SurfaceHolder.Callback {


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

    @BindView(R.id.tv_alarm_name)
    TextView tv_alarm_name;

    @BindView(R.id.tv_alarm_addr)
    TextView tv_alarm_addr;

    @BindView(R.id.tv_alarm_time)
    TextView tv_alarm_time;

    @BindView(R.id.tv_alarm_status)
    TextView tv_alarm_status;

    @BindView(R.id.tv_alarm_level)
    TextView tv_alarm_level;

    private RadarBean radarBean;


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
        setContentView(R.layout.activity_detail_radar);
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        hkHelper = HkHelper.getInstance(); // 实例化
        if (!hkHelper.initSdk((Activity) context)) { // 初始化HK sdk
            toast("海康sdk初始化失败，无法观看视频预览和回放！");
        }
        hkHelper.addSurfaceView(1);

        Toolbar toolbar = findViewById(R.id.toolbar);
        String title = "雷达监控";
        try {
            radarBean = (RadarBean) getIntent().getExtras().getSerializable("bean");
            if (!TextUtils.isEmpty(radarBean.getAlarmMessage())) {
                title = radarBean.getAlarmMessage();
            }
            showView();
        } catch (Exception e) {
            toast("无此告警数据~");
            finish();
            return;
        }

        toolbar.setTitle(title);
        setSupportActionBar(toolbar);
        setToolbarEnable(toolbar);

        goneAllBtn();
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

        requestData();
    }

    // 展示ipc相关信息
    private void showView() {
        tv_alarm_name.setText(radarBean.getAlarmMessage());
        tv_alarm_addr.setText(radarBean.getAreaName());
        tv_alarm_time.setText(radarBean.getAlarmTime());
        tv_alarm_level.setText(radarBean.getAlarmLevelName());
        int status = radarBean.getAlarmStatus();
        String statusVal = "";
        if (status == 0) {
            statusVal = "告警开始";
        } else if (status == 1) {
            statusVal = "告警结束";
        }
        tv_alarm_status.setText(statusVal);
    }

    // 根据告警信息获取ipc信息
    private void requestData() {
        JSONObject obj = new JSONObject();
        try {
            obj.put("DeviceId", radarBean.getDeviceId());
            obj.put("AlarmCode", radarBean.getAlarmCode());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        showProgressDialog();
        doRequestPost(DataUtils.getApiUrl(Finals.ipc_getbyalarm), obj.toString(), new StringCallback() {
            @Override
            public void onError(Call call, final Exception e, int id) {
                hideProgressDialog();
                toast("请求异常");
                e.printStackTrace();
            }

            @Override
            public void onResponse(String value, int id) {
                hideProgressDialog();
                Log.i(TAG, "获取ipc信息请求 返回参数是：" + value);
                try {
                    if (DataUtils.isReturnOK(value)) {
                        ipcBean = com.alibaba.fastjson.JSON.parseObject(
                                DataUtils.getReturnData(value), IpcBean.class);
                        tv_ipc_name.setText(ipcBean.getIpcName());
                        if (loginIPC()){
                            showAllBtn();
                        }
                    } else {
                        toast(DataUtils.getReturnMsg(value));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    toast("数据有误，请重试");
                }
            }
        });
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

    private void goneAllBtn() {
        btn_show_preview.setVisibility(View.GONE);
        btn_stop_preview.setVisibility(View.GONE);
        btn_show_placback.setVisibility(View.GONE);
        btn_stop_placback.setVisibility(View.GONE);
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
        try {
            if (ipcBean == null ||
                    TextUtils.isEmpty(ipcBean.getIp()) ||
                    TextUtils.isEmpty(ipcBean.getUserName()) ||
                    TextUtils.isEmpty(ipcBean.getPassword()) ||
                    ipcBean.getChannel() == 0) {
                toast("IPC参数有误，登录IPC失败");
                return false;
            }

            return hkHelper.login(
                    ipcBean.getIp(),
                    8000,
                    ipcBean.getUserName(),
                    ipcBean.getPassword(),
                    ipcBean.getChannel());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
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
