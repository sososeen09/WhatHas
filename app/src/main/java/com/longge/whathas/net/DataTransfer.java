package com.longge.whathas.net;

import com.zhy.http.okhttp.callback.StringCallback;

/**
 * Created by long on 2016/7/15.
 */
public interface DataTransfer {
    void loadHtml(String htmlUrl, StringCallback stringCallback);//加载html数据
}
