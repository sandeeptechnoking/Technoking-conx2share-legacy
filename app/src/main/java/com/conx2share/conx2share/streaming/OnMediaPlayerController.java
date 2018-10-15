package com.conx2share.conx2share.streaming;

import android.media.MediaPlayer;
import android.widget.MediaController;


public class OnMediaPlayerController implements MediaController.MediaPlayerControl {

    private MediaPlayer mediaPlayer;

    public OnMediaPlayerController(MediaPlayer mediaPlayer) {
        this.mediaPlayer = mediaPlayer;
    }

    @Override
    public void start() {
        if (mediaPlayer != null) {
            mediaPlayer.start();
        }
    }

    @Override
    public void pause() {
        if (mediaPlayer != null) {
            mediaPlayer.pause();
        }
    }

    @Override
    public int getDuration() {
        return mediaPlayer != null ? mediaPlayer.getDuration() : 0;
    }

    @Override
    public int getCurrentPosition() {
        return mediaPlayer != null ? mediaPlayer.getCurrentPosition() : 0;
    }

    @Override
    public void seekTo(int pos) {
        if (mediaPlayer != null) {
            mediaPlayer.seekTo(pos);
        }
    }

    @Override
    public boolean isPlaying() {
        return mediaPlayer != null && mediaPlayer.isPlaying();
    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public boolean canPause() {
        return mediaPlayer != null;
    }

    @Override
    public boolean canSeekBackward() {
        return mediaPlayer != null;
    }

    @Override
    public boolean canSeekForward() {
        return mediaPlayer != null;
    }

    @Override
    public int getAudioSessionId() {
        return mediaPlayer != null ? mediaPlayer.getAudioSessionId() : 0;
    }
}
