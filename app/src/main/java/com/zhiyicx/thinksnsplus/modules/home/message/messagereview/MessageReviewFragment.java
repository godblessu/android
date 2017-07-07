package com.zhiyicx.thinksnsplus.modules.home.message.messagereview;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.zhiyicx.baseproject.base.TSListFragment;
import com.zhiyicx.common.utils.recycleviewdecoration.CustomLinearDecoration;
import com.zhiyicx.thinksnsplus.R;
import com.zhiyicx.thinksnsplus.data.beans.DigedBean;
import com.zhiyicx.thinksnsplus.data.beans.TopDynamicCommentBean;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.MultiItemTypeAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

/**
 * @Author Jliuer
 * @Date 2017/7/5/20:55
 * @Email Jliuer@aliyun.com
 * @Description
 */
public class MessageReviewFragment extends TSListFragment<MessageReviewContract.Presenter,
        TopDynamicCommentBean> implements MessageReviewContract.View,MultiItemTypeAdapter.OnItemClickListener {

    public MessageReviewFragment() {
    }

    public static MessageReviewFragment newInstance() {
        MessageReviewFragment fragment = new MessageReviewFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected String setCenterTitle() {
        return getString(R.string.review);
    }

    @Override
    protected void initView(View rootView) {
        super.initView(rootView);
    }

    @Override
    protected RecyclerView.ItemDecoration getItemDecoration() {
        return new CustomLinearDecoration(0, 1, 0, 0, ContextCompat.getDrawable(getContext(), R.drawable.shape_recyclerview_divider));
    }

    @Override
    protected boolean isNeedRefreshDataWhenComeIn() {
        return true;
    }

    @Override
    protected boolean isNeedRefreshAnimation() {
        return false;
    }

    @Override
    protected void initData() {
        super.initData();
    }

    @Override
    protected CommonAdapter<TopDynamicCommentBean> getAdapter() {
        CommonAdapter<TopDynamicCommentBean> adapter=new CommonAdapter<TopDynamicCommentBean>
                (getContext(),R.layout.item_message_review_list,mListDatas) {
            @Override
            protected void convert(ViewHolder holder, TopDynamicCommentBean
                    topDynamicCommentBean, int position) {

            }
        };
        adapter.setOnItemClickListener(this);
        return adapter;
    }

    @Override
    protected int setEmptView() {
        return R.mipmap.img_default_nothing;
    }

    @Override
    public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
        return false;
    }
}