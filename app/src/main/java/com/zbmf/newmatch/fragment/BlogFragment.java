package com.zbmf.newmatch.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;


import com.zbmf.newmatch.R;
import com.zbmf.newmatch.adapter.DynamicAdapter;
import com.zbmf.newmatch.api.JSONHandler;
import com.zbmf.newmatch.api.WebBase;
import com.zbmf.newmatch.bean.BlogBean;
import com.zbmf.newmatch.bean.NewsFeed;
import com.zbmf.newmatch.common.Constans;
import com.zbmf.newmatch.common.IntentKey;
import com.zbmf.newmatch.util.JSONParse;
import com.zbmf.newmatch.util.ShowActivity;
import com.zbmf.worklibrary.presenter.BasePresenter;
import com.zbmf.worklibrary.pulltorefresh.PullToRefreshBase;
import com.zbmf.worklibrary.pulltorefresh.PullToRefreshListView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xuhao on 2017/8/17.
 */

public class BlogFragment extends BaseFragment{
    private PullToRefreshListView lv_tief;
    private DynamicAdapter dynamicAdapter;
    private int flag;
    private List<NewsFeed> mNewsFeeds = new ArrayList<NewsFeed>();
    private int page,pages;
    private boolean isFirst,isRush;
    public static BlogFragment newInstance(int flag){
        BlogFragment fragment=new BlogFragment();
        Bundle bundle=new Bundle();
        bundle.putInt(IntentKey.FLAG,flag);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments()!=null){
            flag=getArguments().getInt(IntentKey.FLAG);
        }
    }

//    @Override
//    protected View setContentView(LayoutInflater inflater) {
//        return inflater.inflate(R.layout.fragment_blog_layout,null);
//    }

    @Override
    protected int getLayout() {
        return R.layout.fragment_blog_layout;
    }

    @Override
    protected void initView() {
        lv_tief=(PullToRefreshListView) getView(R.id.lv_focus);
        lv_tief.setMode(PullToRefreshBase.Mode.BOTH);
        dynamicAdapter=new DynamicAdapter(getContext(),mNewsFeeds);
        dynamicAdapter.setFlag(flag);
        lv_tief.setAdapter(dynamicAdapter);
        lv_tief.setOnItemClickListener((parent, view, position, id) -> {
            NewsFeed newsFeed = mNewsFeeds.get(position-1);
            BlogBean blog = new BlogBean();
            blog.setBlog_id(newsFeed.getBlog_id());
            blog.setApp_link(newsFeed.getLink().getApp());
            blog.setWap_link(newsFeed.getLink().getWap());
            blog.setImg(newsFeed.getCover());
            blog.setTitle(newsFeed.getSubject());
            blog.setLook_number(newsFeed.getStat().getViews());
            blog.setPinglun(newsFeed.getStat().getReplys());
            blog.setDate(newsFeed.getChanged_at());
            ShowActivity.showBlogDetailActivity(getActivity(), blog);
        });
        lv_tief.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                RushList();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                getMoreList();
            }
        });
    }

    @Override
    protected void initData() {
        isFirst=true;
        RushList();
    }

    @Override
    protected BasePresenter initPresent() {
        return null;
    }

    public void RushList(){
        page=1;
        isRush=true;
        switch (flag){
            case Constans.MFTT:
                getBlog_message();
                break;
            case Constans.ZBMFTT:
                getUserBlog(Constans.ZBMFTT_ID);
                break;
        }
    }
    private void getMoreList(){
        page+=1;
        switch (flag){
            case Constans.MFTT:
                getBlog_message();
                break;
            case Constans.ZBMFTT:
                getUserBlog(Constans.ZBMFTT_ID);
                break;
        }
    }
    private void getUserBlog(String user_id){

        WebBase.getUserBlogs(user_id,page, new JSONHandler(isFirst,getActivity(),getString(R.string.loading)) {
            @Override
            public void onSuccess(JSONObject obj) {
                lv_tief.onRefreshComplete();
                JSONObject result=obj.optJSONObject("result");
                if(isRush){
                    isRush=false;
                    mNewsFeeds.clear();
                }
                NewsFeed blog = JSONParse.blog(result);
                if (blog != null) {
                    mNewsFeeds.addAll(blog.getList());
                }
                dynamicAdapter.notifyDataSetChanged();
                page=result.optInt("page");
                pages=result.optInt("pages");
            }
            @Override
            public void onFailure(String err_msg) {
                lv_tief.onRefreshComplete();
            }
        });
        if(isFirst){
            isFirst=false;
        }
    }
    private void getBlog_message(){
        WebBase.searchUserBlogs(page, new JSONHandler(isFirst,getActivity(),getString(R.string.loading)) {
            @Override
            public void onSuccess(JSONObject obj) {
                lv_tief.onRefreshComplete();
                JSONObject result=obj.optJSONObject("result");
                if(isRush){
                    mNewsFeeds.clear();
                }
                NewsFeed blog = JSONParse.blog(result);
                if (blog != null) {
                    mNewsFeeds.addAll(blog.getList());
                }
                dynamicAdapter.notifyDataSetChanged();
                page=result.optInt("page");
                pages=result.optInt("pages");
                if(page==pages&&!isFirst){
                    showToast("已加载全部数据");
                }
            }
            @Override
            public void onFailure(String err_msg) {
                showToast(err_msg);
                lv_tief.onRefreshComplete();
            }
        });
        if(isFirst){
            isFirst=false;
        }
    }
}
