package com.tym.shortvideo.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;

import com.tym.shortvideo.filter.helper.MagicFilterType;
import com.tym.shortvideo.interfaces.SingleCallback;
import com.tym.shortvideo.interfaces.TrimVideoListener;
import com.tym.shortvideo.media.VideoInfo;
import com.tym.shortvideo.mediacodec.VideoClipper;
import com.zhiyicx.common.utils.*;
import com.zhiyicx.common.utils.FileUtils;
import com.zhiyicx.common.utils.log.LogUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class TrimVideoUtil {

    private static final String TAG = TrimVideoUtil.class.getSimpleName();
    public static final int VIDEO_MAX_DURATION = 15;
    public static final int MIN_TIME_FRAME = 3;
    private static int thumb_Width = (DeviceUtils.getScreenWidth() - DeviceUtils.dipToPX(20)) /
            VIDEO_MAX_DURATION;
    private static final int thumb_Height = DeviceUtils.dipToPX(60);
    private static final long one_frame_time = 1000000;

    public static void trim(Context context, Uri inputFile, String outputFile, long startMs, long
            endMs, final TrimVideoListener callback) {
        VideoClipper clipper = new VideoClipper();
        clipper.setFilterType(MagicFilterType.NONE);
        clipper.setInputVideoPath(context, inputFile);
        clipper.setOutputVideoPath(outputFile);
        final String tempOutFile = outputFile;

        clipper.setOnVideoCutFinishListener(new VideoClipper.OnVideoCutFinishListener() {
            @Override
            public void onFinish() {
                callback.onFinishTrim(tempOutFile);
            }
        });
        try {
            clipper.clipVideo(startMs, endMs - startMs);
            callback.onStartTrim();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static ArrayList<Bitmap> test(Context context, Uri videoUri, ArrayList<Bitmap> thumbnailList) {
        try {
            MediaMetadataRetriever mediaMetadataRetriever =
                    new MediaMetadataRetriever();
            mediaMetadataRetriever.setDataSource(context,
                    videoUri);
            // 微秒
            long videoLengthInMs = Long.parseLong
                    (mediaMetadataRetriever.extractMetadata
                            (MediaMetadataRetriever
                                    .METADATA_KEY_DURATION)) *
                    1000;
            long numThumbs = videoLengthInMs < one_frame_time
                    ? 1 : (videoLengthInMs / one_frame_time);
            // 1s 两张
            numThumbs += numThumbs;
            final long interval = videoLengthInMs / numThumbs;
            for (long i = 0; i < numThumbs; ++i) {
                Bitmap bitmap = mediaMetadataRetriever
                        .getFrameAtTime(i * interval,
                                MediaMetadataRetriever
                                        .OPTION_CLOSEST_SYNC);
                if (bitmap == null) {
                    continue;
                }
                try {
                    bitmap = Bitmap.createScaledBitmap(bitmap,
                            thumb_Height, thumb_Height, false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                thumbnailList.add(bitmap);
            }
            mediaMetadataRetriever.release();
        } catch (Exception ignore) {

        }
        return thumbnailList;
    }


    public static void backgroundShootVideoThumb(final Context context, final List<Uri> videoUris, final
    SingleCallback<ArrayList<Bitmap>, Integer> callback) {
        final ArrayList<Bitmap> thumbnailList = new ArrayList<>();
        BackgroundExecutor.execute(new BackgroundExecutor.Task("", 0L, "") {
                                       @Override
                                       public void execute() {
                                           for (Uri uri : videoUris) {
                                               test(context, uri, thumbnailList);
                                           }
                                           callback.onSingleCallback(thumbnailList,1);
                                       }
                                   }
        );

    }

    public static void backgroundShootVideoThumb(final Context context, final Uri videoUri, final
    SingleCallback<ArrayList<Bitmap>, Integer> callback) {
        final ArrayList<Bitmap> thumbnailList = new ArrayList<>();
        BackgroundExecutor.execute(new BackgroundExecutor.Task("", 0L, "") {
                                       @Override
                                       public void execute() {
                                           try {
                                               MediaMetadataRetriever mediaMetadataRetriever =
                                                       new MediaMetadataRetriever();
                                               mediaMetadataRetriever.setDataSource(context,
                                                       videoUri);
                                               // 微秒
                                               long videoLengthInMs = Long.parseLong
                                                       (mediaMetadataRetriever.extractMetadata
                                                               (MediaMetadataRetriever
                                                                       .METADATA_KEY_DURATION)) *
                                                       1000;
                                               long numThumbs = videoLengthInMs < one_frame_time
                                                       ? 1 : (videoLengthInMs / one_frame_time);
                                               // 1s 两张
                                               numThumbs += numThumbs;
                                               final long interval = videoLengthInMs / numThumbs;
                                               float w, h;
                                               w = thumb_Width;
                                               h = thumb_Height;
                                               for (long i = 0; i < numThumbs; ++i) {
                                                   Bitmap bitmap = mediaMetadataRetriever
                                                           .getFrameAtTime(i * interval,
                                                                   MediaMetadataRetriever
                                                                           .OPTION_CLOSEST_SYNC);
                                                   if (bitmap == null) {
                                                       continue;
                                                   }
                                                   if (h == 0) {
                                                       float scale = (float) bitmap.getHeight() / (float) bitmap.getWidth();
                                                       h = w * scale;
                                                   }
                                                   try {
                                                       bitmap = Bitmap.createScaledBitmap(bitmap,
                                                               thumb_Width, (int) h, false);
                                                   } catch (Exception e) {
                                                       e.printStackTrace();
                                                   }
                                                   thumbnailList.add(bitmap);
                                                   if (thumbnailList.size() == 3) {
                                                       callback.onSingleCallback(
                                                               (ArrayList<Bitmap>) thumbnailList
                                                                       .clone(), (int) interval);
                                                       thumbnailList.clear();
                                                   }
                                               }
                                               if (thumbnailList.size() > 0) {
                                                   callback.onSingleCallback((ArrayList<Bitmap>)
                                                           thumbnailList.clone(), (int) interval);
                                                   thumbnailList.clear();
                                               }
                                               mediaMetadataRetriever.release();
                                           } catch (final Throwable e) {
                                               Thread.getDefaultUncaughtExceptionHandler()
                                                       .uncaughtException(Thread.currentThread(),
                                                               e);
                                           }
                                       }
                                   }
        );

    }


    public static void backgroundShootVideoThumb(final Context context, final Uri videoUri, final
    long frame_time, final SingleCallback<Bitmap, Integer> callback) {
        BackgroundExecutor.execute(new BackgroundExecutor.Task("", 0L, "") {
                                       @Override
                                       public void execute() {
                                           try {
                                               MediaMetadataRetriever mediaMetadataRetriever =
                                                       new MediaMetadataRetriever();
                                               mediaMetadataRetriever.setDataSource(context,
                                                       videoUri);
                                               // 微秒
                                               long videoLengthInMs = Long.parseLong
                                                       (mediaMetadataRetriever.extractMetadata
                                                               (MediaMetadataRetriever
                                                                       .METADATA_KEY_DURATION)) *
                                                       1000;
                                               Bitmap bitmap = mediaMetadataRetriever
                                                       .getFrameAtTime(frame_time,
                                                               MediaMetadataRetriever
                                                                       .OPTION_CLOSEST_SYNC);

                                               callback.onSingleCallback(bitmap, 1);

                                               mediaMetadataRetriever.release();
                                           } catch (final Throwable e) {
                                               Thread.getDefaultUncaughtExceptionHandler()
                                                       .uncaughtException(Thread.currentThread(),
                                                               e);
                                           }
                                       }
                                   }
        );

    }

    public static String getVideoFilePath(String url) {

        if (TextUtils.isEmpty(url) || url.length() < 5) {
            return "";
        }

        if (url.substring(0, 4).equalsIgnoreCase("http")) {
        } else {
            url = "file://" + url;
        }

        return url;
    }

    private static String convertSecondsToTime(long seconds) {
        String timeStr = null;
        int hour = 0;
        int minute = 0;
        int second = 0;
        if (seconds <= 0) {
            return "00:00";
        } else {
            minute = (int) seconds / 60;
            if (minute < 60) {
                second = (int) seconds % 60;
                timeStr = "00:" + unitFormat(minute) + ":" + unitFormat(second);
            } else {
                hour = minute / 60;
                if (hour > 99) {
                    return "99:59:59";
                }
                minute = minute % 60;
                second = (int) (seconds - hour * 3600 - minute * 60);
                timeStr = unitFormat(hour) + ":" + unitFormat(minute) + ":" + unitFormat(second);
            }
        }
        return timeStr;
    }

    private static String unitFormat(int i) {
        String retStr = null;
        if (i >= 0 && i < 10) {
            retStr = "0" + Integer.toString(i);
        } else {
            retStr = "" + i;
        }
        return retStr;
    }

    public static List<VideoInfo> getAllVideoFiles(final Context mContext) {
        VideoInfo video;
        ArrayList<VideoInfo> videos = new ArrayList<>();
        ContentResolver contentResolver = mContext
                .getContentResolver();
        try {

            Cursor cursor = contentResolver.query(MediaStore
                            .Video.Media.EXTERNAL_CONTENT_URI,
                    null,
                    null, null, MediaStore.Video.Media
                            .DEFAULT_SORT_ORDER);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    video = new VideoInfo();
                    video.setPath(cursor.getString(cursor
                            .getColumnIndex(MediaStore.Video
                                    .Media.DATA)));
                    video.setCreateTime(cursor.getString
                            (cursor.getColumnIndex(MediaStore
                                    .Video.Media.DATE_ADDED)));
                    video.setName(cursor.getString(cursor
                            .getColumnIndex(MediaStore.Video
                                    .Media.DISPLAY_NAME)));
                    video.setWidth(cursor.getInt(cursor
                            .getColumnIndex(MediaStore.Video
                                    .Media.WIDTH)));
                    video.setHeight(cursor.getInt(cursor
                            .getColumnIndex(MediaStore.Video
                                    .Media.HEIGHT)));

                    video.setDuration((int) cursor.getLong(cursor
                            .getColumnIndex(MediaStore.Video
                                    .Media.DURATION)));
                    video.setStoreId(cursor.getInt(cursor
                            .getColumnIndex(MediaStore.Video
                                    .Media._ID)));
                    String mime = cursor.getString(cursor
                            .getColumnIndex(MediaStore.Video
                                    .Media.MIME_TYPE));

                    if (!"video/mp4".equals(mime)) {
                        continue;
                    }
                    if (!FileUtils
                            .isFileExists(video.getPath())) {
                        continue;
                    }

                    if (video.getDuration() < 500) {
                        continue;
                    }
                    videos.add(video);
                }
                cursor.close();
            }
        } catch (Exception ignore) {

        }
        return videos;
    }

    public static void getAllVideoFiles(final Context mContext, final
    SingleCallback<ArrayList<VideoInfo>, Integer> callback) {
        BackgroundExecutor.execute(new BackgroundExecutor.Task("", 0L, "") {
                                       @Override
                                       public void execute() {
                                           VideoInfo video;
                                           ArrayList<VideoInfo> videos = new ArrayList<>();
                                           ContentResolver contentResolver = mContext
                                                   .getContentResolver();
                                           try {

                                               Cursor cursor = contentResolver.query(MediaStore
                                                               .Video.Media.EXTERNAL_CONTENT_URI,
                                                       null,
                                                       null, null, MediaStore.Video.Media
                                                               .DEFAULT_SORT_ORDER);
                                               if (cursor != null) {
                                                   while (cursor.moveToNext()) {
                                                       video = new VideoInfo();
                                                       video.setPath(cursor.getString(cursor
                                                               .getColumnIndex(MediaStore.Video
                                                                       .Media.DATA)));
                                                       video.setCreateTime(cursor.getString
                                                               (cursor.getColumnIndex(MediaStore
                                                                       .Video.Media.DATE_ADDED)));
                                                       video.setName(cursor.getString(cursor
                                                               .getColumnIndex(MediaStore.Video
                                                                       .Media.DISPLAY_NAME)));
                                                       video.setWidth(cursor.getInt(cursor
                                                               .getColumnIndex(MediaStore.Video
                                                                       .Media.WIDTH)));
                                                       video.setHeight(cursor.getInt(cursor
                                                               .getColumnIndex(MediaStore.Video
                                                                       .Media.HEIGHT)));

                                                       LogUtils.d("---------------------------------------");
                                                       LogUtils.d("path::" + cursor.getString(cursor
                                                               .getColumnIndex(MediaStore.Video
                                                                       .Media.DATA)));
                                                       LogUtils.d("duration::" + cursor.getLong(cursor
                                                               .getColumnIndex(MediaStore.Video
                                                                       .Media.DURATION)));
                                                       LogUtils.d("width::" + cursor.getInt(cursor
                                                               .getColumnIndex(MediaStore.Video
                                                                       .Media.WIDTH)));
                                                       LogUtils.d("height::" + cursor.getInt(cursor
                                                               .getColumnIndex(MediaStore.Video
                                                                       .Media.HEIGHT)));
                                                       LogUtils.d("---------------------------------------");

                                                       video.setDuration((int) cursor.getLong(cursor
                                                               .getColumnIndex(MediaStore.Video
                                                                       .Media.DURATION)));
                                                       video.setStoreId(cursor.getInt(cursor
                                                               .getColumnIndex(MediaStore.Video
                                                                       .Media._ID)));
                                                       String mime = cursor.getString(cursor
                                                               .getColumnIndex(MediaStore.Video
                                                                       .Media.MIME_TYPE));

                                                       if (!"video/mp4".equals(mime)) {
                                                           continue;
                                                       }
                                                       if (!com.zhiyicx.common.utils.FileUtils
                                                               .isFileExists(video.getPath())) {
                                                           continue;
                                                       }

                                                       if (video.getDuration() < 3000) {
                                                           continue;
                                                       }
                                                       videos.add(video);
//                                                       if (videos.size() >= 3) {
//                                                           callback.onSingleCallback(new
// ArrayList<>(videos), 3);
//                                                           videos.clear();
//                                                       }

                                                   }
                                                   cursor.close();
                                                   callback.onSingleCallback(new ArrayList<>
                                                           (videos), -1);
                                               }
                                           } catch (Exception e) {
                                               callback.onSingleCallback(videos, 0);
                                               e.printStackTrace();
                                           }
                                       }
                                   }
        );
    }
}
