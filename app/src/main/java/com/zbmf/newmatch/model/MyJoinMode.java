package com.zbmf.newmatch.model;

import com.zbmf.newmatch.api.Method;
import com.zbmf.newmatch.api.SendParam;
import com.zbmf.newmatch.bean.MatchList;
import com.zbmf.worklibrary.model.CallBack;
import com.zbmf.worklibrary.util.GsonUtil;

/**
 * Created by pq
 * on 2018/10/23.
 */

public class MyJoinMode extends BaseMatchMode {

    //获取我的参赛列表
    public void getMyJoinMatchsList(final CallBack callBack){
        postSubscrube(Method.GET_RUNMATCHS, SendParam.getRunMatchList(), new CallBack() {
            @Override
            public void onSuccess(Object o) {
                MatchList matchList= GsonUtil.parseData(o,MatchList.class);
                if(matchList.getStatus()){
                    callBack.onSuccess(matchList);
                }else{
                    callBack.onFail(matchList.getErr().getMsg());
                }
            }

            @Override
            public void onFail(String msg) {
                callBack.onFail(msg);
            }
        });
    }
}
