package com.zhiyicx.thinksnsplus.data.source.repository;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zhiyicx.baseproject.cache.CacheImp;
import com.zhiyicx.common.base.BaseJson;
import com.zhiyicx.common.utils.ConvertUtils;
import com.zhiyicx.thinksnsplus.base.AppApplication;
import com.zhiyicx.thinksnsplus.data.beans.AreaBean;
import com.zhiyicx.thinksnsplus.data.beans.AuthBean;
import com.zhiyicx.thinksnsplus.data.beans.StorageTaskBean;
import com.zhiyicx.thinksnsplus.data.source.remote.CommonClient;
import com.zhiyicx.thinksnsplus.data.source.remote.ServiceManager;
import com.zhiyicx.thinksnsplus.data.source.remote.UserInfoClient;
import com.zhiyicx.thinksnsplus.modules.edit_userinfo.UserInfoContract;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * @author LiuChao
 * @describe 用户信息相关的model层实现
 * @date 2017/1/9
 * @contact email:450127106@qq.com
 */

public class UserInfoRepository implements UserInfoContract.Repository {
    private UserInfoClient mUserInfoClient;
    private CommonClient mCommonClient;
    private CacheImp<AuthBean> cacheImp;

    public UserInfoRepository(ServiceManager serviceManager) {
        mUserInfoClient = serviceManager.getUserInfoClient();
        mCommonClient = serviceManager.getCommonClient();
    }

    @Override
    public Observable<ArrayList<AreaBean>> getAreaList() {
        Observable<ArrayList<AreaBean>> observable = Observable.create(new Observable.OnSubscribe<ArrayList<AreaBean>>() {
            @Override
            public void call(Subscriber<? super ArrayList<AreaBean>> subscriber) {
                try {
                    InputStream inputStream = AppApplication.getContext().getAssets().open("area.txt");//读取本地assets数据
                    String jsonString = ConvertUtils.inputStream2String(inputStream, "utf-8");
                    ArrayList<AreaBean> areaBeanList = new Gson().fromJson(jsonString, new TypeToken<ArrayList<AreaBean>>() {
                    }.getType());
                    subscriber.onNext(areaBeanList);
                    subscriber.onCompleted();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
        return observable;
    }

    @Override
    public Observable<BaseJson> changeUserHeadIcon(String hash, String fileName, final Map<String, String> filePathList) {
        return mCommonClient.createStorageTask(hash, fileName, null)
                // 处理创建存储任务到上传文件的过程
                .flatMap(new Func1<BaseJson<StorageTaskBean>, Observable<String[]>>() {
                    @Override
                    public Observable<String[]> call(BaseJson<StorageTaskBean> storageTaskBeanBaseJson) {
                        // 服务器获取成功
                        if (storageTaskBeanBaseJson.isStatus()) {
                            StorageTaskBean storageTaskBean = storageTaskBeanBaseJson.getData();
                            int storageId = storageTaskBean.getStorage_id();
                            final int storageTaskId = storageTaskBean.getStorage_task_id();
                            if (storageId > 0) {
                                // 表示服务器已经存在该附件,已经成功上传，不需要做其他事情了
                                return Observable.just(new String[]{"success", storageTaskId + ""});
                            } else {
                                // 创建上传任务成功，开始上传
                                String method = storageTaskBean.getMethod();
                                String uri = storageTaskBean.getUri();
                                // 处理headers
                                JSONObject headers = storageTaskBean.getHeaders();
                                HashMap<String, String> headerMap = parseJSONObject(headers);
                                if (method.equalsIgnoreCase("put")) {
                                    // 使用map操作符携带任务id，继续向下传递
                                    return mCommonClient.upLoadFileByPut(uri, headerMap, upLoadFile(filePathList, null)).map(new Func1<String, String[]>() {
                                        @Override
                                        public String[] call(String s) {
                                            return new String[]{s, storageTaskId + ""};
                                        }
                                    });
                                } else if (method.equalsIgnoreCase("post")) {
                                    // 使用map操作符携带任务id，继续向下传递
                                    return mCommonClient.upLoadFileByPost(uri, headerMap, upLoadFile(filePathList, null)).map(new Func1<String, String[]>() {
                                        @Override
                                        public String[] call(String s) {
                                            return new String[]{s, storageTaskId + ""};
                                        }
                                    });
                                } else {
                                    return Observable.just(new String[]{"failure", ""});// 没有合适的方法上传文件，这一般是不会发生的
                                }
                            }
                        } else {
                            // 表示服务器创建存储任务失败
                            return Observable.just(new String[]{"failure", ""});
                        }
                    }
                })
                // 处理上传文件到获取任务通知的过程
                .flatMap(new Func1<String[], Observable<BaseJson>>() {
                    @Override
                    public Observable<BaseJson> call(String[] s) {
                        switch (s[0]) {
                            case "success":// 直接成功
                                BaseJson success = new BaseJson();
                                success.setStatus(true);
                                return Observable.just(success);
                            case "failure":// 失败
                                BaseJson failure = new BaseJson();
                                failure.setStatus(false);
                                return Observable.just(failure);
                            default:// 调用通知任务
                                return mCommonClient.notifyStorageTask(s[1], s[0], null);
                        }
                    }
                });
    }

    @Override
    public Observable<BaseJson> changeUserInfo(HashMap<String, String> userInfos) {
        return mUserInfoClient.changeUserInfo(userInfos);
    }

    /**
     * 上传文件，构造参数
     */
    private List<MultipartBody.Part> upLoadFile(Map<String, String> filePathList, Map<String, String> params) {
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);//表单类型
        if (params != null) {
            Set<String> keys = params.keySet();
            for (String key : keys) {
                builder.addFormDataPart(key, params.get(key));//ParamKey.TOKEN 自定义参数key常量类，即参数名
            }
        }
        if (filePathList != null) {
            Set<String> filePathKey = filePathList.keySet();
            for (String fileParam : filePathKey) {
                try {
                    File file = new File(filePathList.get(fileParam));//filePath 图片地址
                    RequestBody imageBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
                    builder.addFormDataPart(fileParam, file.getName(), imageBody);//imgfile 后台接收图片流的参数名
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
        }
        List<MultipartBody.Part> parts = builder.build().parts();
        return parts;
    }

    private HashMap<String, String> parseJSONObject(JSONObject jsonObject) {

        if (jsonObject == null) {
            return null;
        }
        HashMap<String, String> jsonMap = new HashMap<>();
        Iterator<String> iterator = jsonObject.keys();
        while (iterator.hasNext()) {
            try {
                String key = iterator.next();
                jsonMap.put(key, jsonObject.getString(key));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return jsonMap;
    }
}
