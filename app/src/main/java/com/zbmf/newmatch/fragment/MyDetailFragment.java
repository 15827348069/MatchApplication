package com.zbmf.newmatch.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.zbmf.newmatch.R;
import com.zbmf.newmatch.activity.CareTeacherActivity;
import com.zbmf.newmatch.activity.InviteActivity;
import com.zbmf.newmatch.activity.MatchListActivity;
import com.zbmf.newmatch.activity.MfbActivity;
import com.zbmf.newmatch.activity.MyQuestionActivity;
import com.zbmf.newmatch.activity.MySubscripActivity;
import com.zbmf.newmatch.activity.PayDetailActivity;
import com.zbmf.newmatch.activity.PointActivity;
import com.zbmf.newmatch.activity.SelectStockActivity;
import com.zbmf.newmatch.activity.UserCollectsActivity;
import com.zbmf.newmatch.api.JSONHandler;
import com.zbmf.newmatch.api.WebBase;
import com.zbmf.newmatch.bean.CouponsBean;
import com.zbmf.newmatch.common.Constans;
import com.zbmf.newmatch.common.HtmlUrl;
import com.zbmf.newmatch.common.RequestCode;
import com.zbmf.newmatch.dialog.SignInDialog;
import com.zbmf.newmatch.listener.UpUserMessage;
import com.zbmf.newmatch.util.MatchSharedUtil;
import com.zbmf.newmatch.util.MessageType;
import com.zbmf.newmatch.util.ShowActivity;
import com.zbmf.newmatch.view.GlideOptionsManager;
import com.zbmf.newmatch.view.RoundedCornerImageView;
import com.zbmf.worklibrary.pulltorefresh.PullToRefreshBase;
import com.zbmf.worklibrary.pulltorefresh.PullToRefreshScrollView;

import org.json.JSONObject;

/**
 * Created by xuhao
 * on 2017/2/13.
 */

public class MyDetailFragment extends GroupBaseFragment implements View.OnClickListener, UpUserMessage {
    private TextView my_detail_mfb, my_detail_yhq, my_detail_jf, my_detail_name, my_detail_id;
    private Dialog kf_dialog;
    private RoundedCornerImageView my_detail_avatar;
    private CouponsBean couponsBean;
    private CheckBox qiandao;
    private ImageView my_dingyue_point, my_blog_point, my_care_point, my_ask_point;
    private PullToRefreshScrollView home_scrollview;
    private SignInDialog dialog;

    public static MyDetailFragment newInstance() {
        MyDetailFragment fragment = new MyDetailFragment();
        return fragment;
    }

    @Override
    protected View setContentView(LayoutInflater inflater) {
        return inflater.inflate(R.layout.my_detail_fragment, null);
    }

    public void setData(boolean updata) {
        setinitData(updata);
    }

    @Override
    protected void initView() {
        my_detail_name = getView(R.id.my_detail_names);
        my_detail_id = getView(R.id.my_detail_id);
        my_detail_mfb = getView(R.id.my_detail_mfb);
        my_detail_yhq = getView(R.id.my_detail_yhq);
        my_detail_jf = getView(R.id.my_detail_jf);
        my_detail_avatar = getView(R.id.my_detail_avatar_id);

        my_dingyue_point = getView(R.id.my_dingyue_point);
        my_blog_point = getView(R.id.my_blog_point);
        my_care_point = getView(R.id.my_care_point);
        my_ask_point = getView(R.id.my_ask_point);

//        message_count_text=getView(R.id.message_count_text);

        home_scrollview = getView(R.id.home_scrollview);
        home_scrollview.setMode(PullToRefreshBase.Mode.PULL_FROM_START);

        home_scrollview.setOnRefreshListener(refreshView -> initData());
        getView(R.id.my_message_layout).setOnClickListener(this);
        getView(R.id.my_detail_dingyue_button).setOnClickListener(this);
        getView(R.id.my_detail_blog_button).setOnClickListener(this);
        getView(R.id.my_detail_shouc_button).setOnClickListener(this);
        getView(R.id.my_detail_care_button).setOnClickListener(this);
        getView(R.id.my_detail_question_button).setOnClickListener(this);
        getView(R.id.my_detail_setting_button).setOnClickListener(this);
        getView(R.id.my_detail_kf_button).setOnClickListener(this);
        getView(R.id.my_detail_mfb_button).setOnClickListener(this);
        getView(R.id.my_detail_yhq_button).setOnClickListener(this);
        getView(R.id.my_detail_jf_button).setOnClickListener(this);
        getView(R.id.my_detail_open_button).setOnClickListener(this);
        getView(R.id.my_detail_select_stock).setOnClickListener(this);
        getView(R.id.tv_to_pay).setOnClickListener(this);
        getView(R.id.my_detail_invite_button).setOnClickListener(this);
        getView(R.id.my_detail_chat_stock_button).setOnClickListener(this);
        getView(R.id.my_detail_vip).setOnClickListener(this);
        qiandao = getView(R.id.qiandao_button);
//        ((MainActivity)getActivity()).setUpUserMessage(this);
    }

