package com.zbmf.newmatch.activity;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.zbmf.newmatch.R;
import com.zbmf.newmatch.adapter.GroupViewPageAdapter;
import com.zbmf.newmatch.api.JSONHandler;
import com.zbmf.newmatch.api.WebBase;
import com.zbmf.newmatch.bean.Group;
import com.zbmf.newmatch.common.IntentKey;
import com.zbmf.newmatch.dialog.DescDialog;
import com.zbmf.newmatch.dialog.EditTextDialog;
import com.zbmf.newmatch.fragment.group.GroupDetailAskFragment;
import com.zbmf.newmatch.fragment.group.GroupDetailBlogFragment;
import com.zbmf.newmatch.fragment.group.GroupDetailFansFragment;
import com.zbmf.newmatch.fragment.group.GroupDetailHistoryFragment;
import com.zbmf.newmatch.util.DisplayUtil;
import com.zbmf.newmatch.util.JSONParse;
import com.zbmf.newmatch.util.MatchSharedUtil;
import com.zbmf.newmatch.util.MyActivityManager;
import com.zbmf.newmatch.util.ShowActivity;
import com.zbmf.newmatch.view.GlideOptionsManager;
import com.zbmf.newmatch.view.MyCustomViewpager;
import com.zbmf.newmatch.view.MyScrollView;
import com.zbmf.newmatch.view.RoundImageView;
import com.zbmf.worklibrary.presenter.BasePresenter;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by xuhao
 * on 2017/2/25.
 * 圈住直播页面
 */

