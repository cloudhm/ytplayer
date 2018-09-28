package vip.frendy.ytplayer;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;

import com.yanzhenjie.recyclerview.swipe.SwipeItemClickListener;

import java.util.ArrayList;

import vip.frendy.ytplayer.extension.PraseHelper;

/**
 * Created by frendy on 2017/11/19.
 */

public class YTPlayerListView<T> extends YTPlayerView<T> implements SwipeItemClickListener {
    private static String TAG = "YTPlayerListView";

    protected ImageButton mPlayNext, mPlayPrev;

    //播放列表
    protected ArrayList<T> mVideoList = new ArrayList<>();
    protected int mIndex = 0;
    protected PraseHelper<T> mPraseHelper = new PraseHelper<>();

    //主播放标记，在与容器配合使用时，通过容器来控制，不需要回调
    protected boolean isMainPlayer = false;

    public enum PlayListState {
        LIST_ORDER,
        LIST_LOOP,
        LIST_SHUFFLE,
        SINGLE_LOOP
    }
    protected PlayListState mPlayListState = PlayListState.LIST_ORDER;
    protected IVideoChangedListener mVideoChangedListener;


    public YTPlayerListView(Context context) {
        super(context);
    }

    public YTPlayerListView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public YTPlayerListView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.yt_player_list_view;
    }

    @Override
    protected void init(Context context) {
        super.init(context);

        mPlayNext = findViewById(R.id.play_next);
        mPlayNext.setOnClickListener(this);
        mPlayPrev = findViewById(R.id.play_prev);
        mPlayPrev.setOnClickListener(this);
    }

    public void setVideoList(ArrayList<T> list) {
        mVideoList.clear();
        mVideoList.addAll(list);
    }

    public void cueVideoListAt(int index) {
        if(mVideoList.size() <= 0) return;

        mIndex = index % mVideoList.size();
        cueVideoById(mPraseHelper.getVideoId(mVideoList.get(mIndex)));
    }

    public void playVideoListAt(int index) {
        if(mVideoList.size() <= 0) return;

        mIndex = index % mVideoList.size();
        loadVideoById(mPraseHelper.getVideoId(mVideoList.get(mIndex)));
    }

    public void playingVideoListAt(int index) {
        if(mVideoList.size() <= 0) return;

        mIndex = index % mVideoList.size();
    }

    public int getPlayingVideoIndex() {
        return mIndex;
    }

    @Override
    public void rollout() {
        super.rollout();
    }

    @Override
    public void rollup() {
        super.rollup();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(isProceedTouchEvent) {
            super.onTouchEvent(event);
            return false;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public void onItemClick(View itemView, int position) {
        mIndex = position;
        if(mWebView != null) mWebView.loadVideoById(getVideoId(position));
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);

        if(view.getId() == R.id.play_next && mVideoList.size() > 0 && mWebView != null) {
            if(isVideoPlaying()) {
                mWebView.loadVideoById(getNextVideoId());
            } else {
                mWebView.cueVideoById(getNextVideoId());
            }
        } else if(view.getId() == R.id.play_prev && mVideoList.size() > 0 && mWebView != null) {
            if(isVideoPlaying()) {
                mWebView.loadVideoById(getPrevVideoId());
            } else {
                mWebView.cueVideoById(getPrevVideoId());
            }
        }
    }

    protected String getNextVideoId() {
        if(mVideoList.size() > 0)
            mIndex = (++ mIndex) % mVideoList.size();
        T video = mVideoList.get(mIndex);

        return mPraseHelper.getVideoId(video);
    }

    protected String getPrevVideoId() {
        if(mVideoList.size() > 0)
            mIndex = (-- mIndex + mVideoList.size()) % mVideoList.size();
        T video = mVideoList.get(mIndex);

        return mPraseHelper.getVideoId(video);
    }

    protected String getVideoId(int position) {
        T video = mVideoList.get(position);

        return mPraseHelper.getVideoId(video);
    }

    public void playRandomVideo() {
        mIndex = mPraseHelper.getRandomIndex(mIndex, mVideoList.size());
        if(mWebView != null && mVideoList.size() > mIndex) {
            mWebView.loadVideoById(mPraseHelper.getVideoId(mVideoList.get(mIndex)));
        }
    }

    public void setPlayListState(PlayListState state) {
        mPlayListState = state;
    }

    @Override
    public void onVideoEnd() {
        super.onVideoEnd();
        if(mWebView == null || isMainPlayer) return;

        switch (mPlayListState) {
            case LIST_ORDER:
                mWebView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(mIndex < (mVideoList.size() - 1)) {
                            mWebView.loadVideoById(getNextVideoId());
                        } else {
                            mWebView.cueVideoById(getNextVideoId());
                        }
                        if(mVideoChangedListener != null) {
                            mVideoChangedListener.onNextVideo(mVideoList, mIndex);
                        }
                    }
                }, 100);
                break;
            case LIST_LOOP:
                mWebView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mWebView.loadVideoById(getNextVideoId());
                        if(mVideoChangedListener != null) {
                            mVideoChangedListener.onNextVideo(mVideoList, mIndex);
                        }
                    }
                }, 100);
                break;
            case LIST_SHUFFLE:
                mWebView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        playRandomVideo();
                        if(mVideoChangedListener != null) {
                            mVideoChangedListener.onNextVideo(mVideoList, mIndex);
                        }
                    }
                }, 100);
                break;
            case SINGLE_LOOP:
                mWebView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mWebView.loadVideoById(mPraseHelper.getVideoId(mVideoList.get(mIndex)));
                        if(mVideoChangedListener != null) {
                            mVideoChangedListener.onNextVideo(mVideoList, mIndex);
                        }
                    }
                }, 100);
                break;
            default:
                break;
        }
    }

    public void setMainPlayer(boolean isMain) {
        this.isMainPlayer = isMain;
    }

    // 增
    public void addVideos(ArrayList<T> videos) {
        ArrayList<T> list = new ArrayList<>();
        list.addAll(mVideoList);
        list.addAll(videos);
        setVideoList(list);
    }

    // 删
    public void removeVideos(ArrayList<T> videos) {
        for(T video : videos) {
            for(T item : mVideoList) {
                if(mPraseHelper.getVideoId(video).equals(mPraseHelper.getVideoId(item))) {
                    mVideoList.remove(item);
                    break;
                }
            }
        }
    }

    // 改
    public void updateVideo(String video) {

    }

    // 查
    public ArrayList<T> queryVideos() {
        return new ArrayList<>();
    }


    public void setVideoChangedListener(IVideoChangedListener<T> listener) {
        mVideoChangedListener = listener;
    }

    public interface IVideoChangedListener<T> {
        void onNextVideo(ArrayList<T> videos, int index);
    }
}
