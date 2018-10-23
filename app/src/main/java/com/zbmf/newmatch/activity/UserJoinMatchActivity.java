package com.zbmf.newmatch.activity;

import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.zbmf.newmatch.R;
import com.zbmf.newmatch.adapter.NewAllMatchAdapter;
import com.zbmf.newmatch.api.ParamsKey;
import com.zbmf.newmatch.bean.MatchNewAllBean;
import com.zbmf.newmatch.common.Constans;
import com.zbmf.newmatch.common.IntentKey;
import com.zbmf.newmatch.listener.IUserJoinMatchView;
import com.zbmf.newmatch.presenter.UserJoinMatchPresenter;
import com.zbmf.newmatch.util.MyActivityManager;
import com.zbmf.newmatch.util.ShowActivity;
import com.zbmf.newmatch.view.ShowOrHideProgressDialog;
import com.zbmf.worklibrary.pulltorefresh.PullToRefreshBase;
import com.zbmf.worklibrary.pulltorefresh.PullToRefreshListView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by pq
 * on 2018/4/28.
 */

public class UserJoinMatchActivity extends BaseActivity<UserJoinMatchPresenter> implements IUserJoinMatchView,AdapterView.OnItemClickListener{

    @BindView(R.id.plv_match_list)
    PullToRefreshListView plvMatchList;
    private int flag;
    private NewAllMatchAdapter adapter;
    private int page;
    private String mUserID;
    private int mTotal;
    private Handler mHandler = new Handler();
    private UserJoinMatchPresenter mUserJoinMatchPresenter;

    @Override
    protected int getLayout() {
        return R.layout.user_join_mtch_activity;
    }

    @Override
    protected String initTitle() {
        return getString(R.string.join_match_list);
    }

    @Override
    protected void initData(Bundle bundle) {
        //添加管理activity
        MyActivityManager.getMyActivityManager().pushAct(this);
        if (bundle!=null){
            mUserID = bundle.getString(IntentKey.USER_ID);
        }
        adapter = new NewAllMatchAdapter(this);
        plvMatchList.setAdapter(adapter);
        plvMatchList.setOnItemClickListener(this);
        ShowOrHideProgressDialog.showProgressDialog(this, this, getString(R.string.hard_loading));
        plvMatchList.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                if (adapter!=null){
                    if (adapter.getList() != null) {
                        adapter.clearList();
                    }
                    page = ParamsKey.D_PAGE;
                    mUserJoinMatchPresenter.getDatas();
                    ShowOrHideProgressDialog.showProgressDialog(UserJoinMatchActivity.this,
                            UserJoinMatchActivity.this, getString(R.string.hard_loading));
                }
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                if (flag== Constans.NEW_MATCH){
                    if (adapter!=null){
                        if (adapter.getList().size()>=10){
                            showToastMsg("没有更多");
                            plvMatchList.setPullLabel("没有更多", PullToRefreshBase.Mode.PULL_FROM_END);
                            mHandler.postDelayed(() -> plvMatchList.onRefreshComplete(), 1000);
                            return;
                        }
                    }
                }
                //上拉加载更多
                if (adapter.getList().size() == mTotal) {
                    showToastMsg("没有更多");
                    plvMatchList.setPullLabel("没有更多", PullToRefreshBase.Mode.PULL_FROM_END);
                    mHandler.postDelayed(() -> plvMatchList.onRefreshComplete(), 1000);
                } else {
                    page += 1;
                    mUserJoinMatchPresenter.getUserMatch(String.valueOf(page),mUserID);
                }
            }
        });
    }

    @Override
    protected UserJoinMatchPresenter initPresent() {
        mUserJoinMatchPresenter = new UserJoinMatchPresenter(mUserID);
        return mUserJoinMatchPresenter;
    }
private List<MatchNewAllBean.Result.Matches> newMatches =new ArrayList<>();
    @Override
    public void RushMatchList(MatchNewAllBean.Result result) {
        ShowOrHideProgressDialog.disMissProgressDialog();
        if (plvMatchList.isRefreshing()) {
            plvMatchList.onRefreshComplete();
        }
        page = result.getPage();
        mTotal = result.getTotal();
        List<MatchNewAllBean.Result.Matches> matches = result.getMatches();
        if (adapter.getList() == null) {
           adapter.setList(matches);
        } else {
           if (page==1){
               newMatches.addAll(matches);
               adapter.clearList();
               adapter.addList(newMatches);
               newMatches.clear();
           }else {
               adapter.addList(matches);
           }
        }
    }

    @Override
    public void RushMatchListErr(String msg) {
        ShowOrHideProgressDialog.disMissProgressDialog();
        if (plvMatchList.isRefreshing()) {
            plvMatchList.onRefreshComplete();
        }
        if (!TextUtils.isEmpty(msg)) {
            showToast(getString(R.string.login_err_tip));
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        MatchNewAllBean.Result.Matches item = adapter.getItem(position - 1);
        ShowActivity.showUserMatchDetail(this,item);
    }
}
