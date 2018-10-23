package com.zbmf.newmatch.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.flyco.tablayout.SlidingTabLayout;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.zbmf.newmatch.R;
import com.zbmf.newmatch.adapter.ViewPageFragmentadapter;
import com.zbmf.newmatch.common.Constans;
import com.zbmf.newmatch.fragment.TieFansFragment;
import com.zbmf.newmatch.listener.TeacherToStudy;
import com.zbmf.newmatch.util.MyActivityManager;
import com.zbmf.worklibrary.presenter.BasePresenter;

import java.util.ArrayList;
import java.util.List;


//import com.zbmf.StockGroup.fragment.TieFansFragment;
//import com.zbmf.StockGroup.fragment.VideoFragment;

/**
 * 我的订阅 -- 铁粉、视频。
 */
public class MySubscripActivity extends BaseActivity implements OnTabSelectListener, TeacherToStudy {

    private SlidingTabLayout mTab;
    private ViewPager mViewpager;
    private List<Fragment> mList;
    private List<String> title_list;

    @Override
    protected int getLayout() {
        return R.layout.activity_message;
    }

    @Override
    protected String initTitle() {
        return "我的订阅";
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
        initFragment();
        mTab = (SlidingTabLayout) findViewById(R.id.tablayout);
        mViewpager = (ViewPager) findViewById(R.id.viewpager);
        mTab.setVisibility(View.VISIBLE);
    }

    public void GroupinitData() {
        ViewPageFragmentadapter adapter = new ViewPageFragmentadapter(getSupportFragmentManager(), title_list, mList);
        mViewpager.setAdapter(adapter);
        mTab.setViewPager(mViewpager);
        mViewpager.setOffscreenPageLimit(mList.size());
        Intent answer_intent=new Intent(Constans.USER_RED_NEW_MESSAGE);
        answer_intent.putExtra("type", Constans.PUSH_BOX_TYPE);
        this.sendBroadcast(answer_intent);
    }

    public void addListener() {
        mTab.setOnTabSelectListener(this);
    }

    private void initFragment() {
        mList = new ArrayList<>();
        title_list = new ArrayList<>();
        title_list.add("铁粉");
//        title_list.add("视频");
//        title_list.add("操盘高手");
        TieFansFragment unusedFragment = TieFansFragment.newInstance();
//        CommitVideoFragment usedFragment = CommitVideoFragment.newInstance();
//        usedFragment.setTeacherToStudy(this);
        mList.add(unusedFragment);
//        mList.add(usedFragment);
//        mList.add(MyTraderFragment.newInstance());
    }

    @Override
    public void onTabSelect(int position) {

    }

    @Override
    public void onTabReselect(int position) {

    }

    @Override
    public void toStudy() {
        setResult(RESULT_OK);
        finish();
    }

}
