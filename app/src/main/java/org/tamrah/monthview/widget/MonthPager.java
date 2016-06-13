package org.tamrah.monthview.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;

/**
 * Created by abdullah on 5/11/16.
 */
public class MonthPager extends ViewPager {
    public MonthPager(Context context) {
        super(context);
    }

    public MonthPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setChildrenDrawingOrderEnabledCompat(boolean enable) {
        setChildrenDrawingOrderEnabled(enable);
    }
}
