package com.longge.whathas.ui;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
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
import butterknife.OnClick;
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
    @BindView(R.id.fab_arrow_up)
    FloatingActionButton mFabArrowUp;

    private List<PicTabEntity> mListPicTabs = new ArrayList<>();
    private List<BasePicFragment> mListPicFragment = new ArrayList<>();
    private int mCurrentFragIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pic);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbarPic);
//        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("精美图片");
        NetUtils.loadHtml(UrlConstant.TU_PIAN, new StringCallback() {

            @Override
            public void onError(Call call, Exception e, int id) {

            }

            @Override
            public void onResponse(String response, int id) {
                Document document = Jsoup.parse(response);
                Elements elementsTith2 = document.getElementsByClass("page-cat-new");
                for (Element tith2 : elementsTith2) {
                    Elements elementsByTag = tith2.getElementsByTag("a");
                    Elements elements=tith2.getElementsByClass("tp_t");
                    for (int i = 0; i < elements.size(); i++) {
                        Element element = elements.get(i);
                        Element element1 = elementsByTag.get(i);
                        PicTabEntity picTabEntity = new PicTabEntity();
                        picTabEntity.linkUrl = UrlConstant.BASE_URL + element1.attr("href");//链接
                        picTabEntity.tabName = element.text();//tab名
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
     * @param listPicTabs 图片tab的描述
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
                mCurrentFragIndex = position;
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

    /**
     *
     * @param isVisiable FloatActionButton是否可见
     */
    public void setFbVisiable(boolean isVisiable) {
        if (isVisiable) {
            mFabArrowUp.setVisibility(View.VISIBLE);
        } else {
            mFabArrowUp.setVisibility(View.GONE);
        }
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

    @OnClick(R.id.fab_arrow_up)
    public void onClick() {
        mListPicFragment.get(mCurrentFragIndex).scrollUp();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
