package com.zbmf.newmatch;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.zbmf.newmatch.api.JSONHandler;
import com.zbmf.newmatch.api.WebBase;
import com.zbmf.newmatch.bean.Adverts;
import com.zbmf.newmatch.bean.LiveMessage;
import com.zbmf.newmatch.bean.MatchDescBean;
import com.zbmf.newmatch.common.Constans;
import com.zbmf.newmatch.common.IntentKey;
import com.zbmf.newmatch.db.DBManager;
import com.zbmf.newmatch.fragment.CareFragments;
import com.zbmf.newmatch.fragment.DrillFragment;
import com.zbmf.newmatch.fragment.HomeFragment;
import com.zbmf.newmatch.fragment.MatchFragment;
import com.zbmf.newmatch.fragment.MyDetailFragment;
import com.zbmf.newmatch.listener.IMainActivityCilck;
import com.zbmf.newmatch.listener.IlaunchView;
import com.zbmf.newmatch.service.ScoketService;
import com.zbmf.newmatch.util.MatchSharedUtil;
import com.zbmf.newmatch.util.MessageType;
import com.zbmf.newmatch.util.MyActivityManager;
import com.zbmf.newmatch.util.ServiceUtil;
import com.zbmf.newmatch.util.ShowActivity;
import com.zbmf.worklibrary.adapter.ViewPageAdapter;
import com.zbmf.worklibrary.view.ViewPageNoScroll;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class MainActivity extends AppCompatActivity implements /*RadioGroup.OnCheckedChangeListener, */
        IMainActivityCilck, IlaunchView, View.OnClickListener {
    @BindView(R.id.vp_main)
    ViewPageNoScroll vpMain;
    //        @BindView(R.id.rg_main_menu)
//        RadioGroup rgMainMenu;
    @BindView(R.id.rb_home)
    TextView rbHome;
    @BindView(R.id.rb_match)
    TextView rbMatch;
    @BindView(R.id.rb_drill)
    TextView rbDrill;
//    @BindView(R.id.rb_stock)
//    TextView rbStock;
    @BindView(R.id.rb_mine)
    TextView rbMine;
    @BindView(R.id.rb_group)
    TextView rbGroup;
    private MatchFragment matchFragment;

    private Unbinder mUnBinder;
    private boolean isExited = false;
    private UpdateReceiver mReceiver;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFullscreen(true);
        setContentView(R.layout.activity_main);
        MyActivityManager.getMyActivityManager().pushAct(this);
        Intent intent = getIntent();
        if (intent != null) {
            Adverts adverts = (Adverts) intent.getSerializableExtra(IntentKey.ADS);
            ShowActivity.showWebViewActivityJudge(this, adverts);
//            if (adverts != null) {
//                if (adverts.getJump_url().contains(Constans.MATCH_AD_TYPE)) {
//                    //如果是比赛类型的广告，则请求接口，判断是否已经参赛
//                    new LaunchPresenter().getMatchDesc();
//                } else {
//                    ShowActivity.showWebViewActivity(this, adverts.getJump_url(), "");
//                }
//            }
        }
        dbManager = new DBManager(getBaseContext());
        mUnBinder = ButterKnife.bind(this);
        setOnClick();
        //注册聊天服务
        registerChatService();
        //初始化选中的位置
        setSelectHome();
//        rgMainMenu.setOnCheckedChangeListener(this);
//        initView();
        getFragmentList();
    }

    private void setOnClick() {
        rbHome.setOnClickListener(this);
        rbMatch.setOnClickListener(this);
        rbGroup.setOnClickListener(this);
        rbDrill.setOnClickListener(this);
//        rbStock.setOnClickListener(this);
        rbMine.setOnClickListener(this);
    }

    //注册聊天的service
    private void registerChatService(){
        //初始化时启动服务
        if (!ServiceUtil.isServiceRunning(this, ScoketService.class.getName())) {
            Intent intent = new Intent(this, ScoketService.class);
            this.bindService(intent, conn, BIND_AUTO_CREATE);
        }
        mReceiver = new UpdateReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constans.BIND_CLIENT_ID);
        filter.addAction(Constans.UP_DATA_MESSAGE);
        filter.addAction(Constans.UP_DATA_COUPONS);
        filter.addAction(Constans.LOGOUT);
        filter.addAction(Constans.USER_NEW_MESSAGE);
        filter.addAction(Constans.NEW_LIVE_MSG);
        filter.addAction(Constans.NEW_LIVE_MSG_READ);
        filter.addAction(Constans.UNREADNUM);
        filter.addAction(Constans.NETWORK_IS_UNREACHABLE);
        filter.addAction(Constans.USER_RED_NEW_MESSAGE);
        filter.addAction(Constans.UPDATE_VIDEO_LIST);
        registerReceiver(mReceiver, filter);
    }
    private long exitTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
