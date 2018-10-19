package com.zbmf.newmatch.fragment.video;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.zbmf.newmatch.R;
import com.zbmf.newmatch.activity.VideoPlayActivity;
import com.zbmf.newmatch.bean.Video;
import com.zbmf.newmatch.common.IntentKey;
import com.zbmf.newmatch.fragment.BaseFragment;
import com.zbmf.newmatch.util.ImageLoaderOptions;
import com.zbmf.newmatch.util.NetWorkUtil;
import com.zbmf.worklibrary.presenter.BasePresenter;


/**
 * Created by xuhao on 2017/7/26.
 */

public class VideoMobileFragment extends BaseFragment implements View.OnClickListener {

    ImageView videoImg;
    TextView tvVideoGroupName;
    TextView iv_center_message;
    TextView iv_center_play;
    RelativeLayout rlPlay;
    VideoPlayActivity activity;
    private Video video;
    private int commit;
    public static VideoMobileFragment newInstance(Video video,int commit){
        VideoMobileFragment fragment = new VideoMobileFragment();
        Bundle bundle=new Bundle();
        bundle.putSerializable(IntentKey.VIDEO_KEY,video);
        bundle.putInt(IntentKey.VIDEO_MOBILE,commit);
        fragment.setArguments(bundle);
        return fragment;
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments()!=null){
            video = (Video) getArguments().getSerializable(IntentKey.VIDEO_KEY);
            commit=getArguments().getInt(IntentKey.VIDEO_MOBILE);
        }
        activity= (VideoPlayActivity) getActivity();
    }

    public void RushMessage(Video v,int c){
        video=v;
        commit=c;
        setMessage();
    }

    @Override
    protected int getLayout() {
        return R.layout.fragment_mobile_layout;
    }

    @Override
    protected void initView() {
        videoImg=(ImageView) getView(R.id.video_img);
        tvVideoGroupName=(TextView) getView(R.id.tv_video_group_name);
        iv_center_message=(TextView)getView(R.id.iv_center_message);
        iv_center_play=(TextView)getView(R.id.iv_center_play);
        rlPlay=(RelativeLayout)getView(R.id.rl_play);
        getView(R.id.backPlayList).setOnClickListener(this);
        getView(R.id.iv_top_menu).setOnClickListener(this);
        iv_center_play.setOnClickListener(this);
        setMessage();
    }
    private void setMessage(){
//        ViewFactory.getImgView(getActivity(),video.getVideoImg(),videoImg);
        ImageLoader.getInstance().displayImage(video.getVideoImg(),videoImg, ImageLoaderOptions.BigProgressOptions());
        tvVideoGroupName.setText("【"+video.getVideoGroupname()+"】 "+video.getVideoName());

        if(commit==1){
            iv_center_message.setText(getString(R.string.live_message));
            iv_center_play.setCompoundDrawables(null,null,null,null);
            iv_center_play.setText("立即订阅");
        }
    }
    public void setRelaySize(boolean isPortrait){
        ViewGroup.LayoutParams layoutParams=rlPlay.getLayoutParams();
        if(isPortrait){
            layoutParams.height=  ViewGroup.LayoutParams.WRAP_CONTENT;
            layoutParams.width=  ViewGroup.LayoutParams.MATCH_PARENT;
        }else{
            layoutParams.height=  ViewGroup.LayoutParams.MATCH_PARENT;
            layoutParams.width=  ViewGroup.LayoutParams.MATCH_PARENT;
        }
        rlPlay.setLayoutParams(layoutParams);
    }
    @Override
    protected void initData() {

    }

    @Override
    protected BasePresenter initPresent() {
        return null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_center_play:
                if(NetWorkUtil.isNetWork(getContext())){
                    activity.setPlayFragment(commit);
                }else{
                    showToast("网络不可用，请检查网络设置!");
                }
                break;
            case R.id.backPlayList:
                getActivity().finish();
                break;
            case R.id.iv_top_menu:
                activity.showShare();
                break;
        }
    }
}
