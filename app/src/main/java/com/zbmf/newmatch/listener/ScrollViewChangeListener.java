package com.zbmf.newmatch.listener;

import com.zbmf.worklibrary.pulltorefresh.PullToRefreshScrollView;

/**
 * Created by pq
 * on 2018/6/14.
 */

public interface ScrollViewChangeListener {
    void onScrollChanged(PullToRefreshScrollView scrollView, int x, int y, int oldx, int oldy);

    void scrollTop();
    void scrollDown();
    void scrollBottom();
    void onScroll(int x, int y);
    void scrollStop();

 /*   void topToShow();
    void bottomToShow();*/
}
