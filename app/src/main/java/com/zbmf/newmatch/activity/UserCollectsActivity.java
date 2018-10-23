package com.zbmf.newmatch.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.flyco.tablayout.SlidingTabLayout;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.zbmf.newmatch.R;
import com.zbmf.newmatch.adapter.ViewPageFragmentadapter;
import com.zbmf.newmatch.fragment.UserCollectsBlogFragment;
import com.zbmf.newmatch.util.MyActivityManager;
import com.zbmf.worklibrary.presenter.BasePresenter;

import java.util.ArrayList;
import java.util.List;


//import com.zbmf.StockGroup.fragment.TieFansFragment;
//import com.zbmf.StockGroup.fragment.VideoFragment;

/**
 * 我的订阅 -- 铁粉、视频。
 */
public class UserCollectsActivity extends BaseActivity implements OnTabSelectListener {

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
        return "我的收藏";
    }

    @Override
    protected void initData(Bundle bundle) {
//添加管理activity
        MyActivityManager.getMyActivityManager().pushAct(this);
        setupView();
        addListener();

    }

    @Override
    protected BasePresenter initPresent() {
        return null;
    }

    public void addListener() {
        mTab.setOnTabSelectListener(this);
    }

    private void setupView() {
        mList = new ArrayList<>();
        title_list = new ArrayList<>();
        title_list.add("文章");
//        title_list.add("视频");
        mList.add(UserCollectsBlogFragment.newInstance());
//        mList.add(UserCollectsVedioFragment.newInstance());
        mTab = (SlidingTabLayout) findViewById(R.id.tablayout);
        mViewpager = (ViewPager) findViewById(R.id.viewpager);
        ViewPageFragmentadapter adapter = new ViewPageFragmentadapter(getSupportFragmentManager(), title_list, mList);
        mViewpager.setAdapter(adapter);
        mTab.setViewPager(mViewpager);
        mTab.setVisibility(View.GONE);
    }

    @Override
    public void onTabSelect(int position) {

    }

    @Override
    public void onTabReselect(int position) {

    }


}
