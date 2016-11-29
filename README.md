# LyricView
在音乐播放器项目中使用到酷狗音乐展示歌词的效果，需要支持修改字体、颜色、渐变色等功能

截图如下(截图效果不是很好,<a href="https://github.com/zhaodecang/LyricView.git">请下载Demo查看</a>)

![](/ScreenShot.gif)

使用方式：

	<com.zdc.lyricdemo.view.LyricView
			android:id="@+id/lvView"
			android:layout_width="match_parent"
			android:layout_height="420dp"
			android:background="@drawable/base_bg" />
然后在代码中初始化MediaPlayer、歌词、设置监听：

	public class TtActivity extends Activity implements OnClickListener {

		private Button btnPlayPause;
		private LyricView lvView;
		private MediaPlayer mediaPlayer = new MediaPlayer();
		private Handler handler = new Handler();

		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.activity_main);
			initView();
			initMediaPlayer();
		}

		private void initView() {
			btnPlayPause = (Button) findViewById(R.id.btn_play_pause);
			lvView = (LyricView) findViewById(R.id.lvView);
			btnPlayPause.setOnClickListener(this);
		}

		private void initMediaPlayer() {
			AssetManager am = getAssets();
			try {
				mediaPlayer.reset();
				mediaPlayer.setDataSource(am.openFd("cbg.mp3").getFileDescriptor());
				mediaPlayer.prepare();
			} catch (IOException e) {
				e.printStackTrace();
			}
			lvView.initLyricFile(LyricLoader.loadLyricFile("cbg"));
		}

		@Override
		public void onClick(View view) {
			if (!mediaPlayer.isPlaying()) {
				mediaPlayer.start();
				handler.post(runnable);
			} else {
				mediaPlayer.pause();
				handler.removeCallbacks(runnable);
			}
		}

		private Runnable runnable = new Runnable() {
			@Override
			public void run() {
				if (mediaPlayer.isPlaying()) {
					long time = mediaPlayer.getCurrentPosition();
					lvView.updateLyrics((int) time, mediaPlayer.getDuration());
				}
				handler.postDelayed(this, 100);
			}
		};

		@Override
		protected void onDestroy() {
			handler.removeCallbacks(runnable);
			mediaPlayer.reset();
			mediaPlayer.release();
			mediaPlayer = null;
			super.onDestroy();
		}
	}	
