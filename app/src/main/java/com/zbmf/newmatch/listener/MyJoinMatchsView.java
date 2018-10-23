package com.zbmf.newmatch.listener;

import com.zbmf.newmatch.bean.MatchNewAllBean;
import com.zbmf.worklibrary.baseview.BaseView;

/**
 * Created by pq
 * on 2018/10/23.
 */

public interface MyJoinMatchsView extends BaseView {
    void RushMatchList(MatchNewAllBean.Result userMatch);
    void RushMatchListErr(String msg);
}
