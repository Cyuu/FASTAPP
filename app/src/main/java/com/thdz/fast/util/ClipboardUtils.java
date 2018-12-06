package com.thdz.fast.util;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;

import com.thdz.fast.app.MyApplication;


/**
 * Created by ruiqin.shen
 * 类说明： 剪贴板工具类：
 * 1 复制文字
 * 2 获取剪贴板的文本
 */

public class ClipboardUtils {
    public static void copyText(CharSequence text) {
        ClipboardManager clipboardManager = (ClipboardManager) MyApplication.getInstance().getSystemService(Context.CLIPBOARD_SERVICE);
        clipboardManager.setPrimaryClip(ClipData.newPlainText("text", text));
    }

    /**
     * 获取剪贴板的文本
     *
     * @return 剪贴板的文本
     */
    public static CharSequence getText() {
        ClipboardManager clipboard = (ClipboardManager)  MyApplication.getInstance().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = clipboard.getPrimaryClip();
        if (clip != null && clip.getItemCount() > 0) {
            return clip.getItemAt(0).coerceToText( MyApplication.getInstance());
        }
        return null;
    }
}
