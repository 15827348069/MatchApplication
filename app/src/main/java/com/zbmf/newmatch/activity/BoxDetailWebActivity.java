package com.zbmf.newmatch.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bokecc.sdk.mobile.live.DWLive;
import com.bokecc.sdk.mobile.live.DWLiveLoginListener;
import com.bokecc.sdk.mobile.live.Exception.DWLiveException;
import com.bokecc.sdk.mobile.live.pojo.PublishInfo;
import com.bokecc.sdk.mobile.live.pojo.RoomInfo;
import com.bokecc.sdk.mobile.live.pojo.TemplateInfo;
import com.bokecc.sdk.mobile.live.pojo.Viewer;
import com.zbmf.newmatch.R;
import com.zbmf.newmatch.api.JSONHandler;
import com.zbmf.newmatch.api.WebBase;
import com.zbmf.newmatch.bean.BlogBean;
import com.zbmf.newmatch.bean.BoxBean;
import com.zbmf.newmatch.bean.Group;
import com.zbmf.newmatch.bean.Screen;
import com.zbmf.newmatch.bean.Stock;
import com.zbmf.newmatch.bean.Video;
import com.zbmf.newmatch.common.Constans;
import com.zbmf.newmatch.common.IntentKey;
import com.zbmf.newmatch.common.RequestCode;
import com.zbmf.newmatch.listener.OnUrlClick;
import com.zbmf.newmatch.util.BoxDetailHtml;
import com.zbmf.newmatch.util.MatchSharedUtil;
import com.zbmf.newmatch.util.SettingDefaultsManager;
import com.zbmf.newmatch.util.ShowActivity;
import com.zbmf.newmatch.webclient.GroupWebViewClient;
import com.zbmf.worklibrary.presenter.BasePresenter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

//查看宝盒
public class BoxDetailWebActivity extends BaseActivity implements View.OnClickListener, OnUrlClick {
    private String boxId = "",group_id="";
    private int page,pages;
    private BoxBean mBoxBean;
    private WebView box_webview;
    TextView box_textView_pageStatus = null,tv_title;
    Button tv_right=null;
    // 首页
    Button box_button_firstPage = null;

    // 上一页
    Button box_button_priorPage = null;

    // 下一页
    Button box_button_nextPage = null;

    // 末页
    Button box_button_endPage = null;

    @Override
    protected int getLayout() {
        return R.layout.activity_box_web_detail;
    }

    @Override
    protected String initTitle() {
        return null;
    }

    @Override
    protected void initData(Bundle bundle) {

        GroupinitView();
        addListener();
        GroupinitData();



    }

    @Override
    protected BasePresenter initPresent() {
        return null;
    }

    public void GroupinitView() {
        initTitle();
        box_webview= (WebView) findViewById(R.id.box_webView_content);
        // 页面状态
        box_textView_pageStatus = (TextView) findViewById(R.id.box_textView_pageStatus);
        // 首页
        box_button_firstPage = (Button) findViewById(R.id.box_button_firstPage);
        // 上一页
        box_button_priorPage = (Button) findViewById(R.id.box_button_priorPage);
        // 下一页
        box_button_nextPage = (Button) findViewById(R.id.box_button_nextPage);
        // 末页
        box_button_endPage = (Button) findViewById(R.id.box_button_endPage);
        WebSettings webSettings = box_webview.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setSaveFormData(true);
        webSettings.setSavePassword(false);
        webSettings.setSupportZoom(false);
        box_webview.setScrollbarFadingEnabled(true);
        box_webview.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        box_webview.setWebViewClient(new GroupWebViewClient().setUrlClick(this));
        tv_right= (Button) findViewById(R.id.group_title_right_button);
        tv_right.setText("简介");
        tv_right.setVisibility(View.VISIBLE);
        tv_right.setOnClickListener(this);
    }

