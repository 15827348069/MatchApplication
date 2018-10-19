package com.zbmf.newmatch.model.imode;


import com.zbmf.newmatch.bean.DealsRecordList;
import com.zbmf.newmatch.bean.TraderDeals;
import com.zbmf.worklibrary.baseview.BaseView;

/**
 * Created by pq
 * on 2018/3/26.
 */

public interface DealModeView extends BaseView{
    void onRefreshDealRecord(TraderDeals.Result result);
    void queryData(DealsRecordList.Result result);
    void queryErr(String msg);
}
