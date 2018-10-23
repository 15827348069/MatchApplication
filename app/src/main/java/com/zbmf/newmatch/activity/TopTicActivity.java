package com.zbmf.newmatch.activity;

import android.os.Bundle;
import android.widget.TextView;

import com.zbmf.newmatch.R;
import com.zbmf.newmatch.bean.TopticBean;
import com.zbmf.newmatch.util.MyActivityManager;
import com.zbmf.worklibrary.presenter.BasePresenter;


/**
 * Created by xuhao on 2017/4/25.
 */

public class TopTicActivity extends BaseActivity {
    private TextView toptic_text;
    private TopticBean topticBean;

    @Override
    protected int getLayout() {
        return R.layout.toptic_layout;
    }

    @Override
    protected String initTitle() {
        return null;
    }

    @Override
    protected void initData(Bundle bundle) {
        //添加管理activity
        MyActivityManager.getMyActivityManager().pushAct(this);
        topticBean= (TopticBean) getIntent().getSerializableExtra("TopticBean");
        initTitle(topticBean.getSubject());
        toptic_text= (TextView) findViewById(R.id.toptic_text);

        toptic_text.setText(topticBean.getContent());
    }

    @Override
    protected BasePresenter initPresent() {
        return null;
    }

}
