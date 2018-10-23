package com.zbmf.newmatch.fragment.conpons;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.zbmf.newmatch.R;
import com.zbmf.newmatch.adapter.MyCouponsAdapter;
import com.zbmf.newmatch.api.AppUrl;
import com.zbmf.newmatch.api.JSONHandler;
import com.zbmf.newmatch.api.WebBase;
import com.zbmf.newmatch.bean.CouponsBean;
import com.zbmf.newmatch.common.IntentKey;
import com.zbmf.newmatch.fragment.BaseFragment;
import com.zbmf.newmatch.util.JSONParse;
import com.zbmf.worklibrary.presenter.BasePresenter;
import com.zbmf.worklibrary.pulltorefresh.PullToRefreshBase;
import com.zbmf.worklibrary.pulltorefresh.PullToRefreshListView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 已使用、过期的优惠券
 */

public class LoseTimeConponsFragment extends BaseFragment {
    private MyCouponsAdapter adapter;
    private PullToRefreshListView louse_use_coupons;
    private List<CouponsBean> couponslist;
    private int page,pages;
    private LinearLayout ll_none;
    private TextView no_message_text;
    private boolean isFirst=true,isRush;
    private String method;
    public static LoseTimeConponsFragment newInstance(String method) {
        LoseTimeConponsFragment fragment = new LoseTimeConponsFragment();
        Bundle bundle=new Bundle();
        bundle.putString(IntentKey.METHOD,method);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments()!=null){
            method=getArguments().getString(IntentKey.METHOD, AppUrl.getHistCoupons);
        }
    }

    @Override
    protected int getLayout() {
        return R.layout.losttime_fragment_layout;
    }

    @Override
    protected void initView() {
        ll_none=(LinearLayout) getView(R.id.ll_none);
        no_message_text=(TextView) getView(R.id.no_message_text);
        louse_use_coupons=(PullToRefreshListView) getView(R.id.louse_use_coupons);
        louse_use_coupons.setMode(PullToRefreshBase.Mode.BOTH);
        louse_use_coupons.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                isRush=true;
                page=1;
                initData();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                page+=1;
                initData();
            }
        });
        page=1;
        couponslist=new ArrayList<>();
        adapter=new MyCouponsAdapter(getActivity(),couponslist);
        louse_use_coupons.setAdapter(adapter);
    }

    @Override
    protected void initData() {
        WebBase.getCoupons(method,page, new JSONHandler(isFirst,getActivity(),getString(R.string.loading)) {
            @Override
            public void onSuccess(JSONObject obj) {
                if(!obj.isNull("result")){
                    JSONObject result=obj.optJSONObject("result");
                    page=result.optInt("page");
                    pages=result.optInt("pages");
                    if(isRush){
                        isRush=false;
                        couponslist.clear();
                    }
                    if(!result.isNull("coupons")){
                        couponslist.addAll(JSONParse.getCouponsList(result.optJSONArray("coupons")));
                    }
                }
                if(couponslist.size()==0){
                    if(louse_use_coupons.getVisibility()==View.VISIBLE){
                        louse_use_coupons.setVisibility(View.INVISIBLE);
                    }
                    if(ll_none.getVisibility()==View.GONE){
                        ll_none.setVisibility(View.VISIBLE);
                    }
                    no_message_text.setText(getString(R.string.no_coupons_msg));
                }else{
                    if(louse_use_coupons.getVisibility()==View.INVISIBLE){
                        louse_use_coupons.setVisibility(View.VISIBLE);
                    }
                    if(ll_none.getVisibility()==View.VISIBLE){
                        ll_none.setVisibility(View.GONE);
                    }
                    adapter.notifyDataSetChanged();
                }
                louse_use_coupons.onRefreshComplete();
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
    protected BasePresenter initPresent() {
        return null;
    }
}
