package com.zbmf.newmatch.util;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;

import com.bokecc.sdk.mobile.live.DWLive;
import com.bokecc.sdk.mobile.live.DWLiveLoginListener;
import com.bokecc.sdk.mobile.live.Exception.DWLiveException;
import com.bokecc.sdk.mobile.live.pojo.PublishInfo;
import com.bokecc.sdk.mobile.live.pojo.RoomInfo;
import com.bokecc.sdk.mobile.live.pojo.TemplateInfo;
import com.bokecc.sdk.mobile.live.pojo.Viewer;
import com.zbmf.newmatch.activity.VideoPlayActivity;
import com.zbmf.newmatch.api.JSONHandler;
import com.zbmf.newmatch.api.WebBase;
import com.zbmf.newmatch.bean.BlogBean;
import com.zbmf.newmatch.bean.Group;
import com.zbmf.newmatch.bean.Stock;
import com.zbmf.newmatch.bean.Video;
import com.zbmf.newmatch.common.Constans;
import com.zbmf.newmatch.common.HtmlUrl;
import com.zbmf.newmatch.common.IntentKey;
import com.zbmf.newmatch.common.RegConstans;
import com.zbmf.newmatch.common.RequestCode;
import com.zbmf.newmatch.listener.OnUrlClick;

import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by xuhao on 2017/8/2.
 */

public class WebClickUitl {

    public static void ShowActivity(final Activity activity,String url){
        Pattern blogP = Pattern.compile(RegConstans.blogReg);
        Matcher blogM = blogP.matcher(url);
        boolean message_find=blogM.find();
            if(message_find){
                WebBase.searchUserBlogInfo(blogM.group(1)+"-"+blogM.group(2), new JSONHandler() {
                    @Override
                    public void onSuccess(JSONObject obj) {
                        BlogBean blogBean=new BlogBean();
                        blogBean.setBlog_id(obj.optString("blog_id"));
                        blogBean.setTitle(obj.optString("subject"));
                        blogBean.setImg(obj.optString("cover"));
                        blogBean.setLook_number(obj.optJSONObject("stat").optString("views"));
                        blogBean.setPinglun(obj.optJSONObject("stat").optString("replys"));
                        blogBean.setWap_link(obj.optJSONObject("link").optString("wap"));
                        blogBean.setApp_link(obj.optJSONObject("link").optString("app"));
                        blogBean.setDate(obj.optString("posted_at"));
                        ShowActivity.showBlogDetailActivity(activity,blogBean);
                    }
                    @Override
                    public void onFailure(String err_msg) {

                    }
                });
            return;
        }
        Pattern groupP = Pattern.compile(RegConstans.groupReg);
        Matcher groupM = groupP.matcher(url);
        if(groupM.find()){
            Group group=new Group();
            group.setId(groupM.group(1));
            ShowActivity.showGroupDetailActivity(activity,group);
            return;
        }
        Pattern fansP = Pattern.compile(RegConstans.fansReg);
        Matcher fansM = fansP.matcher(url);
        if(fansM.find()){
            Group group=new Group();
            group.setId(fansM.group(1));
            ShowActivity.showGroupDetailActivity(activity,group);
            return;
        }
        Pattern payP = Pattern.compile(RegConstans.accountPay);
        Matcher payM = payP.matcher(url);
        if(payM.find()){
            ShowActivity.showPayDetailActivity(activity);
            return;
        }
        Pattern video=Pattern.compile(RegConstans.videoReg);
        Matcher videoM=video.matcher(url);
        if(videoM.find()){
            WebBase.GetVideoDetail(videoM.group(1), new JSONHandler() {
                @Override
                public void onSuccess(JSONObject obj) {
                    if(!obj.isNull("video")){
                        JSONObject jsonObject = obj.optJSONObject("video");
                        final Video video = JSONParse.getVideo(jsonObject);
                        if(video.getIs_live()){
                            DWLive.getInstance().setDWLiveLoginParams(new DWLiveLoginListener() {
                                @Override
                                public void onLogin(TemplateInfo templateInfo, Viewer viewer, RoomInfo roomInfo, PublishInfo publishInfo) {
                                    activity.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Bundle bundle=new Bundle();
                                            bundle.putSerializable(IntentKey.VIDEO_KEY,video);
                                            ShowActivity.showActivityForResult(activity,bundle, VideoPlayActivity.class, RequestCode.COMIT_VIDEO);
                                        }
                                    });
                                }

                                @Override
                                public void onException(final DWLiveException e) {
                                    activity.runOnUiThread(() -> {
                                        Bundle bundle=new Bundle();
                                        bundle.putSerializable(IntentKey.VIDEO_KEY,video);
                                        ShowActivity.showActivityForResult(activity,bundle, VideoPlayActivity.class, RequestCode.COMIT_VIDEO);
                                    });
                                }
                            }, Constans.CC_USERID,video.getBokecc_id()+"",MatchSharedUtil.NickName(),"");
                            DWLive.getInstance().startLogin();
                        }else{
                            Bundle bundle=new Bundle();
                            bundle.putSerializable(IntentKey.VIDEO_KEY,video);
                            ShowActivity.showActivity(activity,bundle, VideoPlayActivity.class);
                        }
                    }
                }

                @Override
                public void onFailure(String err_msg) {

                }
            });
            return;
        }
        Pattern stockP = Pattern.compile(RegConstans.stockReg);
        Matcher stockM = stockP.matcher(url);
        if(stockM.find()){
            WebBase.getStockRealtimeInfo(stockM.group(1), new JSONHandler() {
                @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                @Override
                public void onSuccess(JSONObject obj) {
//                    Stock stock=JSONParse.getStockRealtimeInfo(obj);
//                    Bundle bundle=new Bundle();
//                    bundle.putSerializable(IntentKey.STOCKHOLDER,stock);
//                    ShowActivity.showActivity(activity,bundle, SimulateOneStockCommitActivity.class);
                }

                @Override
                public void onFailure(String err_msg) {

                }
            });
            return;
        }
