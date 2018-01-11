package com.zjr.demo.ui;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zjr.demo.RoundButtonDrawable;

/**
 * Created by zjr on 2018/1/11.
 */

public class VoicePayButton extends LinearLayout{

    private TextView durationTextView;
    private TextView textView;
    private MediaPlayer mPlayer;
    private int duration;
    private boolean isPlaying;

    public VoicePayButton(Context context) {
        this(context, null);
    }

    public VoicePayButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VoicePayButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        RoundButtonDrawable drawable = RoundButtonDrawable.fromAttributeSet(context, attrs, defStyle);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            setBackground(drawable);
        } else {
            setBackgroundDrawable(drawable);
        }

        setOrientation(HORIZONTAL);


        textView = new TextView(getContext());
        durationTextView = new TextView(getContext());
        addView(textView);
        addView(durationTextView);

        textView.setText("播放语音");
    }

    public void payVoice(String filePath){
        if(isPlaying)return;

        if(filePath!=null&&filePath.startsWith("http")){
            //get from remote
        }else{
            play(filePath);
        }
    }

    private void play(String filePath){
        mPlayer = new MediaPlayer();
        try {
            mPlayer.setDataSource(filePath);
            duration = mPlayer.getDuration();
            mPlayer.start();
            isPlaying = true;
            textView.setText("停止播放");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stop(){
        if(mPlayer!=null){
            mPlayer.stop();
            mPlayer.release();
            isPlaying = false;
            textView.setText("播放语音");
        }
    }
}
