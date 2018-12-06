package com.thdz.fast.base;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.thdz.fast.R;
import com.thdz.fast.app.MyApplication;
import com.thdz.fast.util.TsUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import butterknife.ButterKnife;
import okhttp3.MediaType;

/**
 * 懒加载Fragment框架<br/>
 */
public abstract class BaseLazyFragment extends Fragment implements OnClickListener {

    public String TAG = this.getClass().getSimpleName();

    public MyApplication application;
    public Context context;
    public ProgressDialog progressDialog = null;
    private InputMethodManager imm;

    private boolean isVisible = false;  // 当前Fragment是否可见
    private boolean isInitView = false; // 是否与View建立起映射关系

    private View convertView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = this.getActivity();
        application = MyApplication.getInstance();
        if (null == convertView)
            convertView = inflateView(inflater, container, savedInstanceState);

        ButterKnife.bind(this, convertView);
        return convertView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (!isInitView) {
            isInitView = true;
            initView(savedInstanceState, view);
            if (!isVisible && isInitView) {
                isVisible = true;
                lazyLoad();
            }
        }
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && !isVisible && isInitView) {
            isVisible = true;
            lazyLoad();
        }
    }

    /**
     * 获取root view的layout id
     */
    public abstract View inflateView(LayoutInflater inflater, ViewGroup arg1, Bundle arg2);

    /**
     * 隐藏输入框
     */
    public void hideInputMethod() {
        if (imm == null) {
            imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        }
        if (convertView != null) {
            IBinder mBinder = convertView.getWindowToken();
            if (mBinder != null) {
                imm.hideSoftInputFromWindow(mBinder, InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

//        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
//        imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_NOT_ALWAYS);

    }


    /**
     * 让布局中的view与fragment中的变量建立起映射
     */
    protected abstract void initView(Bundle savedInstanceState, View view);

    /**
     * 加载要显示的数据
     */
    protected abstract void lazyLoad();

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
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

    public void hideProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
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

    public void showDataView() {
        try {
            convertView.findViewById(R.id.lv_new).setVisibility(View.VISIBLE);
            convertView.findViewById(R.id.tv_empty).setVisibility(View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showEmptyView() {
        try {
            convertView.findViewById(R.id.lv_new).setVisibility(View.GONE);
            convertView.findViewById(R.id.tv_empty).setVisibility(View.VISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
