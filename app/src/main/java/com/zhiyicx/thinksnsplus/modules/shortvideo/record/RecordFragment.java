package com.zhiyicx.thinksnsplus.modules.shortvideo.record;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jakewharton.rxbinding.view.RxView;
import com.tym.shortvideo.filter.helper.MagicFilterFactory;
import com.tym.shortvideo.filter.helper.type.GLType;
import com.tym.shortvideo.filter.helper.type.TextureRotationUtils;
import com.tym.shortvideo.recodrender.CaptureFrameCallback;
import com.tym.shortvideo.recodrender.ColorFilterManager;
import com.tym.shortvideo.recodrender.DrawerManager;
import com.tym.shortvideo.recodrender.ParamsManager;
import com.tym.shortvideo.recodrender.RecordManager;
import com.tym.shortvideo.recodrender.RenderManager;
import com.tym.shortvideo.recodrender.RenderStateChangedListener;
import com.tym.shortvideo.recordcore.CountDownManager;
import com.tym.shortvideo.recordcore.VideoListManager;
import com.tym.shortvideo.recordcore.multimedia.MediaEncoder;
import com.tym.shortvideo.utils.BitmapUtils;
import com.tym.shortvideo.utils.CameraUtils;
import com.tym.shortvideo.utils.FileUtils;
import com.tym.shortvideo.utils.StringUtils;
import com.tym.shortvideo.view.AspectFrameLayout;
import com.tym.shortvideo.view.AsyncRecyclerview;
import com.tym.shortvideo.view.CainSurfaceView;
import com.tym.shortvideo.view.ProgressView;
import com.tym.shortvideo.view.ShutterButton;
import com.zhiyicx.baseproject.base.TSFragment;
import com.zhiyicx.common.utils.log.LogUtils;
import com.zhiyicx.thinksnsplus.R;
import com.zhiyicx.thinksnsplus.modules.shortvideo.adapter.EffectFilterAdapter;
import com.zhiyicx.thinksnsplus.modules.shortvideo.preview.PreviewActivity;
import com.zhy.adapter.recyclerview.MultiItemTypeAdapter;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;

import static com.zhiyicx.common.config.ConstantConfig.JITTER_SPACING_TIME;

/**
 * @Author Jliuer
 * @Date 2018/03/28/10:52
 * @Email Jliuer@aliyun.com
 * @Description 录制
 */
