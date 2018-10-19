package com.zbmf.newmatch.listener;


import com.zbmf.newmatch.bean.Adverts;
import com.zbmf.newmatch.bean.City;
import com.zbmf.newmatch.bean.HomeMatchList;
import com.zbmf.newmatch.bean.MatchDescBean;
import com.zbmf.newmatch.bean.PopWindowBean;
import com.zbmf.newmatch.bean.SchoolBean;
import com.zbmf.newmatch.bean.Traders;
import com.zbmf.newmatch.bean.UserWallet;
import com.zbmf.worklibrary.baseview.BaseView;

import java.util.List;
import java.util.Map;

/**
 * Created by xuhao
 * on 2017/11/22.
 */

public interface IHomeView extends BaseView{
    void refreshStockIndex(Map<String, String> map);
    void RusnSupremeMatchAdapter(List<HomeMatchList.Result.Recommend> matchBeans);
    void RushBannerImage(List<Adverts> imgList);
    void rushSponsorAds(List<Adverts> sponsor, int gainStatus);
    void RushMatchSchool(SchoolBean schoolBean);
    void RushTraderList(List<Traders> traders);
    void popWindow(PopWindowBean popWindowBean, int gainStatus);
    void RushHostMatch(List<HomeMatchList.Result.Hot> matchBeans);
    void RushCity(List<City> cityList);
    void RushScrollView();
    void onErr(String msg);
    void refreshMatchDesc(MatchDescBean.Result result);
    void refreshMatchDescErr(String msg);
    void userWallet(UserWallet userWallet);
    void userWalletErr(String msg);
}
