package com.zbmf.newmatch.listener;

import com.zbmf.newmatch.bean.MatchInfo;
import com.zbmf.newmatch.bean.TraderDeals;
import com.zbmf.newmatch.bean.TraderHolderPosition;
import com.zbmf.newmatch.bean.TraderInfo;
import com.zbmf.newmatch.bean.UserWallet;
import com.zbmf.worklibrary.baseview.BaseView;

import java.util.List;

/**
 * Created by xuhao
 * on 2017/12/19.
 */

public interface IMatchTraderInfo extends BaseView {
    void rushMatchBean(MatchInfo matchInfo);
    void rushMatchBeanErr(String msg);
    void traderBuyState(String msg);
    void onRushTraderInfo(TraderInfo traderInfo);
    void onRefreshHolderRecord(List<TraderHolderPosition.Holds> holds);
    void onRefreshDealRecord(TraderDeals.Result result);
    void userWallet(UserWallet userWallet);
    void userWalletErr(String msg);
}
