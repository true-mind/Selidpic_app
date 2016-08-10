package com.jisu.selidpic;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

/**
 * Created by 현석 on 2016-07-14.
 */
public class UserDefineActivity extends Activity {
    EditText edittext, edittext2;
    ImageButton button, button2;
    int width, height;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_define);

        edittext = (EditText) findViewById(R.id.editText1);
        edittext2 = (EditText) findViewById(R.id.editText2);
        button = (ImageButton) findViewById(R.id.user_btn1);
        button2 = (ImageButton) findViewById(R.id.user_btn2);


        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String inPutText = edittext.getText().toString();
                String inPutText2 = edittext2.getText().toString();
                width = Integer.parseInt(inPutText);
                height = Integer.parseInt(inPutText2);
                Intent intent5 = new Intent(UserDefineActivity.this, CautionActivity.class);
                intent5.putExtra("width", width);
                intent5.putExtra("height", height);
                startActivity(intent5);
                finish();
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
}