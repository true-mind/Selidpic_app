package com.jisu.selidpic;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
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
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by 현석 on 2016-08-09.
 */
public class AfterActivity extends Activity {

    ImageButton btn1, btn2, btn3, btn4;
    ImageView imageView2;
    Bitmap image;
    String filename;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_after);

        byte[] arr = getIntent().getByteArrayExtra("image");
        image = BitmapFactory.decodeByteArray(arr, 0, arr.length);
        ImageView imageView2 = (ImageView)findViewById(R.id.imageView2);
        imageView2.setImageBitmap(image);

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
    private void saveScreen(Bitmap image) {

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

        try {
            File mediaStorageDir = new File("/sdcard/SelidPic");

            if(! mediaStorageDir.isDirectory()) {
                mediaStorageDir.mkdirs();
            }

            filename = "SelidPic_"+ timeStamp + ".jpeg";
            FileOutputStream out = new FileOutputStream("/sdcard/SelidPic/"+ filename);
            image.compress(Bitmap.CompressFormat.JPEG, 100, out);
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
