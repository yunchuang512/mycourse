package com.example.xuxiao415.mycourse.Utils;

import android.content.Context;

/**
 * Created by Kai on 2016/10/11.
 */

public class MyUtils {

    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static int stringArray_match(String[] strings, int n, String string) {
        int i;
        for (i = 0; i < n; i++) {
            if (string.equals(strings[i]))
                break;
        }
        return i;
    }
}
