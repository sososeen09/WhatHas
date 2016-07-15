package com.longge.whathas.net;

import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

/**
 * Created by long on 2016/7/15.
 */
public class NetManager implements DataTransfer {

    private static NetManager sNetManager;

    private NetManager() {

    }

    public static NetManager getInstance() {

        if (sNetManager == null) {
            synchronized (NetManager.class) {
                if (sNetManager == null) {
                    sNetManager = new NetManager();
                }
            }
        }
        return sNetManager;
    }


    @Override
    public void loadHtml(String htmlUrl, StringCallback stringCallback) {
        OkHttpUtils.get()
                   .url(htmlUrl)
                   .build()
                   .execute(stringCallback);
    }


}
