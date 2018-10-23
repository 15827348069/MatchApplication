package com.zbmf.newmatch.util;

import android.content.Context;
import android.content.Intent;

import com.zbmf.newmatch.activity.BlogDetailActivity;
import com.zbmf.newmatch.activity.GroupDetailActivity;
import com.zbmf.newmatch.activity.PayDetailActivity;
import com.zbmf.newmatch.activity.VideoEmptyActivity;
import com.zbmf.newmatch.activity.WebViewActivity;
import com.zbmf.newmatch.bean.BlogBean;
import com.zbmf.newmatch.bean.Group;
import com.zbmf.newmatch.common.IntentKey;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by xuhao on 2017/8/2.
 */

public class WebNoticitionUitl {
    private static final String blogReg="people\\/(\\d+)\\/blog\\/(\\d+)";
    private static final String groupReg="people\\/(\\d+)\\/live";
    private static final String accountPay="account/trade/pay/";
    private static final String videoReg="school\\/(\\d+)\\/video_play";
    public static Intent ShowActivity(final Context activity, String url){
        Pattern blogP = Pattern.compile(blogReg);
        Matcher blogM = blogP.matcher(url);
        boolean message_find=blogM.find();
       if(message_find){
                Intent intent = new Intent(activity,BlogDetailActivity.class);
                BlogBean blogBean=new BlogBean();
                blogBean.setBlog_id(blogM.group(1)+"-"+blogM.group(2));
                intent.putExtra("blogBean",blogBean);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                return intent;
        }
        Pattern groupP = Pattern.compile(groupReg);
        Matcher groupM = groupP.matcher(url);
        if(groupM.find()){
            Group group_tp=new Group();
            group_tp.setId(groupM.group(1));
            Intent intent=new Intent(activity, GroupDetailActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(IntentKey.GROUP,group_tp);
            return intent;
        }
        Pattern payP = Pattern.compile(accountPay);
        Matcher payM = payP.matcher(url);
        if(payM.find()){
            return new Intent(activity, PayDetailActivity.class);
        }
        Pattern video=Pattern.compile(videoReg);
        Matcher videoM=video.matcher(url);
        if(videoM.find()){
            Intent intent=new Intent(activity, VideoEmptyActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(IntentKey.VIDEO_KEY,videoM.group(1));
            return intent;
        }
        Intent intent = new Intent(activity,WebViewActivity.class);
        intent.putExtra("web_url",url);
        return intent;
    }
}
