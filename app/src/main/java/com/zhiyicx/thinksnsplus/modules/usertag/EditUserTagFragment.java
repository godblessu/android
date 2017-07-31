package com.zhiyicx.thinksnsplus.modules.usertag;

import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jakewharton.rxbinding.view.RxView;
import com.zhiyicx.baseproject.base.TSFragment;
import com.zhiyicx.baseproject.widget.recycleview.stickygridheaders.StickyHeaderGridLayoutManager;
import com.zhiyicx.thinksnsplus.R;
import com.zhiyicx.thinksnsplus.data.beans.TagCategoryBean;
import com.zhiyicx.thinksnsplus.data.beans.UserTagBean;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.MultiItemTypeAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;

import static com.zhiyicx.common.config.ConstantConfig.JITTER_SPACING_TIME;

/**
 * @Describe
 * @Author Jungle68
 * @Date 2017/1/9
 * @Contact master.jungle68@gmail.com
 */
public class EditUserTagFragment extends TSFragment<EditUserTagContract.Presenter> implements EditUserTagContract.View, TagClassAdapter.OnItemClickListener {
    private static final int SPAN_SIZE = 3;

    @BindView(R.id.tv_choosed_tag_tip)
    TextView mTvChoosedTagTip;
    @BindView(R.id.tv_jump_over)
    TextView mTvJumpOver;
    @BindView(R.id.rv_choosed_tag)
    RecyclerView mRvChoosedTag;
    @BindView(R.id.rv_tag_class)
    RecyclerView mRvTagClass;

    private GridLayoutManager mChoosedTagLayoutManager;
    private StickyHeaderGridLayoutManager mTagClassLayoutManager;


    private List<UserTagBean> mChoosedTags = new ArrayList<>();
    private List<TagCategoryBean> mCategoryTags = new ArrayList<>();

    private int mMaxChooseNums;
    private int mCurrentChooseNums = 0;
    private TagClassAdapter mTagClassAdapter;
    private CommonAdapter<UserTagBean> mChoosedTagAdapter;


    public static EditUserTagFragment newInstance() {
        return new EditUserTagFragment();
    }

    @Override
    protected int getBodyLayoutId() {
        return R.layout.fragment_edit_user_tag;
    }

    @Override
    protected String setCenterTitle() {
        return getString(R.string.user_tag_choose_tag);
    }

    @Override
    protected int setToolBarBackgroud() {
        return R.color.white;
    }

    @Override
    protected boolean showToolBarDivider() {
        return true;
    }

    @Override
    protected String setRightTitle() {
        return getString(R.string.save);
    }

    @Override
    protected void initView(View rootView) {
        mMaxChooseNums = getResources().getInteger(R.integer.user_tag_max_nums);
        mTvChoosedTagTip.setText(getString(R.string.user_tag_choosed_tag_format, mMaxChooseNums, mCurrentChooseNums));
        initRvChoosedTag();
        initRvTagClass();
        initListener();
    }

    @Override
    protected void initData() {
        mPresenter.getAllTags();
    }


    private void initRvChoosedTag() {
        mChoosedTagLayoutManager = new GridLayoutManager(getContext(), SPAN_SIZE);
        mRvChoosedTag.setLayoutManager(mChoosedTagLayoutManager);
        mRvChoosedTag.setHasFixedSize(false);
        initSubscribeAdapter();
        mRvChoosedTag.setAdapter(mChoosedTagAdapter);
    }

