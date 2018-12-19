package br.com.wma.tools;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

import br.com.wma.tools.widget.WMAVideoView;
import br.com.wma.tools.widget.entity.ResumeOpeningVideoEntity;
import br.com.wma.tools.widget.interfaces.OnBackEventListener;
import br.com.wma.tools.widget.interfaces.OnVideoEvents;

public class VideoActivity extends Activity {

    private WMAVideoView wmaVideoView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        // 2 - WMAVideoView
        loadWMAVideoView();
    }

    private void loadWMAVideoView(){
        wmaVideoView = findViewById(R.id.video);
        wmaVideoView.setTitle("Boku no Hero Academia");
        wmaVideoView.loadVídeoStream(this, "https://s3-us-west-2.amazonaws.com/smn-mobile/fanflix/anime/boku-no-hero-s2-ep1.mp4", new ResumeOpeningVideoEntity(5000, 45000), new OnVideoEvents() {
                    @Override
                    public void onPrepared() {
                        System.out.println("PREPAROU");
                    }

                    @Override
                    public void onPlaying(int currentTime, String formattedTime) {
                        System.out.println("TOCANDO: " + formattedTime);
                    }

                    @Override
                    public void onPlayingComplete() {
                        System.out.println("COMPLETOU");
                    }
                },
                new OnBackEventListener() {
                    @Override
                    public void backPressed() {
                        backScreen();
                    }
                }
        );
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        backScreen();
    }

    private void backScreen() {
        finish();
    }
}
