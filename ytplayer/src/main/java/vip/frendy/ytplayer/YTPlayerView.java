package vip.frendy.ytplayer;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import vip.frendy.ytplayer.extension.HandlerExt;
import vip.frendy.ytplayer.interfaces.IYTJSListener;
import vip.frendy.ytplayer.model.PlaylistItems;

import static vip.frendy.ytplayer.Contants.DEBUG;

/**
 * Created by frendy on 2017/11/17.
 */

public class YTPlayerView<T> extends LinearLayout implements IYTJSListener, View.OnClickListener {
    protected static String TAG = "YTPlayerView";

    protected ImageButton mPlayPause;
    protected PlayerState mState = PlayerState.ENDED;
    public enum PlayerState {
        ENDED, PLAYING, PAUSED, BUFFERING, CUED
    };

    protected final static int MAX = 1000;
    protected SeekBar mSeekBar;
    protected float totalVideoDuration;

    protected LinearLayout mContent;
    protected YTWebView mWebView;
    //播放单个视频
    protected String mVideoId = "";

    protected boolean isProceedTouchEvent = false;

    protected ICheckVideoStateListener mCheckVideoStateListener;
    protected IQualityChangeListener mQualityChangeListener;
    protected SeekBar.OnSeekBarChangeListener mSeekBarChangeListener;

    public YTPlayerView(Context context) {
        super(context);
        init(context);
    }

    public YTPlayerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public YTPlayerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    // 继承时可替换布局
    protected int getLayoutResId() {
        return R.layout.yt_player_view;
    }

