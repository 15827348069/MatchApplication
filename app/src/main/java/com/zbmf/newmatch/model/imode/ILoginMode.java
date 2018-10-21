package com.zbmf.newmatch.model.imode;

import android.content.Context;

import com.zbmf.worklibrary.model.CallBack;

/**
 * Created by xuhao
 * on 2017/11/21.
 */

public interface ILoginMode{
    void login(Context context, String name, String pass, CallBack callBack);
    void forgetPassword(String phone, CallBack callBack);
}
