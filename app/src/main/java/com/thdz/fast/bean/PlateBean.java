package com.thdz.fast.bean;

import java.io.Serializable;


/**
 * 车牌识别的列表bean
 */
public class PlateBean implements Serializable {

    private int id; // 1698,
    // 识别时间
    private String alarmTime; // "2018-11-24 14:45:14",
    // 车牌类型
    private String plateType; // "标准民用车与军车车牌",
    /**
     * 0- 蓝色车牌
     * 1- 黄色车牌
     * 2- 白色车牌
     * 3- 黑色车牌
     * 4- 绿色车牌
     * 5- 民航黑色车牌
     * 6- 其他 -- 蓝色
     */
    private String color; // "0",
    // 描述
    private String colorDesc; // "蓝色车牌",
    // 车牌长度， 用于：识别标识 >0 表示识别出来了，<=0 表示未识别出来
    private int licenseLen; //  7,  >0 表示识别出来了，<=0 表示未识别出来
    // 车牌号
    private String license; //  "蓝冀F55V36",
    // url
    private String fileUrl; //  "http://133.124.42.18/Vehicle/2018-11-24/蓝冀F55V36-20181124144514000_Num1.jpg",
    // 设备id
    private String deviceId; //  "100002",
    // 区域
    private String areaName; //  "基地大门",
    // 出入标识
    private String inout; //  0 进， 1 出

    public PlateBean() {

    }

    public PlateBean(int id, String alarmTime, String plateType, String color, String colorDesc, int licenseLen, String license, String fileUrl, String deviceId, String areaName, String inout) {
        this.id = id;
        this.alarmTime = alarmTime;
        this.plateType = plateType;
        this.color = color;
        this.colorDesc = colorDesc;
        this.licenseLen = licenseLen;
        this.license = license;
        this.fileUrl = fileUrl;
        this.deviceId = deviceId;
        this.areaName = areaName;
        this.inout = inout;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAlarmTime() {
        return alarmTime;
    }

    public void setAlarmTime(String alarmTime) {
        this.alarmTime = alarmTime;
    }

    public String getPlateType() {
        return plateType;
    }

    public void setPlateType(String plateType) {
        this.plateType = plateType;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getColorDesc() {
        return colorDesc;
    }

    public void setColorDesc(String colorDesc) {
        this.colorDesc = colorDesc;
    }

    public int getLicenseLen() {
        return licenseLen;
    }

    public void setLicenseLen(int licenseLen) {
        this.licenseLen = licenseLen;
    }

    public String getLicense() {
        return license;
    }

    public void setLicense(String license) {
        this.license = license;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
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

    public String getInout() {
        return inout;
    }

    public void setInout(String inout) {
        this.inout = inout;
    }

    @Override
    public String toString() {
        return "PlateBean{" +
                "id=" + id +
                ", alarmTime='" + alarmTime + '\'' +
                ", plateType='" + plateType + '\'' +
                ", color='" + color + '\'' +
                ", colorDesc='" + colorDesc + '\'' +
                ", licenseLen=" + licenseLen +
                ", license='" + license + '\'' +
                ", fileUrl='" + fileUrl + '\'' +
                ", deviceId='" + deviceId + '\'' +
                ", areaName='" + areaName + '\'' +
                ", inout='" + inout + '\'' +
                '}';
    }
}
