package com.thdz.fast.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.thdz.fast.R;
import com.thdz.fast.bean.RadarBean;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 主页 雷达告警列表适配器
 */
public class RadarListAdapter extends BaseAdapter {

    private List<RadarBean> dataList = null;
    private Context context;

    public RadarListAdapter(Context context) {
        this.context = context;
    }

    public RadarListAdapter(Context context, List<RadarBean> dataList) {
        this.dataList = dataList;
        this.context = context;
    }

    public List<RadarBean> getDataList() {
        return dataList;
    }

    public void setDataList(List<RadarBean> dataList) {
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
            convertView = LayoutInflater.from(context).inflate(R.layout.item_radar, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        RadarBean bean = dataList.get(position);

        holder.item_alarm_msg.setText(bean.getAlarmMessage());
        holder.item_device_name.setText(bean.getDeviceName());
        holder.item_area_name.setText(bean.getAreaName());
        holder.item_alarm_level.setText(bean.getAlarmLevelName());
        holder.item_alarm_time.setText(bean.getAlarmTime());

        if (bean.getAlarmLevel() == 1) {
            holder.item_alarm_level.setBackgroundResource(R.drawable.bg_red_left_corner);
        } else if (bean.getAlarmLevel() == 2) {
            holder.item_alarm_level.setBackgroundResource(R.drawable.bg_orange_left_corner);
        } else if (bean.getAlarmLevel() == 3) {
            holder.item_alarm_level.setBackgroundResource(R.drawable.bg_yellow_left_corner);
        } else {
            holder.item_alarm_level.setBackgroundResource(R.drawable.bg_black_left_corner);
        }

        int status = bean.getAlarmStatus();
        String value = "告警";
        switch (status) {
            case 0:
                value = "新告警";
                break;
            case 1:
                value = "已恢复";
                break;
            case 2:
                value = "已取消";
                break;
            case 256:
                value = "已确认已取消";
                break;
            case 257:
                value = "已确认";
                break;
            case 258:
                value = "已确认已恢复";
                break;
        }
        holder.item_alarm_status.setText(value);

        return convertView;
    }


    static class ViewHolder {
        @BindView(R.id.item_alarm_msg)
        TextView item_alarm_msg;       // 名称

        @BindView(R.id.item_device_name)
        TextView item_device_name;     // 设备名称

        @BindView(R.id.item_area_name)
        TextView item_area_name;       // 区域

        @BindView(R.id.item_alarm_level)
        TextView item_alarm_level;     // 告警级别

        @BindView(R.id.item_alarm_time)
        TextView item_alarm_time;      // 告警时间

        @BindView(R.id.item_alarm_status)
        TextView item_alarm_status;     // 状态

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }

    }

}
