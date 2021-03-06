package com.zbmf.newmatch.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.zbmf.newmatch.R;
import com.zbmf.newmatch.adapter.MfbAdapter;
import com.zbmf.newmatch.api.JSONHandler;
import com.zbmf.newmatch.api.WebBase;
import com.zbmf.newmatch.bean.MFBean;
import com.zbmf.newmatch.common.Constans;
import com.zbmf.newmatch.util.DateUtil;
import com.zbmf.newmatch.util.MatchSharedUtil;
import com.zbmf.newmatch.util.MyActivityManager;
import com.zbmf.newmatch.util.ShowActivity;
import com.zbmf.newmatch.view.ListViewForScrollView;
import com.zbmf.worklibrary.presenter.BasePresenter;
import com.zbmf.worklibrary.pulltorefresh.PullToRefreshBase;
import com.zbmf.worklibrary.pulltorefresh.PullToRefreshScrollView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xuhao on 2017/2/20.
 */

public class MfbActivity extends BaseActivity implements View.OnClickListener {
    private TextView my_mfb_message,group_title_name;
    private ListViewForScrollView mfb_list;
    private MfbAdapter adapter;
    private List<MFBean>info;
    private PullToRefreshScrollView mfb_scrollview;
    private int page,pages;
    private LinearLayout ll_none;
    private TextView no_message_text,right_button;
    DecimalFormat df=new DecimalFormat("");
    DecimalFormat double_df =new DecimalFormat("######0.00");

    public void GroupinitData() {
        page=1;
        pages=0;
        if(info==null){
            info=new ArrayList<>();
        }else{
            info.clear();
        }
        adapter=new MfbAdapter(this,info);
        mfb_list.setAdapter(adapter);
        my_mfb_message.setText(MatchSharedUtil.getPays());
        getmfbMessage(true);
    }

    @Override
    protected int getLayout() {
        return R.layout.my_mfb_layout;
    }

    @Override
    protected String initTitle() {
        return "魔方宝";
    }

    @Override
    protected void initData(Bundle bundle) {
        //添加管理activity
        MyActivityManager.getMyActivityManager().pushAct(this);
        GroupinitView();
        GroupaddListener();
        GroupinitData();
    }

    @Override
    protected BasePresenter initPresent() {
        return null;
    }

    public void GroupaddListener() {
        right_button.setOnClickListener(this);
        mfb_scrollview.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ScrollView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ScrollView> refreshView) {
                updaData();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ScrollView> refreshView) {
                if(page==pages){
                    Toast.makeText(MfbActivity.this,"已加载全部数据",Toast.LENGTH_SHORT).show();
                    mfb_scrollview.onRefreshComplete();
                }else{
                    page+=1;
                    getmfbMessage(false);
                }
            }
        });
    }

    public void updaData() {
        page=1;
        pages=0;
        info.clear();
        my_mfb_message.setText(MatchSharedUtil.getPays());
        getmfbMessage(false);
    }
    @Override
    protected void onRestart() {
        super.onRestart();
        GroupinitData();
    }

    public void GroupinitView() {
        mfb_list= (ListViewForScrollView) findViewById(R.id.mfb_list);
        mfb_scrollview= (PullToRefreshScrollView) findViewById(R.id.mfb_scrollview);
        my_mfb_message= (TextView) findViewById(R.id.my_mfb_message);

        ll_none= (LinearLayout) findViewById(R.id.ll_none);
        no_message_text= (TextView) findViewById(R.id.no_message_text);
        no_message_text.setText(getString(R.string.no_mfb_msg));
        right_button=(TextView) getView(R.id.tv_right_button);
        right_button.setVisibility(View.VISIBLE);
        right_button.setText("去充值");

        mfb_scrollview.setMode(PullToRefreshBase.Mode.BOTH);
    }

    public String getDoubleormat(double vealue){
        if(double_df.format(vealue).contains(".00")){
            double ve=Double.valueOf(double_df.format(vealue));
            return df.format(ve);
        }else{
            return double_df.format(vealue);
        }
    }
    private void getmfbMessage(boolean is_show){
        WebBase.payLogs(page, new JSONHandler(is_show,MfbActivity.this,"正在加载...") {
            @Override
            public void onSuccess(JSONObject obj) {
                JSONObject result=obj.optJSONObject("result");
                JSONArray pay_logs=result.optJSONArray("pay_logs");
                int size=pay_logs.length();
                page=result.optInt("page");
                pages=result.optInt("pages");
                mfb_scrollview.onRefreshComplete();
                if(page==1&&size==0&&info.size()==0){
                    if(mfb_list.getVisibility()==View.VISIBLE){
                        mfb_list.setVisibility(View.GONE);
                    }
                    if(ll_none.getVisibility()==View.GONE){
                        ll_none.setVisibility(View.VISIBLE);
                    }
                }else{
                    if(mfb_list.getVisibility()==View.GONE){
                        mfb_list.setVisibility(View.VISIBLE);
                    }
                    if(ll_none.getVisibility()==View.VISIBLE){
                        ll_none.setVisibility(View.GONE);
                    }
                    for(int i=0;i<size;i++){
                        JSONObject pay=pay_logs.optJSONObject(i);
                        MFBean pb=new MFBean();
                        pb.setDate(pay.optString("created_at"));
                        pb.setMonth(DateUtil.mfbDate(pay.optString("created_at"), Constans.yy_MM_dd_HH_mm, Constans.MM_dd));
                        pb.setDay(DateUtil.mfbDate(pay.optString("created_at"),Constans.yy_MM_dd_HH_mm, Constans.HH_mm));
                        pb.setAction(pay.optInt("action"));
                        double pay_count=pay.optDouble("pay");
                        if(pay_count>0){
                            pb.setCount("+"+getDoubleormat(pay_count));
                        }else{
                            pb.setCount(""+getDoubleormat(pay_count));
                        }
                        pb.setNotes(pay.optString("notes"));
                        pb.setDesc(pay.optString("desc"));
                        info.add(pb);
                    }
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(String err_msg) {
                mfb_scrollview.onRefreshComplete();
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tv_right_button:
                ShowActivity.showPayDetailActivity(this);
                break;
        }
    }
}
