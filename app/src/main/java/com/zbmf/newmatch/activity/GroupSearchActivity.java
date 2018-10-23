package com.zbmf.newmatch.activity;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.zbmf.newmatch.R;
import com.zbmf.newmatch.adapter.SearchGroupadapter;
import com.zbmf.newmatch.adapter.SearchParentTagAdapter;
import com.zbmf.newmatch.api.JSONHandler;
import com.zbmf.newmatch.api.WebBase;
import com.zbmf.newmatch.bean.Group;
import com.zbmf.newmatch.bean.TagBean;
import com.zbmf.newmatch.callback.ResultCallback;
import com.zbmf.newmatch.util.JSONParse;
import com.zbmf.newmatch.util.MatchSharedUtil;
import com.zbmf.newmatch.util.MyActivityManager;
import com.zbmf.newmatch.util.ShowActivity;
import com.zbmf.newmatch.util.ToastUtils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class GroupSearchActivity extends AppCompatActivity implements View.OnClickListener{

    List<Group> searchlistData;
    private ListView listView;
    SearchGroupadapter adapter;
    private EditText search_edittext;
    private int page, pages;
    private List<TagBean> tagBeanList ;
    private SearchParentTagAdapter tagAdapter;
    private RecyclerView tagRecyclerView;
    private boolean history,hotsearch,actionsearch;
    private LinearLayout noMessage;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_search);
//添加管理activity
        MyActivityManager.getMyActivityManager().pushAct(this);

        listView = (ListView) findViewById(R.id.listView1);
        search_edittext = (EditText) findViewById(R.id.search_edittext);
        tagRecyclerView = findViewById(R.id.search_rv);
        noMessage=findViewById(R.id.line_no_search);
        findViewById(R.id.imb_search_clear).setOnClickListener(this);

        GroupaddListener();
        GroupinitData();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void GroupinitData() {
        tagBeanList=new ArrayList<>();
        searchlistData = new ArrayList<>();
        adapter = new SearchGroupadapter(this, searchlistData);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener((parent, view, position, id) -> {
            Group group=searchlistData.get(position);
           MatchSharedUtil.instance().setSearchHistory(new TagBean.ChildrenTag(group.getNick_name(),group.getId(),0));
            //跳转直播室
            ShowActivity.showChatActivity(GroupSearchActivity.this,group);
//           ShowActivity.showGroupDetailActivity(GroupSearchActivity.this,group );
        });

        LinearLayoutManager layoutManage = new LinearLayoutManager(getBaseContext());
        layoutManage.setOrientation(LinearLayoutManager.VERTICAL);
        tagRecyclerView.setLayoutManager(layoutManage);
        tagAdapter = new SearchParentTagAdapter(GroupSearchActivity.this, tagBeanList);
        tagAdapter.setOnItemClickLitener(new SearchParentTagAdapter.OnItemClickLitener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onParenItemClick(TagBean.ChildrenTag childrenTag, int position) {
                //跳转直播室
                ShowActivity.showChatActivity(GroupSearchActivity.this,childrenTag.getId());
            }

            @Override
            public void onDelete(TagBean tagBean) {
                MatchSharedUtil.clearSearchHistory();
                tagBeanList.remove(tagBean);
                tagAdapter.notifyDataSetChanged();
            }
        });
        tagRecyclerView.setAdapter(tagAdapter);
        getSearchHistory();
        getHotSerch();
    }

    public void GroupaddListener() {
        search_edittext.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH || (event != null &&
                    event.getKeyCode() == KeyEvent.KEYCODE_SEARCH)) {
                if(TextUtils.isEmpty(search_edittext.getText())){
                    ToastUtils.rectangleSingleToast("请输入搜索条件");
                }else{
                    actionsearch=true;
                    search_group(search_edittext.getText().toString());
                }
                return true;
            } else {
                return false;
            }
        });
        search_edittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(actionsearch){
                    actionsearch=false;
                }
                if (s.length() > 0) {
                    searchlistData.clear();
                    search_group(s.toString());
                } else {
                    getSearchHistory();
                    searchlistData.clear();
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        getSearchHistory();
    }

    private void getSearchHistory() {
        if(tagRecyclerView.getVisibility()==View.GONE){
            tagRecyclerView.setVisibility(View.VISIBLE);
        }
        if(listView.getVisibility()==View.VISIBLE){
            listView.setVisibility(View.GONE);
        }
        if(noMessage.getVisibility()==View.VISIBLE){
            noMessage.setVisibility(View.GONE);
        }
        if(tagBeanList==null){
            tagBeanList=new ArrayList<>();
        }else{
            if(tagBeanList.size()>0&&tagBeanList.get(0).getTag_name().equals("搜索记录")){
                tagBeanList.remove(0);
            }
        }
        MatchSharedUtil.instance().getSearchHistory(new ResultCallback() {
            @Override
            public void onSuccess(Object str) {
                TagBean tagBean = new TagBean();
                tagBean.setTag_name("搜索记录");
                tagBean.setData( (List<TagBean.ChildrenTag>) str);
                tagBeanList.add(0,tagBean);
                if(!history){
                    history=true;
                }
                RushSearchList();
            }

            @Override
            public void onError(String message) {
                if(!history){
                    history=true;
                }
                RushSearchList();
            }
        });
    }
    private void getHotSerch(){
        WebBase.recommendList(6,new JSONHandler() {
            @Override
            public void onSuccess(JSONObject obj) {
                JSONObject result=obj.optJSONObject("result");
                if(!result.isNull("groups")){
                    TagBean tagBean = new TagBean();
                    tagBean.setTag_name("热门搜索");
                    List<TagBean.ChildrenTag> childrenTags = JSONParse.getGroupTags(result.optJSONArray("groups"));
                    tagBean.setData(childrenTags);
                    tagBeanList.add(tagBean);
                }
                if(!hotsearch){
                    hotsearch=true;
                }
                RushSearchList();
            }

            @Override
            public void onFailure(String err_msg) {
                if(!hotsearch){
                    hotsearch=true;
                }
                RushSearchList();
            }
        });
    }
    private void RushSearchList(){
        if(history&&hotsearch){
            tagAdapter.notifyDataSetChanged();
        }
    }
    /**
     * 搜索圈子
     *
     * @param key
     */
    public void search_group(final String key) {
        WebBase.searchGroup(key, page, new JSONHandler() {
            @Override
            public void onSuccess(JSONObject obj) {
                JSONObject result = obj.optJSONObject("result");
                page = result.optInt("page");
                pages = result.optInt("pages");
                if (!result.isNull("groups")) {
                    searchlistData.clear();
                    List<Group> groups = JSONParse.getGroupList(result.optJSONArray("groups"));
                    if (groups!=null&&groups.size()>0){
//                        int is_private = groups.get(0).getIs_private();
//                        if (is_private==0){//表示公开
                        searchlistData.addAll(groups);
//                        }
                    }
                    if (searchlistData.size() > 0) {
                        tagRecyclerView.setVisibility(View.GONE);
                        listView.setVisibility(View.VISIBLE);
                    } else {
                        if(actionsearch){
                            if(noMessage.getVisibility()==View.GONE){
                                noMessage.setVisibility(View.VISIBLE);
                                tagRecyclerView.setVisibility(View.GONE);
                                listView.setVisibility(View.GONE);
                            }
                        }else{
                            getSearchHistory();
                        }
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(String err_msg) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        search_edittext.setText("");
    }
}
