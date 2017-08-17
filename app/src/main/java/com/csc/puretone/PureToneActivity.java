package com.csc.puretone;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;

public class PureToneActivity extends Activity implements Runnable {
    AudioTrackManager audio;
    Thread thread;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        audio = new AudioTrackManager();
        audio.start(400);
        thread = new Thread(this);
        thread.start();
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            audio.isPlaying = false;
            this.finish();
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    public void run() {
        audio.Song();
        audio.stop();
    }

}