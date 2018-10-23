package com.zbmf.newmatch.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.sina.weibo.sdk.api.ImageObject;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.share.WbShareCallback;
import com.sina.weibo.sdk.share.WbShareHandler;
import com.tencent.connect.share.QQShare;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXImageObject;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.zbmf.newmatch.R;
import com.zbmf.newmatch.bean.Group;
import com.zbmf.newmatch.callback.ResultCallback;
import com.zbmf.newmatch.common.Constans;
import com.zbmf.newmatch.common.IntentKey;
import com.zbmf.newmatch.util.BitmapUtil;
import com.zbmf.newmatch.util.DisplayUtil;
import com.zbmf.newmatch.util.FileUtils;
import com.zbmf.newmatch.util.MyActivityManager;
import com.zbmf.newmatch.view.GlideOptionsManager;
import com.zbmf.newmatch.view.RoundedCornerImageView;
import com.zbmf.worklibrary.dialog.CustomProgressDialog;
import com.zbmf.worklibrary.presenter.BasePresenter;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;

import static com.zbmf.newmatch.util.ScreenShot.CImage;


/**
 * Created by xuhao on 2018/3/29.
 */

public class ShareMessageActivity extends BaseActivity implements View.OnClickListener, IUiListener, WbShareCallback {

    @BindView(R.id.tv_content)
    TextView tvContent;
    @BindView(R.id.tv_date)
    TextView tvDate;
    @BindView(R.id.imv_avatar)
    RoundedCornerImageView imvAvatar;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_circle_id)
    TextView tvCircleId;
    @BindView(R.id.imv_erm)
    ImageView imvEwm;
    @BindView(R.id.ll_share_content)
    LinearLayout llShareContent;
    @BindView(R.id.share_wx_friend_circle)
    Button share_wx_friend_circle;
    @BindView(R.id.share_wx_friend)
    Button share_wx_friend;
    @BindView(R.id.share_sina)
    Button share_sina;
    @BindView(R.id.share_qq)
    Button share_qq;
    @BindView(R.id.ll_share)
    LinearLayout llShareLayout;

    private Group group;
    private WbShareHandler shareHandler;
    private final int SHARE_CLIENT = 1;
    private final int SHARE_ALL_IN_ONE = 2;
    private int mShareType = SHARE_ALL_IN_ONE;
    private IWXAPI iwxapi;
    private Tencent mTencent;
    private CustomProgressDialog progressDialog;
    private Bitmap bitmap;

    private final String shareUrl = "http://www.zbmf.com/register?uid=%1$s";

    public void initView() {
        llShareContent.post(() -> {
            Log.e("llShareContent===", "==" + llShareContent.getBottom());
            int height = llShareLayout.getTop() - llShareContent.getBottom();
            if (height > 0) {
                llShareLayout.setTag(height / 2);
                setMargins(llShareContent, 0, height / 2, 0, 0);
            }
        });
    }

    public void setMargins(View v, int l, int t, int r, int b) {
        if (v.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            p.setMargins(l, t, r, b);
            v.requestLayout();
        }
    }

    public void addListener() {
        share_wx_friend_circle.setOnClickListener(this);
        share_wx_friend.setOnClickListener(this);
        share_sina.setOnClickListener(this);
        share_qq.setOnClickListener(this);
    }

    private void createBitMapImg(final View v) {
        if (progressDialog == null) {
            progressDialog = CustomProgressDialog.createDialog(this);
            progressDialog.setMessage(getString(R.string.loading_img));
        }
        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }
        BitmapUtil.takeScreenShot(llShareContent, new ResultCallback<Bitmap>() {
            @Override
            public void onSuccess(final Bitmap bit) {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                bitmap = bit;
                shareImage(v);
            }

            @Override
            public void onError(String message) {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                Toast.makeText(ShareMessageActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initShare() {
        if (iwxapi == null) {
            iwxapi = WXAPIFactory.createWXAPI(this, Constans.WEI_APK_KEY, true);
        }
        if (shareHandler == null) {
            this.shareHandler = new WbShareHandler(this);
            this.shareHandler.registerApp();
        }
        if (mTencent == null) {
            mTencent = Tencent.createInstance(Constans.TencentSDKAppKey, this);
        }
    }

    private void shareImage(View v) {
        switch (v.getId()) {
            case R.id.share_wx_friend_circle:
                sharetoWx(SendMessageToWX.Req.WXSceneTimeline);
                break;
            case R.id.share_wx_friend:
                sharetoWx(SendMessageToWX.Req.WXSceneSession);
                break;
            case R.id.share_sina:
                WeiboMultiMessage weiboMessage = new WeiboMultiMessage();
                weiboMessage.imageObject = getSinaImageObject();
                weiboMessage.textObject = getSinaTextObject();
                shareHandler.shareMessage(weiboMessage, mShareType == SHARE_CLIENT);
                break;
            case R.id.share_qq:
                String img_url = FileUtils.getIntence().DEFAULT_DATA_IMAGEPATH + getPhotoFileName();
                String url = BitmapUtil.saveBitmap(img_url, bitmap);
                final Bundle params = new Bundle();
                params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_IMAGE);
                params.putString(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL, url);
                params.putString(QQShare.SHARE_TO_QQ_APP_NAME, getString(R.string.app_name));
                mTencent.shareToQQ(ShareMessageActivity.this, params, this);
                break;
        }
    }

    private String getPhotoFileName() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHH");
        return dateFormat.format(date) + "_" + group.getId() + "-" + group.getMessageId() + ".jpg";
    }

    private ImageObject getSinaImageObject() {
        ImageObject imageObject = new ImageObject();
        imageObject.setImageObject(CImage(bitmap));
        imageObject.setThumbImage(BitmapUtil.compressImage(bitmap));
        return imageObject;
    }

    private TextObject getSinaTextObject() {
        TextObject textObject = new TextObject();
        textObject.text = getActivitySharedText();
        return textObject;
    }

    private String getActivitySharedText() {
        String format = getString(R.string.weibosdk_share_webpage_template);
        String text = String.format(format, group.getNick_name());
        return text;
    }

    private void sharetoWx(int scene) {
        WXImageObject wxImageObject = new WXImageObject();
        String img_url = FileUtils.getIntence().DEFAULT_DATA_IMAGEPATH + getPhotoFileName();
        String url = BitmapUtil.saveBitmap(img_url, bitmap);
        wxImageObject.setImagePath(url);
        WXMediaMessage msg = new WXMediaMessage();
        msg.mediaObject = wxImageObject;
        //设置缩略图
        Bitmap mBp = Bitmap.createScaledBitmap(bitmap, 120, 120, true);
        msg.thumbData = BitmapUtil.bmpToByteArray(mBp, true);
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("img");//  transaction字段用
        req.message = msg;
        req.scene = scene;
        iwxapi.sendReq(req);
    }

    private String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }

    @Override
    public void onComplete(Object o) {
        showToast("分享成功");
    }

    @Override
    public void onError(UiError uiError) {
        showToast("分享失败");
    }

    @Override
    public void onCancel() {
        showToast("取消分享");
    }

    @Override
    public void onWbShareSuccess() {
        showToast("分享成功");
    }

    @Override
    public void onWbShareCancel() {
        showToast("取消分享");
    }

    @Override
    public void onWbShareFail() {
        showToast("分享失败");
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_share_message_layout;
    }

    @Override
    protected String initTitle() {
        return null;
    }

    @Override
    protected void initData(Bundle bundle) {
        //添加管理activity
        MyActivityManager.getMyActivityManager().pushAct(this);
        initView();
        //添加监听事件
        addListener();

        if (bundle != null) {
            group = (Group) bundle.getSerializable(IntentKey.GROUP);
            Glide.with(this).load(group.getAvatar()).apply(GlideOptionsManager.getInstance()
                    .getRequestOptionsMatch()).into(imvAvatar);
            imvEwm.setImageBitmap(BitmapUtil.createQRCode(String.format(shareUrl, group.getId()),
                    DisplayUtil.dip2px(this, 100)));
            tvName.setText(group.getNick_name());
            tvCircleId.setText(String.format("圈号：%s", group.getId()));
            tvContent.setText(group.getShareMessage());
            tvDate.setText(group.getMessageDate());
        }
        initShare();
    }

    @Override
    protected BasePresenter initPresent() {
        return null;
    }

    @Override
    public void onClick(View v) {
        if (bitmap == null) {
            createBitMapImg(v);
        } else {
            shareImage(v);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Tencent.onActivityResultData(requestCode, resultCode, data, this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (shareHandler != null) {
            shareHandler.doResultIntent(intent, this);
        }
    }
}
