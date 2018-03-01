package com.example.pna.authencationsocial;

import android.content.Context;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

/**
 * Created by PNA on 28/02/2018.
 */

public class MyScale extends View implements View.OnTouchListener,ScaleGestureDetector.OnScaleGestureListener {
    Context context;

    ScaleGestureDetector scaleGestureDetector;
    private float mScaleVal = 1.0f;

    public MyScale(Context context) {
        super(context);
    }


    @Override
    public boolean onScale(ScaleGestureDetector detector) {
        mScaleVal*=detector.getScaleFactor();

        setScaleX(mScaleVal);
        setScaleY(mScaleVal);
        return true;
    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {
        // Return true here to tell the ScaleGestureDetector we
        // are in a scale and want to continue tracking.
        return true;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector detector) {
        // We don't care about end events, but you could handle this if
        // you wanted to write finished values or interact with the user
        // when they are finished.
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        // Pass our events to the scale gesture detector first.
        boolean handled = scaleGestureDetector.onTouchEvent(event);

        // If the scale gesture detector didn't handle the event,
        // pass it to super.
//        if (!handled) {
//            handled = super.onTouchEvent(event);
//        }

        return handled;
    }
}
