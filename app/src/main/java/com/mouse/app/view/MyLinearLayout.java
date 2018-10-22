package com.mouse.app.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.LinearLayout;

/**
 * class
 *
 * @author bobo
 * @date 2018/10/22
 */
public class MyLinearLayout extends LinearLayout {
    public MyLinearLayout(Context context) {
        super(context);
    }

    public MyLinearLayout(Context context,  AttributeSet attrs) {
        super(context, attrs);
    }

    public MyLinearLayout(Context context,  AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

//    public MyLinearLayout(Context context) {
//        super(context);
//    }


//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        int action = event.getAction();
//        Log.e("----->LinearLayout", "onTouchEvent: "+action);
//        if (MotionEvent.ACTION_OUTSIDE==action){
//
//        }
//        return true;
//    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        Log.e("----->LinearLayout", "onTouchEvent: "+action);
        if (MotionEvent.ACTION_OUTSIDE==action){

        }
        return true;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        int action = event.getAction();
        Log.e("----->LinearLayout", "dispatchTouchEvent: "+action);
        if (MotionEvent.ACTION_OUTSIDE==action){

        }
        return true;
    }
}
