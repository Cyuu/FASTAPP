package com.thdz.fast.ui.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.thdz.fast.R;
import com.thdz.fast.adapter.IPCListAdapter;
import com.thdz.fast.base.BaseLazyFragment;
import com.thdz.fast.bean.IpcBean;
import com.thdz.fast.ui.detail.IPCActivity;
import com.thdz.fast.util.DataUtils;
import com.thdz.fast.util.Finals;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.List;

import butterknife.BindView;
import okhttp3.Call;

/**
 * 视频监控, 都是NVR, 不同通道，安装位置不同。 视频浏览。
 */
public class IPCFragment extends BaseLazyFragment {

    @BindView(R.id.lv_new)
    ListView lv_new;

    @BindView(R.id.iv_refresh)
    ImageView iv_refresh;

    private List<IpcBean> ipcList;

    private IPCListAdapter ipcListAdapter;

    public static IPCFragment newInstance() {
        Bundle args = new Bundle();
        IPCFragment fragment = new IPCFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public void refresh() {
        requestData();
    }

    @Override
    public View inflateView(LayoutInflater inflater, ViewGroup arg1, Bundle arg2) {
        return inflater.inflate(R.layout.fragment_ipc, arg1, false);
    }

    @Override
    public void initView(Bundle savedInstanceState, View view) {

        iv_refresh.setOnClickListener(this);

        ipcListAdapter = new IPCListAdapter(context);
        lv_new.setAdapter(ipcListAdapter);

        lv_new.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("bean", ipcList.get(position));
                goActivity(IPCActivity.class, bundle);
            }
        });
    }


    @Override
    public void lazyLoad() {
        requestData();
    }

    private void requestData() {
        showProgressDialog();
        doRequestPost(DataUtils.getApiUrl(Finals.ipc_list), "", new StringCallback() {
            @Override
            public void onError(Call call, final Exception e, int id) {
                hideProgressDialog();
                e.printStackTrace();
                // toast("请求错误");
                Log.e(TAG, "请求错误");
                showEmptyView();
            }

            @Override
            public void onResponse(String value, int id) {
                hideProgressDialog();
                Log.i(TAG, "IPC列表请求 返回参数是：" + value);
                try {
                    if (DataUtils.isReturnOK(value)) {
                        ipcList = com.alibaba.fastjson.JSONArray.parseArray(DataUtils.getReturnData(value), IpcBean.class);
                        showView();
                    } else {
                        toast(DataUtils.getReturnMsg(value));
                        showEmptyView();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    // toast("数据解析异常");
                    Log.e(TAG, "数据解析异常");
                    showEmptyView();
                }
            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_refresh:
                requestData();
                break;
            default:
                break;
        }
    }


    private void showView() {
        if (ipcList == null || ipcList.isEmpty()) {
            toast("尚无数据");
            showEmptyView();
            return;
        }
        showDataView();
        ipcListAdapter.setDataList(ipcList);
        ipcListAdapter.notifyDataSetChanged();
        lv_new.postDelayed(new Runnable() {
            @Override
            public void run() {
                lv_new.setSelection(0);
            }

        },0);
    }
}