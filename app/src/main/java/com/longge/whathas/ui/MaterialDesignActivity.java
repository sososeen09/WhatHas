package com.longge.whathas.ui;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.longge.whathas.R;
import com.longge.whathas.adapter.TextRecyclerAdapter;
import com.longge.whathas.base.BaseActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MaterialDesignActivity extends BaseActivity {

    @BindView(R.id.toolbar_design)
    Toolbar mToolbarDesign;
    @BindView(R.id.recyclerView_material)
    RecyclerView mRecyclerViewMaterial;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_material_design);
        ButterKnife.bind(this);
        initToolBar();
        initRecyclerView();
    }

    private void initRecyclerView() {
        List<String> strList = new ArrayList<>();
        for (int i = 0; i <30; i++) {
            strList.add("龙哥：" + i);
        }

        mRecyclerViewMaterial.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerViewMaterial.setAdapter(new TextRecyclerAdapter(strList));
    }

    private void initToolBar() {
        setSupportActionBar(mToolbarDesign);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//设置显示返回按钮
        getSupportActionBar().setDisplayShowTitleEnabled(true);//设置显示标题
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
