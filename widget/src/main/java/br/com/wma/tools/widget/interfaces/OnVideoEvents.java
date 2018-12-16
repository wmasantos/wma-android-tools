package br.com.wma.tools.widget.interfaces;

public interface OnVideoEvents {
    void onPrepared();
    void onPlaying(int currentTime, String formattedTime);
    void onPlayingComplete();
}
