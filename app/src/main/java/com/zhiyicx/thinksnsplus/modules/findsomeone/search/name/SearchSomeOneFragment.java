package com.zhiyicx.thinksnsplus.modules.findsomeone.search.name;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zhiyicx.baseproject.base.TSListFragment;
import com.zhiyicx.baseproject.widget.edittext.DeleteEditText;
import com.zhiyicx.common.utils.ConvertUtils;
import com.zhiyicx.common.utils.DeviceUtils;
import com.zhiyicx.thinksnsplus.R;
import com.zhiyicx.thinksnsplus.data.beans.LocationBean;
import com.zhiyicx.thinksnsplus.data.beans.UserInfoBean;
import com.zhiyicx.thinksnsplus.modules.edit_userinfo.location.search.LocationSearchContract;
import com.zhiyicx.thinksnsplus.modules.edit_userinfo.location.search.LocationSearchListAdapter;
import com.zhiyicx.thinksnsplus.modules.findsomeone.list.FindSomeOneListAdapter;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.MultiItemTypeAdapter;

import butterknife.BindView;
import butterknife.OnClick;

import static android.app.Activity.RESULT_OK;

/**
 * @Describe 地区搜索界面
 * @Author Jungle68
 * @Date 2017/1/9
 * @Contact master.jungle68@gmail.com
 */
public class SearchSomeOneFragment extends TSListFragment<SearchSomeOneContract.Presenter, UserInfoBean> implements SearchSomeOneContract.View, MultiItemTypeAdapter.OnItemClickListener {

    public static final String BUNDLE_DATA = "DATA";
    public static final String BUNDLE_LOCATION_STRING = "location_string";


    @BindView(R.id.fragment_info_search_edittext)
    DeleteEditText mFragmentInfoSearchEdittext;

    @BindView(R.id.fragment_search_container)
    RelativeLayout mFragmentInfoSearchContainer;


    public static SearchSomeOneFragment newInstance(Bundle args) {

        SearchSomeOneFragment fragment = new SearchSomeOneFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getBodyLayoutId() {
        return R.layout.fragment_location_search;
    }

    @Override
    protected boolean showToolBarDivider() {
        return false;
    }

    @Override
    protected boolean isRefreshEnable() {
        return false;
    }

    @Override
    protected boolean isLoadingMoreEnable() {
        return false;
    }

    @Override
    protected void musicWindowsStatus(boolean isShow) {
        super.musicWindowsStatus(isShow);
        if (isShow) {
            int rightX = ConvertUtils.dp2px(getContext(), 44) * 3 / 4 + ConvertUtils.dp2px(getContext(), 15);
            mFragmentInfoSearchContainer.setPadding(0, 0, rightX, 0);
        }
    }

    @Override
    protected void initView(View rootView) {
        super.initView(rootView);
        mEmptyView.setVisibility(View.GONE);
        mFragmentInfoSearchEdittext.setOnEditorActionListener(
                (v, actionId, event) -> {
                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                        mPresenter.searchUser(mFragmentInfoSearchEdittext.getText().toString());
                        DeviceUtils.hideSoftKeyboard(getContext(), mFragmentInfoSearchEdittext);
                        return true;
                    }
                    return false;
                });
        mRvList.setBackgroundResource(R.color.white);
    }

    @Override
    protected void initData() {
        super.initData();
        if (getArguments() != null && !TextUtils.isEmpty(getArguments().getString(BUNDLE_LOCATION_STRING))) {
            mFragmentInfoSearchEdittext.setText(getArguments().getString(BUNDLE_LOCATION_STRING));
            mFragmentInfoSearchEdittext.setSelection(getArguments().getString(BUNDLE_LOCATION_STRING).length());
        }
    }

    @Override
    protected boolean showToolbar() {
        return false;
    }

    @OnClick({R.id.fragment_search_back, R.id.fragment_search_cancle})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fragment_search_back:
                getActivity().finish();
                break;
            case R.id.fragment_search_cancle:
                getActivity().finish();
                break;
        }
    }

    @Override
    protected CommonAdapter getAdapter() {
        CommonAdapter adapter = new FindSomeOneListAdapter(getActivity(), R.layout.item_find_some_list, mListDatas,mPresenter);
        adapter.setOnItemClickListener(this);
        return adapter;

    }

    @Override
    public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {
//
//        UserInfoBean bean = mListDatas.get(position);
//        Intent intent = new Intent();
//        Bundle bundle = new Bundle();
//        bundle.putParcelable(BUNDLE_DATA, bean);
//        intent.putExtras(bundle);
//        getActivity().setResult(RESULT_OK, intent);
//        getActivity().finish();
    }

    @Override
    public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
        return false;
    }

    @Override
    public void upDateFollowFansState(int index) {
        refreshData(index);
    }
}
