package com.thdz.fast.bean;

import java.io.Serializable;

/**
 * desc:    雷达告警列表的item数据
 * author:  Administrator
 * date:    2018/11/16  11:06
 */
public class RadarBean implements Serializable {

    private String deviceId;   // "999999-Fos1-1",
    private String deviceName; // "基地大门",
    private String areaNo;     // "999999-Fos1-1",
    private String areaName;   // "基地大门",
    private String alarmCode;  // "600002",
    private int alarmLevel;    // 1,
    private String alarmLevelName;  // "一级告警",
    private String alarmMessage;   // "光纤告警测试",
    private int alarmStatus;     //
    private String alarmTime;   // "2018-11-14 15:52:00"

    public RadarBean() {

    }

    public RadarBean(
            String deviceId, String deviceName, String areaNo, String areaName,
            String alarmCode, int alarmLevel, String alarmLevelName,
            String alarmMessage, int alarmStatus, String alarmTime) {
        this.deviceId = deviceId;
        this.deviceName = deviceName;
        this.areaNo = areaNo;
        this.areaName = areaName;
        this.alarmCode = alarmCode;
        this.alarmLevel = alarmLevel;
        this.alarmLevelName = alarmLevelName;
        this.alarmMessage = alarmMessage;
        this.alarmStatus = alarmStatus;
        this.alarmTime = alarmTime;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getAreaNo() {
        return areaNo;
    }

    public void setAreaNo(String areaNo) {
        this.areaNo = areaNo;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public String getAlarmCode() {
        return alarmCode;
    }

    public void setAlarmCode(String alarmCode) {
        this.alarmCode = alarmCode;
    }

    public int getAlarmLevel() {
        return alarmLevel;
    }

    public void setAlarmLevel(int alarmLevel) {
        this.alarmLevel = alarmLevel;
    }

    public String getAlarmLevelName() {
        return alarmLevelName;
    }

    public void setAlarmLevelName(String alarmLevelName) {
        this.alarmLevelName = alarmLevelName;
    }

    public String getAlarmMessage() {
        return alarmMessage;
    }

    public void setAlarmMessage(String alarmMessage) {
        this.alarmMessage = alarmMessage;
    }

    public int getAlarmStatus() {
        return alarmStatus;
    }

    public void setAlarmStatus(int alarmStatus) {
        this.alarmStatus = alarmStatus;
    }

    public String getAlarmTime() {
        return alarmTime;
    }

    public void setAlarmTime(String alarmTime) {
        this.alarmTime = alarmTime;
    }

    @Override
    public String toString() {
        return "FiberBean{" +
                "deviceId='" + deviceId + '\'' +
                ", deviceName='" + deviceName + '\'' +
                ", areaNo='" + areaNo + '\'' +
                ", areaName='" + areaName + '\'' +
                ", alarmCode='" + alarmCode + '\'' +
                ", alarmLevel='" + alarmLevel + '\'' +
                ", alarmLevelName='" + alarmLevelName + '\'' +
                ", alarmMessage='" + alarmMessage + '\'' +
                ", alarmStatus='" + alarmStatus + '\'' +
                ", alarmTime='" + alarmTime + '\'' +
                '}';
    }
}
