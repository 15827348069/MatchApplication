package com.zbmf.newmatch.activity;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import com.zbmf.newmatch.R;
import com.zbmf.newmatch.adapter.HistoryDateAdapter;
import com.zbmf.newmatch.bean.Group;
import com.zbmf.newmatch.common.Constans;
import com.zbmf.newmatch.common.IntentKey;
import com.zbmf.newmatch.util.DateUtil;
import com.zbmf.newmatch.util.MyActivityManager;
import com.zbmf.newmatch.util.ShowActivity;
import com.zbmf.worklibrary.presenter.BasePresenter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by xuhao on 2017/2/13.
 */

public class HistoryDateActivity extends BaseActivity {
    private HistoryDateAdapter adapter;
    private ListView history_date_list;
    private Group group_bean;
    private List<Date>infolist;
    private TextView group_title_name;

    @Override
    protected int getLayout() {
        return R.layout.history_days_layout;
    }

    @Override
    protected String initTitle() {
        return "直播历史纪录";
    }

    @Override
    protected void initData(Bundle bundle) {
//添加管理activity
        MyActivityManager.getMyActivityManager().pushAct(this);
        history_date_list= (ListView) findViewById(R.id.history_date_list);

        addListener();
        GroupinitData();
    }

    @Override
    protected BasePresenter initPresent() {
        return null;
    }

    public void GroupinitData() {
        group_bean= (Group) getIntent().getSerializableExtra(IntentKey.GROUP);
        infolist=getDate();
        adapter=new HistoryDateAdapter(this,infolist,group_bean.getNick_name());
        history_date_list.setAdapter(adapter);
        getDate();
    }

    public void addListener() {
        history_date_list.setOnItemClickListener((adapterView, view, i, l) -> {
            group_bean.setLive_history_date(infolist.get(i));
            ShowActivity.showLiveHistoryActivity(HistoryDateActivity.this,group_bean);
        });
    }

    public List<Date> getDate(){
        infolist=new ArrayList<>();
        for(int i = -1; i>= Constans.HISTORY_DAYS; i--){
            infolist.add(DateUtil.afterNDay(i));
        }
        return  infolist;
    }


}
