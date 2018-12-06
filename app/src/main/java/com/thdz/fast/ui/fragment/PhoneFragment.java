package com.thdz.fast.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.thdz.fast.R;
import com.thdz.fast.adapter.ModuleCommonListAdapter;
import com.thdz.fast.base.BaseLazyFragment;
import com.thdz.fast.bean.ModuleCommonBean;
import com.thdz.fast.ui.MainActivity;
import com.thdz.fast.ui.detail.PhoneActivity;

import java.util.List;

import butterknife.BindView;


/**
 * 微信号扫描
 */
public class PhoneFragment extends BaseLazyFragment {

    @BindView(R.id.lv_new)
    ListView lv_new;

    private ModuleCommonListAdapter phoneAdapter;

    private List<ModuleCommonBean> phoneList;

    public static PhoneFragment newInstance() {
        Bundle args = new Bundle();
        PhoneFragment fragment = new PhoneFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public void refresh() {
        lazyLoad();
        try {
            ((MainActivity)getActivity()).hideMsgDot(5);
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

        phoneAdapter = new ModuleCommonListAdapter(context);
        lv_new.setAdapter(phoneAdapter);

        lv_new.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("bean", phoneList.get(position));
                goActivity(PhoneActivity.class, bundle);
            }
        });
    }

    @Override
    public void lazyLoad() {
        showView();
    }

    @Override
    public void onClick(View v) {

    }

    private void showView() {
        showEmptyView();

//        phoneList = TestUtil.getPhoneList();
//        phoneAdapter.setDataList(phoneList);
//        phoneAdapter.notifyDataSetChanged();

    }
}