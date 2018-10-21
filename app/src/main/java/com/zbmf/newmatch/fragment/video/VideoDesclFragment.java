package com.zbmf.newmatch.fragment.video;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bokecc.sdk.mobile.live.DWLive;
import com.bokecc.sdk.mobile.live.DWLiveLoginListener;
import com.bokecc.sdk.mobile.live.Exception.DWLiveException;
import com.bokecc.sdk.mobile.live.pojo.PublishInfo;
import com.bokecc.sdk.mobile.live.pojo.RoomInfo;
import com.bokecc.sdk.mobile.live.pojo.TemplateInfo;
import com.bokecc.sdk.mobile.live.pojo.Viewer;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zbmf.newmatch.R;
import com.zbmf.newmatch.activity.SeriesVideoActivity;
import com.zbmf.newmatch.activity.VideoPlayActivity;
import com.zbmf.newmatch.adapter.RecommendVideoAdapter;
import com.zbmf.newmatch.adapter.ViedoCompilanAdapter;
import com.zbmf.newmatch.api.JSONHandler;
import com.zbmf.newmatch.api.WebBase;
import com.zbmf.newmatch.bean.Group;
import com.zbmf.newmatch.bean.Video;
import com.zbmf.newmatch.common.Constans;
import com.zbmf.newmatch.common.IntentKey;
import com.zbmf.newmatch.common.RequestCode;
import com.zbmf.newmatch.fragment.BaseFragment;
import com.zbmf.newmatch.util.ImageLoaderOptions;
import com.zbmf.newmatch.util.JSONParse;
import com.zbmf.newmatch.util.MatchSharedUtil;
import com.zbmf.newmatch.util.SettingDefaultsManager;
import com.zbmf.newmatch.util.ShowActivity;
import com.zbmf.newmatch.view.ListViewForScrollView;
import com.zbmf.newmatch.view.RoundedCornerImageView;
import com.zbmf.worklibrary.presenter.BasePresenter;
import com.zbmf.worklibrary.pulltorefresh.PullToRefreshBase;
import com.zbmf.worklibrary.pulltorefresh.PullToRefreshScrollView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by xuhao on 2017/7/4.
 */

public class VideoDesclFragment extends BaseFragment implements View.OnClickListener {
    private TextView tv_group_name;
    private RoundedCornerImageView avatar;
    private CheckBox video_care;
    private RelativeLayout video_special_layout;
    private RecyclerView video_special;
    private Video videoBean;
    private LinearLayout tv_video_desc_layout;
    private PullToRefreshScrollView video_detail_desc;
    private ListViewForScrollView video_list;

    private List<Video> recommendlist;
    private List<Video> compilanlist;

