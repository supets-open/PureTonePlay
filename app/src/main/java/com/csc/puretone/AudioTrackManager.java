package com.csc.puretone;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

import java.util.concurrent.BlockingQueue;

import static com.csc.puretone.PLsheet.gangqing;
import static com.csc.puretone.PLsheet.tablef;
import static com.csc.puretone.PLsheet.tablep;

public class AudioTrackManager {
    public static final int RATE = 44100;
    public static final float MAXVOLUME = 100f;
    public static final int LEFT = 1;
    public static final int RIGHT = 2;
    public static final int DOUBLE = 3;

    AudioTrack audioTrack;
    /**
     * 音量
     **/
    float volume;
    /**
     * 声道
     **/
    int channel;
    /**
     * 总长度
     **/
    int length;
    /**
     * 一个正弦波的长度
     **/
    int waveLen;
    /**
     * 频率
     **/
    int Hz;
    /**
     * 正弦波
     **/
    byte[] wave;

    public AudioTrackManager() {
        wave = new byte[RATE];
    }

    /**
     * 设置频率
     *
     * @param rate
     */
    public void start(int rate) {
        stop();
        if (rate > 0) {
            Hz = rate;
            waveLen = RATE / Hz;
            length = waveLen * Hz;
            audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, RATE,
                    AudioFormat.CHANNEL_CONFIGURATION_STEREO, // CHANNEL_CONFIGURATION_MONO,
                    AudioFormat.ENCODING_PCM_8BIT, length, AudioTrack.MODE_STREAM);
            //生成正弦波
            wave = SinWave.sin(wave, waveLen, length);
            if (audioTrack != null) {
                audioTrack.play();
            }
        } else {
            return;
        }
    }

    private Thread thread;
    private boolean isPlaying = false;
    private boolean lock = false;

    public void Song() {
        isPlaying = true;
        int i = 1;
        float FDELTA = tablef[0];//Ct大调基础
        float DELAY = (float) (60000 * 1f / tablep[0]);
        while (true) {

            if (tablef[i] == 0xff) {
                // MP3end = 0;
                isPlaying = false;
                break;
            }
            if (tablef[i] == 100)
                ;
            else {
                //延时
                //获取频率数据写出
                Hz = (int) (gangqing[(int) (tablef[i] + FDELTA)]);
                waveLen = RATE / Hz;
                length = waveLen * Hz;
                lock = true;
                wave = SinWave.sin(wave, waveLen, length);
                lock = false;
                if (thread == null) {
                    thread = new Thread(new Runnable() {

                        @Override
                        public void run() {
                            while (isPlaying) {
                                if (!lock)
                                    audioTrack.write(wave, 0, length);
                            }
                        }
                    });

                    thread.start();
                }

                try {
                    Thread.sleep((long) (DELAY * tablep[i]));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
            i++;
        }
    }

    /**
     * 停止播放
     */
    public void stop() {
        if (audioTrack != null) {
            audioTrack.stop();
            audioTrack.release();
            audioTrack = null;
            thread = null;
        }
    }

    /**
     * 设置音量
     *
     * @param volume
     */
    public void setVolume(float volume) {
        this.volume = volume;
        if (audioTrack != null) {
            switch (channel) {
                case LEFT:
                    audioTrack.setStereoVolume(volume / MAXVOLUME, 0f);
                    break;
                case RIGHT:
                    audioTrack.setStereoVolume(0f, volume / MAXVOLUME);
                    break;
                case DOUBLE:
                    audioTrack.setStereoVolume(volume / MAXVOLUME, volume / MAXVOLUME);
                    break;
            }
        }
    }

    /**
     * 设置声道
     *
     * @param channel
     */
    public void setChannel(int channel) {
        this.channel = channel;
        setVolume(volume);
    }

    public boolean isPlaying() {
        return isPlaying;
    }
}