    public void GroupinitData() {
        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            mBoxBean = (BoxBean) bundle.getSerializable("BoxBean");
            if(mBoxBean!=null){
                boxId = mBoxBean.getBox_id();
                group_id=mBoxBean.getId();
                initTitle(mBoxBean.getSubject());
            }else{
                mBoxBean=new BoxBean();
                group_id=getIntent().getStringExtra("group_id");
                boxId=getIntent().getStringExtra("box_id");
                getBoxDetail();
            }
        }
        getGroupBoxInfo(1);
    }

    public void addListener() {
        box_button_firstPage.setOnClickListener(this);
        box_button_priorPage.setOnClickListener(this);
        box_button_nextPage.setOnClickListener(this);
        box_button_endPage.setOnClickListener(this);
    }

    public void getBoxDetail(){
        WebBase.getBoxInfo(group_id,boxId,page, new JSONHandler(){
            @Override
            public void onSuccess(JSONObject obj) {
                JSONObject box=obj.optJSONObject("box");
                mBoxBean.setBox_id(boxId);
                mBoxBean.setId(group_id);
                mBoxBean.setSubject(box.optString("subject"));
                initTitle(mBoxBean.getSubject());
            }

            @Override
            public void onFailure(String err_msg) {
                Toast.makeText(getBaseContext(),err_msg,Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getGroupBoxInfo(int dir) {
        WebBase.getGroupBoxItems(group_id,boxId, dir, Constans.PER_PAGE,
 new JSONHandler(true,BoxDetailWebActivity.this,"正在获取数据...") {
            @Override
            public void onSuccess(JSONObject obj) {
                try {
                    JSONObject o = obj.getJSONObject("result");
                    page=o.optInt("page");
                    pages=o.optInt("pages");
                    JSONArray arr = o.getJSONArray("items");
                    String countent= BoxDetailHtml.getBoxHtml(arr);
                    box_webview.loadDataWithBaseURL(null, countent, "text/html", "utf-8", null);
                    box_textView_pageStatus.setText(page + " / " + pages);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(String err_msg) {
                Toast toast = Toast.makeText(BoxDetailWebActivity.this, err_msg, Toast.LENGTH_SHORT);
                toast.show();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.group_title_right_button:
                ShowActivity.showBoxDetailDescActivity(BoxDetailWebActivity.this,  mBoxBean);
                break;
            // 首页
            case R.id.box_button_firstPage:
            {
                if (page == 1)
                {
                    Toast toast = Toast.makeText(BoxDetailWebActivity.this,
                            "已到达第一页", Toast.LENGTH_SHORT);
                    toast.show();
                } else {
                    getGroupBoxInfo(1);
                }
            }
            break;

            // 上一页
            case R.id.box_button_priorPage:
            {
                if (page == 1)
                {
                    Toast toast = Toast.makeText(BoxDetailWebActivity.this, "已到达第一页", Toast.LENGTH_SHORT);
                    toast.show();
                } else {
                    getGroupBoxInfo(page - 1);
                }
            }
            break;

            // 下一页
            case R.id.box_button_nextPage:
            {
                if (page == pages)
                {
                    Toast toast = Toast.makeText(BoxDetailWebActivity.this, "已到达最后一页", Toast.LENGTH_SHORT);
                    toast.show();

                } else {
                    getGroupBoxInfo(page + 1);
                }
            }
            break;

            // 末页
            case R.id.box_button_endPage:
            {
                if (page == pages)
                {
                    Toast toast = Toast.makeText(BoxDetailWebActivity.this, "已到达最后一页", Toast.LENGTH_SHORT);
                    toast.show();
                } else {
                    getGroupBoxInfo(pages);
                }
            }
            break;
        }
    }
    @Override
    public void onGroup(Group group) {
        ShowActivity.showGroupDetailActivity(this, group);
    }

    @Override
    public void onBolg(BlogBean blogBean) {
        ShowActivity.showBlogDetailActivity(this, blogBean);
    }

    @Override
    public void onVideo(Video video) {
        toVideo(video);
    }

    @Override
    public void onWeb(String url) {
        ShowActivity.showWebViewActivity(this,url);
    }

    @Override
    public void onImage(String url) {
        ShowActivity.ShowBigImage(this, url);
    }

    @Override
    public void onPay() {
        ShowActivity.showPayDetailActivity(this);
    }

    @Override
    public void onStock(Stock stock) {
//        Bundle bundle=new Bundle();
//        bundle.putSerializable(IntentKey.STOCKHOLDER,stock);
//        ShowActivity.showActivity(this,bundle, SimulateOneStockCommitActivity.class);
    }
    @Override
    public void onQQ(String url) {
        ShowActivity.showQQ(this);
    }
    @Override
    public void onScreen() {
//        ShowActivity.showActivity(this,ScreenActivity.class);
    }
    @Override
    public void onScreenDetail(Screen screen) {
//        Bundle bundle=new Bundle();
//        bundle.putSerializable(IntentKey.SCREEN,screen);
//        ShowActivity.showActivity(this,bundle,ScreenDetailActivity.class);
    }
    @Override
    public void onModeStock() {
//        ShowActivity.showActivity(this,StockModeActivity.class);
    }

    @Override
    public void onDongAsk() {
//        ShowActivity.showActivity(this,DongAskActivity.class);
    }

    private void toVideo(final Video video) {
        if (video.getIs_live()) {
            DWLive.getInstance().setDWLiveLoginParams(new DWLiveLoginListener() {
                @Override
                public void onLogin(TemplateInfo templateInfo, Viewer viewer, RoomInfo roomInfo, PublishInfo publishInfo) {
                    runOnUiThread(() -> {
                        Bundle bundle = new Bundle();
                        bundle.putSerializable(IntentKey.VIDEO_KEY, video);
                        ShowActivity.showActivityForResult(BoxDetailWebActivity.this, bundle,
                                VideoPlayActivity.class, RequestCode.COMIT_VIDEO);
                    });
                }

                @Override
                public void onException(final DWLiveException e) {
                    runOnUiThread(() -> {
                        Bundle bundle = new Bundle();
                        bundle.putSerializable(IntentKey.VIDEO_KEY, video);
                        ShowActivity.showActivityForResult(BoxDetailWebActivity.this, bundle,
                                VideoPlayActivity.class, RequestCode.COMIT_VIDEO);
                    });
                }
            }, Constans.CC_USERID, video.getBokecc_id() + "",
                    MatchSharedUtil.NickName(), "");
            DWLive.getInstance().startLogin();
        } else {
            Bundle bundle = new Bundle();
            bundle.putSerializable(IntentKey.VIDEO_KEY, video);
            ShowActivity.showActivity(BoxDetailWebActivity.this, bundle, VideoPlayActivity.class);
        }
    }
}
