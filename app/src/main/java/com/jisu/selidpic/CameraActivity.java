package com.jisu.selidpic;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Paint;
import android.graphics.Path;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;
import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RadioGroup;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import android.os.Handler;

public class CameraActivity extends Activity implements SurfaceHolder.Callback, Camera.PreviewCallback, SensorEventListener {
    int width, height, cameraId, view;
    static Camera camera;
    SurfaceHolder holder;
    VideoView videoView;

    ImageButton button3, button4;
    LinearLayout linearLayout;
    TextView textView;

    ImageView imgStatus;
    Drawable camStatDefault;
    Drawable camStat1;
    Drawable camStat2;
    Drawable camStat3;
    Drawable camStat4;

    //********************아래의 네줄은 차례대로 width와 height의 최대 픽셀을 가져오는 코드와,
    //그 최대 픽셀을 기준으로 height부의 위, 아래 margin, 그리고 그 margin을 제외한 비디오뷰의 높이를 설정하는 코드임
    int screenWidth = getContext().getResources().getDisplayMetrics().widthPixels;
    int screenHeight = getContext().getResources().getDisplayMetrics().heightPixels;
    int camMargin = (int)(screenHeight * 0.137);
    int camHeight = (int)(screenHeight * 0.726);

    int frameHeight = 1280;
    int frameWidth = 720;
    int frameSize = frameHeight * frameWidth;
    int rgb[];
    byte myData[] = new byte[frameSize];

    private SensorManager sensorManager;
    private Sensor sensor;

    private float sensorValue = 0;

    private Handler handler;
    private Timer timer;

    private Boolean brightness_ok = false;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        initView();
        initListener();
        initSensor();
        setCamera();
        timer = new Timer(false);
        handler = new Handler();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (sensorValue < 50) {
                            Toast.makeText(CameraActivity.this, "아,ㅇ,어두워....", Toast.LENGTH_SHORT).show();
                            brightness_ok = false;
                        } else if (sensorValue > 320) {
                            Toast.makeText(CameraActivity.this, "ㅇ,아...눈부셔....", Toast.LENGTH_SHORT).show();
                            brightness_ok = false;
                        } else {
                            brightness_ok = true;
                        }
                    }
                });
            }
        }, 5000, 5000);
        //DrawOnTop mDraw = new DrawOnTop(this);
        //addContentView(mDraw, new ViewGroup.LayoutParams
        //        (ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        //************************아래는 현재 촬영중인 포맷의 status view를 선택하기 위한 코드 ************************
        imgStatus = (ImageView)findViewById(R.id.imageView);

        camStatDefault = getResources().getDrawable(R.mipmap.camera_status_5);
        camStat1 = getResources().getDrawable(R.mipmap.camera_status_1);
        camStat2 = getResources().getDrawable(R.mipmap.camera_status_2);
        camStat3 = getResources().getDrawable(R.mipmap.camera_status_3);
        camStat4 = getResources().getDrawable(R.mipmap.camera_status_4);
        Intent intent = getIntent();
        view = intent.getIntExtra("view", 5);
        Toast.makeText(CameraActivity.this,"ButtonView"+view,Toast.LENGTH_SHORT).show();

        switch(view)
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
        //***********************************************************************************************************
    }

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
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI);

    }

    @Override
    protected void onPause() {
        videoView.setVisibility(View.GONE);
        holder.removeCallback(this);
        camera.setPreviewCallback(null);
        camera.stopPreview();
        camera.release();
        camera = null;
        sensorManager.unregisterListener(this);
        super.onPause();
    }

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
        params.setPreviewSize(1280, 720);
        rgb = new int[frameSize];
        //params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO); //video 화면이 변할 때마다 자동 포커스 맞춤

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

    private void initView() {

        Intent intent = getIntent();
        width = intent.getIntExtra("width", 0);
        height = intent.getIntExtra("height", 0);

        Toast.makeText(CameraActivity.this,"screenWidth"+screenWidth+"+"+"screenHeight"+screenHeight,Toast.LENGTH_SHORT).show();

        Toast.makeText(CameraActivity.this,width+"+"+height,Toast.LENGTH_SHORT).show();

        linearLayout = (LinearLayout) findViewById(R.id.camera_linearlayout_videoview);
        videoView = (VideoView) findViewById(R.id.videoView);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(screenWidth, camHeight);
        videoView.setLayoutParams(layoutParams);



        button3 = (ImageButton) findViewById(R.id.camera_btn_back);
        button4 = (ImageButton) findViewById(R.id.camera_btn_gal);

        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent Intent1 = new Intent(CameraActivity.this, AfterActivity.class);
                startActivity(Intent1);
                finish();

            }
        });
        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(CameraActivity.this, "Gallery", Toast.LENGTH_SHORT).show();
            }
        });

        //************************camMargin설정 (위, 아래)
        layoutParams.setMargins(0, camMargin, 0, camMargin);
        layoutParams.gravity = Gravity.CENTER;


        //  textView = (TextView) findViewById(R.id.textview);
    }

    //************************현재 context를 불러오는 함수
    private Context getContext()
    {
        return getApplicationContext();
    }



    MediaPlayer.OnVideoSizeChangedListener sizeChangeListener = new MediaPlayer.OnVideoSizeChangedListener() {
        @Override
        public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
        }
    };



    //************************비디오뷰의 원본비율을 유지한채로 사이즈를 조절하는 함수부
    //비디오뷰의 사이즈가 변경됨을 핸들러를 통해 알려줌
    //디버깅 필요 -애플리케이션 중단됨
