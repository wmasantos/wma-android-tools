package br.com.wma.tools.widget.entity;

public class AudioPropertiesEntity {
    private int statusProgress;
    private int totalTime;
    private String totalTimeFormatted;
    private Exception e;

    public AudioPropertiesEntity() {
    }

    public AudioPropertiesEntity(int statusProgress, int totalTime, String totalTimeFormatted, Exception e) {
        this.statusProgress = statusProgress;
        this.totalTime = totalTime;
        this.totalTimeFormatted = totalTimeFormatted;
        this.e = e;
    }

    public int getStatusProgress() {
        return statusProgress;
    }

    public void setStatusProgress(int statusProgress) {
        this.statusProgress = statusProgress;
    }

    public int getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(int totalTime) {
        this.totalTime = totalTime;
    }

    public String getTotalTimeFormatted() {
        return totalTimeFormatted;
    }

    public void setTotalTimeFormatted(String totalTimeFormatted) {
        this.totalTimeFormatted = totalTimeFormatted;
    }

    public Exception getE() {
        return e;
    }

    public void setE(Exception e) {
        this.e = e;
    }
}
