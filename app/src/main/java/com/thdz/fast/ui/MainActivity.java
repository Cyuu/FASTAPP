package com.thdz.fast.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.flyco.tablayout.SlidingTabLayout;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.thdz.fast.R;
import com.thdz.fast.app.MyApplication;
import com.thdz.fast.base.BaseActivity;
import com.thdz.fast.base.BaseLazyFragment;
import com.thdz.fast.bean.PushBaseBean;
import com.thdz.fast.ui.fragment.FaceFragment;
import com.thdz.fast.ui.fragment.FiberFragment;
import com.thdz.fast.ui.fragment.IPCFragment;
import com.thdz.fast.ui.fragment.PhoneFragment;
import com.thdz.fast.ui.fragment.PlateFragment;
import com.thdz.fast.ui.fragment.RadarFragment;
import com.thdz.fast.util.BusUtil;
import com.thdz.fast.util.DataUtils;
import com.thdz.fast.util.DialogUtil;
import com.thdz.fast.util.Finals;
import com.thdz.fast.util.NotifyUtil;
import com.thdz.fast.util.SpUtil;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import okhttp3.Call;

/**
 * 主页
 */
public class MainActivity extends BaseActivity {

    @BindView(R.id.viewpager)
    ViewPager viewpager;

    @BindView(R.id.top_tab)
    SlidingTabLayout top_tab;

    // 是否有ipc告警， 理论上不存在该告警，
    public boolean alarmFlag0 = false;
    // 是否有光纤告警
    public boolean alarmFlag1 = false;
    // 是否有雷达告警
    public boolean alarmFlag2 = false;
    // 是否有脸谱告警
    public boolean alarmFlag3 = false;

    // 是否有i车牌告警
    public boolean alarmFlag4 = false;
    // 是否有微信号告警
    public boolean alarmFlag5 = false;


    // 业务模式，tab头
    private final String[] mTitles = {
            "视频",   // "综合安防视频监控",      // 视频,     //
            "光纤",   // "振动光纤周界防范",      // 光纤,     //
            "雷达",   // "重点区域激光雷达防护",  // 雷达,     //
            "脸谱",   // "全区域人员管控",       // 人脸,     //
            "车牌",   // "全区域车辆管控",       // 车牌,     //
            "微信号"   // "微电波信号监测"        // 微信号,   //
    };

//    // 场景模式，tab头
//    private final String[] sceneTitles = {
//            "实验楼外道路",
//            "南垭口",
//            "观测基地",
//            "水源地",
//            "台址入口",
//            "装置垭口",
//            "石坡寨",
//            "牛角",
//            "观景台",
//            "光明顶",
//    };

    private int currentIndex = 0;

    private ArrayList<BaseLazyFragment> mFragments = new ArrayList<>();


    @Override
    public void setContentView() {
        setContentView(R.layout.activity_main);
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        MyApplication.hasCreateMain = true;
        BusUtil.reg(context);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setToolbarUnable(toolbar);

        initFragments();

        findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                testAlarm();
            }
        });

        initViewpager();

        /*
        // 初始化顶部tab
        top_tab.setMsgMargin(3, 0, 10);
        // 模拟设置新消息数
        top_tab.showMsg(2, 5);
        // 设置tab间距
        top_tab.setTabPadding(5f);
        top_tab.setTabSpaceEqual(true);
        top_tab.setTabWidth(application.getScreenWidth() / 6f);
        */

        // tab字体大小
        top_tab.setTextsize(13f);

        top_tab.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                currentIndex = position;
                if (position == 1 && alarmFlag1) {
                    ((FiberFragment) mFragments.get(position)).refresh();
                    alarmFlag1 = false;
                }
                if (position == 2 && alarmFlag2) {
                    ((RadarFragment) mFragments.get(position)).refresh();
                    alarmFlag2 = false;
                }
                if (position == 3 && alarmFlag3) {
                    ((FaceFragment) mFragments.get(position)).refresh();
                    alarmFlag3 = false;
                }
                if (position == 4 && alarmFlag4) {
                    ((PlateFragment) mFragments.get(position)).refresh();
                    alarmFlag4 = false;
                }
                if (position == 5 && alarmFlag5) {
                    ((FiberFragment) mFragments.get(position)).refresh();
                    alarmFlag5 = false;
                }
            }

            @Override
            public void onTabReselect(int position) {
                switch (position) {
                    case 0:
                        ((IPCFragment) mFragments.get(position)).refresh();
                        break;
                    case 1:
                        ((FiberFragment) mFragments.get(position)).refresh();
                        break;
                    case 2:
                        ((RadarFragment) mFragments.get(position)).refresh();
                        break;
                    case 3:
                        ((FaceFragment) mFragments.get(position)).refresh();
                        break;
                    case 4:
                        ((PlateFragment) mFragments.get(position)).refresh();
                        break;
                    case 5:
                        ((FiberFragment) mFragments.get(position)).refresh();
                        break;
                }
            }
        });
    }

    private void initViewpager() {
        MyPagerAdapter mAdapter = new MyPagerAdapter(getSupportFragmentManager());
        viewpager.setAdapter(mAdapter);
        viewpager.setOffscreenPageLimit(1);
        top_tab.setViewPager(viewpager);

        // viewpager当前展示第几个item
        viewpager.setCurrentItem(0);

        viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                top_tab.hideMsg(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void initFragments() {
        mFragments.add(IPCFragment.newInstance());
        mFragments.add(FiberFragment.newInstance());
        mFragments.add(RadarFragment.newInstance());

        mFragments.add(FaceFragment.newInstance());
        mFragments.add(PlateFragment.newInstance());
        mFragments.add(PhoneFragment.newInstance());
    }

    @Override
    public void onClick(int resId) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_his) { // 查找历史告警
//            goActivity(null, null);
        } else if (item.getItemId() == R.id.action_mine) { // 退出登录
            showLogoutDialog();
        }
        return super.onOptionsItemSelected(item);
    }


    private class MyPagerAdapter extends FragmentPagerAdapter {
        private MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTitles[position];
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }
    }


    /**
     * 登出，弹出对话框
     */
    public void showLogoutDialog() {
        String msg = "确定要登出当前账号：‘" + application.getUserBean().getUserName() + "’ 吗？";
        DialogUtil.showOnlyMsg(context, msg, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                doLogout();
            }
        });

