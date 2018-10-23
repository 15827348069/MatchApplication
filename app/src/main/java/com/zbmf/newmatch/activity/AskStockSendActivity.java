package com.zbmf.newmatch.activity;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.zbmf.newmatch.R;
import com.zbmf.newmatch.adapter.StockAdapter;
import com.zbmf.newmatch.api.JSONHandler;
import com.zbmf.newmatch.api.WebBase;
import com.zbmf.newmatch.common.IntentKey;
import com.zbmf.newmatch.util.MyActivityManager;
import com.zbmf.newmatch.util.ShowActivity;
import com.zbmf.newmatch.view.TextDialog;
import com.zbmf.worklibrary.presenter.BasePresenter;

import org.json.JSONObject;


/**
 * Created by xuhao on 2017/8/31.
 */

public class AskStockSendActivity extends BaseActivity implements View.OnClickListener {
    private ImageButton send_button;
    private EditText etContent;
    private AutoCompleteTextView etStock;
    private StockAdapter adapter;
    private TextView tvInputNumber;
    private TextDialog textDialog;
    private int flag;
    private String stock_symbol,stock_name;


    @Override
    protected int getLayout() {
        return R.layout.activity_ask_stock_layout;
    }

    @Override
    protected String initTitle() {
        return "提问";
    }

    @Override
    protected void initData(Bundle bundle) {
        //添加管理activity
        MyActivityManager.getMyActivityManager().pushAct(this);
        send_button = (ImageButton) getView(R.id.imb_send);
        send_button.setVisibility(View.VISIBLE);
        etStock = (AutoCompleteTextView) getView(R.id.et_input_stock);
        etContent = (EditText) getView(R.id.et_input_content);
        tvInputNumber = (TextView) getView(R.id.tv_input_number);

        addListener();
        GroupinitData();
    }

    @Override
    protected BasePresenter initPresent() {
        return null;
    }

    public void GroupinitData() {
        flag=getIntent().getIntExtra(IntentKey.FLAG,0);
        stock_name=getIntent().getStringExtra(IntentKey.STOCK_NAME);
        stock_symbol=getIntent().getStringExtra(IntentKey.STOCK_SYMOL);
        if(stock_name!=null){
            etStock.setText(stock_name+"("+stock_symbol+")");
            showSoftInputFromWindow(etContent);
        }
        adapter=new StockAdapter(AskStockSendActivity.this);
        etStock.setAdapter(adapter);
    }
    private void showSoftInputFromWindow(EditText editText) {
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }

    public void addListener() {
        send_button.setOnClickListener(this);
        etStock.setOnItemClickListener((parent, view, position, id) -> stock_symbol=adapter.getSymbolAtPosition(position));
        etStock.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(before>=1){
                    stock_symbol=null;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        etContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                tvInputNumber.setText(s.length() + "/140");
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imb_send:
                String content = etContent.getText().toString();
                if (TextUtils.isEmpty(stock_symbol)) {
                    showToast("请输入正确的股票信息");
                    return;
                }
                if (TextUtils.isEmpty(content)) {
                    showToast("请输入提问内容");
                    return;
                }
                if(flag==1||flag==0){
                    //发送
                    WebBase.sendAskStock(stock_symbol, content, new JSONHandler(true,
                            AskStockSendActivity.this, getString(R.string.commit)) {
                        @Override
                        public void onSuccess(JSONObject obj) {
                            if (textDialog == null) {
                                textDialog = getTextDialog();
                            }
                            textDialog.show();
                        }

                        @Override
                        public void onFailure(String err_msg) {
                            showToast(err_msg);
                        }
                    });
                }else if(flag==2||flag==3){
                    WebBase.stockAsk(stock_symbol, content, new JSONHandler(true,
                            AskStockSendActivity.this, getString(R.string.commit)) {
                        @Override
                        public void onSuccess(JSONObject obj) {
                            if (textDialog == null) {
                                textDialog = getTextDialog();
                            }
                            textDialog.show();
                        }

                        @Override
                        public void onFailure(String err_msg) {
                            showToast(err_msg);
                        }
                    });
                }
                break;
        }
    }
    private TextDialog getTextDialog(){
        return  TextDialog.createDialog(AskStockSendActivity.this)
                .setLeftButton(getString(R.string.cancel))
                .setMessage(getString(R.string.ask_success))
                .setRightButton("我的提问")
                .setRightClick(() -> {
                    switch (flag){
                        case 0:
                            ShowActivity.showActivity(AskStockSendActivity.this,MyQuestionActivity.class);
                            break;
                        case 2:
                            setResult(RESULT_OK);
                            break;
//                            case 3:
//                                Bundle bundle=new Bundle();
//                                bundle.putInt(IntentKey.SELECT,1);
//                                ShowActivity.showActivity(AskStockSendActivity.this,bundle,DongAskActivity.class);
//                                break;
                    }
                    finish();
                });
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive()) {
            imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
}
