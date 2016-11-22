package com.zdc.lyricdemo.view;

import java.io.File;
import java.util.ArrayList;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.widget.TextView;

import com.example.lyricdemo.R;
import com.zdc.lyricdemo.bean.LyricBean;
import com.zdc.lyricdemo.utils.LrcUtil;

/**
 * 描述：
 * <p>
 * Created by zhaodecang on 2016-11-22下午7:02:39
 * <p>
 * 邮箱：zhaodecang@gmail.com
 */
public class LyricView extends TextView {
	private int viewWidth;
	private int viewHeight;
	private float currentLyricSize;
	private float defaultLyricSize;
	private float defaultLineHight;
	private Paint paint;
	private int currentLyricIndex = 0;
	private ArrayList<LyricBean> lyrics = new ArrayList<LyricBean>();
	private int currentPosition;
	private int duration = 0;
	private float passedPercent;
	private int mCurrentColor;
	private int mGradientEndColor;
	private int mNormalColor;
	private int[] colors = { Color.GREEN, Color.WHITE };

	public LyricView(Context context) {
		super(context);
	}

	public LyricView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initLyricInfo(attrs);
	}

	public LyricView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initLyricInfo(attrs);
	}

	private void initLyricInfo(AttributeSet attrs) {
		// 获取布布局文件中设置的值
		TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.LrcView);
		defaultLyricSize = ta.getDimension(R.styleable.LrcView_lrcTextSize, LrcUtil.sp2px(getContext(), 14));
		mCurrentColor = ta.getColor(R.styleable.LrcView_lrcGradientStartColor, 0xff00ff00);
		mGradientEndColor = ta.getColor(R.styleable.LrcView_lrcGradientStartColor, 0xffffffff);
		mNormalColor = ta.getColor(R.styleable.LrcView_lrcNormalTextColor, 0xFFFF00FF);
		// 初始化其他相关的值
		currentLyricSize = defaultLyricSize + LrcUtil.sp2px(getContext(), 3.0f);
		colors[0] = mCurrentColor;
		colors[1] = mGradientEndColor;
		defaultLineHight = LrcUtil.dp2px(getContext(), 24.0f);
		paint = new Paint();
		// 设置字体边缘平滑
		paint.setAntiAlias(true);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		// 获取view的宽度和高度
		viewWidth = w;
		viewHeight = h;
		super.onSizeChanged(w, h, oldw, oldh);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if (lyrics != null && lyrics.size() > 0) {
			canvas.translate(0, -smoothScrollLyric());
			drawAllLyric(canvas);
		}
	}

	private void drawAllLyric(Canvas canvas) {
		for (int i = 0; i < lyrics.size(); i++) {
			drawSingleLyric(canvas, i);
		}
	}

	private void drawSingleLyric(Canvas canvas, int i) {
		Rect rect = new Rect();
		String text = lyrics.get(i).lyric;
		// 判断当前歌词是否是正在唱的 如果是 需要高亮 设置大字体 颜色为绿色
		if (i == currentLyricIndex) {
			paint.setColor(mCurrentColor);
			paint.setTextSize(currentLyricSize);
			// 这个方法执行之后 文字的边界都保存到了rect对象中
			paint.getTextBounds(text, 0, text.length(), rect);
			// 文字宽度
			int textWidth = rect.width();
			// 文字高度
			int textHeight = rect.height();
			// 计算 文字在View中的坐标
			float x = viewWidth / 2 - textWidth / 2;
			float y = viewHeight / 2 + textHeight / 2;
			// 添加渐变效果
			// LinearGradient 线性渐变的效果 前两个参数 线性渐变开始的位置 第三个四个参数 线性渐变结束的坐标
			// 第五个参数 int数组 线性渐变用到的颜色 第六个参数 float数组 取值0-1之间 第一个元素对应第一种颜色
			// 第二个元素对应第一个颜色到第二个颜色过渡的距离
			// 最后一个参数 模式
			float[] positions = new float[] { passedPercent, passedPercent + 0.01f };
			paint.setShader(new LinearGradient(x, y, x + textWidth, y, colors, positions, Shader.TileMode.CLAMP));
			canvas.drawText(text, x, y, paint);
		} else {
			paint.setColor(mNormalColor);
			paint.setTextSize(defaultLyricSize);
			paint.getTextBounds(text, 0, text.length(), rect);
			int textWidth = rect.width();
			int textHeight = rect.height();
			float x = viewWidth / 2 - textWidth / 2;
			float y = viewHeight / 2 + textHeight / 2 + (i - currentLyricIndex) * defaultLineHight;
			// 取消渐变的效果
			paint.setShader(null);
			canvas.drawText(text, x, y, paint);
		}
	}

	public void updateCurrentLyricIndex(int currentPosition) {
		// 遍历集合
		for (int i = 0; i < lyrics.size(); i++) {
			// 如果唱到最后一句歌词 当前高亮的歌词就是最后一句
			if (i == lyrics.size() - 1) {
				currentLyricIndex = i;
				break;
			}
			int time = lyrics.get(i).time;
			if (time < currentPosition && currentPosition < lyrics.get(i + 1).time) {
				// 更新当前行的索引
				currentLyricIndex = i;
				break;
			}
		}
	}

	/**
	 * 更新歌词的进度
	 * 
	 * @param currentPosition 当前的进度
	 * @param duration 歌曲的总时长{@value} >0
	 **/
	public void updateLyrics(int currentPosition, int duration) {
		if (this.duration == 0)
			this.duration = duration;
		this.currentPosition = currentPosition;
		updateCurrentLyricIndex(currentPosition);
		invalidate();
	}

	/**
	 * 平滑滚动歌词
	 */
	private float smoothScrollLyric() {
		// 如果是最后一个行 处理越界问题
		// 获取当前歌词的时间
		int time = lyrics.get(currentLyricIndex).time;
		int totalTime;
		if (currentLyricIndex == lyrics.size() - 1) {
			totalTime = duration - time;
		} else {
			totalTime = lyrics.get(currentLyricIndex + 1).time - time;
		}
		// 获取当前歌词已经唱了多久
		int passedTime = currentPosition - time;
		passedPercent = passedTime / (float) totalTime;
		// 获取当前歌词一共要唱多久
		return defaultLineHight * passedTime / (float) totalTime;
	}

	/** 初始化歌词 **/
	public void initLyricFile(File file) {
		// 解析歌词 保存到集合
		lyrics = LyricsParser.parserFromFile(file);
	}
}
