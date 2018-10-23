package com.zbmf.newmatch.activity;

import android.os.Bundle;

import com.bokecc.sdk.mobile.live.DWLive;
import com.bokecc.sdk.mobile.live.DWLiveLoginListener;
import com.bokecc.sdk.mobile.live.Exception.DWLiveException;
import com.bokecc.sdk.mobile.live.pojo.PublishInfo;
import com.bokecc.sdk.mobile.live.pojo.RoomInfo;
import com.bokecc.sdk.mobile.live.pojo.TemplateInfo;
import com.bokecc.sdk.mobile.live.pojo.Viewer;
import com.zbmf.newmatch.R;
import com.zbmf.newmatch.api.JSONHandler;
import com.zbmf.newmatch.api.WebBase;
import com.zbmf.newmatch.bean.Video;
import com.zbmf.newmatch.common.Constans;
import com.zbmf.newmatch.common.IntentKey;
import com.zbmf.newmatch.common.RequestCode;
import com.zbmf.newmatch.util.JSONParse;
import com.zbmf.newmatch.util.MatchSharedUtil;
import com.zbmf.newmatch.util.MyActivityManager;
import com.zbmf.newmatch.util.ShowActivity;
import com.zbmf.worklibrary.presenter.BasePresenter;

import org.json.JSONObject;

/**
 * Created by xuhao on 2017/8/29.
 */

public class VideoEmptyActivity extends BaseActivity{

    @Override
    protected int getLayout() {
        return R.layout.activity_empty_layout;
    }

    @Override
    protected String initTitle() {
        return null;
    }

    @Override
    protected void initData(Bundle bundle) {
        //添加管理activity
        MyActivityManager.getMyActivityManager().pushAct(this);
        initTitle();
        String video_id= getIntent().getStringExtra(IntentKey.VIDEO_KEY);
        WebBase.GetVideoDetail(video_id, new JSONHandler(true,VideoEmptyActivity.this,getString(R.string.loading)) {
            @Override
            public void onSuccess(JSONObject obj) {
                if(!obj.isNull("video")){
                    JSONObject jsonObject = obj.optJSONObject("video");
                    final Video video = JSONParse.getVideo(jsonObject);
                    if(video.getIs_live()){
                        DWLive.getInstance().setDWLiveLoginParams(new DWLiveLoginListener() {
                            @Override
                            public void onLogin(TemplateInfo templateInfo, Viewer viewer, RoomInfo roomInfo, PublishInfo publishInfo) {
                                runOnUiThread(() -> {
                                    Bundle bundle1 =new Bundle();
                                    bundle1.putSerializable(IntentKey.VIDEO_KEY,video);
                                    ShowActivity.showActivityForResult(VideoEmptyActivity.this, bundle1,
                                            VideoPlayActivity.class, RequestCode.COMIT_VIDEO);
                                    finish();
                                });
                            }
                            @Override
                            public void onException(final DWLiveException e) {
                                runOnUiThread(() -> {
                                    Bundle bundle12 =new Bundle();
                                    bundle12.putSerializable(IntentKey.VIDEO_KEY,video);
                                    ShowActivity.showActivityForResult(VideoEmptyActivity.this, bundle12,
                                            VideoPlayActivity.class, RequestCode.COMIT_VIDEO);
                                    finish();
                                    finish();
                                });
                            }
                        }, Constans.CC_USERID,video.getBokecc_id()+"", MatchSharedUtil.NickName(),"");
                        DWLive.getInstance().startLogin();

                    }else{
                        Bundle bundle=new Bundle();
                        bundle.putSerializable(IntentKey.VIDEO_KEY,video);
                        ShowActivity.showActivity(VideoEmptyActivity.this,bundle, VideoPlayActivity.class);
                        finish();
                    }
                }
            }

            @Override
            public void onFailure(String err_msg) {
                finish();
            }
        });
    }

    @Override
    protected BasePresenter initPresent() {
        return null;
    }



}
