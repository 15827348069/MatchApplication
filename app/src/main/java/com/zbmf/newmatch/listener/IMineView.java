package com.zbmf.newmatch.listener;


import com.zbmf.newmatch.bean.MatchNewAllBean;
import com.zbmf.newmatch.bean.User;
import com.zbmf.worklibrary.baseview.BaseView;

/**
 * Created by xuhao
 * on 2017/11/30.
 */

public interface IMineView extends BaseView{
    void RushMatchList(MatchNewAllBean.Result userMatch);
    void RushMatchListErr(String msg);
    void RushMineMessage(User user);
    void onError(String msg);
}
