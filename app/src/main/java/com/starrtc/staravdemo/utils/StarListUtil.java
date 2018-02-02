package com.starrtc.staravdemo.utils;

import android.view.View;
import android.widget.AbsListView;

/**
 * Created by zhangjt on 2017/9/14.
 */

public class StarListUtil {
    public static boolean isListViewReachTopEdge(final AbsListView listView) {
        boolean result=false;
        if(listView.getFirstVisiblePosition()==0){
            final View topChildView = listView.getChildAt(0);
            result=topChildView.getTop()==0;
        }
        return result ;
    }
    public static boolean isListViewReachBottomEdge(final AbsListView listView) {
        boolean result = false;
        if (listView.getLastVisiblePosition() == (listView.getCount() - 1)) {
            final View bottomChildView = listView.getChildAt(listView.getLastVisiblePosition() - listView.getFirstVisiblePosition());
            result = (listView.getHeight() >= bottomChildView.getBottom());
        };
        return result;
    }
}
