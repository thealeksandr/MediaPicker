package com.thealeksandr.mediapicker.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

/**
 * Created by Aleksandr Nikiforov on 2/14/17.
 */

public class AutofitRecyclerView extends RecyclerView {

    private int mColumnWidth = 0;
    private GridLayoutManager mManager;

    /**
     * @see android.support.v7.widget.RecyclerView#RecyclerView(android.content.Context)
     */
    public AutofitRecyclerView(Context context) {
        super(context);
    }

    /**
     * @see android.support.v7.widget.RecyclerView#RecyclerView(android.content.Context,
     * AttributeSet)
     */
    public AutofitRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }


    /**
     * @see android.support.v7.widget.RecyclerView#RecyclerView(android.content.Context,
     * AttributeSet, int)
     */
    public AutofitRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            int[] attrsArray = {
                    android.R.attr.columnWidth
            };
            TypedArray array = context.obtainStyledAttributes(
                    attrs, attrsArray);
            mColumnWidth = array.getDimensionPixelSize(0, -1);
            array.recycle();
        }

        mManager = new GridLayoutManager(getContext(), 1);
        setLayoutManager(mManager);
    }

    protected void onMeasure(int widthSpec, int heightSpec) {
        super.onMeasure(widthSpec, heightSpec);
        if (mColumnWidth > 0) {
            int spanCount = Math.max(1, getMeasuredWidth() / mColumnWidth);
            mManager.setSpanCount(spanCount);
        }
    }
}

