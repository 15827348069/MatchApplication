package com.zbmf.newmatch.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.zbmf.newmatch.R;
import com.zbmf.newmatch.activity.FansDiscountsActivity;
import com.zbmf.newmatch.activity.FindTeacherActivity;
import com.zbmf.newmatch.adapter.BoxItemAdapter;
import com.zbmf.newmatch.api.JSONHandler;
import com.zbmf.newmatch.api.WebBase;
import com.zbmf.newmatch.bean.BoxBean;
import com.zbmf.newmatch.common.RequestCode;
import com.zbmf.newmatch.util.JSONParse;
import com.zbmf.newmatch.util.ShowActivity;
import com.zbmf.worklibrary.presenter.BasePresenter;
import com.zbmf.worklibrary.pulltorefresh.PullToRefreshBase;
import com.zbmf.worklibrary.pulltorefresh.PullToRefreshListView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * 铁粉
 */
public class TieFansFragment extends BaseFragment {
    private int page,pages;
    private List<BoxBean>infolist;
    private BoxItemAdapter adapter;
    private PullToRefreshListView listview;
    private boolean isFirst=true;
    private LinearLayout ll_none;
    private TextView no_message_text,right_button,left_button;
    public static TieFansFragment newInstance() {
        TieFansFragment fragment = new TieFansFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayout() {
        return R.layout.fragment_coupon_used;
    }

    @Override
    protected void initView() {

        ll_none=(LinearLayout) getView(R.id.ll_none);
        no_message_text=(TextView) getView(R.id.no_message_text);
        right_button=(TextView) getView(R.id.tv_right_button);
        left_button=(TextView)getView(R.id.tv_left_button);
        no_message_text.setText(getString(R.string.no_msg));
        right_button.setVisibility(View.VISIBLE);
        right_button.setText(getString(R.string.fans_coupons));
        right_button.setOnClickListener(v -> ShowActivity.showActivity(getActivity(), FansDiscountsActivity.class));
        left_button.setVisibility(View.VISIBLE);
        left_button.setText(getString(R.string.find_teacher));
        left_button.setOnClickListener(v ->
                ShowActivity.showActivityForResult(getActivity(),null,
                        FindTeacherActivity.class, RequestCode.STUDY));
        listview=(PullToRefreshListView) getView(R.id.listview);
        infolist=new ArrayList<>();
        adapter=new BoxItemAdapter(getContext(),infolist);
        listview.setAdapter(adapter);
        listview.setOnItemClickListener((parent, view, position, id) ->
                ShowActivity.showBoxDetailActivity(getActivity(),infolist.get(position-1)));
        listview.setMode(PullToRefreshBase.Mode.BOTH);
        listview.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                //下拉刷新
                initData();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                //上拉加载
                page+=1;
                getFans();
            }
        });
    }

    @Override
    protected void initData() {
        if(infolist==null){
            infolist=new ArrayList<>();
        }else{
            infolist.clear();
        }
        page=1;
        getFans();
    }

    @Override
    protected BasePresenter initPresent() {
        return null;
    }

    public void getFans(){
        WebBase.getUserBoxs("0", "0", page, new JSONHandler(isFirst,getActivity(),getString(R.string.loading)) {
            @Override
            public void onSuccess(JSONObject obj) {
                BoxBean bb = JSONParse.getGroupBoxs(obj);
                if(bb!=null && bb.getList()!=null ){
                    infolist.addAll(bb.getList());
                }
                adapter.notifyDataSetChanged();
                listview.onRefreshComplete();
                if(infolist.size()==0){
                    page=bb.getPage();
                    pages=bb.getPages();
                    if(ll_none.getVisibility()==View.GONE){
                        ll_none.setVisibility(View.VISIBLE);
                    }
                }else{
                    if(ll_none.getVisibility()==View.VISIBLE){
                        ll_none.setVisibility(View.GONE);
                    }
                }
                if(isFirst){
                    isFirst=false;
                }
            }

            @Override
            public void onFailure(String err_msg) {
                if(isFirst){
                    isFirst=false;
                }
            }
        });
    }
    @Override
    public void onDetach() {
        super.onDetach();
    }

}
