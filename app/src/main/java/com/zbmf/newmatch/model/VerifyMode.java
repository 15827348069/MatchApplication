package com.zbmf.newmatch.model;

import com.zbmf.newmatch.api.Method;
import com.zbmf.newmatch.api.SendParam;
import com.zbmf.newmatch.bean.VerifyCodeBean;
import com.zbmf.worklibrary.model.CallBack;
import com.zbmf.worklibrary.util.GsonUtil;

/**
 * Created by pq
 * on 2018/4/16.
 */

public class VerifyMode extends BaseVerifyMode {

    public void verifyCode(String phone, String code, final CallBack callBack) {
        postSubscrube(Method.VER_CODE, SendParam.verifyCode(phone, code), new CallBack() {
            @Override
            public void onSuccess(Object o) {
                VerifyCodeBean verifyCodeBean = GsonUtil.parseData(o, VerifyCodeBean.class);
                if (verifyCodeBean.getStatus()) {
                    callBack.onSuccess(verifyCodeBean);
                } else {
                    callBack.onFail(verifyCodeBean.getErr().getMsg());
                }
            }

            @Override
            public void onFail(String msg) {
                callBack.onFail(msg);
            }
        });
    }
}
