package com.zhiyicx.baseproject.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.zhiyicx.baseproject.R;

/**
 * @Describe
 * @Author Jungle68
 * @Date 2017/1/12
 * @Contact master.jungle68@gmail.com
 */

public class InputLimitView extends FrameLayout {

    protected TextView mTvLimitTip;
    protected Button mBtSend;
    protected EditText mEtContent;

    private int mLimitMaxSize;// 最大输入值
    private int mshowLimitSize;// 当输入值达到 mshowLimitSize 时，显示提示

    private OnSendClickListener mOnSendClickListener;

    public void setTvLimitTip(TextView tvLimitTip) {
        mTvLimitTip = tvLimitTip;
    }

    public InputLimitView(Context context) {
        super(context);
        init(context, null);
    }

    public InputLimitView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public InputLimitView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        LayoutInflater.from(context).inflate(R.layout.view_input_limit_viewgroup, this);
        mTvLimitTip = (TextView) findViewById(R.id.tv_limit_tip);
        mBtSend = (Button) findViewById(R.id.bt_send);
        mEtContent = (EditText) findViewById(R.id.et_content);
        if (attrs != null) {
            TypedArray array = context.obtainStyledAttributes(attrs,
                    R.styleable.inputLimitView);
            mLimitMaxSize = (int) array.getDimension(R.styleable.inputLimitView_limitSize, context.getResources().getInteger(R.integer.comment_input_max_size));
            mshowLimitSize = (int) array.getDimension(R.styleable.inputLimitView_showLimitSize, context.getResources().getInteger(R.integer.show_comment_input_size));
            array.recycle();
        } else {
            mLimitMaxSize = context.getResources().getInteger(R.integer.comment_input_max_size);
            mshowLimitSize = context.getResources().getInteger(R.integer.show_comment_input_size);
        }
        mEtContent.setFilters(new InputFilter[]{new InputFilter.LengthFilter(mLimitMaxSize)});
        mEtContent.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (mOnSendClickListener == null) {
                    return handled;
                }
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    mOnSendClickListener.onSendClick();
                    handled = true;
                }
                return handled;
            }
        });

        mEtContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() >= mshowLimitSize) {
                    mTvLimitTip.setVisibility(VISIBLE);
                } else {
                    mTvLimitTip.setVisibility(GONE);
                }
            }
        });

        mBtSend.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnSendClickListener != null) {
                    mOnSendClickListener.onSendClick();
                }
            }
        });
    }

    /**
     * 获取输入内容
     *
     * @return 当前输入内容，去掉前后空格
     */
    public String getInputContent() {
        return mEtContent.getText().toString().trim();
    }

    interface OnSendClickListener {
        void onSendClick();

    }
}
