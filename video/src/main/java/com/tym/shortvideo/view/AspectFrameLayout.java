package com.tym.shortvideo.view;

import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.tym.shortvideo.utils.CameraUtils;


/**
 * @author Jliuer
 * @Date 18/04/28 9:47
 * @Email Jliuer@aliyun.com
 * @Description 录制预览 surfaceview container，重写了onMeasure
 */
public class AspectFrameLayout extends FrameLayout {

    // 宽高比
    private double mTargetAspect = -1.0;

    public AspectFrameLayout(@NonNull Context context) {
        super(context);
    }

    public AspectFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public AspectFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setAspectRatio(CameraUtils.Ratio ratio) {
        double aspectRatio = ratio.getRatio();
        if (aspectRatio < 0) {
            throw new IllegalArgumentException("ratio < 0");
        }
        if (ratio == CameraUtils.Ratio.RATIO_4_3_2_1_1 || ratio == CameraUtils.Ratio.RATIO_16_9_2_1_1) {
            mTargetAspect = 1;
        } else {
            mTargetAspect = aspectRatio;
        }
        requestLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mTargetAspect == 1) {
            super.onMeasure(widthMeasureSpec, widthMeasureSpec);
        } else if (mTargetAspect > 0) {
            int initialWidth = MeasureSpec.getSize(widthMeasureSpec);
            int initialHeight = MeasureSpec.getSize(heightMeasureSpec);

            int horizPadding = getPaddingLeft() + getPaddingRight();
            int vertPadding = getPaddingTop() + getPaddingBottom();

            initialWidth -= horizPadding;
            initialHeight -= vertPadding;

            double viewAspectRatio = (double) initialWidth / initialHeight;
            double aspectDiff = mTargetAspect / viewAspectRatio - 1;

            if (Math.abs(aspectDiff) >= 0.01) {
                initialHeight = (int) (initialWidth / mTargetAspect);
            }
            initialWidth += horizPadding;
            initialHeight += vertPadding;
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(initialWidth, MeasureSpec.EXACTLY);
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(initialHeight, MeasureSpec.EXACTLY);
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }

    }
}
