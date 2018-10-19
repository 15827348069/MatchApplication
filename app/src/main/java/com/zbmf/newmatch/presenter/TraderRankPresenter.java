package com.zbmf.newmatch.presenter;


import com.zbmf.newmatch.bean.Traders;
import com.zbmf.newmatch.listener.ITraderRankView;
import com.zbmf.newmatch.model.TraderRankMode;
import com.zbmf.worklibrary.model.CallBack;
import com.zbmf.worklibrary.presenter.BasePresenter;
import com.zbmf.worklibrary.util.Logx;

import java.util.List;

/**
 * Created by pq
 * on 2018/3/28.
 */

public class TraderRankPresenter extends BasePresenter<TraderRankMode, ITraderRankView> {
    @Override
    public void getDatas() {
        if (isFirst()) {
            setFirst(false);
        }
        getTraderRank();
    }

    @Override
    public TraderRankMode initMode() {
        return new TraderRankMode();
    }

    private void getTraderRank() {
        getMode().getTraderRank(new CallBack<List<Traders>>() {
            @Override
            public void onSuccess(List<Traders> o) {
                if (getView() != null) {
                    getView().traderRank(o);
                }
            }

            @Override
            public void onFail(String msg) {
                Logx.e(msg);
            }
        });
    }
}
