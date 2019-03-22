package br.com.wma.tools;

import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import br.com.wma.tools.widget.WMAAudioView;
import br.com.wma.tools.widget.WMADropdownView;
import br.com.wma.tools.widget.WMAVideoView;
import br.com.wma.tools.widget.entity.DropdownEntity;
import br.com.wma.tools.widget.interfaces.OnBackEventListener;
import br.com.wma.tools.widget.interfaces.OnDeleteEventListener;
import br.com.wma.tools.widget.interfaces.OnDropdownClick;
import br.com.wma.tools.widget.interfaces.OnVideoEvents;

public class MainActivity extends AppCompatActivity {

    private WMAAudioView wmaAudioView;
    private Button btVideo;
    private WMADropdownView wmaDropdownView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 1 - WMAAudioView
        loadWMAAudioView();

        // 2 - WMAVideoView
        loadWMAVideoView();

        // 3 - WMADropdownView
        loadWMADropdownView();
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
        btVideo = findViewById(R.id.btVideo);
        btVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, VideoActivity.class);
                startActivity(intent);
            }
        });
    }

    private void loadWMADropdownView(){
        List<DropdownEntity> dropdownEntities = new ArrayList<>();
        dropdownEntities.add(new DropdownEntity("1", "Teste 1", false));
        dropdownEntities.add(new DropdownEntity("2", "Teste 2", false));
        dropdownEntities.add(new DropdownEntity("3", "Teste 3", false));
        dropdownEntities.add(new DropdownEntity("4", "Teste 4", false));
        dropdownEntities.add(new DropdownEntity("5", "Teste 5", false));
        dropdownEntities.add(new DropdownEntity("6", "Teste 6", false));
        dropdownEntities.add(new DropdownEntity("7", "Teste 7", false));
        dropdownEntities.add(new DropdownEntity("8", "Teste 8", false));
        dropdownEntities.add(new DropdownEntity("9", "Teste 9", false));
        dropdownEntities.add(new DropdownEntity("10", "Teste 10", false));
        dropdownEntities.add(new DropdownEntity("11", "Teste 11", false));


        wmaDropdownView = findViewById(R.id.wmaDropdownView);
        wmaDropdownView.prepareDropdownComponent(this, dropdownEntities, new OnDropdownClick() {
            @Override
            public void onClick(DropdownEntity dropdownEntity, int position) {
                Toast.makeText(getApplicationContext(), dropdownEntity.getDescription(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
