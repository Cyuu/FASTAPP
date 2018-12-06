package com.thdz.fast.service;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSONObject;
import com.igexin.sdk.GTIntentService;
import com.igexin.sdk.PushConsts;
import com.igexin.sdk.PushManager;
import com.igexin.sdk.message.FeedbackCmdMessage;
import com.igexin.sdk.message.GTCmdMessage;
import com.igexin.sdk.message.GTNotificationMessage;
import com.igexin.sdk.message.GTTransmitMessage;
import com.igexin.sdk.message.SetTagCmdMessage;
import com.thdz.fast.app.MyApplication;
import com.thdz.fast.bean.PushBaseBean;
import com.thdz.fast.event.ClientIdEvent;
import com.thdz.fast.ui.PushListenActivity;
import com.thdz.fast.util.BusUtil;
import com.thdz.fast.util.Finals;
import com.thdz.fast.util.NotifyUtil;
import com.thdz.fast.util.SpUtil;

import org.greenrobot.eventbus.EventBus;

/**
 * todo 处理所有推送消息，不在之前的PushBackReceiver里处理
 * 继承 GTIntentService          接收来自个推的消息, 所有消息在线程中回调, 如果注册了该服务, 则务必要在 AndroidManifest中声明, 否则无法接受消息<br>
 * onReceiveMessageData         处理透传消息<br>
 * onReceiveClientId            接收 cid <br>
 * onReceiveOnlineState cid     离线上线通知 <br>
 * onReceiveCommandResult       各种事件处理回执 <br>
 * -----------------
 * onReceiveMessageData + handlePush 处理推送消息
 */
public class GetuiIntentService extends GTIntentService {

    private static final String TAG = "GetuiIntentService";

    public GetuiIntentService() {

    }

    @Override
    public void onReceiveServicePid(Context context, int pid) {
        Log.d(TAG, "onReceiveServicePid -> " + pid);
    }

    /**
     * 处理透传消息  -- 获取到推送消息
     */
    @Override
    public void onReceiveMessageData(Context context, GTTransmitMessage msg) {
        String appid = msg.getAppid();
        String taskid = msg.getTaskId();
        String messageid = msg.getMessageId();
        byte[] payload = msg.getPayload();
        String pkg = msg.getPkgName();
        String cid = msg.getClientId();
        // TsUtils.toast(context, "推送的消息是：" + new String(payload));

        // 第三方回执调用接口，actionid范围为90000-90999，可根据业务场景执行
        boolean result = PushManager.getInstance().sendFeedbackMessage(context, taskid, messageid, 90001);
        Log.d(TAG, "call sendFeedbackMessage = " + (result ? "success" : "failed"));

        Log.d(TAG, "onReceiveMessageData -> " + "appid = " + appid + "\ntaskid = " + taskid +
                "\nmessageid = " + messageid + "\npkg = " + pkg + "\ncid = " + cid);

        if (payload == null) {
            Log.e(TAG, "推送data = null");
        } else {
            PushBaseBean pBean = JSONObject.parseObject(new String(payload), PushBaseBean.class);
            Log.d(TAG, "获取到推送数据为：" + pBean.toString());
            handlePush(context, pBean); // 处理推送
        }
    }

    /**
     * 接收 cid
     */
    @Override
    public void onReceiveClientId(Context context, String clientid) {
        Log.i(TAG, "onReceiveClientId -> " + "clientid = " + clientid);
        // 登陆页面保存
        SpUtil.save(context, Finals.SP_CLIENTID, clientid);
        ClientIdEvent clientEvent = new ClientIdEvent();
        clientEvent.setClientId(clientid);
        EventBus.getDefault().post(clientEvent);
    }


    /**
     * cid 离线上线通知
     */
    @Override
    public void onReceiveOnlineState(Context context, boolean online) {
        Log.d(TAG, "onReceiveOnlineState -> " + (online ? "online" : "offline"));
    }

    /**
     * 各种事件处理回执
     */
    @Override
    public void onReceiveCommandResult(Context context, GTCmdMessage cmdMessage) {
        Log.d(TAG, "onReceiveCommandResult -> " + cmdMessage);

        int action = cmdMessage.getAction();

        if (action == PushConsts.SET_TAG_RESULT) {
            setTagResult((SetTagCmdMessage) cmdMessage);
        } else if ((action == PushConsts.THIRDPART_FEEDBACK)) {
            feedbackResult((FeedbackCmdMessage) cmdMessage);
        }
    }

