package com.jisu.selidpic;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.VideoView;
import android.widget.ImageView;
import android.graphics.drawable.Drawable;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import android.os.Handler;

public class CameraActivity extends Activity implements SurfaceHolder.Callback, Camera.PreviewCallback, SensorEventListener {
    int width, height, cameraId, statview;
    double display;
    static Camera camera;
    SurfaceHolder holder;
    VideoView videoView;
    LinearLayout linearLayout;
    ByteArrayOutputStream outstr;
    ToggleButton toggleButton;
    Bitmap bmp;
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

    ProgressBar progressBar;

    //********************아래의 네줄은 차례대로 width와 height의 최대 픽셀을 가져오는 코드와,
    //그 최대 픽셀을 기준으로 height부의 위, 아래 margin, 그리고 그 margin을 제외한 비디오뷰의 높이를 설정하는 코드임

    int screenWidth, screenHeight;
    int camMargin;// = (int)(screenHeight * 0.137);
    int camHeight;// = (int)(screenHeight * 0.726);

    int frameHeight = 1280;
    int frameWidth = 720;
    int frameSize = frameHeight * frameWidth;
    int rgb[];
    byte myData[] = new byte[10000000];

    private SensorManager sensorManager;
    private Sensor sensor;

    private float sensorValue = 0;

    private Handler handler;
    private Timer timer;

    private Boolean brightness_ok = false;
    private Boolean autoshot = false;

    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;


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
                if(msg.arg1==4){
                    auto_textview_guide.setVisibility(View.VISIBLE);
                }
                else if(msg.arg1==3){
                    auto_textview_guide.setVisibility(View.INVISIBLE);
                    auto_textview_number.setVisibility(View.VISIBLE);
                    auto_textview_number.setText("3");
                }else if(msg.arg1==2){
                    auto_textview_number.setText("2");
                }
                else if(msg.arg2==1){
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
                            Log.i("MyTag", "autoshot debug, autoshot:"+autoshot.booleanValue());
                            if(autoshot){
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        int i=4;
                                        Log.i("MyTag", "autoshot debug, i="+i);
                                        while(autoshot){
                                            if(i==0){
                                                byte[] byteArray = outstr.toByteArray();
                                                Intent intent = new Intent(CameraActivity.this, AfterActivity.class);
                                                intent.putExtra("image",byteArray);
                                                intent.putExtra("width", width);
                                                intent.putExtra("height", height);
                                                intent.putExtra("screenWidth", screenWidth);
                                                intent.putExtra("screenHeight", screenHeight);
                                                intent.putExtra("statview", statview);
                                                intent.putExtra("display", display);
                                                startActivity(intent);
                                                finish();
                                                break;
                                            }else {
                                                Message msg = timeHandler.obtainMessage();
                                                msg.arg1 = i;
                                                timeHandler.sendMessage(msg);
                                            }

                                            if(i==4){
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
        }, 5000, 5000);
        //DrawOnTop mDraw = new DrawOnTop(this);
        //addContentView(mDraw, new ViewGroup.LayoutParams
        //        (ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

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

                byte[] byteArray = outstr.toByteArray();
                Intent intent = new Intent(CameraActivity.this, AfterActivity.class);
                intent.putExtra("image",byteArray);
                intent.putExtra("width", width);
                intent.putExtra("height", height);
                intent.putExtra("screenWidth", screenWidth);
                intent.putExtra("screenHeight", screenHeight);
                intent.putExtra("statview", statview);
                intent.putExtra("display", display);
                startActivity(intent);
                finish();
            }
        });


        toggleButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if(toggleButton.isChecked()){
                    camera_switch.setClickable(true);
                    camera_switch.setImageDrawable(camera_user);
                    autoshot = false;
                }
                else{
                    camera_switch.setClickable(false);
                    camera_switch.setImageDrawable(camera_auto);
                    autoshot = true;
/*
                    if(brightness_ok){

                    }
                    TimerTask taskCheck = new TimerTask() {
                        @Override

                        public void run() {

                            byte[] byteArray = outstr.toByteArray();
                            Intent intent = new Intent(CameraActivity.this, AfterActivity.class);
                            intent.putExtra("image",byteArray);
                            intent.putExtra("width", width);
                            intent.putExtra("height", height);
                            intent.putExtra("screenWidth", screenWidth);
                            intent.putExtra("screenHeight", screenHeight);
                            intent.putExtra("statview", statview);
                            startActivity(intent);
                            finish();

                        }
                    };

                    Timer timer = new Timer();
                    timer.schedule(taskCheck, 5000);
                    */

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

        //카메라에서 찍을 수 있는 모든 사이즈를 가지고 와서 그 중에 하나를 선택한다.
        /*
        List<Camera.Size> pictureSizes = params.getSupportedPictureSizes();
        Camera.Size optimalSize = getOptimalPreviewSize(pictureSizes, screenWidth, camHeight);
        params.setPreviewSize(optimalSize.width, optimalSize.height);
*/
        List<Camera.Size> pictureSizes = params.getSupportedPictureSizes();
        for(int i=0;i<pictureSizes.size();i++){
            Log.d("MyTag", "Supported Picture Size : "+pictureSizes.get(i).width +" "+pictureSizes.get(i).height);
        }
        /*Camera.Size optimalSize = getOptimalPreviewSize(pictureSizes, screenWidth, camHeight);
        Log.d("MyTag", "Optimal Preview Size : "+optimalSize.width+" "+optimalSize.height);
        params.setPreviewSize(optimalSize.width, optimalSize.height);*/
        /*
        frameHeight = optimalSize.height;
        frameWidth = optimalSize.width;
        frameSize = frameHeight * frameWidth;*/

        //params.setPreviewSize(screenWidth, camHeight);
        //params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO); //video 화면이 변할 때마다 자동 포커스 맞춤
        //rgb = new int[frameSize];

        params.setPreviewFormat(ImageFormat.NV21);
        params.setPreviewFrameRate(30);
        camera.setDisplayOrientation(90); //세로 모드 가정
        camera.setParameters(params);

        holder = videoView.getHolder();
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        holder.addCallback(this);
    }
