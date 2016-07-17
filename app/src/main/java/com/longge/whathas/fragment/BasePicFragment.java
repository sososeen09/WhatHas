package com.longge.whathas.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.longge.whathas.R;
import com.longge.whathas.adapter.PrettyGirlsRecyclerAdapter;
import com.longge.whathas.entity.PicEntity;
import com.longge.whathas.net.UrlConstant;
import com.longge.whathas.ui.PicActivity;
import com.longge.whathas.ui.PrettyDetailActivity;
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

public class BasePicFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    private static final String TAB_NAME = "tabName";
    private static final String LINK_URL = "linkUrl";

    private List<PicEntity> mPicEntityList = new ArrayList<>();
    private Activity mActivity;
    private StaggeredGridLayoutManager mLayoutManager;
    private PrettyGirlsRecyclerAdapter mPrettyGirlsRecyclerAdapter;
    private int mPage = 0;

    @BindView(R.id.recyclerView_main)
    RecyclerView mRecyclerViewMain;
    @BindView(R.id.swipe_refreshLayout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.fab_second)
    FloatingActionButton mFabSecond;

    // TODO: Rename and change types of parameters
    private String mTabName;
    private String mLinkUrl;
    private static final String TAG = BasePicFragment.class.getName();

    private boolean mPostRefresh = false;

    public BasePicFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param tabName Parameter 1.
     * @param linkUrl Parameter 2.
     * @return A new instance of fragment BasePicFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BasePicFragment newInstance(String tabName, String linkUrl) {
        BasePicFragment fragment = new BasePicFragment();
        Bundle args = new Bundle();
        args.putString(TAB_NAME, tabName);
        args.putString(LINK_URL, linkUrl);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mTabName = getArguments().getString(TAB_NAME);
            mLinkUrl = getArguments().getString(LINK_URL);
        }
        Log.d(TAG, "onCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pic, container, false);
        ButterKnife.bind(this, view);
        Log.d(TAG, "onCreateView");

        initView();
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = getActivity();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated");
        mFabSecond.setVisibility(View.GONE);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadData(true, getPage(true));
            }
        });
    }

    @OnClick(R.id.fab_second)
    public void onClick() {
        mRecyclerViewMain.scrollToPosition(0);
    }

    public void scrollUp() {
        mRecyclerViewMain.scrollToPosition(0);
    }


    private void initView() {
        mLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerViewMain.setLayoutManager(mLayoutManager);
        mPrettyGirlsRecyclerAdapter = new PrettyGirlsRecyclerAdapter(mActivity, mPicEntityList);
        mRecyclerViewMain.setAdapter(mPrettyGirlsRecyclerAdapter);

        mPrettyGirlsRecyclerAdapter.setonItemClickListener(new PrettyGirlsRecyclerAdapter.onItemClickListener() {
            @Override
            public void onItemClick(View view, int pos) {
                PicEntity picEntity = mPicEntityList.get(pos);
                Intent intent = new Intent(mActivity, PrettyDetailActivity.class);
                intent.putExtra("linkUrl", picEntity.linkUrl);
                startActivity(intent);
            }
        });

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


                if (dy < 0) {
//                    mFabSecond.setVisibility(View.VISIBLE);
                    ((PicActivity) getActivity()).setFbVisiable(true);
                } else {
//                    mFabSecond.setVisibility(View.GONE);
                    ((PicActivity) getActivity()).setFbVisiable(false);
                }

            }
        });
    }

    public void initLoad() {
        if (!mPostRefresh) {

            mSwipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    mSwipeRefreshLayout.setRefreshing(true);
                    loadData(true, getPage(true));
                }
            });
            mPostRefresh = true;
        }

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
     * @param isRefresh 是刷新请求
     */
    private void loadData(final boolean isRefresh, String page) {
//        http://www.wmpic.me/meinv/page/1
        String url = mLinkUrl + "/page/" + page;
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


                           if (mPrettyGirlsRecyclerAdapter != null) {
                               mPrettyGirlsRecyclerAdapter.setMoreStatus(PrettyGirlsRecyclerAdapter.PULLUP_LOAD_MORE);
                               mPrettyGirlsRecyclerAdapter.notifyDataSetChanged();
                           }
                           mSwipeRefreshLayout.setRefreshing(false);
                       }
                   });
    }
}