    @Override
    public void onNotificationMessageArrived(Context context, GTNotificationMessage message) {
        Log.d(TAG, "onNotificationMessageArrived -> " + "appid = " + message.getAppid() + "\ntaskid = " + message.getTaskId() + "\nmessageid = "
                + message.getMessageId() + "\npkg = " + message.getPkgName() + "\ncid = " + message.getClientId() + "\ntitle = "
                + message.getTitle() + "\ncontent = " + message.getContent());
    }

    @Override
    public void onNotificationMessageClicked(Context context, GTNotificationMessage message) {
        Log.d(TAG, "onNotificationMessageClicked -> " + "appid = " + message.getAppid() + "\ntaskid = " + message.getTaskId() + "\nmessageid = "
                + message.getMessageId() + "\npkg = " + message.getPkgName() + "\ncid = " + message.getClientId() + "\ntitle = "
                + message.getTitle() + "\ncontent = " + message.getContent());
    }

    private void setTagResult(SetTagCmdMessage setTagCmdMsg) {
        String sn = setTagCmdMsg.getSn();
        String code = setTagCmdMsg.getCode();

        String text = "设置标签失败, 未知异常";
        switch (Integer.valueOf(code)) {
            case PushConsts.SETTAG_SUCCESS:
                text = "设置标签成功";
                break;

            case PushConsts.SETTAG_ERROR_COUNT:
                text = "设置标签失败, tag数量过大, 最大不能超过200个";
                break;

            case PushConsts.SETTAG_ERROR_FREQUENCY:
                text = "设置标签失败, 频率过快, 两次间隔应大于1s且一天只能成功调用一次";
                break;

            case PushConsts.SETTAG_ERROR_REPEAT:
                text = "设置标签失败, 标签重复";
                break;

            case PushConsts.SETTAG_ERROR_UNBIND:
                text = "设置标签失败, 服务未初始化成功";
                break;

            case PushConsts.SETTAG_ERROR_EXCEPTION:
                text = "设置标签失败, 未知异常";
                break;

            case PushConsts.SETTAG_ERROR_NULL:
                text = "设置标签失败, tag 为空";
                break;

            case PushConsts.SETTAG_NOTONLINE:
                text = "还未登陆成功";
                break;

            case PushConsts.SETTAG_IN_BLACKLIST:
                text = "该应用已经在黑名单中,请联系售后支持!";
                break;

            case PushConsts.SETTAG_NUM_EXCEED:
                text = "已存 tag 超过限制";
                break;

            default:
                break;
        }

        Log.d(TAG, "settag result sn = " + sn + ", code = " + code + ", text = " + text);
    }

    private void feedbackResult(FeedbackCmdMessage feedbackCmdMsg) {
        String appid = feedbackCmdMsg.getAppid();
        String taskid = feedbackCmdMsg.getTaskId();
        String actionid = feedbackCmdMsg.getActionId();
        String result = feedbackCmdMsg.getResult();
        long timestamp = feedbackCmdMsg.getTimeStamp();
        String cid = feedbackCmdMsg.getClientId();

        Log.d(TAG, "onReceiveCommandResult -> " + "appid = " + appid + "\ntaskid = " +
                taskid + "\nactionid = " + actionid + "\nresult = " + result
                + "\ncid = " + cid + "\ntimestamp = " + timestamp);
    }


    /**
     * 推送请求处理
     * ----------------
     * 1 目前只处理告警推， 2 使用EventBus分发
     * 处理方式： </br>
     * 通知知栏提示，<br
     * 1 点击调用HomeActivity，地图移动到告警站点<br
     * 2 打开告警详情页。<br>
     * 3 如果有告警，在主页tab上增加红点，表示有该类型告警
     */
    private void handlePush(Context context, PushBaseBean bean) {
        if (bean == null || TextUtils.isEmpty(bean.getAlarmCode())) {
            return;
        }

        MyApplication.getInstance().pushBean = bean;
        BusUtil.postSticky(bean);

        // 2 弹出通知栏消息
        // MyApplication.notyId++; // 有多个消息时覆盖
        Intent intent = new Intent(context, PushListenActivity.class);
        String name = bean.getAreaName();
        if (TextUtils.isEmpty(name) || name.equals("null")) {
            name = "";
        }
        NotifyUtil.showNotification(
                context,
                name + " 发生 " + bean.getAlarmMsg(),
                bean.getAlarmTime(),
                intent,
                MyApplication.notyId);

    }

}
