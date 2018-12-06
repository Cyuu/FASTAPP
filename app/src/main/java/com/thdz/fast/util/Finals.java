package com.thdz.fast.util;

import android.os.Environment;

/**
 * 常量
 * -------------
 * url示例： http://192.163.5.119/WebService.asmx/login?username=1&pwd=2
 */
public class Finals {

    public static final boolean IS_TEST;    // 是否测试


    static { // TODO 打包前修改 测试地址和正式地址
        IS_TEST = false; // false  true

    }

    // 定义接口

    public static final String login = "user/login/"; // "commons/loginAction"; // 登录
    public static final String logout = "user/loginout"; // 退出登录 todo unprogram

    public static final String ipc_list = "camera/all"; // 摄像机列表
    public static final String ipc_getbyalarm = "camera/getbyalarm"; // 根据 光纤或者雷达告警查找摄像机
    public static final String fiber_list = "alarm/fiber"; // 获取光纤告警列表

    public static final String radar_list = "alarm/radar"; // 获取雷达告警列表
    public static final String face_list = "face/list"; // 获取雷达告警列表
    public static final String plate_list = "cars/list"; // 车牌列表


    // 文件缓存至磁盘路径
    public static final String FilePath = Environment.getExternalStorageDirectory() + "/fast/";
    // 视频文件缓存至磁盘路径
    public static final String VideoPath = FilePath + "video/";
    // 图片文件缓存至磁盘路径
    public static final String ImagePath = FilePath + "image/";
    // 图片文件缓存至磁盘路径
    public static final String LogPath = FilePath + "log/";

    // ----sp----
    public static final String SP_NAME = "SP_FAST";
    public static final String SP_USERNAME = "SP_username";    // 用户名称
    public static final String SP_PWD = "SP_PWD";
    public static final String SP_UID = "SP_uid";              // 用户id
    public static final String SP_HOST = "SP_HOST";            // 服务器地址
    public static final String SP_AUTOLOGIN = "SP_AUTOLOGIN";

    public static final String SP_CLIENTID = "SP_CLIENTID";     // 推送唯一码


    public static final String Default_User = "1"; // 默认登录用户名
    public static final String Default_Pwd = "123456"; // 默认密码


}

