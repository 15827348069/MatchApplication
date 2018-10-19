package com.zbmf.newmatch.model.imode;

import com.zbmf.worklibrary.model.CallBack;

/**
 * Created by xuhao
 * on 2017/11/28.
 */

public interface IMatchFragmentMode {
    void getMatchLsit(int matchOrg, int page/*RefreshStatus status*/, CallBack callBack);
}
