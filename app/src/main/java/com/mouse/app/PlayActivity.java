package com.mouse.app;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.mouse.app.view.seekbar.PhasedSeekBar;
import com.mouse.app.view.seekbar.SimplePhasedAdapter;


public class PlayActivity extends AppCompatActivity {
    private PhasedSeekBar seekbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        seekbar = findViewById(R.id.seekbar);
        final Resources resources = getResources();
        seekbar.setAdapter(new SimplePhasedAdapter(resources, new int[]{
                R.drawable.btn_like_selector,
                R.drawable.btn_unlike_selector}));
    }
}
