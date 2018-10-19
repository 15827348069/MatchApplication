package com.zbmf.newmatch.fragment;

import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.flyco.tablayout.SlidingTabLayout;
import com.zbmf.newmatch.MainActivity;
import com.zbmf.newmatch.R;
import com.zbmf.newmatch.activity.SearchActivity;
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
import com.zbmf.newmatch.util.SettingDefaultsManager;
import com.zbmf.newmatch.util.ShowActivity;
import com.zbmf.newmatch.view.MyCustomViewpager;
import com.zbmf.newmatch.view.RoundedCornerImageView;
import com.zbmf.worklibrary.listener.ScrollViewChangeListener;
import com.zbmf.worklibrary.presenter.BasePresenter;
import com.zbmf.worklibrary.pulltorefresh.OverscrollHelper;
import com.zbmf.worklibrary.pulltorefresh.PullToRefreshBase;
import com.zbmf.worklibrary.pulltorefresh.PullToRefreshScrollView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;


/**
 * Created by xuhao
 * on 2017/8/16.
 * 圈子 fragment
 */

public class CareFragments extends BaseFragment implements ViewPager.OnPageChangeListener, LoadFinish,
        /*PullToRefreshScrollView.onScrolls,*/ View.OnClickListener, ScrollViewChangeListener {
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
    @BindView(R.id.care_tab_layout)
    SlidingTabLayout mTab;
    @BindView(R.id.care_viewpager_teacher)
    MyCustomViewpager care_viewpager_teacher;
    @BindView(R.id.care_refresh)
    PullToRefreshScrollView sc_focus;
    @BindView(R.id.care_top_tab_layout)
    SlidingTabLayout care_top_tab_layout;
    private MainActivity groupActivity;

    public MyCustomViewpager mViewpager;
    private List<Fragment> mList;
    private List<String> title_list;
    private DBManager dbManager;
    private Database db;
    private boolean isFirstIn = true;
    private RankTeacherFragment recomedFragment, exclusiveFragment, arrowFragment;
    private int select;
    private List<Group> infolist;
    private ViewPageFragmentadapter mAdapter;


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
        dbManager = new DBManager(getContext());
        db = new Database(getContext());
        groupActivity = (MainActivity) getActivity();
        group_title_name.setText("圈子");

        group_title_return.setVisibility(View.GONE);
        search_button.setVisibility(View.VISIBLE);
        search_button.setOnClickListener(this);

        infolist = new ArrayList<>();
        new OverscrollHelper().setScrollViewChangeListener(this);

        initCareFragment();

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
//        sc_focus.setOnScroll(this);
    }

    private void rush() {
        new Handler().postDelayed(() -> sc_focus.onRefreshComplete(), 300);
    }

    @Override
    protected void initData() {
        isFirstIn = true;
        if (!SettingDefaultsManager.getInstance().authToken().isEmpty()) {
            userGroups();
        }
    }

    @Override
    protected BasePresenter initPresent() {
        return null;
    }

    private void RunshList() {
        if (!SettingDefaultsManager.getInstance().authToken().isEmpty()) {
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

    public void setData(boolean updata){
        // TODO: 2018/10/18
//        setinitData(updata);
    }


    private void initCareFragment() {
        mList = new ArrayList<>();
        title_list = Arrays.asList(getResources().getStringArray(R.array.find_teacher));

        recomedFragment = RankTeacherFragment.newInstance(Constans.PEOPLE_RECOMED);
        recomedFragment.setCustomViewPage(mViewpager);
        recomedFragment.setLoadFinish(this);
        mViewpager.setObjectForPosition(recomedFragment.getFragmentView(), 0);

/*        liveFragment = RankTeacherFragment.newInstance(Constans.NOW_LIVE);
        liveFragment.setCustomViewPage(mViewpager);
        liveFragment.setLoadFinish(this);
        mViewpager.setObjectForPosition(liveFragment.getFragmentView(), 1);*/

        exclusiveFragment = RankTeacherFragment.newInstance(Constans.EXCLUSIVE);
        exclusiveFragment.setCustomViewPage(mViewpager);
        exclusiveFragment.setLoadFinish(this);
        mViewpager.setObjectForPosition(exclusiveFragment.getFragmentView(), 1);

        arrowFragment = RankTeacherFragment.newInstance(Constans.PEOPLE_ARROW);
        arrowFragment.setCustomViewPage(mViewpager);
        arrowFragment.setLoadFinish(this);
        mViewpager.setObjectForPosition(arrowFragment.getFragmentView(), 2);

        mList.add(recomedFragment);
        mList.add(exclusiveFragment);
        mList.add(arrowFragment);

        mAdapter = new ViewPageFragmentadapter(getActivity().getSupportFragmentManager(), title_list, mList);
        mViewpager.setAdapter(mAdapter);
        mViewpager.setOffscreenPageLimit(mList.size());
        mViewpager.setOnPageChangeListener(this);
        mTab.setViewPager(mViewpager);
        care_top_tab_layout.setViewPager(mViewpager);
    }

    private void userGroups() {
        WebBase.userGroups(1, 3, new JSONHandler() {
            @Override
            public void onSuccess(JSONObject obj) {
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
                        int chatNo = db.getChatUnReadNum(group1.getId())/*+dbManager.getOfflineMsgConunt(Constants.ROOM,group1.getId())*/;
                        group1.setChat(chatNo);
                    }
                    if (infolist.size() == 0) {
                        dbManager.setUnablemessage(null);
                    } else if (infolist.size() < 3) {

                    } else {
                        sc_focus.setVisibility(View.VISIBLE);
                    }
                    mAdapter.notifyDataSetChanged();
                    groupActivity.setCare_menu_point();
                }
            }

            @Override
            public void onFailure(String err_msg) {
                if (checkVa(err_msg)) {
                    SettingDefaultsManager.getInstance().setAuthtoken("");
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
                recomedFragment.setViewPageHeight(select);
                break;
            case 1:
                exclusiveFragment.setViewPageHeight(select);
                break;
            case 2:
                arrowFragment.setViewPageHeight(select);
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
                ShowActivity.showActivity(getActivity(), SearchActivity.class);
                break;
        }
    }
}
