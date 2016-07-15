package com.longge.whathas.net;

import com.zhy.http.okhttp.callback.StringCallback;

/**
 * Created by long on 2016/7/15.
 */
public class NetUtils {
    public static void loadHtml(String htmlUrl, StringCallback stringCallback) {
        NetManager.getInstance()
                  .loadHtml(htmlUrl, stringCallback);
    }
}
