package vip.frendy.ytdemo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import java.util.ArrayList;

import vip.frendy.ytdemo.interfaces.IYTJSListener;

public class YoutubeListActivity extends Activity implements IYTJSListener {

	String TAG = "YoutubeActivity";
	WebView webView;
	SeekBar seekBar;
	float totalVideoDuration;
	final static int MAX = 1000;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_youtube_list);
		
		seekBar = (SeekBar) this.findViewById(R.id.seekBar1);
		seekBar.setMax(MAX);
		
		seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				int progress = seekBar.getProgress();
				double secs = (progress * totalVideoDuration)/1000;
				secs = Math.ceil(secs);
				Log.d(TAG, "onStopTrackingTouch :: progress = " + progress +  "-- secs = " + secs);
				webView.loadUrl("javascript:seekVideo('"+ secs +"')");
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				Log.d(TAG, "onStartTrackingTouch :: progress = " + seekBar.getProgress() );
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

			}
		});
		
		loadVideo();
	}

	@SuppressLint("JavascriptInterface")
	private void loadVideo(){
		webView = (WebView) this.findViewById(R.id.webView);
		
		WebSettings settings = webView.getSettings();
		settings.setJavaScriptEnabled(true);

		webView.setWebChromeClient(new MyChromwClient());
		webView.setWebViewClient(new MyWebviewClient());
		webView.addJavascriptInterface(new YTJSInterface(this), "android");
		webView.loadUrl("file:///android_asset/ytplayer.html");
	}
	
	private void changeSlider(float time){
		float progress =  (time/totalVideoDuration) * 1000;
		final double d = Math.ceil(progress);
		Log.d(TAG, "changeSlider progress = " + d);
		this.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				seekBar.setProgress((int)d);
			}
		});
	}

	private void modifySlider(final String flag){
		this.runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				if(flag.equals("ENDED"))
					seekBar.setProgress(MAX);
			}
		});
	}

	private int mIndex = 0;
	private ArrayList<String> mVideoIds = new ArrayList<String>() {{
		//dmLSM9zM0ME - 59 secs video
		//M7lc1UVf-VE - iframe API video
		//H6SShCF58-U
		//3tmd-ClpJxA ：被禁止？
		add("dmLSM9zM0ME");
		add("M7lc1UVf-VE");
		add("H6SShCF58-U");
	}};

	public void load(View view) {
		String id = "H6SShCF58-U";
		webView.loadUrl("javascript:loadVideo('" + id + "')");
	}

	public void cueList(View view) {
		String list = mVideoIds.toString();
		list = list.substring(1, list.length()-1);
		webView.loadUrl("javascript:cuePlaylist('" + list + "')");
	}

	public void loadList(View view) {
		String list = mVideoIds.toString();
		list = list.substring(1, list.length()-1);
		webView.loadUrl("javascript:loadPlaylist('" + list + "')");
	}

	public void nextVideo(View view) {
		webView.loadUrl("javascript:nextVideo()");
	}

	public void previousVideo(View view) {
		webView.loadUrl("javascript:previousVideo()");
	}

	public void playVideoAt(View view) {
		int index = 0;
		webView.loadUrl("javascript:playVideoAt(" + index + ")");
	}

	public void loop(View view) {
		webView.loadUrl("javascript:setLoop(" + true + ")");
	}

	public void shuffle(View view) {
		webView.loadUrl("javascript:setShuffle(" + true + ")");
	}

	@Override
	public void setVideoDuration(String duration) {
		try {
			changeSlider(Float.parseFloat(duration));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void setTotalVideoDuration(String duration) {
		try {
			totalVideoDuration = Float.parseFloat(duration);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void videoEnd() {
		modifySlider("ENDED");
	}

	private class MyWebviewClient extends WebViewClient{
		@Override
		public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
			super.onReceivedError(view, errorCode, description, failingUrl);
			Log.d(TAG, "onReceivedError : description = " + description);
		}
		
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			Log.d(TAG, "shouldOverrideUrlLoading : url = " + url);
			return true;
		}
	}
	
	private class MyChromwClient extends WebChromeClient{
		@Override
		public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
			Log.d(TAG, "consoleMessage : " + consoleMessage.message());
			return super.onConsoleMessage(consoleMessage);
		}
	}


}