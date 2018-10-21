package com.zbmf.newmatch.presenter;

import android.text.TextUtils;

import com.zbmf.newmatch.api.ParamsKey;
import com.zbmf.newmatch.bean.HolderPositionBean;
import com.zbmf.newmatch.bean.MatchInfo;
import com.zbmf.newmatch.bean.NoticeBean;
import com.zbmf.newmatch.bean.UserWallet;
import com.zbmf.newmatch.listener.IDrillFragment;
import com.zbmf.newmatch.model.AddStockMode;
import com.zbmf.newmatch.model.JoinMatchMode;
import com.zbmf.newmatch.model.MatchDetailMode;
import com.zbmf.newmatch.model.MatchListMode;
import com.zbmf.newmatch.model.WalletMode;
import com.zbmf.newmatch.util.MatchSharedUtil;
import com.zbmf.worklibrary.model.CallBack;
import com.zbmf.worklibrary.presenter.BasePresenter;

/**
 * Created by xuhao
 * on 2017/12/11.
 */

public class DrillPresenter extends BasePresenter<MatchDetailMode, IDrillFragment> {
    private String matchId;

    public DrillPresenter(String matchId) {
        this.matchId = matchId;
    }

    @Override
    public void getDatas() {
        if (isFirst()) {
            setFirst(false);
        }
        if (!TextUtils.isEmpty(matchId)) {
            String userId = MatchSharedUtil.UserId();
            getMatchDetail(matchId, userId);
            getMatchHolder(matchId, ParamsKey.D_PAGE, MatchSharedUtil.UserId());
            getMatchNotice(matchId, String.valueOf(ParamsKey.D_PAGE));
        }
    }

    @Override
    public MatchDetailMode initMode() {
        return new MatchDetailMode();
    }

    public void getMatchDetail(String matchID,String userId) {
        new AddStockMode().getMatchDetail(matchID, userId,new CallBack<MatchInfo>() {
            @Override
            public void onSuccess(MatchInfo matchInfo) {
                if (getView() != null) {
                    getView().rushMatchBean(matchInfo);
                    setFirst(false);
                }
            }

            @Override
            public void onFail(String msg) {
                if (getView() != null) {
                    getView().rushHoldErr(msg);
                    setFirst(false);
                }
            }
        });
    }

    public void getMatchHolder(String matchId,int page,String userID) {
        new JoinMatchMode().holderPosition(Integer.parseInt(matchId),
                String.valueOf(page),userID, new CallBack<HolderPositionBean.Result>() {
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
    }

    public void getMatchNotice(String matchId, String page) {
        new MatchListMode().getNotice(matchId, page, new CallBack<NoticeBean.Result>() {
            @Override
            public void onSuccess(NoticeBean.Result o) {
                if (getView() != null) {
                    getView().notice(o);
                }
            }

            @Override
            public void onFail(String msg) {
                if (getView() != null) {
                    getView().noticeErr(msg);
                }
            }
        });
    }

    public void getUserWallet() {
        new WalletMode().getUserMoney(new CallBack<UserWallet>() {
            @Override
            public void onSuccess(UserWallet o) {
                if (getView() != null) {
                    getView().userWallet(o);
                }
            }

            @Override
            public void onFail(String msg) {
                if (getView() != null) {
                    getView().userWalletErr(msg);
                }
            }
        });
    }

    public void resetMatch(String matchID) {
        new AddStockMode().resetMatch(matchID, new CallBack<String>() {
            @Override
            public void onSuccess(String o) {
                if (getView() != null) {
                    getView().resetOnSuccess(o);
                }
            }

            @Override
            public void onFail(String msg) {
                if (getView() != null) {
                    getView().resetOnFail(msg);
                }
            }
        });
    }
}
