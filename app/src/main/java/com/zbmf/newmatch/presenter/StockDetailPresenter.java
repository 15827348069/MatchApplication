package com.zbmf.newmatch.presenter;

import android.text.TextUtils;

import com.zbmf.newmatch.bean.MatchInfo;
import com.zbmf.newmatch.bean.StockAskBean;
import com.zbmf.newmatch.listener.IStockDetailView;
import com.zbmf.newmatch.model.AddStockMode;
import com.zbmf.newmatch.model.StockDetailMode;
import com.zbmf.worklibrary.model.CallBack;
import com.zbmf.worklibrary.presenter.BasePresenter;

/**
 * Created by pq
 * on 2018/3/31.
 */

public class StockDetailPresenter extends BasePresenter<StockDetailMode, IStockDetailView> {
    private String symbol, page;

    public StockDetailPresenter(String symbol, String page) {
        this.symbol = symbol;
        this.page = page;
    }

    @Override
    public void getDatas() {
        if (isFirst()) {
            setFirst(false);
        }
        if (!TextUtils.isEmpty(symbol) && !TextUtils.isEmpty(page)) {
            getAskStockList(symbol, page);
        }
    }

    @Override
    public StockDetailMode initMode() {
        return new StockDetailMode();
    }

    public void getAskStockList(String symbol, String page) {
        getMode().getAskList(symbol, page, new CallBack<StockAskBean.Result>() {
            @Override
            public void onSuccess(StockAskBean.Result o) {
                if (getView() != null) {
                    getView().stockAskList(o);
                }
            }

            @Override
            public void onFail(String msg) {
                if (getView() != null) {
                    getView().stockAskErr(msg);
                }
            }
        });
    }

    public void getMatchInfo(String matchID,String userId) {
        new AddStockMode().getMatchDetail(matchID, userId,new CallBack<MatchInfo>() {
            @Override
            public void onSuccess(MatchInfo o) {
                if (getView() != null) {
                    getView().matchInfo(o);
                }
            }

            @Override
            public void onFail(String msg) {
                if (getView() != null) {
                    getView().matchInfoErr(msg);
                }
            }
        });
    }
}
