package au.com.letsjoinin.android.utils;

import android.animation.ArgbEvaluator;
import android.animation.LayoutTransition;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import androidx.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import au.com.letsjoinin.android.R;

/**
 * Created by lj on 3/29/2017.
 */

public class ViewSwitch extends RelativeLayout {
    public int currentGravity = Gravity.START;
    public TextView textView, textView1;
    public View blueView;
    public FrameLayout container;
    public int activeTextColor, inActiveTextColor;
    public SwitchStatusListner listner;

    public ViewSwitch(Context context) {
        super(context);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.layout_switch, this);
        blueView = findViewById(R.id.blue_oval);
        container = (FrameLayout) findViewById(R.id.container);
        textView = (TextView) findViewById(R.id.textView);
        textView1 = (TextView) findViewById(R.id.textView1);
        container.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentGravity == Gravity.START) {
                    animateColor(textView1, inActiveTextColor, activeTextColor);
                    animateColor(textView, activeTextColor, inActiveTextColor);
                    animateGravity(Gravity.END);
                    listner.onSwitchStatus(1);
                } else {
                    animateColor(textView, inActiveTextColor, activeTextColor);
                    animateColor(textView1, activeTextColor, inActiveTextColor);
                    animateGravity(Gravity.START);
                    listner.onSwitchStatus(0);
                }
            }
        });
    }

    private void animateColor(TextView view, int fromColor, int toColor) {
        ObjectAnimator anim = ObjectAnimator.ofInt(view, "textColor", fromColor, toColor);
        anim.setDuration(100);
        anim.setEvaluator(new ArgbEvaluator());
        anim.start();
    }

    private void animateGravity(int toGravity) {
        currentGravity = toGravity;
        LayoutTransition layoutTransition = container.getLayoutTransition();
        layoutTransition.enableTransitionType(LayoutTransition.CHANGING);
        layoutTransition.setDuration(100);
        layoutTransition.setInterpolator(LayoutTransition.CHANGING, new AccelerateDecelerateInterpolator());

        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) blueView.getLayoutParams();
        layoutParams.gravity = toGravity;
        blueView.setLayoutParams(layoutParams);
    }

    public ViewSwitch(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        init();
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.ViewSwitch, 0, 0);
        textView.setText(typedArray.getString(R.styleable.ViewSwitch_firstText));
        textView1.setText(typedArray.getString(R.styleable.ViewSwitch_secondText));
        setActiveTextColor(typedArray.getColor(R.styleable.ViewSwitch_activeTextColor, Color.WHITE));
        setInActiveTextColor(typedArray.getColor(R.styleable.ViewSwitch_inActiveTextColor, Color.parseColor("#592384")));
        currentGravity = typedArray.getInt(R.styleable.ViewSwitch_currentActive, 0) == 0 ? Gravity.START : Gravity.END;
        setBackgroundColor(typedArray.getColor(R.styleable.ViewSwitch_inActiveColor, Color.WHITE));
        setActiveColor(typedArray.getColor(R.styleable.ViewSwitch_activeColor, Color.parseColor("#592384")));
        if (currentGravity == Gravity.START) {
            textView.setTextColor(activeTextColor);
            textView1.setTextColor(inActiveTextColor);
            animateGravity(Gravity.START);
        } else {
            textView1.setTextColor(activeTextColor);
            textView.setTextColor(inActiveTextColor);
            animateGravity(Gravity.END);
        }
    }
public void setActive(boolean state)
{
    if (state) {
        animateColor(textView1, inActiveTextColor, activeTextColor);
        animateColor(textView, activeTextColor, inActiveTextColor);
        animateGravity(Gravity.END);
       // listner.onSwitchStatus(1);
    } else {
        animateColor(textView, inActiveTextColor, activeTextColor);
        animateColor(textView1, activeTextColor, inActiveTextColor);
        animateGravity(Gravity.START);
       // listner.onSwitchStatus(0);
    }
}
    public void setActiveTextColor(int activeTextColor) {
        this.activeTextColor = activeTextColor;
        if (currentGravity == Gravity.START) {
            textView.setTextColor(activeTextColor);
        } else {
            textView1.setTextColor(activeTextColor);
        }
    }

    public void setInActiveTextColor(int inActiveTextColor) {
        this.inActiveTextColor = inActiveTextColor;
        if (currentGravity == Gravity.START) {
            textView1.setTextColor(inActiveTextColor);
        } else {
            textView.setTextColor(inActiveTextColor);
        }
    }

    @Override
    public void setBackgroundColor(int color) {
        GradientDrawable gradientDrawable = (GradientDrawable) container.getBackground();
        gradientDrawable.setColor(color);
    }

    public void setActiveColor(int color) {
        GradientDrawable gradientDrawable = (GradientDrawable) blueView.getBackground();
        gradientDrawable.setColor(color);
    }

    public ViewSwitch(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public ViewSwitch(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    public int getCurrentPosition() {
        return currentGravity == Gravity.START ? 0 : 1;
    }

    public void setOnStatusChangeListener(SwitchStatusListner switchStatusListner) {
        this.listner = switchStatusListner;
    }

    public void setInActiveColor(int color) {
        setBackgroundColor(color);
    }

    public interface SwitchStatusListner {
        void onSwitchStatus(int status);
    }
}
