package com.jisu.selidpic;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;

import java.io.ByteArrayOutputStream;

/**
 * Created by 현석 on 2016-09-23.
 */
public class TouchToolActivity extends Activity {

    ImageButton touchtool_btn1, touchtool_btn2, touchtool_btn3, touchtool_btn4;
    ImageView imageView3, imageView4, imageView5;
    SeekBar seekBar1, seekBar2;
    Bitmap image, background_before_crop, background, imageCropped, temp_image, edge_image;
    int width, height, view, screenWidth, screenHeight, statview, ppi, convertCount;
    ProgressDialog progressdialog;
    int back_width, back_height, counterPara, widthMid, heightMid, picWidth, picHeight, cropStartX, cropStartY, global_x, global_y;
    Drawable touchtool_btn1_selected, touchtool_btn2_selected, touchtool_btn1_disabled, touchtool_btn2_disabled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_touchtool);
        initView();
        initListener();
        progressdialog = ProgressDialog.show(this, "로딩중", "Loading...please wait", true, false);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                byte[] arr = getIntent().getByteArrayExtra("image");
                image = set_resolution(arr);
                background_before_crop = getBackgroundImage();

                width = getIntent().getIntExtra("width", width);
                height = getIntent().getIntExtra("height", height);

                statview = getIntent().getIntExtra("statview", statview);
                ppi = getIntent().getIntExtra("ppi", ppi);

                screenWidth = image.getWidth();
                screenHeight = image.getHeight();

                counterPara = (int) Math.sqrt(ppi)*240000;


                widthMid = screenWidth/2;
                heightMid = screenHeight/2;

                picHeight = (screenWidth/width*height);
                picWidth = (screenHeight/height*width);

                if(picHeight>screenHeight){
                    picHeight = screenHeight;
                    picWidth = (picHeight/height*width);
                    cropStartX = widthMid - (picWidth/2);
                    cropStartY = heightMid - (picHeight/2);

                    imageCropped = Bitmap.createBitmap(image, cropStartX, cropStartY, picWidth, picHeight);
                    int crop_w = (back_width - picHeight);
                    crop_w/=2;
                    background = Bitmap.createBitmap(background_before_crop, crop_w, 0, picHeight, picWidth);
                }
                else{
                    picWidth = screenWidth;
                    picHeight = (picWidth/width*height);
                    cropStartX = widthMid - (picWidth/2);
                    cropStartY = heightMid - (picHeight/2);
                    int crop_w = (back_width - picHeight);
                    crop_w/=2;
                    imageCropped = Bitmap.createBitmap(image, cropStartX, cropStartY, picWidth, picHeight);
                    background = Bitmap.createBitmap(background_before_crop, crop_w, 0, picHeight, picWidth);
                }
                convertCount = 0;
                temp_image = rotateImage(imageCropped, 90);

                edge_image = getEdge(temp_image);

                initTouchListener();
                threadhandler.sendEmptyMessage(0);
            }
        });
        thread.start();

    }

    private void initTouchListener() {
        imageView3.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int action = motionEvent.getAction();
                if(action==MotionEvent.ACTION_DOWN){
                    global_x = (int) motionEvent.getX();
                    global_y = (int) motionEvent.getY();
                }
                if(action==MotionEvent.ACTION_MOVE){
                    global_x = (int) motionEvent.getX();
                    global_y = (int) motionEvent.getY();
                    if(global_x>10 && global_x<picHeight-10 && global_y>10 && global_y<picWidth-10) {
                        for(int n=global_x-10;n<global_x+10;n++){
                            for(int m=global_y-10;m<global_y+10;m++){
                                int c = background.getPixel(n, m);
                                edge_image.setPixel(n, m, c);
                            }
                        }
                        imageView3.setImageBitmap(edge_image);
                        imageView3.invalidate();
                    }
                }
                return true;
            }
        });
    }


    private Bitmap rotateImage(Bitmap src, float degree) {

        // Matrix 객체 생성
        Matrix matrix = new Matrix();
        // 회전 각도 셋팅
        matrix.setScale(1, -1);  // 상하반전
        matrix.setScale(-1, 1);  // 좌우반전

        matrix.postRotate(degree);
        // 이미지와 Matrix 를 셋팅해서 Bitmap 객체 생성
        return Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
    }

    private Bitmap getEdge(Bitmap bitmapimage){ // Sobel 윤곽선 검출 알고리즘 사용
        int  Gx[][], Gy[][];
        int width = bitmapimage.getWidth();
        int height = bitmapimage.getHeight();
        int[] pixels = new int[width * height];
        int[][] output = new int[width][height];
        int i, j, counter, k=0;

        int edge_y[] = new int[width];
        int temp_point_right[][] = new int[100][2];
        int temp_point_left[][] = new int[100][2];
        int temp_point_left_length = 0;
        int temp_point_right_length = 0;

        for(i=0;i<width;i++){
            for(j=0;j<height;j++){
                pixels[k]=bitmapimage.getPixel(i, j);
                output[i][j] = pixels[k];
                k++;
            }
        }
        Gx = new int[width][height];
        Gy = new int[width][height];

        counter = 0;
        for (i=0; i<width; i++) {
            for (j=0; j<height; j++) {
                if (i==0 || i==width-1 || j==0 || j==height-1) {
                    Gx[i][j] = Gy[i][j] = 0;// Image boundary cleared
                    pixels[counter] = 0;
                    counter++;
                }
                else{
                    Gx[i][j] = output[i+1][j-1] + 2*output[i+1][j] + output[i+1][j+1] -
                            output[i-1][j-1] - 2*output[i-1][j] - output[i-1][j+1];
                    Gy[i][j] = output[i-1][j+1] + 2*output[i][j+1] + output[i+1][j+1] -
                            output[i-1][j-1] - 2*output[i][j-1] - output[i+1][j-1];
                    pixels[counter] = Math.abs(Gx[i][j]) + Math.abs(Gy[i][j]);
                    counter++;
                }
            }
        }

        counter=0;
        boolean founded_edge=false;
        counter=0;
        founded_edge=false;

        for(i=0;i<width;i++){
            for(j=0;j<height;j++){
                if(pixels[counter]>counterPara){
                    if(founded_edge==false){
                        edge_y[i] = j;
                        int c, q;
                        for(q=j;q<j+8;q++){
                            if(q>=height){
                                break;
                            }
                            c=background.getPixel(i, q);
                            bitmapimage.setPixel(i, q, c);
                        }
                        founded_edge = true;
                    }
                }
                if(founded_edge==false){
                    int c = background.getPixel(i, j);
                    bitmapimage.setPixel(i, j, c);
                }
                counter++;
            }
            if(founded_edge==false){
                edge_y[i] = 0;
            }else{
                founded_edge=false;
            }
        }

        j=0;
        for(i=width/2;i<width-1;i++){
            if(edge_y[i]-edge_y[i-1] > (height/5)){
                temp_point_right[j][0] = edge_y[i-1];
                temp_point_right[j][1] = edge_y[i];
                j++;
                temp_point_right_length++;
            }
        }

        j=0;
        for(i=1;i<width/2;i++){
            if(edge_y[i-1]-edge_y[i] > (height/5)){
                temp_point_left[j][0] = edge_y[i-1];
                temp_point_left[j][1] = edge_y[i];
                j++;
                temp_point_left_length++;
            }
        }

        founded_edge=false;
        for(int q=0;q<temp_point_left_length;q++) {
            for (i = temp_point_left[q][0]; i > temp_point_left[q][1]; i--) {
                counter = i;
                for (j = 0; j < width / 2; j++) {
                    counter += height;
                    if (pixels[counter] > counterPara) {
                        if (founded_edge == false) {
                            int c, p;
                            for(p=j;p<j+5;p++){
                                if(p>=width/2){
                                    break;
                                }
                                c=background.getPixel(p, i);
                                bitmapimage.setPixel(p, i, c);
                            }
                            founded_edge = true;
                        }
                    }
                    if (founded_edge == false) {
                        int c = background.getPixel(j, i);
                        bitmapimage.setPixel(j, i,  c);
                    }
                }
                founded_edge = false;
            }
        }

        founded_edge=false;
        for(int q=0;q<temp_point_right_length;q++){
            for(i = temp_point_right[q][0]; i < temp_point_right[q][1]; i++){
                counter = (height
                        * (width-1)) + i;
                for(j=width-1; j>width/2; j--){
                    counter -= height;
                    if(pixels[counter] > counterPara) {
                        if(founded_edge == false){
                            int c, p;
                            for(p=j;p>j-5;p--){
                                if(p<=width/2){
                                    break;
                                }
                                c=background.getPixel(p, i);
                                bitmapimage.setPixel(p, i, c);
                            }
                            founded_edge = true;
                        }
                    }
                    if(founded_edge == false){
                        int c = background.getPixel(j, i);
                        bitmapimage.setPixel(j, i, c);}
                }
                founded_edge=false;
            }
        }
        return bitmapimage;
    }


    private Handler threadhandler = new Handler(){
        public void handleMessage(Message msg){
            imageView3.setImageBitmap(edge_image);
            progressdialog.dismiss();
        }
    };

    private Bitmap getBackgroundImage(){
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.photo_back);
        back_width = bitmap.getWidth();
        back_height = bitmap.getHeight();
        return bitmap;
    }


    private Bitmap set_resolution(byte[] arr) {
        //Get the dimensions of the View
        ppi = getIntent().getIntExtra("ppi", 0);
        int targetW, targetH;
        //onCreate 안에서 view가 아직 안 띄워짐 고로 임의 값 설정하겠음 나중에 수정해도 됨

        double tempW = (double) getIntent().getIntExtra("width", 0);
        double tempH = (double) getIntent().getIntExtra("height", 0);
        double multy_value = (double) ppi/30;
        tempW *= multy_value;
        tempH *= multy_value;

        targetW = (int) tempW;
        targetH = (int) tempH;

        //Get the dimensions of the bitmap
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(arr, 0, arr.length, options);
        int photoW = options.outHeight;
        int photoH = options.outWidth;

        //Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        //Decode the image file into a Bitmap sized to fill the View
        options.inJustDecodeBounds = false;
        options.inSampleSize = scaleFactor;
        options.inPurgeable = true;
        Bitmap bitmap = BitmapFactory.decodeByteArray(arr, 0, arr.length, options);
        return bitmap;
    };

    private void initListener() {
        touchtool_btn1.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {
                touchtool_btn1.setBackground(touchtool_btn1_selected);
                touchtool_btn2.setBackground(touchtool_btn2_disabled);
            }
        });

        touchtool_btn2.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {
                touchtool_btn1.setBackground(touchtool_btn1_disabled);
                touchtool_btn2.setBackground(touchtool_btn2_selected);
            }
        });

        touchtool_btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TouchToolActivity.this, CameraActivity.class);
                intent.putExtra("width", width);
                intent.putExtra("height", height);
                intent.putExtra("view", statview);
                startActivity(intent);
                finish();

            }
        });

        touchtool_btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* Bitmap bmp = BitmapFactory.decodeByteArray(outstr.toByteArray(), 0, outstr.size());
                bmp.compress(Bitmap.CompressFormat.JPEG, 50, outstr);
                outstr = new ByteArrayOutputStream();
                byte[] byteArray = outstr.toByteArray();*/
                Intent intent = new Intent(TouchToolActivity.this, AfterActivity.class);
                //intent.putExtra("image",byteArray);
                intent.putExtra("width", width);
                intent.putExtra("height", height);
                intent.putExtra("screenWidth", screenWidth);
                intent.putExtra("screenHeight", screenHeight);
                intent.putExtra("statview", statview);
                intent.putExtra("ppi", ppi);
                startActivity(intent);
                finish();
            }
        });
    }

    private void initView() {
        imageView3 = (ImageView) findViewById(R.id.imageView3);
        imageView4 = (ImageView) findViewById(R.id.imageView4);
        imageView5 = (ImageView) findViewById(R.id.imageView5);
        touchtool_btn1 = (ImageButton) findViewById(R.id.touchtool_btn1);
        touchtool_btn2 = (ImageButton) findViewById(R.id.touchtool_btn2);
        touchtool_btn3 = (ImageButton) findViewById(R.id.touchtool_btn3);
        touchtool_btn4 = (ImageButton) findViewById(R.id.touchtool_btn4);
        seekBar1 = (SeekBar) findViewById(R.id.seekBar1);
        seekBar2 = (SeekBar) findViewById(R.id.seekBar2);
        touchtool_btn1_selected = getResources().getDrawable(R.mipmap.touchtool_pen_selected);
        touchtool_btn2_selected = getResources().getDrawable(R.mipmap.touchtool_eraser_selected);
        touchtool_btn1_disabled = getResources().getDrawable(R.mipmap.touchtool_pen);
        touchtool_btn2_disabled = getResources().getDrawable(R.mipmap.touchtool_eraser);

    }

    public void onBackPressed() {
        Intent Intent = new Intent(TouchToolActivity.this, MainActivity.class);
        startActivity(Intent);
        finish();

    }
}
