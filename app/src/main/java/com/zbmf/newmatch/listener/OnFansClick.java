package com.zbmf.newmatch.listener;

import com.zbmf.newmatch.bean.BoxBean;

/**
 * Created by xuhao on 2017/8/28.
 */

public interface OnFansClick {
    void onBox(BoxBean boxBean);
    void onFans(String groupId);
    void onGroup(String groupId);
}
