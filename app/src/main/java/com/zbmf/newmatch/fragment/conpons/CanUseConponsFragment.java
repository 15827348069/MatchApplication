package com.zbmf.newmatch.fragment.conpons;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.zbmf.newmatch.R;
import com.zbmf.newmatch.adapter.MyCouponsAdapter;
import com.zbmf.newmatch.api.JSONHandler;
import com.zbmf.newmatch.api.WebBase;
import com.zbmf.newmatch.bean.CouponsBean;
import com.zbmf.newmatch.bean.Group;
import com.zbmf.newmatch.fragment.BaseFragment;
import com.zbmf.newmatch.util.JSONParse;
import com.zbmf.newmatch.util.ShowActivity;
import com.zbmf.worklibrary.presenter.BasePresenter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 未使用优惠券
 */

public class CanUseConponsFragment extends BaseFragment {
    private MyCouponsAdapter adapter;
    private ListView can_use_conpous;
    private CouponsBean couponsBean;
    private List<CouponsBean>infolist;
    private LinearLayout ll_none;
    private TextView no_message_text;
    public static CanUseConponsFragment newInstance() {
        CanUseConponsFragment fragment = new CanUseConponsFragment();
        return fragment;
    }
    @Override
    protected int getLayout() {
        return R.layout.canuse_fragment_layout;
    }

    @Override
    protected void initView() {
        can_use_conpous= (ListView) getView(R.id.can_use_coupons);
        ll_none=(LinearLayout) getView(R.id.ll_none);
        no_message_text=(TextView) getView(R.id.no_message_text);
        can_use_conpous.setOnItemClickListener((parent, view, position, id) -> {
            String use_id=infolist.get(position).getId();
            switch (use_id){
                case "0":
                    ShowActivity.skipGroupActivity(getActivity(),0);
//                        new GroupActivity().onPageSelected(1);
                    getActivity().finish();
//                        showToast("该优惠券可以在所有圈子使用");
                    break;
                default:
                    into_fansActivity(infolist.get(position).getId());
                    break;
            }
        });
    }
    @Override
    protected void initData() {
        infolist=new ArrayList<>();
        adapter=new MyCouponsAdapter(getActivity(),infolist);
        can_use_conpous.setAdapter(adapter);
        getUserCoupons();
    }

    @Override
    protected BasePresenter initPresent() {
        return null;
    }

    private void setLayoutMessage(int size){
        if(size==0){
            if(can_use_conpous.getVisibility()==View.VISIBLE){
                can_use_conpous.setVisibility(View.GONE);
            }
            if(ll_none.getVisibility()==View.GONE){
                ll_none.setVisibility(View.VISIBLE);
            }
            no_message_text.setText(getString(R.string.no_coupons_msg));
        }else{
            if(can_use_conpous.getVisibility()==View.GONE){
                can_use_conpous.setVisibility(View.VISIBLE);
            }
            if(ll_none.getVisibility()==View.VISIBLE){
                ll_none.setVisibility(View.GONE);
            }
        }
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
    public void into_fansActivity(String group_id){
        WebBase.fansInfo(group_id, new JSONHandler(true,getActivity(),"正在加载数据...") {
            @Override
            public void onSuccess(JSONObject obj) {
                JSONObject group=obj.optJSONObject("group");
                Group groupbean= JSONParse.getGroup(group);
                ShowActivity.showAddFansActivity(getActivity(),groupbean);
            }
            @Override
            public void onFailure(String err_msg) {
                showToast(err_msg);
            }
        });
    }
    private void getUserCoupons() {
        WebBase.getUserCoupons(null, new JSONHandler(true,getContext(),getString(R.string.loading)) {
            @Override
            public void onSuccess(JSONObject obj) {
                if (!obj.isNull("coupons")) {
                    JSONArray coupons = obj.optJSONArray("coupons");
                    infolist.addAll(JSONParse.getCouponsList(coupons));
                }
                int size = infolist.size();
                setLayoutMessage(size);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(String err_msg) {

            }
        });
    }
}
