package com.jisu.selidpic;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;

/**
 * Created by 현석 on 2016-09-23.
 */
public class TouchToolActivity extends Activity {

    ImageButton touchtool_btn1, touchtool_btn2, touchtool_btn3, touchtool_btn4;
    ImageView imageView3, imageView4, imageView5;
    SeekBar seekBar1, seekBar2;
    int width, height, view, screenWidth, screenHeight, statview, ppi;

    Drawable touchtool_btn1_selected;
    Drawable touchtool_btn2_selected;
    Drawable touchtool_btn1_disabled;
    Drawable touchtool_btn2_disabled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_touchtool);


        width = getIntent().getIntExtra("width", width);
        height = getIntent().getIntExtra("height", height);
        screenWidth = getIntent().getIntExtra("screenWidth", screenWidth);
        screenHeight = getIntent().getIntExtra("screenHeight", screenHeight);
        statview = getIntent().getIntExtra("statview", statview);
        ppi = getIntent().getIntExtra("ppi", ppi);

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

            }
        });

        touchtool_btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent Intent = new Intent(TouchToolActivity.this, AfterActivity.class);
                startActivity(Intent);
                finish();
            }
        });

    }
    public void onBackPressed() {
        Intent Intent = new Intent(TouchToolActivity.this, MainActivity.class);
        startActivity(Intent);
        finish();

    }
}
