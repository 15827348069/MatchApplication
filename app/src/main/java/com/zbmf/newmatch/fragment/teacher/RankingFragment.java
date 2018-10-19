package com.zbmf.newmatch.fragment.teacher;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.zbmf.newmatch.R;
import com.zbmf.newmatch.activity.LoginActivity;
import com.zbmf.newmatch.adapter.RecommendAdapter;
import com.zbmf.newmatch.api.JSONHandler;
import com.zbmf.newmatch.api.WebBase;
import com.zbmf.newmatch.bean.Group;
import com.zbmf.newmatch.common.Constans;
import com.zbmf.newmatch.common.IntentKey;
import com.zbmf.newmatch.fragment.BaseFragment;
import com.zbmf.newmatch.listener.TeacherToStudy;
import com.zbmf.newmatch.util.JSONParse;
import com.zbmf.newmatch.util.SettingDefaultsManager;
import com.zbmf.newmatch.util.ShowActivity;
import com.zbmf.worklibrary.presenter.BasePresenter;
import com.zbmf.worklibrary.pulltorefresh.PullToRefreshBase;
import com.zbmf.worklibrary.pulltorefresh.PullToRefreshListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by xuhao on 2017/8/16.
 */

public class RankingFragment extends BaseFragment implements RecommendAdapter.OnCareClink, View.OnClickListener {
    @BindView(R.id.recommend_list)
    PullToRefreshListView recommend_list;
    @BindView(R.id.tv_to_study)
    TextView tv_to_study;
    @BindView(R.id.no_message_layout)
    LinearLayout no_message_layout;
    private RecommendAdapter adapter;
    private List<Group> infolist;
    private int page, pages;
    private int flags;
    private boolean isRush, isFirst = true;

    private TeacherToStudy teacherToStudy;

    public RankingFragment setTeacherToStudy(TeacherToStudy teacherToStudy) {
        this.teacherToStudy = teacherToStudy;
        return this;
    }

    public static RankingFragment newInstance(int flag) {
        RankingFragment fragment = new RankingFragment();
        Bundle args = new Bundle();
        args.putInt(IntentKey.FLAG, flag);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            flags = bundle.getInt(IntentKey.FLAG);
        }
    }

    @Override
    protected int getLayout() {
        return R.layout.fragment_ranking;
    }

    @Override
    protected void initView() {
        tv_to_study.setOnClickListener(this);
        infolist = new ArrayList<>();
        adapter = new RecommendAdapter(getActivity(), infolist);
        adapter.setOnCareClink(this);
        recommend_list.setAdapter(adapter);
        recommend_list.setOnItemClickListener((parent, view, position, id) -> {
            Group groupbean = infolist.get(position - 1);
            ShowActivity.showGroupDetailActivity(getActivity(), groupbean);
        });
    }

    @Override
    protected void initData() {
        page = 1;
        isRush = true;
        setRecommend_list();
        getRecommend();
    }

    @Override
    protected BasePresenter initPresent() {
        return null;
    }

    private void setRecommend_list() {
        if (flags == Constans.NOW_LIVE) {
            recommend_list.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
            recommend_list.setOnRefreshListener(refreshView -> initData());
        } else {
            recommend_list.setMode(PullToRefreshBase.Mode.BOTH);
            recommend_list.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
                @Override
                public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                    //下拉刷新
                    initData();
                }

                @Override
                public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                    //上拉加载
                    page += 1;
                    getRecommend();
                }
            });
        }
    }

    @Override
    public void onCareClink(final int position) {
        if (SettingDefaultsManager.getInstance().authToken() == null || SettingDefaultsManager.getInstance().authToken().equals("")) {
            showToast("登陆后才可关注！");
            infolist.get(position).setIs_recommend(0);
            adapter.notifyDataSetChanged();
            ShowActivity.showActivity(getActivity(), LoginActivity.class);
        } else {
            String group_id = infolist.get(position).getId();
            WebBase.follow(group_id, new JSONHandler(true, getActivity(), "正在关注圈主...") {
                @Override
                public void onSuccess(JSONObject obj) {
                    showToast("关注成功");
                    infolist.get(position).setIs_recommend(1);
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onFailure(String err_msg) {
                    showToast(err_msg);
                    infolist.get(position).setIs_recommend(0);
                    adapter.notifyDataSetChanged();
                }
            });
        }
    }

    private void getRecommend() {
        WebBase.getTeaCher(flags, page, new JSONHandler(isFirst, getActivity(), getResources().getString(R.string.loading)) {
            @Override
            public void onSuccess(JSONObject obj) {
                recommend_list.onRefreshComplete();
                JSONArray recommend = null;
                if (obj.isNull("result")) {
                    if (!obj.isNull("groups")) {
                        recommend = obj.optJSONArray("groups");
                    }
                } else {
                    JSONObject result = obj.optJSONObject("result");
                    page = result.optInt("page");
                    pages = result.optInt("pages");
                    if (!result.isNull("groups")) {
                        recommend = result.optJSONArray("groups");
                    }
                }
                if (isRush) {
                    infolist.clear();
                    isRush = false;
                }
                if (recommend != null) {
                    infolist.addAll(JSONParse.getGroupList(recommend));
                    adapter.notifyDataSetChanged();
                }
                if (page == pages && !isFirst) {
                    showToast("已加载全部数据");
                    recommend_list.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                }
                if (flags == Constans.NOW_LIVE && infolist.size() == 0) {
                    no_message_layout.setVisibility(View.VISIBLE);
                } else {
                    no_message_layout.setVisibility(View.GONE);
                }
                isFirst = false;
            }

            @Override
            public void onFailure(String err_msg) {
                recommend_list.onRefreshComplete();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_to_study:
                if (teacherToStudy != null) {
                    teacherToStudy.toStudy();
                }
                break;
        }
    }
}
