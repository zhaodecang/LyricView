package com.zdc.lyricdemo.bean;

/**
 * 描述：
 * <p>
 * Created by zhaodecang on 2016-11-22下午7:02:13
 * <p>
 * 邮箱：zhaodecang@gmail.com
 */
public class LyricBean implements Comparable<LyricBean> {
	public int time;
	public String lyric;

	public LyricBean(int time, String lyric) {
		this.time = time;
		this.lyric = lyric;
	}

	@Override
	public int compareTo(LyricBean o) {
		// 时间小的放在前面
		return this.time - o.time;
	}
}
