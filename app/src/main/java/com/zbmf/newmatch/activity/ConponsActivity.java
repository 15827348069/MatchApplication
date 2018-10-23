package com.zbmf.newmatch.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;

import com.flyco.tablayout.SlidingTabLayout;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.zbmf.newmatch.R;
import com.zbmf.newmatch.adapter.ViewPageFragmentadapter;
import com.zbmf.newmatch.api.AppUrl;
import com.zbmf.newmatch.fragment.conpons.CanUseConponsFragment;
import com.zbmf.newmatch.fragment.conpons.LoseTimeConponsFragment;
import com.zbmf.newmatch.util.MyActivityManager;
import com.zbmf.newmatch.util.ToastUtils;
import com.zbmf.worklibrary.presenter.BasePresenter;

import java.util.ArrayList;
import java.util.List;


public class ConponsActivity extends BaseActivity implements OnTabSelectListener {
    private SlidingTabLayout mTab;
    private ViewPager mViewpager;
    private List<Fragment> mList;
    private List<String> title_list;

    @Override
    protected int getLayout() {
        return R.layout.activity_coupon;
    }

    @Override
    protected String initTitle() {
        return "优惠券";
    }

    @Override
    protected void initData(Bundle bundle) {
//添加管理activity
        MyActivityManager.getMyActivityManager().pushAct(this);
        GroupinitView();


    }

    @Override
    protected BasePresenter initPresent() {
        return null;
    }

    public void GroupinitView() {
        initFragment();
        setupView();
    }

    private void initFragment() {
        mList = new ArrayList<>();
        title_list = new ArrayList<>();
        title_list.add("已领取");
        title_list.add("已使用");
        title_list.add("已过期");
        mList.add(CanUseConponsFragment.newInstance());
        mList.add(LoseTimeConponsFragment.newInstance(AppUrl.getHistCoupons));
        mList.add(LoseTimeConponsFragment.newInstance(AppUrl.getExpireCoupons));
    }

    private void setupView() {
        mTab = (SlidingTabLayout) findViewById(R.id.tablayout);
        mViewpager = (ViewPager) findViewById(R.id.viewpager);
        ViewPageFragmentadapter adapter = new ViewPageFragmentadapter(getSupportFragmentManager(), title_list, mList);
        mViewpager.setAdapter(adapter);
        mTab.setViewPager(mViewpager);
        mTab.setOnTabSelectListener(this);
        findViewById(R.id.to_get_conpons).setOnClickListener(view ->
                        ToastUtils.showSquareTvToast("领券中心敬请期待"));
        mViewpager.setOffscreenPageLimit(mList.size());
    }

    @Override
    public void onTabSelect(int position) {

    }

    @Override
    public void onTabReselect(int position) {

    }


}
