package com.cml.newframe.scrollablebox;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.PointF;
import android.os.Build;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.Toast;

/**
 * Created by cmlBeliever on 2016/3/25.
 * <p>弹性layout</p>
 */
public class BounceLayout extends FrameLayout {

    private static final String TAG = BounceLayout.class.getSimpleName();
    private static final int DIRECTION_X = 1;
    private static final int DIRECTION_Y = 2;
    private static final int DIRECTION_NONE = 3;

    private PointF initPoint = new PointF();

    private Animator translateAnim;
    private int direction = DIRECTION_NONE;

    public BounceLayout(Context context) {
        super(context);
        this.init();
    }

    public BounceLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.init();
    }

    public BounceLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public BounceLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.init();
    }

    private void init() {
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "点击了view", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_MOVE:
                float dx = ev.getX() - initPoint.x;
                float dy = ev.getY() - initPoint.y;

                if (direction == DIRECTION_NONE) {
                    //x方向移动
                    if (Math.abs(dx) > Math.abs(dy) && Math.abs(dx) > 10) {
                        direction = DIRECTION_X;
                    } else if (Math.abs(dy) > 10) {
                        direction = DIRECTION_Y;
                    }
                } else {
                    //x方向移动
                    if (direction == DIRECTION_X) {
                        ViewCompat.setTranslationX(getChildAt(0), dx);
                    } else if (direction == DIRECTION_Y) {
                        ViewCompat.setTranslationY(getChildAt(0), dy);
                    }
                    return true;
                }

                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                dx = ViewCompat.getTranslationX(getChildAt(0));
                dy = ViewCompat.getTranslationY(getChildAt(0));

                if (direction == DIRECTION_X) {
                    startTransAnim(DIRECTION_X, dx, 0);
                } else if (direction == DIRECTION_Y) {
                    startTransAnim(DIRECTION_Y, dy, 0);
                }

                if (direction != DIRECTION_NONE) {
                    direction = DIRECTION_NONE;
                    return true;
                }

                break;
        }


        return super.onTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                initPoint.x = ev.getX();
                initPoint.y = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                float dx = ev.getX() - initPoint.x;
                float dy = ev.getY() - initPoint.y;

                if (direction == DIRECTION_NONE) {
                    //x方向移动
                    if (Math.abs(dx) > Math.abs(dy) && Math.abs(dx) > 10) {
                        direction = DIRECTION_X;
                    } else if (Math.abs(dy) > 10) {
                        direction = DIRECTION_Y;
                    }
                }

                if (direction == DIRECTION_Y && !canChildScrollVertically((int) dy)) {
                    MotionEvent event = MotionEvent.obtain(ev);
                    event.setAction(MotionEvent.ACTION_CANCEL);
                    onTouchEvent(event);
                    return super.onInterceptTouchEvent(ev);
                }

                if (direction == DIRECTION_X && !canChildScrollHorizontally((int) dx)) {
                    MotionEvent event = MotionEvent.obtain(ev);
                    event.setAction(MotionEvent.ACTION_CANCEL);
                    onTouchEvent(event);
                    return super.onInterceptTouchEvent(ev);
                }

                break;
        }


        return direction == DIRECTION_NONE ? super.onInterceptTouchEvent(ev) : true;
    }

//    public boolean daaispatchTouchEvent(MotionEvent ev) {
//
////        Log.d(TAG, "dispatchTouchEvent===>:" + ev.getAction());
//
//        switch (ev.getAction()) {
//            case MotionEvent.ACTION_DOWN:
//                initPoint.x = ev.getX();
//                initPoint.y = ev.getY();
//                break;
//            case MotionEvent.ACTION_MOVE:
//                float dx = ev.getX() - initPoint.x;
//                float dy = ev.getY() - initPoint.y;
//
//                if (direction == DIRECTION_NONE) {
//                    //x方向移动
//                    if (Math.abs(dx) > Math.abs(dy) && Math.abs(dx) > 10) {
//                        direction = DIRECTION_X;
//                    } else if (Math.abs(dy) > 10) {
//                        direction = DIRECTION_Y;
//                    }
//                } else {
//                    //x方向移动
//                    if (direction == DIRECTION_X) {
////                        if (!canChildScrollHorizontally((int) dx)) {
//                        ViewCompat.setTranslationX(getChildAt(0), dx);
////                        } else {
////                            direction = DIRECTION_NONE;
////                        }
//
//                    } else if (direction == DIRECTION_Y) {
////                        if (!canChildScrollVertically((int) dy)) {
//                        ViewCompat.setTranslationY(getChildAt(0), dy);
////                        } else {
////                            direction = DIRECTION_NONE;
////                        }
//
//                    }
//                }
//
//                break;
//            case MotionEvent.ACTION_UP:
//            case MotionEvent.ACTION_CANCEL:
//                dx = ViewCompat.getTranslationX(getChildAt(0));
//                dy = ViewCompat.getTranslationY(getChildAt(0));
//
//                if (direction == DIRECTION_X) {
//                    startTransAnim(DIRECTION_X, dx, 0);
//                } else if (direction == DIRECTION_Y) {
//                    startTransAnim(DIRECTION_Y, dy, 0);
//                }
//
//                if (direction != DIRECTION_NONE) {
//                    direction = DIRECTION_NONE;
//                    return true;
//                }
//
////                Log.d(TAG, "dispatchTouchEvent===>ACTION_UP:" + dx + "," + dy);
//                break;
//        }
//
//        return super.dispatchTouchEvent(ev);
//    }


    private void startTransAnim(int direction, float start, float end) {
        if (translateAnim != null && translateAnim.isRunning()) {
            translateAnim.end();
        }
        String proName = direction == DIRECTION_X ? "translationX" : "translationY";
        translateAnim = ObjectAnimator.ofFloat(getChildAt(0), proName, start, end);
        translateAnim.setInterpolator(new AccelerateDecelerateInterpolator());
        translateAnim.start();
    }


    /**
     * @param direction 负数：向上滑动 else 向下滑动
     * @return
     */
    private boolean canChildScrollVertically(int direction) {

        View child = getChildAt(0);

        if (child instanceof ViewGroup) {
            ViewGroup childGroup = (ViewGroup) child;
            for (int i = 0; i < childGroup.getChildCount(); i++) {
                View childView = childGroup.getChildAt(i);

                if (ViewCompat.canScrollVertically(childView, direction)) {
                    return true;
                }
            }
        }

        return ViewCompat.canScrollVertically(getChildAt(0), direction);
    }


    /**
     * @param direction 负数：向左滑动 else 向右滑动
     * @return
     */
    private boolean canChildScrollHorizontally(int direction) {
        View child = getChildAt(0);

        if (child instanceof ViewGroup) {
            ViewGroup childGroup = (ViewGroup) child;
            for (int i = 0; i < childGroup.getChildCount(); i++) {
                if (ViewCompat.canScrollHorizontally(childGroup.getChildAt(i), direction)) {
                    return true;
                }
            }
        }
        return ViewCompat.canScrollHorizontally(getChildAt(0), direction);
    }

}
