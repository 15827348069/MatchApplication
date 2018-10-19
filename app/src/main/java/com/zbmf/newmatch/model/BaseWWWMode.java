package com.zbmf.newmatch.model;

import com.zbmf.newmatch.api.HostUrl;
import com.zbmf.newmatch.common.SharedKey;
import com.zbmf.worklibrary.model.BaseMode;
import com.zbmf.worklibrary.util.SharedpreferencesUtil;

/**
 * Created by xuhao
 * on 2017/11/22.
 */

public class BaseWWWMode extends BaseMode{
    @Override
    protected String getHost() {
        return SharedpreferencesUtil.getInstance().getString(SharedKey.WWW_HOST, HostUrl.WWW_URL);
    }
}