public class RecordFragment extends TSFragment implements SurfaceHolder.Callback, CainSurfaceView.OnClickListener, CainSurfaceView.OnTouchScroller, RenderStateChangedListener,
        CaptureFrameCallback, ShutterButton.GestureListener {

    @BindView(R.id.layout_aspect)
    AspectFrameLayout mLayoutAspect;
    @BindView(R.id.tv_fps)
    TextView mTvFps;

    @BindView(R.id.btn_take)
    ShutterButton mBtnTake;
    @BindView(R.id.effect_list)
    AsyncRecyclerview mEffectList;
    @BindView(R.id.tym_test)
    ProgressView mTymTest;
    @BindView(R.id.tv_countdown)
    TextView mTvCountdown;

    @BindView(R.id.iv_back)
    ImageView mIvBack;
    @BindView(R.id.iv_left)
    ImageView mIvLeft;
    @BindView(R.id.iv_right)
    ImageView mIvRight;
    @BindView(R.id.tv_next_setup)
    TextView mTvNextSetUp;

    private CainSurfaceView mCameraSurfaceView;

    // 状态标志
    private boolean mOnPreviewing = false;
    private boolean mOnRecording = false;

    // 显示滤镜选择器
    private boolean isShowingEffect = false;
    private LinearLayoutManager mEffectManager;

    // 是否需要滚动
    private boolean mEffectNeedToMove = false;

    // 当前长宽比值
    private float mCurrentRatio;

    private int mColorIndex = 0;

    // 主线程Handler
    private Handler mMainHandler;

    // 对焦大小
    private static final int sFocusSize = 100;

    @Override
    protected boolean showToolBarDivider() {
        return false;
    }

    @Override
    protected boolean setUseSatusbar() {
        return false;
    }

    @Override
    protected boolean showToolbar() {
        return false;
    }

    @Override
    protected void initView(View rootView) {
        String phoneName = Build.MODEL;
        if (phoneName.toLowerCase().contains("bullhead")
                || phoneName.toLowerCase().contains("nexus 5x")) {
            TextureRotationUtils.setBackReverse(true);
            ParamsManager.mBackReverse = true;
        }
    }

    @Override
    protected void initData() {
        // 创建渲染线程
        DrawerManager.getInstance().createRenderThread(mActivity);
        // 添加渲染状态回调监听
        DrawerManager.getInstance().addRenderStateChangedListener(this);
        // 设置拍照回调
        DrawerManager.getInstance().setCaptureFrameCallback(this);
        mMainHandler = new Handler(mActivity.getMainLooper());

        mCurrentRatio = CameraUtils.getCurrentRatio();

        mLayoutAspect.setAspectRatio(mCurrentRatio);
        mCameraSurfaceView = new CainSurfaceView(mActivity);
        mCameraSurfaceView.getHolder().addCallback(this);
        mCameraSurfaceView.addClickListener(this);
        mLayoutAspect.addView(mCameraSurfaceView);
        mLayoutAspect.requestLayout();

        adjustBottomView();

        initEffectListView();
        setRecordType(2);
        initListener();
    }

    private void initListener() {

        RxView.clicks(mIvBack)
                .throttleFirst(JITTER_SPACING_TIME, TimeUnit.SECONDS)
                .compose(this.bindToLifecycle())
                .subscribe(aVoid -> mActivity.finish());

        RxView.clicks(mIvRight)
                .throttleFirst(JITTER_SPACING_TIME, TimeUnit.SECONDS)
                .compose(this.bindToLifecycle())
                .subscribe(aVoid -> switchCamera());

        RxView.clicks(mBtnTake)
                .throttleFirst(JITTER_SPACING_TIME, TimeUnit.SECONDS)
                .compose(this.bindToLifecycle())
                .subscribe(aVoid -> takePicture());
        mBtnTake.setGestureListener(this);

        RxView.clicks(mIvLeft)
                .throttleFirst(JITTER_SPACING_TIME, TimeUnit.SECONDS)
                .compose(this.bindToLifecycle())
                .subscribe(aVoid -> {
                            if (mTymTest.getSplitList().isEmpty()) {
                                switchBeauty();
                            } else {
                                deleteRecordedVideo(false);
                            }
                        }
                );

        RxView.clicks(mTvNextSetUp)
                .throttleFirst(JITTER_SPACING_TIME, TimeUnit.SECONDS)
                .compose(this.bindToLifecycle())
                .subscribe(aVoid -> {
                    if (CountDownManager.getInstance().getVisibleDuration() < CountDownManager.getInstance().getMinMilliSeconds()) {
                        showSnackErrorMessage("不得小于" + CountDownManager.getInstance().getMinMilliSeconds() / 1000 + "秒");
                        return;
                    }
                    previewRecordVideo();
                });
    }


    @Override
    public void onResume() {
        super.onResume();
        registerHomeReceiver();
        DrawerManager.getInstance().startPreview();
        if (isShowingEffect) {
            scrollToCurrentEffect();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        unRegisterHomeReceiver();
        DrawerManager.getInstance().stopPreview();
    }

    @Override
    public void onBackPressed() {
        if (isShowingEffect) {
            isShowingEffect = false;
            mEffectList.setVisibility(View.GONE);
            return;
        }
        mActivity.finish();
    }

    @Override
    public void onDestroyView() {
        DrawerManager.getInstance().destoryTrhead();
        // 在停止时需要释放上下文，防止内存泄漏
//        ParamsManager.context = null;
        super.onDestroyView();
    }

    @Override
    protected int getBodyLayoutId() {
        return R.layout.activity_camera;
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        DrawerManager.getInstance().surfaceCreated(holder);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        DrawerManager.getInstance().surfacrChanged(width, height);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        DrawerManager.getInstance().surfaceDestroyed();
    }

    @Override
    public void onPreviewing(boolean previewing) {
        mOnPreviewing = previewing;
        mBtnTake.setEnableOpenned(mOnPreviewing);
    }

    @Override
    public void onFrameCallback(ByteBuffer buffer, int width, int height) {
        mMainHandler.post(new Runnable() {
            @Override
            public void run() {
                String filePath = ParamsManager.ImagePath + "CainCamera_"
                        + System.currentTimeMillis() + ".jpeg";
                File file = new File(filePath);
                if (!file.getParentFile().exists()) {
                    file.getParentFile().mkdirs();
                }
                BufferedOutputStream bos = null;
                try {
                    bos = new BufferedOutputStream(new FileOutputStream(file));
                    Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                    bitmap.copyPixelsFromBuffer(buffer);
                    bitmap = BitmapUtils.rotateBitmap(bitmap, 180, true);
                    bitmap = BitmapUtils.flipBitmap(bitmap, true);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                    bitmap.recycle();
                    bitmap = null;
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } finally {
                    if (bos != null) {
                        try {
                            bos.close();
                        } catch (IOException e) {
                            // do nothing
                        }
                    }
                }
            }
        });
    }

    @Override
    public void swipeBack() {
        mColorIndex++;
        if (mColorIndex >= ColorFilterManager.getInstance().getColorFilterCount()) {
            mColorIndex = 0;
        }
        DrawerManager.getInstance()
                .changeFilterType(ColorFilterManager.getInstance().getColorFilterType(mColorIndex));
        scrollToCurrentEffect();
        LogUtils.d("changeFilter", "index = " + mColorIndex + ", filter name = "
                + ColorFilterManager.getInstance().getColorFilterName(mColorIndex));
    }

    @Override
    public void swipeFrontal() {
        mColorIndex--;
        if (mColorIndex < 0) {
            int count = ColorFilterManager.getInstance().getColorFilterCount();
            mColorIndex = count > 0 ? count - 1 : 0;
        }
        DrawerManager.getInstance()
                .changeFilterType(ColorFilterManager.getInstance().getColorFilterType(mColorIndex));

        scrollToCurrentEffect();
        LogUtils.d("changeFilter", "index = " + mColorIndex + ", filter name = "
                + ColorFilterManager.getInstance().getColorFilterName(mColorIndex));
    }

    @Override
    public void swipeUpper(boolean startInLeft) {

    }

    @Override
    public void swipeDown(boolean startInLeft) {

    }

    @Override
    public void onClick(float x, float y) {
        surfaceViewClick(x, y);
    }

    @Override
    public void doubleClick(float x, float y) {

    }

    @Override
    public void onStartRecord() {
        // 初始化录制线程
        RecordManager.getInstance().initThread();
        // 设置输出路径
        String path = ParamsManager.VideoPath
                + "Zhiyi_" + System.currentTimeMillis() + ".mp4";
        RecordManager.getInstance().setOutputPath(path);
        // 是否允许录音，只有录制视频才有音频
        RecordManager.getInstance().setEnableAudioRecording(ParamsManager.canRecordingAudio
                && ParamsManager.sMGLType == GLType.VIDEO);
        // 是否允许高清录制
        RecordManager.getInstance().enableHighDefinition(false);
        // 初始化录制器
        RecordManager.getInstance().initRecorder(RecordManager.RECORD_WIDTH,
                RecordManager.RECORD_HEIGHT, mEncoderListener);

        // 隐藏删除按钮
        if (ParamsManager.sMGLType == GLType.VIDEO) {
            mIvLeft.setVisibility(View.GONE);
            mIvRight.setVisibility(View.GONE);
        }
        // 初始化倒计时
        CountDownManager.getInstance().initCountDownTimer();
        CountDownManager.getInstance().setCountDownListener(mCountDownListener);
        mBtnTake.setProgressMax((int) CountDownManager.getInstance().getMaxMilliSeconds());
        mTymTest.setProgressMax((int) CountDownManager.getInstance().getMaxMilliSeconds());
        mTymTest.setProgressMin((int) CountDownManager.getInstance().getMinMilliSeconds());
        // 添加分割线
        mBtnTake.addSplitView();
        mBtnTake.setImageResource(R.mipmap.ico_video_recording);
        mTymTest.setDeleteMode(false);
        mTymTest.addSplitView();
    }

    @Override
    public void onStopRecord() {
        // 停止录制
        DrawerManager.getInstance().stopRecording();
        // 停止倒计时
        CountDownManager.getInstance().stopTimer();
        mBtnTake.setImageResource(R.mipmap.ico_video_record);
        mIvLeft.setImageResource(R.mipmap.ico_video_delete);
        mIvLeft.setVisibility(View.VISIBLE);
        mIvRight.setVisibility(View.VISIBLE);
    }

    @Override
    public void onProgressOver() {
        // 如果最后一秒内点击停止录制，则仅仅关闭录制按钮，因为前面已经停止过了，不做跳转
        // 如果最后一秒内没有停止录制，否则停止录制并跳转至预览页面
        if (CountDownManager.getInstance().isLastSecondStop()) {
            // 关闭录制按钮
            mBtnTake.closeButton();
        } else {
            previewRecordVideo();
        }
    }

    @Override
    public void onVideoReady() {

    }

    private void registerHomeReceiver() {
        IntentFilter homeFilter = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        mActivity.registerReceiver(mHomePressReceiver, homeFilter);
    }

    private void unRegisterHomeReceiver() {
        mActivity.unregisterReceiver(mHomePressReceiver);
    }

    /**
     * 切换相机
     */
    private void switchCamera() {
        if (mCameraSurfaceView != null) {
            DrawerManager.getInstance().switchCamera();
        }
    }

    private void switchBeauty() {
        if (RenderManager.getInstance().getBeautiLevel() > 0) {
            DrawerManager.getInstance().setBeautifyLevel(0);
        } else {
            DrawerManager.getInstance().setBeautifyLevel(100);
        }
        setLeftImage(RenderManager.getInstance().getBeautiLevel() > 0);
    }

    private void setLeftImage(boolean beautyOn) {
        mIvLeft.setImageResource(beautyOn ? R.mipmap.ico_video_beauty_on : R.mipmap.ico_video_beauty_close);
    }

    /**
     * 显示滤镜
     */
    private void showFilters() {
        isShowingEffect = true;
        if (mEffectList != null) {
            mEffectList.setVisibility(View.VISIBLE);
            scrollToCurrentEffect();
        }
    }

    /**
     * 调整底部视图
     */
    private void adjustBottomView() {
        if (CameraUtils.getCurrentRatio() < CameraUtils.Ratio_4_3) {

        } else {

        }
    }

    /**
     * 初始化滤镜显示
     */
    private void initEffectListView() {
        // 初始化滤镜图片
        mEffectList.setVisibility(View.GONE);
        mEffectManager = new LinearLayoutManager(mActivity);
        mEffectManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mEffectList.setLayoutManager(mEffectManager);

        EffectFilterAdapter adapter = new EffectFilterAdapter(mActivity, R.layout.item_effect_view,
                MagicFilterFactory.getInstance().getFilterTypes());

        mEffectList.setAdapter(adapter);
        adapter.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {
                mColorIndex = position;
                DrawerManager.getInstance().changeFilterType(
                        ColorFilterManager.getInstance().getColorFilterType(position));
                LogUtils.d("changeFilter", "index = " + mColorIndex + ", filter name = "
                        + ColorFilterManager.getInstance().getColorFilterName(mColorIndex));
            }

            @Override
            public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
                return false;
            }
        });
    }

    public void setRecordType(int currentIndex) {
        if (currentIndex == 0) {
            ParamsManager.sMGLType = GLType.GIF;
            // TODO GIF录制后面再做处理
            mBtnTake.setIsRecorder(true);
        } else if (currentIndex == 1) {
            ParamsManager.sMGLType = GLType.PICTURE;
            // 拍照状态
            mBtnTake.setIsRecorder(false);
        } else if (currentIndex == 2) {
            ParamsManager.sMGLType = GLType.VIDEO;
            // 录制视频状态
            mBtnTake.setIsRecorder(true);
        }

        // 显示时间
        if (currentIndex == 2) {
            mTvCountdown.setVisibility(View.VISIBLE);
        } else {
            mTvCountdown.setVisibility(View.GONE);
        }
    }

    /**
     * 滚动到当前位置
     */
    private void scrollToCurrentEffect() {
        if (isShowingEffect) {
            int firstItem = mEffectManager.findFirstVisibleItemPosition();
            int lastItem = mEffectManager.findLastVisibleItemPosition();
            if (mColorIndex <= firstItem) {
                mEffectList.scrollToPosition(mColorIndex);
            } else if (mColorIndex <= lastItem) {
                int top = mEffectList.getChildAt(mColorIndex - firstItem).getTop();
                mEffectList.scrollBy(0, top);
            } else {
                mEffectList.scrollToPosition(mColorIndex);
                mEffectNeedToMove = true;
            }
        }
    }

    /**
     * 点击SurfaceView
     *
     * @param x x轴坐标
     * @param y y轴坐标
     */
    private void surfaceViewClick(float x, float y) {
        if (isShowingEffect) {
            isShowingEffect = false;
            mEffectList.setVisibility(View.GONE);
        }
        // 设置聚焦区域
        DrawerManager.getInstance().setFocusAres(CameraUtils.getFocusArea((int) x, (int) y,
                mCameraSurfaceView.getWidth(), mCameraSurfaceView.getHeight(), sFocusSize));
    }

    /**
     * 拍照
     */
    private void takePicture() {
        if (!mOnPreviewing) {
            return;
        }
        DrawerManager.getInstance().takePicture();
    }

    /**
     * 等待录制完成再预览录制视频
     */
    private void previewRecordVideo() {
        if (mOnRecording) {
            mNeedToWaitStop = true;
            // 停止录制
            DrawerManager.getInstance().stopRecording();
        } else {
            // 销毁录制线程
            RecordManager.getInstance().destoryThread();
            mNeedToWaitStop = false;
            // 隐藏删除和预览按钮
            ArrayList<String> arrayList = new ArrayList<>(VideoListManager.getInstance()
                    .getSubVideoPathList());
            PreviewActivity.startPreviewActivity(mActivity, arrayList);
            mActivity.finish();
        }
    }

    /**
     * 删除录制的视频
     */
    synchronized private void deleteRecordedVideo(boolean clearAll) {
        // 处于删除模式，则删除文件
        if (mBtnTake.isDeleteMode()) {

            // 删除视频，判断是否清除所有
            if (clearAll) {
                // 清除所有分割线
                mBtnTake.cleanSplitView();
                mTymTest.cleanSplitView();
                VideoListManager.getInstance().removeAllSubVideo();
            } else {
                // 删除分割线
                mBtnTake.deleteSplitView();
                mTymTest.deleteSplitView();
                VideoListManager.getInstance().removeLastSubVideo();
            }
            // 重置计时器记录走过的时长
            CountDownManager.getInstance().resetDuration();
            // 重置最后一秒点击标志
            CountDownManager.getInstance().resetLastSecondStop();
            // 更新进度
            mBtnTake.setProgress(CountDownManager.getInstance().getVisibleDuration());
            mTymTest.setProgress(CountDownManager.getInstance().getVisibleDuration());
            // 更新时间
            mTvCountdown.setText(CountDownManager.getInstance().getVisibleDurationString());
            // 如果此时没有了视频，则隐藏删除按钮
            if (VideoListManager.getInstance().getSubVideoList().size() <= 0) {

                setLeftImage(RenderManager.getInstance().getBeautiLevel() > 0);

                // 复位状态
                mNeedToWaitStop = false;
                mOnRecording = false;
            }

        } else { // 没有进入删除模式则进入删除模式
            mBtnTake.setDeleteMode(true);
            mTymTest.setDeleteMode(true);
        }
    }

    /**
     * 监听点击home键
     */
    private BroadcastReceiver mHomePressReceiver = new BroadcastReceiver() {
        private final String SYSTEM_DIALOG_REASON_KEY = "reason";
        private final String SYSTEM_DIALOG_REASON_HOME_KEY = "homekey";

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
                String reason = intent.getStringExtra(SYSTEM_DIALOG_REASON_KEY);
                if (TextUtils.isEmpty(reason)) {
                    return;
                }
                // 当点击了home键时需要停止预览，防止后台一直持有相机
                if (reason.equals(SYSTEM_DIALOG_REASON_HOME_KEY)) {
                    // 停止录制
                    if (mOnRecording) {
                        // 停止录制
                        RecordManager.getInstance().stopRecording();
                        // 取消倒计时
                        CountDownManager.getInstance().cancelTimerWithoutSaving();
                        // 重置进入条
                        mBtnTake.setProgress((int) CountDownManager.getInstance().getVisibleDuration());
                        mTymTest.setProgress((int) CountDownManager.getInstance().getVisibleDuration());
                        // 删除分割线
                        mBtnTake.deleteSplitView();
                        mTymTest.deleteSplitView();
                        // 关闭按钮
                        mBtnTake.closeButton();
                        // 更新时间
                        mTvCountdown.setText(CountDownManager.getInstance().getVisibleDurationString());
                    }
                    if (mOnPreviewing) {
                        DrawerManager.getInstance().stopPreview();
                    }
                }
            }
        }
    };

    // MediaEncoder准备好的数量
    private int mPreparedCount = 0;

    // 开始MediaEncoder的数量
    private int mStartedCount = 0;

    // 释放MediaEncoder的数量
    private int mReleaseCount = 0;

    // 是否需要等待录制完成再跳转
    private boolean mNeedToWaitStop;
    /**
     * 录制监听器
     */
    private MediaEncoder.MediaEncoderListener
            mEncoderListener = new MediaEncoder.MediaEncoderListener() {

        @Override
        public void onPrepared(MediaEncoder encoder) {
            mPreparedCount++;
            // 没有录音权限、不允许音频录制、允许录制音频并且准备好两个MediaEncoder，就可以开始录制了
            if (!ParamsManager.canRecordingAudio
                    || (ParamsManager.canRecordingAudio && mPreparedCount == 2)
                    || ParamsManager.sMGLType == GLType.GIF) { // 录制GIF，没有音频
                // 准备完成，开始录制
                DrawerManager.getInstance().startRecording();

                // 重置
                mPreparedCount = 0;
            }
        }

        @Override
        public void onStarted(MediaEncoder encoder) {
            mStartedCount++;
            // 没有录音权限、不允许音频录制、允许录制音频并且开始了两个MediaEncoder，就处于录制状态了
            if (!ParamsManager.canRecordingAudio
                    || (ParamsManager.canRecordingAudio && mStartedCount == 2)
                    || ParamsManager.sMGLType == GLType.GIF) { // 录制GIF，没有音频
                mOnRecording = true;

                // 重置状态
                mStartedCount = 0;

                // 编码器已经进入录制状态，则快门按钮可用
                mBtnTake.setEnableEncoder(true);

                // 开始倒计时
                CountDownManager.getInstance().startTimer();
            }
        }

        @Override
        public void onStopped(MediaEncoder encoder) {
        }

        @Override
        public void onReleased(MediaEncoder encoder) { // 复用器释放完成
            mReleaseCount++;
            // 没有录音权限、不允许音频录制、允许录制音频并且释放了两个MediaEncoder，就完全释放掉了
            if (!ParamsManager.canRecordingAudio
                    || (ParamsManager.canRecordingAudio && mReleaseCount == 2)
                    || ParamsManager.sMGLType == GLType.GIF) { // 录制GIF，没有音频
                // 录制完成跳转预览页面
                String outputPath = RecordManager.getInstance().getOutputPath();
                // 添加分段视频，存在时长为0的情况，也就是取消倒计时但不保存时长的情况
                if (CountDownManager.getInstance().getRealDuration() > 0) {
                    VideoListManager.getInstance().addSubVideo(outputPath,
                            (int) CountDownManager.getInstance().getRealDuration());
                } else {    // 移除多余的视频
                    FileUtils.deleteFile(outputPath);
                }
                // 重置当前走过的时长
                CountDownManager.getInstance().resetDuration();

                // 处于非录制状态
                mOnRecording = false;

                // 显示删除按钮
                if (ParamsManager.sMGLType == GLType.VIDEO) {
                    mActivity.runOnUiThread(() -> {
                        mIvLeft.setVisibility(View.VISIBLE);
                        mIvRight.setVisibility(View.VISIBLE);
                    });
                }

                // 处于录制状态点击了预览按钮，则需要等待完成再跳转， 或者是处于录制GIF状态
                if (mNeedToWaitStop || ParamsManager.sMGLType == GLType.GIF) {
                    mActivity.runOnUiThread(() -> {
                        // 重置按钮状态
                        // 开始预览
                        previewRecordVideo();
                    });
                }
                // 重置释放状态
                mReleaseCount = 0;

                // 编码器已经完全释放，则快门按钮可用
                mBtnTake.setEnableEncoder(true);

            }

        }
    };

    private CountDownManager.CountDownListener
            mCountDownListener = new CountDownManager.CountDownListener() {
        @Override
        public void onProgressChanged(long duration) {
            // 设置进度
            mBtnTake.setProgress(duration);
            mTymTest.setProgress(duration);
            // 设置时间
            mTvCountdown.setText(StringUtils.generateMillisTime((int) duration));
        }
    };
}
