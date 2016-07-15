package com.longge.whathas.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.longge.whathas.R;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;

public class PrettyDetailActivity extends AppCompatActivity {

    @BindView(R.id.webview_pretty)
    WebView mWebviewPretty;
    @BindView(R.id.swipe_refreshLayout_detail)
    SwipeRefreshLayout mSwipeRefreshLayoutDetail;
    private String mLinkUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pretty_detail);
        ButterKnife.bind(this);
        handleIntent();
        mSwipeRefreshLayoutDetail.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayoutDetail.setRefreshing(true);
                loadWebData();
            }
        });

    }


    private void handleIntent() {
        mLinkUrl = getIntent().getStringExtra("linkUrl");
    }

    private void loadWebData() {


        mWebviewPretty.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        mWebviewPretty.setWebViewClient(new WebViewClient() {
            // 网页开始加载
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                view.getSettings()
                    .setJavaScriptEnabled(true);
                super.onPageStarted(view, url, favicon);
            }

            // 网页跳转
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return super.shouldOverrideUrlLoading(view, url);

            }

            // 网页加载结束
            @Override
            public void onPageFinished(WebView view, String url) {
                view.getSettings()
                    .setJavaScriptEnabled(true);

                super.onPageFinished(view, url);

                // html加载完成之后，添加监听图片的点击js函数
                addImageClickListner();
                mSwipeRefreshLayoutDetail.setRefreshing(false);
            }


            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                //出现页面错误的时候，不让webView显示了。同时跳出一个错误Toast
                mWebviewPretty.setVisibility(View.INVISIBLE);
                Toast.makeText(getApplicationContext(), "请检查您的网络设置", Toast.LENGTH_SHORT)
                     .show();
            }
        });

        mWebviewPretty.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
            }

            // 网页标题
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);

            }
        });


        WebSettings settings = mWebviewPretty.getSettings();
        settings.setDefaultTextEncodingName("utf-8");// 避免中文乱码
        settings.setCacheMode(WebSettings.LOAD_DEFAULT | WebSettings.LOAD_CACHE_ELSE_NETWORK);
        settings.setJavaScriptEnabled(true);

        settings.setNeedInitialFocus(false);

        settings.setSupportZoom(true);

        settings.setLoadWithOverviewMode(true);//适应屏幕

        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);

        settings.setLoadsImagesAutomatically(true);//自动加载图片

//        mWebviewPretty.loadUrl(mLinkUrl);
        loadData(mLinkUrl);
    }


    /**
     * 加载数据
     */
    private void loadData(String linkUrl) {
        OkHttpUtils.get()
                   .url(linkUrl)
                   .build()
                   .execute(new StringCallback() {
                       @Override
                       public void onError(Call call, Exception e, int id) {
                       }

                       @Override
                       public void onResponse(String response, int id) {
                           Document document = Jsoup.parse(response);
                           Elements elements = document.getElementsByClass("content-c");
                           String body = elements.html();
                           String html = getHtmlData(body);
                           mWebviewPretty.loadDataWithBaseURL(null, html, "text/html", "utf-8", null);
                           for (Element e : elements) {
                               System.out.println(e.toString());
                               Elements imgElements = e.getElementsByTag("img");
                               for (Element imgE : imgElements) {
                                   System.out.println(imgE.attr("src"));
                               }
                               Elements pElements = e.getElementsByTag("p");
                               for (Element pE : pElements) {
                                   System.out.println(pE.text());
                               }
                           }
                       }

                   });
    }

    // 注入js函数监听
    private void addImageClickListner() {
        // 这段js函数的功能就是，遍历所有的img几点，并添加onclick函数，函数的功能是在图片点击的时候调用本地java接口并传递url过去
        mWebviewPretty.loadUrl("javascript:(function(){" +
                "var objs = document.getElementsByTagName(\"img\"); " +
                "for(var i=0;i<objs.length;i++)  " +
                "{"
                + "    objs[i].onclick=function()  " +
                "    {  "
                + "        window.imagelistner.openImage(this.src);  " +
                "    }  " +
                "}" +
                "})()");
    }

    // js通信接口
    public class JavascriptInterface {

        private Context context;


        public JavascriptInterface(Context context) {
            this.context = context;
        }

        @android.webkit.JavascriptInterface
        public void openImage(String img) {
            System.out.println(img);
            Intent intent = new Intent();
            intent.putExtra("image", img);
            System.out.println(img);
        }
    }

    private String getHtmlData(String bodyHTML) {
        String head = "<head>" +
                "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0, user-scalable=no\"> " +
                "<style>img{max-width: 100%; width:auto; height:auto;}</style>" +
                "</head>";
        return "<html>" + head + "<body>" + bodyHTML + "</body></html>";
    }

}
