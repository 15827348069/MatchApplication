package com.zbmf.newmatch.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zbmf.newmatch.R;
import com.zbmf.newmatch.api.JSONHandler;
import com.zbmf.newmatch.api.WebBase;
import com.zbmf.newmatch.bean.User;
import com.zbmf.newmatch.util.JSONParse;
import com.zbmf.newmatch.util.MyActivityManager;
import com.zbmf.newmatch.util.ShowActivity;
import com.zbmf.newmatch.util.ToastUtils;
import com.zbmf.worklibrary.presenter.BasePresenter;

import org.json.JSONObject;

public class AccountBindActivity extends BaseActivity {

    private LinearLayout ll_none, ll_account;
    private TextView tv_username;
    private TextView tv_phone;

    @Override
    protected int getLayout() {
        return R.layout.activity_account_bind;
    }

    @Override
    protected String initTitle() {
        return null;
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
        initTitle("账号和绑定设置");
        tv_username = ((TextView) findViewById(R.id.tv_username));
        tv_phone = ((TextView) findViewById(R.id.tv_phone));
        ll_none = (LinearLayout) findViewById(R.id.ll_none);
        ll_account = (LinearLayout) findViewById(R.id.ll_account);
    }

    public void GroupinitData() {
        WebBase.isBind(new JSONHandler(true, this, "加载中....") {
            @Override
            public void onSuccess(JSONObject obj) {
                User user = JSONParse.isBind(obj);
                if ("1".equals(user.getIs_bind())) {
                    tv_username.setText(user.getUsername());
                    String phone = user.getPhone();
                    tv_phone.setText(phone.substring(0, 3) + "****" + phone.substring(7, 11));
                } else {
                    ll_none.setVisibility(View.VISIBLE);
                    ll_account.setVisibility(View.GONE);
                }
            }
            @Override
            public void onFailure(String err_msg) {

            }
        });
    }

    public void addListener() {
        findViewById(R.id.btn_bind).setOnClickListener(v ->
                ShowActivity.showActivity(AccountBindActivity.this, BindPhoneActivity.class));
        findViewById(R.id.rl_phone).setOnClickListener(v ->
                        ToastUtils.showSquareTvToast("暂不支持修改绑定手机号，请联系客服"));
    }


}
