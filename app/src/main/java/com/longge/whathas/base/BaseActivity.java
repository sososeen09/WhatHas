package com.longge.whathas.base;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.longge.whathas.R;

public class BaseActivity extends AppCompatActivity {

    public static String TAG = BaseActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
    }
}
