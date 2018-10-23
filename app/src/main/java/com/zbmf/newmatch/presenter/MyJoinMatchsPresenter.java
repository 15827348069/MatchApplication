package com.zbmf.newmatch.presenter;

import com.zbmf.newmatch.api.ParamsKey;
import com.zbmf.newmatch.bean.MatchNewAllBean;
import com.zbmf.newmatch.listener.MyJoinMatchsView;
import com.zbmf.newmatch.model.JoinMatchMode;
import com.zbmf.newmatch.model.MyJoinMode;
import com.zbmf.newmatch.util.MatchSharedUtil;
import com.zbmf.worklibrary.model.CallBack;
import com.zbmf.worklibrary.presenter.BasePresenter;

/**
 * Created by pq
 * on 2018/10/23.
 * 获取我的参赛的列表
 */

public class MyJoinMatchsPresenter extends BasePresenter<MyJoinMode,MyJoinMatchsView>{

    @Override
    public void getDatas() {
        getMyJoinMatchList(String.valueOf(ParamsKey.D_PAGE), MatchSharedUtil.UserId());
    }

    @Override
    public MyJoinMode initMode() {
        return new MyJoinMode();
    }

    //获取我的参赛列表
    public void getMyJoinMatchList(String page,String userId){
        new JoinMatchMode().getUserMatch(page,userId, new CallBack<MatchNewAllBean.Result>() {
            @Override
            public void onSuccess(MatchNewAllBean.Result o) {
                if (getView() != null) {
                    getView().RushMatchList(o);
                }
            }

            @Override
            public void onFail(String msg) {
                if (getView()!=null){
                    getView().RushMatchListErr(msg);
                }
            }
        });
    }
}
