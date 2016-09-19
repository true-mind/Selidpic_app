package com.jisu.selidpic;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by 현석 on 2016-08-09.
 */
public class AfterActivity extends Activity {
    ProgressDialog progressdialog;
    Drawable ConP, Con;
    ImageButton btn1, btn2, btn3, btn4;
    ImageView imageView2;
    Bitmap image, imageCropped, edge_image, temp_image, background, background_before_crop;
    String filename;
    int ppi;
    int counterPara;
    int convertCount;
    int width, height, statview;
    int screenWidth, screenHeight, widthMid, heightMid, picWidth, picHeight;
    int cropStartX, cropStartY;
    int back_width, back_height;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_after);
        initView();
        initListener();
        progressdialog = ProgressDialog.show(this, "로딩중", "Loading...please wait", true, false);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                byte[] arr = getIntent().getByteArrayExtra("image");
                image = resize(arr);
                background_before_crop = getBackgroundImage();
                //image = BitmapFactory.decodeByteArray(arr, 0, arr.length);

                screenWidth = image.getWidth();
                screenHeight = image.getHeight();

                width = getIntent().getIntExtra("width", 0);
                height = getIntent().getIntExtra("height", 0);
                statview = getIntent().getIntExtra("statview", 5);

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

                Con = getResources().getDrawable(R.mipmap.after_convert);
                ConP = getResources().getDrawable(R.mipmap.after_convert_pressed);

                threadhandler.sendEmptyMessage(0);
            }
            
        });
        thread.start();

    }

    private Bitmap getBackgroundImage(){
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.photo_back);
        back_width = bitmap.getWidth();
        back_height = bitmap.getHeight();
        return bitmap;
    }

    private Bitmap resize(byte[] arr) {
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


    private Handler threadhandler = new Handler(){
        public void handleMessage(Message msg){
            imageView2.setImageBitmap(edge_image);
            progressdialog.dismiss();
        }
    };

    private void initView() {
        imageView2 = (ImageView)findViewById(R.id.imageView2);
        btn1 = (ImageButton) findViewById(R.id.after_btn1);
        btn2 = (ImageButton) findViewById(R.id.after_btn2);
        btn3 = (ImageButton) findViewById(R.id.after_btn3);
        btn4 = (ImageButton) findViewById(R.id.after_btn4);
    }


    private void initListener() {

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent it3=getIntent(); //파일명을 가져오기 위한 인텐트(에디트텍스트에서 이름입력받은 걸 파일명으로 쓰기 위해)
                String str_name=filename; //이름을 가져온다.
                File fileRoute = null;
                fileRoute = Environment.getExternalStorageDirectory(); //sdcard 파일경로 선언

                File files = new File(fileRoute,"/SelidPic/"+str_name); //폴더에 이름으로 저장된 jpeg파일 경로 선언

                if(files.exists()==true)  //파일유무확인
                {
                    Intent intentSend  = new Intent(Intent.ACTION_SEND);
                    intentSend.setType("image/*");
                    //이름으로 저장된 파일의 경로를 넣어서 공유하기
                    intentSend.putExtra(Intent.EXTRA_STREAM, Uri.parse(fileRoute+"/SelidPic/"+str_name));
                    startActivity(Intent.createChooser(intentSend, "Share")); //공유하기 창 띄우기
                }else{
                    //파일이 없다면 저장을 해달라는 토스트메세지를 띄운다.
                    Toast.makeText(getApplicationContext(), "Save first", Toast.LENGTH_LONG).show();
                }
            }
        });

        btn2.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {
                Toast.makeText(AfterActivity.this, "Convert", Toast.LENGTH_SHORT).show();
                imageView2.setImageBitmap(null);
                if (convertCount==0){
                    btn2.setBackground(ConP);
                    imageView2.setImageBitmap(rotateImage(imageCropped, 90));
                    convertCount--;
                }
                else{
                    btn2.setBackground(Con);
                    imageView2.setImageBitmap(edge_image);
                    convertCount++;

                }
                imageView2.invalidate();

            }
        });

        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                screenWidth = getIntent().getIntExtra("screenWidth", 0);
                screenHeight = getIntent().getIntExtra("screenHeight", 0);

                Intent intent = new Intent(AfterActivity.this, CameraActivity.class);

                intent.putExtra("width", width);
                intent.putExtra("height", height);
                intent.putExtra("view", statview);
                intent.putExtra("screenWidth", screenWidth);
                intent.putExtra("screenHeight", screenHeight);

                startActivity(intent);
                finish();
                Toast.makeText(AfterActivity.this, "Replay", Toast.LENGTH_SHORT).show();

            }
        });

        btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveScreen1(imageCropped);
                saveScreen2(edge_image);
                Toast.makeText(AfterActivity.this, "Saved", Toast.LENGTH_SHORT).show();
            }

        });

    }
    private Bitmap getEdge(Bitmap bitmapimage){ // Sobel 윤곽선 검출 알고리즘 사용
        int  Gx[][], Gy[][];//, G[][];
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
        //G  = new int[width][height];

        counter = 0;
        for (i=0; i<width; i++) {
            for (j=0; j<height; j++) {
                if (i==0 || i==width-1 || j==0 || j==height-1) {
                    Gx[i][j] = Gy[i][j] = 0;//G[i][j] = 0; // Image boundary cleared
                    pixels[counter] = 0;
                    counter++;
                }
                else{
                    Gx[i][j] = output[i+1][j-1] + 2*output[i+1][j] + output[i+1][j+1] -
                            output[i-1][j-1] - 2*output[i-1][j] - output[i-1][j+1];
                    Gy[i][j] = output[i-1][j+1] + 2*output[i][j+1] + output[i+1][j+1] -
                            output[i-1][j-1] - 2*output[i][j-1] - output[i+1][j-1];
                    //G[i][j]  = Math.abs(Gx[i][j]) + Math.abs(Gy[i][j]);
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

    private void saveScreen1(Bitmap image) {

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

        try {
            File mediaStorageDir = new File("/sdcard/SelidPic");

            if(! mediaStorageDir.isDirectory()) {
                mediaStorageDir.mkdirs();
            }

            filename = "Origin_"+ timeStamp + ".jpeg";
            FileOutputStream out = new FileOutputStream("/sdcard/SelidPic/"+ filename);
            image.compress(Bitmap.CompressFormat.JPEG, 50, out);
            out.close();
        } catch (FileNotFoundException e) {
            Log.d("FileNotFoundException:", e.getMessage());
        }catch(IOException exception){
            Log.e("IOException", exception.getMessage());
        }
    }
    private void saveScreen2(Bitmap image) {

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

        try {
            File mediaStorageDir = new File("/sdcard/SelidPic");

            if(! mediaStorageDir.isDirectory()) {
                mediaStorageDir.mkdirs();
            }

            filename = "SelidPic_"+ timeStamp + ".jpeg";
            FileOutputStream out = new FileOutputStream("/sdcard/SelidPic/"+ filename);
            image.compress(Bitmap.CompressFormat.JPEG, 50, out);
            out.close();
        } catch (FileNotFoundException e) {
            Log.d("FileNotFoundException:", e.getMessage());
        }catch(IOException exception){
            Log.e("IOException", exception.getMessage());
        }
    }

    public void onBackPressed() {
        Intent Intent = new Intent(AfterActivity.this, MainActivity.class);
        startActivity(Intent);
        finish();

    }
}