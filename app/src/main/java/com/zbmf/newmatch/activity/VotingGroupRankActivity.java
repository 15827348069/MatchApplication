package com.zbmf.newmatch.activity;

import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zbmf.newmatch.R;
import com.zbmf.newmatch.adapter.VotingAdapter;
import com.zbmf.newmatch.adapter.VotingTeacherAdapter;
import com.zbmf.newmatch.api.JSONHandler;
import com.zbmf.newmatch.api.WebBase;
import com.zbmf.newmatch.bean.Group;
import com.zbmf.newmatch.common.Constans;
import com.zbmf.newmatch.common.IntentKey;
import com.zbmf.newmatch.util.DateUtil;
import com.zbmf.newmatch.util.JSONParse;
import com.zbmf.newmatch.util.MyActivityManager;
import com.zbmf.newmatch.util.ShowActivity;
import com.zbmf.newmatch.view.ListViewForScrollView;
import com.zbmf.newmatch.view.RoundedCornerImageView;
import com.zbmf.worklibrary.presenter.BasePresenter;
import com.zbmf.worklibrary.pulltorefresh.PullToRefreshBase;
import com.zbmf.worklibrary.pulltorefresh.PullToRefreshScrollView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;


/**
 * Created by xuhao on 2017/11/6.
 */

public class VotingGroupRankActivity extends BaseActivity implements VotingTeacherAdapter.OnGroup {
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
    @BindView(R.id.tv_rank)
    TextView tv_rank;
    @BindView(R.id.tv_diff)
    TextView tv_diff;
    @BindView(R.id.tv_sign)
    TextView tv_sign;
    @BindView(R.id.list_voting)
    ListViewForScrollView listViewForScrollView;
    @BindView(R.id.tv_teacher_sign)
    TextView tv_teacher_sign;
    @BindView(R.id.list_teacher_voting)
    ListViewForScrollView list_teacher_voting;
    @BindView(R.id.tv_desc)
    TextView tv_desc;
    @BindView(R.id.tv_find_stock)
    TextView tv_find_stock;
    @BindView(R.id.pull_to_refresh_scrollview)
    PullToRefreshScrollView pull_to_refresh_scrollview;

    private VotingAdapter adapter;
    private VotingTeacherAdapter teacherAdapter;
    private List<Group> infolist;

    private String month;
    private boolean user, teacher;
    private String group_id;

    @Override
    protected int getLayout() {
        return R.layout.activity_voting_layout;
    }

    @Override
    protected String initTitle() {
        return "贡献榜";
    }

    @Override
    protected void initData(Bundle bundle) {
//添加管理activity
        MyActivityManager.getMyActivityManager().pushAct(this);
        tv_sign.setVisibility(View.VISIBLE);
        pull_to_refresh_scrollview.setMode(PullToRefreshBase.Mode.PULL_FROM_START);

        // TODO: 2018/10/18 这里注销了ScreenActivity

//        tv_find_stock.setOnClickListener(v -> ShowActivity.showActivity(VotingGroupRankActivity.this
//                , ScreenActivity.class));
        pull_to_refresh_scrollview.setOnRefreshListener(refreshView -> getData());

        initData();


    }

    @Override
    protected BasePresenter initPresent() {
        return null;
    }



    public void initData() {
        tv_desc.setText(Html.fromHtml(Constans.VOTING_DESC));
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            Group group = (Group) bundle.getSerializable(IntentKey.GROUP);
            group_id = group.getId();
        }
        infolist = new ArrayList<>();
        adapter = new VotingAdapter(this);
        adapter.setList(infolist);
        listViewForScrollView.setAdapter(adapter);
        teacherAdapter = new VotingTeacherAdapter(this);
        teacherAdapter.setOnGroup(this);
        list_teacher_voting.setAdapter(teacherAdapter);
        month = DateUtil.getMonth(0, Constans.YYYYMM);
        getData();
    }


    private void getData() {
        WebBase.userVote(group_id, new JSONHandler() {
            @Override
            public void onSuccess(JSONObject obj) {
                tv_rank.setText(obj.optString("rank"));
                tv_diff.setText(getString(R.string.voting).replace("[*]",
                        obj.optString("votes")).replace("[**]", obj.optString("diff")));
                infolist.clear();
                infolist.addAll(JSONParse.getGroupList(obj.optJSONArray("users")));
                adapter.notifyDataSetChanged();
                user = true;
                onRefreshScrollView();
            }

            @Override
            public void onFailure(String err_msg) {
                user = true;
                onRefreshScrollView();
            }
        });
        WebBase.vote(month, new JSONHandler() {
            @Override
            public void onSuccess(JSONObject obj) {
                if (obj.has("groups")) {
                    List<Group> teacherList = JSONParse.getGroupList(obj.optJSONArray("groups"));
                    teacherAdapter.setList(teacherList);
                }
                teacher = true;
                onRefreshScrollView();
            }

            @Override
            public void onFailure(String err_msg) {
                teacher = true;
                onRefreshScrollView();
            }
        });
    }

    private void onRefreshScrollView() {
        if (user && teacher && pull_to_refresh_scrollview != null && pull_to_refresh_scrollview.isRefreshing()) {
            pull_to_refresh_scrollview.onRefreshComplete();
        }
    }

    @Override
    public void onGroup(Group group) {
        if (group.getId().equals(group_id)) {
            finish();
        } else {
            ShowActivity.showGroupDetailActivity(this, group);
        }
    }

}
