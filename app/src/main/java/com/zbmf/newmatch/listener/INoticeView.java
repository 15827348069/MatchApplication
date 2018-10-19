package com.zbmf.newmatch.listener;

import com.zbmf.newmatch.bean.NoticeBean;
import com.zbmf.worklibrary.baseview.BaseView;

/**
 * Created by pq
 * on 2018/4/11.
 */

public interface INoticeView extends BaseView {
    void noticeList(NoticeBean.Result notice);
    void noticeErr(String msg);
}
