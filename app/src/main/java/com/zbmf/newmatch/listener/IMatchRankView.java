package com.zbmf.newmatch.listener;

import com.zbmf.newmatch.bean.MatchRank;
import com.zbmf.worklibrary.baseview.BaseView;

/**
 * Created by xuhao
 * on 2017/12/1.
 */

public interface IMatchRankView extends BaseView {
//    void RushDealList(List<Yield> yieldList);
    void RushDealList(MatchRank matchRank);
    void rushErr(String msg);
}
