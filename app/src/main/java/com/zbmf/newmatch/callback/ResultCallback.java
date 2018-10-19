package com.zbmf.newmatch.callback;

import android.os.Handler;

/**
 * Created by xuhao
 * on 2017/7/25.
 */
public abstract class ResultCallback<T> {
    private  Handler mHandler=new Handler();
    public ResultCallback() {
    }

    public abstract void onSuccess(T str);

    public abstract void onError(String message);

    public void onFail(final String errMessage) {
        mHandler.post(() -> onError(errMessage));
    }

   public void onCallback(final T t) {
        mHandler.post(() -> onSuccess(t));
    }
}