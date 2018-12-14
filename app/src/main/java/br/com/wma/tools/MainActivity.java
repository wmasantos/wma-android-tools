package br.com.wma.tools;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.io.File;

import br.com.wma.tools.widget.WMAAudioView;
import br.com.wma.tools.widget.interfaces.OnDeleteEventListener;

public class MainActivity extends AppCompatActivity {

    private WMAAudioView wmaAudioView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 1 - WMAAudioView
        loadWMAAudioView();
    }

    private final void loadWMAAudioView(){
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
}
