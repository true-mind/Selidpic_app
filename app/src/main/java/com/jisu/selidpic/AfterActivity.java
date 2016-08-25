package com.jisu.selidpic;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by 현석 on 2016-08-09.
 */
public class AfterActivity extends Activity {

    ImageButton btn1, btn2, btn3, btn4;
    ImageView imageView2;
    Bitmap image, rounded_image, imageCropped;
    String filename;
    int width, height, statview;
    int screenWidth, screenHeight, widthMid, heightMid, picWidth, picHeight;
    int cropStartX, cropStartY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_after);

        byte[] arr = getIntent().getByteArrayExtra("image");
        image = BitmapFactory.decodeByteArray(arr, 0, arr.length);

        screenWidth = image.getWidth();
        screenHeight = image.getHeight();

        width = getIntent().getIntExtra("width", 0);
        height = getIntent().getIntExtra("height", 0);
        statview = getIntent().getIntExtra("statview", 5);
        //screenWidth = getIntent().getIntExtra("screenWidth", 0);
        //screenHeight = getIntent().getIntExtra("screenHeight", 0);

        widthMid = screenWidth/2;
        heightMid = screenHeight/2;

        picHeight = (screenWidth/width*height);
        picWidth = (screenHeight/height*width);
/*
        Log.d("MyTag", "width:"+width);
        Log.d("MyTag", "height:"+height);
        Log.d("MyTag", "picWidth:"+picWidth);
        Log.d("MyTag", "picHeight:"+picHeight);
        Log.d("MyTag", "screenWidth:"+screenWidth);
        Log.d("MyTag", "screenHeight:"+screenHeight);
        Log.d("MyTag", "widthMid:"+widthMid);
        Log.d("MyTag", "heightMid:"+heightMid);
*/

        if(picHeight>screenHeight){
            picHeight = screenHeight;
            picWidth = (picHeight/height*width);
            cropStartX = widthMid - (picWidth/2);
            cropStartY = heightMid - (picHeight/2);


            Log.d("MyTag", "cropStartX:"+cropStartX);
            Log.d("MyTag", "cropStartY:"+cropStartY);
            Log.d("MyTag", "widthMid:"+widthMid);
            Log.d("MyTag", "heightMid:"+heightMid);
            Log.d("MyTag", "picWidth:"+picWidth);
            Log.d("MyTag", "picHeight:"+picHeight);

            imageCropped = Bitmap.createBitmap(image, cropStartX, cropStartY, picWidth, picHeight);
        }
        else{

            picWidth = screenWidth;
            picHeight = (picWidth/width*height);
            cropStartX = widthMid - (picWidth/2);
            cropStartY = heightMid - (picHeight/2);

            imageCropped = Bitmap.createBitmap(image, cropStartX, cropStartY, picWidth, picHeight);

        }


        ImageView imageView2 = (ImageView)findViewById(R.id.imageView2);
        rounded_image = getRoundedBitmap(imageCropped);
        imageView2.setImageBitmap(rotateImage(rounded_image, 90));

        btn1 = (ImageButton) findViewById(R.id.after_btn1);
        btn2 = (ImageButton) findViewById(R.id.after_btn2);
        btn3 = (ImageButton) findViewById(R.id.after_btn3);
        btn4 = (ImageButton) findViewById(R.id.after_btn4);

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AfterActivity.this, "Share", Toast.LENGTH_SHORT).show();
                Intent intent = getPackageManager().getLaunchIntentForPackage("packageName");
                startActivity(intent);


            }
        });

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AfterActivity.this, "Convert", Toast.LENGTH_SHORT).show();

            }
        });

        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AfterActivity.this, CameraActivity.class);

                intent.putExtra("width", width);
                intent.putExtra("height", height);
                intent.putExtra("view", statview);

                startActivity(intent);
                finish();
                Toast.makeText(AfterActivity.this, "Replay", Toast.LENGTH_SHORT).show();

            }
        });

        btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                saveScreen(image);


/*
                TimerTask taskCheck = new TimerTask() {
                    @Override
                    public void run() {
                        String filepath = Environment.getExternalStorageDirectory()
                                .getAbsolutePath() + "/SelidPic/" + filename;
                        File file = new File(filepath);
                        if (file.exists()){
                            Toast.makeText(AfterActivity.this, "Save", Toast.LENGTH_SHORT).show();
                        }
                    }
                };

                Timer timer = new Timer();
                timer.schedule(taskCheck, 5000);
*/
                Toast.makeText(AfterActivity.this, "Saved", Toast.LENGTH_SHORT).show();
            }

        });

    }


    private static Bitmap getRoundedBitmap(Bitmap bitmap) {
        final Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(output);

        final int color = Color.GRAY;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, 85, 85, paint);
        //canvas.drawOval(rectF, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
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

    private void saveScreen(Bitmap image) {

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
