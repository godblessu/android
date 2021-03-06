package com.zhiyicx.thinksnsplus.modules.gallery;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import com.zhiyicx.baseproject.base.TSFragment;
import com.zhiyicx.baseproject.impl.photoselector.ImageBean;
import com.zhiyicx.baseproject.widget.indicator_expand.ScaleCircleNavigator;
import com.zhiyicx.common.utils.DeviceUtils;
import com.zhiyicx.thinksnsplus.R;
import com.zhiyicx.thinksnsplus.data.beans.AnimationRectBean;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.UIUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * @author LiuChao
 * @describe
 * @date 2017/2/8
 * @contact email:450127106@qq.com
 */

public class GalleryFragment extends TSFragment {
    public static final String BUNDLE_IMAGS = "imags";
    public static final String BUNDLE_IMAGS_POSITON = "imags_positon";
    public static final String BUNDLE_NEED_START_LOADING = "start_loding";
    private static final int MAX_OFF_SIZE = 8;

    @BindView(R.id.vp_photos)
    ViewPager mVpPhotos;
    @BindView(R.id.mi_indicator)
    MagicIndicator mMiIndicator;


    private SectionsPagerAdapter mPagerAdapter;
    /**
     * 点击第几张图片进入的预览界面
     */
    private int currentItem = 0;
    private List<ImageBean> allImages;
    private boolean mNeedStartLoading = false;

    @Override
    protected int getBodyLayoutId() {
        return R.layout.fragment_gallery;
    }

    @Override
    protected boolean showToolbar() {
        return false;
    }

    @Override
    protected boolean setUseSatusbar() {
        return true;
    }

    @Override
    protected boolean setUseStatusView() {
        return false;
    }

    @Override
    protected void initView(View rootView) {
        mNeedStartLoading = getArguments().getBoolean(BUNDLE_NEED_START_LOADING);
        currentItem = getArguments().getInt(BUNDLE_IMAGS_POSITON);
        rectList = getArguments().getParcelableArrayList("rect");
        allImages = getArguments().getParcelableArrayList(BUNDLE_IMAGS);
        mPagerAdapter = new SectionsPagerAdapter(getChildFragmentManager());
        mVpPhotos.setAdapter(mPagerAdapter);
        mVpPhotos.setOffscreenPageLimit(MAX_OFF_SIZE);
        // 大于一张图片才会显示小圆点，否则隐藏
        if (allImages != null && allImages.size() > 1) {
            addCircleNavigator();
            mMiIndicator.onPageSelected(currentItem);
        }
        mMiIndicator.setVisibility(View.GONE);
        mVpPhotos.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageScrollStateChanged(int state) {
                viewPageState = state;
                // 刚拖动
                if (viewPageState == ViewPager.SCROLL_STATE_DRAGGING) {
                    // 获取到的是当前要退出的fragment
                    if (fragmentMap.get(mVpPhotos.getCurrentItem()) != null) {
                        fragmentMap.get(mVpPhotos.getCurrentItem()).showOrHideOriginBtn(false);
                    }
                }
                // 通过手指滑动切换到新的fragment，而不是第一次进入切换到fragment
                if (viewPageState == ViewPager.SCROLL_STATE_SETTLING || viewPageState == ViewPager.SCROLL_STATE_IDLE) {
                    // 获取到的是当前要进入的fragment
                    if (fragmentMap.get(mVpPhotos.getCurrentItem()) != null) {
                        fragmentMap.get(mVpPhotos.getCurrentItem()).showOrHideOriginBtn(true);
                    }
                }
            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                try {
                    if (position + 1 < allImages.size()) { // 提前加载前后图片
                        handlePreLoadData(mVpPhotos.getCurrentItem() + 1);
                    }
                    if (position - 1 >= 0) {
                        handlePreLoadData(mVpPhotos.getCurrentItem() - 1);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            /**
             *  处理预加载图片
             * @param key
             */
            private void handlePreLoadData(int key) {
                if (fragmentMap.get(key) != null) {
                    fragmentMap.get(key).preLoadData();
                }
            }
        });
        mVpPhotos.setCurrentItem(currentItem);
    }

    private int viewPageState = 0;


    @Override
    protected void initData() {

    }

    public static GalleryFragment initFragment(Bundle bundle) {
        GalleryFragment galleryFragment = new GalleryFragment();
        galleryFragment.setArguments(bundle);
        return galleryFragment;
    }

    ////////////////////////////////缩放动画//////////////////////////////////
    private SparseArray<GalleryPictureFragment> fragmentMap
            = new SparseArray<>();
    private boolean alreadyAnimateIn = false;
    private ArrayList<AnimationRectBean> rectList;

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentStatePagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            GalleryPictureFragment fragment = fragmentMap.get(position);
            if (fragment == null) {
                boolean animateIn = (currentItem == position) && !alreadyAnimateIn;
                allImages.get(position).setPosition(position);

                boolean isFirstLoadPage = currentItem == position || Math.abs(currentItem - position) == 1;
                fragment = GalleryPictureFragment
                        .newInstance(allImages.get(position), rectList.get(position), animateIn,
                                isFirstLoadPage, mNeedStartLoading);
                alreadyAnimateIn = true;
                fragmentMap.put(position, fragment);
            }

            return fragment;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return allImages.size();
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            super.setPrimaryItem(container, position, object);
            if (object instanceof Fragment) {
                fragmentMap.put(position, (GalleryPictureFragment) object);
            }
        }

    }


    /////////////////////////////////处理转场缩放动画/////////////////////////////////////
    private ColorDrawable backgroundColor = new ColorDrawable(Color.BLACK);

    @Override
    public void onBackPressed() {
        // 退出隐藏圆点指示器，防止显示在透明背景上
        mMiIndicator.setVisibility(View.INVISIBLE);
        GalleryPictureFragment fragment = fragmentMap.get(mVpPhotos.getCurrentItem());
        backgroundColor.setAlpha(0);
        fragment.animationExit();
    }

    private void addCircleNavigator() {
        // 添加指示器
        ScaleCircleNavigator circleNavigator = new ScaleCircleNavigator(getContext());
        circleNavigator.setCircleCount(mPagerAdapter.getCount());
        // 需要注意这两个值2.5 和2.1，他如果相同，就会出现不显示颜色的bug，另外在有些手机上，可能因为
        // 两个值太接近，也不显示颜色
        circleNavigator.setMaxRadius(UIUtil.dip2px(getContext(), 2.5));
        circleNavigator.setMinRadius(UIUtil.dip2px(getContext(), 2.1));
        circleNavigator.setCircleSpacing(UIUtil.dip2px(getContext(), 5));
        circleNavigator.setNormalCircleColor(Color.argb(102, 99, 99, 99));
        circleNavigator.setSelectedCircleColor(Color.argb(255, 99, 99, 99));
        circleNavigator.setFollowTouch(false);
        circleNavigator.setCircleClickListener(index -> mVpPhotos.setCurrentItem(index));
        mMiIndicator.setNavigator(circleNavigator);
        ViewPagerHelper.bind(mMiIndicator, mVpPhotos);
    }

    public void setIndiactorVisible(boolean visible) {
        mMiIndicator.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onDestroy() {
        DeviceUtils.gc();
        super.onDestroy();
    }
}
