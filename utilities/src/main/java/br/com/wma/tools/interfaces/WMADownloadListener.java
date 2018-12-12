package br.com.wma.tools.interfaces;

import java.io.IOException;

public interface WMADownloadListener {
    void onStart();
    void onProgress(int percent);
    void onFail(IOException ex);
    void onComplete();
}
