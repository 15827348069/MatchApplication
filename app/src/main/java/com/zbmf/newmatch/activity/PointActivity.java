package com.zbmf.newmatch.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.zbmf.newmatch.R;
import com.zbmf.newmatch.adapter.PointAdapter;
import com.zbmf.newmatch.api.JSONHandler;
import com.zbmf.newmatch.api.WebBase;
import com.zbmf.newmatch.bean.PointBean;
import com.zbmf.newmatch.util.MatchSharedUtil;
import com.zbmf.newmatch.view.ListViewForScrollView;
import com.zbmf.worklibrary.presenter.BasePresenter;
import com.zbmf.worklibrary.pulltorefresh.PullToRefreshBase;
import com.zbmf.worklibrary.pulltorefresh.PullToRefreshScrollView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xuhao on 2017/2/20.
 */

public class PointActivity extends BaseActivity {
    private TextView my_point_message,no_message_text;
    private ListViewForScrollView point_list;
    private PointAdapter adapter;
    private List<PointBean>info;
    private PullToRefreshScrollView point_scrollview;
    private int page,pages;
    private LinearLayout ll_none;

    @Override
    protected int getLayout() {
        return R.layout.my_point_layout;
    }

    @Override
    protected String initTitle() {
        return "积分";
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

    public void GroupinitData() {
        page=1;
        pages=0;
        if(info==null){
            info=new ArrayList<>();
        }else{
            info.clear();
        }
        adapter=new PointAdapter(this,info);
        point_list.setAdapter(adapter);
        my_point_message.setText(String.format("%d", MatchSharedUtil.getPoint()));
        getPointMessage(true);
    }

    public void addListener() {
        point_scrollview.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ScrollView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ScrollView> refreshView) {
                updata();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ScrollView> refreshView) {
                if(page==pages){
                    Toast.makeText(PointActivity.this,"已加载全部数据",Toast.LENGTH_SHORT).show();
                    point_scrollview.onRefreshComplete();
                }else{
                    page+=1;
                    getPointMessage(false);
                }
            }
        });
    }

    private void updata(){
        page=1;
        pages=0;
        info.clear();
        my_point_message.setText(String.format("%d",MatchSharedUtil.getPoint()));
        getPointMessage(false);
    }

    public void GroupinitView() {
        point_list= (ListViewForScrollView) findViewById(R.id.point_list);
        point_scrollview= (PullToRefreshScrollView) findViewById(R.id.point_scrollview);
        my_point_message= (TextView) findViewById(R.id.my_point_message);
        no_message_text= (TextView) findViewById(R.id.no_message_text);
        ll_none= (LinearLayout) findViewById(R.id.ll_none);
        point_scrollview.setMode(PullToRefreshBase.Mode.BOTH);
    }

    private void getPointMessage(boolean is_show){
        WebBase.pointLogs(page, new JSONHandler(is_show,PointActivity.this,"正在加载...") {
            @Override
            public void onSuccess(JSONObject obj) {
                JSONObject result=obj.optJSONObject("result");
                JSONArray point_logs=result.optJSONArray("point_logs");
                page=result.optInt("page");
                pages=result.optInt("pages");
                int size=point_logs.length();
                point_scrollview.onRefreshComplete();
                if(page==1&&size==0&&info.size()==0){
                    if(point_list.getVisibility()==View.VISIBLE){
                        point_list.setVisibility(View.GONE);
                    }
                    if(ll_none.getVisibility()==View.GONE){
                        ll_none.setVisibility(View.VISIBLE);
                    }
                    no_message_text.setText(getString(R.string.no_point_msg));
                }else{
                    if(point_list.getVisibility()==View.GONE){
                        point_list.setVisibility(View.VISIBLE);
                    }
                    if(ll_none.getVisibility()==View.VISIBLE){
                        ll_none.setVisibility(View.GONE);
                    }
                    for(int i=0;i<size;i++){
                        JSONObject p=point_logs.optJSONObject(i);
                        PointBean pb=new PointBean();
                        pb.setDate(p.optString("created_at"));
                        int earn_scores=p.optInt("earn_scores");
                        if(earn_scores>0){
                            pb.setCount("+"+earn_scores);
                        }else{
                            pb.setCount(""+earn_scores);
                        }
                        pb.setTitle(p.optString("notes"));
                        info.add(pb);
                    }
                    adapter.notifyDataSetChanged();
                }
                point_scrollview.onRefreshComplete();
            }

            @Override
            public void onFailure(String err_msg) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        StatService.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        StatService.onPause(this);
    }
}
