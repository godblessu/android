package com.zhiyicx.thinksnsplus.modules.edit_userinfo;

import com.zhiyicx.common.base.BaseJson;
import com.zhiyicx.common.mvp.i.IBasePresenter;
import com.zhiyicx.common.mvp.i.IBaseView;
import com.zhiyicx.thinksnsplus.data.beans.AreaBean;
import com.zhiyicx.thinksnsplus.data.beans.CommentedBean;
import com.zhiyicx.thinksnsplus.data.beans.DigRankBean;
import com.zhiyicx.thinksnsplus.data.beans.DigedBean;
import com.zhiyicx.thinksnsplus.data.beans.FlushMessages;
import com.zhiyicx.thinksnsplus.data.beans.FollowFansBean;
import com.zhiyicx.thinksnsplus.data.beans.UserInfoBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import rx.Observable;

/**
 * @author LiuChao
 * @describe
 * @date 2017/1/9
 * @contact email:450127106@qq.com
 */

public interface UserInfoContract {
    interface View extends IBaseView<UserInfoContract.Presenter> {
        /**
         * 设置地域选择的数据
         */
        void setAreaData(ArrayList<AreaBean> options1Items, ArrayList<ArrayList<AreaBean>> options2Items, ArrayList<ArrayList<ArrayList<AreaBean>>> options3Items);

        /**
         * 设置头像上传的状态
         *
         * @param upLoadState -1 失败 0进行中 1 图片上传成功 2图片用户信息修改成功
         * @param taskId      返回的图片任务id
         */
        void setUpLoadHeadIconState(int upLoadState, int taskId);

        /**
         * 设置信息修改提交状态
         *
         * @param changeUserInfoState -1 失败 1进行中 2 成功
         */
        void setChangeUserInfoState(int changeUserInfoState, String message);

        /**
         * 初始化界面数据
         */
        void initUserInfo(UserInfoBean mUserInfoBean);

    }

    interface Repository {
        /**
         * 从本地文件获取全国所有的省市
         */
        Observable<ArrayList<AreaBean>> getAreaList();

        /**
         * 编辑用户信息
         *
         * @param userInfos 用户需要修改的信息，通过 hashMap 传递，key 表示请求字段，value 表示修改的值
         */
        Observable<BaseJson> changeUserInfo(HashMap<String, String> userInfos);

        /**
         * 获取用户信息
         *
         * @param user_ids 用户 id 数组
         * @return
         */
        Observable<BaseJson<List<UserInfoBean>>> getUserInfo(List<Object> user_ids);

        /**
         * 获取用户关注状态
         */
        Observable<BaseJson<List<FollowFansBean>>> getUserFollowState(String user_ids);

        /**
         * 关注操作
         *
         * @param followFansBean
         */
        void handleFollow(FollowFansBean followFansBean);

        /**
         * 获取点赞排行榜
         *
         * @param page
         * @return
         */
        Observable<BaseJson<List<DigRankBean>>> getDidRankList(int page);

        /**
         * 获取用户收到的点赞
         *
         * @param max_id
         * @return
         */
        Observable<BaseJson<List<DigedBean>>> getMyDiggs(int max_id);


        /**
         * 获取用户收到的评论
         *
         * @param max_id
         * @return
         */
        Observable<BaseJson<List<CommentedBean>>> getMyComments(int max_id);


        /**
         * @param time 零时区的秒级时间戳
         * @param key  查询关键字 默认查询全部 多个以逗号隔开 可选参数有 diggs comments follows
         * @return
         */
        Observable<BaseJson<List<FlushMessages>>> getMyFlushMessage(long time, String key);
    }

    interface Presenter extends IBasePresenter {
        void getAreaData();

        /**
         * 上传用户头像
         *
         * @param filePath
         */
        void changeUserHeadIcon(String filePath);

        /**
         * @param userInfos
         * @param isHeadIcon 仅仅修改头像
         */
        void changUserInfo(HashMap<String, String> userInfos, boolean isHeadIcon);


        /**
         * 初始化用户界面数据
         */
        void initUserInfo();

    }
}
