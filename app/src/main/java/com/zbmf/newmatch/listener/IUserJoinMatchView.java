package com.zbmf.newmatch.listener;

import com.zbmf.newmatch.bean.MatchNewAllBean;
import com.zbmf.worklibrary.baseview.BaseView;

/**
 * Created by pq
 * on 2018/4/28.
 */

public interface IUserJoinMatchView extends BaseView{
    void RushMatchList(MatchNewAllBean.Result result);
    void RushMatchListErr(String msg);
}
