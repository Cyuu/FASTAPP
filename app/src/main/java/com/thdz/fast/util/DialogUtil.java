package com.thdz.fast.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;

import com.thdz.fast.R;

public class DialogUtil {


//    /**
//     * 创建弹出对话框
//     */
//    public static void showChooseDialog(Context context, List<CommonIdValueBean> list, DialogInterface.OnClickListener listener, boolean isBottom) {
//        try {
//            AlertDialog.Builder builder = new AlertDialog.Builder(context);
//            // builder.setIcon(R.mipmap.ic_logo_green_round);
//            // builder.setTitle(title);
//            DialogAdapter adapter = new DialogAdapter(context, list);
//            builder.setAdapter(adapter, listener);
//
//            Dialog dd  = builder.create();
//            if (isBottom) {
//                Window window = dd.getWindow();
//                window.getDecorView().setPadding(0, VUtil.dp2px(10), 0, VUtil.dp2px(10));
//                WindowManager.LayoutParams lp = window.getAttributes();
//                lp.width = WindowManager.LayoutParams.MATCH_PARENT;
//                lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
//                window.setAttributes(lp);
//                window.setGravity(Gravity.BOTTOM);
//                window.setWindowAnimations(R.style.dialog_anim);
//            }
//
//            dd.show();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }


    /**
     * 原生对话框， 只做展示，不做数据点选等交互
     */
    public static void showOnly(Context context, String title, String msg) {
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(msg)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create();
        dialog.show();
    }

    /**
     * 原生对话框， 只做展示，不做数据点选等交互
     */
    public static void showOnlyMsg(Context context, String msg, DialogInterface.OnClickListener listener) {
        AlertDialog dialog = new AlertDialog.Builder(context, R.style.Dialog_common)
                .setMessage(msg)
                .setPositiveButton("确定", listener)
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        dialog.show();
    }


    public static <E extends View> E getView(View view, int id) {
        try {
            return (E) view.findViewById(id);
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
        return null;
    }

}
