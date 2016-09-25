package com.jisu.selidpic;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Message;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.VideoView;
import android.widget.ImageView;
import android.graphics.drawable.Drawable;

import java.io.ByteArrayOutputStream;
import java.util.Timer;
import java.util.TimerTask;
import android.os.Handler;

public class CameraActivity extends Activity implements SurfaceHolder.Callback, Camera.PreviewCallback, SensorEventListener {
    int width, height, cameraId, statview;
    double display;
    int ppi;
    static Camera camera;
    SurfaceHolder holder;
    VideoView videoView;
    LinearLayout linearLayout;
    ByteArrayOutputStream outstr;
    ToggleButton toggleButton;
    ImageButton button3, button4, camera_switch;

    TextView auto_textview_number, auto_textview_guide;

    ImageView imgStatus, camera_helper;

    Drawable camStatDefault;
    Drawable camStat1;
    Drawable camStat2;
    Drawable camStat3;
    Drawable camStat4;

    Drawable camera_user;
    Drawable camera_auto;

    //********************아래의 네줄은 차례대로 width와 height의 최대 픽셀을 가져오는 코드와,
    //그 최대 픽셀을 기준으로 height부의 위, 아래 margin, 그리고 그 margin을 제외한 비디오뷰의 높이를 설정하는 코드임

    int screenWidth, screenHeight;
    int camMargin;
    int camHeight;

    byte myData[] = new byte[10000000];

    private SensorManager sensorManager;
    private Sensor sensor;

    private float sensorValue = 0;

    private Handler handler;
    private Timer timer;

    private Boolean brightness_ok = false;
    private Boolean autoshot = false;



    //******************************************************************************************************************************
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        initView();
        initListener();
        initSensor();
        setCamera();
        camera_switch.setImageDrawable(camera_user);

        timer = new Timer(false);
        handler = new Handler();

