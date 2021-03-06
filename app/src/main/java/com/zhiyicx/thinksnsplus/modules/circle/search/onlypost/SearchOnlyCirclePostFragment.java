package com.zhiyicx.thinksnsplus.modules.circle.search.onlypost;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.EditorInfo;

import com.jakewharton.rxbinding.widget.RxTextView;
import com.zhiyicx.baseproject.widget.edittext.DeleteEditText;
import com.zhiyicx.baseproject.widget.popwindow.ActionPopupWindow;
import com.zhiyicx.common.utils.DeviceUtils;
import com.zhiyicx.common.widget.popwindow.CustomPopupWindow;
import com.zhiyicx.thinksnsplus.R;
import com.zhiyicx.thinksnsplus.data.beans.CircleInfo;
import com.zhiyicx.thinksnsplus.data.beans.CircleJoinedBean;
import com.zhiyicx.thinksnsplus.data.beans.CircleMembers;
import com.zhiyicx.thinksnsplus.data.source.repository.BaseCircleRepository;
import com.zhiyicx.thinksnsplus.modules.circle.search.SearchCirclePostFragment;
import com.zhiyicx.thinksnsplus.modules.markdown_editor.BaseMarkdownActivity;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @Describe
 * @Author Jungle68
 * @Date 2017/12/6
 * @Contact master.jungle68@gmail.com
 */
public class SearchOnlyCirclePostFragment extends SearchCirclePostFragment {
    @BindView(R.id.fragment_info_search_edittext)
    DeleteEditText mFragmentInfoSearchEdittext;
    @BindView(R.id.fragment_search_cancle)
    View mFragmentSearchCancle;

    /**
     * 权限说明提示框
     */
    private ActionPopupWindow mAuditTipPop;

    public static SearchOnlyCirclePostFragment newInstance(BaseCircleRepository.CircleMinePostType circleMinePostType, CircleInfo circleInfo) {
        SearchOnlyCirclePostFragment circleDetailFragment = new SearchOnlyCirclePostFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(CIRCLE_TYPE, circleMinePostType);
        bundle.putLong(CIRCLE_ID, circleInfo.getId());
        bundle.putSerializable(CIRCLE, circleInfo);
        circleDetailFragment.setArguments(bundle);
        return circleDetailFragment;
    }

    @Override
    protected void initView(View rootView) {
        super.initView(rootView);
        mFragmentInfoSearchEdittext.setHint(getString(R.string.info_search));
        RxTextView.editorActionEvents(mFragmentInfoSearchEdittext).subscribe(textViewEditorActionEvent -> {
            if (textViewEditorActionEvent.actionId() == EditorInfo.IME_ACTION_SEARCH) {
                if (!TextUtils.isEmpty(mFragmentInfoSearchEdittext.getText().toString())) {
                    onEditChanged(mFragmentInfoSearchEdittext.getText().toString());
                    DeviceUtils.hideSoftKeyboard(getContext(), mFragmentInfoSearchEdittext);
                }
            }
        });
    }

    @Override
    protected boolean setUseStatusView() {
        return true;
    }

    @Override
    protected boolean needMusicWindowView() {
        return true;
    }

    @Override
    public boolean isOutsideSerach() {
        return false;
    }

    @Override
    protected View getRightViewOfMusicWindow() {
        return mFragmentSearchCancle;
    }

    @Override
    protected int getBodyLayoutId() {
        return R.layout.fragment_circle_search_only_post_list;
    }

    @Override
    protected boolean showPostFrom() {
        return false;
    }

    @OnClick({R.id.fragment_search_cancle, R.id.bt_do})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.fragment_search_cancle:
                getActivity().finish();
                break;
            case R.id.bt_do:
                if (getArguments() != null && getArguments().getSerializable(CIRCLE) != null) {
                    CircleInfo mCircleInfo = (CircleInfo) getArguments().getSerializable(CIRCLE);
                    if (mCircleInfo != null) {
                        if (mCircleInfo.getJoined() == null) {
                            showAuditTipPopupWindow(getString(R.string.please_join_circle_first));
                            // 已经申请了加入，但被拒绝了
                        } else if (mCircleInfo.getJoined().getAudit() == CircleJoinedBean.AuditStatus.REJECTED.value) {
                            showAuditTipPopupWindow(getString(R.string.circle_join_rejected));
                            // 已经申请了加入，审核中
                        } else if (mCircleInfo.getJoined().getAudit() == CircleJoinedBean.AuditStatus.REVIEWING.value) {
                            showAuditTipPopupWindow(getString(R.string.circle_join_reviewing));
                            // 通过了
                        } else {
                            // 当前角色可用
                            if (mCircleInfo.getPermissions().contains(mCircleInfo.getJoined().getRole())) {
                                if (mCircleInfo.getJoined()
                                        .getDisabled() == CircleJoinedBean.DisableStatus.NORMAL.value) {
                                    BaseMarkdownActivity.startActivityForPublishPostInCircle(mActivity, mCircleInfo);
                                }
                            } else if (CircleMembers.BLACKLIST.equals(mCircleInfo.getJoined().getRole())) {
                                //被拉到了黑名单
                                showAuditTipPopupWindow(getString(R.string.circle_member_added_blacklist));
                            } else {
                                // 没有权限发帖
                                if (mCircleInfo.getPermissions().contains(CircleMembers.ADMINISTRATOR)) {
                                    showAuditTipPopupWindow(getString(R.string.publish_circle_post_format, mCircleInfo.getName(), getString(R
                                            .string.circle_master_manager)));
                                } else {
                                    showAuditTipPopupWindow(getString(R.string.publish_circle_post_format, mCircleInfo.getName(), getString(R
                                            .string.circle_master)));
                                }

                            }
                        }
                        return;
                    }
                    BaseMarkdownActivity.startActivityForPublishPostInCircle(mActivity, (CircleInfo) getArguments().getSerializable(CIRCLE));
                } else {
                    BaseMarkdownActivity.startActivityForPublishPostOutCircle(mActivity);

                }
                // 创建圈子帖子
                break;
            default:
        }
    }

    /**
     * 权限说明提示弹框
     */
    private void showAuditTipPopupWindow(String tipStr) {
        mAuditTipPop = ActionPopupWindow.builder()
                .item1Str(getString(R.string.info_publish_hint))
                .desStr(tipStr)
                .bottomStr(getString(R.string.i_know))
                .isOutsideTouch(true)
                .isFocus(true)
                .backgroundAlpha(CustomPopupWindow.POPUPWINDOW_ALPHA)
                .with(getActivity())
                .bottomClickListener(() -> mAuditTipPop.hide()).build();
        mAuditTipPop.show();
    }

}
