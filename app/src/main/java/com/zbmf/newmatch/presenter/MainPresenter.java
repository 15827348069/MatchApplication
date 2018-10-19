package com.zbmf.newmatch.presenter;


import com.zbmf.worklibrary.baseview.BaseView;
import com.zbmf.worklibrary.model.BaseMode;
import com.zbmf.worklibrary.presenter.BasePresenter;


/**
 * Created by xuhao on 2017/11/22.
 */

public class MainPresenter extends BasePresenter<BaseMode,BaseView> {

    @Override
    public void getDatas() {

    }

    @Override
    public BaseMode initMode() {
        return new BaseMode() {
            @Override
            protected String getHost() {
                return null;
            }
        };
    }

}
