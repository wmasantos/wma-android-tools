package br.com.wma.tools.widget;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.media.MediaPlayer;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import br.com.wma.tools.exception.WMAException;
import br.com.wma.tools.widget.entity.ResumeOpeningVideoEntity;
import br.com.wma.tools.widget.interfaces.OnBackEventListener;
import br.com.wma.tools.widget.interfaces.OnVideoEvents;

public class WMAVideoView extends LinearLayout implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnInfoListener {
    public static final int FORWARD_TIME = 10000;
    public static final int REPLAY_TIME = 10000;

    private Activity activity;
    private String urlStream;
    private OnVideoEvents onVideoEvents;
    private OnBackEventListener onBackEventListener;
    private Timer timer;
    private boolean startOnLoad;
    private boolean restartVideo;
    private ResumeOpeningVideoEntity resumeOpeningProperties;

    private FrameLayout frameContainer;
    private VideoView vwVideoView;
    private LinearLayout llMediaController;
    private LinearLayout llTransparenceLoad;
    private ImageView ivBack;
    private ImageView ivReplay;
    private ImageView ivPlay;
    private ImageView ivForward;
    private TextView tvTitle;
    private TextView tvBuffering;
    private TextView tvSkipOpening;
    private SeekBar skTimeLine;
    private TextView tvTimeElapsed;
    private TextView tvTotalTime;
    private boolean skipeCalled = false;

