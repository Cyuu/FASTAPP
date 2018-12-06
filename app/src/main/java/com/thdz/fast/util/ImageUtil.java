package com.thdz.fast.util;

import android.content.Context;
import android.net.Uri;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.File;

/**
 * 图片加载框架
 * 作用：加载图片统一入口
 */

public class ImageUtil {

    /**
     * 加载网络图片
     */
    public static void load(Context context, String imageUrl, ImageView imageView) {
        Picasso.with(context).load(imageUrl).into(imageView);
    }

    /**
     * 加载Android Resources 图片
     */
    public static void load(Context context, File file, ImageView imageView) {
        Picasso.with(context).load(file).into(imageView);
    }

    /**
     * 加载图片
     */
    public static void load(Context context, Uri uri, ImageView imageView) {
        Picasso.with(context).load(uri).into(imageView);
    }

    /**
     * 加载URI地址 图片
     */
    public static void load(Context context, int resId, ImageView imageView) {
        Picasso.with(context).load(resId).into(imageView);
    }

}
