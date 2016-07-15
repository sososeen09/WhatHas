package com.longge.whathas.net;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

/**
 * Created by long on 2016/7/13.
 */
public class ImageLoader {
    private static ImageLoader sImageLoader;

    private ImageLoader() {

    }

    public static ImageLoader getInstance() {

        if (sImageLoader == null) {
            synchronized (ImageLoader.class) {
                if (sImageLoader == null) {
                    sImageLoader = new ImageLoader();
                }
            }
        }
        return sImageLoader;
    }

    public void disPlayPic(Context context, ImageView imageView, String url) {

        Glide.with(context)
             .load(url)
             .into(imageView);

    }
}
