package com.zbmf.newmatch.presenter;


import android.content.Context;

import com.zbmf.newmatch.bean.LoginUser;
import com.zbmf.newmatch.listener.ILoginFragmentView;
import com.zbmf.newmatch.model.LoginMode;
import com.zbmf.worklibrary.model.CallBack;
import com.zbmf.worklibrary.presenter.BasePresenter;

/**
 * Created by xuhao
 * on 2017/11/21.
 */

public class LoginFragmentPresenter extends BasePresenter<LoginMode, ILoginFragmentView> {
    @Override
    public void getDatas() {}

    @Override
    public LoginMode initMode() {
        return new LoginMode();
    }

    public void Login(Context context, String name, String pass) {
        getMode().login(context,name, pass, new CallBack<LoginUser>() {
            @Override
            public void onSuccess(LoginUser user) {
                if (getView()!=null){
                    getView().dissLoading();
                    getView().onLoginSucceed(user);
                }
            }

            @Override
            public void onFail(String code) {
                if (getView()!=null){
                    getView().onLoginErr(code);
                }
            }
        });
    }
}
