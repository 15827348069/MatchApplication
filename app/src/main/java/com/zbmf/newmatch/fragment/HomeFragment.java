package com.zbmf.newmatch.fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zbmf.newmatch.R;
import com.zbmf.newmatch.activity.LookStockActivity;
import com.zbmf.newmatch.activity.MatchTraderInfoActivity;
import com.zbmf.newmatch.adapter.HeadMessageAdapter;
import com.zbmf.newmatch.adapter.HomeCityAdapter;
import com.zbmf.newmatch.adapter.HomeHotMatchAdapter;
import com.zbmf.newmatch.adapter.HomeMatchAdapter;
import com.zbmf.newmatch.adapter.HomeTraderAdapter;
import com.zbmf.newmatch.adapter.SponsorAdapter;
import com.zbmf.newmatch.api.JSONHandler;
import com.zbmf.newmatch.api.ParamsKey;
import com.zbmf.newmatch.api.WebBase;
import com.zbmf.newmatch.bean.Adverts;
import com.zbmf.newmatch.bean.BlogBean;
import com.zbmf.newmatch.bean.City;
import com.zbmf.newmatch.bean.HomeMatchList;
import com.zbmf.newmatch.bean.MatchDescBean;
import com.zbmf.newmatch.bean.MatchNewAllBean;
import com.zbmf.newmatch.bean.PopWindowBean;
import com.zbmf.newmatch.bean.SchoolBean;
import com.zbmf.newmatch.bean.StockIndexBean;
import com.zbmf.newmatch.bean.Traders;
import com.zbmf.newmatch.bean.UserWallet;
import com.zbmf.newmatch.common.Constans;
import com.zbmf.newmatch.common.HtmlUrl;
import com.zbmf.newmatch.common.IntentKey;
import com.zbmf.newmatch.common.SharedKey;
import com.zbmf.newmatch.listener.BannerViewHolder;
import com.zbmf.newmatch.listener.IHomeView;
import com.zbmf.newmatch.listener.SponsorAdsClick;
import com.zbmf.newmatch.presenter.HomePresenter;
import com.zbmf.newmatch.util.MatchSharedUtil;
import com.zbmf.newmatch.util.ShowActivity;
import com.zbmf.newmatch.util.TimeOnItemClickListener;
import com.zbmf.newmatch.view.MZBannerView;
import com.zbmf.newmatch.view.ShowOrHideProgressDialog;
import com.zbmf.newmatch.view.ViewFactory;
import com.zbmf.worklibrary.pulltorefresh.PullToRefreshBase;
import com.zbmf.worklibrary.pulltorefresh.PullToRefreshScrollView;
import com.zbmf.worklibrary.util.GsonUtil;
import com.zbmf.worklibrary.util.SharedpreferencesUtil;
import com.zbmf.worklibrary.view.CycleViewPager;
import com.zbmf.newmatch.view.ListViewForScrollView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by xuhao
 * on 2017/11/24.
 * 首页
 */

