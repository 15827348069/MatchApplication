package com.zbmf.newmatch.bean;

import java.io.Serializable;

/**
 * Created by xuhao
 * on 2017/11/23.
 */
public class User extends BaseBean implements Serializable{

    private double mpay;
    private String avatar;
    private String username;
    private String nickname;
    private String truename;
    private String role;//权限
    private String phone;
//    private String auth_token;
    private String user_id;
    private String is_bind;
    private String has_password;
    private int is_vip;
    private int is_super;
    private String vip_end_at;
    private String idcard;

    public double getMpay() {
        return mpay;
    }
    public void setMpay(double mpay) {
        this.mpay = mpay;
    }
    public String getUsername() {
        return this.username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getNickname() {
        return this.nickname;
    }
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
    public String getTruename() {
        return this.truename;
    }
    public void setTruename(String truename) {
        this.truename = truename;
    }
    public String getAvatar() {
        return this.avatar;
    }
    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
    public String getRole() {
        return this.role;
    }
    public void setRole(String role) {
        this.role = role;
    }
    public String getPhone() {
        return this.phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }
    public String getIdcard() {
        return this.idcard;
    }
    public void setIdcard(String idcard) {
        this.idcard = idcard;
    }

    public int getIs_vip() {
        return is_vip;
    }

    public void setIs_vip(int is_vip) {
        this.is_vip = is_vip;
    }

    public int getIs_super() {
        return is_super;
    }

    public void setIs_super(int is_super) {
        this.is_super = is_super;
    }

    public String getVip_end_at() {
        return vip_end_at;
    }

    public void setVip_end_at(String vip_end_at) {
        this.vip_end_at = vip_end_at;
    }

//    @Override
//    public String getAuth_token() {
//        return auth_token;
//    }
//
//    @Override
//    public void setAuth_token(String auth_token) {
//        this.auth_token = auth_token;
//    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getIs_bind() {
        return is_bind;
    }

    public void setIs_bind(String is_bind) {
        this.is_bind = is_bind;
    }

    public String getHas_password() {
        return has_password;
    }

    public void setHas_password(String has_password) {
        this.has_password = has_password;
    }

    public String getUser_id() {
        return user_id;
    }
}
