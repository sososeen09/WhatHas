package com.longge.whathas.net;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by long on 2016/7/14.
 */
public interface RestApi {
    @GET("meinv/page/{count}")
    Call<PrettyHtml> getHtml(@Path("count") String count);

    public class PrettyHtml {
        public String htmlString;
    }

}
