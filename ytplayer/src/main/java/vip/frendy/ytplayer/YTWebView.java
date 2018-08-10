package vip.frendy.ytplayer;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;

import java.util.HashMap;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import vip.frendy.ytplayer.interfaces.IYTLinkClickListner;
import vip.frendy.ytplayer.interfaces.IYTJSListener;
import vip.frendy.ytplayer.interfaces.IYTWebViewListener;
import vip.frendy.ytplayer.utils.PlayerUtils;

import static vip.frendy.ytplayer.Contants.DEBUG;

/**
 * Created by frendy on 2017/11/17.
 */

public class YTWebView extends WebView {
    public String TAG = "YTWebView";
    public static boolean BASEURL_VERSION = true;

    private Context mContext;
    protected boolean isProceedTouchEvent = false;
    protected String mDesiredQuality = "hd720";

    private IYTWebViewListener mListener;
    private IYTLinkClickListner mLinkListener;

    public YTWebView(Context context) {
        super(context);
        mContext = context;

        setLayoutParams(new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
    }

    public YTWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    @SuppressLint({"SetJavaScriptEnabled", "AddJavascriptInterface"})
    public void init(IYTJSListener listener) {
        WebSettings settings = getSettings();
        settings.setJavaScriptEnabled(true);
        if(Build.VERSION.SDK_INT >= 17) {
            settings.setMediaPlaybackRequiresUserGesture(false);
        }

        setWebChromeClient(new YTChromwClient());
        setWebViewClient(new YTWebviewClient());
        addJavascriptInterface(new YTJSInterface(listener), "android");

        if(!BASEURL_VERSION) {
            loadUrl("file:///android_asset/ytplayer.html");
        } else {
            loadDataWithBaseURL("https://www.youtube.com",
                    PlayerUtils.getPlayerHTML(mContext), "text/html", "utf-8", null);
        }
    }

    public void setWebViewListener(IYTWebViewListener listener) {
        mListener = listener;
    }

    public void setLinkClickListener(IYTLinkClickListner listener) {
        mLinkListener = listener;
    }

    @Override
    public void setOverScrollMode(int mode) {
        try{
            super.setOverScrollMode(mode);
        } catch(Throwable e){
            String trace = Log.getStackTraceString(e);
            if (trace.contains("android.content.pm.PackageManager$NameNotFoundException")
                    || trace.contains("java.lang.RuntimeException: Cannot load WebView")
                    || trace.contains("android.webkit.WebViewFactory$MissingWebViewPackageException: Failed to load WebView provider: No WebView installed")) {
                e.printStackTrace();
            }else{
                throw e;
            }
        }

    }

    /**
     * Player
     */

    public void loadDefault(String id) {
        if(!BASEURL_VERSION) {
            loadUrl("javascript:loadVideo('" + id + "')");
        }
    }

    public void loadVideoById(String id) {
        if(!BASEURL_VERSION) {
            loadUrl("javascript:loadVideoById('" + id + "')");
        } else {
            loadUrl("javascript:loadVideo('" + id +"', " + 0 +", '" + mDesiredQuality + "')");
        }
    }

    public void cueVideoById(String id) {
        if(!BASEURL_VERSION) {
            loadUrl("javascript:cueVideoById('" + id + "')");
        } else {
            loadUrl("javascript:cueVideo('" + id +"', " + 0 +")");
        }
    }

    public void playVideo() {
        loadUrl("javascript:playVideo()");
    }

    public void pauseVideo() {
        loadUrl("javascript:pauseVideo()");
    }

    public void stopVideo() {
        loadUrl("javascript:stopVideo()");
    }

    public void clearVideo() {
        loadUrl("javascript:clearVideo()");
    }

    public void seekVideo(double secs) {
        if(!BASEURL_VERSION) {
            loadUrl("javascript:seekVideo('"+ secs +"')");
        } else {
            loadUrl("javascript:seekTo(" + secs +")");
        }
    }

    public void checkVideoState() {
        loadUrl("javascript:checkVideoState()");
    }

    public void setSize(int w, int h) {
        loadUrl("javascript:setSize(" + w + "," + h + ")");
    }

    public void setPlaybackQuality(String quality) {
        loadUrl("javascript:setPlaybackQuality('"+ quality +"')");
    }

    public void setDesiredQuality(String quality) {
        mDesiredQuality = quality;
    }

    /**
     * Playlist
     */

    public void cuePlaylist(String list) {
        loadUrl("javascript:cuePlaylist('" + list + "')");
    }

    public void loadPlaylist(String list) {
        loadUrl("javascript:loadPlaylist('" + list + "')");
    }

    public void nextVideo() {
        loadUrl("javascript:nextVideo()");
    }

    public void previousVideo() {
        loadUrl("javascript:previousVideo()");
    }

    public void playVideoAt(int index) {
        loadUrl("javascript:playVideoAt(" + index + ")");
    }

    public void setLoop(boolean enable) {
        loadUrl("javascript:setLoop(" + enable + ")");
    }

    public void setShuffle(boolean enable) {
        loadUrl("javascript:setShuffle(" + enable + ")");
    }

    /**
     * Player Setting
     */

    public void mute() {
        loadUrl("javascript:mute()");
    }

    public void setVolume(int volume) {
        loadUrl("javascript:setVolume(" + volume + ")");
    }

    /**
     * Client Impl
     */

    private class YTWebviewClient extends WebViewClient {
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            if(mListener != null) mListener.onPageFinished();
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            if(DEBUG) Log.d(TAG, "onReceivedError : description = " + description);
            super.onReceivedError(view, errorCode, description, failingUrl);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if(DEBUG) Log.d(TAG, "shouldOverrideUrlLoading : url = " + url);
            if(mLinkListener != null) mLinkListener.onYTLinkClicked(url);
            return true;
        }

        @Override
        public void onLoadResource(WebView view, String url) {
            super.onLoadResource(view, url);
        }

        @SuppressWarnings("deprecation")
        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
            if(BASEURL_VERSION) {
                return super.shouldInterceptRequest(view, url);
            }

            if(url.contains("https://www.youtube.com/")) {
                return getNewResponse(url, new HashMap<String, String>());
            } else {
                return super.shouldInterceptRequest(view, url);
            }
        }

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
            if(BASEURL_VERSION) {
                return super.shouldInterceptRequest(view, request);
            }

