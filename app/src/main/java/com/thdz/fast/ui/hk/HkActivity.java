package com.thdz.fast.ui.hk;


import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hcnetsdk.jna.HCNetSDKJNAInstance;
import com.hikvision.netsdk.ExceptionCallBack;
import com.hikvision.netsdk.HCNetSDK;
import com.hikvision.netsdk.INT_PTR;
import com.hikvision.netsdk.NET_DVR_COMPRESSIONCFG_V30;
import com.hikvision.netsdk.NET_DVR_DEVICEINFO_V30;
import com.hikvision.netsdk.NET_DVR_PLAYBACK_INFO;
import com.hikvision.netsdk.NET_DVR_PREVIEWINFO;
import com.hikvision.netsdk.NET_DVR_TIME;
import com.hikvision.netsdk.NET_DVR_VOD_PARA;
import com.hikvision.netsdk.PTZCommand;
import com.hikvision.netsdk.PlaybackCallBack;
import com.hikvision.netsdk.PlaybackControlCommand;
import com.hikvision.netsdk.RealDataCallBack;
import com.hikvision.netsdk.RealPlayCallBack;
import com.hikvision.netsdk.StdDataCallBack;
import com.thdz.fast.R;
import com.thdz.fast.base.BaseActivity;
import com.thdz.fast.hk.CrashUtil;
import com.thdz.fast.hk.HkTestActivity;
import com.thdz.fast.hk.JNATest;
import com.thdz.fast.hk.PlaySurfaceView;
import com.thdz.fast.hk.VoiceTalk;

import org.MediaPlayer.PlayM4.Player;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * 海康视频
 */
public class HkActivity extends BaseActivity implements SurfaceHolder.Callback {
    private Button btn_Login = null;
    private Button btn_Preview = null;
    private Button btn_Playback = null;

    private Button btn_ParamCfg = null;
    private Button btn_Capture = null;
    private Button btn_Record = null;

    private Button btn_Talk = null;
    private Button btn_PTZ = null;
    private Button btn_OTHER = null;

    private EditText m_oIPAddr = null;
    private EditText m_oPort = null;
    private EditText m_oUser = null;
    private EditText m_oPsd = null;

    RelativeLayout layout11;

    private SurfaceView Sur_Player = null;

    private final static int REQUEST_CODE = 1;
//    private final static int RESULT_OK = 0;

    /**
     * 设备信息
     */
    private NET_DVR_DEVICEINFO_V30 m_oNetDvrDeviceInfoV30 = null;
    private StdDataCallBack cbf = null;
    private RealDataCallBack rdf = null;

    /**
     * 点击登录按钮，登录成功返回loginId
     */
    private int iLoginID = -1;      // return by NET_DVR_Login_v30

    /**
     * 实时预览返回的id，  -1表示失败
     */
    private int iPlayID = -1;       // return by NET_DVR_RealPlay_V30

    /**
     * 按时间回放录像文件返回的id，  -1表示失败
     */
    private int iPlaybackID = -1;   // return by NET_DVR_PlayBackByTime

    /**
     * Player的成员变量
     */
    private int iPort = -1;     // play port

    /**
     * 登录设备 --> 获取到 起始通道号
     */
    private int iStartChannel = 0; // start channel no

    /**
     * 登录设备 --> 获取到 通道数
     */
    private int iChannelNum = 0;   // channel number

    private static PlaySurfaceView[] playView = new PlaySurfaceView[4];

    private final String TAG = "HkActivity";

    private boolean m_bTalkOn = false;
    private boolean m_bPTZL = false;
    private boolean m_bMultiPlay = false;
    private boolean m_bInsideDecode = true;
    private boolean m_bSaveRealData = false;
    private boolean m_bStopPlayback = false;

    private String m_retUrl = "";

    public static String accessToken = "";
    public static String areaDomain = "";
    public static String appkey = ""; // fill in with appkey
    public static String appSecret = ""; // fill in with appSecret


    @Override
    public void setContentView() {
        CrashUtil crashUtil = CrashUtil.getInstance();
        crashUtil.init(this);
        setContentView(R.layout.activity_hkdemo);
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        // 页面打开，默认开始初始化，如果初始化失败，关闭页面。
        if (!initeSdk()) {
            this.finish();
            return;
        }

        if (!initeActivity()) {
            this.finish();
            return;
        }

        // todo 设置编辑框默认值:ip,端口，用户名，密码
        // m_oIPAddr.setText("172.10.21.31");
        m_oIPAddr.setText("192.168.60.210");
        m_oPort.setText("8000");
        m_oUser.setText("admin");
        m_oPsd.setText("hk123456");
    }

    @Override
    public void onClick(int resId) {

    }

    // @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Sur_Player.getHolder().setFormat(PixelFormat.TRANSLUCENT);
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

    // @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    // @Override
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
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt("iPort", iPort);
        super.onSaveInstanceState(outState);
        Log.i(TAG, "onSaveInstanceState");
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        iPort = savedInstanceState.getInt("iPort");
        super.onRestoreInstanceState(savedInstanceState);
        Log.i(TAG, "onRestoreInstanceState");
    }

    /**
     * 初始化
     *
     * @return true - success;false - fail
     * @author zhuzhenlei
     * @brief SDK init
     */
    private boolean initeSdk() {
        // init net sdk
        if (!HCNetSDK.getInstance().NET_DVR_Init()) {
            Log.e(TAG, "HCNetSDK init is failed!");
            return false;
        }
        // HCNetSDK.getInstance().NET_DVR_SetLogToFile(3, "/mnt/sdcard/sdklog/", true);
        HCNetSDK.getInstance().NET_DVR_SetLogToFile(3, "/mnt/sdcard/hksdk/log/", true);

        return true;
    }

    // GUI init
    private boolean initeActivity() {
        findViews();
        Sur_Player.getHolder().addCallback(this);
        setListeners();

        return true;
    }

    private void  ChangeSingleSurFace(boolean bSingle) {
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);

        for (int i = 0; i < 4; i++) {
            if (playView[i] == null) {
                playView[i] = new PlaySurfaceView(this);
                playView[i].setParam(metric.widthPixels); // 设置屏幕宽高
                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.WRAP_CONTENT,
                        FrameLayout.LayoutParams.WRAP_CONTENT);
                params.bottomMargin =
                        playView[i].getM_iHeight() - (i / 2) * playView[i].getM_iHeight();
                params.leftMargin = (i % 2) * playView[i].getM_iWidth();
//                params.gravity = Gravity.BOTTOM | Gravity.LEFT;
                params.gravity = Gravity.TOP | Gravity.LEFT;
                addContentView(playView[i], params);

                playView[i].setVisibility(View.INVISIBLE);

            }
        }

        if (bSingle) { // 预览一个通道
            // ��·ֻ��ʾ����1
            for (int i = 0; i < 4; ++i) {
                playView[i].setVisibility(View.INVISIBLE);
            }
            playView[0].setParam(metric.widthPixels * 2);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.WRAP_CONTENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT);
            params.bottomMargin = playView[3].getM_iHeight() - (3 / 2) * playView[3].getM_iHeight();
//            params.bottomMargin = 0;
            params.leftMargin = 0;
            // params.
