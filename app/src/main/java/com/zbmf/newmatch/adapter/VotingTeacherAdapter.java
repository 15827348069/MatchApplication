package com.zbmf.newmatch.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.zbmf.newmatch.R;
import com.zbmf.newmatch.bean.Group;
import com.zbmf.newmatch.view.GlideOptionsManager;
import com.zbmf.worklibrary.util.DoubleFromat;

/**
 * Created by xuhao on 2017/11/6.
 */

public class VotingTeacherAdapter extends ListAdapter<Group> {

    public VotingTeacherAdapter(Activity context) {
        super(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder=null;
        if(convertView==null){
            convertView= LayoutInflater.from(mContext).inflate(R.layout.item_voting_teacher_layout,null);
            holder=new ViewHolder(convertView);
            convertView.setTag(holder);
        }else{
            holder= (ViewHolder) convertView.getTag();
        }
        final Group group= (Group) getItem(position);
        holder.tv_nickname.setText(group.getNick_name());
        holder.tv_votes.setText(DoubleFromat.getDouble(group.getVotes(),0));
        if(position==0){
            holder.imv_rank_img.setVisibility(View.VISIBLE);
            holder.imv_rank_img.setImageDrawable(mContext.getResources().getDrawable(R.drawable.icon_gold));
            holder.tv_rank.setVisibility(View.INVISIBLE);
        }else if(position==1){
            holder.imv_rank_img.setVisibility(View.VISIBLE);
            holder.imv_rank_img.setImageDrawable(mContext.getResources().getDrawable(R.drawable.icon_silver));
            holder.tv_rank.setVisibility(View.INVISIBLE);
        }else if(position==2){
            holder.imv_rank_img.setVisibility(View.VISIBLE);
            holder.imv_rank_img.setImageDrawable(mContext.getResources().getDrawable(R.drawable.icon_copper));
            holder.tv_rank.setVisibility(View.INVISIBLE);
        }else{
            holder.tv_rank.setVisibility(View.VISIBLE);
            holder.imv_rank_img.setVisibility(View.GONE);
            holder.tv_rank.setText((position+1)+"");
        }
        Glide.with(mContext).load(group.getAvatar()).apply(GlideOptionsManager.getInstance()
                .getRequestOptionsMatch()).into(holder.iv_rcv);
        holder.tv_Group.setOnClickListener(v -> {
            if(onGroup!=null){
                onGroup.onGroup(group);
            }
        });
        return convertView;
    }
    private class ViewHolder{
        private TextView tv_rank,tv_nickname,tv_votes,tv_Group;
        private ImageView imv_rank_img,iv_rcv;
        public ViewHolder(View view){
            tv_rank= (TextView) view.findViewById(R.id.tv_rank);
            tv_nickname= (TextView) view.findViewById(R.id.tv_nickname);
            tv_votes= (TextView) view.findViewById(R.id.tv_votes);
            imv_rank_img= (ImageView) view.findViewById(R.id.imv_rank_img);
            iv_rcv= (ImageView) view.findViewById(R.id.iv_rcv);
            tv_Group= (TextView) view.findViewById(R.id.tv_to_group);
        }
    }
    private OnGroup onGroup;

    public void setOnGroup(OnGroup onGroup) {
        this.onGroup = onGroup;
    }

    public interface OnGroup{
        void onGroup(Group group);
    }
}

