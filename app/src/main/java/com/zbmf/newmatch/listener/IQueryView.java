package com.zbmf.newmatch.listener;

import com.zbmf.newmatch.bean.DealsRecordList;
import com.zbmf.worklibrary.baseview.BaseView;

/**
 * Created by pq
 * on 2018/4/8.
 */

public interface IQueryView extends BaseView{
    void queryData(DealsRecordList.Result result);
    void queryErr(String msg);
}