//        AlertDialog.Builder mBuilder = new AlertDialog.Builder(context);
//        LayoutInflater layoutInflater = LayoutInflater.from(context);
//        View pwdView = layoutInflater.inflate(R.layout.dialog_logout, null);
//
//        TextView tv_username = (TextView) pwdView.findViewById(R.id.tv_username); // username
//        TextView tv_version = (TextView) pwdView.findViewById(R.id.tv_version);   // version
//
//        tv_username.setText(application.getUserBean().getUserName());
//        tv_version.setText("v" + DataUtils.getVerName(context));
//
//        Button logout_btn = (Button) pwdView.findViewById(R.id.logout_btn);   // 登出
//        Button btn_dismiss = (Button) pwdView.findViewById(R.id.btn_dismiss); // 取消
//
//        mBuilder.setView(pwdView);
//        mBuilder.setCancelable(false);
//
//        AlertDialog pDialog = mBuilder.create();
//        pDialog.show();
//
//        logout_btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                pDialog.dismiss();
//                doLogout();
//            }
//        });
//
//        btn_dismiss.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                pDialog.dismiss();
//            }
//        });

    }

    private void doLogout() {
        JSONObject obj = new JSONObject();
        try {
            obj.put("LoginNo", application.getUid());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        showProgressDialog();
        doRequestPost(DataUtils.getApiUrl(Finals.logout), obj.toString(), new StringCallback() {
            @Override
            public void onError(Call call, final Exception e, int id) {
                hideProgressDialog();
                toast(failTip + "， 登出失败");
                e.printStackTrace();

            }

            @Override
            public void onResponse(String value, int id) {
                hideProgressDialog();
                Log.i(TAG, "登出 返回参数是：" + value);
                try {
                    if (DataUtils.isReturnOK(value)) {
                        saveLogOutInfo();

                        Intent intent = new Intent(context, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();

                        clearAlarmState();

                    } else {
                        toast(DataUtils.getReturnMsg(value));
                    }
                } catch (Exception e) {
                    toast("退出登录 失败");
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 登录成功后保存登录信息, 1 获取uid，2 跳转主页
     */
    private void saveLogOutInfo() {
        SpUtil.save(context, Finals.SP_USERNAME, "");
        SpUtil.save(context, Finals.SP_PWD, "");
        SpUtil.save(context, Finals.SP_CLIENTID, "");
        SpUtil.save(context, Finals.SP_AUTOLOGIN, "");

        application.setUid("");
        application.setUserBean(null);
    }


    /**
     * 有告警推送过来
     */
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onGetAlarmEvent(PushBaseBean event) {
        if (event == null || TextUtils.isEmpty(event.getAlarmType())) {
            return;
        }
        handlePush();
    }


    private void handlePush() {
        if (application.pushBean == null || TextUtils.isEmpty(application.pushBean.getAlarmType())) {
            return;
        }
        String type = application.pushBean.getAlarmType();
        if (type.equals("01")) {        // 光纤告警
            top_tab.showDot(1);
            alarmFlag1 = true;
        } else if (type.equals("02")) { // 雷达告警`
            top_tab.showDot(2);
            alarmFlag2 = true;
        } else if (type.equals("03")) { // 人脸
            top_tab.showDot(3);
            alarmFlag3 = true;
        } else if (type.equals("04")) { // 车牌
            top_tab.showDot(4);
            alarmFlag4 = true;
        } else if (type.equals("05")) { // 微信号
            top_tab.showDot(5);
            alarmFlag5 = true;
        }
    }

    public void hideMsgDot(int type) {
        switch (type) {
            case 0:

                break;
            case 1:
                top_tab.hideMsg(1);
                alarmFlag1 = false;
                break;
            case 2:
                top_tab.hideMsg(2);
                alarmFlag2 = false;
                break;
            case 3:
                top_tab.hideMsg(3);
                alarmFlag3 = false;
                break;
            case 4:
                top_tab.hideMsg(4);
                alarmFlag4 = false;
                break;
            case 5:
                top_tab.hideMsg(5);
                alarmFlag5 = false;
                break;
        }
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    @Override
    protected void onDestroy() {
        MyApplication.hasCreateMain = false;
        application.exit();
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) { // 返回桌面
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addCategory(Intent.CATEGORY_HOME);
            startActivity(intent);
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }


    /**
     * 测试告警推送
     */
    public void testAlarm() {
        PushBaseBean bean = new PushBaseBean("0321", "02",
                "公司门口10米处光纤告警", "公司大门", "一级告警", "2018-1-2 11:22:33");
        if (TextUtils.isEmpty(bean.getAlarmCode())) {
            return;
        }
        application.pushBean = bean;
        BusUtil.postSticky(bean);

        Intent intent = new Intent(context, PushListenActivity.class);
        String name = bean.getAreaName();
        if (TextUtils.isEmpty(name) || name.equals("null")) {
            name = "";
        }
        NotifyUtil.showNotification(
                context,
                name + " 发生 " + bean.getAlarmMsg(),
                bean.getAlarmTime(),
                intent,
                MyApplication.notyId);
    }

}
