package com.thdz.fast.bean;

import java.io.Serializable;

/**
 * 人脸识别的列表bean
 */
public class FaceBean implements Serializable {

    private int id; // id
    private String deviceId; // 设备id
    private String areaName; // 地址
    private String channlNo; // 通道号
    private String alarmTime; // 时间

    private int matchOk; // 0 不匹配  1 匹配
    private String sex; // 性别
    private String glasses; // 是否佩戴眼镜
    private String ageGroup; // 年龄段
    private String idCardNo; // 身份证号

    private String name; // 名称
    private String fileUrl1; // 大图
    private String fileUrl2; // 小图 ， 细节图
    private String fileUrl3; // 证件图
    private int alarmStatus; // 告警状态


    public FaceBean() {

    }

    public FaceBean(int id, String deviceId, String areaName, String channlNo, String alarmTime, int matchOk, String sex, String glasses, String ageGroup, String idCardNo, String name, String fileUrl1, String fileUrl2, String fileUrl3, int alarmStatus) {
        this.id = id;
        this.deviceId = deviceId;
        this.areaName = areaName;
        this.channlNo = channlNo;
        this.alarmTime = alarmTime;
        this.matchOk = matchOk;
        this.sex = sex;
        this.glasses = glasses;
        this.ageGroup = ageGroup;
        this.idCardNo = idCardNo;
        this.name = name;
        this.fileUrl1 = fileUrl1;
        this.fileUrl2 = fileUrl2;
        this.fileUrl3 = fileUrl3;
        this.alarmStatus = alarmStatus;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public String getChannlNo() {
        return channlNo;
    }

    public void setChannlNo(String channlNo) {
        this.channlNo = channlNo;
    }

    public String getAlarmTime() {
        return alarmTime;
    }

    public void setAlarmTime(String alarmTime) {
        this.alarmTime = alarmTime;
    }

    public int getMatchOk() {
        return matchOk;
    }

    public void setMatchOk(int matchOk) {
        this.matchOk = matchOk;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getGlasses() {
        return glasses;
    }

    public void setGlasses(String glasses) {
        this.glasses = glasses;
    }

    public String getAgeGroup() {
        return ageGroup;
    }

    public void setAgeGroup(String ageGroup) {
        this.ageGroup = ageGroup;
    }

    public String getIdCardNo() {
        return idCardNo;
    }

    public void setIdCardNo(String idCardNo) {
        this.idCardNo = idCardNo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFileUrl1() {
        return fileUrl1;
    }

    public void setFileUrl1(String fileUrl1) {
        this.fileUrl1 = fileUrl1;
    }

    public String getFileUrl2() {
        return fileUrl2;
    }

    public void setFileUrl2(String fileUrl2) {
        this.fileUrl2 = fileUrl2;
    }

    public String getFileUrl3() {
        return fileUrl3;
    }

    public void setFileUrl3(String fileUrl3) {
        this.fileUrl3 = fileUrl3;
    }

    public int getAlarmStatus() {
        return alarmStatus;
    }

    public void setAlarmStatus(int alarmStatus) {
        this.alarmStatus = alarmStatus;
    }

    @Override
    public String toString() {
        return "FaceBean{" +
                "id=" + id +
                ", deviceId='" + deviceId + '\'' +
                ", areaName='" + areaName + '\'' +
                ", channlNo='" + channlNo + '\'' +
                ", alarmTime='" + alarmTime + '\'' +
                ", matchOk=" + matchOk +
                ", sex='" + sex + '\'' +
                ", glasses='" + glasses + '\'' +
                ", ageGroup='" + ageGroup + '\'' +
                ", idCardNo='" + idCardNo + '\'' +
                ", name='" + name + '\'' +
                ", fileUrl1='" + fileUrl1 + '\'' +
                ", fileUrl2='" + fileUrl2 + '\'' +
                ", fileUrl3='" + fileUrl3 + '\'' +
                '}';
    }
}
