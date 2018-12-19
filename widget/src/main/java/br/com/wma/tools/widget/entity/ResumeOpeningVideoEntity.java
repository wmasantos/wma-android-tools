package br.com.wma.tools.widget.entity;

import java.io.Serializable;

public class ResumeOpeningVideoEntity implements Serializable {
    private int startAt;
    private int endAt;

    public ResumeOpeningVideoEntity(int startAt, int endAt) {
        this.startAt = startAt;
        this.endAt = endAt;
    }

    public int getStartAt() {
        return startAt;
    }

    public void setStartAt(int startAt) {
        this.startAt = startAt;
    }

    public int getEndAt() {
        return endAt;
    }

    public void setEndAt(int endAt) {
        this.endAt = endAt;
    }
}
