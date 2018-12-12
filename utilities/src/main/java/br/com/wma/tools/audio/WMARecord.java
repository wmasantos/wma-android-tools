package br.com.wma.tools.audio;

import android.media.MediaRecorder;

import java.io.File;
import java.io.IOException;

public class WMARecord {

    private MediaRecorder mediaRecorder;
    private boolean recording;

    private void ditchMediaRecorder() {
        if(mediaRecorder != null)
            mediaRecorder.release();
    }

    public File beginRecording(File recordPath) throws IllegalStateException, IOException {
        ditchMediaRecorder();

        if(recordPath.exists())
            recordPath.delete();

        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        mediaRecorder.setOutputFile(recordPath.getAbsolutePath());
        mediaRecorder.prepare();
        mediaRecorder.start();

        setRecording(true);

        return recordPath;
    }

    public void stopRecording() {
        if (mediaRecorder != null) {
            mediaRecorder.stop();
            mediaRecorder.release();
        }

        setRecording(false);
    }

    public boolean isRecording() {
        return recording;
    }

    public void setRecording(boolean recording) {
        this.recording = recording;
    }
}
