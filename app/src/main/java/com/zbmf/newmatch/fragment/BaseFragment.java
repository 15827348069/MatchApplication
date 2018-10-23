package com.zbmf.newmatch.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.zbmf.newmatch.R;
import com.zbmf.worklibrary.baseview.BaseView;
import com.zbmf.worklibrary.dialog.CustomProgressDialog;
import com.zbmf.worklibrary.presenter.BasePresenter;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by xuhao
 * on 2017/2/15.
 */

public abstract class BaseFragment<T extends BasePresenter> extends Fragment implements BaseView{
    private View view;
    private T presenter;
    private Unbinder mUnbinder;
    private CustomProgressDialog progressDialog;
    private boolean UserVisible;//标识是否已经显示
    private LinearLayout dialog_layout;
    private boolean already_initDate;

    public boolean isUserVisible() {
        return UserVisible;
    }

    public void setUserVisible(boolean userVisible) {
        UserVisible = userVisible;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        setUserVisible(isVisibleToUser);
//        onResume();
    }
    private LayoutInflater mInflater;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.mInflater=inflater;
        view=inflater.inflate(getLayout(),null);
        mUnbinder= ButterKnife.bind(this,view);
        dialog_layout=view.findViewById(R.id.dialog_layout);
        initView();
        presenter=initPresent();
        initData();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (presenter!=null){
            presenter.onStart(this);
        }
    }

    protected abstract int getLayout();
    protected abstract void initView();
    protected abstract void initData();
    protected abstract T initPresent();
    protected void showToast(String content) {
        if(getActivity()!=null){
            Toast.makeText(getActivity(), content, Toast.LENGTH_SHORT).show();
        }
    }
    public T getPresenter(){
        return presenter;
    }

    public View getFragmentView(){
        return view;
    }
    @Override
    public void onResume() {
        super.onResume();
        //这个方法一定会执行
        if(isUserVisible()&&isAdded()){
            if(presenter!=null){
                //fragment初始化加载数据
                presenter.getDatas();
            }else{
                presenter=initPresent();
            }
        }
        if(view==null){
            view=mInflater.inflate(getLayout(),null);
            initFragment();
            onRush();
        }
        StatService.onPageStart(getContext(),getClass().getName());
    }
    public void setTitleMessage(String message){
        TextView textView=view.findViewById(R.id.tv_title_text);
        textView.setText(message);
    }
    public void setLoadingVis(int visi){
        if(dialog_layout!=null){
            dialog_layout.setVisibility(visi);
        }
    }
    protected void setinitData(boolean already_initDate){
        this.already_initDate=already_initDate;
    }
    private void onVisible(){
        //view已经显示并且不为空，没有加载过数据时去加载数据
        if(UserVisible&&view!=null/*&&!already_initDate*/){
            initData();
            already_initDate=true;
        }
    }
    private void initFragment(){
        initView();
        onVisible();
    }

    /*-----------下面是 progressDialog------------*/
    public void dialogShow(){
        if(getView(R.id.dialog_layout)!=null){
            getView(R.id.dialog_layout).setVisibility(View.VISIBLE);
        }
    }
    public void dialogDiss(){
        if(getView(R.id.dialog_layout)!=null){
            getView(R.id.dialog_layout).setVisibility(View.GONE);
        }
    }

    public void ShowLoading() {
        if(progressDialog!=null&&!progressDialog.isShowing()){
            progressDialog.show();
        }
    }

    protected <E extends View>E getView(int resourcesId){
        return (E) view.findViewById(resourcesId);
    }

    public void DissLoading() {
        if(progressDialog!=null&&progressDialog.isShowing()){
            progressDialog.dismiss();
        }
    }
    public void initProgressDialog(String tipMsg) {
        if (progressDialog == null) {
            progressDialog = CustomProgressDialog.createDialog(getActivity());
        }
    }

    @Override
    public void onRefreshComplete() {

    }

    @Override
    public void loadMore() {

    }

    @Override
    public void dissLoading() {
        DissLoading();
    }

    @Override
    public void showToastMsg(String msg) {
        showToast(msg);
    }
    @Override
    public void onDestroyView() {
        if (mUnbinder != null) {
            mUnbinder.unbind();
            mUnbinder=null;
        }
        if(progressDialog!=null){
            progressDialog=null;
        }
        if(presenter!=null){
            presenter.onDestroy();
            presenter=null;
        }
        super.onDestroyView();
    }
    public void onRush(){

    }
}
