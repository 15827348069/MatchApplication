package com.zbmf.newmatch.model;

import com.zbmf.newmatch.api.Method;
import com.zbmf.newmatch.api.SendParam;
import com.zbmf.newmatch.bean.NoticeBean;
import com.zbmf.newmatch.model.imode.INoticeMode;
import com.zbmf.worklibrary.model.CallBack;
import com.zbmf.worklibrary.util.GsonUtil;

/**
 * Created by pq
 * on 2018/4/11.
 */

public class NoticeMode extends BaseStockMode implements INoticeMode {
    @Override
    public void getMatchNoticeList(String matchId, String page, final CallBack callBack) {
        postSubscrube(Method.MATCH_NOTICE, SendParam.getMatchNotice(matchId, page), new CallBack() {
            @Override
            public void onSuccess(Object o) {
                NoticeBean noticeBean = GsonUtil.parseData(o, NoticeBean.class);
                if (noticeBean.getStatus()) {
                    callBack.onSuccess(noticeBean.getResult());
                } else {
                    callBack.onFail(noticeBean.getErr().getMsg());
                }
            }

            @Override
            public void onFail(String msg) {
                callBack.onFail(msg);
            }
        });
    }
}
