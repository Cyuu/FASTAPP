package com.thdz.fast.hk;

import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;

import com.hcnetsdk.jna.HCNetSDKJNAInstance;
import com.hikvision.netsdk.HCNetSDK;
import com.hikvision.netsdk.NET_DVR_DEVICEINFO_V30;
import com.hikvision.netsdk.NET_DVR_PREVIEWINFO;
import com.hikvision.netsdk.NET_DVR_TIME;
import com.hikvision.netsdk.NET_DVR_VOD_PARA;
import com.hikvision.netsdk.PlaybackControlCommand;
import com.thdz.fast.app.MyApplication;
import com.thdz.fast.util.TsUtils;

/**
 * desc:    海康sdk集成助手
 * author:  Administrator
 * date:    2018/11/15  10:01
 */
public class HkHelper {

    public static final String TAG = "HKSDK_Helper";

    private Activity act;

    private String ip;
    private int port;
    private String username;
    private String pwd;
    private int iChannelNo; // 通道号

    private PlaySurfaceView[] playView;

    // 点击登录按钮，登录成功返回loginId, <0 表示登录失败
    private int iLoginID = -1;
    // 实时预览返回的id，   -1表示失败
    private int iPlayID  = -1;
    // 按时间回放录像文件返回的id， -1表示失败
    private int iPlaybackID = -1;

    private static HkHelper mHelper;

    public static HkHelper getInstance() {
        if (mHelper == null) {
            synchronized (HkHelper.class) {
                if (mHelper == null) {
                    mHelper = new HkHelper();
                }
            }
        }
        return mHelper;
    }

    /**
     * 海康sdk初始化
     */
    public boolean initSdk(Activity act) {
        if (!HCNetSDK.getInstance().NET_DVR_Init()) {
            Log.e(TAG, "海康sdk初始化失败，请重试！");
            return false;
        }
        this.act = act;
        Log.i(TAG, "海康sdk初始化成功");
        return true;
    }

    /**
     * 登录
     */
    public boolean login(String ip, int port, String username, String pwd, int iChannelNo) {
        if (iLoginID > 0) {
            return true;
        }
        this.ip = ip;
        this.port = port;
        this.username = username;
        this.pwd = pwd;
        this.iChannelNo = iChannelNo;

        NET_DVR_DEVICEINFO_V30 m_oNetDvrDeviceInfoV30 = new NET_DVR_DEVICEINFO_V30();

        iLoginID = HCNetSDK.getInstance().NET_DVR_Login_V30(ip, port, username, pwd, m_oNetDvrDeviceInfoV30);

        if (iLoginID < 0) {
            Log.e(TAG, "登录IPC失败,  Err:" + HCNetSDK.getInstance().NET_DVR_GetLastError());
            toast("登录IPC失败, 请重试");
            return false;

            // todo 如果需要根据登录获取到通道号，通道个数，请参照 HK demo
        }
        Log.i(TAG, "登录IPC成功");
        return true;
    }


    /**
     * 登出
     */
    public boolean logout() {
        if (iLoginID < 0) {
            return true;
        }
        return HCNetSDK.getInstance().NET_DVR_Logout_V30(iLoginID);
    }

    /**
     * 开始预览
     *
     * @return 返回playID， >=0 表示预览成功
     */
    public boolean startPreview() {
        if (!login(ip, port, username, pwd, iChannelNo)) {
            toast("登录IPC失败");
            return false;
        }

        if (iPlayID > 0) {
            Log.i(TAG, "需要先关闭预览");
            if (!stopPreview()) {
                toast("停止之前的预览失败");
                return false;
            }
        }

        if (iPlaybackID > 0) {
            Log.i(TAG, "需要先关闭回放");
            if (!stopPlayback()) {
                toast("停止之前的回放失败");
                return false;
            }
//            toast("请先关闭回放");
//            return false;
        }

        // 调用surfaceview的预览方法
        playView[0].startPreview(iLoginID, iChannelNo);
        iPlayID = playView[0].m_iPreviewHandle;

        // 不同这个，会导致延迟和卡顿
//        iPlayID = HCNetSDK.getInstance().NET_DVR_RealPlay_V40(
//                iLoginID, createPrivewInfo(iChannelNo, playView[0]), null);

        if (iPlayID < 0) {
            toast("预览失败");
            Log.e(TAG, "预览失败!Error:" + HCNetSDK.getInstance().NET_DVR_GetLastError());
            return false;
        }

//        // 播放音频
//        boolean bRet = HCNetSDKJNAInstance.getInstance().NET_DVR_OpenSound(iPlayID);
//        if (bRet) {
//            Log.i(TAG, "播放声音成功!");
//        }

        Log.i(TAG, "预览成功");
        return true;
    }

    /**
     * 停止预览
     *
     * @return 返回playid,-1表示不需关闭预览或者正常停止预览，-2表示停止预览中产生异常
     */
    public boolean stopPreview() {
        if (iPlayID < 0) {
            Log.i(TAG, "未开启预览!");
            return true;
        }

        // playView[0].stopPreview();

        if (HCNetSDKJNAInstance.getInstance().NET_DVR_CloseSound()) {
            Log.i(TAG, "关闭声音成功!");
        }

        if (!HCNetSDK.getInstance().NET_DVR_StopRealPlay(iPlayID)) {
            Log.e(TAG, "停止预览失败!Err:"
                    + HCNetSDK.getInstance().NET_DVR_GetLastError());
            toast("停止预览失败");
            return false;
        }

        iPlayID = -1;
        Log.i(TAG, "停止预览成功");
        addSurfaceView(1);
        return true;
    }