    private CommonAdapter initSubscribeAdapter() {
        mChoosedTagAdapter = new CommonAdapter<UserTagBean>(getActivity(), R.layout
                .item_user_tag, mChoosedTags) {
            @Override
            protected void convert(ViewHolder holder, UserTagBean data
                    , final int position) {
                holder.setText(R.id.item_info_channel, data.getTagName());
            }

            @Override
            protected void setListener(ViewGroup parent, final ViewHolder viewHolder, int viewType) {
                RxView.clicks(viewHolder.itemView)
                        .throttleFirst(JITTER_SPACING_TIME, TimeUnit.SECONDS)
                        .compose(bindToLifecycle())
                        .subscribe(o -> {
                            if (mOnItemClickListener != null) {
                                int position = viewHolder.getAdapterPosition();
                                mOnItemClickListener.onItemClick(viewHolder.itemView, viewHolder, position);
                            }
                        });

                viewHolder.getConvertView().setOnLongClickListener(v -> {
                    if (mOnItemClickListener != null) {
                        int position = viewHolder.getAdapterPosition();
                        return mOnItemClickListener.onItemLongClick(v, viewHolder, position);
                    }
                    return true;
                });
            }
        };
        mChoosedTagAdapter.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {
                mPresenter.deleteTag(mChoosedTags.get(position).getId(), position);

            }

            @Override
            public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
                return false;
            }

        });

        return mChoosedTagAdapter;
    }

    private void initRvTagClass() {
        mTagClassLayoutManager = new StickyHeaderGridLayoutManager(SPAN_SIZE);
        mTagClassLayoutManager.setHeaderBottomOverlapMargin(getResources().getDimensionPixelSize(R.dimen.spacing_small));
        mTagClassLayoutManager.setSpanSizeLookup(new StickyHeaderGridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int section, int position) {
                return 1;
            }
        });

        // Workaround RecyclerView limitation when playing remove animations. RecyclerView always
        // puts the removed item on the top of other views and it will be drawn above sticky header.
        // The only way to fix this, abandon remove animations :(
        mRvTagClass.setItemAnimator(new DefaultItemAnimator() {
            @Override
            public boolean animateRemove(RecyclerView.ViewHolder holder) {
                dispatchRemoveFinished(holder);
                return false;
            }
        });
        mRvTagClass.setLayoutManager(mTagClassLayoutManager);
        mTagClassAdapter = new TagClassAdapter(mCategoryTags);
        mTagClassAdapter.setOnItemClickListener(this);
        mRvTagClass.setAdapter(mTagClassAdapter);

    }

    private void initListener() {

        // 跳过
        RxView.clicks(mTvJumpOver)
                .throttleFirst(JITTER_SPACING_TIME, TimeUnit.SECONDS)   //两秒钟之内只取一个点击事件，防抖操作
                .compose(this.bindToLifecycle())
                .subscribe(aVoid -> {

                });
    }

    /**
     * check save button
     *
     * @return
     */
    private boolean checkSaveEnable() {
        return mCurrentChooseNums > 0;
    }

    /**
     * 更新所有标签
     *
     * @param tagCategoryBeanList
     */
    @Override
    public void updateTagsFromNet(List<TagCategoryBean> tagCategoryBeanList) {
        if (tagCategoryBeanList == null) {
            return;
        }
        mCategoryTags.clear();
        this.mCategoryTags.addAll(tagCategoryBeanList);
        mTagClassAdapter.notifyAllSectionsDataSetChanged();
    }

    @Override
    public void updateMineTagsFromNet(List<UserTagBean> tags) {
        if (tags == null) {
            return;
        }
        mChoosedTags.clear();
        mChoosedTags.addAll(tags);
        mChoosedTagAdapter.notifyDataSetChanged();

    }

    @Override
    public void onItemClick(int categoryPosition, int tagPosition) {
        if (mCategoryTags.get(categoryPosition).getTags().get(tagPosition).isMine_has()) {
            return;
        }
        if (mChoosedTags.size() >= mMaxChooseNums) {
            showSnackErrorMessage(getString(R.string.user_tag_choosed_tag_format_tip, mMaxChooseNums));
            return;
        }
        mPresenter.addTags(mCategoryTags.get(categoryPosition).getTags().get(tagPosition).getId(), categoryPosition, tagPosition);

    }

    @Override
    public void addTagSuccess(int categoryPosition, int tagPosition) {
        mCategoryTags.get(categoryPosition).getTags().get(tagPosition).setMine_has(true);
        mPresenter.handleCategoryTagsClick(mCategoryTags.get(categoryPosition).getTags().get(tagPosition));
        mChoosedTags.add(mCategoryTags.get(categoryPosition).getTags().get(tagPosition));
        mChoosedTagAdapter.notifyDataSetChanged();
        mTagClassAdapter.notifyAllSectionsDataSetChanged();
    }

    @Override
    public void deleteTagSuccess(int position) {
        mChoosedTags.get(position).setMine_has(false);
        mPresenter.handleCategoryTagsClick(mChoosedTags.get(position));
        mChoosedTagAdapter.removeItem(position);
        mTagClassAdapter.notifyAllSectionsDataSetChanged();

    }
}
