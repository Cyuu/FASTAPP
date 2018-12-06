package com.thdz.fast.bean;

import java.io.Serializable;

/**
 * desc:
 * author:  Administrator
 * date:    2018/11/16  11:09
 */
public class ReturnBaseBean<T> implements Serializable {

    private int code;
    private String msg;
    private T data;

    public ReturnBaseBean() {

    }

    public ReturnBaseBean(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "ReturnBaseBean{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }
}
