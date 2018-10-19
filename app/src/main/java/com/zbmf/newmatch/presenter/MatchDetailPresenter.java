package com.zbmf.newmatch.presenter;

import com.zbmf.newmatch.api.ParamsKey;
import com.zbmf.newmatch.bean.HolderPositionBean;
import com.zbmf.newmatch.bean.MatchInfo;
import com.zbmf.newmatch.listener.IMatchDetailView;
import com.zbmf.newmatch.model.AddStockMode;
import com.zbmf.newmatch.model.JoinMatchMode;
import com.zbmf.newmatch.model.MatchDetailMode;
import com.zbmf.newmatch.util.MatchSharedUtil;
import com.zbmf.worklibrary.model.CallBack;
import com.zbmf.worklibrary.presenter.BasePresenter;

/**
 * Created by xuhao
 * on 2017/11/29.
 */

public class MatchDetailPresenter extends BasePresenter<MatchDetailMode,IMatchDetailView> {
    private String matchid;
    public MatchDetailPresenter(String matchid){
        this.matchid=matchid;
    }
    @Override
    public void getDatas() {
        if(isFirst()){
            setFirst(false);
        }
        getMatchDetail();
        new JoinMatchMode().holderPosition(Integer.parseInt(matchid), String.valueOf(ParamsKey.D_PAGE)
                , MatchSharedUtil.UserId(), new CallBack<HolderPositionBean.Result>() {
            @Override
            public void onSuccess(HolderPositionBean.Result result) {
                if (getView() != null) {
                    getView().RushHoldList(result);
                }
            }

            @Override
            public void onFail(String msg) {
                if (getView() != null) {
                    getView().holderListErr(msg);
                }
            }
        });
//        getMode().getMatchHold(matchid, MatchSharedUtil.UserId(), new CallBack<StockHoldList>() {
//            @Override
//            public void onSuccess(StockHoldList stockHoldList) {
//                getView().RushMatchHold(stockHoldList);
//                setFirst(false);
//            }
//
//            @Override
//            public void onFail(String msg) {
//                getView().showToastMsg(msg);
//                setFirst(false);
//            }
//        });
    }

    @Override
    public MatchDetailMode initMode() {
        return new MatchDetailMode();
    }
    private void getMatchDetail(){
        new AddStockMode().getMatchDetail(matchid,MatchSharedUtil.UserId(), new CallBack<MatchInfo>() {
            @Override
            public void onSuccess(MatchInfo matchInfo) {
                getView().dissLoading();
                getView().RushMatchDetail(matchInfo);
            }

            @Override
            public void onFail(String msg) {
                getView().dissLoading();
                getView().showToastMsg(msg);
            }
        });
    }
}