public class GroupDetailActivity extends BaseActivity implements MyScrollView.OnScrollListener,
        ViewPager.OnPageChangeListener, View.OnClickListener {
    private MyScrollView group_detail_scroll;
    private LinearLayout group_detail_button_layout;
    private List<Fragment> infolist;
    private int LayoutTop, nameTop,LayoutBottom;
    private ImageView icon_live_id;
    private Animation wheelAnimation;
    private TextView group_title_name,tv_care_detail_message, tv_detail_care_text,group_detail_name,
            group_detail_id, group_detail_desc, group_detail_fans, group_detail_blog_number,
            group_detail_care_number, live_type_text;
    private LinearLayout group_detail_layout;
    private RadioGroup group_detail_radio, group_top_detail_radio;
    public MyCustomViewpager viewPager;
    private Group group_bean;
    private RoundImageView group_detail_avatar;
    private RelativeLayout live_type_layout;
    private LinearLayout bottom_layout;
    private LinearLayout group_detail_top_layout;
    private GroupDetailBlogFragment blogFragment;
    private GroupDetailHistoryFragment groupDetailHistoryFragment;
    private GroupDetailAskFragment groupDetailAskFragment;
    private GroupDetailFansFragment groupDetailFansFragment;

    private Dialog edit_dialog;
    private DescDialog desc_dialog;
    private int flag;
    private TextView mTv_coupons;
    private LinearLayout mSure_add_fans_button;

    @Override
    protected int getLayout() {
        return R.layout.group_detail_layout;
    }

    @Override
    protected String initTitle() {
        return null;
    }

    @Override
    protected void initData(Bundle bundle) {
//添加管理activity
        MyActivityManager.getMyActivityManager().pushAct(this);
        GroupinitView();
        addListener();
        initData();

    }

    @Override
    protected BasePresenter initPresent() {
        return null;
    }

    public void setAction_bar_layout(int v){
        View view=getView(R.id.action_bar_layout);
        if(view!=null){
            getView(R.id.action_bar_layout).setVisibility(v);
        }
    }

    public void GroupinitView() {
        initTitle();
        setAction_bar_layout(View.INVISIBLE);
        group_detail_scroll = (MyScrollView) findViewById(R.id.group_detail_scroll);
        group_detail_button_layout = (LinearLayout) findViewById(R.id.group_detail_button_layout);
        group_detail_layout = (LinearLayout) findViewById(R.id.group_detail_layout);
        group_title_name = (TextView) findViewById(R.id.group_title_name);
        group_detail_name = (TextView) findViewById(R.id.teacherTag);
//        group_detail_id = (TextView) findViewById(R.id.group_detail_id);//
        group_detail_id= findViewById(R.id.countTeacherTv);

        mTv_coupons = (TextView) getView(R.id.tv_coupons);
        mSure_add_fans_button = (LinearLayout) getView(R.id.sure_add_fans_button);

//        group_detail_desc = (TextView) findViewById(R.id.group_detail_desc);//原来的简介
        group_detail_desc = findViewById(R.id.jianjieTv);

//        group_detail_fans = (TextView) findViewById(R.id.group_detail_fans);//原来的粉丝数量
        group_detail_fans = findViewById(R.id.fensi_count_value);

//        group_detail_blog_number = (TextView) findViewById(R.id.group_detail_blog_number);//观点数量
        group_detail_blog_number = findViewById(R.id.point_count_value);//观点数量

//        group_detail_care_number = (TextView) findViewById(R.id.group_detail_care_number);//关注数量
        group_detail_care_number = findViewById(R.id.care_count_value);//关注数量

        live_type_text = (TextView) findViewById(R.id.live_type_text);
        icon_live_id = (ImageView) findViewById(R.id.icon_live_id);
        viewPager = (MyCustomViewpager) findViewById(R.id.group_detail_view_page);
//        group_detail_avatar = (ImageView) findViewById(R.id.group_detail_avatar);//原来的Avatar
        group_detail_avatar = findViewById(R.id.teacherAvatar);

        live_type_layout = (RelativeLayout) findViewById(R.id.live_type_layout);
        wheelAnimation = AnimationUtils.loadAnimation(this, R.anim.send_gift_progress_ami);
        group_detail_radio = (RadioGroup) findViewById(R.id.group_detail_radio);
        group_top_detail_radio = (RadioGroup) findViewById(R.id.group_detail_top_radio);
        group_detail_top_layout=(LinearLayout) getView(R.id.group_detail_top_layout);
        group_detail_top_layout.setAlpha(0f);
        group_detail_scroll.setOnScrollListener(this);
        bottom_layout = (LinearLayout) getView(R.id.bottom_layout);
        tv_detail_care_text=(TextView) getView(R.id.tv_detail_care_text);
//        tv_care_detail_message=(TextView)getView(R.id.tv_care_detail_message);//关注的按钮
        tv_care_detail_message=(TextView)getView(R.id.careTvBtn);//关注的按钮
        if (edit_dialog == null) {
            edit_dialog = Editdialog1();
        }
        initfragment();
    }

    public void initfragment() {
        group_bean = (Group) getIntent().getSerializableExtra(IntentKey.GROUP);
        blogFragment = GroupDetailBlogFragment.newInstance(group_bean);
        groupDetailFansFragment = GroupDetailFansFragment.newInstance(group_bean);
        groupDetailAskFragment = GroupDetailAskFragment.newInstance(group_bean);
        groupDetailHistoryFragment = GroupDetailHistoryFragment.newInstance(group_bean);
        infolist = new ArrayList<>();
        infolist.add(blogFragment);
        infolist.add(groupDetailFansFragment);
        infolist.add(groupDetailAskFragment);
        infolist.add(groupDetailHistoryFragment);
        GroupViewPageAdapter adapter = new GroupViewPageAdapter(getSupportFragmentManager(), infolist);
        viewPager.setAdapter(adapter);
        viewPager.setOnPageChangeListener(this);
        viewPager.setOffscreenPageLimit(infolist.size());
        viewPager.resetHeight(0);
        group_detail_radio.check(R.id.blog_menu);

        int fans_level = group_bean.getFans_level();
        if (fans_level>=5){
            //铁粉
            mTv_coupons.setVisibility(View.VISIBLE);
            mSure_add_fans_button.setVisibility(View.VISIBLE);
        }else {
            boolean isShowFans = MatchSharedUtil.getIsShowFans();
            if (isShowFans){
                mTv_coupons.setVisibility(View.GONE);
                mSure_add_fans_button.setVisibility(View.GONE);
            }else {
                mTv_coupons.setVisibility(View.VISIBLE);
                mSure_add_fans_button.setVisibility(View.VISIBLE);
            }
        }
    }

    public void initData() {
        flag = getIntent().getIntExtra(IntentKey.FLAG, 0);
        boolean showask=getIntent().getBooleanExtra(IntentKey.SHOWASK,false);
        if(showask){
            if (ShowActivity.isLogin(GroupDetailActivity.this)) {
                if (edit_dialog == null) {
                    edit_dialog = Editdialog1();
                }
                edit_dialog.show();
            }
        }
        getGroupInfo();
        GroupStat();
    }

    public void addListener() {
        getView(R.id.blog_menu).setOnClickListener(this);
        getView(R.id.fans_menu).setOnClickListener(this);
        getView(R.id.ask_menu).setOnClickListener(this);
        getView(R.id.my_history).setOnClickListener(this);
        getView(R.id.blog_menu_top).setOnClickListener(this);
        getView(R.id.fans_menu_top).setOnClickListener(this);
        getView(R.id.ask_menu_top).setOnClickListener(this);
        getView(R.id.my_history_top).setOnClickListener(this);
        getView(R.id.sure_add_fans_button).setOnClickListener(this);
        getView(R.id.tv_ask).setOnClickListener(this);
        getView(R.id.tv_coupons).setOnClickListener(this);
        getView(R.id.group_detail_title_return).setOnClickListener(this);
        tv_detail_care_text.setOnClickListener(this);
        tv_care_detail_message.setOnClickListener(this);
        group_detail_desc.setOnClickListener(this);
        group_detail_avatar.setOnClickListener(v -> showChatActivity());
        group_detail_name.setOnClickListener(v -> showChatActivity());
        live_type_layout.setOnClickListener(view -> showChatActivity());
    }

    private void showChatActivity() {
        if (flag == 1) {
            finish();
        } else {
            Bundle bundle = new Bundle();
            bundle.putInt(IntentKey.FLAG, 1);
            bundle.putSerializable(IntentKey.GROUP, group_bean);
            ShowActivity.showActivity(GroupDetailActivity.this, bundle, Chat1Activity.class);
        }
    }

    private void setGroupMessage() {
        Glide.with(this).load(group_bean.getAvatar()).apply(GlideOptionsManager.getInstance()
                .getRequestOptionsMatch()).into(group_detail_avatar);
        group_title_name.setText(group_bean.getNick_name() != null ? group_bean.getNick_name() : group_bean.getId());
        group_detail_name.setText(group_bean.getNick_name() != null ? group_bean.getNick_name() : group_bean.getId());
        group_detail_id.setText(group_bean.getId());
        if(groupDetailHistoryFragment!=null){
            groupDetailHistoryFragment.setGroup(group_bean);
        }
        if(groupDetailFansFragment!=null){
            groupDetailFansFragment.setGroup(group_bean);
        }
        if (group_bean.getDescription() != null) {
            group_detail_desc.setText("简介：" + group_bean.getDescription());
        }
        if (!group_bean.getIs_online()) {
            icon_live_id.setImageResource(R.drawable.icon_detail_live_nomal);
            live_type_text.setText("圈主已离线");
        } else {
            icon_live_id.setImageResource(R.drawable.icon_detail_live_online);
            icon_live_id.startAnimation(wheelAnimation);
            live_type_text.setText("圈主正在直播...");
        }
        tv_detail_care_text.setSelected(group_bean.is_recommend());
        tv_detail_care_text.setText(group_bean.is_recommend()?"已关注":"关注");
        tv_care_detail_message.setSelected(group_bean.is_recommend());
        tv_care_detail_message.setText(group_bean.is_recommend()?"已关注":"关注");

    }

    public void setGroupNumber(Group group) {
        group_detail_fans.setText(group.getFans() + "");
        group_detail_blog_number.setText(group.getBlogs() + "");
        group_detail_care_number.setText(group.getFollows() + "");
    }

    public void getGroupInfo() {
        WebBase.groupInfo(group_bean.getId(), new JSONHandler() {
            @Override
            public void onSuccess(JSONObject obj) {
                if (!obj.isNull("group")) {
                    JSONObject group = obj.optJSONObject("group");
                    group_bean = JSONParse.getGroup(group);
                    setGroupMessage();
                }
            }

            @Override
            public void onFailure(String err_msg) {
                Toast.makeText(GroupDetailActivity.this, err_msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void GroupStat() {
        WebBase.GroupStat(group_bean.getId(), new JSONHandler() {
            @Override
            public void onSuccess(JSONObject obj) {
                Group group = new Group();
                group.setBlogs(obj.optJSONObject("stat").optInt("blogs"));
                group.setFans(obj.optJSONObject("stat").optInt("fans"));
                group.setFollows(obj.optJSONObject("stat").optInt("follows"));
                setGroupNumber(group);
            }

            @Override
            public void onFailure(String err_msg) {

            }
        });
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            int height= DisplayUtil.dip2px(getBaseContext(),15);
            LayoutTop = group_detail_button_layout.getTop()- height;
            nameTop = group_detail_id.getTop()-height;
            LayoutBottom = group_top_detail_radio.getBottom();
        }
    }

    private void showSuspend() {
        if (group_detail_layout.getVisibility() == View.INVISIBLE) {
            group_detail_layout.setVisibility(View.VISIBLE);
            group_title_name.setAlpha(1f);
            viewPager.setMinimumHeight(LayoutBottom);
        }
    }

    private void removeSuspend() {
        if (group_detail_layout.getVisibility() == View.VISIBLE) {
            group_detail_layout.setVisibility(View.INVISIBLE);
            viewPager.setMinimumHeight(0);
        }
    }
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        switch (position) {
            case 0:
                group_detail_radio.check(R.id.blog_menu);
                group_top_detail_radio.check(R.id.blog_menu_top);
                break;
            case 1:
                group_detail_radio.check(R.id.fans_menu);
                group_top_detail_radio.check(R.id.fans_menu_top);
                break;
            case 2:
                group_detail_radio.check(R.id.ask_menu);
                group_top_detail_radio.check(R.id.ask_menu_top);
                break;
            case 3:
                group_detail_radio.check(R.id.my_history);
                group_top_detail_radio.check(R.id.my_history_top);
                break;
        }
        viewPager.resetHeight(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.group_detail_title_return:
                finish();
                break;
            case R.id.careTvBtn:
            case R.id.tv_detail_care_text:
                Toast.makeText(this, "点击了关注你的按钮", Toast.LENGTH_SHORT).show();
                //关注按钮
                if(ShowActivity.isLogin(GroupDetailActivity.this)){
                    if(group_bean.getFans_level()>=5){
                        showToast("铁粉不能取消关注！");
                    }else{
                        follow();
                    }
                }
                break;
            case R.id.blog_menu:
            case R.id.blog_menu_top:
                viewPager.setCurrentItem(0, false);
                break;
            case R.id.fans_menu:
            case R.id.fans_menu_top:
                viewPager.setCurrentItem(1, false);
                break;
            case R.id.ask_menu:
            case R.id.ask_menu_top:
                viewPager.setCurrentItem(2, false);
                break;
            case R.id.my_history:
            case R.id.my_history_top:
                viewPager.setCurrentItem(3, false);
                break;
            case R.id.tv_coupons:
            case R.id.sure_add_fans_button:
                if(ShowActivity.isLogin(GroupDetailActivity.this)){
                    WebBase.fansInfo(group_bean.getId(), new JSONHandler() {
                        @Override
                        public void onSuccess(JSONObject obj) {
                            if (!obj.isNull("group")) {
                                Group group = JSONParse.getGroup(obj.optJSONObject("group"));
                                ShowActivity.showFansActivity(GroupDetailActivity.this, group);
                            }
                        }

                        @Override
                        public void onFailure(String err_msg) {
                            showToast(err_msg);
                        }
                    });
                }
                break;
            case R.id.tv_ask:
                if (ShowActivity.isLogin(GroupDetailActivity.this)) {
                    if (edit_dialog == null) {
                        edit_dialog = Editdialog1();
                    }
                    edit_dialog.show();
                }
                break;
            case R.id.jianjieTv:
                if(desc_dialog==null){
                    desc_dialog= DescDialog.createDialog(GroupDetailActivity.this)
                    .setMessage(group_bean.getDescription());
                }
                desc_dialog.show();
                break;
        }

    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private Dialog Editdialog1() {
        return EditTextDialog.createDialog(GroupDetailActivity.this, R.style.myDialogTheme)
                .setEditHint("写下你的问题")
                .setLeftButton("私密提问")
                .setRightButton("提问")
                .setEmailVisibility(View.GONE)
                .setSendClick((message, type) ->
                        WebBase.ask(group_bean.getId(), message, flag, new JSONHandler(true,
                                GroupDetailActivity.this, getString(R.string.commit)) {
                    @Override
                    public void onSuccess(JSONObject obj) {
                        edit_dialog.dismiss();
                        showToast("提问成功，等待老师回答!");
                    }

                    @Override
                    public void onFailure(String err_msg) {
                        showToast(err_msg);
                    }
                }
                ));
    }
    public void  follow(){
        if(!group_bean.is_recommend()){
            WebBase.follow(group_bean.getId(), new JSONHandler(true,GroupDetailActivity.this,getString(R.string.commit)) {
                @Override
                public void onSuccess(JSONObject obj) {
                    showToast("关注成功");
                    group_bean.setIs_recommend(1);
                    tv_detail_care_text.setSelected(group_bean.is_recommend());
                    tv_detail_care_text.setText(group_bean.is_recommend()?"已关注":"关注");
                    tv_care_detail_message.setSelected(group_bean.is_recommend());
                    tv_care_detail_message.setText(group_bean.is_recommend()?"已关注":"关注");

                }

                @Override
                public void onFailure(String err_msg) {
                    Toast.makeText(getBaseContext(),err_msg,Toast.LENGTH_SHORT).show();
                }
            });
        }else{
            WebBase.unfollow(group_bean.getId(), new JSONHandler(true,GroupDetailActivity.this,getString(R.string.commit)) {
                @Override
                public void onSuccess(JSONObject obj) {
                    showToast("取消关注成功");
                    group_bean.setIs_recommend(0);
                    tv_detail_care_text.setSelected(group_bean.is_recommend());
                    tv_detail_care_text.setText(group_bean.is_recommend()?"已关注":"关注");
                    tv_care_detail_message.setSelected(group_bean.is_recommend());
                    tv_care_detail_message.setText(group_bean.is_recommend()?"已关注":"关注");
                }

                @Override
                public void onFailure(String err_msg) {
                    Toast.makeText(getBaseContext(),err_msg,Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

    @Override
    public void onScroll(int scrollY) {
        if (scrollY == 0||scrollY<20) {
//            if(group_detail_top_layout.getVisibility()==View.VISIBLE){
//                group_detail_top_layout.setVisibility(View.INVISIBLE);
//            }
            group_detail_top_layout.setAlpha(0);
            if (bottom_layout.getVisibility() == View.GONE) {
                bottom_layout.setVisibility(View.VISIBLE);
            }
            removeSuspend();
        }else{
            if(scrollY>10&&scrollY<nameTop){
                float alpha = 1f / nameTop;
                float a = scrollY * alpha;
                group_detail_top_layout.setAlpha(a);
            }else if (scrollY >= nameTop  && scrollY < LayoutTop) {
                group_detail_top_layout.setAlpha(255);
                if (bottom_layout.getVisibility() == View.VISIBLE) {
                    bottom_layout.setVisibility(View.GONE);
                }
                removeSuspend();
            } else if (scrollY >= LayoutTop ) {
                showSuspend();
            }
        }
    }

    @Override
    public void scrollDirectionUp(boolean scrollUp) {}
}
