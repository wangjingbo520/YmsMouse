package com.mouse.app.view.seekbar;

import android.graphics.drawable.StateListDrawable;

public interface PhasedAdapter {

    int getCount();

    StateListDrawable getItem(int position);

}
