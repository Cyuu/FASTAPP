package com.thdz.fast.ui;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.igexin.sdk.PushManager;
import com.thdz.fast.R;
import com.thdz.fast.base.BaseActivity;
import com.thdz.fast.bean.UserBean;
import com.thdz.fast.event.ClientIdEvent;
import com.thdz.fast.service.FastPushService;
import com.thdz.fast.util.BusUtil;
import com.thdz.fast.util.DataUtils;
import com.thdz.fast.util.Finals;
import com.thdz.fast.util.PermissionUtils;
import com.thdz.fast.util.SpUtil;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import okhttp3.Call;

/**
 * 登录页面<br/>
 * ip地址是否用自动输入完成框<br/>
 * 如果不是自动登录，则不填充控件<br/>
 * 这里默认自动登录，而只有退出登录，才取消自动登录<br/>
 * 登录--->请求app使用的状态码-->成功后goMain(),失败后，goMain()<br/>
 * 如果上次已经登录，尚未注销，那将不再登录，直接：--->请求app使用的状态码 <br/>
 * -----------------<br.
 * 该项目里，登录 使用userNo，而不是用username，所以保存逻辑需要修改
 * <br/>
 */
public class LoginActivity extends BaseActivity {

    private static final int REQUEST_PERMISSION = 10;

    @BindView(R.id.tv_host)
    EditText tv_host;

    @BindView(R.id.tv_username)
    EditText tv_uid;

    @BindView(R.id.tv_pwd)
    EditText tv_pwd;

    @BindView(R.id.btn_login)
    Button btn_login;

    @BindView(R.id.tv_title)
    TextView tv_title;

    private String hostStr = "";
    private String uid = "";
    private String password = "";

    private UserBean userBean;
    private String clientid = "";

    private String msgFail = "登录失败";


    @Override
    public void setContentView() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        BusUtil.reg(this);

        btn_login.setOnClickListener(this);

        tv_title.setText(R.string.app_name_all);

        // 获取到ClientId之前，将登陆按钮置灰，不可用
        if (TextUtils.isEmpty(SpUtil.getData(context, Finals.SP_CLIENTID))) {
            btn_login.setBackgroundResource(R.color.gray);
            btn_login.setEnabled(false);
        }

        PermissionUtils.initGetuiService(context);

        showLoginEditView();

        /// ------- 测试内容  begin --------

        if (Finals.IS_TEST) {
            btn_login.setBackgroundResource(R.drawable.bg_green_selector);
            btn_login.setEnabled(true);

            findViewById(R.id.login_logo_iv).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goActivity(MainActivity.class, null);
                }
            });

