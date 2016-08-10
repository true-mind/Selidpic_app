package com.jisu.selidpic;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

/**
 * Created by 현석 on 2016-08-09.
 */
public class AfterActivity extends Activity {

    ImageButton btn1, btn2, btn3, btn4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_after);

        btn1 = (ImageButton) findViewById(R.id.after_btn1);
        btn2 = (ImageButton) findViewById(R.id.after_btn2);
        btn3 = (ImageButton) findViewById(R.id.after_btn3);
        btn4 = (ImageButton) findViewById(R.id.after_btn4);

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AfterActivity.this, "Share", Toast.LENGTH_SHORT).show();

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
                Toast.makeText(AfterActivity.this, "Save", Toast.LENGTH_SHORT).show();

            }
        });
    }
    public void onBackPressed() {
        Intent Intent = new Intent(AfterActivity.this, MainActivity.class);
        startActivity(Intent);
        finish();

    }

}
