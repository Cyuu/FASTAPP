package com.thdz.fast.bean;

import java.io.Serializable;

/**
 * desc:    主页 业务模块列表的公共类
 * author:  Administrator
 * date:    2018/10/30  16:40
 */
public class ModuleCommonBean implements Serializable {

    private int id; // id
    private String name; // 名称
    private String addr; // 地址
    private int alarmCount; // 告警数量
    private boolean hasAlarm; // 是否有告警
    private boolean canMore; // 可以查看更多

    public ModuleCommonBean() {

    }

    public ModuleCommonBean(int id, String name, String addr, int alarmCount, boolean hasAlarm, boolean canMore) {
        this.id = id;
        this.name = name;
        this.addr = addr;
        this.alarmCount = alarmCount;
        this.hasAlarm = hasAlarm;
        this.canMore = canMore;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    public int getAlarmCount() {
        return alarmCount;
    }

    public void setAlarmCount(int alarmCount) {
        this.alarmCount = alarmCount;
    }

    public boolean isHasAlarm() {
        return hasAlarm;
    }

    public void setHasAlarm(boolean hasAlarm) {
        this.hasAlarm = hasAlarm;
    }

    public boolean isCanMore() {
        return canMore;
    }

    public void setCanMore(boolean canMore) {
        this.canMore = canMore;
    }
}
