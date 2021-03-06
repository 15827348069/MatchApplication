package com.zbmf.newmatch.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.zbmf.newmatch.R;
import com.zbmf.newmatch.bean.BlogBean;
import com.zbmf.newmatch.view.GlideOptionsManager;
import com.zbmf.newmatch.view.RoundedCornerImageView;

import java.util.List;

/**
 * Created by xuhao on 2017/2/20.
 */

public class BlogMessageAdapter extends BaseAdapter {
    private Context mContext;
    private LayoutInflater inflaterl;
    private List<BlogBean>info;
    public BlogMessageAdapter(Context context, List<BlogBean>infolist){
        mContext=context;
        this.inflaterl=LayoutInflater.from(context);
        this.info=infolist;
    }
    @Override
    public int getCount() {
        return info.size();
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
    public View getView(int i, View view, ViewGroup viewGroup) {
        HeadMessageItem item=null;
        if(view==null){
            view=inflaterl.inflate(R.layout.blog_message_item,null);
            item=new HeadMessageItem(view);
            view.setTag(item);
        }else{
            item= (HeadMessageItem) view.getTag();
        }
        BlogBean bb=info.get(i);
        Glide.with(mContext).load(bb.getImg()).apply(GlideOptionsManager.getInstance()
                .getBannerOptions(0)).into(item.img);
        item.title.setText(bb.getTitle());
        item.date.setText(bb.getDate());
        if(bb.getLook_number()!=null){
            item.look.setVisibility(View.VISIBLE);
            item.look.setText(bb.getLook_number());
        }else{
            item.look.setVisibility(View.GONE);
        }
        if(bb.getPinglun()!=null){
            item.pinglun.setVisibility(View.VISIBLE);
            item.pinglun.setText(bb.getPinglun());
        }else{
            item.pinglun.setVisibility(View.GONE);
        }

        return view;
    }
    private class HeadMessageItem{
        ImageView img;
//        RoundedCornerImageView avatar;
        TextView title,date,look,pinglun;
        public HeadMessageItem(View view){
            this.img= (ImageView) view.findViewById(R.id.head_message_img);
            this.title= (TextView) view.findViewById(R.id.head_message_title);
            this.date= (TextView) view.findViewById(R.id.head_message_date);
            this.look= (TextView) view.findViewById(R.id.head_message_look);
            this.pinglun= (TextView) view.findViewById(R.id.head_message_pinglun);
        }
    }
}
