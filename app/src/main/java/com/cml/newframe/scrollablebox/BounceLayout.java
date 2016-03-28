package com.cml.newframe.scrollablebox;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.PointF;
import android.graphics.RectF;
import android.os.Build;
import android.support.v4.view.ScrollingView;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.AbsListView;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by cmlBeliever on 2016/3/25.
 * <p>弹性layout</p>
 */
public class BounceLayout extends LinearLayout {

    private static final String TAG = BounceLayout.class.getSimpleName();
    private static final int DIRECTION_X = 1;
    private static final int DIRECTION_Y = 2;
    private static final int DIRECTION_NONE = 3;

    /**
     * 可移动比率 默认为0.5
     */
    private float transRatio = 0.5f;

    /**
     * 手指按下的坐标
     */
    private PointF initPoint = new PointF();

    private Animator translateAnim;
    private int direction = DIRECTION_NONE;

    //手指按下时所在的view
    private View touchView;
    private Map<RectF, ViewGroup> positionMap = new HashMap<>();

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
                        if (Math.abs(dx) <= getMeasuredWidth() * transRatio) {
                            ViewCompat.setTranslationX(getChildAt(0), dx);
                        }
                    } else if (direction == DIRECTION_Y) {
                        if (Math.abs(dy) <= getMeasuredHeight() * transRatio) {
                            ViewCompat.setTranslationY(getChildAt(0), dy);
                        }
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
                touchView = findViewByPosition(ev.getRawX(), ev.getRawY());
                Log.d(TAG, "onInterceptTouchEvent===>touchView:" + touchView);
                break;
            case MotionEvent.ACTION_MOVE:
                float dx = ev.getX() - initPoint.x;
                float dy = ev.getY() - initPoint.y;

                Log.d(TAG, "onInterceptTouchEvent===>ACTION_MOVE touchView:" + touchView);

                if (direction == DIRECTION_NONE) {
                    //x方向移动
                    if (Math.abs(dx) > Math.abs(dy) && Math.abs(dx) > 10) {
                        direction = DIRECTION_X;
                    } else if (Math.abs(dy) > 10) {
                        direction = DIRECTION_Y;
                    }
                }

                //垂直方向移动
                if (direction == DIRECTION_Y && canChildScrollVertically((int) -dy)) {
                    MotionEvent event = MotionEvent.obtain(ev);
                    event.setAction(MotionEvent.ACTION_CANCEL);
                    onTouchEvent(event);
                    return super.onInterceptTouchEvent(ev);
                }

                if (direction == DIRECTION_X && canChildScrollHorizontally((int) -dx)) {
                    MotionEvent event = MotionEvent.obtain(ev);
                    event.setAction(MotionEvent.ACTION_CANCEL);
                    onTouchEvent(event);
                    return super.onInterceptTouchEvent(ev);
                }

                break;
        }


        return direction == DIRECTION_NONE ? super.onInterceptTouchEvent(ev) : true;
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

        if (changed) {
            // 保存可滚动对象
            positionMap.clear();
            saveChildRect(this);
        }

    }

    private View findViewByPosition(float x, float y) {

        for (RectF rectF : positionMap.keySet()) {
            if (rectF.left <= x && x <= rectF.right && rectF.top <= y && y <= rectF.bottom) {
                return positionMap.get(rectF);
            }
        }

        return null;
    }

    /**
     * 找出所有可滚动的view，并存储位置
     *
     * @param view
     */
    private void saveChildRect(ViewGroup view) {


        if (view instanceof ScrollView || view instanceof HorizontalScrollView || view instanceof ScrollingView || view instanceof AbsListView) {

            int[] location = new int[2];
//            view.getLocationOnScreen(location);
            view.getLocationInWindow(location);

            RectF rectF = new RectF();
            rectF.left = location[0];
            rectF.top = location[1];
            rectF.right = rectF.left + view.getMeasuredWidth();
            rectF.bottom = rectF.top + view.getMeasuredHeight();

            Log.d(TAG, "saveChildRect===>(" + rectF);

            positionMap.put(rectF, view);
        } else {
            int childCount = view.getChildCount();
            for (int i = 0; i < childCount; i++) {
                if (view.getChildAt(i) instanceof ViewGroup) {
                    this.saveChildRect((ViewGroup) view.getChildAt(i));
                }
            }
        }
    }


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
        return touchView == null ? false : ViewCompat.canScrollVertically(touchView, direction);
    }


    /**
     * @param direction 负数：向左滑动 else 向右滑动
     * @return
     */
    private boolean canChildScrollHorizontally(int direction) {
        return touchView == null ? false : ViewCompat.canScrollHorizontally(touchView, direction);
    }

    public float getTransRatio() {
        return transRatio;
    }

    /**
     * 设置偏移部分百分比
     *
     * @param transRatio 0-1 ,1 100%距离
     */
    public void setTransRatio(float transRatio) {
        this.transRatio = transRatio;
    }
}
