package com.zbmf.newmatch.model;

import com.zbmf.newmatch.api.Method;
import com.zbmf.newmatch.api.SendParam;
import com.zbmf.newmatch.bean.TraderList;
import com.zbmf.newmatch.model.imode.ITraderRankMode;
import com.zbmf.worklibrary.model.CallBack;
import com.zbmf.worklibrary.util.GsonUtil;

/**
 * Created by pq
 * on 2018/3/28.
 */

public class TraderRankMode extends BaseMatchMode implements ITraderRankMode {
    @Override
    public void getTraderRank(final CallBack callBack) {
        postSubscrube(Method.TRADER_RANKS, SendParam.getTraderRank(), new CallBack() {
            @Override
            public void onSuccess(Object o) {
                TraderList traderList = GsonUtil.parseData(o, TraderList.class);
                assert traderList != null;
                if (traderList.getStatus()) {
                    if (traderList.getTraders() != null) {
                        callBack.onSuccess(traderList.getTraders());
                    }
                } else {
                    callBack.onFail(traderList.getErr().getMsg());
                }
            }

            @Override
            public void onFail(String msg) {
                callBack.onFail(msg);
            }
        });
    }
}
