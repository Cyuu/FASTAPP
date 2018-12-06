package com.thdz.fast.util;

import com.thdz.fast.bean.IpcBean;
import com.thdz.fast.bean.ModuleCommonBean;

import java.util.ArrayList;
import java.util.List;

/**
 * 测试工具类
 */

public class TestUtil {


    public static List<ModuleCommonBean> getModuleCommonList() {
        List<ModuleCommonBean> list = new ArrayList<ModuleCommonBean>();
        for (int i = 0; i < 8; i++) {
            list.add(new ModuleCommonBean((i + 1), "东大门可视位置", "河北省保定市新市区源盛嘉禾1区", (i + 5), i % 2 == 0, true));
        }
        return list;
    }

    public static List<ModuleCommonBean> getFiberList() {
        List<ModuleCommonBean> list = new ArrayList<ModuleCommonBean>();
        list.add(new ModuleCommonBean((1), "实验楼外道路", "贵州省Fast长阳路1", (2), true, true));
        list.add(new ModuleCommonBean((2), "光明顶", "贵州省Fast长阳路3", (3), true, true));
        list.add(new ModuleCommonBean((3), "水源地", "贵州省Fast长阳路4", (6), true, true));
        return list;
    }

    public static List<ModuleCommonBean> getRadarList() {
        List<ModuleCommonBean> list = new ArrayList<ModuleCommonBean>();
        list.add(new ModuleCommonBean((1), "观景台", "贵州省Fast长阳路1", (2), true, true));
        list.add(new ModuleCommonBean((2), "光明顶", "贵州省Fast长阳路3", (3), true, true));
        list.add(new ModuleCommonBean((3), "水源地", "贵州省Fast长阳路4", (6), true, true));
        return list;
    }

    /**
     * 网络摄像机列表
     */
    public static List<IpcBean> getIpcList() {
        List<IpcBean> list = new ArrayList<IpcBean>();
        IpcBean bean = null;
        for (int i = 0; i < 5; i++) {
            bean = new IpcBean(
                    (i + 1) +" -" + i,
                    "32131",
                    "天河电子MR网络摄像机" + (i + 1) + "号",
                    "192.168.60.210",
                    "admin",
                    "hk123456",
                    2
            );
            list.add(bean);
        }
        return list;
    }

    public static List<ModuleCommonBean> getFaceList() {
        List<ModuleCommonBean> list = new ArrayList<ModuleCommonBean>();
        list.add(new ModuleCommonBean((1), "南垭口", "贵州省Fast中心位置100米内", (3), true, true));
        list.add(new ModuleCommonBean((2), "台址入口", "贵州省Fast中心位置100米内", (12), true, true));
        list.add(new ModuleCommonBean((3), "装置垭口", "贵州省Fast中心位置100米内", (44), true, true));
        list.add(new ModuleCommonBean((4), "牛角", "贵州省Fast中心位置100米内", (88), true, true));
        return list;
    }

    public static List<ModuleCommonBean> getPlateList() {
        List<ModuleCommonBean> list = new ArrayList<ModuleCommonBean>();
        list.add(new ModuleCommonBean((4), "牛角", "贵州省Fast中心位置100米内", (8), true, true));
        list.add(new ModuleCommonBean((3), "石坡寨", "贵州省Fast中心位置100米内", (44), true, true));
        list.add(new ModuleCommonBean((2), "台址入口", "贵州省Fast中心位置100米内", (12), true, true));
        return list;
    }

    public static List<ModuleCommonBean> getPhoneList() {
        List<ModuleCommonBean> list = new ArrayList<ModuleCommonBean>();
        list.add(new ModuleCommonBean((1), "观测基地", "贵州省Fast中心位置100米内", (3), true, true));
        list.add(new ModuleCommonBean((2), "水源地", "贵州省Fast中心位置100米内", (12), true, true));
        list.add(new ModuleCommonBean((3), "石坡寨", "贵州省Fast中心位置100米内", (44), true, true));
        list.add(new ModuleCommonBean((4), "观景台", "贵州省Fast中心位置100米内", (88), true, true));
        list.add(new ModuleCommonBean((5), "光明顶", "贵州省Fast中心位置100米内", (18), true, true));
        return list;
    }

    public static List<ModuleCommonBean> getModuleListByScene(int type) {
        List<ModuleCommonBean> list = new ArrayList<ModuleCommonBean>();
        list.add(new ModuleCommonBean(1, "雷达扫描", "河北省保定市新市区源盛嘉禾1区", 11, true, true));
        list.add(new ModuleCommonBean(2, "振动光纤", "河北省保定市新市区源盛嘉禾23区", 22, false, false));
        list.add(new ModuleCommonBean(3, "视频监控", "河北省保定市新市区源盛嘉禾5区", 33, false, false));
        list.add(new ModuleCommonBean(4, "人脸识别", "河北省保定市新市区源盛嘉禾8区", 44, true, true));
        list.add(new ModuleCommonBean(4, "车牌识别", "河北省保定市新市区源盛嘉禾9区", 55, false, false));
        list.add(new ModuleCommonBean(4, "手机信号", "河北省保定市新市区源盛嘉禾20区", 66, false, false));
        return list;
    }

}