package com.zbmf.newmatch.fragment.group;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.zbmf.newmatch.R;
import com.zbmf.newmatch.activity.GroupDetailActivity;
import com.zbmf.newmatch.adapter.HistoryDateAdapter;
import com.zbmf.newmatch.bean.Group;
import com.zbmf.newmatch.common.Constans;
import com.zbmf.newmatch.fragment.BaseFragment;
import com.zbmf.newmatch.util.DateUtil;
import com.zbmf.newmatch.util.ShowActivity;
import com.zbmf.newmatch.view.ListViewForScrollView;
import com.zbmf.worklibrary.presenter.BasePresenter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by xuhao on 2017/2/25.
 */

public class GroupDetailHistoryFragment extends BaseFragment {
    private List<Date>infolist;
    private ListViewForScrollView group_detail_list;
    private HistoryDateAdapter adapter;
    private Group group_bean;
    public static GroupDetailHistoryFragment newInstance(Group group) {
        GroupDetailHistoryFragment fragment = new GroupDetailHistoryFragment();
        Bundle args = new Bundle();
        args.putSerializable("GROUP", group);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            group_bean = (Group) getArguments().getSerializable("GROUP");
        }
    }

    @Override
    protected int getLayout() {
        return R.layout.group_detail_fragment;
    }

    @Override
    protected void initView() {
        infolist=new ArrayList<>();
        adapter=new HistoryDateAdapter(getContext(),infolist,group_bean.getNick_name());
        group_detail_list=(ListViewForScrollView) getView(R.id.group_detail_list);
        group_detail_list.setAdapter(adapter);
        group_detail_list.setOnItemClickListener((adapterView, view, i, l) -> {
            group_bean.setLive_history_date(infolist.get(i));
            ShowActivity.showLiveHistoryActivity(getActivity(),group_bean);
        });

    }

    @Override
    protected void initData() {
        infolist.clear();
        infolist.addAll(getDate());
        adapter.notifyDataSetChanged();
        GroupDetailActivity activity= (GroupDetailActivity) getActivity();
        activity.viewPager.setObjectForPosition(getFragmentView(),3);
    }

    @Override
    protected BasePresenter initPresent() {
        return null;
    }

    private List<Date> getDate(){
        List<Date>info=new ArrayList<>();
        for(int i = -1; i>= Constans.HISTORY_DAYS; i--){
            info.add(DateUtil.afterNDay(i));
        }
        return  info;
    }
    public void setGroup(Group group){
        this.group_bean=group;
        if(adapter!=null&&group!=null&&group.getNick_name()!=null){
            adapter.setGroup_nick_name(group_bean.getNick_name());
        }
    }
}
