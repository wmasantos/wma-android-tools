package br.com.wma.tools.audio;

import android.media.MediaPlayer;

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

    public void readyToPlayForStream(String audioFile, final OnPlayerEventListener onPlayerEventListener){
        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(audioFile);
            mediaPlayer.prepare();

            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    int duration = mediaPlayer.getDuration();

                    String totalTime = getFormattedDurationTime(duration);

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

                onAudioPlayingListener.onPlaying(currentPosition, getFormattedDurationTime(currentPosition));
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

    public String getFormattedTime(){
        int duration = mediaPlayer.getDuration();

        return getFormattedDurationTime(duration);
    }

    public static String getFormattedDurationTime(int duration){
        return String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(duration), TimeUnit.MILLISECONDS.toSeconds(duration) -
                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration)));
    }
}