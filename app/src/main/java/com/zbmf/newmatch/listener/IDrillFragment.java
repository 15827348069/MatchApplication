package com.zbmf.newmatch.listener;


import com.zbmf.newmatch.bean.HolderPositionBean;
import com.zbmf.newmatch.bean.MatchInfo;
import com.zbmf.newmatch.bean.NoticeBean;
import com.zbmf.newmatch.bean.UserWallet;
import com.zbmf.worklibrary.baseview.BaseView;

/**
 * Created by xuhao
 * on 2017/12/11.
 */

public interface IDrillFragment extends BaseView{
    void rushMatchBean(MatchInfo matchBean);
//    void rushHold(StockHoldList stockHoldList);
    void rushHoldErr(String msg);
    void userWallet(UserWallet userWallet);
    void userWalletErr(String msg);
    void resetOnSuccess(String msg);
    void resetOnFail(String msg);
    void notice(NoticeBean.Result notice);
    void noticeErr(String msg);
    void RushHoldList(HolderPositionBean.Result result);
    void holderListErr(String msg);
}
