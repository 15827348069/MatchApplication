package com.zbmf.newmatch.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flyco.tablayout.SlidingTabLayout;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.zbmf.newmatch.R;
import com.zbmf.newmatch.adapter.ViewPageFragmentadapter;
import com.zbmf.newmatch.common.Constans;
import com.zbmf.newmatch.common.IntentKey;
import com.zbmf.newmatch.fragment.teacher.RankingFragment;
import com.zbmf.newmatch.listener.TeacherToStudy;
import com.zbmf.newmatch.util.MyActivityManager;
import com.zbmf.newmatch.util.ShowActivity;
import com.zbmf.newmatch.view.RoundedCornerImageView;
import com.zbmf.worklibrary.presenter.BasePresenter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;

/**
 * Created by xuhao
 * on 2017/8/16.
 */

public class FindTeacherActivity extends BaseActivity implements OnTabSelectListener, TeacherToStudy, View.OnClickListener {
    @BindView(R.id.action_bar_layout)
    View action_bar_layout;
    @BindView(R.id.group_title_return)
    ImageButton group_title_return;
    @BindView(R.id.group_title_avatar)
    RoundedCornerImageView group_title_avatar;
    @BindView(R.id.group_title_name)
    TextView group_title_name;
    @BindView(R.id.group_tiitle_share)
    ImageButton group_tiitle_share;
    @BindView(R.id.group_title_tw)
    ImageButton group_title_tw;
    @BindView(R.id.group_title_right_button)
    Button group_title_right_button;
    @BindView(R.id.msgCount)
    TextView msgCount;
    @BindView(R.id.rightTipR)
    FrameLayout rightTipR;
    @BindView(R.id.search_button)
    ImageButton search_button;
    @BindView(R.id.cause_care_about_blog_button)
    ImageButton cause_care_about_blog_button;
    @BindView(R.id.imb_stock_mode)
    ImageButton imb_stock_mode;
    @BindView(R.id.care_about_blog_button)
    ImageButton care_about_blog_button;
    @BindView(R.id.blog_textsize_setting)
    ImageButton blog_textsize_setting;
    @BindView(R.id.imb_send)
    ImageButton imb_send;
    @BindView(R.id.imb_msg)
    ImageButton imb_msg;
    @BindView(R.id.title_layout)
    RelativeLayout title_layout;
    @BindView(R.id.title_layout_id)
    LinearLayout title_layout_id;
    @BindView(R.id.teacher_tab_layout)
    SlidingTabLayout mTab;
    @BindView(R.id.viewpager_teacher)
    ViewPager mViewpager;
    private List<Fragment> mList;
    private List<String> title_list;

    private void initFragment() {
        mList = new ArrayList<>();
        title_list = Arrays.asList(getResources().getStringArray(R.array.find_teacher));
        mList.add(RankingFragment.newInstance(Constans.PEOPLE_RECOMED));//特别推荐
        mList.add(RankingFragment.newInstance(Constans.EXCLUSIVE));//独家
        mList.add(RankingFragment.newInstance(Constans.PEOPLE_ARROW));//人气推荐
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

    @Override
    protected int getLayout() {
        return R.layout.activity_find_teacher;
    }

    @Override
    protected String initTitle() {
        return getString(R.string.group);
    }

    @Override
    protected void initData(Bundle bundle) {
        //添加管理activity
        MyActivityManager.getMyActivityManager().pushAct(this);
        initFragment();
        search_button.setVisibility(View.VISIBLE);
        search_button.setOnClickListener(this);

        //添加监听事件
        mTab.setOnTabSelectListener(this);

        ViewPageFragmentadapter adapter = new ViewPageFragmentadapter(getSupportFragmentManager(), title_list, mList);
        mViewpager.setAdapter(adapter);
        mTab.setViewPager(mViewpager);
        mViewpager.setOffscreenPageLimit(mList.size());
        if (bundle != null) {
            int flag = bundle.getInt(IntentKey.FLAG);
            mViewpager.setCurrentItem(flag, false);
        }
    }

    @Override
    protected BasePresenter initPresent() {
        return null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.search_button:
                ShowActivity.showActivity(FindTeacherActivity.this, SearchActivity.class);
                break;
        }
    }
}
