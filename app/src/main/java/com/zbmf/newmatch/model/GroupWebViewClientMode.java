package com.zbmf.newmatch.model;

import android.util.Log;

import com.zbmf.newmatch.api.Method;
import com.zbmf.newmatch.api.SendParam;
import com.zbmf.newmatch.model.imode.IGroupWebViewClientMode;
import com.zbmf.worklibrary.model.CallBack;

/**
 * Created by pq
 * on 2018/3/28.
 */

public class GroupWebViewClientMode extends BaseWWWMode implements IGroupWebViewClientMode {

    @Override
    public void getStockRealTimeInfo(String symbol, /*JSONHandler jsonHandler,*/ CallBack callBack) {
        postSubscrube(Method.STOCK_REAL_TIME_INFO, SendParam.getStockRealInfo(symbol), new CallBack() {
            @Override
            public void onSuccess(Object o) {
                Log.i("--TAG","------------ o "+o);
            }

            @Override
            public void onFail(String msg) {

            }
        });
    }

    @Override
    public void searchUserBlogInfo(String blog_id, CallBack callBack) {
        postSubscrube(Method.SEARCH_USER_BLOG_INFO,SendParam.getUserBlogInfo(blog_id), new CallBack() {
            @Override
            public void onSuccess(Object o) {
                Log.i("--TAG","------------ o "+o);
            }

            @Override
            public void onFail(String msg) {

            }
        });
    }

    @Override
    public void getVideoInfo(String videoID, CallBack callBack) {
        postSubscrube(Method.LOAD_VIDEO, SendParam.getVideoInfo(videoID), new CallBack() {
            @Override
            public void onSuccess(Object o) {
                Log.i("--TAG","------------ o "+o);
            }

            @Override
            public void onFail(String msg) {

            }
        });
    }
}
