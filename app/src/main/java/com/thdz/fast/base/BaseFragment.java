package com.thdz.fast.base;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.thdz.fast.R;
import com.thdz.fast.app.MyApplication;
import com.thdz.fast.util.BusUtil;
import com.thdz.fast.util.Finals;
import com.thdz.fast.util.SpUtil;
import com.thdz.fast.util.TsUtils;
import com.thdz.fast.util.VUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import butterknife.ButterKnife;
import okhttp3.MediaType;

/**
 * Fragment框架<br/>
 */
public abstract class BaseFragment extends Fragment implements OnClickListener {

//    public final int CODE_RELOAD = 0; // 重新加载
//    public final int CODE_LOAD_NEW = 1; // 加载新内容
//    public final int CODE_LOAD_MORE = 2;// 加载更多

    public final String failTip = "连接服务器失败";
    public final String errorTip = "未获取到信息";

    public String TAG = this.getClass().getSimpleName();

    public MyApplication application;
    public Context context;
    public ProgressDialog progressDialog = null;

    /**
     * 有两个用途：<br/>
     * 1 标志位，标志已经初始化完成<br/>
     * ~~~ 放在onCreate()中，<br/>
     * ----------------------<br/>
     * 2 是否已加载过 -- 如果已经加载过，则不再重复加载<br/>
     * ~~~ 放在RequestData()，loadOK后，表示已加载过数据了<br/>
     */
    public boolean isPrepared;

    /**
     * 需要输入密码校验的对话框，里面的密码输入框
     */
    public EditText pEdit;

    /**
     * fragment的父view
     */
    public View contentView;

    private InputMethodManager imm;
//    /**
//     * cancelAll使用tag，需要设置的话， 要在
//     */
//    public String request_tag;

//    public Unbinder unbinder;

//    public final int TIME_OUT_MS = 10000;

    // public ApiService api;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = this.getActivity();
        application = (MyApplication) getActivity().getApplication();
//        api = MyApplication.getApi();
        if (null == contentView) {
            contentView = inflateView(inflater, container, savedInstanceState);
        }
        ButterKnife.bind(this, contentView);
        initView(savedInstanceState, contentView, inflater);
        isPrepared = true;
        return contentView;
    }

    /**
     * 获取root view的layout id
     */
    public abstract View inflateView(LayoutInflater inflater, ViewGroup arg1, Bundle arg2);

    /**
     * 初始化Fragment
     */
    public abstract void initView(Bundle savedInstanceState, View view, LayoutInflater inflater);

    /**
     * 点击事件响应
     */
    @Override
    public abstract void onClick(View v);

    /**
     * get方式
     *
     * @param url      url
     * @param callback 回调方法
     */
    public void doRequestGet(String url, StringCallback callback) {
        Log.i(TAG, "请求地址：" + url);
        OkHttpUtils
                .get()
                .url(url)
                .build()
                .execute(callback);
    }


    /**
     * post方式
     */
    public void doRequestPost(String url, String jsonStr, StringCallback callback) {
        Log.i(TAG, "请求地址：" + url);
        OkHttpUtils
                .postString()
                .url(url)
                .content(jsonStr)
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .build()
                .execute(callback);
    }


