package com.zbmf.newmatch.adapter;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import com.zbmf.newmatch.R;
import com.zbmf.newmatch.bean.HomeMatchList;
import com.zbmf.worklibrary.adapter.ListAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by pq
 * on 2018/3/16.
 */

public class HomeHotMatchAdapter extends ListAdapter<HomeMatchList.Result.Hot> {
    public HomeHotMatchAdapter(Activity context) {
        super(context);
    }
    @Override
    protected int getLayout() {
        return R.layout.item_super_match_layout;
    }

    @Override
    public View getHolderView(int position, View convertView, HomeMatchList.Result.Hot matchBean) {
        ViewHolder holder= (ViewHolder) convertView.getTag();
        if (holder == null) {
            holder=new ViewHolder(convertView);
            convertView.setTag(holder);
        }
        holder.tvMatchName.setText(matchBean.getMatch_name());
        holder.tvMatchPlayer.setText(String.format(mContext.getResources().getString(R.string.match_players),matchBean.getPlayers()));
        if (matchBean.getMatch_type()==0){
            holder.tvMatchType.setText(mContext.getString(R.string.gk));
            holder.tvMatchType.setBackgroundResource(R.drawable.tag_yellow);
            holder.tvMatchType.setTextColor(mContext.getResources().getColor(R.color.gk));
        }else if (matchBean.getMatch_type()==1){
            holder.tvMatchType.setText(mContext.getString(R.string.invite));
            holder.tvMatchType.setBackgroundResource(R.drawable.inivate_bg);
            holder.tvMatchType.setTextColor(mContext.getResources().getColor(R.color.err_red));
        }else if (matchBean.getMatch_type()==2){
            holder.tvMatchType.setText(mContext.getString(R.string.private_match));
        }
        if (matchBean.getIs_end()){
            holder.tvMatchAction.setBackgroundResource(R.drawable.end_bg);
            holder.tvMatchAction.setTextColor(mContext.getResources().getColor(R.color.black_grey));
        }else {
            holder.tvMatchAction.setBackgroundResource(R.drawable.tag_blue);
            holder.tvMatchAction.setTextColor(mContext.getResources().getColor(R.color.blue1));
        }
        holder.tvMatchAction.setText(matchBean.getIs_end()?"已结束":"进行中");
        return convertView;
    }

    public class ViewHolder{
        @BindView(R.id.tv_match_name)
        TextView tvMatchName;
        @BindView(R.id.tv_match_type)
        TextView tvMatchType;
        @BindView(R.id.tv_match_action)
        TextView tvMatchAction;
        @BindView(R.id.tv_match_player)
        TextView tvMatchPlayer;
        public ViewHolder(View view){
            ButterKnife.bind(this,view);
        }
    }

}
