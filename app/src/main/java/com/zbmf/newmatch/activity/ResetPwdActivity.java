package com.zbmf.newmatch.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.zbmf.newmatch.R;
import com.zbmf.newmatch.api.JSONHandler;
import com.zbmf.newmatch.api.WebBase;
import com.zbmf.newmatch.util.EditTextUtil;
import com.zbmf.newmatch.util.MyActivityManager;
import com.zbmf.worklibrary.presenter.BasePresenter;

import org.json.JSONObject;

//修改密码
public class ResetPwdActivity extends BaseActivity implements View.OnClickListener {

    private EditText ed_pwd,ed_new,ed_new1;

    @Override
    protected int getLayout() {
        return R.layout.activity_reset_pwd;
    }

    @Override
    protected String initTitle() {
        return "修改密码";
    }

    @Override
    protected void initData(Bundle bundle) {
//添加管理activity
        MyActivityManager.getMyActivityManager().pushAct(this);
        GroupinitView();
        addListener();

    }

    @Override
    protected BasePresenter initPresent() {
        return null;
    }

    public void GroupinitView() {
        ed_pwd = (EditText)findViewById(R.id.ed_pwd);
        ed_new = (EditText)findViewById(R.id.ed_new);
        ed_new1 = (EditText)findViewById(R.id.ed_new1);
    }

    public void addListener() {
        findViewById(R.id.btn_finish).setOnClickListener(this);
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_finish:
                String oldpwd = ed_pwd.getText().toString();
                if (TextUtils.isEmpty(oldpwd)) {
                    Toast.makeText(this,"原密码不能为空",Toast.LENGTH_SHORT).show();
                    return;
                }

                String newpwd = ed_new.getText().toString();
                if (TextUtils.isEmpty(newpwd)) {
                    Toast.makeText(this,"新密码不能为空",Toast.LENGTH_SHORT).show();
                    return;
                }

                if (newpwd.length() < 6) {
                    Toast.makeText(this,"请输入大于6位数密码",Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!EditTextUtil.checkNum(newpwd)) {
                    Toast.makeText(this,"请输入6-18位数字和字母组合",Toast.LENGTH_SHORT).show();
                    return;
                }

                String newpwd1 = ed_new1.getText().toString();
                if(!newpwd1.equals(newpwd)){
                    Toast.makeText(this,"两次密码输入不一致！请重新输入",Toast.LENGTH_SHORT).show();
                    return;
                }

                WebBase.changePwd(oldpwd, newpwd1, new JSONHandler(true,ResetPwdActivity.this,
                        "正在修改密码...") {
                    @Override
                    public void onSuccess(JSONObject obj) {
                        Toast.makeText(ResetPwdActivity.this,"修改密码成功",Toast.LENGTH_LONG).show();
                        finish();
                    }

                    @Override
                    public void onFailure(String err_msg) {
                        Toast.makeText(ResetPwdActivity.this,err_msg,Toast.LENGTH_SHORT).show();
                    }
                });
                break;
        }
    }
}
