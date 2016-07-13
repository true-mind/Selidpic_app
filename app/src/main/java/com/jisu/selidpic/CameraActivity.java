package com.jisu.selidpic;

import android.app.Activity;
import android.content.Context;
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
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import java.util.Timer;
import java.util.TimerTask;
import android.os.Handler;

public class CameraActivity extends Activity implements SurfaceHolder.Callback, Camera.PreviewCallback, SensorEventListener {

    Camera camera;
    SurfaceHolder holder;
    VideoView videoView;
    TextView textView;

    private SensorManager sensorManager;
    private Sensor sensor;

    private float sensorValue =0;

    private Handler handler;
    private Timer timer;

    private Boolean brightness_ok = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        initView();
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
                        }
                        else{
                            brightness_ok = true;
                        }
                    }
                });
            }
        }, 5000, 5000);
        DrawOnTop mDraw = new DrawOnTop(this);
        addContentView(mDraw, new ViewGroup.LayoutParams
                (ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    @Override
    protected void onResume(){
        super.onResume();
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI);

    }

    @Override
    protected void onPause(){
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    private void setCamera() {
        int cameraId = 0;
        int numberOfCameras = Camera.getNumberOfCameras();
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();

        for(int i=0; i < numberOfCameras; i++) {
            Camera.getCameraInfo(i, cameraInfo);

            /* 전면 카메라를 쓸 것인지 후면 카메라를 쓸것인지 설정 시 */
            /* 전면카메라 사용시 CAMERA_FACING_FRONT 로 조건절 */
            if(cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT)
                cameraId = i;
        }
        camera = Camera.open(cameraId);
        Camera.Parameters params = camera.getParameters();
        params.setPreviewFpsRange(15000, 30000);
        params.setPreviewSize(1280, 720);
        //params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO); //video 화면이 변할 때마다 자동 포커스 맞춤

        params.setPreviewFormat(ImageFormat.NV21);
        params.setPreviewFrameRate(30);
        camera.setDisplayOrientation(90); //세로 모드 가정
        camera.setParameters(params);

        holder = videoView.getHolder();
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        holder.addCallback(this);
    }

    private void initSensor(){
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
    }

    private void initView() {
        videoView = (VideoView) findViewById(R.id.videoView);
        textView = (TextView) findViewById(R.id.textview);
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        try {
            camera.unlock();
            camera.reconnect();
            camera.setPreviewDisplay(holder);
            camera.startPreview();
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        camera.setPreviewCallback(this);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        textView.setText("Data: "+data.toString());
        textView.setHighlightColor(Color.WHITE);
        textView.setTextColor(Color.WHITE);
        textView.setShadowLayer(2.0f, 1.0f, 1.0f, Color.BLACK) ;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if(sensorEvent.sensor.getType()==Sensor.TYPE_LIGHT){
            sensorValue = sensorEvent.values[0];
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    protected void onDestroy() {
        timer.cancel();
        super.onDestroy();
    }

    public class DrawOnTop extends View {

        public DrawOnTop(Context context) {
            super(context);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            // TODO Auto-generated method stub
            Paint paint = new Paint();
            /*
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.BLACK);
            paint.setTextSize(20);
            canvas.drawText("Test Text", 20, 20, paint);
            */
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(2);
            paint.setColor(Color.RED);

            Path path = new Path();

            path.moveTo(0, 280);
            path.cubicTo(80, 200, 150, 190, 200, 170);

            path.offset(50, 450);
            canvas.drawPath(path, paint);

            Path path2 = new Path();

            //path2.moveTo(820, 280);
            //path2.cubicTo(740, 200, 670, 190, 620, 170);
            path2.moveTo(670, 280);
            path2.cubicTo(590, 200, 520, 190, 470, 170);
            path2.offset(0, 450);
            canvas.drawPath(path2, paint);


            super.onDraw(canvas);
        }
    }
}
