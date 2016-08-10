package com.jisu.selidpic;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by jisu on 2016. 7. 23..
 */
public class AfterActivity extends Activity {


    ImageButton imagebtn_share, imagebtn_save, imagebtn_convert, imagebtn_shareapp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_after);
        initView();
        initListener();
    }

    private void initListener() {
        imagebtn_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        imagebtn_convert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        imagebtn_shareapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        imagebtn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    private void initView() {
        // TODO: 2016. 7. 23. layout 완성 후 채우기
    }

    private void SaveBitmapToFileCache(Bitmap bitmap, String strFilePath){
        File fileCacheItem = new File(strFilePath);
        OutputStream out = null;

        try{
            fileCacheItem.createNewFile();
            out = new FileOutputStream(fileCacheItem);

            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            //bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
        }
        catch(Exception e){
            e.printStackTrace();
        }
        finally{
            try{
                out.close();
            }
            catch(IOException e){
                e.printStackTrace();
            }
        }
    }
}
