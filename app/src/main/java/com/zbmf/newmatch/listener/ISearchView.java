package com.zbmf.newmatch.listener;

import com.zbmf.newmatch.bean.SearchMatchBean;
import com.zbmf.worklibrary.baseview.BaseView;

/**
 * Created by pq
 * on 2018/3/21.
 */

public interface ISearchView extends BaseView{
    void searchMatchResult(SearchMatchBean searchMatchBean);
}
