package com.zbmf.newmatch.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.zbmf.newmatch.R;
import com.zbmf.newmatch.bean.TagBean;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import java.util.List;


public class SearchParentTagAdapter extends RecyclerView.Adapter<SearchParentTagAdapter.ViewHolder> {

    private List<TagBean> tagList;

    public interface OnItemClickLitener
    {
        void onParenItemClick(TagBean.ChildrenTag childrenTag, int position);
        void onDelete(TagBean tagBean);
    }

    private OnItemClickLitener mOnItemClickLitener;

    public void setOnItemClickLitener(OnItemClickLitener mOnItemClickLitener)
    {
        this.mOnItemClickLitener = mOnItemClickLitener;
    }

    public SearchParentTagAdapter(Context context, List<TagBean> tagList) {
        this.tagList = tagList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_tag, parent, false);
        ViewHolder vh = new ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final TagBean tagBean=tagList.get(position);
        holder.mTextView.setText(tagBean.getTag_name());
        holder.itemView.setTag(tagBean);
        if(position==0&&tagBean.getTag_name().endsWith("搜索记录")){
            holder.imbSearchDelete.setVisibility(View.VISIBLE);
            holder.imbSearchDelete.setOnClickListener(v -> {
                if(mOnItemClickLitener!=null){
                    mOnItemClickLitener.onDelete(tagBean);
                }
            });
        }else{
            holder.imbSearchDelete.setVisibility(View.GONE);
        }
        if(mOnItemClickLitener!=null){
            holder.children_tag.setOnTagClickListener((view, position1, parent) -> {
                mOnItemClickLitener.onParenItemClick(tagBean.getData().get(position1), position1);
                return true;
            });
        }
        holder.children_tag.setAdapter( new TagAdapter(tagBean.getData()) {
            @Override
            public View getView(FlowLayout parent, int position, Object o) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_tag_layout,
                        holder.children_tag, false);
                TextView textView= (TextView) view.findViewById(R.id.tag_tv);
                TagBean.ChildrenTag childrenTag= (TagBean.ChildrenTag) o;
                textView.setText(childrenTag.getName());
                return view;
            }
        });
    }

    @Override
    public int getItemCount() {
        return tagList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView mTextView;
        TagFlowLayout children_tag;
        ImageButton imbSearchDelete;
        public ViewHolder(View view) {
            super(view);
            mTextView = (TextView) view.findViewById(R.id.tag_title);
            children_tag= (TagFlowLayout) view.findViewById(R.id.item_tag_rv);
            imbSearchDelete= (ImageButton) view.findViewById(R.id.imb_search_delete);
        }
    }
}
