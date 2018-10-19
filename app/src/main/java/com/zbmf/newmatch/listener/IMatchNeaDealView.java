package com.zbmf.newmatch.listener;

import com.zbmf.newmatch.bean.DealsList;
import com.zbmf.newmatch.bean.MatchInfo;
import com.zbmf.newmatch.bean.MatchNewAllBean;
import com.zbmf.worklibrary.baseview.BaseView;

/**
 * Created by xuhao
 * on 2017/12/1.
 * 最新交易的数据回调接口
 */

public interface IMatchNeaDealView extends BaseView {
    void RushDealList(DealsList.Result deals_sys);
    void rushMatchBean(MatchInfo matchBean);
    void RushMatchList(MatchNewAllBean.Result userMatch);
    void RushMatchListErr(String msg);
}
