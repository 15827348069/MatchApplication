package com.zbmf.newmatch.activity;

import android.os.Bundle;
import android.os.Handler;
import android.widget.ListView;

import com.zbmf.newmatch.R;
import com.zbmf.newmatch.adapter.NewAllMatchAdapter;
import com.zbmf.newmatch.api.ParamsKey;
import com.zbmf.newmatch.bean.MatchNewAllBean;
import com.zbmf.newmatch.listener.MyJoinMatchsView;
import com.zbmf.newmatch.presenter.MyJoinMatchsPresenter;
import com.zbmf.newmatch.util.MatchSharedUtil;
import com.zbmf.newmatch.util.MyActivityManager;
import com.zbmf.newmatch.util.ShowActivity;
import com.zbmf.newmatch.view.ShowOrHideProgressDialog;
import com.zbmf.worklibrary.pulltorefresh.PullToRefreshBase;
import com.zbmf.worklibrary.pulltorefresh.PullToRefreshListView;

import java.util.ArrayList;
import java.util.List;

public class MatchListActivity extends BaseActivity <MyJoinMatchsPresenter> implements MyJoinMatchsView {

    private PullToRefreshListView plvMatchList;
    private int page;
    private NewAllMatchAdapter adapter;
    private int mTotal;
    private Handler mHandler = new Handler();
    private MyJoinMatchsPresenter mMyJoinMatchsPresenter;
    private List<MatchNewAllBean.Result.Matches> matchList = new ArrayList<>();

    @Override
    protected int getLayout() {
        return R.layout.activity_match_list;
    }

    @Override
    protected String initTitle() {
        return "参赛列表";
    }

    @Override
    protected void initData(Bundle bundle) {
        MyActivityManager.getMyActivityManager().pushAct(this);
        plvMatchList = (PullToRefreshListView)getView(R.id.plv_match_list);

        adapter = new NewAllMatchAdapter(this);
        plvMatchList.setAdapter(adapter);

        //下拉刷新
        plvMatchList.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                if (adapter!=null){
                    if (adapter.getList() != null) {
                        adapter.clearList();
                    }
                    page = ParamsKey.D_PAGE;
                    getPresenter().getDatas();
                    ShowOrHideProgressDialog.showProgressDialog(MatchListActivity.this,
                            MatchListActivity.this, getString(R.string.hard_loading));
                }
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                //上拉加载更多
                if (adapter.getList().size() == mTotal) {
                    showToastMsg("没有更多");
                    plvMatchList.setPullLabel("没有更多", PullToRefreshBase.Mode.PULL_FROM_END);
                    mHandler.postDelayed(() -> plvMatchList.onRefreshComplete(), 1000);
                } else {
                    page += 1;
                    //加载更多数据
                    mMyJoinMatchsPresenter.getMyJoinMatchList(String.valueOf(page), MatchSharedUtil.UserId());
                }
            }
        });
        //listView点击事件
        plvMatchList.setOnItemClickListener((adapterView, view, i, l) ->
                ShowActivity.showMatchDetail(MatchListActivity.this, adapter.getItem(i - 1)));


    }

    @Override
    protected MyJoinMatchsPresenter initPresent() {
        mMyJoinMatchsPresenter = new MyJoinMatchsPresenter();
        return mMyJoinMatchsPresenter;
    }

    @Override
    public void RushMatchList(MatchNewAllBean.Result userMatch) {
        ShowOrHideProgressDialog.disMissProgressDialog();
        if (plvMatchList.isRefreshing()) {
            plvMatchList.onRefreshComplete();
        }
        if (userMatch != null) {
            page = userMatch.getPage();
            mTotal = userMatch.getTotal();
            List<MatchNewAllBean.Result.Matches> matches = userMatch.getMatches();
            if (adapter.getList() == null) {
                adapter.setList(matches);
            } else {
                if (page == ParamsKey.D_PAGE) {
                    matchList.addAll(matches);
                    adapter.clearList();
                    adapter.addList(matchList);
                    matchList.clear();
                } else {
                    adapter.addList(matches);
                }
            }
        }
    }

    @Override
    public void RushMatchListErr(String msg) {
        ShowOrHideProgressDialog.disMissProgressDialog();
    }
}
