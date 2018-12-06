package com.thdz.fast.bean;

import java.io.Serializable;

/**
 * 推送返回的bean -- 韩冲用_分隔
 */

public class PushBaseBean implements Serializable {

    private String alarmCode; // 600002",
    /**
     * 01 光纤告警 <br>
     * 02 雷达告警 <br>
     * 03 人脸识别 <br>
     */
    private String alarmType; // 01",
    private String alarmMsg; // 光纤震动告警",
    private String areaName; // 告警区域",
    private String alarmLevel; // 2",
    private String alarmTime; // 2018-11-21 14:02:38"


    public PushBaseBean() {
    }

    public PushBaseBean(String alarmCode, String alarmType, String alarmMsg, String areaName, String alarmLevel, String alarmTime) {
        this.alarmCode = alarmCode;
        this.alarmType = alarmType;
        this.alarmMsg = alarmMsg;
        this.areaName = areaName;
        this.alarmLevel = alarmLevel;
        this.alarmTime = alarmTime;
    }

    public String getAlarmCode() {
        return alarmCode;
    }

    public void setAlarmCode(String alarmCode) {
        this.alarmCode = alarmCode;
    }

    public String getAlarmType() {
        return alarmType;
    }

    public void setAlarmType(String alarmType) {
        this.alarmType = alarmType;
    }

    public String getAlarmMsg() {
        return alarmMsg;
    }

    public void setAlarmMsg(String alarmMsg) {
        this.alarmMsg = alarmMsg;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public String getAlarmLevel() {
        return alarmLevel;
    }

    public void setAlarmLevel(String alarmLevel) {
        this.alarmLevel = alarmLevel;
    }

    public String getAlarmTime() {
        return alarmTime;
    }

    public void setAlarmTime(String alarmTime) {
        this.alarmTime = alarmTime;
    }

    @Override
    public String toString() {
        return "PushBaseBean{" +
                "alarmCode='" + alarmCode + '\'' +
                ", alarmType='" + alarmType + '\'' +
                ", alarmMsg='" + alarmMsg + '\'' +
                ", areaName='" + areaName + '\'' +
                ", alarmLevel='" + alarmLevel + '\'' +
                ", alarmTime='" + alarmTime + '\'' +
                '}';
    }
}
