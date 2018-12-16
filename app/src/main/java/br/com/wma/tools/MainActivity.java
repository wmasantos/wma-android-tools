package br.com.wma.tools;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.io.File;

import br.com.wma.tools.widget.WMAAudioView;
import br.com.wma.tools.widget.WMAVideoView;
import br.com.wma.tools.widget.interfaces.OnDeleteEventListener;
import br.com.wma.tools.widget.interfaces.OnVideoEvents;

public class MainActivity extends AppCompatActivity {

    private WMAAudioView wmaAudioView;
    private WMAVideoView wmaVideoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 1 - WMAAudioView
        loadWMAAudioView();

        // 2 - WMAVideoView
        loadWMAVideoView();
    }

    private void loadWMAAudioView(){
        wmaAudioView = findViewById(R.id.avAudioView);
        wmaAudioView.readyToPlayForStreamAsync(
                this,
                "https://s3-us-west-2.amazonaws.com/smn-mobile/cdp-desenv/Through+The+Fire+And+Flames.mp3",
                new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/WMATools/Audios"),
                null
        );

        wmaAudioView.setAudioTitle("Som Foda!");

        wmaAudioView.addDeleteEvent(new OnDeleteEventListener() {
            @Override
            public void onDelete() {
                System.out.println("Captura o clique na lixeira!");
            }
        });
    }

    private void loadWMAVideoView(){
        wmaVideoView = findViewById(R.id.ttt);
        wmaVideoView.loadVídeoStream(this, "https://s3-us-west-2.amazonaws.com/smn-mobile/fanflix/anime/boku-no-hero-s2-ep1.mp4", new OnVideoEvents() {
            @Override
            public void onPrepared() {

            }

            @Override
            public void onPlaying(int currentTime, String formattedTime) {

            }

            @Override
            public void onPlayingComplete() {

            }
        });
    }
}
