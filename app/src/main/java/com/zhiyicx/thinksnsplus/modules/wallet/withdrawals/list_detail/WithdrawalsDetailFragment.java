package com.zhiyicx.thinksnsplus.modules.wallet.withdrawals.list_detail;

import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.zhiyicx.baseproject.base.TSListFragment;
import com.zhiyicx.common.utils.recycleviewdecoration.CustomLinearDecoration;
import com.zhiyicx.thinksnsplus.R;
import com.zhiyicx.thinksnsplus.data.beans.WithdrawalsListBean;
import com.zhiyicx.thinksnsplus.modules.wallet.bill_detail.BillDetailActivity;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.MultiItemTypeAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

/**
 * @Author Jliuer
 * @Date 2017/05/23/10:56
 * @Email Jliuer@aliyun.com
 * @Description
 */
public class WithdrawalsDetailFragment extends TSListFragment<WithdrawalsDetailConstract.Presenter, WithdrawalsListBean>
        implements WithdrawalsDetailConstract.View {

    public static WithdrawalsDetailFragment newInstance() {
        return new WithdrawalsDetailFragment();
    }

    @Override
    protected int getBodyLayoutId() {
        return R.layout.fragment_withdrawals_detail;
    }

    @Override
    protected String setCenterTitle() {
        return getString(R.string.withdraw_details);
    }

    @Override
    protected boolean showToolBarDivider() {
        return true;
    }

    @Override
    protected RecyclerView.Adapter getAdapter() {
        CommonAdapter adapter = new CommonAdapter<WithdrawalsListBean>(getActivity(), R.layout.item_withdrawals_detail, mListDatas) {
            @Override
            protected void convert(ViewHolder holder, WithdrawalsListBean s, int position) {
                TextView desc = holder.getView(R.id.withdrawals_desc);
                if (position % 2 == 0) {
                    desc.setEnabled(false);
                }
            }
        };
        adapter.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {
                startActivity(new Intent(getActivity(), BillDetailActivity.class));
            }

            @Override
            public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
                return false;
            }
        });
        return adapter;
    }

    @Override
    protected RecyclerView.ItemDecoration getItemDecoration() {
        return new CustomLinearDecoration(0, getResources().getDimensionPixelSize(R.dimen
                .divider_line), 0, 0, ContextCompat.getDrawable(getContext(), R.drawable
                .shape_recyclerview_grey_divider));
    }
}
