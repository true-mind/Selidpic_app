package com.jisu.selidpic;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class MainActivity extends Activity {

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
                intent1.putExtra("width", 25);
                intent1.putExtra("height", 30);
                startActivity(intent1);
                //finish();
            }

        });
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(MainActivity.this, CautionActivity.class);
                intent2.putExtra("width", 30);
                intent2.putExtra("height", 40);
                startActivity(intent2);
                finish();
            }
        });
        btn3.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent3 = new Intent(MainActivity.this, CautionActivity.class);
                intent3.putExtra("width", 35);
                intent3.putExtra("height", 45);
                startActivity(intent3);
                finish();
            }
        });
        btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent4 = new Intent(MainActivity.this, CautionActivity.class);
                intent4.putExtra("width", 50);
                intent4.putExtra("height", 70);
                startActivity(intent4);
                finish();
            }
        });
        btn5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent5 = new Intent(MainActivity.this, UserDefineActivity.class);
                startActivity(intent5);
            }
        });
        btn6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent6 = new Intent(MainActivity.this, CautionActivity.class);
                startActivity(intent6);
                finish();
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
}