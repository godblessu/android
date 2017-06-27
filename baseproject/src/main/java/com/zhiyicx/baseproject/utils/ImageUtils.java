package com.zhiyicx.baseproject.utils;

import android.content.Context;

import com.bumptech.glide.load.model.GenericLoaderFactory;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.Headers;
import com.bumptech.glide.load.model.LazyHeaders;
import com.bumptech.glide.load.model.ModelLoaderFactory;
import com.bumptech.glide.load.model.stream.BaseGlideUrlLoader;
import com.bumptech.glide.load.model.stream.StreamModelLoader;
import com.zhiyicx.baseproject.config.ApiConfig;

import java.io.InputStream;
import java.util.Locale;

/**
 * @Describe
 * @Author Jungle68
 * @Date 2017/3/7
 * @Contact master.jungle68@gmail.com
 */

public class ImageUtils {

    /**
     * 图片地址转换
     *
     * @param storage 图片对应的 id 号，也可能是本地的图片路径
     * @param part    压缩比例 0-100
     * @return
     */
    public static String imagePathConvert(String storage, int part) {
        try {
            // 如果图片的storage能够转成一个整数
            Integer.parseInt(storage);
            return String.format(Locale.getDefault(), ApiConfig.IMAGE_PATH, storage, part);
        } catch (NumberFormatException e) {
            return storage;
        }
    }

    /**
     * 图片地址转换 V2 api
     *
     * @param storage 图片对应的 id 号，也可能是本地的图片路径
     * @param part    压缩比例 0-100
     * @return
     */
    public static String imagePathConvertV2(int storage, int w, int h, int part) {
        try {
            // 如果图片的storage能够转成一个整数
            return String.format(Locale.getDefault(), ApiConfig.IMAGE_PATH_V2, storage, w, h, part);
        } catch (NumberFormatException e) {
            return "";
        }

//        return new GlideUrl(String.format(Locale.getDefault(), ApiConfig.IMAGE_PATH_V2, storage, w, h, part), new LazyHeaders.Builder()
//                .addHeader("Authorization",token )
//                .build());

    }

    public static class V2ImageHeaderedLoader extends BaseGlideUrlLoader<String> {
        final Headers HEADERS;

        public V2ImageHeaderedLoader(Context context, String token) {
            super(context);
            HEADERS = new LazyHeaders.Builder()
                    .addHeader("Authorization", token)
                    .build();
        }

        @Override
        protected String getUrl(String model, int width, int height) {
            return model;
        }

        @Override
        protected Headers getHeaders(String model, int width, int height) {
            return HEADERS;
        }

        public static class Factory implements ModelLoaderFactory<String, InputStream> {
            String token;

            public Factory(String token) {
                this.token = token;
            }

            @Override
            public StreamModelLoader<String> build(Context context, GenericLoaderFactory factories) {
                return new V2ImageHeaderedLoader(context, token);
            }

            @Override
            public void teardown() { /* nothing to free */ }
        }
    }

}