            String url = request.getUrl().toString();
            if(url.contains("https://www.youtube.com/")) {
                return getNewResponse(url, request.getRequestHeaders());
            } else {
                return super.shouldInterceptRequest(view, request);
            }
        }

        private WebResourceResponse getNewResponse(String url, Map<String, String> requestHeaders) {

            try {
                OkHttpClient httpClient = new OkHttpClient();

                Request.Builder builder = new Request.Builder();
                builder.url(url.trim());
                builder.addHeader("Referer", "https://youtube.com");
                for(Map.Entry<String, String> header : requestHeaders.entrySet()) {
                    builder.addHeader(header.getKey(), header.getValue());
                }
                Request request = builder.build();

                Response response = httpClient.newCall(request).execute();

                return new WebResourceResponse(
                        null,
                        response.header("content-encoding", "utf-8"),
                        response.body().byteStream()
                );
            } catch (Exception e) {
                return null;
            }
        }
    }

    private class YTChromwClient extends WebChromeClient {
        @Override
        public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
            if(DEBUG) Log.d(TAG, "consoleMessage : " + consoleMessage.message());
            return super.onConsoleMessage(consoleMessage);
        }
    }


    public void setProceedTouchEvent(boolean enable) {
        isProceedTouchEvent = enable;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(isProceedTouchEvent) {
            //super.onTouchEvent(event);
            return false;
        }
        return super.onTouchEvent(event);
    }
}