//            long l = System.currentTimeMillis() - exitTime;
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                MyActivityManager.getMyActivityManager().removeAllAct();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void setFullscreen(boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

//    private void initView() {}

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    private boolean b = false;

    @Override
    protected void onResume() {
        super.onResume();
        StatService.onPageStart(this, "大赛MainActivity打开");
        if (ShowActivity.isLogin() && !b) {
            b = true;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        StatService.onPageEnd(this, "大赛MainActivity关闭");
    }

    private void getFragmentList() {
        List<Fragment> fragmentList = new ArrayList<>();
        HomeFragment homeFragment = HomeFragment.newInstance();
        matchFragment = MatchFragment.newInstance();
        //圈子的Fragment
        CareFragments careFragments = CareFragments.newInstance();
        DrillFragment drillFragment = DrillFragment.newInstance();
//        StockFragment stockFragment = StockFragment.newInstance();
        MyDetailFragment myDetailFragment = MyDetailFragment.newInstance();
//        MineFragment mineFragment = MineFragment.newInstance();
        fragmentList.add(homeFragment);
        fragmentList.add(matchFragment);
        fragmentList.add(careFragments);
        fragmentList.add(drillFragment);
//        fragmentList.add(stockFragment);
        fragmentList.add(myDetailFragment);
        vpMain.setAdapter(new ViewPageAdapter(getSupportFragmentManager(), fragmentList));
        vpMain.setOffscreenPageLimit(fragmentList.size());
    }

    private int selectNo = 0;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rb_home:
                selectNo = 0;
                break;
            case R.id.rb_match:
                selectNo = 1;
                break;
            case R.id.rb_group:
                selectNo = 2;
                break;
            case R.id.rb_drill:
                selectNo = 3;
                break;
//            case R.id.rb_stock:
//                selectNo = 4;
//                break;
            case R.id.rb_mine:
                selectNo = 4;
                break;
        }
        selectedWithFg(selectNo);
    }

    private void selectedWithFg(int select) {
        switch (select) {
            case 0:
                rbHome.setSelected(true);
                rbMatch.setSelected(false);
                rbGroup.setSelected(false);
                rbDrill.setSelected(false);
//                rbStock.setSelected(false);
                rbMine.setSelected(false);
                rbHome.setTextColor(getResources().getColor(R.color.share_red));
                rbMatch.setTextColor(getResources().getColor(R.color.black_33));
                rbGroup.setTextColor(getResources().getColor(R.color.black_33));
                rbDrill.setTextColor(getResources().getColor(R.color.black_33));
//                rbStock.setTextColor(getResources().getColor(R.color.black_33));
                rbMine.setTextColor(getResources().getColor(R.color.black_33));
                break;
            case 1:
                rbHome.setSelected(false);
                rbMatch.setSelected(true);
                rbGroup.setSelected(false);
                rbDrill.setSelected(false);
//                rbStock.setSelected(false);
                rbMine.setSelected(false);
                rbHome.setTextColor(getResources().getColor(R.color.black_33));
                rbMatch.setTextColor(getResources().getColor(R.color.share_red));
                rbGroup.setTextColor(getResources().getColor(R.color.black_33));
                rbDrill.setTextColor(getResources().getColor(R.color.black_33));
//                rbStock.setTextColor(getResources().getColor(R.color.black_33));
                rbMine.setTextColor(getResources().getColor(R.color.black_33));
                break;
            case 2:
                //首先判断用户是否已经登录
                if (!ShowActivity.isLogin(this)) {
                    return;
                }
                rbHome.setSelected(false);
                rbMatch.setSelected(false);
                rbGroup.setSelected(true);
                rbDrill.setSelected(false);
//                rbStock.setSelected(false);
                rbMine.setSelected(false);
                rbHome.setTextColor(getResources().getColor(R.color.black_33));
                rbMatch.setTextColor(getResources().getColor(R.color.black_33));
                rbGroup.setTextColor(getResources().getColor(R.color.share_red));
                rbDrill.setTextColor(getResources().getColor(R.color.black_33));
//                rbStock.setTextColor(getResources().getColor(R.color.black_33));
                rbMine.setTextColor(getResources().getColor(R.color.black_33));
                break;
            case 3:
                rbHome.setSelected(false);
                rbMatch.setSelected(false);
                rbGroup.setSelected(false);
                rbDrill.setSelected(true);
                rbMine.setSelected(false);
                rbHome.setTextColor(getResources().getColor(R.color.black_33));
                rbMatch.setTextColor(getResources().getColor(R.color.black_33));
                rbGroup.setTextColor(getResources().getColor(R.color.black_33));
                rbDrill.setTextColor(getResources().getColor(R.color.share_red));
                rbMine.setTextColor(getResources().getColor(R.color.black_33));
                break;
            case 4:
                rbHome.setSelected(false);
                rbMatch.setSelected(false);
                rbGroup.setSelected(false);
                rbDrill.setSelected(false);
                rbMine.setSelected(true);
                rbHome.setTextColor(getResources().getColor(R.color.black_33));
                rbMatch.setTextColor(getResources().getColor(R.color.black_33));
                rbGroup.setTextColor(getResources().getColor(R.color.black_33));
                rbDrill.setTextColor(getResources().getColor(R.color.black_33));
                rbMine.setTextColor(getResources().getColor(R.color.share_red));
                break;

        }
        vpMain.setCurrentItem(select, false);
    }

    public void setIsExit(boolean b) {
        this.isExited = b;
    }

    public void setSelectHome() {
        this.selectNo = 0;
        rbHome.setSelected(true);
        rbHome.setTextColor(getResources().getColor(R.color.share_red));
        rbMatch.setTextColor(getResources().getColor(R.color.black_33));
        rbGroup.setTextColor(getResources().getColor(R.color.black_33));
        rbDrill.setTextColor(getResources().getColor(R.color.black_33));
        rbMine.setTextColor(getResources().getColor(R.color.black_33));
        rbMatch.setSelected(false);
        rbGroup.setSelected(false);
        rbDrill.setSelected(false);
        rbMine.setSelected(false);
        vpMain.setCurrentItem(0, false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mUnBinder != null) {
            mUnBinder.unbind();
        }
        //结束服务
        if (conn != null) {
            unbindService(conn);
        }
        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
        }
        dbManager.closeDB();
    }

    @Override
    public void onCityClick() {
        selectedWithFg(1);
        selectNo = 1;
        matchFragment.onCityClick();
    }

    @Override
    public void loadMore() {
    }

    @Override
    public void dissLoading() {
    }

    @Override
    public void showToastMsg(String msg) {
    }

    @Override
    public void onRefreshComplete() {
    }

    @Override
    public void toLogin() {
    }

    @Override
    public void rushLunchImage(List<Adverts> imgList) {
    }

    @Override
    public void rushLunchImageErr(String msg) {
    }

    @Override
    public void refreshMatchDesc(MatchDescBean.Result result) {
        if (result != null) {
            ShowActivity.showMatch4(MainActivity.this, result);
        }
    }

    @Override
    public void refreshMatchDescErr(String msg) {
    }

    private int roffine = 0;
    private int select;
    private DBManager dbManager;

    public void setCare_menu_point() {
        roffine = dbManager.getAllUnredCount();
        roffine = Math.max(0, Math.min(roffine, 99));
//        setUnRead();
    }

    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };

    public void bind_client_id() {
        WebBase.Bind(new JSONHandler() {
            @Override
            public void onSuccess(JSONObject obj) {
                roffine = 0;
                roffine = dbManager.getAllUnredCount();
                setCare_menu_point();
            }

            @Override
            public void onFailure(String err_msg) {
            }
        });
    }

    private class UpdateReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            //获取service传过来的信息
            switch (intent.getAction()) {
                case Constans.BIND_CLIENT_ID:
                    String client_id = intent.getStringExtra("client_id");
                    MatchSharedUtil.setClientId(client_id);
                    if (MatchSharedUtil.AuthToken() != null) {
                        bind_client_id();
                    }
                    break;
                case Constans.UP_DATA_MESSAGE:
//                    if (upUserMessage != null) {
//                        upUserMessage.upMessage();
//                    }
                    break;
                case Constans.UP_DATA_COUPONS:
//                    myDetailFragment.setData(false);
                    break;

                case Constans.LOGOUT:
//                    myDetailFragment.setData(false);
//                    careFragement.setData(false);
//                    group_main_munu.clearCheck();
//                    group_main_munu.check(R.id.home_menu);
//                    viewpage.setCurrentItem(0, false);
//                    dbManager.setUnablemessage(null);
//                    setCare_menu_point();
                    break;
                case Constans.USER_NEW_MESSAGE:
//                    //个人中心新消息point提示
//                    if (my_detail_point.getVisibility() == View.INVISIBLE) {
//                        my_detail_point.setVisibility(View.VISIBLE);
//                    }
//                    myDetailFragment.setPointVisi(intent.getStringExtra("type"));
                    break;
                case Constans.USER_RED_NEW_MESSAGE:
//                    myDetailFragment.setPointGone(intent.getStringExtra("type"));
//                    if (myDetailFragment.getPoint()) {
//                        if (my_detail_point.getVisibility() == View.INVISIBLE) {
//                            my_detail_point.setVisibility(View.VISIBLE);
//                        }
//                    }
                    break;
                case Constans.NEW_LIVE_MSG:
                    //直播室新消息
                    LiveMessage msg = (LiveMessage) intent.getSerializableExtra("new_live_msg");
                    if (MatchSharedUtil.AuthToken() != null && !msg.getMessage_type().equals(MessageType.SYSTEM)) {
                        roffine += 1;
//                        setUnRead();
                    }
                    break;
                case Constans.NEW_LIVE_MSG_READ:
                    roffine = dbManager.getAllUnredCount();
                    setCare_menu_point();
                    break;
                case Constans.UNREADNUM:
                    //设置菜单未读
//                    if (careFragement != null) {
//                        careFragement.updateDate(false, roffine);
//                    }
                    break;
                case Constans.NETWORK_IS_UNREACHABLE:
                    Toast.makeText(MainActivity.this, "网络不可用", Toast.LENGTH_SHORT).show();
                    break;

            }

        }
    }

}