//        if(url.endsWith(HtmlUrl.SCREEN_STOCK)){
//            ShowActivity.showActivity(activity, ScreenActivity.class);
//            return;
//        }
//        if(url.endsWith(HtmlUrl.MODE_STOCK)){
//            ShowActivity.showActivity(activity, StockModeActivity.class);
//            return ;
//        }
//        if(url.endsWith(HtmlUrl.DMWD)){
//            ShowActivity.showActivity(activity, DongAskActivity.class);
//            return ;
//        }

        if(url.startsWith("http:")||url.startsWith("https:")){
            ShowActivity.showWebViewActivity(activity,url);
        }else{
            ShowActivity.showWebViewActivity(activity,"http://"+url);
        }
    }

    public static void UrlClick(final OnUrlClick onUrlClick, String url){
        Pattern blogP = Pattern.compile(RegConstans.blogReg);
        Matcher blogM = blogP.matcher(url);
        boolean message_find=blogM.find();
        if(message_find){
            WebBase.searchUserBlogInfo(blogM.group(1)+"-"+blogM.group(2), new JSONHandler() {
                @Override
                public void onSuccess(JSONObject obj) {
                    BlogBean blogBean=new BlogBean();
                    blogBean.setBlog_id(obj.optString("blog_id"));
                    blogBean.setTitle(obj.optString("subject"));
                    blogBean.setImg(obj.optString("cover"));
                    blogBean.setLook_number(obj.optJSONObject("stat").optString("views"));
                    blogBean.setPinglun(obj.optJSONObject("stat").optString("replys"));
                    blogBean.setWap_link(obj.optJSONObject("link").optString("wap"));
                    blogBean.setApp_link(obj.optJSONObject("link").optString("app"));
                    blogBean.setDate(obj.optString("posted_at"));
                    if(onUrlClick!=null){
                        onUrlClick.onBolg(blogBean);
                    }
                }
                @Override
                public void onFailure(String err_msg) {

                }
            });
            return;
        }
        Pattern groupP = Pattern.compile(RegConstans.groupReg);
        Matcher groupM = groupP.matcher(url);
        if(groupM.find()){
            Group group=new Group();
            group.setId(groupM.group(1));
            if(onUrlClick!=null){
                onUrlClick.onGroup(group);
            }
            return;
        }
        Pattern payP = Pattern.compile(RegConstans.accountPay);
        Matcher payM = payP.matcher(url);
        if(payM.find()){
            if(onUrlClick!=null){
                onUrlClick.onPay();
            }
            return;
        }
        Pattern video=Pattern.compile(RegConstans.videoReg);
        Matcher videoM=video.matcher(url);
        if(videoM.find()){
            WebBase.GetVideoDetail(videoM.group(1), new JSONHandler() {
                @Override
                public void onSuccess(JSONObject obj) {
                    if(!obj.isNull("video")){
                        JSONObject jsonObject = obj.optJSONObject("video");
                        if(onUrlClick!=null){
                            onUrlClick.onVideo( JSONParse.getVideo(jsonObject));
                        }
                    }
                }
                @Override
                public void onFailure(String err_msg) {

                }
            });
            return;
        }
        //图片
        if(url.indexOf("img:") == 0){
            String imageURL = url.substring(4);
            if(onUrlClick!=null){
                onUrlClick.onImage(imageURL);
            }
            return;
        }
        String[] web_url = url.split("/");
        if(web_url.length==4&&url.startsWith("app://group/")){
            //进入圈子
            Group group=new Group();
            group.setId(web_url[3]);
            if(onUrlClick!=null){
                onUrlClick.onGroup(group);
            }
            return;
        }
        if(web_url.length==7&&url.startsWith("app://app/people/")){
            WebBase.searchUserBlogInfo(web_url[4]+"-"+web_url[6], new JSONHandler() {
                @Override
                public void onSuccess(JSONObject obj) {
                    BlogBean blogBean=new BlogBean();
                    blogBean.setBlog_id(obj.optString("blog_id"));
                    blogBean.setTitle(obj.optString("subject"));
                    blogBean.setImg(obj.optString("cover"));
                    blogBean.setLook_number(obj.optJSONObject("stat").optString("views"));
                    blogBean.setPinglun(obj.optJSONObject("stat").optString("replys"));
                    blogBean.setWap_link(obj.optJSONObject("link").optString("wap"));
                    blogBean.setApp_link(obj.optJSONObject("link").optString("app"));
                    blogBean.setDate(obj.optString("posted_at"));
                    if(onUrlClick!=null){
                        onUrlClick.onBolg(blogBean);
                    }
                }
                @Override
                public void onFailure(String err_msg) {

                }
            });
            return;
        }
        Pattern stockP = Pattern.compile(RegConstans.stockReg);
        Matcher stockM = stockP.matcher(url);
        if(stockM.find()){
            WebBase.getStockRealtimeInfo(stockM.group(1), new JSONHandler() {
                @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                @Override
                public void onSuccess(JSONObject obj) {
                    Stock stock=JSONParse.getStockRealtimeInfo(obj);
                    if(onUrlClick!=null){
                        onUrlClick.onStock(stock);
                    }
                }

                @Override
                public void onFailure(String err_msg) {

                }
            });
            return;
        }
        if(url.endsWith(HtmlUrl.SCREEN_STOCK)){
            if(onUrlClick!=null)
                onUrlClick.onScreen();
            return;
        }
        if(url.endsWith(HtmlUrl.MODE_STOCK)){
            if(onUrlClick!=null)
                onUrlClick.onModeStock();
            return ;
        }
        if(url.endsWith(HtmlUrl.DMWD)){
            if(onUrlClick!=null)
                onUrlClick.onDongAsk();
            return ;
        }
        if(url.startsWith("http:")||url.startsWith("https:")){
            if(onUrlClick!=null){
                onUrlClick.onWeb(url);
            }
        }else{
            if(onUrlClick!=null){
                onUrlClick.onWeb("http://"+url);
            }
        }
    }
}
