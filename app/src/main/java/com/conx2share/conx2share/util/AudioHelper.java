package com.conx2share.conx2share.util;


import java.io.File;

public interface AudioHelper {

    boolean startRecording();

    File stopRecording();

    void cancelRecording();

    boolean startPlaying(String url, Integer maxDuration);

    void stopPlaying();
}
