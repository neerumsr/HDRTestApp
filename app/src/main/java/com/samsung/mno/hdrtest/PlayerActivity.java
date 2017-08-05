package com.samsung.mno.hdrtest;


/**
 * Created by manas on 7/17/17.
 * Modified by Siva on 7/31/17.
 */

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcel;
import android.util.Log;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.media.AudioManager;
import android.widget.CheckBox;

import com.samsung.mno.hdrtest.R;

/*
        HDR_TYPE_DOLBY_VISION
        Constant Value: 1 (0x00000001)

        HDR_TYPE_HDR10
        Constant Value: 2 (0x00000002)

        HDR_TYPE_HLG
        Constant Value: 3 (0x00000003)

        INVALID_LUMINANCE
        Constant Value: -1.0
*/
public class PlayerActivity extends Activity
        implements SurfaceHolder.Callback {

    Uri targetUri;

    MediaPlayer mediaPlayer;
    SurfaceView surfaceView;
    SurfaceHolder surfaceHolder;
    boolean pausing = false;
    String TAG = "MNO_Team2_HDRTestApp";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        CheckBox chkbxAHDRCaps = (CheckBox) findViewById(R.id.aHDRcheck);
        CheckBox chkbxSHDRCaps = (CheckBox) findViewById(R.id.sHDRcheckBx);
        chkbxAHDRCaps.setChecked(false);
        chkbxSHDRCaps.setChecked(false);

        //Android API to check HDR capabilities
        WindowManager wm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        int[] types = display.getHdrCapabilities().getSupportedHdrTypes();
        Log.v(TAG, "Checking Android HDR Caps ... ");

        if (types.length <= 0)
            Log.d(TAG, "No Android HDR Caps:  Caps returned  " + types.length);


        for (int i : types) {
            Log.d(TAG, "Supported HDR Types : " + i);
            chkbxAHDRCaps.setChecked(true);
        }

        //Samsung API to check HDR capabilities
        Log.v(TAG, "Checking Samsung HDR Caps");
        PackageManager pm = getPackageManager();
        boolean capable = pm.hasSystemFeature("com.samsung.feature.hdr_capable");
        chkbxSHDRCaps.setChecked(capable);
        Log.d(TAG, "Samsung is HDR capable " + capable);


        Button buttonPlayVideo = (Button) findViewById(R.id.playvideoplayer);
        Button buttonPauseVideo = (Button) findViewById(R.id.pausevideoplayer);
        Button buttonAHDRVideo = (Button) findViewById(R.id.playAndroidHDRvideoplayer);

        getWindow().setFormat(PixelFormat.UNKNOWN);
        surfaceView = (SurfaceView) findViewById(R.id.surfaceview);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setFixedSize(2000, 800);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        mediaPlayer = new MediaPlayer();


        buttonPlayVideo.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                pausing = false;

                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.reset();
                }

                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mediaPlayer.setDisplay(surfaceHolder);
                //String sourceUrl = Environment.getExternalStorageDirectory().getPath() + "/" + "Sony_4K_HDR_Camp.mp4";
                String sourceUrl = Environment.getExternalStorageDirectory().getPath() + "/" + "The_World_in_HDR_in_4K_HDR10.mkv";

                try {
                    mediaPlayer.setDataSource(sourceUrl);
                    mediaPlayer.prepare();
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                setParameter(mediaPlayer, 35000, "vendorName");
                setParameter(mediaPlayer, 35001, "1"); //set HDR mode ON (0 for off)
                Log.d(TAG, "Before start media play and playing from " + sourceUrl);
                mediaPlayer.start();

            }
        });

        buttonAHDRVideo.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                pausing = false;
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.reset();
                }

                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mediaPlayer.setDisplay(surfaceHolder);
               //tring sourceUrl = Environment.getExternalStorageDirectory().getPath() + "/" + "Sony_4K_HDR_Camp.mp4";
               String sourceUrl = Environment.getExternalStorageDirectory().getPath() + "/" + "The_World_in_HDR_in_4K_HDR10.mkv";

                try {
                    mediaPlayer.setDataSource(sourceUrl);
                    mediaPlayer.prepare();
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Log.d("MNO_TEAM2", "Android way of playing " + sourceUrl);
                mediaPlayer.start();
            }
        });

        buttonPauseVideo.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                if (pausing) {
                    pausing = false;
                    mediaPlayer.start();
                } else {
                    pausing = true;
                    mediaPlayer.pause();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        mediaPlayer.release();
    }

    @Override
    public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
        // TODO Auto-generated method stub

    }

    @Override
    public void surfaceCreated(SurfaceHolder arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder arg0) {
        // TODO Auto-generated method stub

    }

    //HDR API FROM HQ
    private void setParameter(MediaPlayer player, int key, String value) {
        String className = "android.media.MediaPlayer";
        try {
            Class mediaplayerclass = Class.forName(className);
            Method setParameter_method =
                    mediaplayerclass.getMethod("setParameter", Integer.TYPE, Parcel.class);
            if (setParameter_method != null) {
                Parcel p = Parcel.obtain();
                p.writeString(value);
                setParameter_method.invoke(player, key, p);
                p.recycle();
            }
        } catch (ClassNotFoundException | InvocationTargetException |
                IllegalAccessException | NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

}

