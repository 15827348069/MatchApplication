package com.zbmf.newmatch.presenter;

import com.zbmf.newmatch.bean.RecommendTeacherBean;
import com.zbmf.newmatch.listener.IGroupCareView;
import com.zbmf.newmatch.model.GroupCareMode;
import com.zbmf.worklibrary.model.CallBack;
import com.zbmf.worklibrary.presenter.BasePresenter;

/**
 * Created by **
 * on 2018/10/16.
 */

public class GroupCarePresenter extends BasePresenter<GroupCareMode, IGroupCareView> {

    @Override
    public void getDatas() {

    }

    @Override
    public GroupCareMode initMode() {
        return new GroupCareMode();
    }

    //关注圈子
    public void careGroup(String id, int position) {
        getMode().followGroup(id, new CallBack() {
            @Override
            public void onSuccess(Object o) {
                if (getView() != null) {
                    getView().onSucceed((String) o, position);
                }
            }

            @Override
            public void onFail(String msg) {
                if (getView() != null) {
                    getView().onFail(msg, position);
                }
            }
        });
    }

    //特别推荐---老师的数据
    public void getRecommendTeacher(int flags, int page) {
        getMode().recommendTeacher(flags, page, new CallBack<RecommendTeacherBean.RecommendResult>() {
            @Override
            public void onSuccess(RecommendTeacherBean.RecommendResult recommendResult) {
                if (getView() != null) {
                    getView().onRecommendResult(recommendResult);
                } else {
                    getView().onFail("数据加载失败",-1);
                }
            }

            @Override
            public void onFail(String msg) {
                getView().onFail(msg,-1);
            }
        });
    }


}
