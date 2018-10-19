package com.zbmf.newmatch.listener;

import com.zbmf.newmatch.bean.StockRemarkListBean;
import com.zbmf.worklibrary.baseview.BaseView;

/**
 * Created by pq
 * on 2018/3/30.
 */

public interface IStockRemarkView extends BaseView {
    void refreshStockRemarkList(StockRemarkListBean.Result o);

    void refreshStockRemarkListStatus(String msg);

    void addStockRemarkStatus(String msg);

    void deleteStockRemarkStatus(String msg);
}
