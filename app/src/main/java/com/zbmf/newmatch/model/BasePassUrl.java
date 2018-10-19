package com.zbmf.newmatch.model;

import com.zbmf.newmatch.api.HostUrl;
import com.zbmf.worklibrary.model.BaseMode;

/**
 * Created by pq
 * on 2018/4/10.
 */

public class BasePassUrl extends BaseMode {
    @Override
    protected String getHost() {
        return HostUrl.USER_URL;
    }
}
