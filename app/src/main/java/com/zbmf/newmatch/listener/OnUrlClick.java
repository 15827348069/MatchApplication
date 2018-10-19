package com.zbmf.newmatch.listener;


import com.zbmf.newmatch.bean.BlogBean;
import com.zbmf.newmatch.bean.Group;
import com.zbmf.newmatch.bean.Screen;
import com.zbmf.newmatch.bean.Stock;
import com.zbmf.newmatch.bean.Video;

/**
 * Created by xuhao
 * on 2017/8/28.
 */

public interface OnUrlClick {
    void onGroup(Group group);
    void onBolg(BlogBean blogBean);
    void onVideo(Video video);
    void onWeb(String url);
    void onImage(String url);
    void onPay();
    void onStock(Stock stock);
    void onScreen();
    void onScreenDetail(Screen screen);
    void onModeStock();
    void onDongAsk();
    void onQQ(String url);
}
