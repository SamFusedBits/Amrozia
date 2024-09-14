package com.globalfashion.amrozia;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
// This class is used to create a nested RecyclerView that can be used in the main RecyclerView
public class NestedRecyclerView extends RecyclerView {
    // Constructors for the NestedRecyclerView class
    public NestedRecyclerView(Context context) {
        super(context);
    }

    // Constructor with context and attributes
    public NestedRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    // Constructor with context, attributes, and default style
    public NestedRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    // Measure the view and its content to determine the measured width and the measured height
    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        super.onMeasure(widthSpec, MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 1, MeasureSpec.AT_MOST));
    }
}