    public void setPointVisi(String type) {
        switch (type) {
            case Constans.PUSH_BOX_TYPE:
                //宝盒
                if (my_dingyue_point != null && my_dingyue_point.getVisibility() == View.GONE) {
                    my_dingyue_point.setVisibility(View.VISIBLE);
                }
                break;
            case MessageType.ANSWER:
                //问答
                if (my_ask_point != null && my_ask_point.getVisibility() == View.GONE) {
                    my_ask_point.setVisibility(View.VISIBLE);
                }
                break;
        }
    }

    public void setPointGone(String type) {
        switch (type) {
            case Constans.PUSH_BOX_TYPE:
                //宝盒
                if (my_dingyue_point != null && my_dingyue_point.getVisibility() == View.VISIBLE) {
                    my_dingyue_point.setVisibility(View.GONE);
                }
                break;
            case MessageType.ANSWER:
                //问答
                if (my_ask_point != null && my_ask_point.getVisibility() == View.VISIBLE) {
                    my_ask_point.setVisibility(View.GONE);
                }
                break;
        }
    }

    public void runshData() {
        if (home_scrollview != null && !home_scrollview.isRefreshing()) {
            home_scrollview.getRefreshableView().fullScroll(ScrollView.FOCUS_UP);
            home_scrollview.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
            home_scrollview.setRefreshing();
            initData();
        }
    }

    @Override
    protected void initData() {
        getWolle();
        userSigns();
    }

    public boolean getPoint() {
        if (my_ask_point != null && my_ask_point.getVisibility() == View.VISIBLE) {
            return true;
        }
        if (my_dingyue_point != null && my_dingyue_point.getVisibility() == View.VISIBLE) {
            return true;
        }
        return false;
    }

    public void updataUserMessage() {
        if (my_detail_name != null) {
            my_detail_name.setText(MatchSharedUtil.NickName());
        }
        if (my_detail_mfb != null) {
            my_detail_mfb.setText(MatchSharedUtil.getPays());
        }
        if (my_detail_id != null) {
            my_detail_id.setText("ID  " + MatchSharedUtil.UserId());
        }
        if (my_detail_jf != null) {
            my_detail_jf.setText(String.format("%d", MatchSharedUtil.getPoint()));
        }
        if(my_detail_yhq!=null){
            my_detail_yhq.setText(MatchSharedUtil.getCoupon());
        }
        if (my_detail_avatar != null) {
            Glide.with(getActivity()).load(MatchSharedUtil.UserAvatar()).apply(GlideOptionsManager.getInstance()
                    .getBannerOptions(0)).into(my_detail_avatar);
//            ImageLoader.getInstance().displayImage(MatchSharedUtil.UserAvatar()
//                    , my_detail_avatar, ImageLoaderOptions.AvatarOptions());
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_to_pay:
                //充值
                ShowActivity.showActivity(getActivity(), PayDetailActivity.class);
                break;
            case R.id.qiandao_button:
                //签到
                signIn();
                break;
            case R.id.my_detail_mfb_button:
                //魔方宝
                ShowActivity.showActivity(getActivity(), MfbActivity.class);
                break;
            case R.id.my_detail_yhq_button:
                //优惠券
                ShowActivity.showConponsActivity(getActivity());
                break;
            case R.id.my_detail_jf_button:
                //积分
                ShowActivity.showActivity(getActivity(), PointActivity.class);
                break;
            case R.id.my_detail_dingyue_button:
                //订阅
                my_dingyue_point.setVisibility(View.GONE);
                ShowActivity.showActivityForResult(getActivity(), null, MySubscripActivity.class, RequestCode.STUDY);
                break;
            case R.id.my_detail_blog_button:
                //文章
//                ShowActivity.showActivity(getActivity(), MyBlogListActivity.class);
                break;
            case R.id.my_detail_open_button:
                ShowActivity.showWebViewActivity(getActivity(), HtmlUrl.OPEN);
                break;
            case R.id.my_detail_select_stock:
                //跳转自选股
                ShowActivity.showActivity(getActivity(), SelectStockActivity.class);
                break;
            case R.id.my_detail_shouc_button:
                //收藏
                ShowActivity.showActivity(getActivity(), UserCollectsActivity.class);
                break;
            case R.id.my_detail_care_button:
                //关注
                Bundle bundle = new Bundle();
                bundle.putSerializable("type", 0);
                ShowActivity.showActivity(getActivity(), bundle, CareTeacherActivity.class);
                break;
            case R.id.my_detail_question_button:
                //提问
                my_ask_point.setVisibility(View.GONE);
                ShowActivity.showActivity(getActivity(), MyQuestionActivity.class);
                break;
            case R.id.my_detail_setting_button:
                //设置
                ShowActivity.showSettingActivity(getActivity());
                break;
            case R.id.my_detail_chat_stock_button:
                //跳转比赛列表
                if(ShowActivity.isLogin(getActivity())){
                    ShowActivity.showActivity(getActivity(),MatchListActivity.class);
                }
                break;
            case R.id.my_detail_invite_button:
                //邀请
                ShowActivity.showActivity(getActivity(), InviteActivity.class);
                break;
            case R.id.my_detail_kf_button:
                //客服
                if (kf_dialog == null) {
                    kf_dialog = dialog1();
                }
                kf_dialog.show();
                break;
            case R.id.my_message_layout:
                ShowActivity.showMyDetail(getActivity());
                break;
            case R.id.my_detail_vip:
                //跳转VIP页面
                ShowActivity.skipVIPActivity(getActivity());
                break;
        }
    }