//            params.gravity = Gravity.BOTTOM | Gravity.LEFT;
            params.gravity = Gravity.TOP | Gravity.LEFT;
            playView[0].setLayoutParams(params);
            playView[0].setVisibility(View.VISIBLE);
        } else { // 预览4个通道
            for (int i = 0; i < 4; ++i) {
                playView[i].setVisibility(View.VISIBLE);
            }

            playView[0].setParam(metric.widthPixels);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.WRAP_CONTENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT);
            params.bottomMargin = playView[0].getM_iHeight() - (0 / 2) * playView[0].getM_iHeight();
            params.leftMargin = (0 % 2) * playView[0].getM_iWidth();
//            params.gravity = Gravity.BOTTOM | Gravity.LEFT;
            params.gravity = Gravity.TOP | Gravity.LEFT;
            playView[0].setLayoutParams(params);
        }

    }

    // get controller instance
    private void findViews() {
        btn_Login = (Button) findViewById(R.id.btn_Login);
        btn_Preview = (Button) findViewById(R.id.btn_Preview);
        btn_Playback = (Button) findViewById(R.id.btn_Playback);

        btn_ParamCfg = (Button) findViewById(R.id.btn_ParamCfg);
        btn_Capture = (Button) findViewById(R.id.btn_Capture);
        btn_Record = (Button) findViewById(R.id.btn_Record);

        btn_Talk = (Button) findViewById(R.id.btn_Talk);
        btn_PTZ = (Button) findViewById(R.id.btn_PTZ);
        btn_OTHER = (Button) findViewById(R.id.btn_OTHER);

        m_oIPAddr = (EditText) findViewById(R.id.EDT_IPAddr);
        m_oPort = (EditText) findViewById(R.id.EDT_Port);
        m_oUser = (EditText) findViewById(R.id.EDT_User);
        m_oPsd = (EditText) findViewById(R.id.EDT_Psd);

        layout11 = findViewById(R.id.layout11);

        Sur_Player = (SurfaceView) findViewById(R.id.Sur_Player);

        findViewById(R.id.btn_perview_chn1).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // 单路预览
                if (iPlayID < 0) {
                    ChangeSingleSurFace(true);
                    playView[0].startPreview(iLoginID, iStartChannel + 0);
                    iPlayID = playView[0].m_iPreviewHandle;
                    // startPreviewWithChannel(0);
                    ((TextView) findViewById(R.id.btn_perview_chn1)).setText("stop channel 1");
                } else {
                    playView[0].stopPreview();
                    iPlayID = -1;
                    // stopSinglePreview();
                    ((TextView) findViewById(R.id.btn_perview_chn1)).setText("perview_chn 1");
                }
            }
        });

        findViewById(R.id.btn_perview_chn2).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // 单路预览
                if (iPlayID < 0) {
                    ChangeSingleSurFace(true);
                    playView[0].startPreview(iLoginID, iStartChannel + 1);
                    iPlayID = playView[0].m_iPreviewHandle;
                    // startPreviewWithChannel(0);
                    ((TextView) findViewById(R.id.btn_perview_chn2)).setText("stop channel 2");
                } else {
                    playView[0].stopPreview();
                    iPlayID = -1;
                    // stopSinglePreview();
                    ((TextView) findViewById(R.id.btn_perview_chn2)).setText("perview_chn 2");
                }
            }
        });
    }

    // listen
    private void setListeners() {
        btn_Login.setOnClickListener(Login_Listener);
        btn_Preview.setOnClickListener(Preview_Listener);
        btn_Playback.setOnClickListener(Playback_Listener);
        btn_ParamCfg.setOnClickListener(ParamCfg_Listener);
        btn_Capture.setOnClickListener(Capture_Listener);
        btn_Record.setOnClickListener(Record_Listener);
        btn_Talk.setOnClickListener(Talk_Listener);
        btn_OTHER.setOnClickListener(OtherFunc_Listener);
        btn_PTZ.setOnTouchListener(PTZ_Listener);
    }

    // ptz listener
    private OnTouchListener PTZ_Listener = new OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            try {
                if (iLoginID < 0) {
                    Log.e(TAG, "please login on a device first");
                    return false;
                }
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (m_bPTZL == false) {
                        if (!HCNetSDK.getInstance().
                                NET_DVR_PTZControl_Other(iLoginID, iStartChannel, PTZCommand.PAN_LEFT, 0)) {
                            Log.e(TAG, "start PAN_LEFT failed with error code: " + HCNetSDK.getInstance().NET_DVR_GetLastError());
                        } else {
                            Log.i(TAG, "start PAN_LEFT succ");
                        }
                    } else {
                        if (!HCNetSDK.getInstance()
                                .NET_DVR_PTZControl_Other(iLoginID, iStartChannel, PTZCommand.PAN_RIGHT, 0)) {
                            Log.e(TAG, "start PAN_RIGHT failed with error code: " + HCNetSDK.getInstance().NET_DVR_GetLastError());
                        } else {
                            Log.i(TAG, "start PAN_RIGHT succ");
                        }
                    }
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (m_bPTZL == false) {
                        if (!HCNetSDK.getInstance().NET_DVR_PTZControl_Other(iLoginID, iStartChannel, PTZCommand.PAN_LEFT, 1)) {
                            Log.e(TAG, "stop PAN_LEFT failed with error code: " + HCNetSDK.getInstance().NET_DVR_GetLastError());
                        } else {
                            Log.i(TAG, "stop PAN_LEFT succ");
                        }
                        m_bPTZL = true;
                        btn_PTZ.setText("PTZ(R)");
                    } else {
                        if (!HCNetSDK.getInstance().NET_DVR_PTZControl_Other(iLoginID, iStartChannel, PTZCommand.PAN_RIGHT, 1)) {
                            Log.e(TAG, "stop PAN_RIGHT failed with error code: " + HCNetSDK.getInstance().NET_DVR_GetLastError());
                        } else {
                            Log.i(TAG, "stop PAN_RIGHT succ");
                        }
                        m_bPTZL = false;
                        btn_PTZ.setText("PTZ(L)");
                    }
                }
                return true;
            } catch (Exception err) {
                Log.e(TAG, "error: " + err.toString());
                return false;
            }
        }
    };
    // preset listener
    private OnClickListener OtherFunc_Listener = new OnClickListener() {
        public void onClick(View v) {
            // PTZTest.TEST_PTZ(iPlayID, iLoginID, iStartChannel);
            // ConfigTest.Test_ScreenConfig(iLoginID, iStartChannel);
            // PTZTest.TEST_PTZ(iPlayID, iLoginID, iStartChannel);

            /*
             * try { //PictureTest.PicUpload(iLoginID); } catch
             * (InterruptedException e) { // TODO Auto-generated catch block
             * e.printStackTrace(); }
             */

            //PictureTest.BaseMap(iLoginID);
            // DecodeTest.PicPreview(iLoginID);
            //  ManageTest.TEST_Manage(iLoginID);
//             AlarmTest.Test_SetupAlarm(iLoginID);
//             OtherFunction.TEST_OtherFunc(iPlayID, iLoginID, iStartChannel);
            JNATest.TEST_Config(iPlayID, iLoginID, iStartChannel);
            // JNATest.TEST_EzvizConfig(iPlayID, iLoginID, iStartChannel);
//            ConfigTest.TEST_Config(iPlayID, iLoginID, iStartChannel);
            // HttpTest.Test_HTTP();
//             ScreenTest.TEST_Screen(iLoginID);

//             get_ddns_Info(appkey, appSecret);  //get ddns info by using appkey and app secret
//        	 get_ddns_Info_HC();             
        }
    };

    // Test Activity result
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE) {
            if (resultCode == 1 && data != null) {
                m_retUrl = data.getStringExtra("Info");
                Log.e(TAG, "m_retUrl: " + m_retUrl);

                accessToken = m_retUrl.substring(m_retUrl.indexOf("access_token") + 13, m_retUrl.indexOf("access_token") + 77);
                Log.e(TAG, "accessToken: " + accessToken);
                areaDomain = m_retUrl.substring(m_retUrl.indexOf("areaDomain") + 11);
                Log.e(TAG, "areaDomain: " + areaDomain);
            } else {
                Log.e(TAG, "resultCode!= 1");
            }

            new Thread(new Runnable() {  //inner class - new thread to get device list
                @Override
                public void run() {
                    get_device_ip();
                }
            }).start();
        }
    }


    // Talk listener
    private OnClickListener Talk_Listener = new OnClickListener() {
        public void onClick(View v) {
            try {
                if (m_bTalkOn == false) {
                    if (VoiceTalk.startVoiceTalk(iLoginID) >= 0) {
                        m_bTalkOn = true;
                        btn_Talk.setText("Stop");
                    }
                } else {
                    if (VoiceTalk.stopVoiceTalk()) {
                        m_bTalkOn = false;
                        btn_Talk.setText("Talk");
                    }
                }
            } catch (Exception err) {
                Log.e(TAG, "error: " + err.toString());
            }
        }
    };
    // record listener
    private OnClickListener Record_Listener = new OnClickListener() {
        public void onClick(View v) {
            if (!m_bSaveRealData) {
                if (!HCNetSDKJNAInstance.getInstance().NET_DVR_SaveRealData_V30(iPlayID, 0x2, "/sdcard/test.mp4")) {
                    System.out.println("NET_DVR_SaveRealData_V30 failed! error: " + HCNetSDK.getInstance().NET_DVR_GetLastError());
                    return;
                } else {
                    System.out.println("NET_DVR_SaveRealData_V30 succ!");
                }
                m_bSaveRealData = true;
            } else {
                if (!HCNetSDK.getInstance().NET_DVR_StopSaveRealData(iPlayID)) {
                    System.out.println("NET_DVR_StopSaveRealData failed! error: " + HCNetSDK.getInstance().NET_DVR_GetLastError());
                } else {
                    System.out.println("NET_DVR_StopSaveRealData succ!");
                }
                m_bSaveRealData = false;
            }
        }
    };
    // capture listener
    private OnClickListener Capture_Listener = new OnClickListener() {
        public void onClick(View v) {
            try {

                if (iPlayID < 0) {
                    Log.e(TAG, "please start preview first");
                    return;
                } else {
//                    HCNetSDKJNAInstance.getInstance().NET_DVR_SetCapturePictureMode(0x1);
                    if (HCNetSDKJNAInstance.getInstance().NET_DVR_CapturePictureBlock(iPlayID, "/sdcard/capblock.jpg", 0)) {
                        Log.e(TAG, "NET_DVR_CapturePictureBlock Succ!");
                    } else {
                        Log.e(TAG, "NET_DVR_CapturePictureBlock fail! Err:" + HCNetSDK.getInstance().NET_DVR_GetLastError());
                    }
                }
//            	if(iPlaybackID < 0){
//            		Log.e(TAG, "please start preview first");
//            		return;
//            	}
//            	else{
//            		if(HCNetSDKJNAInstance.getInstance().NET_DVR_PlayBackCaptureFile(iPlaybackID, "/sdcard/capfile.bmp")){
//                     	Log.e(TAG, "NET_DVR_PlayBackCaptureFile succ");
//                     }else{
//                    	 Log.e(TAG, "NET_DVR_PlayBackCaptureFile fail " + HCNetSDK.getInstance().NET_DVR_GetLastError());
//                     }
//            	}


//                if (iPort < 0) {
//                    Log.e(TAG, "please start preview first");
//                    return;
//                }
//                Player.MPInteger stWidth = new Player.MPInteger();
//                Player.MPInteger stHeight = new Player.MPInteger();
//                if (!Player.getInstance().getPictureSize(iPort, stWidth,
//                        stHeight)) {
//                    Log.e(TAG, "getPictureSize failed with error code:"
//                            + Player.getInstance().getLastError(iPort));
//                    return;
//                }
//                int nSize = 5 * stWidth.value * stHeight.value;
//                byte[] picBuf = new byte[nSize];
//                Player.MPInteger stSize = new Player.MPInteger();
//                if (!Player.getInstance()
//                        .getBMP(iPort, picBuf, nSize, stSize)) {
//                    Log.e(TAG, "getBMP failed with error code:"
//                            + Player.getInstance().getLastError(iPort));
//                    return;
//                }
//
//                SimpleDateFormat sDateFormat = new SimpleDateFormat(
//                        "yyyy-MM-dd-hh:mm:ss");
//                String date = sDateFormat.format(new java.util.Date());
//                FileOutputStream file = new FileOutputStream("/mnt/sdcard/"
//                        + date + ".bmp");
//                file.write(picBuf, 0, stSize.value);
//                file.close();
            } catch (Exception err) {
                Log.e(TAG, "error: " + err.toString());
            }
        }
    };


    private PlaybackCallBack getPlayerbackPlayerCbf() {
        PlaybackCallBack cbf = new PlaybackCallBack() {
            @Override
            public void fPlayDataCallBack(int iPlaybackHandle, int iDataType, byte[] pDataBuffer, int iDataSize) {
                // player channel 1
                // PprocessRealData(1, iDataType, pDataBuffer, iDataSize, Player.STREAM_FILE);
                HkActivity.this.processRealData(1, iDataType, pDataBuffer, iDataSize, Player.STREAM_REALTIME);
            }
        };
        return cbf;
    }


    /**
     * 回放，通过时间段进行录像回放。
     */
    private OnClickListener Playback_Listener = new OnClickListener() {

        public void onClick(View v) {
            try {
                if (iLoginID < 0) {
                    Log.e(TAG, "please login on a device first");
                    return;
                }
                if (iPlaybackID < 0) {
                    if (iPlayID >= 0) {
                        Log.i(TAG, "Please stop preview first");
                        return;
                    }

                    // 回放单路
                    ChangeSingleSurFace(true);

                    NET_DVR_TIME timeStart = new NET_DVR_TIME();
                    NET_DVR_TIME timeStop = new NET_DVR_TIME();

                    timeStart.dwYear = 2018;
                    timeStart.dwMonth = 8;
                    timeStart.dwDay = 15;

                    timeStop.dwYear = 2018;
                    timeStop.dwMonth = 10;
                    timeStop.dwDay = 29;

                    NET_DVR_VOD_PARA vodParma = new NET_DVR_VOD_PARA();
                    vodParma.struBeginTime = timeStart;
                    vodParma.struEndTime = timeStop;
                    vodParma.byStreamType = 0;
                    vodParma.struIDInfo.dwChannel = iStartChannel + 1;
                    vodParma.hWnd = playView[0].getHolder().getSurface();

                    //	 NET_DVR_PLAYCOND playcond = new NET_DVR_PLAYCOND();
                    //	 playcond.dwChannel = iStartChannel;
                    //	 playcond.struStartTime = timeStart;
                    //	 playcond.struStopTime = timeStop;
                    //	 playcond.byDrawFrame = 0;
                    //	 playcond.byStreamType = 1;

                    //     iPlaybackID = HCNetSDK.getInstance().NET_DVR_PlayBackReverseByTime_V40(iLoginID, playView[0].getHolder().getSurface(), playcond);


                    iPlaybackID = HCNetSDK.getInstance().NET_DVR_PlayBackByTime_V40(iLoginID, vodParma);


                    //	 PlaybackCallBack fPlaybackDataCallBack = getPlayerbackPlayerCbf();
                    //	 if (fPlaybackDataCallBack == null)
                    //		{
                    //		    Log.e(TAG, "fPlaybackDataCallBack object is failed!");
                    //         return ;
                    //		}
                    //
                    //	 if(!HCNetSDK.getInstance().NET_DVR_SetPlayDataCallBack(iPlaybackID, fPlaybackDataCallBack))
                    //		{
                    //			Log.e(TAG, "Set playback callback failed!");
                    //			return ;
                    //		}


                    if (iPlaybackID >= 0) {
                        NET_DVR_PLAYBACK_INFO struPlaybackInfo = null;
                        if (!HCNetSDK.getInstance().NET_DVR_PlayBackControl_V40(iPlaybackID, PlaybackControlCommand.NET_DVR_PLAYSTART, null, 0, null)) {
                            Log.e(TAG, "net sdk playback start failed!");
                            return;
                        }
                        m_bStopPlayback = false;
                        btn_Playback.setText("Stop");


                        //     if(HCNetSDK.getInstance().NET_DVR_PlayBackControl_V40(iPlaybackID, PlaybackControlCommand.NET_DVR_PLAYPAUSE, null, 0, struPlaybackInfo))
                        //     {
                        //    	 Log.e(TAG, "NET_DVR_PlayBackControl_V40 pause succ");
                        //     }
                        //
                        //     if(HCNetSDK.getInstance().NET_DVR_PlayBackControl_V40(iPlaybackID, PlaybackControlCommand.NET_DVR_PLAYRESTART, null, 0, struPlaybackInfo))
                        //     {
                        //    	 Log.e(TAG, "NET_DVR_PLAYRESTART restart succ");
                        //     }
                        //
                        //     if(HCNetSDK.getInstance().NET_DVR_PlayBackControl_V40(iPlaybackID, PlaybackControlCommand.NET_DVR_PLAYFAST, null, 0, struPlaybackInfo))
                        //     {
                        //    	 Log.e(TAG, "NET_DVR_PLAYFAST succ");
                        //     }
                        //     if(HCNetSDK.getInstance().NET_DVR_PlayBackControl_V40(iPlaybackID, PlaybackControlCommand.NET_DVR_PLAYSLOW, null, 0, struPlaybackInfo))
                        //     {
                        //    	 Log.e(TAG, "NET_DVR_PLAYSLOW succ");
                        //     }
                        //
                        //     if(HCNetSDK.getInstance().NET_DVR_PlayBackControl_V40(iPlaybackID, PlaybackControlCommand.NET_DVR_PLAYSTARTAUDIO, null, 0, struPlaybackInfo))
                        //     {
                        //    	 Log.e(TAG, "NET_DVR_PLAYSTARTAUDIO succ");
                        //     }


                        Thread thread = new Thread() {
                            public void run() {
                                int nProgress = -1;
                                while (true) {
                                    nProgress = HCNetSDK.getInstance().NET_DVR_GetPlayBackPos(iPlaybackID);
                                    System.out.println("NET_DVR_GetPlayBackPos:" + nProgress);
                                    if (nProgress < 0 || nProgress >= 100) {
                                        break;
                                    }

                                    try {
                                        Thread.sleep(1000);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        };
                        thread.start();
                    } else {
                        Log.i(TAG, "NET_DVR_PlayBackByTime failed, error code: " + HCNetSDK.getInstance().NET_DVR_GetLastError());
                    }
                } else {
                    m_bStopPlayback = true;
                    if (!HCNetSDK.getInstance().NET_DVR_StopPlayBack(iPlaybackID)) {
                        Log.e(TAG, "net sdk stop playback failed");
                    }
                    btn_Playback.setText("Playback");
                    iPlaybackID = -1;

                    ChangeSingleSurFace(false);
                }
            } catch (Exception err) {
                Log.e(TAG, "error: " + err.toString());
            }
        }
    };

 
 
    /**
    private Button.OnClickListener Playback_Listener = new Button.OnClickListener() {

        public void onClick(View v) {
            try {
                if (iLoginID < 0) {
                    Log.e(TAG, "please login on a device first");
                    return;
                }
                if (iPlaybackID < 0) {
                    if (iPlayID >= 0) {
                        Log.i(TAG, "Please stop preview first");
                        return;
                    }
                    
                    ChangeSingleSurFace(true);
                    iPlaybackID = HCNetSDK.getInstance()
                            .NET_DVR_PlayBackByName(iLoginID,
                                    new String("ch0001_00000000300000000"), playView[0].getHolder().getSurface());  
                    if (iPlaybackID >= 0) {
                        NET_DVR_PLAYBACK_INFO struPlaybackInfo = null;
                        if (!HCNetSDK
                                .getInstance()
                                .NET_DVR_PlayBackControl_V40(
                                        iPlaybackID,
                                        PlaybackControlCommand.NET_DVR_PLAYSTART,
                                        null, 0, struPlaybackInfo)) {
                            Log.e(TAG, "net sdk playback start failed!");                                                                            
                            return;
                        }
                        
//                        HCNetSDK.getInstance().NET_DVR_PlayBackControl_V40(iPlaybackID, PlaybackControlCommand.NET_DVR_PLAYPAUSE, null, 0, struPlaybackInfo);
//                        Thread thread = new Thread()
//                		{
//                	   		public void run()
//                	   		{
//                	   			try 
//                	   			{
//                					sleep(10*1000);
//                				} catch (InterruptedException e) 
//                				{
//                					e.printStackTrace();
//                				}	
//                	   			HCNetSDK.getInstance().NET_DVR_PlayBackControl_V40(iPlaybackID, PlaybackControlCommand.NET_DVR_PLAYRESTART, null, 0, null);
//                	   		}
//                	   	};
//                	   	thread.start();
//                        
//                        m_bStopPlayback = false;
//                        btn_Playback.setText("Stop");
//                        
//                        int iPlaybackIndex = HCNetSDKJNAInstance.getInstance().NET_DVR_GetPlayBackPlayerIndex(iPlaybackID);
//                        if(iPlaybackIndex != -1){
//                        	Log.e(TAG, "NET_DVR_GetPlayBackPlayerIndex Succ! iPlaybackIndex = %d" + iPlaybackIndex);
//                        }
//                        
//                        if(HCNetSDKJNAInstance.getInstance().NET_DVR_PlayBackCaptureFile(iPlaybackID, "/mnt/sdcard/capture_02.dmp")){
//                        	Log.e(TAG, "NET_DVR_PlayBackCaptureFile succ");
//                        }

                        Thread thread11 = new Thread() {
                            public void run() {
                                int nProgress = -1;
                                while (true) {
                                    nProgress = HCNetSDK.getInstance()
                                            .NET_DVR_GetPlayBackPos(
                                                    iPlaybackID);
                                    System.out
                                            .println("NET_DVR_GetPlayBackPos:"
                                                    + nProgress);
                                    if (nProgress < 0 || nProgress >= 100) {
                                        break;
                                    }
                                    try {
                                        Thread.sleep(1000);
                                    } catch (InterruptedException e) { // TODO
                                                                       // Auto-generated
                                                                       // catch
                                                                       // block
                                        e.printStackTrace();
                                    }

                                }
                            }
                        };
                        thread11.start();
                    } else {
                        Log.i(TAG,
                                "NET_DVR_PlayBackByName failed, error code: "
                                        + HCNetSDK.getInstance()
                                                .NET_DVR_GetLastError());
                    }
                } else {
                    m_bStopPlayback = true;
                    if (!HCNetSDK.getInstance().NET_DVR_StopPlayBack(
                            iPlaybackID)) {
                        Log.e(TAG, "net sdk stop playback failed");
                    } // player stop play
                    btn_Playback.setText("Playback");
                    iPlaybackID = -1;
                    
                    ChangeSingleSurFace(false);
                }
            } catch (Exception err) {
                Log.e(TAG, "error: " + err.toString());
            }
        }
    };
*/

    // login listener
    private OnClickListener Login_Listener = new OnClickListener() {
        public void onClick(View v) {
            try {
                if (iLoginID < 0) {
                    // login on the device
                    iLoginID = loginDevice();
                    if (iLoginID < 0) {
                        Log.e(TAG, "This device logins failed!");
                        return;
                    } else {
                        System.out.println("iLoginID=" + iLoginID);
                    }
                    // get instance of exception callback and set
                    ExceptionCallBack oexceptionCbf = getExceptiongCbf();
                    if (oexceptionCbf == null) {
                        Log.e(TAG, "ExceptionCallBack object is failed!");
                        return;
                    }

                    if (!HCNetSDK.getInstance().NET_DVR_SetExceptionCallBack(oexceptionCbf)) {
                        Log.e(TAG, "NET_DVR_SetExceptionCallBack is failed!");
                        return;
                    }

                    btn_Login.setText("Logout");
                    Log.i(TAG, "Login sucess  ***********************");
                } else {
                    // whether we have logout
                    if (!HCNetSDK.getInstance().NET_DVR_Logout_V30(iLoginID)) {
                        Log.e(TAG, " NET_DVR_Logout is failed!");
                        // if (!HCNetSDKJNAInstance.getInstance().NET_DVR_DeleteOpenEzvizUser(iLoginID)) {
                        // Log.e(TAG, " NET_DVR_DeleteOpenEzvizUser is failed!");
                        return;
                    }
                    btn_Login.setText("Login");
                    iLoginID = -1;
                }

            } catch (Exception err) {
                Log.e(TAG, "error: " + err.toString());
            }
        }
    };

    private RealPlayCallBack getRealPlayerCbf() {
        RealPlayCallBack cbf = new RealPlayCallBack() {
            public void fRealDataCallBack(int iRealHandle, int iDataType, byte[] pDataBuffer, int iDataSize) {
                try {
                    FileOutputStream file = new FileOutputStream("/sdcard/RealPlay.mp4", true);
                    file.write(pDataBuffer, 0, iDataSize);
                    file.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        };
        return cbf;
    }


    /**
     * 实时预览
     */
    private OnClickListener Preview_Listener = new OnClickListener() {
        public void onClick(View v) {
            try {
                ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
                        HkActivity.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                if (iLoginID < 0) {
                    Log.e(TAG, "please login on device first");
                    return;
                }

                if (iPlaybackID >= 0) {
                    Log.i(TAG, "Please stop palyback first");
                    return;
                }

//                if (true)
//                    return;

                if (m_bInsideDecode) {
                    // 多路预览
                    if (iChannelNum > 1) { // preview more than a channel
                        if (!m_bMultiPlay) {
                            startMultiPreview();
                            m_bMultiPlay = true;
                            btn_Preview.setText("Stop");
                        } else {
                            stopMultiPreview();
                            m_bMultiPlay = false;
                            btn_Preview.setText("Preview");
                        }
                        // 单路预览
                    } else { // preivew a channel
                        if (iPlayID < 0) {
                            startSinglePreview();
                        } else {
                            stopSinglePreview();
                            btn_Preview.setText("Preview");
                        }
                    }
                } else {
                    toast("m_bInsideDecode m_bInsideDecode m_bInsideDecode");
                    if (iPlayID < 0) {
                        if (iPlaybackID >= 0) {
                            Log.i(TAG, "Please stop palyback first");
                            return;
                        }

//                     NET_DVR_RealPlay_V40 callback
//                     RealPlayCallBack fRealDataCallBack = getRealPlayerCbf();
//                     if (fRealDataCallBack == null) {
//                         Log.e(TAG, "fRealDataCallBack object is failed!");
//                         return;
//                     }

                        Log.i(TAG, "iStartChannel:" + iStartChannel);
                        NET_DVR_PREVIEWINFO previewInfo = new NET_DVR_PREVIEWINFO();
                        previewInfo.lChannel = iStartChannel;
                        previewInfo.dwStreamType = 1; // substream
                        previewInfo.bBlocked = 1;

                        iPlayID = HCNetSDK.getInstance().NET_DVR_RealPlay_V40(iLoginID, previewInfo, null);
                        if (iPlayID < 0) {
                            Log.e(TAG, "NET_DVR_RealPlay is failed!Err:" + HCNetSDK.getInstance().NET_DVR_GetLastError());
                            return;
                        }
                        Log.i(TAG, "NetSdk Play sucess ***********************3***************************");
                        btn_Preview.setText("Stop");
                        ///////////////////////
                        // real data call back
                        //    if(rdf == null) {
                        //        rdf = new RealDataCallBack() {
                        //            public void fRealDataCallBack(int iRealHandle, int iDataType, byte[] pDataBuffer, int iDataSize) {
                        //                HkActivity.this.processRealData(1, iDataType, pDataBuffer, iDataSize, Player.STREAM_REALTIME);
                        //            }
                        //        };
                        //    }
                        //
                        //      if(!HCNetSDK.getInstance().NET_DVR_SetRealDataCallBack(iPlayID, rdf)){
                        //      	Log.e(TAG, "NET_DVR_SetRealDataCallBack is failed!Err:" + HCNetSDK.getInstance().NET_DVR_GetLastError());
                        //      }
                        //      Log.i(TAG, "NET_DVR_SetRealDataCallBack sucess ***************************************************");
                        /////////////////////////


                        //std data call back
//                        if (cbf == null) {
//                            cbf = new StdDataCallBack() {
//                                public void fStdDataCallback(int iRealHandle, int iDataType, byte[] pDataBuffer, int iDataSize) {
//                                    HkActivity.this.processRealData(1, iDataType, pDataBuffer, iDataSize, Player.STREAM_REALTIME);
//                                }
//                            };
//                        }
//
//                        if (!HCNetSDK.getInstance().NET_DVR_SetStandardDataCallBack(iPlayID, cbf)) {
//                            Log.e(TAG, "NET_DVR_SetStandardDataCallBack is failed!Err:" + HCNetSDK.getInstance().NET_DVR_GetLastError());
//                        }
                        Log.i(TAG, "NET_DVR_SetStandardDataCallBack sucess ***************************************************");
                    } else {
                        stopSinglePreview();
                        btn_Preview.setText("Preview");
                    }
                }
            } catch (Exception err) {
                Log.e(TAG, "error: " + err.toString());
            }
        }
    };

    /**
     * @fn getStdDataPlayerCbf
     * @author
     * @brief get realplay callback instance
     * @param NULL
     *            [in]
     * @param NULL
     *            [out]
     * @return callback instance
     */
//    private static StdDataCallBack cbf = null;
//    private StdDataCallBack getStdDataPlayerCbf() {
//    	
//    	StdDataCallBack cbf = new StdDataCallBack(){ 
//            public void fStdDataCallback(int iRealHandle, int iDataType, byte[] pDataBuffer, int iDataSize) {
//                HkActivity.this.processRealData(1, iDataType, pDataBuffer, iDataSize, Player.STREAM_REALTIME);
//            }
//        };
//        return cbf;
//    };

    /**
     * 播放实时视频数据
     *
     * @param iPlayViewNo - player channel [in]
     * @param iDataType   - data type [in]
     * @param pDataBuffer - data buffer [in]
     * @param iDataSize   - data size [in]
     * @param iStreamMode - stream mode [in]
     * @return NULL
     * @fn processRealData
     * @author zhuzhenlei
     * @brief process real data
     */
    public void processRealData(int iPlayViewNo, int iDataType, byte[] pDataBuffer, int iDataSize, int iStreamMode) {
        if (HCNetSDK.NET_DVR_SYSHEAD == iDataType) {
            if (iPort >= 0) {
                return;
            }
            iPort = Player.getInstance().getPort();
            if (iPort == -1) {
                Log.e(TAG, "getPort is failed with: " + Player.getInstance().getLastError(iPort));
                return;
            }
            Log.i(TAG, "getPort succ with: " + iPort);
            if (iDataSize > 0) {
                if (!Player.getInstance().setStreamOpenMode(iPort, iStreamMode)) // set stream mode{
                    Log.e(TAG, "setStreamOpenMode failed");
                return;
            }
            if (!Player.getInstance().openStream(iPort, pDataBuffer, iDataSize, 2 * 1024 * 1024)) {// open stream
                Log.e(TAG, "openStream failed");
                return;
            }
            if (!Player.getInstance().play(iPort, Sur_Player.getHolder())) {
                Log.e(TAG, "play failed");
                return;
            }
            if (!Player.getInstance().playSound(iPort)) {
                Log.e(TAG, "playSound failed with error code:" + Player.getInstance().getLastError(iPort));
                return;
            }
        } else {
            try {
                FileOutputStream file = new FileOutputStream("/sdcard/StdPlayData.mp4", true);
                file.write(pDataBuffer, 0, iDataSize);
                file.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            //  if (!Player.getInstance().inputData(iPort, pDataBuffer, iDataSize)) {
            //      for (int i = 0; i < 4000 && iPlaybackID >= 0 && !m_bStopPlayback; i++) {
            //          if (Player.getInstance().inputData(iPort, pDataBuffer, iDataSize)) {
            //              break;
            //          }
            //
            ////          if (i % 100 == 0) {
            ////              Log.e(TAG, "inputData failed with: " + Player.getInstance().getLastError(iPort) + ", i:" + i);
            ////          }
            //
            //          try {
            //              Thread.sleep(10);
            //          } catch (InterruptedException e) {
            //              e.printStackTrace();
            //          }
            //      }
            //  }
        }
    }

    // configuration listener
    private OnClickListener ParamCfg_Listener = new OnClickListener() {
        public void onClick(View v) {
            try {
                paramCfg(iLoginID);
            } catch (Exception err) {
                Log.e(TAG, "error: " + err.toString());
            }
        }
    };

    /**
     * todo 后加的
     * 带通道的预览视频
     */
    private void startPreviewWithChannel(int channelno) {
        if (iPlaybackID >= 0) {
            Log.i(TAG, "Please stop palyback first");
            toast("播放失败， Please stop palyback first");
            return;
        }

        Log.i(TAG, "iStartChannel:" + iStartChannel);

        NET_DVR_PREVIEWINFO previewInfo = new NET_DVR_PREVIEWINFO();
        previewInfo.lChannel = iStartChannel; // iStartChannel channelno
        previewInfo.dwStreamType = 0; // substream
        previewInfo.bBlocked = 1;
        previewInfo.hHwnd = playView[0].getHolder();

        iPlayID = HCNetSDK.getInstance().NET_DVR_RealPlay_V40(iLoginID, previewInfo, null);
        if (iPlayID < 0) {
            Log.e(TAG, "NET_DVR_RealPlay is failed!Err:" + HCNetSDK.getInstance().NET_DVR_GetLastError());
            toast("播放失败，error:" + HCNetSDK.getInstance().NET_DVR_GetLastError());
            return;
        }

        boolean bRet = HCNetSDKJNAInstance.getInstance().NET_DVR_OpenSound(iPlayID);
        if (bRet) {
            Log.e(TAG, "NET_DVR_OpenSound Success!");
        }

        Log.i(TAG, "NetSdk Play sucess ****************** ");
        btn_Preview.setText("Stop");
    }


    /**
     * 单路预览，目前用于登录IPC网络摄像机设备预览
     */
    private void startSinglePreview() {
        if (iPlaybackID >= 0) {
            Log.i(TAG, "Please stop palyback first");
            return;
        }

        Log.i(TAG, "iStartChannel:" + iStartChannel);

        NET_DVR_PREVIEWINFO previewInfo = new NET_DVR_PREVIEWINFO();
        previewInfo.lChannel = iStartChannel;
        previewInfo.dwStreamType = 0; // substream
        previewInfo.bBlocked = 1;
        previewInfo.hHwnd = playView[0].getHolder();

        iPlayID = HCNetSDK.getInstance().NET_DVR_RealPlay_V40(iLoginID, previewInfo, null);
        if (iPlayID < 0) {
            Log.e(TAG, "NET_DVR_RealPlay is failed!Err:" + HCNetSDK.getInstance().NET_DVR_GetLastError());
            return;
        }


        //  iPort = HCNetSDKJNAInstance.getInstance().NET_DVR_GetRealPlayerIndex(iPlayID);
        //  if(iPort != -1)
        //  {
        //  	Log.e(TAG, "NET_DVR_GetRealPlayerIndex Succ! iPlayIndex = " + iPort);
        //  }
        //
        //  Player.getInstance().getLastError(iPort);

        boolean bRet = HCNetSDKJNAInstance.getInstance().NET_DVR_OpenSound(iPlayID);
        if (bRet) {
            Log.e(TAG, "NET_DVR_OpenSound Succ!");
        }

        //  if(HCNetSDKJNAInstance.getInstance().NET_DVR_CapturePicture(iPlayID, "/mnt/sdcard/capture_01.dmp")){
        //  	Log.e(TAG, "NET_DVR_CapturePicture Succ!");
        //  }
        //  else
        //  {
        //  	Log.e(TAG, "NET_DVR_CapturePicture fail! Err:" + HCNetSDK.getInstance().NET_DVR_GetLastError());
        //  }
        //
        //
        //  short volume = 55;
        //  if(HCNetSDKJNAInstance.getInstance().NET_DVR_Volume(iPlayID, volume)){
        //  	Log.e(TAG, "NET_DVR_Volume Succ!");
        //  }


        Log.i(TAG, "NetSdk Play sucess ************** 3 *************");
        btn_Preview.setText("Stop");
    }

    private void startMultiPreview() {

        for (int i = 0; i < 4; i++) {
            playView[i].startPreview(iLoginID, iStartChannel + i);
        }

        // new Thread(new Runnable() {
        //
        // @Override
        // public void run() {
        // // TODO Auto-generated method stub
        // for (int i = 0; i < 4; i++) {
        // while (!playView[i].bCreate) {
        // try {
        // Thread.sleep(100);
        // Log.i(TAG, "wait for surface create");
        // } catch (InterruptedException e) {
        // // TODO Auto-generated catch block
        // e.printStackTrace();
        // }
        // }
        //
        // NET_DVR_PREVIEWINFO previewInfo = new NET_DVR_PREVIEWINFO();
        // previewInfo.lChannel = iStartChannel + i;
        // previewInfo.dwStreamType = 0; // substream
        // previewInfo.bBlocked = 1;
        // previewInfo.hHwnd = playView[i].getHolder();
        //
        // playView[i].m_iPreviewHandle =
        // HCNetSDK.getInstance().NET_DVR_RealPlay_V40(
        // iLoginID, previewInfo, null);
        // if (playView[i].m_iPreviewHandle < 0) {
        // Log.e(TAG, "NET_DVR_RealPlay is failed!Err:"
        // + HCNetSDK.getInstance().NET_DVR_GetLastError());
        // }
        // }
        // }
        // }).start();

        iPlayID = playView[0].m_iPreviewHandle;
    }

    private void stopMultiPreview() {
        int i = 0;
        for (i = 0; i < 4; i++) {
            playView[i].stopPreview();
        }
        iPlayID = -1;
    }

    /**
     * @return NULL
     * @fn stopSinglePreview
     * @author zhuzhenlei
     * @brief stop preview
     */
    private void stopSinglePreview() {
        if (iPlayID < 0) {
            Log.e(TAG, "iPlayID < 0");
            return;
        }

        if (HCNetSDKJNAInstance.getInstance().NET_DVR_CloseSound()) {
            Log.e(TAG, "NET_DVR_CloseSound Succ!");
        }

        // net sdk stop preview
        if (!HCNetSDK.getInstance().NET_DVR_StopRealPlay(iPlayID)) {
            Log.e(TAG, "StopRealPlay is failed!Err:"
                    + HCNetSDK.getInstance().NET_DVR_GetLastError());
            return;
        }
        Log.i(TAG, "NET_DVR_StopRealPlay succ");
        iPlayID = -1;
    }

    /**
     * @return login ID
     * @fn loginNormalDevice
     * @author zhuzhenlei
     * @brief login on device
     */
    private int loginNormalDevice() {
        // get instance
        m_oNetDvrDeviceInfoV30 = new NET_DVR_DEVICEINFO_V30();
        //  if (null == m_oNetDvrDeviceInfoV30) {
        //      Log.e(TAG, "HKNetDvrDeviceInfoV30 new is failed!");
        //      return -1;
        //  }
        String strIP = m_oIPAddr.getText().toString();
        int nPort = Integer.parseInt(m_oPort.getText().toString());
        String strUser = m_oUser.getText().toString();
        String strPsd = m_oPsd.getText().toString();
        // call NET_DVR_Login_v30 to login on, port 8000 as default
        int iLogID = HCNetSDK.getInstance()
                .NET_DVR_Login_V30(strIP, nPort, strUser, strPsd, m_oNetDvrDeviceInfoV30);
        if (iLogID < 0) {
            Log.e(TAG, "NET_DVR_Login is failed!Err:" + HCNetSDK.getInstance().NET_DVR_GetLastError());
            return -1;
        }
        if (m_oNetDvrDeviceInfoV30.byChanNum > 0) {
            iStartChannel = m_oNetDvrDeviceInfoV30.byStartChan;
            iChannelNum = m_oNetDvrDeviceInfoV30.byChanNum;
        } else if (m_oNetDvrDeviceInfoV30.byIPChanNum > 0) {
            iStartChannel = m_oNetDvrDeviceInfoV30.byStartDChan;
            iChannelNum = m_oNetDvrDeviceInfoV30.byIPChanNum
                    + m_oNetDvrDeviceInfoV30.byHighDChanNum * 256;
        }

        if (iChannelNum > 1) {
            ChangeSingleSurFace(false);
        } else {
            ChangeSingleSurFace(true);
        }
        Log.i(TAG, "NET_DVR_Login is Successful!");

        return iLogID;
    }

    public static void Test_XMLAbility(int iUserID) {
        byte[] arrayOutBuf = new byte[64 * 1024];
        INT_PTR intPtr = new INT_PTR();
        String strInput = new String("<AlarmHostAbility version=\"2.0\"></AlarmHostAbility>");
        byte[] arrayInBuf = new byte[8 * 1024];
        arrayInBuf = strInput.getBytes();
        if (!HCNetSDK.getInstance().NET_DVR_GetXMLAbility(iUserID,
                HCNetSDK.DEVICE_ABILITY_INFO, arrayInBuf, strInput.length(),
                arrayOutBuf, 64 * 1024, intPtr)) {
            System.out.println("get DEVICE_ABILITY_INFO faild!" + " err: "
                    + HCNetSDK.getInstance().NET_DVR_GetLastError());
        } else {
            System.out.println("get DEVICE_ABILITY_INFO succ!");
        }
    }

    /**
     * @return login ID
     * @fn loginEzvizDevice
     * @author liuyu6
     * @brief login on ezviz device
     */
    private int loginEzvizDevice() {
        return -1;
        /*
         * NET_DVR_OPEN_EZVIZ_USER_LOGIN_INFO struLoginInfo = new
         * NET_DVR_OPEN_EZVIZ_USER_LOGIN_INFO(); NET_DVR_DEVICEINFO_V30
         * struDeviceInfo = new NET_DVR_DEVICEINFO_V30();
         *
         * //String strInput = new String("pbsgp.p2papi.ezviz7.com"); String
         * strInput = new String("open.ys7.com"); //String strInput = new
         * String("pbdev.ys7.com"); //String strInput = new
         * String("183.136.184.67"); byte[] byInput = strInput.getBytes();
         * System.arraycopy(byInput, 0, struLoginInfo.sEzvizServerAddress, 0,
         * byInput.length);
         *
         * struLoginInfo.wPort = 443;
         *
         * strInput = new
         * String("at.43anfq0q9k8zt06vd0ppalfhc4bj177p-3k4ovrh4vu-105zgp6-jgt8edqst"
         * ); byInput = strInput.getBytes(); System.arraycopy(byInput, 0,
         * struLoginInfo.sAccessToken, 0, byInput.length);
         *
         * //strInput = new String("67a7daedd4654dc5be329f2289914859");
         * //byInput = strInput.getBytes(); //System.arraycopy(byInput, 0,
         * struLoginInfo.sSessionID, 0, byInput.length);
         *
         * //strInput = new String("ae1b9af9dcac4caeb88da6dbbf2dd8d5"); strInput
         * = new String("com.hik.visualintercom"); byInput =
         * strInput.getBytes(); System.arraycopy(byInput, 0,
         * struLoginInfo.sAppID, 0, byInput.length);
         *
         * //strInput = new String("78313dadecd92bd11623638d57aa5128"); strInput
         * = new String("226f102a99ad0e078504d380b9ddf760"); byInput =
         * strInput.getBytes(); System.arraycopy(byInput, 0,
         * struLoginInfo.sFeatureCode, 0, byInput.length);
         *
         * //strInput = new
         * String("https://pbopen.ys7.com:443/api/device/transmission");
         * strInput = new String("/api/device/transmission"); byInput =
         * strInput.getBytes(); System.arraycopy(byInput, 0, struLoginInfo.sUrl,
         * 0, byInput.length);
         *
         * strInput = new String("520247131"); byInput = strInput.getBytes();
         * System.arraycopy(byInput, 0, struLoginInfo.sDeviceID, 0,
         * byInput.length);
         *
         * strInput = new String("2"); byInput = strInput.getBytes();
         * System.arraycopy(byInput, 0, struLoginInfo.sClientType, 0,
         * byInput.length);
         *
         * strInput = new String("UNKNOWN"); byInput = strInput.getBytes();
         * System.arraycopy(byInput, 0, struLoginInfo.sNetType, 0,
         * byInput.length);
         *
         * strInput = new String("5.0.1"); byInput = strInput.getBytes();
         * System.arraycopy(byInput, 0, struLoginInfo.sOsVersion, 0,
         * byInput.length);
         *
         * strInput = new String("v.5.1.5.30"); byInput = strInput.getBytes();
         * System.arraycopy(byInput, 0, struLoginInfo.sSdkVersion, 0,
         * byInput.length);
         *
         * int iUserID = -1;
         *
         * iUserID =
         * HCNetSDK.getInstance().NET_DVR_CreateOpenEzvizUser(struLoginInfo,
         * struDeviceInfo);
         *
         * if (-1 == iUserID) { System.out.println("NET_DVR_CreateOpenEzvizUser"
         * + " err: " + HCNetSDK.getInstance().NET_DVR_GetLastError()); return
         * -1; } else {
         * System.out.println("NET_DVR_CreateOpenEzvizUser success"); }
         *
         * Test_XMLAbility(iUserID); Test_XMLAbility(iUserID);
         * Test_XMLAbility(iUserID);
         *
         * return iUserID;
         */

    }

    /**
     * @return login ID
     * @fn loginDevice
     * @author zhangqing
     * @brief login on device
     */
    private int loginDevice() {
        int iLogID = -1;

        iLogID = loginNormalDevice();

        // iLogID = JNATest.TEST_EzvizLogin();
        // iLogID = loginEzvizDevice();

        return iLogID;
    }

    /**
     * @param iUserID - login ID [in]
     * @return NULL
     * @fn paramCfg
     * @author zhuzhenlei
     * @brief configuration
     */
    private void paramCfg(final int iUserID) {
        // whether have logined on
        if (iUserID < 0) {
            Log.e(TAG, "iUserID < 0");
            return;
        }

        // 通道压缩参数
        NET_DVR_COMPRESSIONCFG_V30 struCompress = new NET_DVR_COMPRESSIONCFG_V30();
        if (!HCNetSDK.getInstance().NET_DVR_GetDVRConfig(iUserID,
                HCNetSDK.NET_DVR_GET_COMPRESSCFG_V30, iStartChannel, struCompress)) {
            Log.e(TAG, "NET_DVR_GET_COMPRESSCFG_V30 failed with error code:" + HCNetSDK.getInstance().NET_DVR_GetLastError());
        } else {
            Log.i(TAG, "NET_DVR_GET_COMPRESSCFG_V30 succ");
        }
        // set substream resolution to cif
        struCompress.struNetPara.byResolution = 1;
        if (!HCNetSDK.getInstance().NET_DVR_SetDVRConfig(iUserID,
                HCNetSDK.NET_DVR_SET_COMPRESSCFG_V30, iStartChannel, struCompress)) {
            Log.e(TAG, "NET_DVR_SET_COMPRESSCFG_V30 failed with error code:" + HCNetSDK.getInstance().NET_DVR_GetLastError());
        } else {
            Log.i(TAG, "NET_DVR_SET_COMPRESSCFG_V30 succ");
        }
    }

    /**
     * @return exception instance
     * @fn getExceptiongCbf
     * @author zhuzhenlei
     * @brief process exception
     */
    private ExceptionCallBack getExceptiongCbf() {
        ExceptionCallBack oExceptionCbf = new ExceptionCallBack() {
            public void fExceptionCallBack(int iType, int iUserID, int iHandle) {
                System.out.println("recv exception, type:" + iType);
            }
        };
        return oExceptionCbf;
    }

    /**
     * release net SDK resource
     */
    public void Cleanup() {
        HCNetSDK.getInstance().NET_DVR_Cleanup();
    }

    public void get_access_token(String appKey, String appSecret) {
        Log.e(TAG, "get_access_token in");

        if (appKey == "" || appSecret == "") {
            Log.e(TAG, "appKey or appSecret is null");
            return;
        }

        try {
            String url = "https://open.ezvizlife.com/api/lapp/token/get";
            URL getDeviceUrl = new URL(url);
            /*Set Http Request Header*/
            HttpURLConnection connection = (HttpURLConnection) getDeviceUrl.openConnection();

            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestProperty("Host", "isgpopen.ezvizlife.com");
            connection.setDoInput(true);
            connection.setDoOutput(true);

            PrintWriter PostParam = new PrintWriter(connection.getOutputStream());
            String sendParam = "appKey=" + appKey + "&appSecret=" + appSecret;
            PostParam.print(sendParam);
            PostParam.flush();

            BufferedReader inBuf = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            JSONObject RetValue = new JSONObject(new String(inBuf.readLine().getBytes(), "utf-8"));
            int RetCode = Integer.parseInt(RetValue.getString("code"));
            if (RetCode != 200) {
                Log.e(TAG, "Get DDNS Info fail! Err code: " + RetCode);
                return;
            } else {
                JSONObject DetailInfo = RetValue.getJSONObject("data");
                accessToken = DetailInfo.getString("accessToken");
                Log.e(TAG, "accessToken: " + accessToken);
                areaDomain = DetailInfo.getString("areaDomain");
                Log.e(TAG, "areaDomain: " + areaDomain);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getKey() {
        return appkey;
    }

    public String getSecret() {
        return appSecret;
    }

    public void get_device_ip() {
        String deviceSerial = "711563208" /*m_oIPAddr.getText().toString()*/;  //IP text instead of deviceSerial
        if (deviceSerial == null) {
            Log.e(TAG, "deviceSerial is null ");
            return;
        }

        try {
            String url = areaDomain + "/api/lapp/ddns/get";
            URL getDeviceUrl = new URL(url);
            /*Set Http Request Header*/
            HttpURLConnection connection = (HttpURLConnection) getDeviceUrl.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestProperty("Host", "isgpopen.ezvizlife.com");
            connection.setDoInput(true);
            connection.setDoOutput(true);

            PrintWriter PostParam = new PrintWriter(connection.getOutputStream());
            String sendParam = "accessToken=" + accessToken + "&deviceSerial=" + deviceSerial;
//            String sendParam = "accessToken=" + accessToken + "&domain=" + areaDomain;  
            System.out.println(sendParam);
            PostParam.print(sendParam);
            PostParam.flush();

            BufferedReader inBuf = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            JSONObject RetValue = new JSONObject(new String(inBuf.readLine().getBytes(), "utf-8"));
            Log.e(TAG, "RetValue = " + RetValue);
            return;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public JSONObject get_ddns_Info(String appkey, String appSecret) {
        try {
            if (m_retUrl != "") {
                Log.e(TAG, "m_retUrl != null ");
                accessToken = m_retUrl.substring(m_retUrl.indexOf("access_token") + 13, m_retUrl.indexOf("access_token") + 77);
                Log.e(TAG, "accessToken: " + accessToken);
                areaDomain = m_retUrl.substring(m_retUrl.indexOf("areaDomain") + 11);
                Log.e(TAG, "areaDomain: " + areaDomain);
            } else {
                new Thread(new Runnable() { //inner class - new thread to get device list
                    @Override
                    public void run() {
                        get_access_token(getKey(), getSecret());
                        get_device_ip();
                    }
                }).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void get_ddns_Info_HC() {
        Intent intent = new Intent(HkActivity.this, HkTestActivity.class);    //skip to HC page
        startActivityForResult(intent, REQUEST_CODE);   //get ddns info by using HC
    }

}