    /**
     * 开始播放录像
     *
     * @return >-0 回放成功， -1 回放失败， -2 调用开始播放命令失败
     */
    public boolean startPlayback(String beginTime, String endTime) {
        if (!login(ip, port, username, pwd, iChannelNo)) {
            toast("登录IPC失败");
            return false;
        }
        if (TextUtils.isEmpty(beginTime)) {
            toast("请先选择起始时间！");
            return false;
        }
        if (TextUtils.isEmpty(endTime)) {
            toast("请先选择结束时间！");
            return false;
        }

        if (iPlayID > 0) {
            Log.i(TAG, "需要先关闭预览");
            if (!stopPreview()) {
                return false;
            }
        }

        if (iPlaybackID > 0) {
            Log.i(TAG, "需要先关闭回放");
            if (!stopPlayback()) {
                toast("停止之前的回放失败");
                return false;
            }
//            toast("请先关闭回放");
//            return false;
        }

        NET_DVR_TIME timeStart = convertTime(beginTime);
        NET_DVR_TIME timeStop = convertTime(endTime);

        NET_DVR_VOD_PARA vodParma = new NET_DVR_VOD_PARA();
        vodParma.struBeginTime = timeStart;
        vodParma.struEndTime = timeStop;
        vodParma.byStreamType = 0;
        vodParma.struIDInfo.dwChannel = iChannelNo;
        vodParma.hWnd = playView[0].getHolder().getSurface();

        iPlaybackID = HCNetSDK.getInstance().NET_DVR_PlayBackByTime_V40(iLoginID, vodParma);

        if (iPlaybackID < 0) {
            Log.i(TAG, "播放回放失败, error:" + HCNetSDK.getInstance().NET_DVR_GetLastError());
            return false;
        }

        if (!HCNetSDK.getInstance().NET_DVR_PlayBackControl_V40(
                iPlaybackID,
                PlaybackControlCommand.NET_DVR_PLAYSTART,
                null,
                0,
                null)
        ) {
            Log.e(TAG, "发送开始播放命令失败!");
            toast("发送开始播放命令失败!");
            return false;
        }
        Log.i(TAG, "播放回放成功");
        return true;
    }

    /**
     * 停止播放录像，
     *
     * @return 返回iPlaybackID, -1表示不需关闭预览或者正常停止预览，-2表示停止预览中产生异常
     */
    public boolean stopPlayback() {
        if (iPlaybackID < 0) {
            Log.i(TAG, "未开启回放!");
            return true;
        }

        if (!HCNetSDK.getInstance().NET_DVR_StopPlayBack(iPlaybackID)) {
            Log.e(TAG, "停止回放失败！");
            toast("停止回放失败, Err:" + HCNetSDK.getInstance().NET_DVR_GetLastError());
            return false;
        }

        iPlaybackID = -1;
        Log.i(TAG, "停止回放成功!");
        addSurfaceView(1);
        return true;
    }


    private NET_DVR_TIME convertTime(String timestr) {
        NET_DVR_TIME dvrTime = new NET_DVR_TIME();

        String[] times = timestr.split(" ");
        String[] times1 = times[0].split("-");
        String[] times2 = times[1].split(":");

        dvrTime.dwYear = Integer.parseInt(times1[0]);
        dvrTime.dwMonth = Integer.parseInt(times1[1]);
        dvrTime.dwDay = Integer.parseInt(times1[2]);

        dvrTime.dwHour = Integer.parseInt(times2[0]);
        dvrTime.dwMinute = Integer.parseInt(times2[1]);
        dvrTime.dwSecond = Integer.parseInt(times2[2]);

        return dvrTime;
    }


    private NET_DVR_PREVIEWINFO createPrivewInfo(int channelNo, PlaySurfaceView psfView) {
        NET_DVR_PREVIEWINFO previewInfo = new NET_DVR_PREVIEWINFO();
        previewInfo.lChannel = channelNo;
        previewInfo.dwStreamType = 0; // 0 主码流 1 子码流
        previewInfo.bBlocked = 1;
        previewInfo.hHwnd = psfView.getHolder();
        return previewInfo;
    }


    /**
     * 加载预览surfaceview，将播放窗口放在屏幕最上方
     *
     * @param count PlaySurfaceView个数
     */
    public void addSurfaceView(int count) {
        if (count <= 0 || count > 4) {
            toast("count参数错误!");
            return;
        }

        int lWidth = MyApplication.getInstance().getScreenWidth();
        playView = new PlaySurfaceView[count];

        for (int i = 0; i < 1; i++) {
            if (playView[i] == null) {
                playView[i] = new PlaySurfaceView(act);
                playView[i].setParam(lWidth); // 设置屏幕宽高

                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.WRAP_CONTENT,
                        FrameLayout.LayoutParams.WRAP_CONTENT);
                params.bottomMargin =
                        playView[i].getM_iHeight() - (i / 2) * playView[i].getM_iHeight();
                params.leftMargin = (i % 2) * playView[i].getM_iWidth();
                params.gravity = Gravity.TOP | Gravity.LEFT;
                act.addContentView(playView[i], params);

                playView[i].setVisibility(View.INVISIBLE);

            }
        }

        // 预览一个通道
        for (int i = 0; i < 1; ++i) {
            playView[i].setVisibility(View.INVISIBLE);
        }
        playView[0].setParam(lWidth * 2);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT);
        params.topMargin = 0;
        params.leftMargin = 0;
        params.gravity = Gravity.TOP | Gravity.LEFT;
        playView[0].setLayoutParams(params);
        playView[0].setVisibility(View.VISIBLE);

    }


    public void toast(String msg) {
        TsUtils.toast(act, msg);
    }

}
