package com.zbmf.newmatch.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.zbmf.newmatch.R;
import com.zbmf.newmatch.activity.Chat1Activity;
import com.zbmf.newmatch.bean.GiftBean;
import com.zbmf.newmatch.bean.Group;
import com.zbmf.newmatch.bean.LiveMessage;
import com.zbmf.newmatch.util.DateUtil;
import com.zbmf.newmatch.util.DisplayUtil;
import com.zbmf.newmatch.util.LogUtil;
import com.zbmf.newmatch.util.MatchSharedUtil;
import com.zbmf.newmatch.util.MessageType;
import com.zbmf.newmatch.util.SettingDefaultsManager;
import com.zbmf.newmatch.view.GlideOptionsManager;
import com.zbmf.newmatch.view.GroupTextView;

import java.util.List;

/**
 * Created by xuhao
 * on 2016/12/16.
 * 直播室item的Adapter
 */

public class LiveAdapter extends BaseAdapter implements View.OnClickListener, View.OnLongClickListener {
    public LayoutInflater inflater;
    private List<LiveMessage> infolist;
    private boolean is_tf;
    private Context context;
    private GroupTextView.OnTextClickListener listener;
    private View.OnClickListener onClickListener;

    private int pos;
    private PopupWindow mPopupWindow;
    private OnMenuClickListener mListener;
    private TextView tv_item1, tv_item2;
    private View mView;
    private Group mGroupBean;

    @Override
    public void onClick(View v) {
        if (mListener != null) {
            mListener.onMenuClick(v, pos);
        }
        if (mPopupWindow != null) {
            mPopupWindow.dismiss();
        }
    }

    public void setmListener(OnMenuClickListener mListener) {
        this.mListener = mListener;
    }

    @Override
    public boolean onLongClick(View v) {
        pos = (int) v.getTag();
        int[] windowPos = getPopPos(v, mView);
        if (mPopupWindow != null) {
            mPopupWindow.showAtLocation(v, Gravity.TOP | Gravity.START, windowPos[0], windowPos[1]);
        }
        return false;
    }

    public interface OnMenuClickListener {
        void onMenuClick(View view, int pos);
    }

    public LiveAdapter(Activity context, List<LiveMessage> data, boolean is_tf_type,
                       GroupTextView.OnTextClickListener textlistener, View.OnClickListener onClickListener) {
        this.inflater = LayoutInflater.from(context);
        this.infolist = data;
        this.context = context;
        this.is_tf = is_tf_type;
        this.listener = textlistener;
        this.onClickListener = onClickListener;
        mView = View.inflate(context, R.layout.long_click_message_view, null);
        mPopupWindow = new PopupWindow(mView, ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, true);
        tv_item1 = mView.findViewById(R.id.tv_copy);
        tv_item2 = mView.findViewById(R.id.tv_share);
        tv_item1.setOnClickListener(this);
        tv_item2.setOnClickListener(this);
        mPopupWindow.setTouchable(true);
        mPopupWindow.setBackgroundDrawable(new ColorDrawable());
        mPopupWindow.update();
    }

    public void setGroup(Group groupbean) {
        mGroupBean = groupbean;
    }

    private int[] getPopPos(View anchorView, View popView) {
        int windowPos[] = new int[2];
        int viewLoc[] = new int[2];
        int anchorHeight = anchorView.getHeight();
        int anchorWidth = anchorView.getWidth();
        anchorView.getLocationOnScreen(viewLoc);
        int screenHeight = DisplayUtil.getScreenHeightPixels((Chat1Activity) context);
        popView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);

        int windowHeight = popView.getMeasuredHeight();
        int windowWidth = popView.getMeasuredWidth();

        boolean isNeedDown = (screenHeight - viewLoc[1] < windowHeight);
        if (isNeedDown) {
            windowPos[0] = viewLoc[0] + anchorWidth / 2 - windowWidth / 2;
            windowPos[1] = viewLoc[1] + anchorHeight;
        } else {
            windowPos[0] = viewLoc[0] + anchorWidth / 2 - windowWidth / 2;
            windowPos[1] = viewLoc[1] - windowHeight;
        }

