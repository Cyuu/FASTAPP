package com.thdz.fast.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.thdz.fast.R;
import com.thdz.fast.bean.IpcBean;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 主页 网络摄像机 列表适配器
 */
public class IPCListAdapter extends BaseAdapter {

    private List<IpcBean> dataList = null;
    private Context context;

    public IPCListAdapter(Context context) {
        this.context = context;
    }

    public IPCListAdapter(Context context, List<IpcBean> dataList) {
        this.dataList = dataList;
        this.context = context;
    }

    public List<IpcBean> getDataList() {
        return dataList;
    }

    public void setDataList(List<IpcBean> dataList) {
        this.dataList = dataList;
    }

    @Override
    public int getCount() {
        return null == dataList ? 0 : dataList.size();
    }

    @Override
    public Object getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_ipc, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final IpcBean bean = dataList.get(position);

        holder.item_ipc_name.setText(bean.getIpcName());

        return convertView;
    }


    static class ViewHolder {

        @BindView(R.id.item_ipc_name)
        TextView item_ipc_name;     // 名称

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }

    }

}
