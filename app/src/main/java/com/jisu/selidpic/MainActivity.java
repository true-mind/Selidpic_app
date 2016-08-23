package com.jisu.selidpic;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

public class MainActivity extends Activity {

    private final long FINISH_INTERVAL_TIME = 2000;
    private long backPressedTime = 0;
    ImageButton btn1, btn2, btn3, btn4, btn5, btn6;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initListener();
    }

    private void initListener() {

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {

                Intent intent1 = new Intent(MainActivity.this, CautionActivity.class);
                intent1.putExtra("width", 30);
                intent1.putExtra("height", 25);
                intent1.putExtra("view", 1);
                startActivity(intent1);
                finish();
            }

        });



        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(MainActivity.this, CautionActivity.class);
                intent2.putExtra("width", 40);
                intent2.putExtra("height", 30);
                intent2.putExtra("view", 2);
                startActivity(intent2);
                finish();
            }
        });


        btn3.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent3 = new Intent(MainActivity.this, CautionActivity.class);
                intent3.putExtra("width", 45);
                intent3.putExtra("height", 35);
                intent3.putExtra("view", 3);
                startActivity(intent3);
                finish();
            }
        });


        btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent4 = new Intent(MainActivity.this, CautionActivity.class);
                intent4.putExtra("width", 70);
                intent4.putExtra("height", 50);
                intent4.putExtra("view", 4);
                startActivity(intent4);
                finish();
            }
        });



        btn5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent5 = new Intent(MainActivity.this, UserDefineActivity.class);
                intent5.putExtra("view", 5);
                startActivity(intent5);
                finish();
            }
        });



        btn6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "솔직헌 심정 - Truemind", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initView() {
        btn1 = (ImageButton) findViewById(R.id.main_btn1);
        btn2 = (ImageButton) findViewById(R.id.main_btn2);
        btn3 = (ImageButton) findViewById(R.id.main_btn3);
        btn4 = (ImageButton) findViewById(R.id.main_btn4);
        btn5 = (ImageButton) findViewById(R.id.main_btn5);
        btn6 = (ImageButton) findViewById(R.id.main_btn6);
    }

    @Override
    public void onBackPressed() {
        long tempTime = System.currentTimeMillis();
        long intervalTime = tempTime - backPressedTime;

        if(0<=intervalTime && FINISH_INTERVAL_TIME >= intervalTime) {
            super.onBackPressed();
        }
        else
        {
            backPressedTime = tempTime;
            Toast.makeText(getApplicationContext(), "\"Back\"버튼 을 한번 더 눌러 종료",Toast.LENGTH_SHORT).show();
        }
    }
}