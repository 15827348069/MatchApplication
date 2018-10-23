package com.zbmf.newmatch.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.zbmf.newmatch.R;
import com.zbmf.newmatch.bean.MFBean;
import com.zbmf.newmatch.common.Constans;

import java.util.List;

/**
 * Created by xuhao on 2017/2/20.
 */

public class MfbAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private List<MFBean>infolist;
    private Resources resources;
    public MfbAdapter(Context context, List<MFBean>info){
        this.inflater=LayoutInflater.from(context);
        this.infolist=info;
        this.resources=context.getResources();
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

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        mfbItem item=null;
        if(view==null){
            view=inflater.inflate(R.layout.mfb_detail_item,null);
            item=new mfbItem(view);
            view.setTag(item);
        }else{
            item= (mfbItem) view.getTag();
        }
        MFBean pb=infolist.get(i);
        item.month.setText(pb.getMonth());
        item.day.setText(pb.getDay());
        item.count.setText(pb.getCount()+" 魔方宝");
        item.type.setText(pb.getNotes());
        item.desc.setText(pb.getDesc());
        switch (pb.getAction()){
            case Constans.CHARGE:
                item.type.setBackground(resources.getDrawable(R.drawable.shape_mfb_type_cc6c6c));
                break;
            case Constans.REWARD:
                item.type.setBackground(resources.getDrawable(R.drawable.shape_mfb_type_ff8989));
                break;
            case Constans.USE:
                item.type.setBackground(resources.getDrawable(R.drawable.shape_mfb_type_f3ad6f));
                break;
            case Constans.SYSTEM:
                item.type.setBackground(resources.getDrawable(R.drawable.shape_mfb_type_55587c));
                break;
            case Constans.USER_PAY:
                item.type.setBackground(resources.getDrawable(R.drawable.shape_mfb_type_9b8989));
                break;
            case Constans.PAY:
                item.type.setBackground(resources.getDrawable(R.drawable.shape_mfb_type_7db6f1));
                break;
            case Constans.GAIN:
                item.type.setBackground(resources.getDrawable(R.drawable.shape_mfb_type_c28dce));
                break;
            case Constans.REFUND:
                item.type.setBackground(resources.getDrawable(R.drawable.shape_mfb_type_e86477));
                break;
            default:
                item.type.setBackground(resources.getDrawable(R.drawable.shape_mfb_type_default));
                break;
        }

        return view;
    }
    private class mfbItem{
        private TextView desc,month,day,count,type;
        mfbItem(View view){
            this.count= (TextView) view.findViewById(R.id.item_mfb_count);
            this.month= (TextView) view.findViewById(R.id.item_mfb_month);
            this.day= (TextView) view.findViewById(R.id.item_mfb_day);
            this.type= (TextView) view.findViewById(R.id.item_mfb_type);
            this.desc= (TextView) view.findViewById(R.id.item_mfb_desc);
        }
    }
}
