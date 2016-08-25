package com.jisu.selidpic;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

/**
 * Created by 현석 on 2016-07-14.
 */
public class UserDefineActivity extends Activity {
    EditText edittext, edittext2;
    ImageButton button, button2;
    int width, height;
    Drawable alertViewSource, alertViewSource2;
    ImageView alertView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_define);

        height = 0;
        width = 0;

        edittext = (EditText) findViewById(R.id.editText1);
        edittext2 = (EditText) findViewById(R.id.editText2);
        button = (ImageButton) findViewById(R.id.user_btn1);
        button2 = (ImageButton) findViewById(R.id.user_btn2);
        alertView = (ImageView) findViewById(R.id.alertView);
        alertViewSource = getResources().getDrawable(R.mipmap.user_define_alert);
        alertViewSource2 = getResources().getDrawable(R.mipmap.user_define_alert2);

        alertView.setImageDrawable(null);

        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String inPutText = edittext.getText().toString();
                String inPutText2 = edittext2.getText().toString();

                if (inPutText.length() == 0 ){
                    Toast.makeText(UserDefineActivity.this, "값을 입력해주세요", Toast.LENGTH_SHORT).show();
                }
                else if(inPutText2.length() == 0){
                    Toast.makeText(UserDefineActivity.this, "값을 입력해주세요", Toast.LENGTH_SHORT).show();
                }
                else {
                    height = Integer.parseInt(inPutText);
                    width = Integer.parseInt(inPutText2);
                }

                if(width<height) {
                    alertView.setImageDrawable(alertViewSource);
                }
                else if((width==0)||(height==0)){
                    alertView.setImageDrawable(alertViewSource2);
                }
                else {

                    Intent intent5 = new Intent(UserDefineActivity.this, CautionActivity.class);
                    intent5.putExtra("width", width);
                    intent5.putExtra("height", height);
                    startActivity(intent5);
                    finish();

                }
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent Intent = new Intent(UserDefineActivity.this, MainActivity.class);
                startActivity(Intent);
                finish();
            }
        });

    }
    public void onBackPressed() {
        Intent Intent2 = new Intent(UserDefineActivity.this, MainActivity.class);
        startActivity(Intent2);

        finish();

    }
}