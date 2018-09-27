package com.mouse.app.view.seekbar;

import android.view.MotionEvent;

public interface PhasedInteractionListener {

    void onInteracted(int x, int y, int position, MotionEvent motionEvent);

}
