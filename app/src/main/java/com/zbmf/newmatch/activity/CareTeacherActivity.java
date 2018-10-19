package com.zbmf.newmatch.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.zbmf.newmatch.R;
import com.zbmf.newmatch.adapter.FoucusAdapter;
import com.zbmf.newmatch.api.JSONHandler;
import com.zbmf.newmatch.api.WebBase;
import com.zbmf.newmatch.bean.Group;
import com.zbmf.newmatch.common.Constans;
import com.zbmf.newmatch.common.IntentKey;
import com.zbmf.newmatch.db.DBManager;
import com.zbmf.newmatch.db.Database;
import com.zbmf.newmatch.util.JSONParse;
import com.zbmf.newmatch.util.ShowActivity;
import com.zbmf.newmatch.view.RoundedCornerImageView;
import com.zbmf.worklibrary.presenter.BasePresenter;
import com.zbmf.worklibrary.pulltorefresh.PullToRefreshBase;
import com.zbmf.worklibrary.pulltorefresh.PullToRefreshListView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;


//关注老师  //个人中心关注
public class CareTeacherActivity extends BaseActivity implements FoucusAdapter.OnCareClink {

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
    @BindView(R.id.no_message_text)
    TextView no_message_text;
    @BindView(R.id.tv_left_button)
    TextView tv_left_button;
    @BindView(R.id.tv_right_button)
    TextView tv_right_button;
    @BindView(R.id.ll_none)
    LinearLayout ll_none;
    @BindView(R.id.listview)
    PullToRefreshListView listview;
    @BindView(R.id.activity_care_teacher)
    LinearLayout activity_care_teacher;
    private FoucusAdapter mFoucusAdapter;
    private List<Group> mGroups = new ArrayList<>();
    private int PAGE_INDEX, PAGGS;
    public static final int Refresh = 1;
    public static final int LoadMore = 2;
    private int type = FoucusAdapter.FOCUS_LIST;

    private TextView  right_button, left_button;
    private Database db;
    private DBManager dbManager;
    private boolean isFirst = true;

    private UnReadNumReceiver unReadNumReceiver;

    @Override
    public void onCareClink(int position) {
        onCheckedChanged(position);
    }

    private class UnReadNumReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case Constans.NEW_LIVE_MSG:
                    for (Group group : mGroups) {
                        group.setUnredcount(dbManager.getUnredCount(group.getId()));
                    }
                    break;
                case Constans.UNREADNUM:
                    for (Group group : mGroups) {
                        group.setChat(db.getChatUnReadNum(group.getId()));
                    }
                    break;
            }
            mFoucusAdapter.notifyDataSetChanged();
        }
    }

    private void getGroupInfo(Group group) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(IntentKey.GROUP, group);
        ShowActivity.showActivity(CareTeacherActivity.this, bundle, Chat1Activity.class);
    }

    private void userGroups(final int direction) {
        if (direction == Refresh)
            PAGE_INDEX = 1;
        else
            PAGE_INDEX++;

        WebBase.userGroups(PAGE_INDEX, new JSONHandler(isFirst, this, "正在获取数据...") {
            @Override
            public void onSuccess(JSONObject obj) {
                listview.onRefreshComplete();
                Group group = JSONParse.userGroups(obj);
                if (group != null) {
                    PAGGS = group.getPages();
                    if (PAGE_INDEX == PAGGS) {
                        listview.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                    } else {
                        listview.setMode(PullToRefreshBase.Mode.BOTH);
                    }

                    if (group.getList() != null && group.getList().size() > 0) {
                        List<Group> infolist = group.getList();
                        for (Group group1 : infolist) {
                            int chatNo = db.getChatUnReadNum(group1.getId());
                            int liveNo = dbManager.getUnredCount(group1.getId());
                            group1.setChat(chatNo);
                            group1.setUnredcount(liveNo);
                        }
                        if (direction == Refresh) {
                            mGroups.clear();
                        }
                        mGroups.addAll(infolist);
                        mFoucusAdapter.notifyDataSetChanged();
                        ll_none.setVisibility(View.GONE);
                    } else
                        ll_none.setVisibility(View.VISIBLE);
                }
                if (isFirst) {
                    isFirst = false;
                }
            }

            @Override
            public void onFailure(String err_msg) {
                if (isFirst) {
                    isFirst = false;
                }
            }
        });
    }

    public void onCheckedChanged(int position) {
        final String id = mGroups.get(position).getId();
        WebBase.unfollow(id, new JSONHandler(true, CareTeacherActivity.this, "正在取消关注圈主...") {
            @Override
            public void onSuccess(JSONObject obj) {
                Toast.makeText(getBaseContext(), "取消关注成功", Toast.LENGTH_SHORT).show();
                for (int i = 0; i < mGroups.size(); i++) {
                    if (mGroups.get(i).getId().equals(id)) {
                        mGroups.remove(i);
                        break;
                    }
                }
                mFoucusAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(String err_msg) {
                Toast.makeText(getBaseContext(), err_msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        isFirst = true;
        userGroups(Refresh);
        StatService.onResume(this);
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_care_teacher;
    }

    @Override
    protected String initTitle() {
        return "关注老师";
    }

    @Override
    protected void initData(Bundle bundle) {
        listview.setMode(PullToRefreshBase.Mode.BOTH);

        right_button=ll_none.findViewById(R.id.tv_right_button);
        left_button=ll_none.findViewById(R.id.tv_left_button);

        no_message_text.setText(getString(R.string.no_msg));
        right_button.setVisibility(View.VISIBLE);
        right_button.setText(getString(R.string.only_teacher));
        right_button.setOnClickListener(v -> {
            Bundle bundle1 = new Bundle();
            bundle1.putInt(IntentKey.FLAG, 2);
            ShowActivity.showActivityForResult(CareTeacherActivity.this, bundle1,
                    FindTeacherActivity.class, Constans.STUDY);
        });
        left_button.setVisibility(View.VISIBLE);
        left_button.setText(getString(R.string.find_teacher));
        left_button.setOnClickListener(v ->
                ShowActivity.showActivityForResult(CareTeacherActivity.this, null,
                        FindTeacherActivity.class,  Constans.STUDY));
        //设置监听
        addListener();
//        Intent intent = this.getIntent();
//        Bundle bundle = intent.getExtras();

        if (bundle != null) {
            type = bundle.getInt(IntentKey.FLAG);
        }
        dbManager = new DBManager(this);
        db = new Database(this);
        mFoucusAdapter = new FoucusAdapter(this, mGroups, type);
        listview.setAdapter(mFoucusAdapter);
        unReadNumReceiver = new UnReadNumReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constans.NEW_LIVE_MSG);
        intentFilter.addAction(Constans.UNREADNUM);
        registerReceiver(unReadNumReceiver, intentFilter);
    }

    private void addListener() {
        listview.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                userGroups(Refresh);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                userGroups(LoadMore);
            }
        });
        listview.setOnItemClickListener((parent, view, position, id) ->
                getGroupInfo((Group) parent.getItemAtPosition(position)));
        mFoucusAdapter.setOnCareClink(this);
    }

    @Override
    protected BasePresenter initPresent() {
        return null;
    }

    @Override
    protected void onPause() {
        super.onPause();
        StatService.onPause(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (unReadNumReceiver != null) {
            unregisterReceiver(unReadNumReceiver);
        }
    }
}
