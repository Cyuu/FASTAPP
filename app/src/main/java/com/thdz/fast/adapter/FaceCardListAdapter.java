package com.thdz.fast.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.thdz.fast.R;
import com.thdz.fast.bean.FaceBean;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 主页 人脸列表
 */
public class FaceCardListAdapter extends BaseAdapter {

    private List<FaceBean> dataList = null;
    private Context context;
    private SparseBooleanArray matchArray = new SparseBooleanArray();

    // private int www;

    public FaceCardListAdapter(Context context) {
        this.context = context;
        // www = MyApplication.getInstance().getScreenWidth();
    }

    public FaceCardListAdapter(Context context, List<FaceBean> dataList) {
        this.dataList = dataList;
        this.context = context;
        // www = MyApplication.getInstance().getScreenWidth();
    }

    public List<FaceBean> getDataList() {
        return dataList;
    }

    public void setDataList(List<FaceBean> dataList) {
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
            convertView = LayoutInflater.from(context).inflate(R.layout.item_card, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        FaceBean bean = dataList.get(position);

        String name = bean.getName();
        if (TextUtils.isEmpty(name)) {
            name = "未知";
        }
        holder.item_face_name.setText(name);
        holder.item_face_sex.setText(bean.getSex());
        holder.item_face_addr.setText(bean.getAreaName());
        holder.item_certno.setText(bean.getIdCardNo());
        holder.item_face_ages.setText(bean.getAgeGroup());
        holder.item_face_glasses.setText(bean.getGlasses());
        holder.item_face_time.setText(bean.getAlarmTime());

        int match = bean.getMatchOk();
        matchArray.append(position, match == 1);
        if (matchArray.get(position)) {
            holder.layout_bg.setBackgroundResource(R.drawable.bg_card_blue);
        } else {
            holder.layout_bg.setBackgroundResource(R.drawable.bg_corner_red_light);
        }

//        holder.iv_card.setLayoutParams(new RelativeLayout.LayoutParams(
//                www / 3, (www / 3  * 3 / 2)));
//        Picasso.with(context).load(bean.getImgUrl()).into(holder.iv_card);

        return convertView;
    }


    static class ViewHolder {

        @BindView(R.id.layout_bg)
        LinearLayout layout_bg;

        @BindView(R.id.item_face_name)
        TextView item_face_name;       // 名称

        @BindView(R.id.item_face_sex)
        TextView item_face_sex;       // 性别

        @BindView(R.id.item_face_glasses)
        TextView item_face_glasses;    // 是否戴眼镜

        @BindView(R.id.item_face_ages)
        TextView item_face_ages;       // 年龄段

        @BindView(R.id.item_face_addr)
        TextView item_face_addr;       // 地址

        @BindView(R.id.item_face_time)
        TextView item_face_time;       // 识别时间

        @BindView(R.id.item_certno)
        TextView item_certno;     // 身份证号

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }

    }

}
