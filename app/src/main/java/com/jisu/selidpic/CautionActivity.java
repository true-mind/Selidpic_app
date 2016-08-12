package com.jisu.selidpic;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

/**
 * Created by 현석 on 2016-07-13.
 */
public class CautionActivity extends Activity {

    ImageButton button, button2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_caution);

        final int width, height, view;
        Intent intent = getIntent();
        width = intent.getIntExtra("width", 0);
        height = intent.getIntExtra("height", 0);
        view = intent.getIntExtra("view", 5);



        button = (ImageButton) findViewById(R.id.caution_btn);
        button2 = (ImageButton) findViewById(R.id.caution_btn_cancel);


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(CautionActivity.this, CameraActivity.class);
                intent1.putExtra("width", width);
                intent1.putExtra("height", height);
                intent1.putExtra("view", view);
                startActivity(intent1);
                finish();
            }
        });


        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent Intent = new Intent(CautionActivity.this, MainActivity.class);
                startActivity(Intent);
                finish();

            }
        });
    }

        public void onBackPressed() {
                Intent Intent2 = new Intent(CautionActivity.this, MainActivity.class);
                startActivity(Intent2);
                finish();
    }

}