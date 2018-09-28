package vip.frendy.ytplayer;

import android.content.Context;

import java.util.HashMap;
import java.util.Map;

import vip.frendy.ytplayer.interfaces.IYTJSListener;
import vip.frendy.ytplayer.interfaces.IYTWebViewListener;

/**
 * Created by frendy on 2017/11/21.
 */

public class YTPlayerManager implements IYTJSListener, IYTWebViewListener {

    private static YTPlayerManager mPlayerManager;

    public static YTPlayerManager getInstance() {
        if(mPlayerManager == null) {
            mPlayerManager = new YTPlayerManager();
        }
        return mPlayerManager;
    }


    private YTWebView mWebView;
    private boolean hasLoadDefault = false;
    private IYTJSListener mClientListener;
    private IYTJSListener mManagerListener;
    private HashMap<String, IYTJSListener> mOtherListeners = new HashMap<>();
    private float mCurrentTime = 0f, mTotalTime = 0f;

    public void init(Context context) {
        mWebView = new YTWebView(context);
        mWebView.setWebViewListener(this);
        mWebView.init(this);
    }

    @Override
    public void onPageFinished() {

    }

    public YTWebView getWebView() {
        if(!hasLoadDefault) {
            hasLoadDefault = true;
            if(mWebView != null) mWebView.loadDefault("");
        }

        return mWebView;
    }

    public void attachListener(IYTJSListener listener) {
        mClientListener = listener;
    }

    public void detachListener() {
        mClientListener = null;
    }

    public void attachManagerListener(IYTJSListener listener) {
        mManagerListener = listener;
    }

    public void detachManagerListener() {
        mManagerListener = null;
    }

    public void addOtherListener(String tag, IYTJSListener listener) {
        mOtherListeners.put(tag, listener);
    }

    public void removeOtherListener(String tag) {
        mOtherListeners.remove(tag);
    }

    @Override
    public void onYouTubeIframeAPIReady() {
        if(mClientListener != null)
            mClientListener.onYouTubeIframeAPIReady();
        if(mManagerListener != null)
            mManagerListener.onYouTubeIframeAPIReady();
        if(mOtherListeners != null && !mOtherListeners.isEmpty()) {
            for (Map.Entry<String, IYTJSListener> entry : mOtherListeners.entrySet()) {
                IYTJSListener listener = entry.getValue();
                listener.onYouTubeIframeAPIReady();
            }
        }
    }

    @Override
    public void updateVideoDuration(float duration) {
        mCurrentTime = duration;

        if(mClientListener != null)
            mClientListener.updateVideoDuration(duration);
        if(mManagerListener != null)
            mManagerListener.updateVideoDuration(duration);
        if(mOtherListeners != null && !mOtherListeners.isEmpty()) {
            for (Map.Entry<String, IYTJSListener> entry : mOtherListeners.entrySet()) {
                IYTJSListener listener = entry.getValue();
                listener.updateVideoDuration(duration);
            }
        }
    }

    @Override
    public void updateTotalVideoDuration(float duration) {
        mTotalTime = duration;

        if(mClientListener != null)
            mClientListener.updateTotalVideoDuration(duration);
        if(mManagerListener != null)
            mManagerListener.updateTotalVideoDuration(duration);
        if(mOtherListeners != null && !mOtherListeners.isEmpty()) {
            for (Map.Entry<String, IYTJSListener> entry : mOtherListeners.entrySet()) {
                IYTJSListener listener = entry.getValue();
                listener.updateTotalVideoDuration(duration);
            }
        }
    }

    @Override
    public void onReady() {
        if(mClientListener != null)
            mClientListener.onReady();
        if(mManagerListener != null)
            mManagerListener.onReady();
        if(mOtherListeners != null && !mOtherListeners.isEmpty()) {
            for (Map.Entry<String, IYTJSListener> entry : mOtherListeners.entrySet()) {
                IYTJSListener listener = entry.getValue();
                listener.onReady();
            }
        }
    }

    @Override
    public void onVideoUnStarted() {
        if(mClientListener != null)
            mClientListener.onVideoUnStarted();
        if(mManagerListener != null)
            mManagerListener.onVideoUnStarted();
        if(mOtherListeners != null && !mOtherListeners.isEmpty()) {
            for (Map.Entry<String, IYTJSListener> entry : mOtherListeners.entrySet()) {
                IYTJSListener listener = entry.getValue();
                listener.onVideoUnStarted();
            }
        }
    }

    @Override
    public void onVideoEnd() {
        if(mClientListener != null)
            mClientListener.onVideoEnd();
        if(mManagerListener != null)
            mManagerListener.onVideoEnd();
        if(mOtherListeners != null && !mOtherListeners.isEmpty()) {
            for (Map.Entry<String, IYTJSListener> entry : mOtherListeners.entrySet()) {
                IYTJSListener listener = entry.getValue();
                listener.onVideoEnd();
            }
        }
    }

