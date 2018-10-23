package com.zbmf.newmatch.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.flyco.tablayout.SlidingTabLayout;
import com.zbmf.newmatch.R;
import com.zbmf.newmatch.adapter.ViewPageFragmentadapter;
import com.zbmf.newmatch.common.Constans;
import com.zbmf.newmatch.common.HtmlUrl;
import com.zbmf.newmatch.common.IntentKey;
import com.zbmf.newmatch.fragment.BlogFragment;
import com.zbmf.newmatch.fragment.WebFragment;
import com.zbmf.newmatch.util.MyActivityManager;
import com.zbmf.worklibrary.presenter.BasePresenter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * 看股市
 */
public class LookStockActivity extends BaseActivity {

    private SlidingTabLayout mTab;
    private ViewPager mViewpager;
    private List<Fragment> mList;
    private List<String> title_list;
    private int flag;

    @Override
    protected int getLayout() {
        return R.layout.activity_message;
    }

    @Override
    protected String initTitle() {
        return "看股市";
    }

    @Override
    protected void initData(Bundle bundle) {
//添加管理activity
        MyActivityManager.getMyActivityManager().pushAct(this);
        GroupinitView();


        GroupinitData();
    }

    @Override
    protected BasePresenter initPresent() {
        return null;
    }
//    @Override
//    public int getLayoutResId() {
//        return R.layout.activity_message;
//    }

    public void GroupinitView() {
        initFragment();
        mTab = (SlidingTabLayout) findViewById(R.id.tablayout);
        mViewpager = (ViewPager) findViewById(R.id.viewpager);
        mTab.setVisibility(View.VISIBLE);
    }

    public void GroupinitData() {
        ViewPageFragmentadapter adapter = new ViewPageFragmentadapter(getSupportFragmentManager(),
                title_list, mList);
        mViewpager.setAdapter(adapter);
        mTab.setViewPager(mViewpager);
        mViewpager.setOffscreenPageLimit(mList.size());
        Bundle bundle=getIntent().getExtras();
        if(bundle!=null){
            flag=bundle.getInt(IntentKey.FLAG);
            mViewpager.setCurrentItem(flag, false);
        }
    }

    private void initFragment() {
        mList = new ArrayList<>();
        title_list =  Arrays.asList(getResources().getStringArray(R.array.look_stock));
        mList.add(WebFragment.newInstance(HtmlUrl.STOCK_LIVE));
        mList.add(BlogFragment.newInstance(Constans.MFTT));
        mList.add(BlogFragment.newInstance(Constans.ZBMFTT));
    }


}