/*
    private Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int width, int height) {
        final double ASPECT_TOLERANCE = 0.05;
        double targetRatio = (double) width / height;
        if (sizes == null) {
            return null;
        }

        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = height;

        // Try to find an size match aspect ratio and size
        for (Camera.Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) {
                continue;
            }
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        // Cannot find the one match the aspect ratio, ignore the requirement
        if (optimalSize == null) {
            Log.d("MyTag", "optimalSize를 찾지 못함");
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        Log.i("optimal size", ""+optimalSize.width+" x "+optimalSize.height);
        return optimalSize;
    }
*/

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

        //layoutParams.setMargins(0, camMargin, 0, camMargin);

        layoutParams.gravity = Gravity.CENTER;

        //videoView.setOnPreparedListener(onPrepared);
    }
/*/*//******************************************************************************************************************************


     /*//************************비디오뷰의 원본비율을 유지한채로 사이즈를 조절하는 함수부
     //비디오뷰의 사이즈가 변경됨을 핸들러를 통해 알려줌
     //디버깅 필요 -애플리케이션 중단됨

     private MediaPlayer.OnVideoSizeChangedListener onVideoSizeChangedListener =
     new MediaPlayer.OnVideoSizeChangedListener() {
     public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
     LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(screenWidth, camHeight);
     videoView.setLayoutParams(layoutParams);
     }
     };



     private MediaPlayer.OnPreparedListener onPrepared = new MediaPlayer.OnPreparedListener() {
     public void onPrepared(MediaPlayer mp) {
     mp.setOnVideoSizeChangedListener(onVideoSizeChangedListener);

     LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(screenWidth, camHeight);
     videoView.setLayoutParams(layoutParams);
     }
     };

     MediaPlayer.OnVideoSizeChangedListener sizeChangeListener = new MediaPlayer.OnVideoSizeChangedListener() {
    @Override
    public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
    }
    };
     /*//*******************************************************************************************************************************/
    private Context getContext()//************************현재 context를 불러오는 함수
    {
        Context mContext;
        mContext = getApplicationContext();
        return mContext;
    }

//****************************************************************************************************************************** File save directory


//****************************************************************************************************************************** TakePicture에 필요한 함수들


    private void showScreen(Bitmap bm) {

    }

    Camera.ShutterCallback shutterCallback = new Camera.ShutterCallback() {
        public void onShutter() {
            Log.d("MyTag", "onShutter'd");
        }
    };

    Camera.PictureCallback rawCallback = new Camera.PictureCallback() {
        public void onPictureTaken(byte[] data, Camera camera) {
            Log.d("MyTag", "onPictureTaken - raw");
        }
    };
    private Camera.PictureCallback mPicutureListener =
            new Camera.PictureCallback() {
                public void onPictureTaken(byte[] data, Camera camera) {
                    Log.d("MyTag", "Picture Taken");
                    if (data != null) {
                        Log.d("MyTag", "JPEG Picture Taken");
                        BitmapFactory.Options options =
                                new BitmapFactory.Options();
                        options.inSampleSize = 2;
                        Bitmap bitmap =BitmapFactory.decodeByteArray(
                                data, 0, data.length);  //bitmap 변수에 사진을 저장

                        Matrix matrix = new Matrix();
                        // rotate the Bitmap
                        matrix.postRotate(90);
                        // recreate the new Bitmap
                        Bitmap rotateBitmap = Bitmap.createBitmap(bitmap, 0, 0,
                                bitmap.getWidth(), bitmap.getHeight(), matrix, true);

                    }
                }
            };

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

        //textView.setText("Data: "+data.toString());
        //textView.setHighlightColor(Color.WHITE);
        //textView.setTextColor(Color.WHITE);
        //textView.setShadowLayer(2.0f, 1.0f, 1.0f, Color.BLACK) ;

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
        if(camera != null){
            camera.stopPreview();
            camera.release();
            camera=null;
        }
        super.onStop();
    }
    //****************************************************************************************************************************** DrawOnTop
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
    //****************************************************************************************************************************** SpoidRGB
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
    //****************************************************************************************************************************** Softkey - back
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



