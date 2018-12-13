package br.com.wma.tools.widget;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

import br.com.wma.tools.audio.WMAAudio;
import br.com.wma.tools.exception.WMAException;
import br.com.wma.tools.interfaces.OnAudioPlayingListener;
import br.com.wma.tools.interfaces.OnPlayerEventListener;
import br.com.wma.tools.interfaces.WMADownloadListener;
import br.com.wma.tools.util.WMAUtilities;
import br.com.wma.tools.widget.entity.AudioPropertiesEntity;
import br.com.wma.tools.widget.entity.DownloadAudioEntity;
import br.com.wma.tools.widget.interfaces.OnDeleteEventListener;

public class WMAAudioView extends LinearLayout {

    private TextView tvTimeElapsed, tvTotalTime, tvAudioTitle, tvDownloadPercent;
    private ImageView ivPlayPause, ivDeleteTrack, ivDownloadTrack, ivReload;
    private SeekBar skTimeLine;
    private LinearLayout llAudioLayout;
    private FrameLayout llDownloadContainer;
    private WMAAudio wmaAudio;
    private String urlStream;
    private OnDeleteEventListener onDeleteEventListener;
    private ProgressBar pbLoadAudioPlay;
    private boolean backToBegin;
    private Context context;
    private File downloadPath;
    private Activity activity;
    private OnPlayerEventListener onPlayerEventListener;

