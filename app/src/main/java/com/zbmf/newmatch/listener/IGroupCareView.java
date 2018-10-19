package com.zbmf.newmatch.listener;

import com.zbmf.newmatch.bean.RecommendTeacherBean;
import com.zbmf.worklibrary.baseview.BaseView;

/**
 * Created by pq
 * on 2018/10/17.
 */

public interface IGroupCareView extends BaseView{

    void onSucceed(String msg, int position);
    void onRecommendResult(RecommendTeacherBean.RecommendResult reslut);
    void onFail(String msg,int position);

}