        return windowPos;
    }

    @Override
    public int getCount() {
        return infolist.size();
    }

    @Override
    public Object getItem(int i) {
        return infolist.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LiveItem item = null;
        if (view == null) {
            view = inflater.inflate(R.layout.live_item_layout, viewGroup, false);
            item = new LiveItem(view);
            view.setTag(item);
        } else {
            item = (LiveItem) view.getTag();
        }
        final LiveMessage cm = infolist.get(i);
        item.message_countent.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getResources()
                .getDimension(MatchSharedUtil.getTextSize()));
        item.message_time.setText(DateUtil.getNewChatTime(cm.getMessage_time()));
        if (cm.getMessage_type().equals(MessageType.FANS) || cm.getMessage_type().equals(MessageType.CHAT)) {
            item.message_countent.setTag(i);
            item.message_countent.setOnLongClickListener(this);
        } else {
            item.message_countent.setOnLongClickListener(null);
        }

        item.ll_gift_layout.setVisibility(View.GONE);
        switch (cm.getMessage_type()) {
            case MessageType.BOX:
                red_bag_g(item);
                //宝盒消息 1
                String box_message = null;
                switch (cm.getAction()) {
                    case 1://发布
                        box_message = "圈主" + cm.getBox_user_name() + "发布了铁粉宝盒";
                        break;
                    case 2://更新
                        box_message = "圈主" + cm.getBox_user_name() + "更新了铁粉宝盒";
                        break;
                    case 3://订阅
                        box_message = cm.getBox_user_name() + "订阅了圈主的铁粉宝盒";
                        break;
                }
                item.image.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_live_box));
                item.message_name.setText("宝盒消息");
                item.message_countent.setOnClickListener(null);
                item.message_countent.setTextClickListener(listener);
                item.message_countent.getPaint().setFakeBoldText(false);
                item.message_countent.setTextColor(context.getResources().getColor(R.color.black));
                String box_name = "";
                if (mGroupBean != null) {
                    int fans_level = mGroupBean.getFans_level();
                    if (fans_level >= 5) {
                        //铁粉
                        box_name = cm.getBox_name();
                    } else {
                        boolean isShowFans = MatchSharedUtil.getIsShowFans();
                        if (isShowFans) {
                            item.message_countent.setClickable(false);
                        } else {
                            box_name = cm.getBox_name();
                        }
                    }
                }
                String box_id = cm.getBox_id();
                int box_leaver = cm.getBox_leaver();
                item.message_countent.setBoxMessage(box_message,box_name , box_id,box_leaver);
                item.live_img_id.setVisibility(View.GONE);
                break;
            case MessageType.BLOG:
                red_bag_g(item);
                item.image.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_live_notice));
                item.message_name.setText("系统消息");
                item.message_countent.setOnClickListener(null);
                item.message_countent.setTextClickListener(listener);
                item.message_countent.getPaint().setFakeBoldText(false);
                item.message_countent.setTextColor(context.getResources().getColor(R.color.black));
                String blog_message = cm.getMessage_countent();
                blog_message = blog_message.substring(0, cm.getMessage_countent().indexOf("["));
                item.message_countent.setBlogMessage(blog_message, cm.getBlog_name(), cm.getUser_id()
                        + "-" + cm.getBlog_id());
                item.live_img_id.setVisibility(View.GONE);
                break;
            case MessageType.MEMBER:
                red_bag_g(item);
                item.image.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_live_notice));
                item.message_name.setText("系统消息");
                item.message_countent.setOnClickListener(null);
                item.message_countent.setTextClickListener(listener);
                item.message_countent.getPaint().setFakeBoldText(false);
                item.message_countent.setTextColor(context.getResources().getColor(R.color.black));
                String moment_message = cm.getMessage_countent();

                if (mGroupBean != null) {
                    int fans_level = mGroupBean.getFans_level();
                    if (fans_level >= 5) {
                        //铁粉
                        moment_message = moment_message + context.getResources().getString(R.string.tf_commit);
                    } else {
                        boolean isShowFans =MatchSharedUtil.getIsShowFans();
                        if (isShowFans) {
                            item.message_countent.setClickable(false);
                        } else {
                            moment_message = moment_message + context.getResources().getString(R.string.tf_commit);
                        }
                    }
                }
                item.message_countent.setMessageText(moment_message);

                item.live_img_id.setVisibility(View.GONE);
                break;
            case MessageType.FANS:
                red_bag_g(item);
                item.image.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_live_tf));
                item.message_countent.setOnClickListener(null);
                item.message_countent.setTextClickListener(listener);
                item.message_name.setText(cm.getMessage_name());
                if (!is_tf) {
                    item.message_countent.getPaint().setFakeBoldText(false);
                    item.message_countent.setTextColor(context.getResources().getColor(R.color.black));
                    String a = context.getResources().getString(R.string.add_to_tf);
                    if (mGroupBean != null) {
                        int fans_level = mGroupBean.getFans_level();
                        if (fans_level >= 5) {
                            a = a + context.getResources().getString(R.string.add_tf_ck);
                        } else {
                            boolean isShowFans = MatchSharedUtil.getIsShowFans();
                            if (isShowFans) {
                                item.message_countent.setClickable(false);
                            } else {
                                a = a + context.getResources().getString(R.string.add_tf_ck);
                            }
                        }
                    }
                    item.message_countent.setMessageText(a);
                    item.live_img_id.setVisibility(View.GONE);
                } else {
                    if (cm.getImportent() == 0) {
                        item.message_countent.getPaint().setFakeBoldText(false);
                        item.message_countent.setTextColor(context.getResources().getColor(R.color.black));
                    } else {
                        item.message_countent.getPaint().setFakeBoldText(true);
                        item.message_countent.setTextColor(context.getResources().getColor(R.color.colorAccent));
                    }
                    item.message_countent.setMessageText(cm.getMessage_countent());
                    if (cm.getMessage_or_img() != null && cm.getMessage_or_img().equals(MessageType.IMG)) {
                        item.live_img_id.setVisibility(View.VISIBLE);
                        Glide.with(context).load(cm.getThumb()).apply(GlideOptionsManager.getInstance()
                                .getBannerOptions(0)).into(item.live_img_id);
                        item.live_img_id.setTag(cm.getImg_url());
                        item.live_img_id.setOnClickListener(onClickListener);
                    } else {
                        item.live_img_id.setVisibility(View.GONE);
                    }
                }
                break;
            case MessageType.SYSTEM:
                red_bag_g(item);
                item.message_name.setText("系统公告");
                item.message_countent.setOnClickListener(null);
                item.message_countent.setTextClickListener(listener);
                item.message_countent.getPaint().setFakeBoldText(false);
                item.message_countent.setTextColor(context.getResources().getColor(R.color.black));
                LogUtil.d("系统公告:   "+cm.getMessage_countent());
                item.message_countent.setMessageText(cm.getMessage_countent());
                item.image.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_live_notice));
                if (cm.getMessage_or_img() != null && cm.getMessage_or_img().equals(MessageType.IMG)) {
                    item.live_img_id.setVisibility(View.VISIBLE);
                    Glide.with(context).load(cm.getThumb()).apply(GlideOptionsManager.getInstance()
                            .getBannerOptions(0)).into(item.live_img_id);
                    item.live_img_id.setTag(cm.getImg_url());
                    item.live_img_id.setOnClickListener(onClickListener);
                } else {
                    item.live_img_id.setVisibility(View.GONE);
                }
                break;
            case MessageType.CHAT:
                red_bag_g(item);
                item.image.setImageDrawable(context.getResources().getDrawable(R.drawable.quan));
                item.message_name.setText(cm.getMessage_name());
                if (cm.getImportent() == 0) {
                    item.message_countent.getPaint().setFakeBoldText(false);
                    item.message_countent.setTextColor(context.getResources().getColor(R.color.black));
                } else {
                    item.message_countent.getPaint().setFakeBoldText(true);
                    item.message_countent.setTextColor(context.getResources().getColor(R.color.colorAccent));
                }
                item.message_countent.setOnClickListener(null);
                item.message_countent.setTextClickListener(listener);
                item.message_countent.setMessageText(cm.getMessage_countent());
                if (cm.getMessage_or_img() != null && cm.getMessage_or_img().equals(MessageType.IMG)) {
                    item.live_img_id.setVisibility(View.VISIBLE);
                    Glide.with(context).load(cm.getThumb()).apply(GlideOptionsManager.getInstance()
                            .getBannerOptions(0)).into(item.live_img_id);
                    item.live_img_id.setTag(cm.getImg_url());
                    item.live_img_id.setOnClickListener(onClickListener);
                } else {
                    item.live_img_id.setVisibility(View.GONE);
                }
                break;
            case MessageType.GIFT:
                red_bag_g(item);
                item.message_name.setText("系统公告");
                item.message_countent.getPaint().setFakeBoldText(false);
                item.message_countent.setTextColor(context.getResources().getColor(R.color.black));
                item.message_countent.setOnClickListener(null);
                item.message_countent.setTextClickListener(listener);
                item.image.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_live_gift));
                item.message_countent.setMessageText(cm.getMessage_countent() + context.getResources().getString(R.string.tf_gift));
                item.live_img_id.setVisibility(View.GONE);
                GiftBean giftBean = cm.getGiftbean();
                if (giftBean != null) {
                    item.ll_gift_layout.setVisibility(View.VISIBLE);
                    item.tv_gift_num.setText(String.format("x%s", String.valueOf(giftBean.getSend_gift_number())));
                    Glide.with(context).load(giftBean.getGift_icon()).apply(GlideOptionsManager.getInstance()
                            .getBannerOptions(0)).into(item.imv_gift);
                    item.ll_gift_layout.setOnClickListener(this);
                }
                break;
            case MessageType.RED_PACKET:
                red_bag_v(item);
                item.image.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_live_redbag));
                item.message_name.setText(cm.getMessage_name());
                item.red_bag_layout.setTag(cm.getRed_id());
                item.red_bag_layout.setOnClickListener(onClickListener);
                item.red_bag_message.setText(cm.getRed_message());
                break;
            case MessageType.ANSWER:
                asklayoutV(item);
                item.tv_question_name.setText(cm.getQuestion_name());
                item.tv_question_time.setText(cm.getQuestion_time());
                item.tv_question_content.setText(cm.getQuestion_content());

                item.tv_answer_name.setText(cm.getAnswer_name());
                item.tv_answer_time.setText(cm.getAnswer_time());
                item.tv_answer_content.setText(cm.getAnswer_content());
                item.image.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_live_ask));
                break;
        }
        return view;
    }

    private void asklayoutV(LiveItem item) {
        item.message_countent.setVisibility(View.GONE);
        item.live_img_id.setVisibility(View.GONE);
        item.red_bag_layout.setVisibility(View.GONE);
        item.message_name.setText("");
        item.ask_layout.setVisibility(View.VISIBLE);
    }

    public void red_bag_v(LiveItem item) {
        item.red_bag_layout.setVisibility(View.VISIBLE);
        item.message_countent.setVisibility(View.GONE);
        item.live_img_id.setVisibility(View.GONE);
        item.ask_layout.setVisibility(View.GONE);
    }

    public void red_bag_g(LiveItem item) {
        item.message_countent.setVisibility(View.VISIBLE);
        item.live_img_id.setVisibility(View.VISIBLE);
        item.ask_layout.setVisibility(View.GONE);
        item.red_bag_layout.setVisibility(View.GONE);
    }

    public void add_tf(boolean add_tf) {
        is_tf = add_tf;
        notifyDataSetChanged();
    }

    public class LiveItem {
        TextView message_name, message_time, red_bag_message, tv_question_name, tv_question_time, tv_question_content, tv_answer_name,
                tv_answer_time, tv_answer_content, tv_gift_num;
        private GroupTextView message_countent;
        ImageView live_img_id, image, imv_gift;
        private RelativeLayout red_bag_layout;
        private LinearLayout ask_layout;
        private LinearLayout ll_gift_layout;

        public LiveItem(View view) {
            this.message_countent = view.findViewById(R.id.message_countent);
            this.message_time = view.findViewById(R.id.message_time);
            this.message_name = view.findViewById(R.id.message_name);
            this.live_img_id = view.findViewById(R.id.live_img_id);
            this.red_bag_layout = view.findViewById(R.id.red_bag_layout);
            this.red_bag_message = view.findViewById(R.id.red_bag_message);
            this.image = view.findViewById(R.id.image);

            this.ask_layout = view.findViewById(R.id.ask_layout);

            this.tv_question_name = view.findViewById(R.id.tv_question_name);
            this.tv_question_time = view.findViewById(R.id.tv_question_time);
            this.tv_question_content = view.findViewById(R.id.tv_question_content);

            this.tv_answer_name = view.findViewById(R.id.tv_answer_name);
            this.tv_answer_time = view.findViewById(R.id.tv_answer_time);
            this.tv_answer_content = view.findViewById(R.id.tv_answer_content);

            this.ll_gift_layout = view.findViewById(R.id.ll_gift_layout);
            this.imv_gift = view.findViewById(R.id.imv_gift);
            this.tv_gift_num = view.findViewById(R.id.tv_gift_num);

        }
    }
}
