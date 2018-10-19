package com.zbmf.newmatch.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.zbmf.newmatch.R;
import com.zbmf.newmatch.bean.Group;
import com.zbmf.newmatch.view.GlideOptionsManager;
import com.zbmf.newmatch.view.RoundedCornerImageView;

import java.util.List;

/**
 * Created by xuhao on 2017/2/24.
 */

public class RecommendAdapter extends BaseAdapter {
    private Context mContext;
    private List<Group>infolist;
    private LayoutInflater inflater;
    private OnCareClink onCareClink;
    private Drawable drawable;
    private int white,gray;
    public RecommendAdapter(Context context, List<Group>info){
        mContext=context;
        this.infolist=info;
        this.inflater=LayoutInflater.from(context);
        drawable= context.getResources().getDrawable(R.drawable.icon_left_care);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        white=context.getResources().getColor(R.color.white);
        gray=context.getResources().getColor(R.color.black_66);
    }

    public void setOnCareClink(OnCareClink onCareClink) {
        this.onCareClink = onCareClink;
    }

    @Override
    public int getCount() {
        return infolist.size();
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        RecommendItem item=null;
        if(view==null){
            view=inflater.inflate(R.layout.recommend_item,null);
            item=new RecommendItem(view);
            view.setTag(item);
        }else{
            item= (RecommendItem) view.getTag();
        }
        final Group group=infolist.get(i);
        item.name.setText(group.getNick_name());
        item.desc.setText(group.getFollow_num()+"人关注");
        item.recommend_style.setText(group.getStyle());
        item.recommend_tag.setText(group.getTags());
        item.care_button.setSelected(!group.is_recommend());
        Glide.with(mContext).load(group.getAvatar()).apply(GlideOptionsManager.getInstance()
                .getRequestOptionsMatch()).into(item.avatar);
        if(!group.is_recommend()){
            item.care_button.setText("+关注");
            item.care_button.setTextColor(white);
            item.care_button.setCompoundDrawables(null,null,null,null);
            item.care_button.setOnClickListener(v -> {
                if(onCareClink!=null){
                    onCareClink.onCareClink(i);
                }
            });
        }else{
            item.care_button.setTextColor(gray);
            item.care_button.setCompoundDrawables(drawable,null,null,null);
            item.care_button.setText("已关注");
            item.care_button.setOnClickListener(null);
        }
        return view;
    }
    private class RecommendItem{
        TextView name,desc;
        TextView care_button,recommend_tag,recommend_style;
        RoundedCornerImageView avatar;
        RecommendItem(View view){
            name= view.findViewById(R.id.recommend_item_name);
            desc= view.findViewById(R.id.recommend_follownum);
            recommend_style= view.findViewById(R.id.recommend_style);
            recommend_tag= view.findViewById(R.id.recommend_tag);
            care_button= view.findViewById(R.id.recommend_item_button);
            avatar= view.findViewById(R.id.recommend_item_avatar);
        }
    }
    public interface OnCareClink{
        void onCareClink(int position);
    }
}
