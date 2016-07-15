package com.longge.whathas.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;

import com.longge.whathas.R;
import com.longge.whathas.adapter.PrettyGirlsRecyclerAdapter;
import com.longge.whathas.entity.PicEntity;
import com.longge.whathas.net.UrlConstant;
import com.zhy.http.okhttp.OkHttpUtils;
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

public class PrettyGirlsActivity extends AppCompatActivity {
    @BindView(R.id.swipe_refreshLayout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.recyclerView_main)
    RecyclerView mRecyclerViewMain;
    @BindView(R.id.fab_second)
    FloatingActionButton mFabSecond;

    private List<PicEntity> mPicEntityList = new ArrayList<>();
    private Activity mActivity;
    private StaggeredGridLayoutManager mLayoutManager;
    private PrettyGirlsRecyclerAdapter mPrettyGirlsRecyclerAdapter;
    private int mPage = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prettygirls);
        ButterKnife.bind(this);
        mActivity = this;
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadData(true, getPage(true));
            }
        });
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(true);
                loadData(true, getPage(true));
            }
        });
        mFabSecond.setVisibility(View.GONE);

    }

    private String getPage(boolean isRefresh) {

        if (isRefresh) {
            return "1";
        } else {
            mPage++;
            return String.valueOf(mPage);
        }
    }

    /**
     * 加载数据
     *
     * @param isRefresh
     */
    private void loadData(final boolean isRefresh, String page) {
//        http://www.wmpic.me/meinv/page/1
        String url = UrlConstant.MEI_NV + page;
        OkHttpUtils.get()
                   .url(url)
                   .build()
                   .execute(new StringCallback() {
                       @Override
                       public void onError(Call call, Exception e, int id) {
                           mSwipeRefreshLayout.setRefreshing(false);
                       }

                       @Override
                       public void onResponse(String response, int id) {
                           if (isRefresh) {
                               mPicEntityList.clear();
                           }
                           Document document = Jsoup.parse(response);
                           Element element = document.getElementById("mainbox");

                           Elements elements = element.getElementsByClass("item_list");
                           for (Element e : elements) {
                               Elements item_boxs = e.getElementsByClass("item_box");
                               for (Element item_box : item_boxs) {
                                   Elements posts = item_box.getElementsByClass("post");
                                   for (Element post : posts) {
                                       PicEntity picEntity = new PicEntity();
                                       Elements a = post.getElementsByTag("a");

                                       String title = a.attr("title");
                                       String href = a.attr("href");
                                       picEntity.title = title;
                                       picEntity.linkUrl = UrlConstant.BASE_URL + href;

                                       Elements img = post.getElementsByTag("img");
                                       String src = img.attr("src");
                                       picEntity.imagePath = src;

                                       mPicEntityList.add(picEntity);


                                   }
                               }

                           }

                           for (int i = 0; i < mPicEntityList.size(); i++) {
                               System.out.println("title: " + mPicEntityList.get(i).title);
                               System.out.println("href: " + mPicEntityList.get(i).linkUrl);
                               System.out.println("src: " + mPicEntityList.get(i).imagePath);
                           }


                           if (mPrettyGirlsRecyclerAdapter == null) {
                               mPrettyGirlsRecyclerAdapter = new PrettyGirlsRecyclerAdapter(mActivity, mPicEntityList);
                               mPrettyGirlsRecyclerAdapter.setonItemClickListener(new PrettyGirlsRecyclerAdapter.onItemClickListener() {
                                   @Override
                                   public void onItemClick(View view, int pos) {
                                       PicEntity picEntity = mPicEntityList.get(pos);
//                                       Snackbar.make(view, picEntity.title, Snackbar.LENGTH_SHORT)
//                                               .show();
                                       Intent intent = new Intent(mActivity, PrettyDetailActivity.class);
                                       intent.putExtra("linkUrl", picEntity.linkUrl);
                                       startActivity(intent);
                                   }
                               });
//                               mLayoutManager = new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false);
//                               mLayoutManager = new GridLayoutManager(mActivity, 2, GridLayoutManager.VERTICAL, false);
                               mLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
                               mRecyclerViewMain.setLayoutManager(mLayoutManager);
                               mRecyclerViewMain.setAdapter(mPrettyGirlsRecyclerAdapter);
                               mRecyclerViewMain.addOnScrollListener(new RecyclerView.OnScrollListener() {
                                   private int lastVisiableItem;

                                   @Override
                                   public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                                       super.onScrollStateChanged(recyclerView, newState);
                                       if (newState == RecyclerView.SCROLL_STATE_IDLE && lastVisiableItem + 1 == mPrettyGirlsRecyclerAdapter
                                               .getItemCount
                                                       ()) {
                                           mPrettyGirlsRecyclerAdapter.setMoreStatus(PrettyGirlsRecyclerAdapter.LOADING_MORE);
                                           loadData(false, getPage(false));
                                       }
                                   }

                                   @Override
                                   public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                                       super.onScrolled(recyclerView, dx, dy);
                                       int[] items = new int[2];
                                       mLayoutManager.findLastVisibleItemPositions(items);
                                       lastVisiableItem = Math.max(items[0], items[1]);
                                       System.out.println("items[0]: " + items[0] + "--" + "items[1]: " + items[1] + "--lastVisiableItem: " +
                                               lastVisiableItem);
                                       System.out.println("dy: " + dy);
                                       if (dy < 0) {
                                           mFabSecond.setVisibility(View.VISIBLE);
                                       } else {
                                           mFabSecond.setVisibility(View.GONE);
                                       }

                                   }
                               });
                           } else {
                               mPrettyGirlsRecyclerAdapter.setMoreStatus(PrettyGirlsRecyclerAdapter.PULLUP_LOAD_MORE);
                               mPrettyGirlsRecyclerAdapter.notifyDataSetChanged();
                           }
                           mSwipeRefreshLayout.setRefreshing(false);
                       }
                   });
    }

    @OnClick(R.id.fab_second)
    public void onClick() {
        mRecyclerViewMain.scrollToPosition(0);
    }
}