    protected void init(Context context) {
        LayoutInflater.from(context).inflate(getLayoutResId(), this);

        mPlayPause = findViewById(R.id.play_pause);
        mPlayPause.setOnClickListener(this);

        mSeekBar = findViewById(R.id.seek_bar);
        mSeekBar.setMax(MAX);

        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int progress = seekBar.getProgress();
                double secs = (progress * totalVideoDuration) / 1000;
                secs = Math.ceil(secs);
                if(mWebView != null) mWebView.seekVideo(secs);

                if(mSeekBarChangeListener != null)
                    mSeekBarChangeListener.onStopTrackingTouch(seekBar);

                if(DEBUG) Log.d(TAG, "onStopTrackingTouch :: progress = " + progress +  "-- secs = " + secs);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if(mSeekBarChangeListener != null)
                    mSeekBarChangeListener.onStartTrackingTouch(seekBar);

                if(DEBUG) Log.d(TAG, "onStartTrackingTouch :: progress = " + seekBar.getProgress() );
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(mSeekBarChangeListener != null)
                    mSeekBarChangeListener.onProgressChanged(seekBar, progress, fromUser);
            }
        });

        mContent = findViewById(R.id.webview_content);
    }

    public void attachWebView(YTWebView webView) {
        mWebView = webView;
        mContent.addView(mWebView);
    }

    public void detachWebView() {
        mContent.removeView(mWebView);
    }

    public void setVideoId(String id) {
        mVideoId = id;
    }

    public void loadDefault() {
        if(mWebView != null) mWebView.loadDefault(mVideoId);
    }

    public void loadVideoById(String id) {
        mVideoId = id;
        if(mWebView != null) mWebView.loadVideoById(mVideoId);
    }

    public void cueVideoById(String id) {
        mVideoId = id;
        if(mWebView != null) mWebView.cueVideoById(id);
    }

    public void stopVideo() {
        if(mWebView != null) mWebView.stopVideo();
    }

    public void clearVideo() {
        if(mWebView != null) mWebView.clearVideo();
    }

    public void pauseVideo() {
        if(mWebView != null) mWebView.pauseVideo();
    }

    public void playVideo() {
        if(mWebView != null) mWebView.playVideo();
    }

    public void setSize(int w, int h) {
        if(mWebView != null) mWebView.setSize(w, h);
    }

    // 展开
    public void rollout() {
        mSeekBar.setVisibility(VISIBLE);
        mPlayPause.setVisibility(VISIBLE);
    }

    // 收起
    public void rollup() {
        mSeekBar.setVisibility(GONE);
        mPlayPause.setVisibility(GONE);
    }

    public boolean isRollup() {
        return mSeekBar.getVisibility() == GONE;
    }


    public void setProceedTouchEvent(boolean enable) {
        isProceedTouchEvent = enable;
        if(mWebView != null) mWebView.setProceedTouchEvent(enable);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(isProceedTouchEvent) {
            super.onTouchEvent(event);
            return false;
        }
        return super.onTouchEvent(event);
    }

    public boolean isVideoPlaying() {
        return mState == PlayerState.PLAYING;
    }

    public boolean isVideoBuffering() {
        return mState == PlayerState.BUFFERING;
    }

    public boolean isVideoPaused() {
        return mState == PlayerState.PAUSED;
    }

    @Override
    public void onYouTubeIframeAPIReady() {

    }

    @Override
    public void updateVideoDuration(float duration) {
        try {
            changeSeekBar(duration);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateTotalVideoDuration(float duration) {
        totalVideoDuration = duration;
    }

    @Override
    public void onReady() {

    }

    @Override
    public void onVideoUnStarted() {
        updatePlayPuaseButton(mState);
    }

    @Override
    public void onVideoEnd() {
        mState = PlayerState.ENDED;
        setSeekBar("ENDED");
        updatePlayPuaseButton(mState);
    }

    @Override
    public void onVideoPlaying() {
        mState = PlayerState.PLAYING;
        updatePlayPuaseButton(mState);
    }

    @Override
    public void onVideoPaused() {
        mState = PlayerState.PAUSED;
        updatePlayPuaseButton(mState);
    }

    @Override
    public void onVideoBuffering() {
        mState = PlayerState.BUFFERING;
        updatePlayPuaseButton(mState);
    }

    @Override
    public void onVideoCued() {
        mState = PlayerState.CUED;
        updatePlayPuaseButton(mState);
    }

    public void checkVideoState(ICheckVideoStateListener listener) {
        mCheckVideoStateListener = listener;
        if(mWebView != null) {
            mWebView.checkVideoState();
        }
    }

    @Override
    public void onVideoStateCheckResult(int state, float current, float total) {
        switch (state) {
            case -1: break;
            case 0: mState = PlayerState.ENDED; break;
            case 1: mState = PlayerState.PLAYING; break;
            case 2: mState = PlayerState.PAUSED; break;
            case 3: mState = PlayerState.BUFFERING; break;
            case 5: mState = PlayerState.CUED; break;
            default: break;
        }

        totalVideoDuration = total;
        changeSeekBar(current);
        updatePlayPuaseButton(mState);

        HandlerExt.postToUI(new Runnable() {
            @Override
            public void run() {
                if(mCheckVideoStateListener != null) {
                    mCheckVideoStateListener.onResult(mState);
                    mCheckVideoStateListener = null;
                }
            }
        });
    }

    @Override
    public void onApiChange() {

    }

    @Override
    public void onError(int error) {

    }

    @Override
    public void onPlaybackRateChange(double rate) {

    }

    @Override
    public void onPlaybackQualityChange(int playbackQuality) {
        if(mQualityChangeListener != null)
            mQualityChangeListener.onQualityChange(playbackQuality);
    }

    public float getTotalVideoDuration() {
        return totalVideoDuration;
    }

    protected void changeSeekBar(float time){
        float progress =  (time/totalVideoDuration) * 1000;
        final double d = Math.ceil(progress);

        if(DEBUG) Log.d(TAG, "changeSlider progress = " + d);

        HandlerExt.postToUI(new Runnable() {
            @Override
            public void run() {
                mSeekBar.setProgress((int)d);
            }
        });
    }

    protected void setSeekBar(final String flag){
        HandlerExt.postToUI(new Runnable() {
            @Override
            public void run() {
                if(flag.equals("ENDED"))
                    mSeekBar.setProgress(MAX);
            }
        });
    }

    protected void updatePlayPuaseButton(final PlayerState state) {
        if(mPlayPause == null) return;

        HandlerExt.postToUI(new Runnable() {
            @Override
            public void run() {
                if(state == PlayerState.PLAYING || state == PlayerState.BUFFERING) {
                    mPlayPause.setImageResource(android.R.drawable.ic_media_pause);
                } else {
                    mPlayPause.setImageResource(android.R.drawable.ic_media_play);
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.play_pause && mWebView != null) {
            if(mState == PlayerState.PLAYING || mState == PlayerState.BUFFERING) {
                mWebView.pauseVideo();
            } else {
                mWebView.playVideo();
            }
        }
    }

    public void setSeekBarChangeListener(SeekBar.OnSeekBarChangeListener listener) {
        this.mSeekBarChangeListener = listener;
    }

    public void setQualityChangeListener(IQualityChangeListener listener) {
        this.mQualityChangeListener = listener;
    }

    public interface ICheckVideoStateListener {
        void onResult(PlayerState state);
    }

    public interface IQualityChangeListener {
        void onQualityChange(int playbackQuality);
    }
}
