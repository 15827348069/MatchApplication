package com.zbmf.newmatch.listener;


import com.zbmf.newmatch.bean.HolderPositionBean;
import com.zbmf.newmatch.bean.TraderHolderPosition;
import com.zbmf.worklibrary.baseview.BaseView;

import java.util.List;

/**
 * Created by xuhao
 * on 2017/12/18.
 */

public interface IMatchHold extends BaseView{
    void RushHoldList(HolderPositionBean.Result result);
    void holderListErr(String msg);
    void onRefreshHolderRecord(List<TraderHolderPosition.Holds> holds);
   /* void onRefreshDealRecord(TraderDeals.Result result);*/
}
