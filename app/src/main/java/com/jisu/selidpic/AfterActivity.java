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
import android.view.MotionEvent;
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
    Bitmap image, imageCropped, edge_image, temp_image, background, background_before_crop, origin_image;
    String filename;
    int ppi, counterPara, convertCount;
    int width, height, statview;
    int screenWidth, screenHeight, widthMid, heightMid, picWidth, picHeight;
    int cropStartX, cropStartY, back_width, back_height;
    int global_x, global_y;

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
                byte[] arr1 = getIntent().getByteArrayExtra("origin_image");
                byte[] arr2 = getIntent().getByteArrayExtra("composed_image");

                width = getIntent().getIntExtra("width", 0);
                height = getIntent().getIntExtra("height", 0);
                statview = getIntent().getIntExtra("statview", 5);
                picWidth = getIntent().getIntExtra("picWidth", 0);
                picHeight = getIntent().getIntExtra("picHeight", 0);
                Con = getResources().getDrawable(R.mipmap.after_convert);
                ConP = getResources().getDrawable(R.mipmap.after_convert_pressed);
                origin_image = BitmapFactory.decodeByteArray(arr1, 0, arr1.length);
                edge_image = BitmapFactory.decodeByteArray(arr2, 0, arr2.length);

                threadhandler.sendEmptyMessage(0);
            }
            
        });
        thread.start();

    }

    private Handler threadhandler = new Handler(){
        public void handleMessage(Message msg){
            imageView2.setImageBitmap(edge_image);
            Toast.makeText(AfterActivity.this, "완료", Toast.LENGTH_SHORT).show();
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
                    imageView2.setImageBitmap(origin_image);
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