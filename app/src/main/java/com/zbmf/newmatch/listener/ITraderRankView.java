package com.zbmf.newmatch.listener;


import com.zbmf.newmatch.bean.Traders;
import com.zbmf.worklibrary.baseview.BaseView;

import java.util.List;

/**
 * Created by pq
 * on 2018/3/28.
 */

public interface ITraderRankView extends BaseView{
    void traderRank(List<Traders> traders);
}