    @Override
    public void onVideoPlaying() {
        if(mClientListener != null)
            mClientListener.onVideoPlaying();
        if(mManagerListener != null)
            mManagerListener.onVideoPlaying();
        if(mOtherListeners != null && !mOtherListeners.isEmpty()) {
            for (Map.Entry<String, IYTJSListener> entry : mOtherListeners.entrySet()) {
                IYTJSListener listener = entry.getValue();
                listener.onVideoPlaying();
            }
        }
    }

    @Override
    public void onVideoPaused() {
        if(mClientListener != null)
            mClientListener.onVideoPaused();
        if(mManagerListener != null)
            mManagerListener.onVideoPaused();
        if(mOtherListeners != null && !mOtherListeners.isEmpty()) {
            for (Map.Entry<String, IYTJSListener> entry : mOtherListeners.entrySet()) {
                IYTJSListener listener = entry.getValue();
                listener.onVideoPaused();
            }
        }
    }

    @Override
    public void onVideoBuffering() {
        if(mClientListener != null)
            mClientListener.onVideoBuffering();
        if(mManagerListener != null)
            mManagerListener.onVideoBuffering();
        if(mOtherListeners != null && !mOtherListeners.isEmpty()) {
            for (Map.Entry<String, IYTJSListener> entry : mOtherListeners.entrySet()) {
                IYTJSListener listener = entry.getValue();
                listener.onVideoBuffering();
            }
        }
    }

    @Override
    public void onVideoCued() {
        if(mClientListener != null)
            mClientListener.onVideoCued();
        if(mManagerListener != null)
            mManagerListener.onVideoCued();
        if(mOtherListeners != null && !mOtherListeners.isEmpty()) {
            for (Map.Entry<String, IYTJSListener> entry : mOtherListeners.entrySet()) {
                IYTJSListener listener = entry.getValue();
                listener.onVideoCued();
            }
        }
    }

    @Override
    public void onVideoStateCheckResult(int state, float current, float total) {
        if(mClientListener != null)
            mClientListener.onVideoStateCheckResult(state, current, total);
        if(mManagerListener != null)
            mManagerListener.onVideoStateCheckResult(state, current, total);
        if(mOtherListeners != null && !mOtherListeners.isEmpty()) {
            for (Map.Entry<String, IYTJSListener> entry : mOtherListeners.entrySet()) {
                IYTJSListener listener = entry.getValue();
                listener.onVideoStateCheckResult(state, current, total);
            }
        }
    }

    @Override
    public void onApiChange() {
        if(mClientListener != null)
            mClientListener.onApiChange();
        if(mManagerListener != null)
            mManagerListener.onApiChange();
        if(mOtherListeners != null && !mOtherListeners.isEmpty()) {
            for (Map.Entry<String, IYTJSListener> entry : mOtherListeners.entrySet()) {
                IYTJSListener listener = entry.getValue();
                listener.onApiChange();
            }
        }
    }

    @Override
    public void onError(int error) {
        if(mClientListener != null)
            mClientListener.onError(error);
        if(mManagerListener != null)
            mManagerListener.onError(error);
        if(mOtherListeners != null && !mOtherListeners.isEmpty()) {
            for (Map.Entry<String, IYTJSListener> entry : mOtherListeners.entrySet()) {
                IYTJSListener listener = entry.getValue();
                listener.onError(error);
            }
        }
    }

    @Override
    public void onPlaybackRateChange(double rate) {
        if(mClientListener != null)
            mClientListener.onPlaybackRateChange(rate);
        if(mManagerListener != null)
            mManagerListener.onPlaybackRateChange(rate);
        if(mOtherListeners != null && !mOtherListeners.isEmpty()) {
            for (Map.Entry<String, IYTJSListener> entry : mOtherListeners.entrySet()) {
                IYTJSListener listener = entry.getValue();
                listener.onPlaybackRateChange(rate);
            }
        }
    }

    @Override
    public void onPlaybackQualityChange(int playbackQuality) {
        if(mClientListener != null)
            mClientListener.onPlaybackQualityChange(playbackQuality);
        if(mManagerListener != null)
            mManagerListener.onPlaybackQualityChange(playbackQuality);
        if(mOtherListeners != null && !mOtherListeners.isEmpty()) {
            for (Map.Entry<String, IYTJSListener> entry : mOtherListeners.entrySet()) {
                IYTJSListener listener = entry.getValue();
                listener.onPlaybackQualityChange(playbackQuality);
            }
        }
    }

    public float getCurrentTime() {
        return mCurrentTime;
    }

    public float getTotalTime() {
        return mTotalTime;
    }
}