public class HomeFragment extends BaseFragment<HomePresenter> implements IHomeView,
        CycleViewPager.ImageCycleViewListener<Adverts>, HomeTraderAdapter.onItemClickListener,
        HomeCityAdapter.CityClick, SponsorAdsClick {
    @BindView(R.id.tv_sh_num)
    TextView tvShNum;
    @BindView(R.id.tv_sh_yield_num)
    TextView tvShYieldNum;
    @BindView(R.id.tv_sh_yield)
    TextView tvShYield;
    @BindView(R.id.tv_sz_num)
    TextView tvSzNum;
    @BindView(R.id.tv_sz_yield_num)
    TextView tvSzYieldNum;
    @BindView(R.id.tv_sz_yield)
    TextView tvSzYield;
    @BindView(R.id.tv_cy_num)
    TextView tvCyNum;
    @BindView(R.id.tv_cy_yield_num)
    TextView tvCyYieldNum;
    @BindView(R.id.tv_cy_yield)
    TextView tvCyYield;
    @BindView(R.id.tv_create_match_num)
    TextView tvCreateMatchNum;
    @BindView(R.id.tv_player)
    TextView tvPlayer;
    //    @BindView(R.id.rv_school)
//    RecyclerView rvSchool;
    @BindView(R.id.list_superme_match)
    ListViewForScrollView listSupermeMatch;
    @BindView(R.id.home_pull_scrollview)
    PullToRefreshScrollView homePullScrollview;
    @BindView(R.id.rv_trader)
    RecyclerView rvTrader;
    //    @BindView(R.id.rv_city)
//    RecyclerView rvCity;
    @BindView(R.id.list_hot_match)
    ListViewForScrollView listHotMatch;
    @BindView(R.id.m_banner)
    MZBannerView banner;
    @BindView(R.id.kf_layout_id)
    LinearLayout kf_layout_id;
    @BindView(R.id.imgRecyclerView)
    RecyclerView imgRecyclerView;
    @BindView(R.id.sponsorView)
    LinearLayout sponsorView;
    //    @BindView(R.id.home_mf_head_message)
    private ListViewForScrollView home_mf_head_message;

    private List<ImageView> views;
    private CycleViewPager cycleViewPager;
    private List<BlogBean> bloglist;

    private HomeMatchAdapter homeMatchAdapter;
    private HomeHotMatchAdapter hotMatchAdapter;
    private HomeTraderAdapter traderAdapter;
    private HomeCityAdapter cityAdapter;
    private static final int TIME = 50000;//每三秒获取一次证券指数
    private static final int[] BANNER = {R.drawable.bjs, R.drawable.gdcxkj, R.drawable.gdkjxy, R.drawable
            .gtjnzq, R.drawable.gyzq, R.drawable.hns, R.drawable.qhs, R.drawable.qkw, R.drawable.qlzq, R.drawable
            .shsf, R.drawable.shzf, R.drawable.xncs, R.drawable.zj, R.drawable.zjgss, R.drawable.zjlgs, R.
            drawable.zjzq, R.drawable.zszq, R.drawable.zxzq, R.drawable.zyzq, R.drawable.zzf};
    private static final int[] BANNER_TV = {R.string.bjs, R.string.gdcxkj, R.string.gdkjxy, R.string.gtjnzq
            , R.string.gyzq, R.string.hns, R.string.qhs, R.string.qkw, R.string.qlzq, R.string.shsf, R.string.shzf,
            R.string.xncs, R.string.zj, R.string.zjgss, R.string.zjlgs, R.string.zjzq, R.string.zszq, R.string.zxzq
            , R.string.zyzq, R.string.zzf};
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };

    private Runnable mRunnable = () -> {
        if (HomePresenter.getInstance() != null) {
            HomePresenter.getInstance().getStockIndex();
        }
    };
    private Dialog mDialog;
    private SponsorAdapter mSponsorAdapter;
    private HeadMessageAdapter headadapter;
    private TextView tv_more_button;

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    protected int getLayout() {
        return R.layout.fragment_home_layout;
    }

    @Override
    protected HomePresenter initPresent() {
        return HomePresenter.getInstance();
    }

    @Override
    protected void initView() {
        home_mf_head_message = getView(R.id.home_mf_head_message);
        tv_more_button = getView(R.id.tv_more_button);

        homePullScrollview.setMode(PullToRefreshBase.Mode.PULL_FROM_START);//只能刷新
        ShowOrHideProgressDialog.showProgressDialog(getActivity(), getActivity(), getString(R.string.hard_loading));
        homePullScrollview.setOnRefreshListener(refreshView -> getPresenter().getDatas());
        if (cycleViewPager == null && getChildFragmentManager() != null) {
            cycleViewPager = (CycleViewPager) getChildFragmentManager().findFragmentById(R.id.home_cycleViewPage);
        }
        if (cycleViewPager != null) {
            cycleViewPager.setCycle(true);
            cycleViewPager.setWheel(true);
            cycleViewPager.setTime(2000);
            cycleViewPager.setIndicatorCenter();
        }
        imgRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        mSponsorAdapter = new SponsorAdapter(getActivity());
        imgRecyclerView.setAdapter(mSponsorAdapter);
        mSponsorAdapter.setSponsorAdsClick(this);
        listSupermeMatch.setOnItemClickListener((adapterView, view, i, l) -> ShowActivity.showUserMatchDetail(getActivity(), homeMatchAdapter.getItem(i)));
        listHotMatch.setOnItemClickListener((adapterView, view, i, l) -> ShowActivity.showHotMatchDetail(getActivity(), hotMatchAdapter.getItem(i)));
        homePullScrollview.setOnRefreshListener(refreshView -> {
            ShowOrHideProgressDialog.showProgressDialog(getActivity(), getActivity(), getString(R.string.hard_loading));
            first_onload_url = true;
            first_onload_bloglist = true;
            first_onload_screen = true;
            getPresenter().getDatas();
        });
        bloglist = new ArrayList<>();
        //设置魔方头条
        headadapter = new HeadMessageAdapter(getActivity(), bloglist);
        home_mf_head_message.setAdapter(headadapter);
        home_mf_head_message.setOnItemClickListener(new TimeOnItemClickListener() {
            @Override
            public void onNoDoubleClick(int position) {
                ShowActivity.showBlogDetailActivity(getActivity(), bloglist.get(position));
            }
        });
        //跳转到魔方头条的更多
        tv_more_button.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putInt(IntentKey.FLAG, 1);
            ShowActivity.showActivity(getActivity(), bundle, LookStockActivity.class);
        });
    }

    @Override
    protected void initData() {
        //初始化数据
        page = 1;
        pages = 0;
        homeMatchAdapter = new HomeMatchAdapter(getActivity());
        listSupermeMatch.setAdapter(homeMatchAdapter);

        hotMatchAdapter = new HomeHotMatchAdapter(getActivity());
        listHotMatch.setAdapter(hotMatchAdapter);

        //获取博文
        getBlog_message();

//        HomeSchoolAdapter schoolAdapter = new HomeSchoolAdapter(getActivity());
        // TODO: 2018/3/20 改成这里设置Banner的点击事件
        banner.setBannerPageClickListener((view, position) -> {

        });
        List<Integer> bannerList = new ArrayList<>();
        final List<Integer> tvList = new ArrayList<>();
        for (int aBANNER : BANNER) {
            bannerList.add(aBANNER);
        }
        for (int aBANNER_TV : BANNER_TV) {
            tvList.add(aBANNER_TV);
        }
        banner.setIndicatorVisible(false);
        //传进参赛单位的图片数量
        banner.setPages(bannerList, tvList, imgs -> {
            BannerViewHolder bannerViewHolder = new BannerViewHolder(imgs);
            //设置Banner的点击事件
            bannerViewHolder.setBannerClickListener((view, position) -> {
            });
            return bannerViewHolder;
        });

        traderAdapter = new HomeTraderAdapter(getActivity());
        rvTrader.setLayoutManager(new GridLayoutManager(getActivity(), 3) {
            @Override
            public boolean canScrollHorizontally() {
                return false;
            }

            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        traderAdapter.setItemClickListener(this);
        rvTrader.setAdapter(traderAdapter);
        cityAdapter = new HomeCityAdapter(getActivity());
        cityAdapter.setCityClick(this);
//        rvCity.setLayoutManager(new GridLayoutManager(getActivity(), 3) {
//            @Override
//            public boolean canScrollVertically() {
//                return false;
//            }
//
//            @Override
//            public boolean canScrollHorizontally() {
//                return false;
//            }
//        });
//        rvCity.setAdapter(cityAdapter);

    }

    @Override
    public void refreshStockIndex(Map<String, String> map) {
        if (homePullScrollview.isRefreshing()) {
            homePullScrollview.onRefreshComplete();
        }
        //更新股票指数  399001:深圳指数 399005:中小板指数 399006:创业板指数 1A0001:上证指数
        ShowOrHideProgressDialog.disMissProgressDialog();
        if (map != null) {
            String sz = map.get("399001");
            /*String zx = map.get("399005");*/
            String cy = map.get("399006");
            String shz = map.get("1A0001");
            refreshSecurity("1A0001", shz);
            refreshSecurity("399001", sz);
            refreshSecurity("399006", cy);
        }
        mHandler.postDelayed(mRunnable, TIME);//每两秒获取一次证券指数
    }

    private void refreshSecurity(String code, String security) {
        StockIndexBean stockIndexBean = GsonUtil.parseData(security, StockIndexBean.class);
        assert stockIndexBean != null;
        String close = stockIndexBean.getClose();
        String current = stockIndexBean.getCurrent();
        float curr = Float.parseFloat(current);
        float clo = Float.parseFloat(close);
        float f = curr - clo;
        String format = new DecimalFormat("0.00").format(f);
        NumberFormat instance = NumberFormat.getInstance();
        instance.setMaximumFractionDigits(2);//设置精确到小数点后两位
        String scale = instance.format(f / clo * 100) + "%";
        switch (code) {
            case "1A0001":
                setTvColor(f, tvShNum);
                setTvColor(f, tvShYieldNum);
                setTvColor(f, tvShYield);
                tvShNum.setText(current);
                setTvText(f, tvShYieldNum, format);
                setTvText(f, tvShYield, scale);
                break;
            case "399001":
                setTvColor(f, tvSzNum);
                setTvColor(f, tvSzYieldNum);
                setTvColor(f, tvSzYield);
                tvSzNum.setText(current);
                setTvText(f, tvSzYieldNum, format);
                setTvText(f, tvSzYield, scale);
                break;
            case "399006":
                setTvColor(f, tvCyNum);
                setTvColor(f, tvCyYieldNum);
                setTvColor(f, tvCyYield);
                tvCyNum.setText(current);
                setTvText(f, tvCyYieldNum, format);
                setTvText(f, tvCyYield, scale);
                break;
        }
    }

    private void setTvText(float f, TextView tv, String yield) {
        tv.setText(f >= 0 ? "+" + yield : yield);
    }

    private void setTvColor(float f, TextView tv) {
        tv.setTextColor(f >= 0 ? getActivity().getResources().getColor(R.color.red1) : getActivity()
                .getResources().getColor(R.color.green));
    }

    private List<MatchNewAllBean.Result.Matches> matchList = new ArrayList<>();
    private List<HomeMatchList.Result.Hot> hotMatch = new ArrayList<>();

    @Override
    public void RusnSupremeMatchAdapter(List<HomeMatchList.Result.Recommend> matchBeans) {
        if (homePullScrollview.isRefreshing()) {
            homePullScrollview.onRefreshComplete();
        }
        ShowOrHideProgressDialog.disMissProgressDialog();
        if (matchList.size() < 2 && matchBeans != null) {//首页的推荐比赛最多为两条
            matchList.clear();
            for (int i = 0; i < matchBeans.size(); i++) {
                if (i < 2) {
                    MatchNewAllBean.Result.Matches matches = new MatchNewAllBean.Result.Matches();
                    matches.setIs_end(matchBeans.get(i).getIs_end() ? 1 : 0);
                    matches.setIs_player(matchBeans.get(i).getIs_player() ? 1 : 0);
                    matches.setMatch_id(matchBeans.get(i).getMatch_id());
                    matches.setMatch_name(matchBeans.get(i).getMatch_name());
                    matches.setMatch_type(matchBeans.get(i).getMatch_type());
                    matches.setPlayers(matchBeans.get(i).getPlayers());
                    matchList.add(matches);
                }
            }
        }
        homeMatchAdapter.setList(matchList);
    }

    //更新热门赛事推荐
    @Override
    public void RushHostMatch(List<HomeMatchList.Result.Hot> matchBeans) {
        ShowOrHideProgressDialog.disMissProgressDialog();
        if (hotMatch.size() < 3 && matchBeans != null) {//
            hotMatch.clear();
            for (int i = 0; i < matchBeans.size(); i++) {
                if (i < 3) {
                    hotMatch.add(matchBeans.get(i));
                }
            }
        }
        hotMatchAdapter.setList(hotMatch);
    }

    //轮播图广告回传
    @Override
    public void RushBannerImage(List<Adverts> imgList) {
        if (homePullScrollview.isRefreshing()) {
            homePullScrollview.onRefreshComplete();
        }
        ShowOrHideProgressDialog.disMissProgressDialog();
        if (views == null) {
            views = new ArrayList<>();
        } else {
            views.clear();
        }
        for (int i = 0; i < imgList.size(); i++) {
            views.add(ViewFactory.getImageView(getActivity(), imgList.get(i).getImg_url()));
        }
        cycleViewPager.setData(views, imgList, this);
    }

    @Override
    public void rushSponsorAds(final List<Adverts> sponsor, final int gainStatus) {
        new Handler(Looper.getMainLooper()).post(() -> {
            //获取赞助商广告
            if (gainStatus == Constans.GAIN_DATA_SUCCESS) {
                if (sponsor == null && sponsor.size() == 0) {
                    sponsorView.setVisibility(View.GONE);
                    return;
                } else {
                    sponsorView.setVisibility(View.VISIBLE);
                }
                if (mSponsorAdapter.getSponsorList() != null) {
                    mSponsorAdapter.clearList();
                    mSponsorAdapter.addList(sponsor);
                }
            }
        });
    }

    @Override
    public void RushMatchSchool(SchoolBean schoolBean) {
        if (schoolBean != null) {
            ShowOrHideProgressDialog.disMissProgressDialog();
            SchoolBean.Result result = schoolBean.getResult();
            if (result != null) {
                tvCreateMatchNum.setText(String.format(getString(R.string.create_match), result.getMatches()));
                tvPlayer.setText(String.format(getString(R.string.match_player), result.getPlayers()));
                //这里没有设置参赛过的学校数据
//                schoolAdapter.refreshData(result.getSchools());
            }
        }
    }

    @Override
    public void RushTraderList(List<Traders> traders) {
        ShowOrHideProgressDialog.disMissProgressDialog();
        traderAdapter.refreshData(traders);
    }

    private boolean isPopWindow = false;

    @SuppressLint("SimpleDateFormat")
    @Override
    public void popWindow(PopWindowBean popWindowBean, int gainStatus) {
        if (gainStatus == Constans.GAIN_DATA_SUCCESS && !isPopWindow) {
            //获取弹窗的数据
            ShowActivity.skipPopWindowAct(popWindowBean, getActivity(), "");
            isPopWindow = true;
        }
    }

    @Override
    public void RushCity(List<City> cityList) {
        ShowOrHideProgressDialog.disMissProgressDialog();
        cityAdapter.refreshData(cityList);
    }

    @Override
    public void RushScrollView() {
        if (homePullScrollview.isRefreshing()) {
            homePullScrollview.onRefreshComplete();
        }
    }

    @Override
    public void onErr(String msg) {
        if (!TextUtils.isEmpty(msg)) {
            if (ShowActivity.checkLoginStatus(getActivity(), msg)) {
                showToast(getString(R.string.login_err_tip));
                MatchSharedUtil.clearAuthToken();
            }
        }
    }

    @Override
    public void refreshMatchDesc(MatchDescBean.Result result) {
        if (result != null) {
            ShowActivity.showMatch4(getActivity(), result);
        }
    }

    @Override
    public void refreshMatchDescErr(String msg) {
    }

    @Override
    public void userWallet(UserWallet userWallet) {
        if (userWallet != null) {
            SharedpreferencesUtil.getInstance().putString(SharedKey.MFB_PAY, userWallet.getPay().getUnfrozen());
            SharedpreferencesUtil.getInstance().putString(SharedKey.MFB_POINT, userWallet.getPoint().getUnfrozen());
            SharedpreferencesUtil.getInstance().putString(SharedKey.MFB_COUPON, userWallet.getCoupon().getUnfrozen());
        }
    }

    @Override
    public void userWalletErr(String msg) {
    }

    @Override
    public void onImageClick(Adverts phoneAd, int position, View imageView) {
        //轮播图点击回调
        ShowActivity.circleImageClick(phoneAd, getActivity());
    }

    @OnClick({R.id.tv_trader_more,/* R.id.tv_city_more,*/ R.id.tv_create, R.id.kf_layout_id})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_trader_more:
                ShowActivity.showMatchRankActivity(getActivity(), Constans.MATCH_ID, Constans.TRADER_RANK_FLAG, false);
                break;
//            case R.id.tv_city_more:
//                MainActivity activity = (MainActivity) getActivity();
//                activity.onCityClick();
//                break;
            case R.id.tv_create:
                if (ShowActivity.isLogin(getActivity(), ParamsKey.MATCH_ORG_OTHER)) {
                    ShowActivity.showWebViewActivity(getActivity(), HtmlUrl.CREATE_MATCH,
                            getString(R.string.create_matchs));
                }
                break;
            case R.id.kf_layout_id:
                if (mDialog == null) {
                    mDialog = dialog1();
                }
                mDialog.show();
                break;
        }
    }

    /**
     * 操盘高手点击事件
     *
     * @param traders
     */
    @Override
    public void onItemClick(Traders traders) {
        if (ShowActivity.isLogin(getActivity(), ParamsKey.FG_HOME_V)) {
            Bundle bundle = new Bundle();
            bundle.putSerializable(IntentKey.TRADER, traders);
            ShowActivity.showActivity(getActivity(), bundle, MatchTraderInfoActivity.class);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (banner != null) {
            banner.isPause();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (banner != null) {
            banner.isPause();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mHandler != null) {
            mHandler.removeCallbacks(mRunnable);
            mHandler = null;
        }
    }

    private Dialog dialog1() {
        Dialog dialog = new Dialog(getActivity(), R.style.myDialogTheme);
        View layout = LayoutInflater.from(getActivity()).inflate(R.layout.kefu_layout, null);
        TextView qq_button = (TextView) layout.findViewById(R.id.qq_kefu_button);
        TextView phone_button = (TextView) layout.findViewById(R.id.phone_id_button);
        phone_button.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:4000607878"));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            mDialog.dismiss(); //
        });
        qq_button.setOnClickListener(v -> {
            String url = "mqqwpa://im/chat?chat_type=wpa&uin=2852273339&version=1";
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
            mDialog.dismiss(); //关闭dialog
        });
        dialog.setContentView(layout);
        Window win = dialog.getWindow();
        win.setGravity(Gravity.BOTTOM);
        WindowManager.LayoutParams lp = win.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        win.setAttributes(lp);
        win.setWindowAnimations(R.style.bottomDialogStyle);
        dialog.setCancelable(true);
        return dialog;
    }

    @Override
    public void cityClick(String matchID) {
        if (!TextUtils.isEmpty(matchID)) {
            HomePresenter.getInstance().getMatchDesc(Integer.parseInt(matchID));
        }
    }

    @Override
    public void sponsor(int position, Adverts ad) {
        //跳转赞转上链接
        if (ad != null) {
            if (!TextUtils.isEmpty(ad.getJump_url())) {
                ShowActivity.showWebViewActivity(getActivity(), ad.getJump_url(), "");
            }
        }
    }

    private int page, pages;
    public boolean first_onload_url, first_onload_bloglist, first_onload_screen;

    private void getBlog_message() {
        if (page == pages) {
            showToast("已加载全部数据");
            homePullScrollview.onRefreshComplete();
            return;
        }
        WebBase.searchUserBlogs(new JSONHandler() {
            @Override
            public void onSuccess(JSONObject obj) {
                JSONObject result = obj.optJSONObject("result");
                page = result.optInt("page");
                pages = result.optInt("pages");
                bloglist.clear();
                JSONArray blogs = result.optJSONArray("blogs");
                int size = blogs.length();
                for (int i = 0; i < size; i++) {
                    JSONObject blog = blogs.optJSONObject(i);
                    BlogBean blogBean = new BlogBean();
                    blogBean.setImg(blog.optString("cover"));
                    blogBean.setTitle(blog.optString("subject"));
                    blogBean.setDate(blog.optString("posted_at"));
                    blogBean.setLook_number(blog.optJSONObject("stat").optString("views"));
                    blogBean.setPinglun(blog.optJSONObject("stat").optString("replys"));
                    blogBean.setAvatar(blog.optJSONObject("user").optString("avatar"));
                    blogBean.setName(blog.optJSONObject("user").optString("nickname"));
                    blogBean.setApp_link(blog.optJSONObject("link").optString("app"));
                    blogBean.setWap_link(blog.optJSONObject("link").optString("wap"));
                    blogBean.setBlog_id(blog.optString("blog_id"));
                    bloglist.add(blogBean);
                }
                headadapter.notifyDataSetChanged();
                if (first_onload_bloglist) {
                    first_onload_bloglist = false;
                }
                RunshComplete();
            }

            @Override
            public void onFailure(String err_msg) {
                showToast(err_msg);
                if (first_onload_bloglist) {
                    first_onload_bloglist = false;
                }
                RunshComplete();
            }
        });
    }

    private void RunshComplete() {
        if (!first_onload_bloglist && !first_onload_screen && !first_onload_url && homePullScrollview.isRefreshing()) {
            homePullScrollview.onRefreshComplete();
        }
    }
}
