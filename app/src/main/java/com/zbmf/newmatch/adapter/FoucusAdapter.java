package com.zbmf.newmatch.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.zbmf.newmatch.MainActivity;
import com.zbmf.newmatch.R;
import com.zbmf.newmatch.activity.CareTeacherActivity;
import com.zbmf.newmatch.bean.Group;
import com.zbmf.newmatch.view.GlideOptionsManager;
import com.zbmf.newmatch.view.RoundedCornerImageView;

import java.util.List;


/**
 * Created by iMac on 2017/2/21.
 */

public class FoucusAdapter extends BaseAdapter {

    private List<Group> mGroups;
    private Activity mContext;
    private int type;
    public static final int FOCUS_LIST = 1;//默认为通过关注进入直播
    private OnCareClink onCareClink;
    private int red,gray;
    public void setOnCareClink(OnCareClink onCareClink) {
        this.onCareClink = onCareClink;
    }

    public FoucusAdapter(Context cxt, List<Group> groups, int type) {
        mGroups = groups;
        if (cxt instanceof MainActivity)
            mContext = (MainActivity) cxt;
        else if (cxt instanceof CareTeacherActivity)
            mContext = (CareTeacherActivity) cxt;
        this.type = type;
        red=mContext.getResources().getColor(R.color.colorAccent);
        gray=mContext.getResources().getColor(R.color.black_99);
    }

    @Override
    public int getCount() {
        return mGroups.size();
    }

    @Override
    public Object getItem(int position) {
        return mGroups.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        UnFocusHolder holder1 = null;
        final Group group = mGroups.get(position);
        if (type == FOCUS_LIST) {
            if (convertView != null) {
                holder = (ViewHolder) convertView.getTag();
            } else {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.item_focus, null);
                holder = new ViewHolder();
                holder.tv_name = convertView.findViewById(R.id.tv_name);
                holder.tv_live_text = convertView.findViewById(R.id.tv_live_text);
                holder.tv_chat_text = convertView.findViewById(R.id.tv_chat_text);
                holder.riv = convertView.findViewById(R.id.iv);
                holder.ll_root = convertView.findViewById(R.id.ll_root);
                convertView.setTag(holder);
            }
            Glide.with(mContext).load(group.getAvatar()).apply(GlideOptionsManager.getInstance()
                    .getRequestOptionsMatch()).into(holder.riv);
            holder.tv_name.setText(group.getNick_name());
            holder.tv_live_text.setTextColor(group.getUnredcount()!=0?red:gray);
            holder.tv_chat_text.setTextColor(group.getChat()!=0?red:gray);
            holder.tv_live_text.setSelected(group.getUnredcount()!=0);
            holder.tv_chat_text.setSelected(group.getChat()!=0);
            holder.tv_live_text.setText(group.getUnredcount() + "");
            holder.tv_chat_text.setText(group.getChat()+"");
        } else {
            if (convertView != null) {
                holder1 = (UnFocusHolder) convertView.getTag();
            } else {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.item_fo_item, null);
                holder1 = new UnFocusHolder();
                holder1.recommend_item_button = convertView.findViewById(R.id.recommend_item_button);
                holder1.tv_name = convertView.findViewById(R.id.recommend_item_name);
                holder1.recommend_item_desc = convertView.findViewById(R.id.recommend_item_desc);
                holder1.riv = convertView.findViewById(R.id.recommend_item_avatar);
                holder1.ll_root = convertView.findViewById(R.id.rl_root);
                convertView.setTag(holder1);
            }
            Glide.with(mContext).load(group.getAvatar()).apply(GlideOptionsManager.getInstance()
                    .getRequestOptionsMatch()).into(holder.riv);
            holder1.tv_name.setText(group.getNick_name());

            if (group.is_recommend()) {
                holder1.recommend_item_button.setTag(position);
                holder1.recommend_item_button.setText("取消关注");
                holder1.recommend_item_button.setOnClickListener(v -> {
                    if(onCareClink!=null){
                        onCareClink.onCareClink(position);
                    }
                });
            } else {
                holder1.recommend_item_button.setText("----");
                holder1.recommend_item_button.setOnClickListener(null);
            }

            holder1.recommend_item_button.setVisibility(View.GONE);
            holder1.recommend_item_desc.setVisibility(View.GONE);
        }

        return convertView;
    }
    public interface OnCareClink{
        void onCareClink(int position);
    }

    static class ViewHolder {
        TextView tv_name, tv_live_text, tv_chat_text;
        RoundedCornerImageView riv;
        RelativeLayout ll_root;
    }

    static class UnFocusHolder {
        TextView tv_name, recommend_item_desc;
        RoundedCornerImageView riv;
        TextView recommend_item_button;
        RelativeLayout ll_root;
    }
}