    private ViedoCompilanAdapter adapter;
    private RecommendVideoAdapter recommendVideoAdapter;
    private RelativeLayout group_message_layout;
    private boolean desc, recommend, compilan;
    private int series_id;
    private TextView tv_special, tv_series_name;
    private VideoPlayActivity activity;
    public static VideoDesclFragment newInstance(Video video) {
        VideoDesclFragment fragment = new VideoDesclFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(IntentKey.VIDEO_KEY, video);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            videoBean = (Video) getArguments().getSerializable(IntentKey.VIDEO_KEY);
            series_id = videoBean.getSeries_id();
        }
        activity= (VideoPlayActivity) getActivity();
    }

    @Override
    protected int getLayout() {
        return R.layout.vedio_detail;
    }

    @Override
    protected void initView() {
        tv_group_name = (TextView) getView(R.id.tv_group_name);
        tv_video_desc_layout = (LinearLayout) getView(R.id.tv_video_desc_layout);
        avatar = (RoundedCornerImageView) getView(R.id.group_avatar);
        video_care = (CheckBox) getView(R.id.video_care);

        video_special_layout = (RelativeLayout) getView(R.id.video_special_layout);
        video_special = (RecyclerView) getView(R.id.video_special);
        video_list = (ListViewForScrollView) getView(R.id.video_list);
        group_message_layout = (RelativeLayout) getView(R.id.group_message_layout);
        tv_special = (TextView) getView(R.id.tv_special);
        tv_series_name = (TextView) getView(R.id.tv_series_name);
        video_detail_desc = (PullToRefreshScrollView) getView(R.id.video_detail_desc);
        video_detail_desc.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        video_detail_desc.setOnRefreshListener(refreshView -> onRush());
        video_care.setOnClickListener(this);
        getView(R.id.btn_into_group).setOnClickListener(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        video_special.setLayoutManager(linearLayoutManager);
        compilanlist = new ArrayList<>();
        adapter = new ViedoCompilanAdapter(getContext(), compilanlist);
        adapter.setOnItemClickLitener((view, video) -> {
            //切换播放视频
            if (video != null) {
                if(video.getVideoId()==videoBean.getVideoId()){
                    showToast("正在播放该视频");
                }else{
                    if (video.getIs_live()) {
                        setLoginMessage(video);
                    } else {
                        activity.changeVideo(video);
                    }
                }
            }
        });
        video_special.setAdapter(adapter);
        recommendlist = new ArrayList<>();
        recommendVideoAdapter = new RecommendVideoAdapter(getContext(), recommendlist);
        video_list.setAdapter(recommendVideoAdapter);
        video_list.setOnItemClickListener((parent, view, position, id) -> {
            Video video=recommendlist.get(position);
            if (video != null) {
                if(video.getVideoId()==videoBean.getVideoId()){
                    showToast("正在播放该视频");
                }else{
                    if (video.getIs_live()) {
                        setLoginMessage(video);
                    } else {
                        activity.changeVideo(video);
                    }
                }
            }
        });
        if (series_id != 0) {
            video_special_layout.setVisibility(View.VISIBLE);
            getCompilanDetail();
        } else {
            video_special_layout.setVisibility(View.GONE);
        }
        setVideoMessage();
        getVideoDetail();
        getRecommendList();
    }

    @Override
    public void onRush() {
        if (series_id != 0) {
            getCompilanDetail();
        }
        getVideoDetail();
        getRecommendList();
    }


    public void setLoginMessage(final Video video) {
        DWLive.getInstance().setDWLiveLoginParams(new DWLiveLoginListener() {
            @Override
            public void onLogin(TemplateInfo templateInfo, Viewer viewer, RoomInfo roomInfo, PublishInfo publishInfo) {
                getActivity().runOnUiThread(() -> activity.changeVideo(video));
            }

            @Override
            public void onException(final DWLiveException e) {
                getActivity().runOnUiThread(() -> activity.changeVideo(video));
            }
        }, Constans.CC_USERID, video.getBokecc_id() + "",
                MatchSharedUtil.NickName(), "");
        DWLive.getInstance().startLogin();
    }

    @Override
    protected void initData() {

    }

    @Override
    protected BasePresenter initPresent() {
        return null;
    }

    public void setVideoSeries() {
        if(isAdded()){
            Video.SeriesVideo series =videoBean.getSeriesVideo();
            if (!series.getStatus()) {
                tv_special.setText(getString(R.string.video_pecial).replace("[*]", series.getPhase()));
            } else {
                tv_special.setText(getString(R.string.video_pecial_done).replace("[*]", series.getPhase()));
            }
            tv_special.setTag(series);
            tv_special.setOnClickListener(this);
        }
    }

    /**
     * 获取视频详情
     */
    public void getVideoDetail() {
        WebBase.GetVideoDetail(videoBean.getVideoId(), new JSONHandler() {
            @Override
            public void onSuccess(JSONObject obj) {
                JSONObject video = obj.optJSONObject("video");
                videoBean = JSONParse.getVideo(video);
                setVideoMessage();
                desc = true;
                RunshView();
            }

            @Override
            public void onFailure(String err_msg) {
                video_detail_desc.onRefreshComplete();
            }
        });
    }

    /**
     * 获取推荐视频
     */
    public void getRecommendList() {
        WebBase.Recommend(new JSONHandler() {
            @Override
            public void onSuccess(JSONObject obj) {
                if(recommendVideoAdapter!=null&&recommendlist!=null){
                    recommendlist.clear();
                    recommendlist.addAll(JSONParse.getVideoList(obj.optJSONArray("result")));
                    recommendVideoAdapter.notifyDataSetChanged();
                }
                recommend = true;
                RunshView();
            }

            @Override
            public void onFailure(String err_msg) {
                recommend = true;
            }
        });
    }

    /**
     * 获取专辑列表
     */
    public void getCompilanDetail() {
        WebBase.Series(series_id+"", new JSONHandler() {
            @Override
            public void onSuccess(JSONObject obj) {
                if (!obj.isNull("videos")) {
                    compilanlist.clear();
                    compilanlist.addAll(JSONParse.getVideoList(obj.optJSONArray("videos")));
                    adapter.notifyDataSetChanged();
                }
                if (!obj.isNull("series")) {
                    Video.SeriesVideo seriesVideo=JSONParse.getSeries(obj.optJSONObject("series"));
                    seriesVideo.setSeries_id(series_id+"");
                    videoBean.setSeriesVideo(seriesVideo);
                    setVideoSeries();
                }
                compilan = true;
                RunshView();
            }

            @Override
            public void onFailure(String err_msg) {
                compilan = true;
            }
        });
    }

    public void RunshView() {
        if (desc && compilan && recommend) {
            video_detail_desc.onRefreshComplete();
        }
    }

    private void setVideoMessage() {
        if (tv_group_name != null) {
            tv_group_name.setText(videoBean.getVideoGroupname());
        }
            if(avatar!=null){
//                ViewFactory.imgCircleView(getActivity(),videoBean.getAvatar(), avatar);
                ImageLoader.getInstance().displayImage(videoBean.getAvatar(), avatar, ImageLoaderOptions.AvatarOptions());
            }
            if(video_care!=null){
                video_care.setChecked(videoBean.getFollow());
                if (videoBean.getFollow()) {
                    video_care.setText("已关注");
                } else {
                    video_care.setText("关注");
                }
            }
            if(group_message_layout!=null){
                if (videoBean.getIs_group()) {
                    group_message_layout.setVisibility(View.VISIBLE);
                } else {
                    group_message_layout.setVisibility(View.GONE);
                }
            }
            getVideoContent();
    }

    public void getVideoContent() {
        if(videoBean==null||videoBean.getContent()==null){
            return;
        }
        try {
            tv_video_desc_layout.removeAllViews();
            JSONObject content = new JSONObject(videoBean.getContent());
            Iterator it = content.keys();
            while (it.hasNext()) {
                String key = (String) it.next();
                View view = getDescView(key, content.optString(key));
                if (view != null&&tv_video_desc_layout!=null) {
                    tv_video_desc_layout.addView(view);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public View getDescView(String title, String desc) {
        if(isAdded()){
            LayoutInflater inflater = LayoutInflater.from(getContext());
            View view = inflater.inflate(R.layout.video_teacher_desc, null);
            TextView tv_title = (TextView) view.findViewById(R.id.tv_video_desc_title);
            TextView tv_desc = (TextView) view.findViewById(R.id.tv_video_desc);
            tv_title.setText("[" + title + "]");
            tv_desc.setText(desc);
            return view;
        }
        return null;
    }

    public void follow() {
        WebBase.follow(videoBean.getUser_id(), new JSONHandler(true, getActivity(),
                "正在关注圈主...") {
            @Override
            public void onSuccess(JSONObject obj) {
                Toast.makeText(getContext(), "关注成功", Toast.LENGTH_SHORT).show();
                videoBean.setFollow(1);
                video_care.setText("已关注");
                video_care.setChecked(true);
            }

            @Override
            public void onFailure(String err_msg) {
                if (videoBean.getFollow()) {
                    video_care.setText("已关注");
                } else {
                    video_care.setText("关注");
                }
                video_care.setChecked(videoBean.getFollow());
                Toast.makeText(getContext(), err_msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 取消关注
     */
    public void unfollow() {
        WebBase.unfollow(videoBean.getUser_id(), new JSONHandler(true,
                getActivity(), "正在取消关注圈主...") {
            @Override
            public void onSuccess(JSONObject obj) {
                video_care.setChecked(false);
                videoBean.setFollow(0);
                video_care.setText("关注");
                Toast.makeText(getContext(), "取消关注成功", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onFailure(String err_msg) {
                Toast.makeText(getContext(), err_msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.video_care:
                if(ShowActivity.isLogin(getActivity())){
                    CheckBox checkBox = (CheckBox) v;
                    if (!checkBox.isChecked()) {
                        unfollow();
                    } else {
                        follow();
                    }
                }
                break;
            case R.id.btn_into_group:
                Group group = new Group();
                group.setId(videoBean.getUser_id());
                ShowActivity.showGroupDetailActivity(getActivity(), group);
                break;
            case R.id.tv_special:
                Video.SeriesVideo seriesVideo= (Video.SeriesVideo) tv_special.getTag();
                if(seriesVideo!=null){
                    Bundle bundle=new Bundle();
                    seriesVideo.setIs_play(1);
                    bundle.putSerializable(IntentKey.VIDEO_SERIES,seriesVideo);
                    ShowActivity.showActivityForResult(activity,bundle,SeriesVideoActivity.class, RequestCode.VIDEO_PLAY);
                }
                break;
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