//            tv_host.setText("192.168.2.123:8080"); // 服务器
//            tv_uid.setText("1000");
//            tv_pwd.setText("123456");
//
//            tv_host.setSelection(tv_host.getText().toString().length());
//            tv_uid.setSelection(tv_uid.getText().toString().length());
//            tv_pwd.setSelection(tv_pwd.getText().toString().length());
        }

        /// ------- 测试内容  end  --------
    }

    @Override
    public void onClick(int resId) {
        switch (resId) {
            case R.id.btn_login:
                doLogin();
                break;
            default:
                break;
        }
    }


    /**
     * 响应 获取个推ClientId的 请求
     */
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onGetClientIdEvent(ClientIdEvent event) {
        if (event == null || TextUtils.isEmpty(event.getClientId())) {
            return;
        }
        clientid =  event.getClientId();
        // 如果本地未保存，则调整登陆按钮颜色，并保存（第一次登录使用），否则，不做任何处理(以后使用)
        // toast("获取到ClientId：" + clientid);
        Log.i(TAG, "已获取到ClientId: " + clientid);

//        if (TextUtils.isEmpty(SpUtil.getData(context, Finals.SP_CLIENTID))) {
        // 将登陆框置为绿色，并可用
        btn_login.setBackgroundResource(R.drawable.bg_green_selector);
        btn_login.setEnabled(true);
    }

    /**
     * 登录请求
     */
    private void doLogin() {
        if (!doCheck()) {// 输入验证
            return;
        }

        JSONObject obj = new JSONObject();
        try {
            obj.put("LoginNo", uid);
            obj.put("LoginPwd", password);
            obj.put("ClientId", clientid);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // 根据输入的地址，生成app请求的接口地址
        DataUtils.CreateHostApiUrl(hostStr);

        showProgressDialog();
        doRequestPost(DataUtils.getApiUrl(Finals.login), obj.toString(), new StringCallback() {
            @Override
            public void onError(Call call, final Exception e, int id) {
                hideProgressDialog();
                toast(msgFail);
                e.printStackTrace();
            }

            @Override
            public void onResponse(String value, int id) {
                hideProgressDialog();
                Log.i(TAG, "登录请求 返回参数是：" + value);
                try {
                    if (DataUtils.isReturnOK(value)) {
                        userBean = com.alibaba.fastjson.JSONObject.parseObject(
                                DataUtils.getReturnData(value),
                                UserBean.class);

                        saveLoginInfo();
                        toast("欢迎" + userBean.getUserName() + ",登录成功");
                        gotoMain();
                    } else {
                        toast(DataUtils.getReturnMsg(value));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    toast(msgFail);
                }
            }
        });
    }


    /**
     * 设置检测规则，检查输入是否正确, true 输入正确 // false 输入错误
     */
    private boolean doCheck() {
        hostStr = tv_host.getText().toString().trim();
        if (TextUtils.isEmpty(hostStr)) {
            toast("请输入服务器地址");
            return false;
        }

        uid = tv_uid.getText().toString().trim();
        if (TextUtils.isEmpty(uid)) {
            toast("请输入用户名");
            return false;
        }

        password = tv_pwd.getText().toString().trim();
        if (TextUtils.isEmpty(password)) {
            toast("请输入密码");
            return false;
        }
        return true;
    }


    /**
     * 跳转至首页 --- 推送监听页
     */
    private void gotoMain() {
        Bundle bundle = new Bundle();
        bundle.putBoolean("fromLogin", true);
        // todo 推送监听 --> 主页
        goActivity(PushListenActivity.class, bundle); // MainActivity
        finish();
    }


    /**
     * 判断是否是退出登录而来,这里默认自动登录，而只有退出登录，才取消自动登录<br/>
     * 根据sp里的值，填充到控件中，如果没有则，不动<br/>
     */
    private void showLoginEditView() {
        if (SpUtil.isAutoLogin(context) && !TextUtils.isEmpty(SpUtil.getUid(context))) { // 自动登录
            tv_host.setText(SpUtil.getData(context, Finals.SP_HOST));
            tv_uid.setText(SpUtil.getData(context, Finals.SP_UID));
            tv_pwd.setText(SpUtil.getData(context, Finals.SP_PWD));

            tv_host.setSelection(tv_host.getText().toString().length());
            tv_uid.setSelection(tv_uid.getText().toString().length());
            tv_pwd.setSelection(tv_pwd.getText().toString().length());

            clientid = SpUtil.getData(context, Finals.SP_CLIENTID);

            doLogin();
        } else { // 正常登录或者退出登录
            hideProgressDialog();

            tv_host.setText(SpUtil.getData(context, Finals.SP_HOST));
            tv_uid.setText(SpUtil.getData(context, Finals.SP_UID));

            tv_host.setSelection(tv_host.getText().toString().length());
            tv_uid.setSelection(tv_uid.getText().toString().length());
        }

    }


    /**
     * 登录成功后保存登录信息
     */
    private void saveLoginInfo() {
        // 1 获取uid，2 跳转主页
        SpUtil.save(context, Finals.SP_HOST, hostStr);
        SpUtil.save(context, Finals.SP_UID, userBean.getUserNo());
        SpUtil.save(context, Finals.SP_USERNAME, userBean.getUserName());
        SpUtil.save(context, Finals.SP_PWD, password);
        // SpUtil.save(context, Finals.SP_CLIENTID, clientid); // service里已经保存了
        SpUtil.save(context, Finals.SP_AUTOLOGIN, "1");//自动登录

        application.setUid(userBean.getUserNo());
        application.setUserBean(userBean);

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_PERMISSION) {
            if ((grantResults.length == 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED)) {
                PushManager.getInstance().initialize(this.getApplicationContext(), FastPushService.class);
            } else {
                Log.e(TAG, "We highly recommend that you need to grant the special permissions before initializing the SDK, otherwise some "
                        + "functions will not work");
                PushManager.getInstance().initialize(this.getApplicationContext(), FastPushService.class);
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }

    }

}
