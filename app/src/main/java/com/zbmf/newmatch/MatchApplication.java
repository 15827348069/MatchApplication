package com.zbmf.newmatch;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.sina.weibo.sdk.WbSdk;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.zbmf.newmatch.common.AppConfig;
import com.zbmf.newmatch.common.Constans;
import com.zbmf.newmatch.util.SettingDefaultsManager;
import com.zbmf.worklibrary.util.Logx;
import com.zbmf.worklibrary.util.SharedpreferencesUtil;

/**
 * Created by xuhao
 * on 2017/11/22.
 */

/**
 *  以后维护的人注意：  因为时间比较赶，两个项目合成的时候，代码没整理：
 *  该项目中有两个Sp存储文件，两套网络加载，两套图片加载，后续维护的时候最好统一
 *
 */

public class MatchApplication extends Application {
    private static MatchApplication instance=null;
    private static Context mContext;
    public static MatchApplication getInstance(){
        if (instance==null){
            synchronized (MatchApplication.class){
                if (instance==null){
                    instance=new MatchApplication();
                }
            }
        }
        return instance;
    }
    public static Context getContext(){
        return mContext;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
        Logx.init(AppConfig.IS_DEBUG);
        Log.d("===TAG","---   初始化SP   :");
        SharedpreferencesUtil.getInstance().initSharedUtil(this);
        WbSdk.install(this,new AuthInfo(this, Constans.WBSDKAppKey,
                Constans.REDIRECT_URL, Constans.SCOPE));
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
//        MultiDex.install(this);
    }
}
