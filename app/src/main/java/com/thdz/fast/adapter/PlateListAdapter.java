package com.thdz.fast.adapter;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.thdz.fast.R;
import com.thdz.fast.bean.PlateBean;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 主页 车牌识别列表适配器
 */
public class PlateListAdapter extends BaseAdapter {

    private List<PlateBean> dataList = null;
    private Context context;
    private SparseArray<String> colorArray;

    public PlateListAdapter(Context context) {
        this.context = context;
        colorArray = new SparseArray<String>();
    }

    public PlateListAdapter(Context context, List<PlateBean> dataList) {
        this.dataList = dataList;
        this.context = context;
        colorArray = new SparseArray<String>();
    }

    public List<PlateBean> getDataList() {
        return dataList;
    }

    public void setDataList(List<PlateBean> dataList) {
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
            convertView = LayoutInflater.from(context).inflate(R.layout.item_plate, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        try {
            PlateBean bean = dataList.get(position);

            String ccc = bean.getColor();
            colorArray.put(position, ccc);

            int licenseLen = bean.getLicenseLen();
            if (licenseLen <= 0) {
                holder.tv_plate_no.setText("     未识别     ");
            } else {
                holder.tv_plate_no.setText(bean.getLicense()); // .substring(1)
            }

            holder.item_area_name.setText(bean.getAreaName());
            holder.item_plate_type.setText(bean.getPlateType());
            holder.item_plate_time.setText(bean.getAlarmTime());

            switch (Integer.parseInt(colorArray.get(position))) {
                case 1: // 黄色
                    holder.tv_plate_no.setBackgroundResource(R.drawable.bg_plate_yellow);
                    holder.tv_plate_no.setTextColor(context.getResources().getColor(R.color.black));
                    break;
                case 2: // 白色
                    holder.tv_plate_no.setBackgroundResource(R.drawable.bg_plate_white);
                    holder.tv_plate_no.setTextColor(context.getResources().getColor(R.color.black));
                    break;
                case 3: // 黑色
                    holder.tv_plate_no.setBackgroundResource(R.drawable.bg_plate_black);
                    holder.tv_plate_no.setTextColor(context.getResources().getColor(R.color.white));
                    break;
                case 4: // 绿色
                    holder.tv_plate_no.setBackgroundResource(R.drawable.bg_plate_green);
                    holder.tv_plate_no.setTextColor(context.getResources().getColor(R.color.black));
                    break;
                case 5: // 民航黑色车牌
                    holder.tv_plate_no.setBackgroundResource(R.drawable.bg_plate_black);
                    holder.tv_plate_no.setTextColor(context.getResources().getColor(R.color.white));
                    break;
                case 6: // 其他，用蓝色代替
                case 0: // 蓝色
                default: // 蓝色
                    holder.tv_plate_no.setBackgroundResource(R.drawable.bg_plate_blue);
                    holder.tv_plate_no.setTextColor(context.getResources().getColor(R.color.white));
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return convertView;
    }


    static class ViewHolder {
        @BindView(R.id.tv_plate_no)
        TextView tv_plate_no;       // 车牌号

        @BindView(R.id.item_area_name)
        TextView item_area_name;     // 区域

        @BindView(R.id.item_plate_type)
        TextView item_plate_type;       // 车型

        @BindView(R.id.item_plate_time)
        TextView item_plate_time;      // 告警时间

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }

    }

}
