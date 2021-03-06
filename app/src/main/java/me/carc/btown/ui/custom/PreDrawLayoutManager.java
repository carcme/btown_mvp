package me.carc.btown.ui.custom;

/**
 * Created by Carc.me on 10.04.16.
 * <p/>
 * Layout manager - add smooth scroll and pre-draw for recyclerview
 */

import android.content.Context;
import android.graphics.PointF;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;

import com.crashlytics.android.Crashlytics;

public class PreDrawLayoutManager extends LinearLayoutManager {
    private static final float MILLISECONDS_PER_INCH = 50f;
    private static final int DEFAULT_EXTRA_LAYOUT_SPACE = 600;
    private Context mContext;
    private int extraLayoutSpace = -1;


    public PreDrawLayoutManager(Context context) {
        super(context);
        mContext = context;
    }

    public PreDrawLayoutManager(Context context, int extraLayoutSpace) {
        super(context);
        mContext = context;
        this.extraLayoutSpace = extraLayoutSpace;
    }

    public PreDrawLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
        mContext = context;
    }

    @Override
    public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, final int position) {

        LinearSmoothScroller smoothScroller = new LinearSmoothScroller(mContext) {

            //This controls the direction in which smoothScroll looks for your view
            @Override
            public PointF computeScrollVectorForPosition
            (int targetPosition) {
                return PreDrawLayoutManager.this.computeScrollVectorForPosition(targetPosition);
            }

            //This returns the milliseconds it takes to scroll one pixel.
            @Override
            protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
                return MILLISECONDS_PER_INCH / displayMetrics.densityDpi;
            }
        };

        smoothScroller.setTargetPosition(position);
        startSmoothScroll(smoothScroller);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        try {
            super.onLayoutChildren(recycler, state);
        } catch (IndexOutOfBoundsException e) {
            Crashlytics.log(PreDrawLayoutManager.class.getName() + " : IndexOutOfBoundsException : " + e.getMessage());
        }
    }

    public void setExtraLayoutSpace(int extraLayoutSpace) {
        this.extraLayoutSpace = extraLayoutSpace;
    }

    @Override
    protected int getExtraLayoutSpace(RecyclerView.State state) {
        if (extraLayoutSpace > 0) {
            return extraLayoutSpace;
        }
        return DEFAULT_EXTRA_LAYOUT_SPACE;
    }
}