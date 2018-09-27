package com.mouse.app;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        findViewById(R.id.tv1).setOnClickListener(this);
        findViewById(R.id.tv2).setOnClickListener(this);
        findViewById(R.id.tv3).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv1:
                startActivity(new Intent(this, MenuActivity.class));
                break;
            case R.id.tv2:
                startActivity(new Intent(this, GuideActivity.class));
                break;
            case R.id.tv3:
                startActivity(new Intent(this, BleActivity.class));
                break;
            default:
                break;
        }

    }
}
