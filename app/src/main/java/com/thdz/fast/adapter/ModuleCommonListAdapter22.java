package com.thdz.fast.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.thdz.fast.R;
import com.thdz.fast.bean.ModuleCommonBean;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 主页 业务列表适配器
 */
public class ModuleCommonListAdapter22 extends BaseAdapter {

    private List<ModuleCommonBean> dataList = null;
    private Context context;

    public ModuleCommonListAdapter22(Context context) {
        this.context = context;
    }

    public ModuleCommonListAdapter22(Context context, List<ModuleCommonBean> dataList) {
        this.dataList = dataList;
        this.context = context;
    }

    public List<ModuleCommonBean> getDataList() {
        return dataList;
    }

    public void setDataList(List<ModuleCommonBean> dataList) {
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
            convertView = LayoutInflater.from(context).inflate(R.layout.item_module_common, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final ModuleCommonBean bean = dataList.get(position);

        holder.item_module_name.setText(bean.getName());
        holder.item_module_addr.setText(bean.getAddr());

        boolean hasAlarm = bean.isHasAlarm();
        if (hasAlarm) {
            holder.layout_bg.setBackgroundResource(R.drawable.bg_item_red_stroke);
            holder.item_module_count.setText(bean.getAlarmCount() + "个");
            holder.item_module_count.setTextColor(context.getResources().getColor(R.color.red));

        } else {
            holder.layout_bg.setBackgroundResource(R.drawable.bg_item_white);
            holder.item_module_count.setText("无");
            holder.item_module_count.setTextColor(context.getResources().getColor(R.color.green_color));
        }

        return convertView;
    }


    static class ViewHolder {


        @BindView(R.id.layout_bg)
        View layout_bg;

        @BindView(R.id.item_module_name)
        TextView item_module_name;          // 名称

        @BindView(R.id.item_module_addr)
        TextView item_module_addr;     // 地址

        @BindView(R.id.item_module_count)
        TextView item_module_count;    // 告警数量


        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }

    }

}
