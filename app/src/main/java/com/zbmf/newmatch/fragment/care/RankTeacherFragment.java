package com.zbmf.newmatch.fragment.care;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.zbmf.newmatch.R;
import com.zbmf.newmatch.activity.LoginActivity;
import com.zbmf.newmatch.adapter.RecommendAdapter;
import com.zbmf.newmatch.adapter.interfaces.LoadFinish;
import com.zbmf.newmatch.api.JSONHandler;
import com.zbmf.newmatch.api.WebBase;
import com.zbmf.newmatch.bean.Group;
import com.zbmf.newmatch.bean.RecommendTeacherBean;
import com.zbmf.newmatch.common.Constans;
import com.zbmf.newmatch.common.IntentKey;
import com.zbmf.newmatch.fragment.BaseFragment;
import com.zbmf.newmatch.listener.IGroupCareView;
import com.zbmf.newmatch.presenter.GroupCarePresenter;
import com.zbmf.newmatch.util.JSONParse;
import com.zbmf.newmatch.util.MatchSharedUtil;
import com.zbmf.newmatch.util.SettingDefaultsManager;
import com.zbmf.newmatch.util.ShowActivity;
import com.zbmf.newmatch.view.ListViewForScrollView;
import com.zbmf.newmatch.view.MyCustomViewpager;
import com.zbmf.newmatch.view.ShowOrHideProgressDialog;
import com.zbmf.worklibrary.presenter.BasePresenter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xuhao
 * on 2017/8/16.
 */

public class RankTeacherFragment extends BaseFragment/*<GroupCarePresenter>*/ implements
        RecommendAdapter.OnCareClink/*, IGroupCareView */{
    private ListViewForScrollView recommend_list;
    private RecommendAdapter adapter;
    private List<Group> infolist;
    private int page, pages;
    private int flags;
    private boolean isRush, isFirst = true;
    private MyCustomViewpager customViewpager;
//    private GroupCarePresenter mGroupCarePresenter;

    public void setCustomViewPage(MyCustomViewpager customViewPage) {
        this.customViewpager = customViewPage;
    }

    public static RankTeacherFragment newInstance(int flag) {
        RankTeacherFragment fragment = new RankTeacherFragment();
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
        return R.layout.fragment_care_fans_layout;
    }

    @Override
    protected void initView() {
        recommend_list = (ListViewForScrollView)getView(R.id.lv_focus);
        infolist = new ArrayList<>();
        adapter = new RecommendAdapter(getActivity(), infolist);
        adapter.setOnCareClink(this);
        recommend_list.setAdapter(adapter);
        recommend_list.setOnItemClickListener((parent, view, position, id) ->
                ShowActivity.showGroupDetailActivity(getActivity(), infolist.get(position)));
    }

    @Override
    protected void initData() {
        if (!MatchSharedUtil.AuthToken().isEmpty()) {
            page = 1;
            isRush = true;
            getRecommend();
        }
    }

    @Override
    protected BasePresenter initPresent() {
        return null;
    }

//    @Override
//    protected GroupCarePresenter initPresent() {
//        mGroupCarePresenter = new GroupCarePresenter();
//        return mGroupCarePresenter;
//    }

    public void rushList() {
        if (!MatchSharedUtil.AuthToken().isEmpty()) {
            page = 1;
            isRush = true;
            getRecommend();
        }
    }

    public void getMore() {
        page += 1;
        getRecommend();
    }

    public void setViewPageHeight(int position) {
        if (customViewpager != null) {
            customViewpager.setObjectForPosition(getFragmentView(), position);
            customViewpager.resetHeight(position);
        }
    }

    @Override
    public void onCareClink(final int position) {
        if (MatchSharedUtil.AuthToken() == null || MatchSharedUtil.AuthToken().equals("")) {
            showToast("登陆后才可关注！");
            infolist.get(position).setIs_recommend(0);
            adapter.notifyDataSetChanged();
            ShowActivity.showActivity(getActivity(), LoginActivity.class);
        } else {
            String group_id = infolist.get(position).getId();
            ShowOrHideProgressDialog.showProgressDialog(getActivity(), getActivity(), getString(R.string.care_group));
            //调用关注圈子的代码
//            mGroupCarePresenter.careGroup(group_id, position);
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
//        mGroupCarePresenter.getRecommendTeacher(flags, page);
        WebBase.getTeaCher(flags, page, new JSONHandler() {
            @Override
            public void onSuccess(JSONObject obj) {
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
                if (loadFinish != null) {
                    loadFinish.onFinish();
                }
                isFirst = false;
            }

            @Override
            public void onFailure(String err_msg) {
                if (loadFinish != null) {
                    loadFinish.onFinish();
                }
            }
        });
    }

    private LoadFinish loadFinish;

    public void setLoadFinish(LoadFinish loadFinish) {
        this.loadFinish = loadFinish;
    }

//    @Override
//    public void onSucceed(String msg, int position) {
//        ShowOrHideProgressDialog.disMissProgressDialog();
//        showToast("关注成功");
//        infolist.get(position).setIs_recommend(1);
//        adapter.notifyDataSetChanged();
//    }

//    //获取到的推荐的结果
//    @Override
//    public void onRecommendResult(RecommendTeacherBean.RecommendResult reslut) {
//        page = reslut.getPage();
//        pages = reslut.getPages();
//        List<Group> groups = reslut.getGroups();
//        if (isRush) {
//            infolist.clear();
//            isRush = false;
//        }
//        infolist.addAll(groups);
//        adapter.notifyDataSetChanged();
//        if (loadFinish != null) {
//            loadFinish.onFinish();
//        }
//        isFirst = false;
//    }
//
//    @Override
//    public void onFail(String msg, int position) {
//        ShowOrHideProgressDialog.disMissProgressDialog();
//        showToast(msg);
//        infolist.get(position).setIs_recommend(0);
//        adapter.notifyDataSetChanged();
//
//        if (loadFinish != null) {
//            loadFinish.onFinish();
//        }
//    }
}
