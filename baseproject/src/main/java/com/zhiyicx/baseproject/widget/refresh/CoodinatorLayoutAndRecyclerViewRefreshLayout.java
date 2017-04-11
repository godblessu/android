package com.zhiyicx.baseproject.widget.refresh;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.aspsine.swipetoloadlayout.SwipeToLoadLayout;
import com.zhiyicx.common.utils.log.LogUtils;

/**
 * @author LiuChao
 * @describe 处理CoodianatorLayout中ToolBar滚动隐藏后，下拉刷新和recyclerview滑动的冲突
 * @date 2017/4/7
 * @contact email:450127106@qq.com
 */

public class CoodinatorLayoutAndRecyclerViewRefreshLayout extends SwipeToLoadLayout {
    private static final String TAG = "CoodinatorLayoutAndRecy";
    private float downX, downY;
    private int startY;
    private boolean isFirstOnLayout = true;

    public CoodinatorLayoutAndRecyclerViewRefreshLayout(Context context) {
        super(context);
    }

    public CoodinatorLayoutAndRecyclerViewRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CoodinatorLayoutAndRecyclerViewRefreshLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void init() {
        // 获取刷新控件初始距离顶部的位置
        if (isFirstOnLayout) {
            isFirstOnLayout = false;
            int[] location = new int[2];
            getLocationOnScreen(location);
            startY = location[1];
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
//        LogUtils.i(TAG + "onLayout");
        init();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = ev.getX();
                downY = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                float temX = ev.getX();
                float temY = ev.getY();
                float dX = temX - downX;
                float dY = temY - downY;
                // 垂直方向 手指向下滑动
                if (Math.abs(dY) / Math.abs(dX) >= 1 && dY > 0) {
                    int[] location = new int[2];
                    getLocationOnScreen(location);
//                    LogUtils.i(TAG + "getY" + getY() + "getTop" + getTop() + "location" + location[1] + "startY  " + startY);
                    if (location[1] < startY) {
                        return false;
                    }
                }

                break;
            case MotionEvent.ACTION_UP:
                break;

        }
        return super.onInterceptTouchEvent(ev);
    }
}