package com.zbmf.newmatch.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.zbmf.newmatch.R;


/**
 * Created by xuhao on 2017/7/27.
 */

public class ImageDialog extends Dialog {
    private OnBtnClick btnClick;

    public ImageDialog setBtnClick(OnBtnClick btnClick) {
        this.btnClick = btnClick;
        return this;
    }

    public ImageDialog(@NonNull Context context) {
        super(context);
        initDialog();
    }
    public ImageDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
        initDialog();
    }
    public ImageDialog setZButton(int V){
        Button button= (Button) getWindow().findViewById(R.id.btn_zxing);
        button.setVisibility(V);
        return this;
    };
    public static ImageDialog createDialog(Context context) {
        return new ImageDialog(context, R.style.myDialogTheme);
    }

    public void initDialog() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_img_layout, null);
        setContentView(view);
        Window win = getWindow();
        WindowManager.LayoutParams lp = win.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        win.setAttributes(lp);
        win.setGravity(Gravity.BOTTOM);
        win.setWindowAnimations(R.style.dialoganimstyle);
        setCancelable(true);
        win.findViewById(R.id.btn_cause).setOnClickListener(v -> dismiss());
        win.findViewById(R.id.btn_save).setOnClickListener(v -> {
            if(btnClick!=null){
                btnClick.save();
                dismiss();
            }
        });
        win.findViewById(R.id.btn_send).setOnClickListener(v -> {
            if(btnClick!=null){
                btnClick.send();
                dismiss();
            }
        });
        win.findViewById(R.id.btn_zxing).setOnClickListener(v -> {
            if(btnClick!=null){
                btnClick.zxing();
                dismiss();
            }
        });
    }
    public interface OnBtnClick{
        void send();
        void save();
        void zxing();
    }
}
