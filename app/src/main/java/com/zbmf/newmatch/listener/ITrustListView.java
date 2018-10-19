package com.zbmf.newmatch.listener;

import com.zbmf.newmatch.bean.OrderList;
import com.zbmf.worklibrary.baseview.BaseView;

/**
 * Created by pq
 * on 2018/4/4.
 */

public interface ITrustListView extends BaseView {
    void getTrustList(OrderList.Result result);
    void err(String msg);
    void revokeResult(String msg);
}
