package com.zbmf.newmatch.fragment;

import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.flyco.tablayout.SlidingTabLayout;
import com.zbmf.newmatch.MainActivity;
import com.zbmf.newmatch.R;
import com.zbmf.newmatch.activity.GroupSearchActivity;
import com.zbmf.newmatch.adapter.ViewPageFragmentadapter;
import com.zbmf.newmatch.adapter.interfaces.LoadFinish;
import com.zbmf.newmatch.api.JSONHandler;
import com.zbmf.newmatch.api.WebBase;
import com.zbmf.newmatch.bean.Group;
import com.zbmf.newmatch.common.Constans;
import com.zbmf.newmatch.db.DBManager;
import com.zbmf.newmatch.db.Database;
import com.zbmf.newmatch.fragment.care.RankTeacherFragment;
import com.zbmf.newmatch.util.JSONParse;
import com.zbmf.newmatch.util.MatchSharedUtil;
import com.zbmf.newmatch.util.ShowActivity;
import com.zbmf.newmatch.view.MyCustomViewpager;
import com.zbmf.newmatch.view.ShowOrHideProgressDialog;
import com.zbmf.worklibrary.listener.ScrollViewChangeListener;
import com.zbmf.worklibrary.presenter.BasePresenter;
import com.zbmf.worklibrary.pulltorefresh.OverscrollHelper;
import com.zbmf.worklibrary.pulltorefresh.PullToRefreshBase;
import com.zbmf.worklibrary.pulltorefresh.PullToRefreshScrollView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * Created by xuhao on 2017/8/16.
 */

