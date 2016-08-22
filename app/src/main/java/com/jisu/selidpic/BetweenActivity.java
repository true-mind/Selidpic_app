package com.jisu.selidpic;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

/**
 * Created by 현석 on 2016-08-23.
 */
public class BetweenActivity extends Activity {

    ImageButton button1, button2;
    ImageView guideview;
    int width, height, view, nextorstart;

    Drawable between_next;
    Drawable between_start;
    Drawable between1;
    Drawable between2;
    Drawable between3;
    Drawable between4;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_between);

        nextorstart = 0;

        Intent intent = getIntent();

        width = intent.getIntExtra("width", 0);
        height = intent.getIntExtra("height", 0);
        view = intent.getIntExtra("view", 5);

        button1 = (ImageButton) findViewById(R.id.camera_between_next);
        button2 = (ImageButton) findViewById(R.id.camera_between_skip);
        between_next = getResources().getDrawable(R.mipmap.camera_between_next);
        between_start = getResources().getDrawable(R.mipmap.camera_between_start);

        between1 = getResources().getDrawable(R.mipmap.camera_between1);
        between2 = getResources().getDrawable(R.mipmap.camera_between2);
        between3 = getResources().getDrawable(R.mipmap.camera_between3);
        between4 = getResources().getDrawable(R.mipmap.camera_between4);

        guideview = (ImageView) findViewById(R.id.guideview);
        guideview.setImageDrawable(between1);

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(BetweenActivity.this, CameraActivity.class);

                intent1.putExtra("width", width);
                intent1.putExtra("height", height);
                intent1.putExtra("view", view);

                startActivity(intent1);
                finish();

            }
        });
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(nextorstart<3){
                    button1.setImageDrawable(between_next);
                }
                else {
                    button1.setImageDrawable(between_start);
                }
                switch (nextorstart){

                    case 1:
                        guideview.setImageDrawable(between2);
                        break;
                    case 2:
                        guideview.setImageDrawable(between3);
                        break;
                    case 3:
                        guideview.setImageDrawable(between4);
                        break;
                    case 4:
                        Intent intent1 = new Intent(BetweenActivity.this, CameraActivity.class);
                        intent1.putExtra("width", width);
                        intent1.putExtra("height", height);
                        intent1.putExtra("view", view);
                        startActivity(intent1);
                        finish();

                }

                nextorstart++;
            }
        });
    }
    public void onBackPressed() {
        Intent Intent2 = new Intent(BetweenActivity.this, MainActivity.class);
        startActivity(Intent2);
        finish();
    }

}