package com.thdz.fast.view;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import com.ldoublem.loadingviewlib.view.LVBlock;
import com.thdz.fast.R;

/**
 * 自定义loadingview, from 'com.ldoublem.loadingviewlib.view.LVBlock'
 */
public class BlockProgressDialog extends ProgressDialog {

    private Context mContext;

    LVBlock mLVBlock;

    public BlockProgressDialog(Context context) {
        super(context, R.style.Custom_Progress);
        mContext = context;
    }

    public BlockProgressDialog(Context context, int theme) {
        super(context, theme);
        mContext = context;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_progress_block, null);

        mLVBlock = (LVBlock) view.findViewById(R.id.lv_block);
//        mLVBlock.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//            }
//        });

        mLVBlock.setViewColor(Color.rgb(245,209,22));
        mLVBlock.setShadowColor(mContext.getResources().getColor(R.color.black_light));

        setContentView(view);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

    }

    @Override
    public void show() {
        super.show();
        if (null != mLVBlock) {
            mLVBlock.startAnim();
        }
    }


    @Override
    public void dismiss() {
        super.dismiss();
        if (null != mLVBlock) {
            mLVBlock.stopAnim();
        }
    }
}
