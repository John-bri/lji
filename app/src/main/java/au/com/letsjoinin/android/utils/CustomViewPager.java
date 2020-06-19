package au.com.letsjoinin.android.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import androidx.viewpager.widget.ViewPager;
import au.com.letsjoinin.android.MVP.model.SignUpData;
import au.com.letsjoinin.android.UI.activity.LoginActivity;
import au.com.letsjoinin.android.UI.activity.SignUpActivity;

public class CustomViewPager extends ViewPager {

    public boolean enabled;
    SignUpActivity loginActivity;

    public CustomViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.enabled = true;
    }

    public void CustomViewPagerData(SignUpActivity login) {
        loginActivity = login;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (this.enabled) {
            return super.onTouchEvent(event);
        }

        return false;
    }
    float downX;
    private boolean wasSwipeToRightEvent(MotionEvent event){
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = event.getX();
//                return false;

            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_UP:
                return downX - event.getX() > 0;

            default:
                return false;
        }
    }
    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {

        boolean wasSwipeToRight = this.wasSwipeToRightEvent(event);

//        if (wasSwipeToRight && loginActivity != null) {
//
//            loginActivity.signUpValidation();
//        }else{
//            this.enabled = true;
//        }
//        Log.d("InterceptTouch","Event "+wasSwipeToRight);
        if (this.enabled) {
            return super.onInterceptTouchEvent(event);
        }

        return false;

    }

    public void setPagingEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}