package com.longge.whathas.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.longge.whathas.R;
import com.longge.whathas.base.BaseActivity;
import com.longge.whathas.entity.PicTabEntity;
import com.longge.whathas.net.NetUtils;
import com.longge.whathas.net.UrlConstant;
import com.zhy.http.okhttp.callback.StringCallback;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;

public class MainActivity extends BaseActivity {

    @BindView(R.id.cardView_pic)
    CardView mCardViewPic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                startAct();
                            }
                        })
                        .show();
            }
        });


        NetUtils.loadHtml(UrlConstant.TU_PIAN, new StringCallback() {
            final List<PicTabEntity> mListPicTabs = new ArrayList<>();
            @Override
            public void onError(Call call, Exception e, int id) {

            }

            @Override
            public void onResponse(String response, int id) {
                Document document = Jsoup.parse(response);
                Elements elementsTith2 = document.getElementsByClass("tith2");
                for (Element tith2 : elementsTith2) {
                    Elements elementsByTag = tith2.getElementsByTag("a");
                    for (Element tagA : elementsByTag) {
                        PicTabEntity picTabEntity = new PicTabEntity();
                        picTabEntity.linkUrl = UrlConstant.BASE_URL + tagA.attr("href");//链接
                        picTabEntity.tabName = tagA.text();//tab名
                        mListPicTabs.add(picTabEntity);
                    }
                }
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @OnClick({R.id.cardView_pic,})
    public void onClick() {
        startAct();
    }

    private void startAct() {
        Intent intent = new Intent(MainActivity.this, PrettyGirlsActivity.class);
        startActivity(intent);
    }

}
