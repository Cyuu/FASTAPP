package com.thdz.fast.ui.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.TypeReference;
import com.thdz.fast.R;
import com.thdz.fast.adapter.FiberListAdapter;
import com.thdz.fast.base.BaseLazyFragment;
import com.thdz.fast.bean.FiberBean;
import com.thdz.fast.bean.ReturnDataBean;
import com.thdz.fast.ui.MainActivity;
import com.thdz.fast.ui.detail.FiberActivity;
import com.thdz.fast.util.DataUtils;
import com.thdz.fast.util.Finals;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import butterknife.BindView;
import okhttp3.Call;


/**
 * 周界防范（振动光纤）
 */
public class FiberFragment extends BaseLazyFragment {

    @BindView(R.id.lv_new)
    ListView lv_new;

    @BindView(R.id.tv_empty)
    TextView tv_empty;

    @BindView(R.id.tv_pageup)
    TextView tv_pageup;

    @BindView(R.id.tv_pagehome)
    ImageView tv_pagehome;

    @BindView(R.id.tv_curpage)
    TextView tv_curpage;

    @BindView(R.id.tv_pagedown)
    TextView tv_pagedown;


    private static final int pageSize = 10;  // 每页的数量

    private int pageIndex = 1; // 接口返回的当前页
    private int rowCount = 0;  // 总页数

    private FiberListAdapter fiberAdapter;

    private List<FiberBean> fiberList;

    public static FiberFragment newInstance() {
        Bundle args = new Bundle();
        FiberFragment fragment = new FiberFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public void refresh() {
        requestData(1);
        try {
            ((MainActivity)getActivity()).hideMsgDot(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public View inflateView(LayoutInflater inflater, ViewGroup arg1, Bundle arg2) {
        return inflater.inflate(R.layout.fragment_module_common, arg1, false);
    }

    @Override
    public void initView(Bundle savedInstanceState, View view) {

        tv_pageup.setOnClickListener(this);
        tv_pagehome.setOnClickListener(this);
        tv_pagedown.setOnClickListener(this);

        fiberAdapter = new FiberListAdapter(context);
        lv_new.setAdapter(fiberAdapter);

        lv_new.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("bean", fiberList.get(position));
                goActivity(FiberActivity.class, bundle);
            }
        });
    }

    @Override
    public void lazyLoad() {
        requestData(pageIndex);
    }

    private void requestData(int page) {
        JSONObject obj = new JSONObject();
        try {
            obj.put("PageSize", pageSize);
            obj.put("PageIndex", page);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        showProgressDialog();
        doRequestPost(DataUtils.getApiUrl(Finals.fiber_list), obj.toString(), new StringCallback() {
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
                Log.i(TAG, "光纤告警列表请求 返回参数是：" + value);
                try {
                    if (DataUtils.isReturnOK(value)) {

                        // 泛型方式
                        ReturnDataBean<FiberBean> dataBean = com.alibaba.fastjson.JSON.parseObject(
                                DataUtils.getReturnData(value),
                                new TypeReference<ReturnDataBean<FiberBean>>() {
                                });
                        rowCount = dataBean.getRowCount();
                        pageIndex = dataBean.getPageIndex();
                        fiberList = dataBean.getPageData();

                        tv_curpage.setText(pageIndex + "");

//                        // 层级 解析方式
//                        String dataStr = DataUtils.getReturnData(value);
//                        rowCount = DataUtils.getReturnRowCount(dataStr);
//                        pageIndex = DataUtils.getReturnPageIndex(dataStr);
//                        fiberList = JSONArray.parseArray(
//                                DataUtils.getReturnList(dataStr),
//                                FiberBean.class);

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
            case R.id.tv_pageup:
                if (pageIndex <= 1) {
                    pageIndex = 1;
                    toast("已经是首页");
                    return;
                }
                requestData(pageIndex - 1);
                break;
            case R.id.tv_pagehome:
                refresh();
                break;
            case R.id.tv_pagedown:
                if (pageIndex >= rowCount) {
                    pageIndex = rowCount;
                    toast("已经是末页");
                    return;
                }
                requestData(pageIndex + 1);
                break;
            default:
                break;
        }
    }

    private void showView() {
        if (fiberList == null || fiberList.isEmpty()) {
            toast("尚无数据");
            showEmptyView();
            return;
        }
        showDataView();
        fiberAdapter.setDataList(fiberList);
        fiberAdapter.notifyDataSetChanged();
        lv_new.postDelayed(new Runnable() {
            @Override
            public void run() {
                lv_new.setSelection(0);
            }

        },0);
    }

}