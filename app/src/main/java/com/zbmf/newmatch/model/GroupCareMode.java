package com.zbmf.newmatch.model;

import com.zbmf.newmatch.api.Method;
import com.zbmf.newmatch.api.SendParam;
import com.zbmf.newmatch.bean.BaseBean;
import com.zbmf.newmatch.bean.RecommendTeacherBean;
import com.zbmf.newmatch.common.Constans;
import com.zbmf.newmatch.model.imode.IGroupCareMode;
import com.zbmf.worklibrary.model.CallBack;
import com.zbmf.worklibrary.util.GsonUtil;

import java.util.Map;

/**
 * Created by pq
 * on 2018/10/16.
 */

public class GroupCareMode extends BaseCareMode implements IGroupCareMode {

    /**
     * 关注圈子
     *
     * @param id       参数
     * @param callBack 回调
     */
    @Override
    public void followGroup(String id, CallBack callBack) {
        postSubscrube(Method.FOLLOW, SendParam.getCareGroup(id), new CallBack() {
            @Override
            public void onSuccess(Object o) {
                BaseBean baseBean = GsonUtil.parseData(o, BaseBean.class);
                assert baseBean != null;
                if (baseBean.getStatus()) {
                    callBack.onSuccess("ok");
                } else {
                    callBack.onFail("关注失败！");
                }
            }

            @Override
            public void onFail(String msg) {
                callBack.onFail(msg);
            }
        });
    }

    /**
     * 获取特别推荐的老师的数据
     *
     * @param flags
     * @param page
     */
    @Override
    public void recommendTeacher(int flags, int page, CallBack callBack) {
        String method = "";
        switch (flags) {
            case Constans.PEOPLE_ARROW:
                method = Method.HOT;
                break;
            case Constans.NOW_LIVE:
                method = Method.LIVE;
                break;
            case Constans.EXCLUSIVE:
                method = Method.EXCLUSLIVE;
                break;
            case Constans.PEOPLE_RECOMED:
                method = Method.RECOMMED;
                break;
        }
        Map<String, String> map = SendParam.getRecommendParams(flags, String.valueOf(page));
        if (map != null) {
            postSubscrube(method, map, new CallBack() {
                @Override
                public void onSuccess(Object o) {
                    RecommendTeacherBean recommendTeacherBean = GsonUtil.parseData(o, RecommendTeacherBean.class);
                    assert recommendTeacherBean != null;
                    if (recommendTeacherBean.getStatus()) {
                        callBack.onSuccess(recommendTeacherBean.getResult());
                    } else {
                        callBack.onFail(recommendTeacherBean.getErr().getMsg());
                    }
                }

                @Override
                public void onFail(String msg) {
                    callBack.onFail(msg);
                }
            });
        }

    }


}
