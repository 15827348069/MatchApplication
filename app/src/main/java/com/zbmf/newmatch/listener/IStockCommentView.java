package com.zbmf.newmatch.listener;


import com.zbmf.newmatch.bean.StockCommentsBean;
import com.zbmf.worklibrary.baseview.BaseView;

/**
 * Created by pq
 * on 2018/3/31.
 */

public interface IStockCommentView extends BaseView{
    void getStockCommentList(StockCommentsBean.Result o);
    void getStockCommentErr(String msg);
    void addStockCommentStatus(String msg);
}
