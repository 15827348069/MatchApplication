package com.zbmf.newmatch.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.zbmf.newmatch.R;
import com.zbmf.newmatch.adapter.InviteAdapter;
import com.zbmf.newmatch.api.JSONHandler;
import com.zbmf.newmatch.api.WebBase;
import com.zbmf.newmatch.bean.Invite;
import com.zbmf.newmatch.bean.ShareBean;
import com.zbmf.newmatch.common.AppConfig;
import com.zbmf.newmatch.common.Constans;
import com.zbmf.newmatch.util.JSONParse;
import com.zbmf.newmatch.util.MatchSharedUtil;
import com.zbmf.newmatch.util.MyActivityManager;
import com.zbmf.newmatch.view.InviteShareLayout;
import com.zbmf.worklibrary.presenter.BasePresenter;
import com.zbmf.worklibrary.pulltorefresh.PullToRefreshBase;
import com.zbmf.worklibrary.pulltorefresh.PullToRefreshListView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by xuhao on 2017/7/20.
 */

public class InviteActivity extends BaseActivity{
    private InviteShareLayout inviteShareLayout;
    private TextView tv_all_reward,tv_valid_people,tv_people;
    private PullToRefreshListView listView;
    private List<Invite>infolist;
    private InviteAdapter adapter;
    private int page,pages;
    private boolean rush=true;

    @Override
    protected int getLayout() {
        return R.layout.activity_invite_layout;
    }

    @Override
    protected String initTitle() {
        return "我的邀请";
    }

    @Override
    protected void initData(Bundle bundle) {
//添加管理activity
        MyActivityManager.getMyActivityManager().pushAct(this);
        GroupinitView();
        addListener();
        GroupinitData();

    }

    @Override
    protected BasePresenter initPresent() {
        return null;
    }

    public void GroupinitView() {
        inviteShareLayout= (InviteShareLayout) findViewById(R.id.invite_share_layout);
        inviteShareLayout.init(this,new ShareBean(getString(R.string.invite_share_title),
                AppConfig.SHARE_IMG, Constans.SHARE_URL_INVANITE+ MatchSharedUtil.UserId(),
                getString(R.string.invite_share_desc)));
        tv_people= (TextView) findViewById(R.id.tv_people);
        tv_valid_people= (TextView) findViewById(R.id.tv_valid_people);
        tv_all_reward= (TextView) findViewById(R.id.tv_all_reward);
        listView= (PullToRefreshListView) findViewById(R.id.invite_list_view);
        listView.setMode(PullToRefreshBase.Mode.BOTH);
    }

    public void GroupinitData() {
        infolist=new ArrayList<>();
        adapter = new InviteAdapter(this, infolist);
        listView.setAdapter(adapter);
        getInviteList(true);
    }

    public void addListener() {
        listView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                rush=true;
                page=1;
                getInviteList(false);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                page+=1;
                rush=false;
                getInviteList(false);
            }
        });
    }

    private void getInviteList(final boolean show) {
        WebBase.getInviteList(page, new JSONHandler(show, InviteActivity.this, "正在加载数据...") {
            @Override
            public void onSuccess(JSONObject obj) {
                listView.onRefreshComplete();
                Invite invite = JSONParse.getInviteList(obj);
                page=invite.getPage();
                pages = invite.getPages();
                if (invite != null && invite.getInvite_lists() != null){
                    if (rush){
                        infolist.clear();
                    }
                    infolist.addAll(invite.getInvite_lists());
                }
                if (page == pages&&!show) {
                    Toast.makeText(InviteActivity.this, "已加载全部数据", Toast.LENGTH_SHORT).show();
                    listView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                } else{
                    listView.setMode(PullToRefreshBase.Mode.BOTH);
                }

                tv_all_reward.setText(invite.getStat().getMpay() + " 魔方宝");
                tv_people.setText((int) invite.getStat().getTotal() + " 人");
                tv_valid_people.setText((int) invite.getStat().getValid() + " 人");
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(String err_msg) {
                listView.onRefreshComplete();
            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if(inviteShareLayout!=null){
            inviteShareLayout.onNewIntent(intent);
        }
    }


}
