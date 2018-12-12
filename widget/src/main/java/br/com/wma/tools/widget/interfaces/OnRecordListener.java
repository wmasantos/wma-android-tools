package br.com.wma.tools.widget.interfaces;

import java.io.IOException;

public interface OnRecordListener {
    void startRecording();
    void onTick(long time, String formattedTime);
    void onFinish();
    void onError(IOException e);
}
