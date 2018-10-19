package com.zbmf.newmatch.fragment.video;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bokecc.sdk.mobile.play.DWMediaPlayer;
import com.zbmf.newmatch.R;
import com.zbmf.newmatch.activity.VideoPlayActivity;
import com.zbmf.newmatch.bean.Video;
import com.zbmf.newmatch.common.Constans;
import com.zbmf.newmatch.common.IntentKey;
import com.zbmf.newmatch.dialog.VideoDialog;
import com.zbmf.newmatch.fragment.BaseFragment;
import com.zbmf.newmatch.util.DateUtil;
import com.zbmf.newmatch.util.LogUtil;
import com.zbmf.newmatch.util.NetWorkUtil;
import com.zbmf.newmatch.util.ParamsUtil;
import com.zbmf.worklibrary.presenter.BasePresenter;

import java.util.Timer;
import java.util.TimerTask;


/**
 * Created by xuhao on 2017/7/27.
 */

public class VideoFragment extends BaseFragment implements
        MediaPlayer.OnErrorListener, MediaPlayer.OnVideoSizeChangedListener,
        MediaPlayer.OnInfoListener, SurfaceHolder.Callback,
        MediaPlayer.OnCompletionListener,
        MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnPreparedListener, View.OnClickListener {
    LinearLayout playerBottomLayout;
    ProgressBar bufferProgressBar;
    RelativeLayout rlPlay;
    ImageView rePlayFullScreen;
    SurfaceView videoSurface;
    RelativeLayout playerTopLayout;
    RelativeLayout videoCenterLayout;
    ImageView ivCenterPlay;
    ImageView replayPlayIcon;
    ImageView ivLock;
    TextView tvVideoGroupName;
    TextView tvVideoName;
    TextView videoPeople;
    TextView playDuration;
    SeekBar skbProgress;
    TextView videoDuration;
    private View fragment_back_view;
    private Video video;
    private VideoPlayActivity activity;

    private WindowManager wm;
    private ConnectivityManager cm;
    private GestureDetector detector;

    private DWMediaPlayer player;
    boolean isBackupPlay = false;
    private int currentPlayPosition;
    private int currentVolume;
    private int maxVolume;
    private AudioManager audioManager;
    private Handler playerHandler;
    private float scrollTotalDistance;
    // 控制播放器按钮显示
    private boolean isDisplay = false;
    private boolean isPrepared;
    private boolean isFreeze = false,isPlaying;
    private VideoDialog dialog;

    private SurfaceHolder surfaceHolder;

    public static VideoFragment newInstance(Video video) {
        VideoFragment fragment = new VideoFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(IntentKey.VIDEO_KEY, video);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (VideoPlayActivity) getActivity();
        if (getArguments() != null) {
            video = (Video) getArguments().getSerializable(IntentKey.VIDEO_KEY);
        }
        initSystem();
    }

    public void RushVideo(Video videobean) {
        video = videobean;
    }

    @Override
    protected int getLayout() {
        return R.layout.fragment_video_layout;
    }

    private void initSystem() {
        wm = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
        cm = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        detector = new GestureDetector(activity, new MyGesture());

        audioManager = (AudioManager) activity.getSystemService(Context.AUDIO_SERVICE);
        maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);

    }

    @Override
    protected void initView() {
        rePlayFullScreen = (ImageView) getView(R.id.replay_full_screen);
        videoSurface = (SurfaceView)getView(R.id.video_surface);
        playerTopLayout = (RelativeLayout) getView(R.id.playerTopLayout);
        videoCenterLayout = (RelativeLayout) getView(R.id.video_center_layout);
        ivCenterPlay = (ImageView) getView(R.id.iv_center_play);
        replayPlayIcon = (ImageView) getView(R.id.replay_play_icon);
        ivLock = (ImageView) getView(R.id.iv_lock);
        tvVideoGroupName = (TextView) getView(R.id.tv_video_group_name);
        tvVideoName = (TextView) getView(R.id.tv_video_name);
        videoPeople = (TextView) getView(R.id.video_people);
        playDuration = (TextView) getView(R.id.playDuration);
        skbProgress = (SeekBar) getView(R.id.skbProgress);
        videoDuration = (TextView) getView(R.id.videoDuration);
        playerBottomLayout = (LinearLayout) getView(R.id.playerBottomLayout);
        fragment_back_view=(View) getView(R.id.fragment_back_view);

//        videoSurface.setZOrderOnTop(true);
        videoSurface.getHolder().setFormat(PixelFormat.TRANSLUCENT);//设置画布  背景透明

        getView(R.id.backPlayList).setOnClickListener(this);
        getView(R.id.iv_top_menu).setOnClickListener(this);
        ivLock.setOnClickListener(this);
        ivCenterPlay.setOnClickListener(this);
        replayPlayIcon.setOnClickListener(this);
        rePlayFullScreen.setOnClickListener(this);
        bufferProgressBar = (ProgressBar) getView(R.id.bufferProgressBar);
        rlPlay = (RelativeLayout) getView(R.id.rl_play_layout);
        rlPlay.setOnTouchListener((v, event) -> {
            if (bufferProgressBar.getVisibility()==View.VISIBLE) {
                return true;
            }
            resetHideDelayed();
            detector.onTouchEvent(event);
            return true;
        });
        rlPlay.setClickable(true);
        rlPlay.setLongClickable(true);
        rlPlay.setFocusable(true);
        skbProgress.setOnSeekBarChangeListener(onSeekBarChangeListener);

        surfaceHolder = videoSurface.getHolder();
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS); //2.3及以下使用，不然出现只有声音没有图像的问题
        surfaceHolder.addCallback(this);
        tvVideoGroupName.setText(video.getVideoGroupname());
        tvVideoName.setText(video.getVideoName());
        videoPeople.setText(video.getVideoParticipation() + "");
        initPlayHander();
        initPlayInfo();
    }

    @Override
    protected void initData() {

    }

    @Override
    protected BasePresenter initPresent() {
        return null;
    }

    private void initPlayHander() {
        playerHandler = new Handler() {
            public void handleMessage(Message msg) {
                if (player == null) {
                    return;
                }
                if(play_stop){
                    ivLock.setSelected(false);
                    isPrepared = false;
                    replayPlayIcon.setSelected(false);
                    CenterLayout(View.VISIBLE);
                    player.pause();
                    return;
                }
                // 更新播放进度
                currentPlayPosition = player.getCurrentPosition();
                int duration = player.getDuration();
                if (duration > 0) {
                    if (currentPlayPosition >= Constans.CC_TIME && !video.getOrder()) {
                        currentPlayPosition = Constans.CC_TIME;
                        if (player.isPlaying()) {
                            player.pause();
                            ivLock.setSelected(false);
                            isPrepared = false;
                            replayPlayIcon.setSelected(false);
                            CenterLayout(View.VISIBLE);
                            long pos = skbProgress.getMax() * Constans.CC_TIME / duration;
                            skbProgress.setProgress((int) pos);
                            playDuration.setText(DateUtil.getVedioTime(currentPlayPosition));
                            //弹出提示框
                            if (dialog == null) dialog = VideoDialog.CreateDialog(getActivity())
                                    .setOnCommitClick(flag -> {
                                        switch (flag) {
                                            case VideoDialog.COMMIT:
                                                activity.PayVideoNews();
                                                break;
                                            case VideoDialog.AGAIN:
                                                currentPlayPosition=0;
                                                resetHideDelayed();
                                                changePlayStatus();
                                                playDuration.setText(DateUtil.getVedioTime(0));
                                                isPrepared=true;
                                                break;
                                        }
                                    });
                                    dialog.setOnDismissListener(dialog -> isPrepared=true);
                            dialog.show();
                        }
                    } else {
                        long pos = skbProgress.getMax() * currentPlayPosition / duration;
                        playDuration.setText(DateUtil.getVedioTime(player.getCurrentPosition()));
                        skbProgress.setProgress((int) pos);
                    }
                }
            }
        };
    }

    private void initPlayInfo() {
        // 通过定时器和Handler来更新进度
        isPrepared = false;
        player = new DWMediaPlayer();
        player.reset();
        player.setOnErrorListener(this);
        player.setOnCompletionListener(this);
        player.setOnVideoSizeChangedListener(this);
        player.setOnInfoListener(this);
        try {
            player.setVideoPlayInfo(video.getBokecc_id(), Constans.CC_USERID, Constans.CC_KEY, activity);
            // 设置默认清晰度
            player.setDefaultDefinition(DWMediaPlayer.NORMAL_DEFINITION);
        } catch (IllegalArgumentException e) {
            LogUtil.e(e.getMessage());
        } catch (SecurityException e) {
            LogUtil.e(e.getMessage());
        } catch (IllegalStateException e) {
            LogUtil.e(e.getMessage());
        }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        if (alertHandler != null&&!play_stop) {
            alertHandler.sendEmptyMessage(what);
        }
        return false;
    }
    private Handler alertHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            showToast("视频异常，无法播放。");
            bufferProgressBar.setVisibility(View.GONE);
            super.handleMessage(msg);
        }

    };
    @Override
    public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
        if (width == 0 || height == 0) {
            return;
        }
    }
    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        switch (what) {
            case DWMediaPlayer.MEDIA_INFO_BUFFERING_START:
                if (player.isPlaying()) {
                    bufferProgressBar.setVisibility(View.VISIBLE);
                }

                if (!isBackupPlay) {
                    playerHandler.postDelayed(backupPlayRunnable, 10 * 1000);
                }

                break;
            case DWMediaPlayer.MEDIA_INFO_BUFFERING_END:
                bufferProgressBar.setVisibility(View.GONE);
                playerHandler.removeCallbacks(backupPlayRunnable);
                break;
        }
        return false;
    }

    private Runnable backupPlayRunnable = () -> startBackupPlay();

    private void startBackupPlay() {
        player.setBackupPlay(true);
        isBackupPlay = true;
        player.reset();
        player.prepareAsync();
    }
    public void setRelaySize() {
        ViewGroup.LayoutParams layoutParams = rlPlay.getLayoutParams();
        if (activity.isPortrait()) {
            layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
            ivLock.setVisibility(View.GONE);
            rePlayFullScreen.setSelected(false);
        } else {
            ivLock.setVisibility(View.VISIBLE);
            rePlayFullScreen.setSelected(true);
            layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        }
        rlPlay.setLayoutParams(layoutParams);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            player.setAudioStreamType(AudioManager.STREAM_MUSIC);
            player.setOnBufferingUpdateListener(this);
            player.setOnPreparedListener(this);
            player.setDisplay(holder);
            player.setScreenOnWhilePlaying(true);
            player.prepareAsync();
        } catch (Exception e) {
            LogUtil.e(e.getMessage());
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        holder.setFixedSize(width, height);
    }

    int currentPosition;

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (player == null) {
            return;
        }
        if (isPrepared) {
            currentPosition = player.getCurrentPosition();
        }
        isPrepared = false;
        player.stop();
        player.reset();
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        skbProgress.setSecondaryProgress(percent);
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        initTimerTask();
        isPrepared = true;
        if (!play_stop) {
            changePlayStatus();
        }
        bufferProgressBar.setVisibility(View.GONE);
        videoDuration.setText(ParamsUtil.millsecondsToStr(player.getDuration()));
    }

    private Timer timer = new Timer();
    private TimerTask timerTask;

    private void initTimerTask() {
        cancelTimerTask();
        timerTask = new TimerTask() {
            @Override
            public void run() {

                if (!isPrepared) {
                    return;
                }

                playerHandler.sendEmptyMessage(0);
            }
        };
        timer.schedule(timerTask, 0, 1000);
    }

    private void cancelTimerTask() {
        if (timerTask != null) {
            timerTask.cancel();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        onPlayDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backPlayList:
                if(activity.isPortrait()){
                    activity.finish();
                }else{
                    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                }
                break;
            case R.id.iv_top_menu:
                activity.showShare();
                break;
            case R.id.iv_center_play:
                //播放、暂停
                if (isPrepared) {
                    changePlayStatus();
                }
                break;
            case R.id.replay_play_icon:
                if (isPrepared) {
                    changePlayStatus();
                }
                break;
            case R.id.iv_lock:
                //锁屏
                if (ivLock.isSelected()) {
                    ivLock.setSelected(false);
                    setLayoutVisibility(View.VISIBLE);
                    showToast("已解开屏幕");
                } else {
                    ivLock.setSelected(true);
                    setLayoutVisibility(View.GONE);
                    setLandScapeRequestOrientation();
                    showToast("已锁定屏幕");
                }
                break;
            case R.id.replay_full_screen:
                //全屏
                rePlayFullScreen.setSelected(!rePlayFullScreen.isSelected());
                if (activity.isPortrait()) {
                    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                } else {
                    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                }
                break;
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {

    }

    private class MyGesture extends GestureDetector.SimpleOnGestureListener {
        private Boolean isVideo;
        private float scrollCurrentPosition;
        private float scrollCurrentVolume;

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            return super.onSingleTapUp(e);
        }

        @Override
        public void onLongPress(MotionEvent e) {
            super.onLongPress(e);
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if (ivLock.isSelected()) {
                return true;
            }
            if (isVideo == null) {
                if (Math.abs(distanceX) > Math.abs(distanceY)) {
                    isVideo = true;
                } else {
                    isVideo = false;
                }
            }
            if (isVideo.booleanValue()) {
                parseVideoScroll(distanceX);
            } else {
                parseAudioScroll(distanceY);
            }

            return super.onScroll(e1, e2, distanceX, distanceY);
        }

        private void parseVideoScroll(float distanceX) {
            if (ivLock.isSelected()) {
                return;
            }
            if (!isDisplay) {
                setLayoutVisibility(View.VISIBLE);
            }
            scrollTotalDistance += distanceX;
            float duration = (float) player.getDuration();
            float width = wm.getDefaultDisplay().getWidth() * 0.75f; // 设定总长度是多少，此处根据实际调整
            //右滑distanceX为负
            float currentPosition = scrollCurrentPosition - (float) duration * scrollTotalDistance / width;
            if (currentPosition < 0) {
                currentPosition = 0;
            } else if (currentPosition > duration) {
                currentPosition = duration;
            }
            if (currentPosition > Constans.CC_TIME && !video.getOrder()) {
                currentPosition = Constans.CC_TIME;
                currentPlayPosition = Constans.CC_TIME;
                long pos = skbProgress.getMax() * Constans.CC_TIME / player.getDuration();
                skbProgress.setProgress((int) pos);
            }
            player.seekTo((int) currentPosition);
            playDuration.setText(ParamsUtil.millsecondsToStr((int) currentPosition));
            int pos = (int) (skbProgress.getMax() * currentPosition / duration);
            skbProgress.setProgress(pos);
        }

        private void parseAudioScroll(float distanceY) {
            if (!isDisplay) {
                setLayoutVisibility(View.VISIBLE);
            }
            scrollTotalDistance += distanceY;
            float height = wm.getDefaultDisplay().getHeight() * 0.75f;
            // 上滑distanceY为正
            currentVolume = (int) (scrollCurrentVolume + maxVolume * scrollTotalDistance / height);
            if (currentVolume < 0) {
                currentVolume = 0;
            } else if (currentVolume > maxVolume) {
                currentVolume = maxVolume;
            }
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return super.onFling(e1, e2, velocityX, velocityY);
        }

        @Override
        public void onShowPress(MotionEvent e) {
            super.onShowPress(e);
        }

        @Override
        public boolean onDown(MotionEvent e) {
            scrollTotalDistance = 0f;
            isVideo = null;
            scrollCurrentPosition = (float) player.getCurrentPosition();
            scrollCurrentVolume = currentVolume;
            return super.onDown(e);
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            if (ivLock.isSelected()) {
                return true;
            }
            if (!isDisplay) {
                setLayoutVisibility(View.VISIBLE);
            }
            changePlayStatus();
            return super.onDoubleTap(e);
        }

        @Override
        public boolean onDoubleTapEvent(MotionEvent e) {
            return super.onDoubleTapEvent(e);
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            if (!isDisplay) {
                setLayoutVisibility(View.GONE);
            } else {
                setLayoutVisibility(View.VISIBLE);
            }
            return super.onSingleTapConfirmed(e);
        }
    }

    private void changePlayStatus() {
        if(fragment_back_view.getVisibility()==View.VISIBLE){
            fragment_back_view.setVisibility(View.GONE);
        }
        if (player.isPlaying()) {
            player.pause();
            ivCenterPlay.setVisibility(View.VISIBLE);
            replayPlayIcon.setSelected(false);
        } else {
            player.seekTo(currentPlayPosition);
            player.start();
            ivCenterPlay.setVisibility(View.GONE);
            replayPlayIcon.setSelected(true);
        }
    }

    private void setLayoutVisibility(int visibility) {
        isDisplay = (visibility == View.GONE);
        if (ivLock.isSelected()) {
            CenterLayout(visibility);
            playerTopLayout.setVisibility(View.GONE);
            playerBottomLayout.setVisibility(View.GONE);
        } else {
            playerTopLayout.setVisibility(visibility);
            videoCenterLayout.setVisibility(visibility);
            playerBottomLayout.setVisibility(visibility);
        }
    }

    private void CenterLayout(int visibility) {
        videoCenterLayout.setVisibility(visibility);
        ivLock.setVisibility(View.VISIBLE);
        if (!ivLock.isSelected()) {
            ivCenterPlay.setVisibility(visibility);
        }
    }

    // 重置隐藏界面组件的延迟时间
    private void resetHideDelayed() {
        playerHandler.removeCallbacks(hidePlayRunnable);
        playerHandler.postDelayed(hidePlayRunnable, 5000);
    }

    // 隐藏界面的线程
    private Runnable hidePlayRunnable = new Runnable() {
        @Override
        public void run() {
            isPrepared=false;
            setLayoutVisibility(View.GONE);
        }
    };
    SeekBar.OnSeekBarChangeListener onSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        int progress = 0;

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            if (NetWorkUtil.isNetWork(getContext())) {
                player.seekTo(progress);
                playerHandler.postDelayed(hidePlayRunnable, 5000);
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            playerHandler.removeCallbacks(hidePlayRunnable);
        }

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            this.progress = progress * player.getDuration() / seekBar.getMax();
            if (this.progress > Constans.CC_TIME && !video.getOrder()) {
                this.progress = Constans.CC_TIME;
                currentPlayPosition = Constans.CC_TIME;
            }
        }
    };

    public void onPlayResume() {
        play_stop=false;
        if (isFreeze) {
            isFreeze = false;
            if (isPrepared) {
                player.start();
            }
        } else {
            isFreeze = true;
            if (isPlaying && isPrepared) {
                player.start();
            }
        }
    }

    public void onPlayPause() {
        if (player != null && player.isPlaying()) {
            isPlaying = true;
            player.pause();
        } else {
            isPlaying = false;
        }
        isFreeze = false;
    }

    public void onPlayDestroy() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        if (playerHandler != null) {
            playerHandler.removeCallbacksAndMessages(null);
            playerHandler = null;
        }
        if(alertHandler!=null){
            alertHandler.removeCallbacksAndMessages(null);
            alertHandler = null;
        }
        if (player != null) {
            player.reset();
            player.release();
            player = null;
        }
    }

    boolean play_stop;

    public void onPlayStop() {
        setLandScapeRequestOrientation();
        play_stop = true;
        if (player != null && player.isPlaying()) {
            player.pause();
            ivCenterPlay.setVisibility(View.VISIBLE);
        }
        replayPlayIcon.setSelected(!play_stop);
    }

    private void setLandScapeRequestOrientation() {
        int rotation = wm.getDefaultDisplay().getRotation();
        // 旋转90°为横屏正向，旋转270°为横屏逆向
        if (rotation == Surface.ROTATION_90) {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else if (rotation == Surface.ROTATION_270) {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
        }
    }
}
