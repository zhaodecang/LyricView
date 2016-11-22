package com.zdc.lyricdemo.utils;

import android.content.Context;

/**
 * 描述：
 * <p>
 * Created by zhaodecang on 2016-11-22下午7:01:58
 * <p>
 * 邮箱：zhaodecang@gmail.com
 */
public class LrcUtil {
	public static int dp2px(Context context, float dpValue) {
		float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	public static int sp2px(Context context, float spValue) {
		float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
		return (int) (spValue * fontScale + 0.5f);
	}
}
