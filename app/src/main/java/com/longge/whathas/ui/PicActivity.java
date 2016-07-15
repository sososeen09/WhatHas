package com.longge.whathas.ui;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.longge.whathas.BuildConfig;
import com.longge.whathas.R;
import com.longge.whathas.entity.PicTabEntity;
import com.longge.whathas.fragment.BasePicFragment;
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
import okhttp3.Call;

public class PicActivity extends AppCompatActivity {


    @BindView(R.id.tab_layout_pic)
    TabLayout mTabLayoutPic;
    @BindView(R.id.view_pager_pic)
    ViewPager mViewPagerPic;
    @BindView(R.id.loading_progress)
    ProgressBar mLoadingProgress;
    @BindView(R.id.toolbar_pic)
    Toolbar mToolbarPic;

    private List<PicTabEntity> mListPicTabs = new ArrayList<>();
    private List<BasePicFragment> mListPicFragment = new ArrayList<>();
    private String[] mTabNames = {"唯美图片", "小清新图", "意境图片", "美女图片", "搞笑图片", "空间图片", "明星图片"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pic);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbarPic);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        NetUtils.loadHtml(UrlConstant.TU_PIAN, new StringCallback() {

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

                if (mListPicTabs.size() > 0) {
                    initViewPagerData(mListPicTabs);
                }
            }

        });
    }

    /**
     * 初始化ViewPager数据
     *
     * @param listPicTabs
     */
    private void initViewPagerData(List<PicTabEntity> listPicTabs) {

        for (int i = 0; i < listPicTabs.size(); i++) {
            PicTabEntity picTabEntity = listPicTabs.get(i);
            BasePicFragment basePicFragment = BasePicFragment.newInstance(picTabEntity.tabName, picTabEntity.linkUrl);
            mListPicFragment.add(basePicFragment);
            TabLayout.Tab tab = mTabLayoutPic.newTab()
                                             .setText(picTabEntity.tabName);
            if (i == 0) {
                mTabLayoutPic.addTab(tab, true);
            } else {
                mTabLayoutPic.addTab(tab, false);
            }
        }
        mLoadingProgress.setVisibility(View.GONE);
        if (BuildConfig.DEBUG) Log.d("PicActivity.initViewPagerData", "fragment create complete！");
        PicPagerAdapter picPagerAdapter = new PicPagerAdapter(getSupportFragmentManager(), mListPicFragment);
        mViewPagerPic.setAdapter(picPagerAdapter);
        mTabLayoutPic.setupWithViewPager(mViewPagerPic);
        mTabLayoutPic.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();

                mViewPagerPic.setCurrentItem(position);
                mListPicFragment.get(position)
                                .initLoad();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        //人为的加载第一页界面数据
        mListPicFragment.get(0)
                        .initLoad();
    }


    class PicPagerAdapter extends FragmentPagerAdapter {
        private final List<BasePicFragment> listPicFragment;

        public PicPagerAdapter(FragmentManager fm, List<BasePicFragment> listPicFragment) {
            super(fm);
            this.listPicFragment = listPicFragment;
        }

        @Override
        public Fragment getItem(int position) {
            return listPicFragment.get(position);
        }

        @Override
        public int getCount() {
            return listPicFragment.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mListPicTabs.get(position).tabName;
        }
    }


}