        final Handler timeHandler = new Handler(){
            public void handleMessage(Message msg){
                super.handleMessage(msg);
                if(msg.arg1==5){
                    auto_textview_guide.setVisibility(View.VISIBLE);
                }
                else if(msg.arg1==4){
                    auto_textview_guide.setVisibility(View.INVISIBLE);
                    auto_textview_number.setVisibility(View.VISIBLE);
                    auto_textview_number.setText("3");
                }else if(msg.arg1==3){
                    auto_textview_number.setText("2");
                }
                else if(msg.arg1==2){
                    auto_textview_number.setText("1");
                }
            }
        };

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (sensorValue < 30) {
                            Toast.makeText(CameraActivity.this, "사용자의 조명이 너무 어두움", Toast.LENGTH_SHORT).show();
                            brightness_ok = false;
                        } else if (sensorValue > 320) {
                            Toast.makeText(CameraActivity.this, "사용자의 조명이 너무 밝음", Toast.LENGTH_SHORT).show();
                            brightness_ok = false;
                        } else {
                            brightness_ok = true;
                            if(autoshot){
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        int i=5;
                                        while(autoshot){
                                            if(i==1){
                                                func_take_picture();
                                                break;
                                            }
                                            else {
                                                Message msg = timeHandler.obtainMessage();
                                                msg.arg1 = i;
                                                timeHandler.sendMessage(msg);
                                            }

                                            if(i==5){
                                                try {
                                                    Thread.sleep(3000);
                                                } catch (InterruptedException e) {
                                                    e.printStackTrace();
                                                }
                                            }else {
                                                try {
                                                    Thread.sleep(1000);
                                                } catch (InterruptedException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                            i--;
                                        }
                                    }
                                }).start();
                            }
                        }
                    }
                });
            }
        }, 5000, 9000);

        //************************아래는 현재 촬영중인 포맷의 status view를 선택하기 위한 코드 *********************************** ImageView (Camera Status)
        imgStatus = (ImageView)findViewById(R.id.imageView);
        camStatDefault = getResources().getDrawable(R.mipmap.camera_status_5);
        camStat1 = getResources().getDrawable(R.mipmap.camera_status_1);
        camStat2 = getResources().getDrawable(R.mipmap.camera_status_2);
        camStat3 = getResources().getDrawable(R.mipmap.camera_status_3);
        camStat4 = getResources().getDrawable(R.mipmap.camera_status_4);
        camera_user = getResources().getDrawable(R.drawable.camera_switch);
        camera_auto = getResources().getDrawable(R.mipmap.camera_auto);
        Intent intent = getIntent();
        statview = intent.getIntExtra("view", 5);

        switch(statview)
        {
            case 1:
                imgStatus.setImageDrawable(camStat1);
                break;
            case 2:
                imgStatus.setImageDrawable(camStat2);
                break;
            case 3:
                imgStatus.setImageDrawable(camStat3);
                break;
            case 4:
                imgStatus.setImageDrawable(camStat4);
                break;
            case 5:
                imgStatus.setImageDrawable(camStatDefault);
                break;
        }

    }

    private void func_take_picture() {
        byte[] byteArray = outstr.toByteArray();
        Intent intent = new Intent(CameraActivity.this, TouchToolActivity.class);
        intent.putExtra("image",byteArray);
        intent.putExtra("width", width);
        intent.putExtra("height", height);
        intent.putExtra("statview", statview);
        intent.putExtra("ppi", ppi);
        startActivity(intent);
        finish();
    }

    //****************************************************************************************************************************** Button and switches
    private void initListener() {
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                camera.setPreviewCallback(null);
                camera.stopPreview();
                camera.release();
                camera=null;
                Intent Intent = new Intent(CameraActivity.this, MainActivity.class);
                startActivity(Intent);
                finish();
            }
        });

        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(CameraActivity.this, "Gallery", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent,1);
            }
        });

        camera_switch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                func_take_picture();
            }
        });

        toggleButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(toggleButton.isChecked()){
                    camera_switch.setClickable(true);
                    camera_switch.setImageDrawable(camera_user);
                    autoshot = false;
                    auto_textview_guide.setVisibility(View.INVISIBLE);
                    auto_textview_number.setVisibility(View.INVISIBLE);
                }
                else{
                    camera_switch.setClickable(false);
                    camera_switch.setImageDrawable(camera_auto);
                    autoshot = true;

                    auto_textview_number.setText("");
                    auto_textview_number.setVisibility(View.VISIBLE);
                }
            }
        });

        camera_helper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                camera_helper.setVisibility(View.GONE);

            }
        });
    }

    //****************************************************************************************************************************** Video view resume, pause

    @Override
    protected void onResume() {
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI);
        videoView.setVisibility(View.VISIBLE);
        if(camera==null){
            setCamera();
        }
        camera.setPreviewCallback(this);
        camera.startPreview();
        super.onResume();
    }

    @Override
    protected void onPause() {
        videoView.setVisibility(View.GONE);
        holder.removeCallback(this);
        if(camera!=null){
            camera.setPreviewCallback(null);
            camera.stopPreview();
            camera.release();
            camera = null;
        }
        sensorManager.unregisterListener(this);
        super.onPause();
    }
    //****************************************************************************************************************************** Camera Size setup, basic (optimal size)

    private void setCamera() {
        cameraId = 0;
        int numberOfCameras = Camera.getNumberOfCameras();
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();

        for (int i = 0; i < numberOfCameras; i++) {
            Camera.getCameraInfo(i, cameraInfo);

            /* 전면 카메라를 쓸 것인지 후면 카메라를 쓸것인지 설정 시 */
            /* 전면카메라 사용시 CAMERA_FACING_FRONT 로 조건절 */
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT)
                cameraId = i;
        }
        camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
        Camera.Parameters params = camera.getParameters();

        params.setPreviewFpsRange(15000, 30000);

        params.setPreviewFormat(ImageFormat.NV21);
        params.setPreviewFrameRate(30);
        camera.setDisplayOrientation(90); //세로 모드 가정
        camera.setParameters(params);

        holder = videoView.getHolder();
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        holder.addCallback(this);
    }

    private void initSensor() {
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
    }
    //****************************************************************************************************************************** VideoView size crop,
    private void initView() {
        Intent intent = getIntent();
        width = intent.getIntExtra("width", 0);
        height = intent.getIntExtra("height", 0);

        screenWidth = getContext().getResources().getDisplayMetrics().widthPixels;
        screenHeight = getContext().getResources().getDisplayMetrics().heightPixels;
        camMargin = (int)(screenHeight * 0.137);
        camHeight = (int)(screenHeight * 0.726);

        float xdpi = getContext().getResources().getDisplayMetrics().xdpi;
        float ydpi = getContext().getResources().getDisplayMetrics().ydpi;
        float x_inch = screenWidth/xdpi;
        float y_inch = screenHeight/ydpi;
        display = Math.sqrt(x_inch*x_inch+y_inch*y_inch);
        ppi = (int)(Math.sqrt(screenWidth*screenWidth+screenHeight*screenHeight)/display);


        Toast.makeText(CameraActivity.this,"screenWidth"+screenWidth+"+"+"screenHeight"+screenHeight,Toast.LENGTH_SHORT).show();

        Toast.makeText(CameraActivity.this,width+"+"+height,Toast.LENGTH_SHORT).show();

        videoView = (VideoView) findViewById(R.id.videoView);

        //LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(screenWidth, camHeight);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(screenWidth, screenHeight);

        videoView.setLayoutParams(layoutParams);

        button3 = (ImageButton) findViewById(R.id.camera_btn_back);
        button4 = (ImageButton) findViewById(R.id.camera_btn_gal);
        camera_switch = (ImageButton) findViewById(R.id.camera_switch);
        camera_helper = (ImageView) findViewById(R.id.camera_helper);
        toggleButton = (ToggleButton) findViewById(R.id.toggleButton);
        linearLayout = (LinearLayout) findViewById(R.id.camera_linearlayout_videoview);

        auto_textview_guide = (TextView) findViewById(R.id.camera_auto_guide_textview);
        auto_textview_number = (TextView) findViewById(R.id.camera_auto_guide_number);

        //************************camMargin설정 (위, 아래)

        layoutParams.gravity = Gravity.CENTER;
    }
    private Context getContext()//************************현재 context를 불러오는 함수
    {
        Context mContext;
        mContext = getApplicationContext();
        return mContext;
    }