    public WMAVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initComponents(context, attrs);
    }

    private void initComponents(final Context context, AttributeSet attrs){
        inflate(context, R.layout.layout_wma_videoview, this);

        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.Video, 0, 0);

        this.startOnLoad = a.getBoolean(R.styleable.Video_startOnLoad, false);
        this.restartVideo = a.getBoolean(R.styleable.Video_restartVideo, false);

        a.recycle();

        frameContainer = findViewById(R.id.frameContainer);
        vwVideoView = findViewById(R.id.vwVideoView);
        llMediaController = findViewById(R.id.llMediaController);
        llTransparenceLoad = findViewById(R.id.llTransparenceLoad);
        ivBack = findViewById(R.id.ivBack);
        ivReplay = findViewById(R.id.ivReplay);
        ivPlay = findViewById(R.id.ivPlay);
        ivForward = findViewById(R.id.ivForward);
        tvTitle = findViewById(R.id.tvTitle);
        tvBuffering = findViewById(R.id.tvBuffering);
        tvSkipOpening = findViewById(R.id.tvSkipOpening);
        skTimeLine = findViewById(R.id.skTimeLine);
        tvTimeElapsed = findViewById(R.id.tvTimeElapsed);
        tvTotalTime = findViewById(R.id.tvTotalTime);
    }

    public void loadVídeoStream(Activity act, String urlS, ResumeOpeningVideoEntity resOpProperties, OnVideoEvents onVE, OnBackEventListener onBack){
        this.activity = act;
        this.urlStream = urlS;
        this.onVideoEvents = onVE;
        this.onBackEventListener = onBack;
        this.resumeOpeningProperties = resOpProperties;

        // Seta a URL no videoView
        vwVideoView.setVideoPath(urlStream);

        // Mantem a tela sempre ativa
        vwVideoView.setKeepScreenOn(true);

        // Habilita o loading
        llTransparenceLoad.setVisibility(VISIBLE);

        // Recebe evento do vídeo preparado
        vwVideoView.setOnPreparedListener(WMAVideoView.this);

        // Recebe evento do vídeo finalizado
        vwVideoView.setOnCompletionListener(WMAVideoView.this);

        // Recebe eventos do buffer
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
            vwVideoView.setOnInfoListener(WMAVideoView.this);

        frameContainer.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                final Animation animControll = AnimationUtils.loadAnimation(activity, R.anim.fade_out);

                if(llMediaController.isShown()){
                    llMediaController.setAnimation(animControll);
                    animControll.start();
                    llMediaController.setVisibility(GONE);
                }
                else
                    llMediaController.setVisibility(VISIBLE);
            }
        });

        ivBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                pause();
                if(onBackEventListener != null)
                   onBackEventListener.backPressed();
            }
        });

        ivForward.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                int forwardTime = vwVideoView.getCurrentPosition() + FORWARD_TIME;
                vwVideoView.seekTo(forwardTime);
                skTimeLine.setProgress(forwardTime);
                tvTimeElapsed.setText(getFormatedCurrentTime(forwardTime));
            }
        });

        ivReplay.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                int forwardTime = vwVideoView.getCurrentPosition() - REPLAY_TIME;
                vwVideoView.seekTo(forwardTime);
                skTimeLine.setProgress(forwardTime);
                tvTimeElapsed.setText(getFormatedCurrentTime(forwardTime));
            }
        });

    }

    private void play(){
        timer = new Timer();
        vwVideoView.start();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                final int currentPosition = vwVideoView.getCurrentPosition();

                final String formatedTime = String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(currentPosition), TimeUnit.MILLISECONDS.toSeconds(currentPosition) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(currentPosition)));

                if(onVideoEvents != null)
                   onVideoEvents.onPlaying(currentPosition, formatedTime);

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        skTimeLine.setProgress(currentPosition);
                        tvTimeElapsed.setText(formatedTime);
                        onResumeVideo(resumeOpeningProperties);
                    }
                });
            }
        }, 0, 300);
    }

    private void pause(){
        vwVideoView.pause();
        timer.cancel();
    }

    public String getFormatedCurrentTime(){
        int duration = vwVideoView.getCurrentPosition();

        return String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(duration), TimeUnit.MILLISECONDS.toSeconds(duration) -
                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration)));
    }

    public boolean isPlaying(){
        return vwVideoView.isPlaying();
    }

    private String getFormatedCurrentTime(int duration){

        return String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(duration), TimeUnit.MILLISECONDS.toSeconds(duration) -
                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration)));
    }

    public void setTitle(String title){
        if(title != null)
            tvTitle.setText(String.valueOf(title));
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        if(onVideoEvents != null)
           onVideoEvents.onPrepared();

        llTransparenceLoad.setVisibility(GONE);
        llMediaController.setVisibility(VISIBLE);
        tvTotalTime.setText(getFormatedCurrentTime(vwVideoView.getDuration()));

        skTimeLine.setMax(vwVideoView.getDuration());

        skTimeLine.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser) {
                    vwVideoView.seekTo(progress);
                    tvTimeElapsed.setText(getFormatedCurrentTime());
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        ivPlay.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isPlaying()) {
                    ivPlay.setImageResource(R.drawable.selector_play_n_video);
                    pause();
                } else {
                    play();
                    ivPlay.setImageResource(R.drawable.selector_pause_n_video);
                }
            }
        });

        mp.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(MediaPlayer mp, int percent) {
                if(((percent * skTimeLine.getMax()) / 100) < skTimeLine.getMax())
                    skTimeLine.setSecondaryProgress((percent * skTimeLine.getMax()) / 100);
            }
        });

        tvSkipOpening.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                int newCurrent = resumeOpeningProperties.getEndAt() + 3000;

                vwVideoView.seekTo(newCurrent);
                skTimeLine.setProgress(newCurrent);
            }
        });

        if(startOnLoad)
            ivPlay.callOnClick();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        mp.pause();
        if(timer != null)
            timer.cancel();

        if(restartVideo){
            mp.seekTo(0);
            skTimeLine.setProgress(0);
            skTimeLine.setSecondaryProgress(0);
            ivPlay.setImageResource(R.drawable.selector_play_n_video);
            tvTimeElapsed.setText("00:00");
        }

        if(onVideoEvents != null)
           onVideoEvents.onPlayingComplete();
    }

    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        switch(what){
            case MediaPlayer.MEDIA_INFO_BUFFERING_START:{
                tvBuffering.setVisibility(VISIBLE);
                break;
            }

            case MediaPlayer.MEDIA_INFO_BUFFERING_END:{
                tvBuffering.setVisibility(GONE);
                break;
            }

            case MediaPlayer.MEDIA_INFO_AUDIO_NOT_PLAYING:{
                // TODO fazer algo aqui
                break;
            }
        }

        return false;
    }

    private void onResumeVideo(ResumeOpeningVideoEntity resOpProperties){

        int duration = vwVideoView.getDuration();
        int currentPosition = vwVideoView.getCurrentPosition();

        if(resOpProperties == null) {
            System.out.println("Nenhum parametro informado");
            return;
        }

        if(resOpProperties.getStartAt() > duration || resOpProperties.getEndAt() > duration) {
            System.out.println("Valor informado não pode ser maior que duração total");
            return;
        }

        if(resOpProperties.getStartAt() <= 0 || resOpProperties.getEndAt() <= 0) {
            System.out.println("Valor informado não pode ser menor que zero");
            return;
        }

        if(resOpProperties.getStartAt() >= resOpProperties.getEndAt()) {
            System.out.println("Início deve ser menor que fim");
            return;
        }

        if(currentPosition > resOpProperties.getStartAt() && currentPosition < resOpProperties.getEndAt()){
            if(!tvSkipOpening.isShown()){
                Animation in = AnimationUtils.loadAnimation(activity, R.anim.fade_in);
                tvSkipOpening.setAnimation(in);
                in.start();
                tvSkipOpening.setVisibility(VISIBLE);
            }
        }
        else{
            if(tvSkipOpening.isShown()){
                Animation out = AnimationUtils.loadAnimation(activity, R.anim.fade_out);
                tvSkipOpening.setAnimation(out);
                out.start();
                tvSkipOpening.setVisibility(GONE);
            }
        }
    }
}
