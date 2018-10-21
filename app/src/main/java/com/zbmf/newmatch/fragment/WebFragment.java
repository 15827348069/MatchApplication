package com.zbmf.newmatch.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.zbmf.newmatch.R;
import com.zbmf.newmatch.common.IntentKey;
import com.zbmf.newmatch.util.WebClickUitl;
import com.zbmf.worklibrary.presenter.BasePresenter;


/**
 * Created by xuhao on 2017/8/25.
 */

public class WebFragment extends BaseFragment {
    private WebView jianghu_webview;
    private String URL;
    public static WebFragment newInstance(String url){
        WebFragment fragment=new WebFragment();
        Bundle bundle=new Bundle();
        bundle.putString(IntentKey.WEB_URL,url);
        fragment.setArguments(bundle);
        return  fragment;
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle=getArguments();
        if(bundle!=null){
            URL=bundle.getString(IntentKey.WEB_URL,"");
        }
    }

//    @Override
//    protected View setContentView(LayoutInflater inflater) {
//        return inflater.inflate(R.layout.fragment_webview_layout,null);
//    }

    @Override
    protected int getLayout() {
        return R.layout.fragment_webview_layout;
    }

    @Override
    protected void initView() {
        jianghu_webview= (WebView) getView(R.id.jianghu_webview);
        final WebSettings settings=jianghu_webview.getSettings();
        settings.setJavaScriptEnabled(true);
        // 设置可以支持缩放
        settings.setSupportZoom(true);
        // 设置出现缩放工具
        settings.setBuiltInZoomControls(false);
        //扩大比例的缩放
        settings.setUseWideViewPort(true);
        //自适应屏幕
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        settings.setLoadWithOverviewMode(true);
        settings.setDomStorageEnabled(true);
        settings.setAppCacheMaxSize(1024*1024*8);
        String appCachePath = getContext().getCacheDir().getAbsolutePath();
        settings.setAppCachePath(appCachePath);
        settings.setAllowFileAccess(true);
        settings.setAppCacheEnabled(true);
        jianghu_webview.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        String agent=settings.getUserAgentString();
        settings.setUserAgentString(agent.replace("Android", "StockGroup"));
        final SwipeRefreshLayout swipeRefreshLayout= (SwipeRefreshLayout)getView(R.id.swiperefresh_id);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            //重新刷新页面
            jianghu_webview.reload();
            if(swipeRefreshLayout.isRefreshing()){
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        jianghu_webview.setWebChromeClient(new WebChromeClient(){
            //设置加载进度
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if(newProgress==100){
                    dialogDiss();
                }else{
                    dialogShow();
                }
            }
        });

        jianghu_webview.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                WebClickUitl.ShowActivity(getActivity(),url);
                return true;
            }
        });
        jianghu_webview.setOnLongClickListener(view -> true);
    }

    @Override
    protected void initData() {
        jianghu_webview.loadUrl(URL);
    }

    @Override
    protected BasePresenter initPresent() {
        return null;
    }
}
