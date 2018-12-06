package com.thdz.fast.util;

import org.greenrobot.eventbus.EventBus;

/**
 * desc:    事件总线
 * author:  Administrator
 * date:    2018/6/7  8:27
 */
public class BusUtil {


    /**
     * 注册
     */
    public static void reg(Object obj) {
        if (!EventBus.getDefault().isRegistered(obj)) {
            EventBus.getDefault().register(obj);
        }
    }

    /**
     * 注销
     */
    public static void unreg(Object obj) {
        if (EventBus.getDefault().isRegistered(obj)) {
            EventBus.getDefault().unregister(obj);
        }
    }

    /**
     * 发送事件
     */
    public static void post(Object obj) {
        EventBus.getDefault().post(obj);
    }

    /**
     * 发送粘性事件
     */
    public static void postSticky(Object obj) {
        EventBus.getDefault().postSticky(obj);
    }

}
