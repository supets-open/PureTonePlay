package com.csc.puretone;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;

public class PureToneActivity extends Activity implements Runnable {
    private AudioTrackManager audio;
    private Button btn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        audio = new AudioTrackManager();
        btn = (Button) findViewById(R.id.play);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(PureToneActivity.this).start();
            }
        });
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            audio.stop();
            this.finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void run() {
        if (!audio.isPlaying()){
            audio.start(400);
            audio.Song();
            audio.stop();
        }
    }

}