//    /**
//     * 发送控制命令的请求<br/>
//     * 1 code， no的确认<br/>
//     * 2 没有StringCallback
//     */
//    public void RequestControlCMD(String stnNo, String unitNo, int code) {
//        RequestControlCMD(stnNo, unitNo, code, true);
//    }
//
//
//    /**
//     * 发送控制命令的请求，没有Toast<br/>
//     */
//    public void RequestControlCMD(String stnNo, String unitNo, int code, boolean needToast) {
//        String dataStr = DataUtils.CreateCommandParams(context, stnNo, unitNo, code);
//        String url = DataUtils.createReqUrl4Get(
//                application.getIP(), application.getUid(), Finals.CMD_SystemControlCmd, dataStr);
//        doRequestGet(url, null);
//        if (needToast) {
////            toast("命令已发送"); // 已由EventBus分发到各页面处理
//        }
//    }


    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        getActivity().overridePendingTransition(R.anim.push_right_in,
                R.anim.push_left_out);
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);
        getActivity().overridePendingTransition(R.anim.push_right_in,
                R.anim.push_left_out);
    }


    @Override
    public void onPause() {
        super.onPause();
        hideInputMethod();
    }


    @Override
    public void onStop() {
        BusUtil.unreg(this);
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        BusUtil.unreg(this);
        if (progressDialog != null) {
            progressDialog.dismiss();
        }

    }

    public void showProgressDialog() {
        showProgressDialog("正在加载...");
    }

    public void showProgressDialog(String txt) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(context);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setCancelable(true);
            progressDialog.setMessage(txt);
        }

        progressDialog.show();
    }

    public void showProgressDialog(String txt, DialogInterface.OnClickListener sureListener) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(context);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setCancelable(true);
            progressDialog.setProgressDrawable(getResources().getDrawable(R.mipmap.ic_checked));
            progressDialog.setMessage(txt);

            progressDialog.setButton(DialogInterface.BUTTON_POSITIVE, "确定", sureListener);
            progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    hideProgressDialog();
                }
            });
        }

        progressDialog.show();
    }

    public void hideProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }


    /**
     * 打开输入密码验证对话框
     */
    public void showPwdDialog(DialogInterface.OnClickListener sureListener) {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(context);
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View pwdView = layoutInflater.inflate(R.layout.dialog_pwd, null);
        TextView title_tv = (TextView) pwdView.findViewById(R.id.dialog_title_tv); // title
        title_tv.setText("请输入密码进行校验");
        pEdit = (EditText) pwdView.findViewById(R.id.pwd_check_ed);
        if (Finals.IS_TEST) {
            pEdit.setText(SpUtil.getData(context, Finals.SP_PWD));
            pEdit.setSelection(pEdit.getText().toString().length());
        }
        mBuilder.setView(pwdView);
        mBuilder.setCancelable(false);
        // mBuilder.setTitle("请输入密码进行校验");
        mBuilder.setPositiveButton("确认", sureListener);

        mBuilder.setNeutralButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            hideInputMethod();// 关闭输入法
                            toast("已取消");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                });
            }
        });
        AlertDialog pwdDialog = mBuilder.create();
        pwdDialog.show();

    }


    /**
     * 隐藏输入框
     */
    public void hideInputMethod() {
        if (imm == null) {
            imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        }
        if (contentView != null) {
            IBinder mBinder = contentView.getWindowToken();
            if (mBinder != null) {
                imm.hideSoftInputFromWindow(mBinder, InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

    }

    /**
     * 设置title for Fragment
     */
    public void setTitle(View view, String title) {
        TextView titletv = (TextView) view.findViewById(R.id.title_tv);
        titletv.setText(title);
    }


    public void setToolbarBackEnable(Toolbar toolbar) {
        if (toolbar != null) {
            ((BaseActivity) getActivity()).setSupportActionBar(toolbar);
            ((BaseActivity) getActivity()).getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
            ((BaseActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 显示左上角返回按钮
            // 左上角返回键
            toolbar.setNavigationOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((BaseActivity) getActivity()).onBackPressed();
                }
            });
        }
    }

    public void setToolbarBackGone(Toolbar toolbar) {
        if (toolbar != null) {
            ((BaseActivity) getActivity()).setSupportActionBar(toolbar);
            ((BaseActivity) getActivity()).getSupportActionBar().setHomeButtonEnabled(false); //设置返回键可用
            ((BaseActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false); // 显示左上角返回按钮
        }
    }


    public void showSureDialog(String tip, DialogInterface.OnClickListener sureListener) {
        VUtils.showSureDialog(context, tip, sureListener);
    }


    public void toast(String info) {
        TsUtils.toast(context, info);
    }


    public void goActivity(Class<?> cls, Bundle bundle) {
        Intent intent = new Intent(context, cls);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
    }

}
