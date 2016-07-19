package com.longge.whathas.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.longge.whathas.R;
import com.longge.whathas.base.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener, SearchView.OnQueryTextListener {

    @BindView(R.id.cardView_pic)
    CardView mCardViewPic;
    @BindView(R.id.cardView_material_design)
    CardView mCardViewMaterialDesign;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.coordinator_Layout_main)
    CoordinatorLayout mCoordinatorLayoutMain;
    @BindView(R.id.navigation_header_container)
    NavigationView mNavigationHeaderContainer;
    @BindView(R.id.drawer_layout_main)
    DrawerLayout mDrawerLayoutMain;
    private String mAppKey;
    private String mAppsercet;
    private String mCurrentTime;
    private String mNonce;
    private String mCheckSum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initToolBar();


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                startAct(PicActivity.class);
                            }
                        })
                        .show();
            }
        });

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayoutMain, mToolbar, R.string.navigation_open, R
                .string.navigation_close);
        mDrawerLayoutMain.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        mNavigationHeaderContainer.setNavigationItemSelectedListener(this);
    }

    private void initToolBar() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//显示图标
        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.ic_menu);//修改图标
        getSupportActionBar().setDisplayShowTitleEnabled(false);//隐藏标题
    }


    @Override
    public void onBackPressed() {
        if (mDrawerLayoutMain.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayoutMain.closeDrawer(GravityCompat.START);
            return;
        }
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
//        searchView.setOnQueryTextListener(this);
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
        } else if (id == android.R.id.home) {
            mDrawerLayoutMain.openDrawer(GravityCompat.START);
        }

        return super.onOptionsItemSelected(item);
    }


    @OnClick({R.id.cardView_pic, R.id.cardView_material_design})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cardView_pic:
                startAct(PicActivity.class);
                break;
            case R.id.cardView_material_design:
//                startAct(MaterialDesignActivity.class);
                startAct(ScrollingActivity.class);
                break;
        }
    }


    private void startAct(Class<? extends Activity> clazz) {
        Intent intent = new Intent(MainActivity.this, clazz);
        startActivity(intent);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        int itemId = item.getItemId();
        if (itemId == R.id.menu_item_setting) {
            Toast.makeText(this, "setting", Toast.LENGTH_SHORT).show();
        }

        mDrawerLayoutMain.closeDrawer(GravityCompat.START);

        return true;

    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }
}
