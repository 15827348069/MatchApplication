package com.zbmf.newmatch.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.zbmf.newmatch.R;
import com.zbmf.newmatch.activity.LookStockActivity;
import com.zbmf.newmatch.adapter.HeadMessageAdapter;
import com.zbmf.newmatch.api.JSONHandler;
import com.zbmf.newmatch.api.WebBase;
import com.zbmf.newmatch.bean.BlogBean;
import com.zbmf.newmatch.common.IntentKey;
import com.zbmf.newmatch.util.ShowActivity;
import com.zbmf.worklibrary.presenter.BasePresenter;
import com.zbmf.worklibrary.pulltorefresh.PullToRefreshBase;
import com.zbmf.worklibrary.pulltorefresh.PullToRefreshListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xuhao on 2017/2/24.
 */

public class UserCollectsBlogFragment extends BaseFragment {
    private PullToRefreshListView user_collects_blog_list;
    private HeadMessageAdapter adapter;
    private List<BlogBean>infolist;
    private int page,pages;
    private boolean isFirst=true;
    private LinearLayout ll_none;
    private TextView no_message_text,right_button,left_button;
    public static UserCollectsBlogFragment newInstance() {
        UserCollectsBlogFragment fragment = new UserCollectsBlogFragment();
        return fragment;
    }

    @Override
    protected int getLayout() {
        return R.layout.usercollectsbloglayout;
    }

    @Override
    protected void initView() {
        user_collects_blog_list=(PullToRefreshListView) getView(R.id.user_collects_blog_list);
        infolist=new ArrayList<>();
        adapter=new HeadMessageAdapter(getContext(),infolist);
        user_collects_blog_list.setAdapter(adapter);
        user_collects_blog_list.setOnItemClickListener((adapterView, view, i, l) -> {
            BlogBean bb=infolist.get(i-1);
            if(bb.is_delete()){
                showToast("该博文已被删除");
            }else{
                ShowActivity.showBlogDetailActivity(getActivity(),bb);
            }
        });
        user_collects_blog_list.setMode(PullToRefreshBase.Mode.BOTH);
        user_collects_blog_list.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                //下拉刷新
                initData();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                //上拉加载
                page+=1;
                getBlog_message();
            }
        });
        ll_none=(LinearLayout) getView(R.id.ll_none);
        no_message_text=(TextView)getView(R.id.no_message_text);
        right_button=(TextView) getView(R.id.tv_right_button);
        left_button=(TextView) getView(R.id.tv_left_button);
        no_message_text.setText(getString(R.string.no_msg));
        right_button.setVisibility(View.VISIBLE);
        right_button.setText(getString(R.string.to_original));
        right_button.setOnClickListener(v -> {
            Bundle bundle=new Bundle();
            bundle.putInt(IntentKey.FLAG,2);
            ShowActivity.showActivity(getActivity(),bundle,LookStockActivity.class);
        });
        left_button.setVisibility(View.VISIBLE);
        left_button.setText(getString(R.string.to_stock));
        left_button.setOnClickListener(v -> ShowActivity.showActivity(getActivity(), LookStockActivity.class));

    }

    @Override
    protected void initData() {
        page=1;
        pages=0;
        infolist.clear();
        getBlog_message();
    }

    @Override
    protected BasePresenter initPresent() {
        return null;
    }

    public void getBlog_message(){
        WebBase.getUserCollects(page,new JSONHandler(isFirst,getActivity(),getString(R.string.loading)) {
            @Override
            public void onSuccess(JSONObject obj) {
                JSONObject result=obj.optJSONObject("result");
                page=result.optInt("page");
                pages=result.optInt("pages");
                JSONArray blogs=result.optJSONArray("collects");
                int size=blogs.length();
                for(int i=0;i<size;i++){
                    JSONObject blog_detail=blogs.optJSONObject(i);
                    BlogBean blogBean=new BlogBean();
                    if(blog_detail.optInt("is_delete")==0){
                        JSONObject blog=blog_detail.optJSONObject("object");
                        blogBean.setImg(blog.optString("cover"));
                        blogBean.setTitle(blog.optString("subject"));
                        blogBean.setDate(blog.optString("posted_at"));
                        blogBean.setBlog_id(blog.optString("blog_id"));
                        blogBean.setApp_link(blog.optJSONObject("link").optString("app"));
                        blogBean.setWap_link(blog.optJSONObject("link").optString("wap"));
                        if(!blog.isNull("user")){
                            blogBean.setAvatar(blog.optJSONObject("user").optString("avatar"));
                            blogBean.setName(blog.optJSONObject("user").optString("nickname"));
                        }
                        blogBean.setIs_collects(false);
                    }else{
                        blogBean.setIs_delete(true);
                    }
                    infolist.add(blogBean);
                }
                adapter.notifyDataSetChanged();
                user_collects_blog_list.onRefreshComplete();
                if(page==pages&&!isFirst){
                    showToast("已加载全部数据");
                    user_collects_blog_list.onRefreshComplete();
                }
                if(infolist.size()==0){
                    ll_none.setVisibility(View.VISIBLE);
                }else{
                    ll_none.setVisibility(View.GONE);
                }
                if(isFirst){
                    isFirst=false;
                }
            }

            @Override
            public void onFailure(String err_msg) {
                showToast(err_msg);
                if(isFirst){
                    isFirst=false;
                }
                user_collects_blog_list.onRefreshComplete();
            }
        });
    }
}
