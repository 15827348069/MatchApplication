package com.zbmf.newmatch.listener;

import com.zbmf.newmatch.bean.LoginUser;
import com.zbmf.worklibrary.baseview.BaseView;


/**
 * Created by xuhao
 * on 2017/11/21.
 */

public interface IRegisterFragmentView extends BaseView {
    void onRegisterSucceed(LoginUser loginUser);  //注册成功
    void onRegisterErr(String msg);
    void sendCodeSuccess(String msg);
    void sendCodeFail(String msg);
}