    private Dialog dialog1() {
        Dialog dialog = new Dialog(getActivity(), R.style.myDialogTheme);
        View layout = LayoutInflater.from(getActivity()).inflate(R.layout.kefu_layout, null);
        TextView qq_button = (TextView) layout.findViewById(R.id.qq_kefu_button);
        TextView phone_button = (TextView) layout.findViewById(R.id.phone_id_button);
        phone_button.setOnClickListener(v -> {
            // TODO Auto-generated method stub
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:4000607878"));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            kf_dialog.dismiss(); //
        });
        qq_button.setOnClickListener(v -> {
            // TODO Auto-generated method stub
            String url = "mqqwpa://im/chat?chat_type=wpa&uin=2852273339&version=1";
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
            kf_dialog.dismiss(); //关闭dialog
        });
        dialog.setContentView(layout);
        Window win = dialog.getWindow();
        win.setGravity(Gravity.BOTTOM);
        WindowManager.LayoutParams lp = win.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        win.setAttributes(lp);
        win.setWindowAnimations(R.style.dialoganimstyle);
        dialog.setCancelable(true);
        return dialog;
    }
    private boolean checkVa(String err_msg) {
        if (err_msg.equals("用户登录失败或已过期") || err_msg.equals(Constans.NEED_LOGIN)) {
            return true;
        }
        return false;
    }

    /**
     * 签到
     */
    public void signIn() {
        WebBase.signIn(new JSONHandler() {
            @Override
            public void onSuccess(JSONObject obj) {
               MatchSharedUtil.setPoint(obj.optLong("point"));
                my_detail_jf.setText(String.format("%d", MatchSharedUtil.getPoint()));
                qiandao.setChecked(false);
                qiandao.setEnabled(false);
                if (dialog == null) {
                    dialog = SignInDialog.createDialog(getActivity());
                }
                dialog.setMessage(obj.optString("gains"));
                dialog.show();
            }

            @Override
            public void onFailure(String err_msg) {
                showToast("签到失败");
                qiandao.setChecked(true);
                qiandao.setEnabled(true);
            }
        });
    }
    private void getWolle() {
        WebBase.getWalle(new JSONHandler() {
            @Override
            public void onSuccess(JSONObject obj) {
                JSONObject pays = obj.optJSONObject("pay");
                JSONObject point = obj.optJSONObject("point");
                JSONObject coupon=obj.optJSONObject("coupon");
                MatchSharedUtil.setPays(pays.optString("unfrozen"));
                MatchSharedUtil.setPoint(point.optLong("unfrozen"));
                MatchSharedUtil.setCoupon(coupon.optString("unfrozen"));
                updataUserMessage();
                home_scrollview.onRefreshComplete();
            }

            @Override
            public void onFailure(String err_msg) {
                home_scrollview.onRefreshComplete();
            }
        });
    }
    /**
     * 签到状态
     */
    public void userSigns() {
        WebBase.userSigns(new JSONHandler() {
            @Override
            public void onSuccess(JSONObject obj) {
                if (obj.optInt("today_sign") == 0) {
                    qiandao.setOnClickListener(v -> {
                        if (!qiandao.isChecked()) {
                            signIn();
                        } else {
                            showToast("今日已签到");
                        }
                    });
                    qiandao.setChecked(true);
                    qiandao.setEnabled(true);
                } else {
                    qiandao.setChecked(false);
                    qiandao.setEnabled(false);
                }
            }

            @Override
            public void onFailure(String err_msg) {
                if (checkVa(err_msg)) {
//                    MatchSharedUtil.setAuthtoken("");
//                    ((MainActivity) getActivity()).checked();
                } else {
                    Toast.makeText(getActivity(), err_msg, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void upMessage() {
        getWolle();
    }
}
