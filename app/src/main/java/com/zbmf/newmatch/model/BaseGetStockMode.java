package com.zbmf.newmatch.model;


import com.zbmf.newmatch.api.HostUrl;
import com.zbmf.newmatch.api.Method;
import com.zbmf.newmatch.api.SendParam;
import com.zbmf.worklibrary.model.BaseMode;

/**
 * Created by pq
 * on 2018/3/29.
 */

public class BaseGetStockMode extends BaseMode {
    private String searchKey;

    public void setSearchKey(String searchKey) {
        this.searchKey = searchKey;
    }

    @Override
    protected String getHost() {
        return HostUrl.GET_STOCK + Method.GET_STOCK + SendParam.getStock(searchKey);
    }
}
