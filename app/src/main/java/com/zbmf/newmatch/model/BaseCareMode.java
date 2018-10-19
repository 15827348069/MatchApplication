package com.zbmf.newmatch.model;

import com.zbmf.newmatch.api.HostUrl;
import com.zbmf.worklibrary.model.BaseMode;

/**
 * Created by pq
 * on 2018/10/17.
 */

public class BaseCareMode extends BaseMode{
    @Override
    protected String getHost() {
        return HostUrl.ALL_URL;//返回圈子的域名
    }
}
