package com.zbmf.newmatch.fragment.rank;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.widget.ListView;

import com.zbmf.newmatch.R;
import com.zbmf.newmatch.activity.LoginActivity;
import com.zbmf.newmatch.activity.MatchDetailActivity;
import com.zbmf.newmatch.adapter.MatchRankAdapter;
import com.zbmf.newmatch.bean.MatchRank;
import com.zbmf.newmatch.common.Constans;
import com.zbmf.newmatch.common.IntentKey;
import com.zbmf.newmatch.fragment.BaseFragment;
import com.zbmf.newmatch.listener.IMatchRankView;
import com.zbmf.newmatch.listener.LastUpdateTime;
import com.zbmf.newmatch.presenter.MatchRankPresenter;
import com.zbmf.newmatch.util.ShowActivity;
import com.zbmf.newmatch.util.SkipMatchDetailEvent;
import com.zbmf.newmatch.view.ShowOrHideProgressDialog;
import com.zbmf.worklibrary.pulltorefresh.PullToRefreshBase;
import com.zbmf.worklibrary.pulltorefresh.PullToRefreshListView;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.BindView;

/**
 * Created by xuhao
 * on 2017/12/1.
 */

public class MatchRankFragment extends BaseFragment<MatchRankPresenter> implements IMatchRankView {
    @BindView(R.id.new_rank_listview)
    PullToRefreshListView newRankListview;
    private String order, match_id;
    private MatchRankAdapter adapter;
    private int mPage;
    private int mTotal;
    private Handler mHandler = new Handler();

    public static MatchRankFragment newInstance(String match_id, int flag, String myFlag, String matchName) {
        MatchRankFragment matchRankFragment = new MatchRankFragment();
        Bundle bundle = new Bundle();
        bundle.putString(IntentKey.MATCH_ID, match_id);
        bundle.putInt(IntentKey.FLAG, flag);
        bundle.putString(IntentKey.MY_FLAG, myFlag);
        bundle.putString(IntentKey.MATCH_NAME, matchName);
        matchRankFragment.setArguments(bundle);
        return matchRankFragment;
    }

    @Override
    protected int getLayout() {
        return R.layout.fragment_match_rank_layout;
    }

    @Override
    protected void initView() {
        ShowOrHideProgressDialog.showProgressDialog(getActivity(), getActivity(), getString(R.string.hard_loading));
        newRankListview.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                if (adapter.getList() != null) {
                    adapter.clearList();
                }
                mPage = 1;
                getPresenter().setFirst(true);
                getPresenter().getDatas();
                ShowOrHideProgressDialog.showProgressDialog(getActivity(), getActivity(), getString(R.string.hard_loading));
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                if (adapter.getList().size() < mTotal) {
                    mPage += 1;
                    getPresenter().setPage(mPage);
                    getPresenter().loadMore();
                } else {
                    showToast(getString(R.string.nomore_loading));
                    mHandler.postDelayed(() -> newRankListview.onRefreshComplete(), 1000);
                }
            }
        });
        newRankListview.setOnItemClickListener((adapterView, view, i, l) -> {
            MatchRank.Result.Yields.Last_deal last_deal = adapter.getItem(i - 1).getLast_deal();
            String myFlag = getArguments().getString(IntentKey.MY_FLAG);
            String matchName = getArguments().getString(IntentKey.MATCH_NAME);
            if (!TextUtils.isEmpty(myFlag) && myFlag.equals(IntentKey.MY_FLAG)) {
                if (matchRank!=null){
                    MatchRank.Result.Yields yields = matchRank.getResult().getYields().get(i - 1);
                    EventBus.getDefault().post(new SkipMatchDetailEvent(matchName, match_id, myFlag, yields));
                    startActivity(new Intent(getActivity(), MatchDetailActivity.class));
                }
            } else {
                ShowActivity.showStockDetail(getActivity(), last_deal, match_id);
            }
        });
    }

    @Override
    protected void initData() {
        adapter = new MatchRankAdapter(getActivity());
        newRankListview.setAdapter(adapter);
    }

    @Override
    protected MatchRankPresenter initPresent() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            int flag = getArguments().getInt(IntentKey.FLAG);
            switch (flag) {
                case Constans.LASTLY_DEALS://最新交易
                case Constans.HOLDER_SUPERIOR:
                    order = "-1";
                    break;
                case Constans.DAY_RANK://日榜
                    order = "1";
                    break;
                case Constans.WEEK_RANK:
                    order = "7";
                    break;
                case Constans.MOUNTH_RANK:
                    order = "30";
                    break;
                case Constans.ALL_RANK:
                    order = "0";
                    break;
            }
            if (adapter != null) {
                adapter.setFlag(flag);
            }
            match_id = bundle.getString(IntentKey.MATCH_ID);
        }
        return new MatchRankPresenter(match_id, order);
    }

    private MatchRank matchRank;

    @Override
    public void RushDealList(MatchRank matchRank/*List<Yield> yieldList*/) {
        this.matchRank = matchRank;
        ShowOrHideProgressDialog.disMissProgressDialog();
        if (newRankListview.isRefreshing()) {
            newRankListview.onRefreshComplete();
        }
        if (mLastUpdateTime != null) {
            mLastUpdateTime.lastUpdateTIme(matchRank.getLast_update());
        }
        MatchRank.Result result = matchRank.getResult();
        assert result != null;
        mPage = result.getPage();
        mTotal = result.getTotal();
        List<MatchRank.Result.Yields> yields = result.getYields();
        assert yields != null;
        if (adapter.getList() == null) {
            adapter.setList(yields);
        } else {
            if (mTotal > adapter.getList().size()) {
                adapter.addList(yields);
            }
        }
    }

    @Override
    public void rushErr(String msg) {
        ShowOrHideProgressDialog.disMissProgressDialog();
        if (!TextUtils.isEmpty(msg)) {
            if (ShowActivity.checkLoginStatus(getActivity(), msg)) {
                mHandler.postDelayed(() -> {
                    ShowActivity.showActivity(getActivity(), LoginActivity.class);
                    showToast(getString(R.string.need_login));
                    getActivity().finish();
                }, 1000);
            }
        }
    }

    private LastUpdateTime mLastUpdateTime;

    public void setLastUpdateTime(LastUpdateTime lastUpdateTime) {
        this.mLastUpdateTime = lastUpdateTime;
    }
}
