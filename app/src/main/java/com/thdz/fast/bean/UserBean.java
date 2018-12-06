package com.thdz.fast.bean;

import java.io.Serializable;

/**
 * 登录成功后返回的用户信息类
 */
public class UserBean implements Serializable {

    private String userNo; //   uid
    private String userName; // 用户名，用于页面展示
    private String roleId; //   角色id
    private String roleName; // 角色名称
    private String clientId; // 推送的cid


    public UserBean() {

    }

    public UserBean(String userNo, String userName, String roleId, String roleName, String clientId) {
        this.userNo = userNo;
        this.userName = userName;
        this.roleId = roleId;
        this.roleName = roleName;
        this.clientId = clientId;
    }

    public String getUserNo() {
        return userNo;
    }

    public void setUserNo(String userNo) {
        this.userNo = userNo;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    @Override
    public String toString() {
        return "UserBean{" +
                "userNo='" + userNo + '\'' +
                ", userName='" + userName + '\'' +
                ", roleId='" + roleId + '\'' +
                ", roleName='" + roleName + '\'' +
                ", clientId='" + clientId + '\'' +
                '}';
    }
}
