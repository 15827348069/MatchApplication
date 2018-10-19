package com.zbmf.newmatch.model.imode;

import com.zbmf.worklibrary.model.CallBack;

/**
 * Created by pq
 * on 2018/10/17.
 * 圈子相关的网络请求回调
 */

public interface IGroupCareMode {
    //关注圈子
    void followGroup(String id, CallBack callBack);
    //获取特别推荐的老师的数据
    void recommendTeacher(int flags,int page,CallBack callBack);


}