    public WMAAudioView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
        this.context = context;
    }

    private void initComponents(Drawable backgroundContainer, int elapsedTime, int totalTime, int progress, String audioTitle, boolean deleteTrack, boolean downloadTrack) {
        llAudioLayout = findViewById(R.id.llAudioLayout);
        if(backgroundContainer != null)
            llAudioLayout.setBackgroundDrawable(backgroundContainer);

        tvAudioTitle = findViewById(R.id.tvAudioTitle);
        if(audioTitle != null && !audioTitle.trim().equals("")) {
            tvAudioTitle.setVisibility(View.VISIBLE);
            tvAudioTitle.setText(audioTitle);
        }

        tvTimeElapsed = findViewById(R.id.tvTimeElapsed);
        tvTimeElapsed.setTextColor(elapsedTime);

        tvTotalTime = findViewById(R.id.tvTotalTime);
        tvTotalTime.setTextColor(totalTime);

        ivPlayPause = findViewById(R.id.ivPlayPause);

        ivDeleteTrack = findViewById(R.id.ivDeleteTrack);
        if(deleteTrack)
            ivDeleteTrack.setVisibility(View.VISIBLE);

        ivDeleteTrack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(onDeleteEventListener != null)
                    onDeleteEventListener.onDelete();
            }
        });

        skTimeLine = findViewById(R.id.skTimeLine);
        skTimeLine.setProgress(progress);

        ivDownloadTrack = findViewById(R.id.ivDownloadTrack);
        tvDownloadPercent = findViewById(R.id.tvDownloadPercent);
        llDownloadContainer = findViewById(R.id.llDownloadContainer);
        if(downloadTrack)
            ivDownloadTrack.setVisibility(View.VISIBLE);

        ivDownloadTrack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(WMAUtilities.isStoragePermissionGranted(getContext())){
                    if(WMAUtilities.checkIfStreamFileExistOnDisk(urlStream, downloadPath) != null)
                        Toast.makeText(getContext(), "Arquivo já existe em: " + downloadPath.getAbsolutePath(), Toast.LENGTH_LONG).show();
                    else{
                        try {
                            downloadAudio();
                        } catch (WMAException e) {
                            e.printStackTrace();
                        }
                    }
                }
                else
                    ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
            }
        });

        pbLoadAudioPlay = findViewById(R.id.pbLoadAudioPlay);
        ivReload = findViewById(R.id.ivReload);
    }

    private void init(Context context, AttributeSet attrs) {
        inflate(context, R.layout.layout_audio, this);

        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.Options, 0, 0);

        int elapsedTimeColor = a.getColor(R.styleable.Options_elapsedTimeColor, getResources().getColor(R.color.default_time_color));
        int totalTimeColor = a.getColor(R.styleable.Options_totalTimeColor, getResources().getColor(R.color.default_time_color));
        int progress = a.getInt(R.styleable.Options_progress, 0);
        Drawable drawable = a.getDrawable(R.styleable.Options_backgroundContainer);
        String audioTitle = a.getString(R.styleable.Options_audioTitle);
        boolean deleteTrack = a.getBoolean(R.styleable.Options_deleteTrack, false);
        boolean downloadTrack = a.getBoolean(R.styleable.Options_downloadTrack, false);
        this.backToBegin = a.getBoolean(R.styleable.Options_backToBegin, false);

        a.recycle();

        initComponents(drawable, elapsedTimeColor, totalTimeColor, progress, audioTitle, deleteTrack, downloadTrack);
    }

    public TextView getTvTimeElapsed() {
        return tvTimeElapsed;
    }

    public void setTvTimeElapsed(TextView tvTimeElapsed) {
        this.tvTimeElapsed = tvTimeElapsed;
    }

    public TextView getTvTotalTime() {
        return tvTotalTime;
    }

    public void setTvTotalTime(TextView tvTotalTime) {
        this.tvTotalTime = tvTotalTime;
    }

    public ImageView getIvPlayPause() {
        return ivPlayPause;
    }

    public void setIvPlayPause(ImageView ivPlayPause) {
        this.ivPlayPause = ivPlayPause;
    }

    public SeekBar getSkTimeLine() {
        return skTimeLine;
    }

    public void setSkTimeLine(SeekBar skTimeLine) {
        this.skTimeLine = skTimeLine;
    }

    public WMAAudio getWmaAudio() {
        return wmaAudio;
    }

    private void loadSMNAudio() {

        tvTotalTime.setText(wmaAudio.getFormatedTotalTime());
        skTimeLine.setMax(wmaAudio.getMediaPlayer().getDuration());

        skTimeLine.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser) {
                    WMAAudioView.this.wmaAudio.seekTo(progress);
                    tvTimeElapsed.setText(WMAAudioView.this.wmaAudio.getFormatedCurrentTime());
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        ivPlayPause.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (WMAAudioView.this.wmaAudio.isPlaying()) {
                    ivPlayPause.setImageResource(R.drawable.ic_play);
                    WMAAudioView.this.wmaAudio.pause();
                } else {
                    WMAAudioView.this.wmaAudio.play(new OnAudioPlayingListener() {
                        @Override
                        public void onPlaying(int currentPosition, final String formatedTime) {
                            skTimeLine.setProgress(currentPosition);
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    tvTimeElapsed.setText(formatedTime);
                                }
                            });
                        }
                    });

                    ivPlayPause.setImageResource(R.drawable.ic_pause);
                }
            }
        });
    }

    /**
     * Método assíncrono que carrega uma view de audio por stream de dados, a view aparece com um loading e aguarda até que o áudio esteja pronto para executar
     * @param activity referência de contexto para onde o audio será apresentado
     * @param urlStream URL do audio para ser reproduzido
     * @param downloadPath Informando um diretório no parametro será onde o audio será salvo caso iniciado um download, se passado nulo, mesmo habilitando o icone de download ele desaparecerá na view
     *                     após fazer o download, o player passa a utilizar o arquivo em disco como saída.
     * @param onPlayerEventListener Listener que joga para o desenvolvedor os eventos que acontecem no player caso seja necessário um tratamento adicional.
     */
    public void readyToPlayForStreamAsync(Activity activity, @NonNull String urlStream, File downloadPath, OnPlayerEventListener onPlayerEventListener){
        this.urlStream = urlStream;
        this.downloadPath = downloadPath;
        this.activity = activity;
        this.onPlayerEventListener = onPlayerEventListener;

        if(downloadPath == null)
            ivDownloadTrack.setVisibility(View.GONE);
        else {
            File localFile = WMAUtilities.checkIfStreamFileExistOnDisk(this.urlStream, this.downloadPath);
            if(localFile != null) {
                ivDownloadTrack.setImageResource(R.drawable.ic_check);
                this.urlStream = localFile.getAbsolutePath();
            }
        }

        this.wmaAudio = new WMAAudio();

        new AsyncLoadAudio(onPlayerEventListener).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        ivReload.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                new AsyncLoadAudio(WMAAudioView.this.onPlayerEventListener).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        });
    }

    public void setAudioTitle(String audioTitle){
        if(audioTitle != null && !audioTitle.trim().equals("")) {
            tvAudioTitle.setVisibility(View.VISIBLE);
            tvAudioTitle.setText(audioTitle);
        }
    }

    public void addDeleteEvent(OnDeleteEventListener onDeleteEventListener){
        this.onDeleteEventListener = onDeleteEventListener;
    }

    private void downloadAudio()throws WMAException{
        WMAUtilities.createDirectory(this.downloadPath, context);

        String urlStream = this.urlStream;
        if(urlStream == null || urlStream.isEmpty())
            throw new WMAException("URL de áudio não encontrada! Verifique se o método 'readyToPlayForStream' foi invocado previamente.");

        new AsyncDownloadAudio(urlStream, downloadPath).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private class AsyncLoadAudio extends AsyncTask<Void, Void, Void> {
        private OnPlayerEventListener onPlayerEventListener;
        private AudioPropertiesEntity audioPropertiesEntity;

        public AsyncLoadAudio(OnPlayerEventListener onPlayerEventListener) {
            this.onPlayerEventListener = onPlayerEventListener;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            audioPropertiesEntity = new AudioPropertiesEntity();
            audioPropertiesEntity.setStatusProgress(0);

            pbLoadAudioPlay.setVisibility(View.VISIBLE);
            ivPlayPause.setVisibility(View.GONE);
            ivReload.setVisibility(View.GONE);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            wmaAudio.readyToPlayForStream(urlStream, new OnPlayerEventListener() {
                @Override
                public void onAudioReady(final int i, final String s) {
                    audioPropertiesEntity.setStatusProgress(1);
                    audioPropertiesEntity.setTotalTime(i);
                    audioPropertiesEntity.setTotalTimeFormatted(s);

                    if(onPlayerEventListener != null){
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                onPlayerEventListener.onAudioReady(i, s);
                            }
                        });
                    }

                    publishProgress(null);
                }

                @Override
                public void onPlayingComplete() {
                    audioPropertiesEntity.setStatusProgress(2);

                    if(onPlayerEventListener != null){
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                onPlayerEventListener.onPlayingComplete();
                            }
                        });
                    }

                    publishProgress(null);
                }

                @Override
                public void onAudioReadyError(final Exception e) {
                    audioPropertiesEntity.setStatusProgress(3);
                    audioPropertiesEntity.setE(e);

                    if(onPlayerEventListener != null){
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                onPlayerEventListener.onAudioReadyError(e);
                            }
                        });
                    }

                    publishProgress(null);
                }
            });

            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);

            switch(audioPropertiesEntity.getStatusProgress()){
                case 0:{
                    // STATUS INICIO ANTES DO LOAD
                    break;
                }

                case 1:{
                    pbLoadAudioPlay.setVisibility(View.GONE);
                    ivPlayPause.setVisibility(View.VISIBLE);
                    ivReload.setVisibility(View.GONE);

                    loadSMNAudio();
                    break;
                }

                case 2:{
                    if(backToBegin){
                        wmaAudio.getMediaPlayer().seekTo(0);
                        skTimeLine.setProgress(0);
                        ivPlayPause.setImageResource(R.drawable.ic_play);
                        tvTimeElapsed.setText("00:00");
                    }
                    break;
                }

                case 3:{
                    pbLoadAudioPlay.setVisibility(View.GONE);
                    ivPlayPause.setVisibility(View.GONE);
                    ivReload.setVisibility(View.VISIBLE);
                    break;
                }
            }
        }
    }

    private class AsyncDownloadAudio extends AsyncTask<Void, Void, Void> {
        private String urlStream;
        private File downloadPath;

        private DownloadAudioEntity downloadAudioEntity;

        private AsyncDownloadAudio(String urlStream, File downloadPath) {
            this.urlStream = urlStream;
            this.downloadPath = downloadPath;

            downloadAudioEntity = new DownloadAudioEntity(0, 0, null);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            WMAUtilities.downloadFile(urlStream, downloadPath, new WMADownloadListener() {
                @Override
                public void onStart() {
                    publishProgress(null);
                }

                @Override
                public void onProgress(int percent) {
                    downloadAudioEntity.setStatus(1);
                    downloadAudioEntity.setProgress(percent);

                    publishProgress(null);
                }

                @Override
                public void onFail(IOException ex) {
                    downloadAudioEntity.setStatus(3);
                    downloadAudioEntity.setIoException(ex);

                    publishProgress(null);
                }

                @Override
                public void onComplete() {
                    downloadAudioEntity.setStatus(2);
                    publishProgress(null);
                }
            });
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);

            switch(downloadAudioEntity.getStatus()){
                case 0:{
                    ivDownloadTrack.setVisibility(View.GONE);
                    llDownloadContainer.setVisibility(View.VISIBLE);
                    break;
                }

                case 1:{
                    tvDownloadPercent.setText(String.valueOf(downloadAudioEntity.getProgress()));
                    break;
                }

                case 2:{
                    llDownloadContainer.setVisibility(View.GONE);
                    ivDownloadTrack.setVisibility(View.VISIBLE);

                    ivDownloadTrack.setImageResource(R.drawable.ic_check);

                    Toast.makeText(getContext(), "Download completed.", Toast.LENGTH_LONG).show();

                    break;
                }

                case 3:{
                    llDownloadContainer.setVisibility(View.GONE);
                    ivDownloadTrack.setVisibility(View.VISIBLE);

                    Toast.makeText(getContext(), "Download failed... TRY AGAIN.", Toast.LENGTH_LONG).show();
                    break;
                }
            }
        }
    }
}
