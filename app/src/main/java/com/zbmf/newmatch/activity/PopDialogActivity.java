package com.zbmf.newmatch.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.zbmf.newmatch.R;
import com.zbmf.newmatch.bean.PopWindowBean;
import com.zbmf.newmatch.common.Constans;
import com.zbmf.newmatch.common.IntentKey;
import com.zbmf.newmatch.presenter.HomePresenter;
import com.zbmf.newmatch.util.MyActivityManager;
import com.zbmf.newmatch.util.ShowActivity;
import com.zbmf.newmatch.view.GlideOptionsManager;


public class PopDialogActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //添加管理activity
        MyActivityManager.getMyActivityManager().pushAct(this);
        //当前版本大于11时，点击dialog外部关闭dialog
        PopDialogActivity.this.setFinishOnTouchOutside(true);
        setContentView(R.layout.activity_pop_dialog);

        Display display = getWindow().getWindowManager().getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        int widthPixels = metrics.widthPixels;
        int heightPixels = metrics.heightPixels;

        widthPixels=widthPixels*5/6;
        heightPixels=heightPixels*3/5;
        getWindow().setLayout(widthPixels,heightPixels);
        getWindow().setGravity(Gravity.CENTER);
        ImageView popIv = findViewById(R.id.popDialogIv);
        Bundle extras = getIntent().getExtras();
        final PopWindowBean popWindowBean = (PopWindowBean) extras.getSerializable(IntentKey.POP_MSG);
        Glide.with(this).load(popWindowBean.getImg()).apply(GlideOptionsManager.getInstance()
                .getAngleOptions(0)).into(popIv);
        popIv.setOnClickListener(v -> {
            if (popWindowBean!=null){
                String url = popWindowBean.getUrl();
                    if (url.contains(Constans.MATCH_AD_TYPE)) {
                        //app://match/2121/
                        int h = url.lastIndexOf("h") + 2;
                        String adMatchId = "";
                        if (url.endsWith("/")) {
                            adMatchId = url.substring(h, url.length() + 1);
                        } else {
                            adMatchId = url.substring(h, url.length());
                        }
                        //如果是比赛类型的广告，则请求接口，判断是否已经参赛
                        HomePresenter.getInstance().getMatchDesc(Integer.parseInt(adMatchId));
                    } else {
                        ShowActivity.showWebViewActivity(PopDialogActivity.this, popWindowBean.getUrl(), "");
                    }
                PopDialogActivity.this.finish();
            }
        });
    }
}