//****************************************************************************************************************************** File save directory


//****************************************************************************************************************************** TakePicture에 필요한 함수들

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        camera.setPreviewCallback(this);
    }


    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        sensorManager.unregisterListener(this);
    }
//******************************************************************************************************************************


    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        camera.unlock();
        try {
            camera.reconnect();
            camera.setPreviewDisplay(holder);
        } catch (Exception e) {
        }
        camera.startPreview();
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        for(int i = 0; i < data.length; i++){
            myData[i] = data[i];
        }
        Camera.Parameters parameters = camera.getParameters();
        int width = parameters.getPreviewSize().width;
        int height = parameters.getPreviewSize().height;
        outstr = new ByteArrayOutputStream();
        Rect rect = new Rect(0, 0, width, height);
        YuvImage yuvimage=new YuvImage(data,ImageFormat.NV21,width,height,null);
        yuvimage.compressToJpeg(rect, 50, outstr);
        Bitmap bmp = BitmapFactory.decodeByteArray(outstr.toByteArray(), 0, outstr.size());
        bmp.compress(Bitmap.CompressFormat.JPEG, 50, outstr);

    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == Sensor.TYPE_LIGHT) {
            sensorValue = sensorEvent.values[0];
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    protected void onDestroy() {
        timer.cancel();
        sensorManager.unregisterListener(this);
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        sensorManager.unregisterListener(this);
        if(camera != null){
            camera.stopPreview();
            camera.release();
            camera=null;
        }
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        Intent intent2 = new Intent(CameraActivity.this, CautionActivity.class);
        intent2.putExtra("width", width);
        intent2.putExtra("height", height);
        intent2.putExtra("view", statview);
        startActivity(intent2);
        finish();
    }
}
