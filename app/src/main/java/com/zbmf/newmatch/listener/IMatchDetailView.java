package com.zbmf.newmatch.listener;


import com.zbmf.newmatch.bean.HolderPositionBean;
import com.zbmf.newmatch.bean.MatchInfo;
import com.zbmf.worklibrary.baseview.BaseView;

/**
 * Created by xuhao
 * on 2017/11/29.
 */

public interface IMatchDetailView extends BaseView{
    void RushMatchDetail(MatchInfo matchInfo);
//    void RushMatchHold(StockHoldList stockHoldList);

    void RushHoldList(HolderPositionBean.Result result);
    void holderListErr(String msg);
}
