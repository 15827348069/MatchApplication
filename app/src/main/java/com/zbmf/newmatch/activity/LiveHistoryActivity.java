package com.zbmf.newmatch.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.zbmf.newmatch.R;
import com.zbmf.newmatch.adapter.LiveAdapter;
import com.zbmf.newmatch.api.JSONHandler;
import com.zbmf.newmatch.api.WebBase;
import com.zbmf.newmatch.bean.Group;
import com.zbmf.newmatch.bean.LiveMessage;
import com.zbmf.newmatch.bean.LiveTypeMessage;
import com.zbmf.newmatch.common.IntentKey;
import com.zbmf.newmatch.service.GetLiveMessage;
import com.zbmf.newmatch.util.ShowActivity;
import com.zbmf.newmatch.view.GroupTextView;
import com.zbmf.newmatch.view.ListViewForScrollView;
import com.zbmf.newmatch.view.ScrollBottomScrollView;
import com.zbmf.worklibrary.presenter.BasePresenter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;


public class LiveHistoryActivity extends BaseActivity implements GroupTextView.OnTextClickListener, View.OnClickListener {

    @BindView(R.id.group_title_name)
    TextView group_title_name;
    @BindView(R.id.live_history_title)
    TextView live_history_title;
    @BindView(R.id.live_history_list)
    ListViewForScrollView live_history_list;
    @BindView(R.id.red_bag_scrollview)
    ScrollBottomScrollView red_bag_scrollview;

    private LiveAdapter adapter;
    private List<LiveMessage> info;
    private Group group_bean;
    private String GROUP_ID;
    private String live_date;
    private int index;
    private int page, pages;
    private boolean is_tf;


    @Override
    protected int getLayout() {
        return R.layout.activity_live_history;
    }

    @Override
    protected String initTitle() {
        return "直播历史记录";
    }

    @Override
    protected void initData(Bundle bundle) {

        addListener();

        GroupinitData();


    }

    @Override
    protected BasePresenter initPresent() {
        return null;
    }

    public void GroupinitData() {
        group_bean = (Group) getIntent().getSerializableExtra(IntentKey.GROUP);
        if (group_bean != null) {
            GROUP_ID = group_bean.getId();
            DateFormat df2 = new SimpleDateFormat("yyyyMMdd");
            live_date = df2.format(group_bean.getLive_history_date());
            info = new ArrayList<>();
            if (group_bean.getFans_level() < 5) {
                is_tf = false;
            } else {
                is_tf = true;
            }
            adapter = new LiveAdapter(this, info, is_tf, this, this);
            live_history_list.setAdapter(adapter);
            DateFormat df = new SimpleDateFormat("yyyy年MM月dd日");
            group_title_name.setText(df.format(group_bean.getLive_history_date()) + "直播记录");
            page = 1;
            getHistoryMsg();
        }
    }

    public void addListener() {
        red_bag_scrollview.setScrollBottomListener(new ScrollBottomScrollView.ScrollBottomListener() {
            @Override
            public void scrollBottom() {
                if (index == 0) {
                    index++;
                    if (page != pages) {
                        page += 1;
                        getHistoryMsg();
                    }
                }
            }

            @Override
            public void scrollnoBottom() {

            }

            @Override
            public void scrollTop() {

            }
        });
    }

    @Override
    public void onClick(View view) {
        String url = (String) view.getTag();
        if (url != null && !TextUtils.isEmpty(url)) {
            ShowActivity.ShowBigImage(this, url);
        }
    }

    public void add_to_tf() {
        WebBase.fansInfo(GROUP_ID, new JSONHandler(true,
                LiveHistoryActivity.this,"正在加载数据...") {
            @Override
            public void onSuccess(JSONObject obj) {
                JSONObject group=obj.optJSONObject("group");
                Group groupbean=new Group();
                groupbean.setId(group.optString("id"));
                groupbean.setName(group.optString("name"));
                groupbean.setNick_name(group.optString("nickname"));
                groupbean.setAvatar(group.optString("avatar"));
                groupbean.setIs_close(group.optInt("is_close"));
                groupbean.setIs_private(group.optInt("is_private"));
                groupbean.setRoles(group.optInt("roles"));
                groupbean.setFans_level(group.optInt("fans_level"));
                groupbean.setDay_mapy(group.optLong("day_mpay"));
                groupbean.setMonth_mapy(group.optLong("month_mpay"));
                groupbean.setEnable_day(group.optInt("enable_day"));
                groupbean.setEnable_point(group.optInt("enable_point"));
                groupbean.setMax_point(group.optInt("max_point"));
                groupbean.setDescription(group.optString("fans_profile"));
                groupbean.setFans_activity(group.optString("fans_activity"));
                groupbean.setFans_countent(group.optString("fans_content"));
                groupbean.setPoint_desc(group.optString("point_desc"));
                groupbean.setMax_mpay(group.optLong("max_mpay"));

                adapter.setGroup(groupbean);
                ShowActivity.showFansActivity(LiveHistoryActivity.this,groupbean);
            }
            @Override
            public void onFailure(String err_msg) {

            }
        });
    }

    public void getHistoryMsg() {
        WebBase.getHistoryMsg(GROUP_ID, live_date, page+"", new JSONHandler() {
            @Override
            public void onSuccess(JSONObject obj) {
                JSONArray result=obj.optJSONObject("result").optJSONArray("msgs");
                pages=obj.optJSONObject("result").optInt("pages");
                if(page==pages){
                    Toast.makeText(getBaseContext(),"已加载全部数据",Toast.LENGTH_SHORT).show();
                }
                int size=result.length();
                for (int i=0;i<size;i++){
                    JSONObject oj=result.optJSONObject(i);
                    info.add(GetLiveMessage.getMessage(oj,true));
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(String err_msg) {

            }
        });
    }

    @Override
    public void OnTextClickListener(LiveTypeMessage message) {
        switch (message.getMessage_type()) {
            case "url":
                ShowActivity.showWebViewActivity(this, message.getMessage());
                break;
            case "stock":
                ShowActivity.showWebViewActivity(this, getString(R.string.stock_link).replace("[*]", message.getMessage()));
                break;
            case "tf_message":
                add_to_tf();
                break;
            case "box_message":
                ShowActivity.showBoxDetailActivity(this, GROUP_ID, message.getMessage());
                break;
        }
    }
}
