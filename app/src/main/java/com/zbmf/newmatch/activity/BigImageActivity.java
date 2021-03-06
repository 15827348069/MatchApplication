package com.zbmf.newmatch.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.zxing.Result;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.zbmf.newmatch.R;
import com.zbmf.newmatch.dialog.ImageDialog;
import com.zbmf.newmatch.util.BitmapUtil;
import com.zbmf.newmatch.util.ImageLoaderOptions;
import com.zbmf.newmatch.util.LogUtil;
import com.zbmf.newmatch.util.MatchSharedUtil;
import com.zbmf.newmatch.util.MyActivityManager;
import com.zbmf.newmatch.util.PhotoViewAttacher;
import com.zbmf.newmatch.util.WebClickUitl;
import com.zbmf.newmatch.view.SwipeToFinishView;

import java.util.Date;

public class BigImageActivity extends AppCompatActivity {
    private String img_url;
    private ImageView big_img_id;
    private PhotoViewAttacher mAttacher;
    private Result result;//二维码解析结果
    private ImageDialog dialog;
    private Bitmap bmp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_big_image);
        //添加管理activity
        MyActivityManager.getMyActivityManager().pushAct(this);
        new SwipeToFinishView(this);
        init();
    }

    private void init() {
        big_img_id = (ImageView) findViewById(R.id.big_img_id);
        img_url = getIntent().getStringExtra("img_url");
        if (img_url != null && !img_url.equals("")) {
            img_url = img_url.replace(MatchSharedUtil.instance().getLiveImg(), "");

            ImageLoader.getInstance().getDiskCache().remove(img_url.trim());
            ImageLoader.getInstance().displayImage(img_url, big_img_id, ImageLoaderOptions.BigProgressOptions(),
                    new ImageLoadingListener() {

                @Override
                public void onLoadingStarted(String s, View view) {

                }

                @Override
                public void onLoadingFailed(String s, View view, FailReason failReason) {

                }

                @Override
                public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                    //加载成功后放入放大缩小控件
                    ViewGroup.LayoutParams lp = big_img_id.getLayoutParams();
                    lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
                    lp.height = ViewGroup.LayoutParams.MATCH_PARENT;
                    bmp=bitmap;
                    big_img_id.setLayoutParams(lp);
                    mAttacher = new PhotoViewAttacher(big_img_id);
                    mAttacher.setOnLongClickListener(v -> {
                        decodeImage();
                        return false;
                    });
                }

                @Override
                public void onLoadingCancelled(String s, View view) {

                }
            });
        }
    }

    /**
     * 判断是否为二维码
     * return
     */
    private void decodeImage() {
        result = BitmapUtil.handleQRCodeFormBitmap(bmp);
        LogUtil.e("识别结果=="+result);
        if (dialog == null) {
            dialog = dialog1();
        }
        if (result == null) {
            dialog.setZButton(View.GONE);
        } else {
            dialog.setZButton(View.VISIBLE);
        }
        dialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mAttacher != null) {
            mAttacher.cleanup();
        }
    }

    /**
     * 发送给好友
     */
    private void sendToFriends() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        String url= MediaStore.Images.Media.insertImage(getContentResolver(),bmp,new Date().getTime()+"", null);
        Uri imageUri = Uri.parse(url);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_STREAM, imageUri);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(Intent.createChooser(intent, getTitle()));
    }

    /**
     * 先保存到本地再广播到图库
     */
    private void saveImageToGallery() {
        // 其次把文件插入到系统图库
        String url= MediaStore.Images.Media.insertImage(getContentResolver(), bmp,new Date().getTime()+"", null);
        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse(url)));
    }

    private ImageDialog dialog1() {
        return ImageDialog.createDialog(BigImageActivity.this)
                .setBtnClick(new ImageDialog.OnBtnClick() {
                    @Override
                    public void send() {
                        sendToFriends();//把图片发送给好友
                    }

                    @Override
                    public void save() {
                        saveImageToGallery();
                    }
                    @Override
                    public void zxing() {
                        WebClickUitl.ShowActivity(BigImageActivity.this,result.toString());
                    }
                });
    }
}
