package com.zbmf.newmatch.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.zbmf.newmatch.R;
import com.zbmf.newmatch.adapter.AskAdapter;
import com.zbmf.newmatch.api.JSONHandler;
import com.zbmf.newmatch.api.WebBase;
import com.zbmf.newmatch.bean.Ask;
import com.zbmf.newmatch.common.Constans;
import com.zbmf.newmatch.common.IntentKey;
import com.zbmf.newmatch.common.RequestCode;
import com.zbmf.newmatch.util.JSONParse;
import com.zbmf.newmatch.util.MatchSharedUtil;
import com.zbmf.newmatch.util.MessageType;
import com.zbmf.newmatch.util.MyActivityManager;
import com.zbmf.newmatch.util.ShowActivity;
import com.zbmf.worklibrary.presenter.BasePresenter;
import com.zbmf.worklibrary.pulltorefresh.PullToRefreshBase;
import com.zbmf.worklibrary.pulltorefresh.PullToRefreshListView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 我的提问
 */
public class MyQuestionActivity extends BaseActivity implements View.OnClickListener {

    private PullToRefreshListView mListView;
    private AskAdapter mAdapter;
    private List<Ask> asks=null;
    private int PAGE_INDEX, PAGGS;
    public static final int Refresh = 1;
    public static final int LoadMore = 2;
    private boolean isFirst=true;
    private LinearLayout ll_none;
    private TextView no_message_text,right_button,left_button;


    @Override
    protected int getLayout() {
        return R.layout.activity_my_question;
    }

    @Override
    protected String initTitle() {
        return "我的提问";
    }

    @Override
    protected void initData(Bundle bundle) {

        GroupinitView();
        addListener();
        GroupinitData();

    }

    @Override
    protected BasePresenter initPresent() {
        return null;
    }

    public void GroupinitView() {
        //添加管理activity
        MyActivityManager.getMyActivityManager().pushAct(this);
        ll_none = (LinearLayout) findViewById(R.id.ll_none);
        mListView = (PullToRefreshListView) findViewById(R.id.listview);
        mListView.setMode(PullToRefreshBase.Mode.BOTH);
        getView(R.id.imb_send).setVisibility(View.VISIBLE);
        getView(R.id.imb_send).setOnClickListener(this);
        ll_none=(LinearLayout) getView(R.id.ll_none);
        no_message_text=(TextView) getView(R.id.no_message_text);
        right_button=(TextView) getView(R.id.tv_right_button);
        left_button=(TextView) getView(R.id.tv_left_button);
        no_message_text.setText(getString(R.string.no_msg));
        right_button.setVisibility(View.VISIBLE);
        right_button.setText(getString(R.string.to_ask));
        right_button.setOnClickListener(v -> {
            Bundle bundle=new Bundle();
            bundle.putInt(IntentKey.FLAG,1);
            ShowActivity.showActivity(MyQuestionActivity.this,bundle,AskStockSendActivity.class);
        });
        left_button.setVisibility(View.VISIBLE);
        left_button.setText(getString(R.string.find_teacher));
        left_button.setOnClickListener(v ->
                ShowActivity.showActivityForResult(MyQuestionActivity.this,
                        null, FindTeacherActivity.class, RequestCode.STUDY));

    }

    public void GroupinitData() {
        asks = new ArrayList<>();
        mAdapter = new AskAdapter(this,asks);
        mListView.setAdapter(mAdapter);
        userAsks(Refresh);
        Intent answer_intent=new Intent(Constans.USER_RED_NEW_MESSAGE);
        answer_intent.putExtra("type", MessageType.ANSWER);
        this.sendBroadcast(answer_intent);
    }

    public void addListener() {
        mListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                userAsks(Refresh);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                userAsks(LoadMore);
            }
        });
    }
    private void userAsks(final int direction) {

        if (direction == Refresh)
            PAGE_INDEX = 1;
        else
            PAGE_INDEX++;

        WebBase.userAsks(PAGE_INDEX, MatchSharedUtil.AuthToken(),
                new JSONHandler(isFirst,this,getString(R.string.loading)) {
            @Override
            public void onSuccess(JSONObject obj) {
                mListView.onRefreshComplete();
                Ask ask = JSONParse.getAsks(obj);
                if(ask != null){
                    PAGGS = ask.getPages();
                    if (PAGE_INDEX == PAGGS){
                        mListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                    }else{
                        mListView.setMode(PullToRefreshBase.Mode.BOTH);
                    }

                    if(ask.getAsks()!= null && ask.getAsks().size()>0){
                        if (direction == Refresh) {
                            asks.clear();
                        }
                        asks.addAll(ask.getAsks());
                        mAdapter.notifyDataSetChanged();
                    }else{
                        ll_none.setVisibility(View.VISIBLE);
                        mListView.setVisibility(View.GONE);
                    }
                }else{
                    mListView.setVisibility(View.GONE);
                    ll_none.setVisibility(View.VISIBLE);
                }
                if(isFirst){
                    isFirst=false;
                }
            }

            @Override
            public void onFailure(String err_msg) {
                if(isFirst){
                    isFirst=false;
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.imb_send:
                if(ShowActivity.isLogin(this)){
                    Bundle bundle=new Bundle();
                    bundle.putInt(IntentKey.FLAG,1);
                    ShowActivity.showActivity(MyQuestionActivity.this,AskStockSendActivity.class);
                }
                break;
        }
    }
}