public class CareFragments extends BaseFragment implements ViewPager.OnPageChangeListener,
        LoadFinish, /*PullToRefreshScrollView.onScrolls,*/ View.OnClickListener, ScrollViewChangeListener {
    private MainActivity groupActivity;
    private List<Group> infolist;

    private DBManager dbManager;
    private Database db;
    private boolean isFirstIn = true;
    private int select;
    private boolean isLoad=false;

    private PullToRefreshScrollView sc_focus;
    private SlidingTabLayout mTab,care_top_tab_layout;
    public MyCustomViewpager mViewpager;

    private RankTeacherFragment recomedFragment, exclusiveFragment, arrowFragment;

    public static CareFragments newInstance() {
        CareFragments fragment = new CareFragments();
        return fragment;
    }

    @Override
    protected int getLayout() {
        return R.layout.fragment_care;
    }

    @Override
    protected void initView() {
        initCare();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!isLoad){
            initCare();
        }
    }

    private void initCare(){
        if (!isLoad){
            isLoad=true;
        }
        sc_focus = (PullToRefreshScrollView) getView(R.id.care_refresh);
        mTab = (SlidingTabLayout) getView(R.id.care_tab_layout);
        care_top_tab_layout = (SlidingTabLayout)getView(R.id.care_top_tab_layout);
        mViewpager = (MyCustomViewpager) getView(R.id.care_viewpager_teacher);
        TextView tv_title_text = (TextView) getView(R.id.tv_title_text);
        ImageView searchBtn = (ImageView) getView(R.id.searchBtn);
        mViewpager.setScrollble(true);
        //设置fragment页面和tabLayout
        List<Fragment> mList = new ArrayList<>();
        List<String> title_list = Arrays.asList(getResources().getStringArray(R.array.find_teacher));
        //排名
        recomedFragment = RankTeacherFragment.newInstance(Constans.PEOPLE_RECOMED);
        recomedFragment.setCustomViewPage(mViewpager);
        recomedFragment.setLoadFinish(this);
        mViewpager.setObjectForPosition(recomedFragment.getFragmentView(), 0);
        //直播
        exclusiveFragment = RankTeacherFragment.newInstance(Constans.EXCLUSIVE);
        exclusiveFragment.setCustomViewPage(mViewpager);
        exclusiveFragment.setLoadFinish(this);
        mViewpager.setObjectForPosition(exclusiveFragment.getFragmentView(), 1);
        //人气
        arrowFragment = RankTeacherFragment.newInstance(Constans.PEOPLE_ARROW);
        arrowFragment.setCustomViewPage(mViewpager);
        arrowFragment.setLoadFinish(this);
        mViewpager.setObjectForPosition(arrowFragment.getFragmentView(), 2);
        mList.add(recomedFragment);
        mList.add(exclusiveFragment);
        mList.add(arrowFragment);
        ViewPageFragmentadapter mAdapter = new ViewPageFragmentadapter(getActivity().getSupportFragmentManager()
                , title_list, mList);
        mViewpager.setAdapter(mAdapter);
        mViewpager.setOffscreenPageLimit(mList.size());
        mViewpager.setOnPageChangeListener(this);
        mTab.setViewPager(mViewpager);
        care_top_tab_layout.setViewPager(mViewpager);
        //获取数据库对象
        dbManager = new DBManager(getContext());
        db = new Database(getContext());
        groupActivity = (MainActivity) getActivity();
        tv_title_text.setText("圈子");
        searchBtn.setVisibility(View.VISIBLE);
        searchBtn.setOnClickListener(v -> {
            ShowActivity.showActivity(getActivity(), GroupSearchActivity.class);
        });

        infolist = new ArrayList<>();
        new OverscrollHelper().setScrollViewChangeListener(this);
        //有登录
        //初始化圈子的Fragment
//        initCareFragment();
        //设置刷新
        setRefresh();
        initCareFragment();
    }

    public void setData(boolean updata) {
        setinitData(updata);
    }

    private void setRefresh() {
        sc_focus.setMode(PullToRefreshBase.Mode.BOTH);
        sc_focus.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ScrollView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ScrollView> refreshView) {
                RunshList();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ScrollView> refreshView) {
                switch (select) {
                    case 0:
                        recomedFragment.getMore();
                        break;
                    case 1:
                        exclusiveFragment.getMore();
                        break;
                    case 2:
                        arrowFragment.getMore();
                        break;
                }
            }
        });
    }

    private void rush() {
        new Handler().postDelayed(() -> sc_focus.onRefreshComplete(), 300);
    }

    @Override
    protected void initData() {
        isFirstIn = true;
        //如果用户已经登录则获取数据
        String authToken = MatchSharedUtil.AuthToken();
        if (!authToken.isEmpty()) {
            ShowOrHideProgressDialog.showProgressDialog(getActivity(), getActivity(), getString(R.string.loading));
            userGroups();
        }
    }

    @Override
    protected BasePresenter initPresent() {
        return null;
    }

    private void RunshList() {
        String authToken = MatchSharedUtil.AuthToken();
        if (!authToken.isEmpty()) {
            ShowOrHideProgressDialog.showProgressDialog(getActivity(), getActivity(), getString(R.string.loading));
            userGroups();
            switch (select) {
                case 0:
                    recomedFragment.rushList();
                    break;
                case 1:
                    exclusiveFragment.rushList();
                    break;
                case 2:
                    arrowFragment.rushList();
                    break;
            }
        }
    }

    private void initCareFragment() {
        //初始化数据
        if (isFirstIn) {
            if (dbManager == null) {
                dbManager = new DBManager(getContext());
            }
            RunshList();
        }
    }

    private void userGroups() {
        WebBase.userGroups(1, 3, new JSONHandler() {
            @Override
            public void onSuccess(JSONObject obj) {
                ShowOrHideProgressDialog.disMissProgressDialog();
                JSONObject result = obj.optJSONObject("result");
                if (!result.isNull("groups")) {
                    if (isFirstIn) {
                        isFirstIn = false;
                    }
                    if (infolist == null) {
                        infolist = new ArrayList<Group>();
                    } else {
                        infolist.clear();
                    }
                    infolist.addAll(JSONParse.careGroups(result.optJSONArray("groups")));
                    for (Group group1 : infolist) {
                        int liveNo = dbManager.getUnredCount(group1.getId());
                        group1.setUnredcount(liveNo);
                    }
                    for (Group group1 : infolist) {
                        int chatNo = db.getChatUnReadNum(group1.getId())
                                /*+dbManager.getOfflineMsgConunt(Constants.ROOM,group1.getId())*/;
                        group1.setChat(chatNo);
                    }
                    if (infolist.size() == 0) {
                        dbManager.setUnablemessage(null);
                    } else if (infolist.size() < 3) {

                    } else {
                        sc_focus.setVisibility(View.VISIBLE);
                    }
//                    mAdapter.notifyDataSetChanged();
                    groupActivity.setCare_menu_point();
                }
            }

            @Override
            public void onFailure(String err_msg) {
                ShowOrHideProgressDialog.disMissProgressDialog();
                if (checkVa(err_msg)) {
//                    MatchSharedUtil.putAuthToken("");
//                    ((MainActivity) getActivity()).checked();
                } else {
                    Toast.makeText(getActivity(), err_msg, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean checkVa(String err_msg) {
        if (err_msg.equals("用户登录失败或已过期") || err_msg.equals(Constans.NEED_LOGIN)) {
            return true;
        }
        return false;
    }

    @Override
    public void onRush() {
        super.onRush();
        Log.i("===TAG", "--    执行CareFragment的  onRush方法  ");
        if (isFirstIn) {
            if (dbManager == null) {
                dbManager = new DBManager(getContext());
            }
            RunshList();
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        select = position;
        mViewpager.resetHeight(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onFinish() {
        rush();
        switch (select) {
            case 0:
                recomedFragment.setViewPageHeight(0);
                break;
            case 1:
                exclusiveFragment.setViewPageHeight(1);
                break;
            case 2:
                arrowFragment.setViewPageHeight(2);
                break;
        }
    }

    @Override
    public void onScrollChanged(PullToRefreshScrollView scrollView, int x, int y, int oldx, int oldy) {

    }

    @Override
    public void scrollTop() {
    }

    @Override
    public void scrollDown() {
    }

    @Override
    public void scrollBottom() {
    }

    @Override
    public void onScroll(int x, int y) {
        int top = mTab.getTop();
        if (y >= top) {
            if (care_top_tab_layout.getVisibility() == View.GONE) {
                care_top_tab_layout.setVisibility(View.VISIBLE);
            }
        } else {
            if (care_top_tab_layout.getVisibility() == View.VISIBLE) {
                care_top_tab_layout.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void scrollStop() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
           /* case R.id.imv_fans_coupons:
                ShowActivity.showActivity(getActivity(), FansDiscountsActivity.class);
                break;
            case R.id.imv_god_stock:
                ShowActivity.showActivity(getActivity(), GoldStockActivity.class);
                break;*/
            case R.id.search_button:
//                Toast.makeText(groupActivity, "点击了搜索按钮", Toast.LENGTH_SHORT).show();
//                ShowActivity.showActivity(getActivity(), GroupSearchActivity.class);
                break;
        }
    }
}
