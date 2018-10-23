package com.zbmf.newmatch.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.zbmf.newmatch.R;
import com.zbmf.newmatch.api.JSONHandler;
import com.zbmf.newmatch.api.WebBase;
import com.zbmf.newmatch.bean.GroupVers;
import com.zbmf.newmatch.common.Constans;
import com.zbmf.newmatch.common.HtmlUrl;
import com.zbmf.newmatch.common.RequestCode;
import com.zbmf.newmatch.db.DBManager;
import com.zbmf.newmatch.util.FileUtils;
import com.zbmf.newmatch.util.JSONParse;
import com.zbmf.newmatch.util.MatchSharedUtil;
import com.zbmf.newmatch.util.MyActivityManager;
import com.zbmf.newmatch.util.SendBrodacast;
import com.zbmf.newmatch.util.ShowActivity;
import com.zbmf.newmatch.util.UpdateManager;
import com.zbmf.worklibrary.dialog.CustomProgressDialog;
import com.zbmf.worklibrary.presenter.BasePresenter;

import org.json.JSONObject;

/**
 * 设置页
 */
public class SettingActivity extends BaseActivity implements View.OnClickListener {

    private TextView tv_version;
    private String version;
    private boolean isTip = false;
    private DBManager dbManager;


    @Override
    protected int getLayout() {
        return R.layout.activity_setting;
    }

    @Override
    protected String initTitle() {
        return "设置";
    }

    @Override
    protected void initData(Bundle bundle) {
//添加管理activity
        MyActivityManager.getMyActivityManager().pushAct(this);
        GroupinitView();
        addListener();
        GroupinitData();


    }

    @Override
    protected BasePresenter initPresent() {
        return null;
    }

    public void GroupinitView() {
        tv_version = (TextView)findViewById(R.id.tv_version);
        SwitchCompat switch_music = (SwitchCompat) findViewById(R.id.switch_music);
        switch_music.setChecked(MatchSharedUtil.getMessageAll());
        switch_music.setOnCheckedChangeListener((buttonView, isChecked) ->
                MatchSharedUtil.setMessageAll(isChecked));
    }

    public void GroupinitData() {
        PackageManager pm = getPackageManager();
        PackageInfo pi;
        try {
            pi = pm.getPackageInfo(getPackageName(), 0);
            version = pi.versionName;
            tv_version.setText("炒股圈子"+version);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        vers(false);
        dbManager=new DBManager(getBaseContext());
    }

    public void addListener() {
        findViewById(R.id.btn_logout).setOnClickListener(this);
        findViewById(R.id.tv_clear).setOnClickListener(this);
        findViewById(R.id.tv_bind).setOnClickListener(this);
        findViewById(R.id.tv_check).setOnClickListener(this);
        findViewById(R.id.about_us).setOnClickListener(this);
        findViewById(R.id.yj_back).setOnClickListener(this);
        findViewById(R.id.textView2).setOnClickListener(this);
    }


    private void vers(final boolean b) {
        WebBase.vers(new JSONHandler(b,this,"正在检查新版本") {
            @Override
            public void onSuccess(JSONObject obj) {
                GroupVers vers = JSONParse.vers(obj);
                if (version.compareTo(vers.getVersion()) < 0) {
                    UpdateManager update = new UpdateManager(SettingActivity.this);
                    update.setApkUrl(vers.getUrl());
                    update.setForce(false);
                    update.setVersion(vers.getVersion());
                    update.checkUpdateInfo(true);
                }else{
                    if(b)
                        Toast.makeText(SettingActivity.this,"已是最新版本!",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(String err_msg) {
                Toast.makeText(SettingActivity.this,err_msg,Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_check:
                vers(true);
                break;
            case R.id.btn_logout:
                log_out();
                break;
            case R.id.tv_clear:
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    ActivityCompat.requestPermissions(SettingActivity.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            RequestCode.WRITE_EXTERNAL_STORAGE);
                } else {
                    new ClearCacheTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }

                break;
            case R.id.tv_bind:
                ShowActivity.showActivity(this,AccountBindActivity.class);
                break;
            case R.id.about_us:
                ShowActivity.showWebViewActivity(this, HtmlUrl.ABOUT_US);
                break;
            case R.id.yj_back:
                ShowActivity.showWebViewActivity(this,HtmlUrl.MESSAGE_BACK);
                break;
            case R.id.textView2:
                ShowActivity.showActivity(this,ResetPwdActivity.class);
                break;
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case RequestCode.WRITE_EXTERNAL_STORAGE:
                new ClearCacheTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                break;
        }
    }
    private CustomProgressDialog progressDialog;

    class ClearCacheTask extends AsyncTask<Void,Void,Boolean>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if(progressDialog==null){
                progressDialog= CustomProgressDialog.createDialog(SettingActivity.this);
            }
            progressDialog.setMessage("正在清除缓存...");
            progressDialog.show();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            boolean cleared = FileUtils.getIntence().deletAllCacheFiles();
            try {
                if(cleared)
                    Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return cleared;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            progressDialog.dismiss();
            if(aBoolean)
            Toast.makeText(SettingActivity.this,"缓存清理成功",Toast.LENGTH_SHORT).show();
            else  Toast.makeText(SettingActivity.this,"清理失败",Toast.LENGTH_SHORT).show();
        }
    }
    public void log_out(){
        dbManager.setUnablemessage(null);
        WebBase.logout(new JSONHandler(true,SettingActivity.this,"正在退出登录...") {
            @Override
            public void onSuccess(JSONObject obj) {
                finish();
            }
            @Override
            public void onFailure(String err_msg) {
                finish();
            }
        });
        Intent intent = new Intent(Constans.NEW_LIVE_MSG_READ);
        sendBroadcast(intent);
        Intent intent2 = new Intent(Constans.UPDATE_VIDEO_LIST);
        sendBroadcast(intent2);
        MatchSharedUtil.clearUser();
        SendBrodacast.send(SettingActivity.this,Constans.LOGOUT);
        deleteDatabase("webview.db");
        deleteDatabase("webviewCache.db");
    }
}
