package br.com.wma.tools.widget.entity;

import java.io.IOException;

public class DownloadAudioEntity {
    private int status;
    private int progress;
    private IOException ioException;

    public DownloadAudioEntity() {
    }

    public DownloadAudioEntity(int status, int progress, IOException ioException) {
        this.status = status;
        this.progress = progress;
        this.ioException = ioException;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public IOException getIoException() {
        return ioException;
    }

    public void setIoException(IOException ioException) {
        this.ioException = ioException;
    }
}
