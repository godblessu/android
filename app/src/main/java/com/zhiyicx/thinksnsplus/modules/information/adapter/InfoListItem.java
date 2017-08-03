package com.zhiyicx.thinksnsplus.modules.information.adapter;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.zhiyicx.baseproject.base.BaseListBean;
import com.zhiyicx.baseproject.config.ImageZipConfig;
import com.zhiyicx.common.base.BaseApplication;
import com.zhiyicx.common.utils.SkinUtils;
import com.zhiyicx.common.utils.TimeUtils;
import com.zhiyicx.thinksnsplus.R;
import com.zhiyicx.thinksnsplus.base.AppApplication;
import com.zhiyicx.thinksnsplus.data.beans.InfoListDataBean;
import com.zhiyicx.thinksnsplus.utils.ImageUtils;
import com.zhy.adapter.recyclerview.base.ItemViewDelegate;
import com.zhy.adapter.recyclerview.base.ViewHolder;

public abstract class InfoListItem implements ItemViewDelegate<BaseListBean> {

    @Override
    public int getItemViewLayoutId() {
        return R.layout.item_info;
    }

    @Override
    public boolean isForViewType(BaseListBean item, int position) {
        return item instanceof InfoListDataBean;
    }

    @Override
    public void convert(ViewHolder holder, BaseListBean baseListBean, BaseListBean lastT,
                        final int position, int itemCounts) {
        final InfoListDataBean realData = (InfoListDataBean) baseListBean;
        final TextView title = holder.getView(R.id.item_info_title);
        final ImageView imageView = holder.getView(R.id.item_info_imag);

        // 记录点击过后颜色
        if (AppApplication.sOverRead.contains(position + "")) {
            title.setTextColor(SkinUtils.getColor(R.color.normal_for_assist_text));
        }

        title.setText(realData.getTitle());
        if (realData.getStorage() == null) {
            imageView.setVisibility(View.GONE);
        } else {
            imageView.setVisibility(View.VISIBLE);
            Glide.with(BaseApplication.getContext())
                    .load(ImageUtils.imagePathConvertV2(realData.getStorage().getId(), imageView.getWidth(), imageView.getHeight(),
                            ImageZipConfig.IMAGE_80_ZIP))
                    .placeholder(R.drawable.shape_default_image)
                    .error(R.drawable.shape_default_image)
                    .override(imageView.getContext().getResources().getDimensionPixelOffset(R.dimen.info_channel_list_image_width)
                            , imageView.getContext().getResources().getDimensionPixelOffset(R.dimen.info_channel_list_height))
                    .into(imageView);
        }
        String from = TextUtils.isEmpty(realData.getFrom()) ? "" : "\b\b\b来自\b" + realData.getFrom();
        holder.setText(R.id.item_info_timeform, TimeUtils.getTimeFriendlyNormal(realData
                .getUpdated_at()) + from);

        holder.itemView.setOnClickListener(v -> itemClick(position, imageView, title, realData));
    }

    public abstract void itemClick(int position, ImageView imageView, TextView title, InfoListDataBean realData);

}