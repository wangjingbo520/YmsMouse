package com.mouse.app;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;

public class LauchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lauch);
        handler.sendEmptyMessageDelayed(200, 1000);
    }


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 200:
                   startActivity(new Intent(LauchActivity.this, MainActivity.class));
               //    startActivity(new Intent(LauchActivity.this, PlayActivity.class));
                    finish();
                    break;
                default:
                    break;
            }
        }
    };
}
