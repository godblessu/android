package com.zhiyicx.thinksnsplus.modules.information.infodetails.topinfo_comment;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxbinding.widget.RxTextView;
import com.trycatch.mysnackbar.Prompt;
import com.zhiyicx.baseproject.base.TSFragment;
import com.zhiyicx.baseproject.widget.popwindow.ActionPopupWindow;
import com.zhiyicx.common.widget.popwindow.CustomPopupWindow;
import com.zhiyicx.thinksnsplus.R;
import com.zhiyicx.thinksnsplus.modules.wallet.WalletActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;

import static com.zhiyicx.common.config.ConstantConfig.JITTER_SPACING_TIME;

/**
 * @Author Jliuer
 * @Date 2017/06/01/17:12
 * @Email Jliuer@aliyun.com
 * @Description
 */
public class InfoCommentTopFragment extends TSFragment<InfoCommentTopContract.Presenter>
        implements InfoCommentTopContract.View {

    public static final String TOP_INFO_COMMENT_ID = "top_info_comment_id";
    public static final String TOP_INFO_ID = "top_info_id";

    @BindView(R.id.rb_one)
    RadioButton mRbOne;
    @BindView(R.id.rb_two)
    RadioButton mRbTwo;
    @BindView(R.id.rb_three)
    RadioButton mRbThree;
    @BindView(R.id.et_top_input)
    EditText mEtTopInput;
    @BindView(R.id.et_top_total)
    EditText mEtTopTotal;
    @BindView(R.id.bt_top)
    TextView mBtTop;
    @BindView(R.id.tv_dynamic_top_dec)
    TextView mTvDynamicTopDec;
    @BindView(R.id.rb_days_group)
    RadioGroup mRbDaysGroup;

    private List<Integer> mSelectDays;
    private int mCurrentDays;
    private double mInputMoney;
    private ActionPopupWindow mStickTopInstructionsPopupWindow;

    public static InfoCommentTopFragment newInstance(long news_id, long comment_id) {
        InfoCommentTopFragment infoCommentTopFragment = new InfoCommentTopFragment();
        Bundle bundle = new Bundle();
        bundle.putLong(TOP_INFO_ID, news_id);
        bundle.putLong(TOP_INFO_COMMENT_ID, comment_id);
        infoCommentTopFragment.setArguments(bundle);
        return infoCommentTopFragment;
    }

    @Override
    protected String setCenterTitle() {
        return getString(R.string.to_top);
    }

    @Override
    protected boolean showToolBarDivider() {
        return true;
    }

    @Override
    protected void initView(View rootView) {
        mSelectDays = new ArrayList<>();
        mSelectDays.add(1);
        mSelectDays.add(5);
        mSelectDays.add(10);
        initSelectDays(mSelectDays);
        initListener();
    }

    @Override
    protected void initData() {
        mTvDynamicTopDec.setText(String.format(getString(R.string.to_top_description), 200f, mPresenter.getBalance()));
    }

    @Override
    public boolean insufficientBalance() {
        return mPresenter.getBalance() < mCurrentDays * mInputMoney;
    }

    @Override
    public void gotoRecharge() {
        startActivity(new Intent(getActivity(), WalletActivity.class));
    }

    @Override
    protected int getBodyLayoutId() {
        return R.layout.fragment_dynamic_top;
    }

    @Override
    public double getInputMoney() {
        return mInputMoney;
    }

    @Override
    public int getTopDyas() {
        return mCurrentDays;
    }

    @Override
    public void topSuccess() {

    }

    @Override
    protected void snackViewDismissWhenTimeOut(Prompt prompt) {
        super.snackViewDismissWhenTimeOut(prompt);
        if (prompt == Prompt.SUCCESS) {
            getActivity().finish();
        }
    }

    private void initListener() {
        mRbDaysGroup.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.rb_one:
                    mCurrentDays = mSelectDays.get(0);
                    break;
                case R.id.rb_two:
                    mCurrentDays = mSelectDays.get(1);
                    break;
                case R.id.rb_three:
                    mCurrentDays = mSelectDays.get(2);
                    break;
            }
            setConfirmEnable();
        });

        RxTextView.textChanges(mEtTopInput)
                .compose(this.bindToLifecycle())
                .subscribe(charSequence -> {
                    if (!TextUtils.isEmpty(charSequence)) {
                        mInputMoney = Double.parseDouble(charSequence.toString());
                    } else {
                        mInputMoney = 0d;
                    }
                    setConfirmEnable();
                }, throwable -> mInputMoney = 0d);

        RxTextView.textChanges(mEtTopTotal)
                .compose(this.bindToLifecycle())
                .subscribe(charSequence -> mBtTop.setText(getString(mPresenter.getBalance() < mCurrentDays * mInputMoney
                        ? R.string.to_recharge : R.string.sure)));

        RxView.clicks(mBtTop)
                .throttleFirst(JITTER_SPACING_TIME, TimeUnit.SECONDS)
                .compose(this.bindToLifecycle())
                .subscribe(aVoid -> mPresenter.topInfoComment(getArguments().getLong(TOP_INFO_ID),
                        getArguments().getLong(TOP_INFO_COMMENT_ID),mInputMoney, mCurrentDays));
    }

    private void initSelectDays(List<Integer> mSelectDays) {
        mRbOne.setText(String.format(getString(R.string.select_day), mSelectDays.get(0)));
        mRbTwo.setText(String.format(getString(R.string.select_day), mSelectDays.get(1)));
        mRbThree.setText(String.format(getString(R.string.select_day), mSelectDays.get(2)));
    }

    private void setConfirmEnable() {
        boolean enable = mCurrentDays > 0 && mInputMoney > 0;
        mBtTop.setEnabled(enable);
        if (!enable)
            return;
        mEtTopTotal.setText(String.valueOf(mCurrentDays * mInputMoney));
    }

    @Override
    public void initStickTopInstructionsPop() {
        if (mStickTopInstructionsPopupWindow != null) {
            mStickTopInstructionsPopupWindow.show();
            return;
        }
        mStickTopInstructionsPopupWindow = ActionPopupWindow.builder()
                .item1Str(getString(R.string.sticktop_instructions))
                .desStr(getString(R.string.sticktop_instructions_detail))
                .bottomStr(getString(R.string.cancel))
                .isOutsideTouch(true)
                .isFocus(true)
                .backgroundAlpha(CustomPopupWindow.POPUPWINDOW_ALPHA)
                .with(getActivity())
                .bottomClickListener(() -> mStickTopInstructionsPopupWindow.hide())
                .build();
        mStickTopInstructionsPopupWindow.show();
    }
}
