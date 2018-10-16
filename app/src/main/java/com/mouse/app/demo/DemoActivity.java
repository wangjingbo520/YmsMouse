package com.mouse.app.demo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.mouse.app.R;

public class DemoActivity extends AppCompatActivity {
    private VerticalSeekBar verticalSeekBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);
        verticalSeekBar = findViewById(R.id.verticalSeekBar);
    }
}
