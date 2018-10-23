package com.zbmf.newmatch.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.zbmf.newmatch.R;
import com.zbmf.newmatch.adapter.ScreenMessageAdapter;
import com.zbmf.newmatch.api.JSONHandler;
import com.zbmf.newmatch.api.WebBase;
import com.zbmf.newmatch.bean.LiveTypeMessage;
import com.zbmf.newmatch.bean.ScreenMessage;
import com.zbmf.newmatch.util.JSONParse;
import com.zbmf.newmatch.util.LogUtil;
import com.zbmf.newmatch.util.MyActivityManager;
import com.zbmf.newmatch.view.GroupTextView;
import com.zbmf.worklibrary.presenter.BasePresenter;
import com.zbmf.worklibrary.pulltorefresh.PullToRefreshBase;
import com.zbmf.worklibrary.pulltorefresh.PullToRefreshListView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xuhao on 2017/9/27.
 */

public class StockMessageActivity extends BaseActivity implements GroupTextView.OnTextClickListener {
    private ScreenMessageAdapter adapter;
    private PullToRefreshListView pullToRefreshListView;
    private List<ScreenMessage>infolst;
    private int page=1,pages;
    private boolean isRush;
    private LinearLayout ll_none;
    private TextView no_message_text;

    @Override
    protected int getLayout() {
        return R.layout.activity_message_layout;
    }

    @Override
    protected String initTitle() {
        return getString(R.string.message);
    }

    @Override
    protected void initData(Bundle bundle) {
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
        pullToRefreshListView=(PullToRefreshListView) getView(R.id.message_list_id);
        ll_none=(LinearLayout) getView(R.id.ll_none);
        no_message_text=(TextView) getView(R.id.no_message_text);
        no_message_text.setText("暂无消息");
    }

    public void GroupinitData() {
        infolst=new ArrayList<>();
        adapter=new ScreenMessageAdapter(this,infolst);
        adapter.setOnTextClickListener(this);
        pullToRefreshListView.setAdapter(adapter);
        pullToRefreshListView.setMode(PullToRefreshBase.Mode.BOTH);
        getMessage();
    }

    public void addListener() {
        pullToRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                isRush=true;
                page=1;
                getMessage();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                page+=1;
                getMessage();
            }
        });
    }
    private void getMessage(){
        WebBase.getNotices(page,new JSONHandler() {
            @Override
            public void onSuccess(JSONObject obj) {
                pullToRefreshListView.onRefreshComplete();
                if(isRush){
                    isRush=false;
                    infolst.clear();
                }
                page=obj.optInt("page");
                pages=obj.optInt("pages");
                if(!obj.isNull("screen")){
                    infolst.addAll(JSONParse.getScreenMessageList(obj.optJSONArray("screen")));
                }
                if(infolst.size()==0){
                    pullToRefreshListView.setVisibility(View.GONE);
                    ll_none.setVisibility(View.VISIBLE);
                }else{
                    ll_none.setVisibility(View.GONE);
                    pullToRefreshListView.setVisibility(View.VISIBLE);
                    adapter.notifyDataSetChanged();
                }
                if(page==pages){
                    pullToRefreshListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                }else{
                    pullToRefreshListView.setMode(PullToRefreshBase.Mode.BOTH);
                }
            }

            @Override
            public void onFailure(String err_msg) {

            }
        });
    }

    @Override
    public void OnTextClickListener(LiveTypeMessage message) {
        LogUtil.e(message.toString());
        switch (message.getMessage_type()){
            case "screen_message":
//                WebBase.loadScreenProduct(message.getMessage(), new JSONHandler() {
//                    @Override
//                    public void onSuccess(JSONObject obj) {
//                        Screen screen= JSONParse.getScreen(obj);
//                        if(screen!=null){
//                            Bundle bundle=new Bundle();
//                            bundle.putSerializable(IntentKey.SCREEN,screen);
//                            ShowActivity.showActivity(StockMessageActivity.this,bundle,ScreenDetailActivity.class);
//                        }
//                    }
//                    @Override
//                    public void onFailure(String err_msg) {
//
//                    }
//                });
                break;
        }

    }

}
