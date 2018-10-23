package com.zbmf.newmatch.activity;

import android.os.Bundle;
import android.widget.ListView;

import com.zbmf.newmatch.R;
import com.zbmf.newmatch.adapter.FansDiscountsAdapter;
import com.zbmf.newmatch.api.JSONHandler;
import com.zbmf.newmatch.api.WebBase;
import com.zbmf.newmatch.bean.BoxBean;
import com.zbmf.newmatch.bean.Group;
import com.zbmf.newmatch.listener.OnFansClick;
import com.zbmf.newmatch.util.JSONParse;
import com.zbmf.newmatch.util.MyActivityManager;
import com.zbmf.newmatch.util.ShowActivity;
import com.zbmf.worklibrary.presenter.BasePresenter;
import com.zbmf.worklibrary.pulltorefresh.PullToRefreshBase;
import com.zbmf.worklibrary.pulltorefresh.PullToRefreshListView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xuhao on 2017/8/28.
 */

public class FansDiscountsActivity extends BaseActivity implements OnFansClick {
    private PullToRefreshListView discounts_list;
    private List<Group>infolist;
    private FansDiscountsAdapter adapter;
    private int page,pages;
    private boolean isRush,isFirst=true;

    @Override
    protected int getLayout() {
        return R.layout.activity_discounts_layout;
    }

    @Override
    protected String initTitle() {
        return getString(R.string.fans_discounts);
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
        discounts_list=(PullToRefreshListView) getView(R.id.lv_discounts);
        discounts_list.setMode(PullToRefreshBase.Mode.BOTH);
    }

    public void GroupinitData() {
        infolist=new ArrayList<>();
        adapter=new FansDiscountsAdapter(FansDiscountsActivity.this,infolist);
        adapter.setAddFans(this);
        discounts_list.setAdapter(adapter);
        Rush();
    }

    public void addListener() {
        discounts_list.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                Rush();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                page+=1;
                getDiscounts();
            }
        });
    }
    private void Rush(){
        page=1;
        isRush=true;
        getDiscounts();
    }
    private void getDiscounts(){
        WebBase.coupon(page, new JSONHandler(isFirst,FansDiscountsActivity.this,getString(R.string.loading)) {
            @Override
            public void onSuccess(JSONObject obj) {
                if(!obj.isNull("result")){
                    JSONObject result=obj.optJSONObject("result");
                    page=result.optInt("page");
                    pages=result.optInt("pages");
                    if(isRush){
                        isRush=false;
                        infolist.clear();
                    }
                    if(!result.isNull("groups")){
                        infolist.addAll(JSONParse.getGroupList(result.optJSONArray("groups")));
                        adapter.notifyDataSetChanged();
                    }
                }
                if(isFirst){
                    isFirst=false;
                }
                discounts_list.onRefreshComplete();
            }

            @Override
            public void onFailure(String err_msg) {
                showToast(err_msg);
                discounts_list.onRefreshComplete();
                if(isFirst){
                    isFirst=false;
                }
            }
        });

    }

    @Override
    public void onBox(BoxBean boxBean) {

    }

    @Override
    public void onFans(String groupId) {
        if(ShowActivity.isLogin(this)){
            WebBase.fansInfo(groupId, new JSONHandler() {
                @Override
                public void onSuccess(JSONObject obj) {
                    if(!obj.isNull("group")){
                        Group group = JSONParse.getGroup(obj.optJSONObject("group"));
                        ShowActivity.showFansActivity(FansDiscountsActivity.this, group);
                    }
                }
                @Override
                public void onFailure(String err_msg) {
                    showToast(err_msg);
                }
            });
        }
    }

    @Override
    public void onGroup(String  user) {
        ShowActivity.showGroupDetailActivity(FansDiscountsActivity.this,user);
    }


}