/*
    private MediaPlayer.OnVideoSizeChangedListener onVideoSizeChangedListener =
            new MediaPlayer.OnVideoSizeChangedListener() {
                public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(screenWidth, camHeight);
                    videoView.setLayoutParams(layoutParams);}
            };

    private MediaPlayer.OnPreparedListener onPrepared = new MediaPlayer.OnPreparedListener() {
        public void onPrepared(MediaPlayer mp) {
       mp.setOnVideoSizeChangedListener(onVideoSizeChangedListener);

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(screenWidth, camHeight);
            videoView.setLayoutParams(layoutParams);
        }
    };

    videoView.setOnPreparedListener(onPrepared);
*/


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
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        camera.setPreviewCallback(this);
    }


    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        sensorManager.unregisterListener(this);
        /*
        try {
            //camera.reconnect();

            if(camera!=null){
                holder.removeCallback(this);
                camera.setPreviewCallback(null);
                //videoView.setVisibility(View.GONE);
                //camera.setPreviewDisplay(null);
                camera.stopPreview();
                camera.release();

                camera = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        for (int i = 0; i < frameSize; i++) {
            myData[i] = data[i];
        }
        /*
        textView.setText("Data: "+data.toString());
        textView.setHighlightColor(Color.WHITE);
        textView.setTextColor(Color.WHITE);
        textView.setShadowLayer(2.0f, 1.0f, 1.0f, Color.BLACK) ;
        */
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
        /*
        camera.stopPreview();
        camera.release();
        camera = null;
        */
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        sensorManager.unregisterListener(this);
        try{
            /*
            camera.setPreviewCallback(null);
            camera.stopPreview();
            camera.release();
            camera = null;
            videoView.setVisibility(View.GONE);
            holder.removeCallback(this);*/
        }catch(Exception e){
            e.printStackTrace();
        }
        super.onStop();
    }
/*
    public class DrawOnTop extends View {

        public DrawOnTop(Context context) {
            super(context);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            Paint paint = new Paint();

            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.BLACK);
            paint.setTextSize(20);
            canvas.drawText("Test Text", 20, 20, paint);
            *//*
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(2);
            paint.setColor(Color.RED);

            Path path = new Path();

            path.moveTo(0, 280);
            path.cubicTo(80, 200, 150, 190, 200, 170);

            path.offset(50, 450);
            canvas.drawPath(path, paint);

            Path path2 = new Path();

            path2.moveTo(670, 280);
            path2.cubicTo(590, 200, 520, 190, 470, 170);
            path2.offset(0, 450);
            canvas.drawPath(path2, paint);


            super.onDraw(canvas);
        }
    }
*/
    private void spoidRGB() {
        for (int i = 0, yp = 0; i < frameHeight; i++) {
            int uvp = frameSize + (i >> 1) * frameWidth, u = 0, v = 0;
            for (int j = 0; j < frameWidth; j++, yp++) {
                int y = (0xff & ((int) myData[yp])) - 16;
                if (y < 0) y = 0;
                if ((i & 1) == 0) {
                    v = (0xff & myData[uvp++]) - 128;
                    u = (0xff & myData[uvp++]) - 128;
                }

                int y1192 = 1192 * y;
                int r = (y1192 + 1634 * v);
                int g = (y1192 - 833 * v - 400 * u);
                int b = (y1192 + 2066 * u);

                if (r < 0) r = 0;
                else if (r > 262143) r = 262143;
                if (g < 0) g = 0;
                else if (g > 262143) g = 262143;
                if (b < 0) b = 0;
                else if (b > 262143) b = 262143;

                rgb[yp] = 0xff000000 |
                        ((r << 6) & 0xff0000) |
                        ((g >> 2) & 0xff00) |
                        ((b >> 10) & 0xff);
            }
        }
    }

    private int getR(int value) {
        return (value >> 16) & 0xff;
    }

    private int getG(int value) {
        return (value >> 8) & 0xff;
    }

    private int getB(int value) {
        return (value) & 0xff;
    }

    private int getColorTypical() {
        int sumR, sumG, sumB, avrR, avrG, avrB, resultRGB;
        sumR = sumG = sumB = avrR = avrG = avrB = resultRGB = 0;

        for (int k = 0; k < frameSize; k++) {
            sumR += getR(rgb[k]);
            sumG += getG(rgb[k]);
            sumB += getB(rgb[k]);
        }

        avrR = sumR / frameSize;
        avrG = sumG / frameSize;
        avrB = sumB / frameSize;

        resultRGB = avrR | (avrG << 8) | (avrB << 16);

        return resultRGB;
    }

    @Override
        public void onBackPressed() {
            Intent Intent2 = new Intent(CameraActivity.this, CautionActivity.class);
            startActivity(Intent2);
            finish();
        }
    }


