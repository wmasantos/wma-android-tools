package br.com.wma.tools.audio;

import android.media.MediaPlayer;

import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import br.com.wma.tools.interfaces.OnAudioPlayingListener;
import br.com.wma.tools.interfaces.OnPlayerEventListener;

public class WMAAudio {
    private MediaPlayer mediaPlayer;
    private Timer timer;

    public WMAAudio() {
        mediaPlayer = new MediaPlayer();
    }

    public void readyToPlay(File audioFile, final OnPlayerEventListener onPlayerEventListener){
        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(audioFile.getAbsolutePath());
            mediaPlayer.prepare();

            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    int duration = mediaPlayer.getDuration();

                    String totalTime = String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(duration), TimeUnit.MILLISECONDS.toSeconds(duration) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration)));

                    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            mp.pause();
                            if(timer != null)
                                timer.cancel();

                            mp.seekTo(0);
                            onPlayerEventListener.onPlayingComplete();
                        }
                    });

                    onPlayerEventListener.onAudioReady(mediaPlayer.getDuration(), totalTime);
                }
            });
        } catch (IOException e) {
            onPlayerEventListener.onAudioReadyError(e);
        }
    }

    public void readyToPlayForStream(String audioFile, final OnPlayerEventListener onPlayerEventListener){
        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(audioFile);
            mediaPlayer.prepare();

            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    int duration = mediaPlayer.getDuration();

                    String totalTime = String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(duration), TimeUnit.MILLISECONDS.toSeconds(duration) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration)));

                    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            mp.pause();
                            if(timer != null)
                                timer.cancel();

                            mp.seekTo(0);
                            onPlayerEventListener.onPlayingComplete();
                        }
                    });

                    onPlayerEventListener.onAudioReady(mediaPlayer.getDuration(), totalTime);
                }
            });
        } catch (IOException e) {
            onPlayerEventListener.onAudioReadyError(e);
        }
    }

    public void seekTo(int seekTo){
        mediaPlayer.seekTo(seekTo);
    }

    public String getFormatedCurrentTime(){
        int duration = mediaPlayer.getCurrentPosition();

        return String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(duration), TimeUnit.MILLISECONDS.toSeconds(duration) -
                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration)));
    }

    public void play(final OnAudioPlayingListener onAudioPlayingListener){
        timer = new Timer();
        mediaPlayer.start();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                int currentPosition = mediaPlayer.getCurrentPosition();

                String formatedTime = String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(currentPosition), TimeUnit.MILLISECONDS.toSeconds(currentPosition) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(currentPosition)));

                onAudioPlayingListener.onPlaying(currentPosition, formatedTime);
            }
        }, 0, 300);
    }

    public void pause(){
        mediaPlayer.pause();
        timer.cancel();
    }

    public boolean isPlaying(){
        return mediaPlayer.isPlaying();
    }

    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    public void setMediaPlayer(MediaPlayer mediaPlayer) {
        this.mediaPlayer = mediaPlayer;
    }

    public Timer getTimer() {
        return timer;
    }

    public void setTimer(Timer timer) {
        this.timer = timer;
    }

    public String getFormatedTotalTime(){
        int duration = mediaPlayer.getDuration();

        return String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(duration), TimeUnit.MILLISECONDS.toSeconds(duration) -
                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration)));
    }
}