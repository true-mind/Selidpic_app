package com.jisu.selidpic;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
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
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by 현석 on 2016-08-09.
 */
public class AfterActivity extends Activity {

    ImageButton btn1, btn2, btn3, btn4;
    ImageView imageView2;
    Bitmap image, rounded_image, imageCropped, composed_image, edge_image, temp_image;
    String filename;
    int width, height, statview;
    int screenWidth, screenHeight, widthMid, heightMid, picWidth, picHeight;
    int cropStartX, cropStartY;
    int colors[], average_color;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_after);

        byte[] arr = getIntent().getByteArrayExtra("image");
        image = BitmapFactory.decodeByteArray(arr, 0, arr.length);

        screenWidth = image.getWidth();
        screenHeight = image.getHeight();

        width = getIntent().getIntExtra("width", 0);
        height = getIntent().getIntExtra("height", 0);
        statview = getIntent().getIntExtra("statview", 5);
        //screenWidth = getIntent().getIntExtra("screenWidth", 0);
        //screenHeight = getIntent().getIntExtra("screenHeight", 0);

        widthMid = screenWidth/2;
        heightMid = screenHeight/2;

        picHeight = (screenWidth/width*height);
        picWidth = (screenHeight/height*width);
/*
        Log.d("MyTag", "width:"+width);
        Log.d("MyTag", "height:"+height);
        Log.d("MyTag", "picWidth:"+picWidth);
        Log.d("MyTag", "picHeight:"+picHeight);
        Log.d("MyTag", "screenWidth:"+screenWidth);
        Log.d("MyTag", "screenHeight:"+screenHeight);
        Log.d("MyTag", "widthMid:"+widthMid);
        Log.d("MyTag", "heightMid:"+heightMid);
*/

        if(picHeight>screenHeight){
            picHeight = screenHeight;
            picWidth = (picHeight/height*width);
            cropStartX = widthMid - (picWidth/2);
            cropStartY = heightMid - (picHeight/2);


            Log.d("MyTag", "cropStartX:"+cropStartX);
            Log.d("MyTag", "cropStartY:"+cropStartY);
            Log.d("MyTag", "widthMid:"+widthMid);
            Log.d("MyTag", "heightMid:"+heightMid);
            Log.d("MyTag", "picWidth:"+picWidth);
            Log.d("MyTag", "picHeight:"+picHeight);

            imageCropped = Bitmap.createBitmap(image, cropStartX, cropStartY, picWidth, picHeight);
        }
        else{

            picWidth = screenWidth;
            picHeight = (picWidth/width*height);
            cropStartX = widthMid - (picWidth/2);
            cropStartY = heightMid - (picHeight/2);

            imageCropped = Bitmap.createBitmap(image, cropStartX, cropStartY, picWidth, picHeight);

        }


        imageView2 = (ImageView)findViewById(R.id.imageView2);

        //Bitmap bm = BitmapFactory.decodeResource(getResources(), R.mipmap.photo_background);
        //temp_image = getRoundedBitmap(image);
        //image = rotateImage(temp_image, 90);
<<<<<<< HEAD
        image = rotateImage(imageCropped, 90);


        edge_image = getEdge(image);
=======
        imageCropped = rotateImage(imageCropped, 90);

        edge_image = getEdge(imageCropped);
>>>>>>> origin/master
        imageView2.setImageBitmap(edge_image);
        //composed_image = getComposedImage(edge_image, image);
        //imageView2.setImageBitmap(edge_image);

        //imageView2.setImageBitmap(composed_image);
        colors = new int[3];
        colors = createBackColors(imageCropped);

        btn1 = (ImageButton) findViewById(R.id.after_btn1);
        btn2 = (ImageButton) findViewById(R.id.after_btn2);
        btn3 = (ImageButton) findViewById(R.id.after_btn3);
        btn4 = (ImageButton) findViewById(R.id.after_btn4);

        //temp color picker
/*        ImageView view1, view2, view3;
        view1 = (ImageView) findViewById(R.id.tempcolorview1);
        view2 = (ImageView) findViewById(R.id.tempcolorview2);
        view3 = (ImageView) findViewById(R.id.tempcolorview3);
        view1.setBackgroundColor(colors[0]);
        view2.setBackgroundColor(colors[1]);
        view3.setBackgroundColor(colors[2]);*/

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent it3=getIntent(); //파일명을 가져오기 위한 인텐트(에디트텍스트에서 이름입력받은 걸 파일명으로 쓰기 위해)
                String str_name=filename; //이름을 가져온다.
                File fileRoute = null;
                fileRoute = Environment.getExternalStorageDirectory(); //sdcard 파일경로 선언

                File files = new File(fileRoute,"/SelidPic/"+str_name); //temp폴더에 이름으로 저장된 jpeg파일 경로 선언

                if(files.exists()==true)  //파일유무확인
                {
                    Intent intentSend  = new Intent(Intent.ACTION_SEND);
                    intentSend.setType("image/*");
                //이름으로 저장된 파일의 경로를 넣어서 공유하기
                    intentSend.putExtra(Intent.EXTRA_STREAM, Uri.parse(fileRoute+"/SelidPic/"+str_name));
                    startActivity(Intent.createChooser(intentSend, "공유")); //공유하기 창 띄우기
                }else{
                //파일이 없다면 저장을 해달라는 토스트메세지를 띄운다.
                    Toast.makeText(getApplicationContext(), "저장을 먼저 해주세요", Toast.LENGTH_LONG).show();
                }
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
                Intent intent = new Intent(AfterActivity.this, CameraActivity.class);

                intent.putExtra("width", width);
                intent.putExtra("height", height);
                intent.putExtra("view", statview);

                startActivity(intent);
                finish();
                Toast.makeText(AfterActivity.this, "Replay", Toast.LENGTH_SHORT).show();

            }
        });

        btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                saveScreen(imageCropped);
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

/*
    private Bitmap getComposedImage(Bitmap edge_image, Bitmap image) {
        Bitmap final_image;
        for(int i = 0; i < compose_pixel_length; i++){
            image.setPixel(compose_pixel[i][0], compose_pixel[i][1], Color.CYAN);
        }
        final_image = image;
        return final_image;
    }*/

    private int[] createBackColors(Bitmap bitmap){
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        colors = new int[3];
        colors[0] = bitmap.getPixel((width/4), (height/10));
        colors[1] = bitmap.getPixel((width/2), (height/15));
        colors[2] = bitmap.getPixel((width*3/4), (height/10));
        average_color=(colors[0]+colors[1]+colors[2])/3;

        return colors;
    }

    private Bitmap getEdge(Bitmap bitmap){ // Sobel 윤곽선 검출 알고리즘 사용
<<<<<<< HEAD
        int Gx[][], Gy[][];//, G[][];
=======
        int  Gx[][], Gy[][];//, G[][];
>>>>>>> origin/master
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int[] pixels = new int[width * height];
        int[][] output = new int[width][height];
        int i, j, counter, k=0;

        int edge_y[] = new int[width];
        int temp_point_right[][] = new int[100][2];
        int temp_point_left[][] = new int[100][2];
        int temp_point_left_length = 0;
        int temp_point_right_length = 0;

        for(i=0;i<width;i++){
            for(j=0;j<height;j++){
                pixels[k]=bitmap.getPixel(i, j);
                output[i][j] = pixels[k];
                k++;
            }
        }
        Gx = new int[width][height];
        Gy = new int[width][height];
        //G  = new int[width][height];

        counter = 0;
        for (i=0; i<width; i++) {
            for (j=0; j<height; j++) {
                if (i==0 || i==width-1 || j==0 || j==height-1) {
<<<<<<< HEAD
                    Gx[i][j] = Gy[i][j] = 0;// = G[i][j] = 0; // Image boundary cleared
=======
                    Gx[i][j] = Gy[i][j] = 0;//G[i][j] = 0; // Image boundary cleared
>>>>>>> origin/master
                    pixels[counter] = 0;
                    counter++;
                }
                else{
                    Gx[i][j] = output[i+1][j-1] + 2*output[i+1][j] + output[i+1][j+1] -
                            output[i-1][j-1] - 2*output[i-1][j] - output[i-1][j+1];
                    Gy[i][j] = output[i-1][j+1] + 2*output[i][j+1] + output[i+1][j+1] -
                            output[i-1][j-1] - 2*output[i][j-1] - output[i+1][j-1];
<<<<<<< HEAD
=======
                    //G[i][j]  = Math.abs(Gx[i][j]) + Math.abs(Gy[i][j]);
>>>>>>> origin/master
                    pixels[counter] = Math.abs(Gx[i][j]) + Math.abs(Gy[i][j]);
                    counter++;
                }
            }
        }

<<<<<<< HEAD
        /*
        counter = 0;
=======
 /*       counter = 0;
>>>>>>> origin/master
        for(int ii = 0 ; ii < width ; ii++ )
        {
            for(int jj = 0 ; jj < height ; jj++ )
            {
                pixels[counter] = (int) G[ii][jj];
                counter = counter + 1;
            }
        }*/
<<<<<<< HEAD
        int average_pixels=0;
        int max_pixels = 0;
        counter=0;
        for(i=0;i<width;i++){
            for(j=0;j<height;j++){
                average_pixels+=pixels[counter];
                if(max_pixels < pixels[counter]){
                    max_pixels = pixels[counter];
                }
                if(pixels[counter]>5800000){
                    //bitmap.setPixel(i, j, Color.BLACK);
=======
        counter=0;
        for(i=0;i<width;i++){
            for(j=0;j<height;j++){
                if(pixels[counter]>3000000){
                    bitmap.setPixel(i, j, Color.BLACK);
>>>>>>> origin/master
                }else{
                    bitmap.setPixel(i, j, Color.WHITE);
                }
                counter++;
            }
        }
        Log.d("MyTag", "average pixels : "+average_pixels/width/height);
        Log.d("MyTag", "max pixel : "+max_pixels);

        counter=0;
        boolean founded_edge=false;

        for(i=0;i<width;i++){
            for(j=0;j<height;j++){
                if(pixels[counter]>5800000){
                    if(founded_edge==false){
                        edge_y[i] = j;
                        //Log.d("MyTag", "found edge : ("+i+","+j+")");
                        founded_edge = true;
                    }
                }
                if(founded_edge==false){
                    bitmap.setPixel(i, j, Color.CYAN);
                    /*
                    compose_pixel[compose_pixel_length][0] = i;
                    compose_pixel[compose_pixel_length][1] = j;
                    compose_pixel_length++;*/
                }
                counter++;
            }
            if(founded_edge==false){
                //Log.d("MyTag", "can't found edge");
                edge_y[i] = 0;
            }else{
                founded_edge=false;
            }
        }

        j=0;
        for(i=width/2;i<width-1;i++){
            if(edge_y[i]-edge_y[i-1] > (height/4)){
                temp_point_right[j][0] = edge_y[i-1];
                temp_point_right[j][1] = edge_y[i];
                j++;
                temp_point_right_length++;
                for(int q=0;q<height;q++){
                    //bitmap.setPixel(i, q, Color.BLUE);
                }
            }
        }

        j=0;
        for(i=1;i<width/2;i++){
            if(edge_y[i-1]-edge_y[i] > (height/4)){
                temp_point_left[j][0] = edge_y[i-1];
                temp_point_left[j][1] = edge_y[i];
                j++;
                temp_point_left_length++;
                for(int q=0;q<height;q++){
                    //bitmap.setPixel(i, q, Color.GREEN);
                }
            }
        }

        founded_edge=false;
        for(int q=0;q<temp_point_left_length;q++) {
            //Log.d("MyTag", "temp_point_left["+q+"][0]="+temp_point_left[q][0]+", temp_point_left["+q+"][0]="+temp_point_left[q][1]);
            for (i = temp_point_left[q][0]; i > temp_point_left[q][1]; i--) {
                counter = i;
                for (j = 0; j < width / 2; j++) {
                    counter += height;
<<<<<<< HEAD
                    if (pixels[counter] > 5800000) {
=======
                    if (pixels[counter] > 4000000) {
>>>>>>> origin/master
                        if (founded_edge == false) {
                            founded_edge = true;
                        }
                    }
                    if (founded_edge == false) {
                        //bitmap.setPixel(j, i,  Color.CYAN);
                        /*compose_pixel[compose_pixel_length][0] = j;
                        compose_pixel[compose_pixel_length][1] = i;
                        compose_pixel_length++;*/
                    }
                }
                founded_edge = false;
            }
        }

        founded_edge=false;
        for(int q=0;q<temp_point_right_length;q++){
            //Log.d("MyTag", "temp_point_right["+q+"][0]="+temp_point_right[q][0]+", temp_point_right["+q+"][0]="+temp_point_right[q][1]);
            for(i = temp_point_right[q][0]; i < temp_point_right[q][1]; i++){
                counter = (height
                        * (width-1)) + i;
                for(j=width-1; j>width/2; j--){
                    counter -= height;
                    if(pixels[counter] > 5800000) {
                        if(founded_edge == false){
                            founded_edge = true;
                        }
                    }
                    if(founded_edge == false){
                        //bitmap.setPixel(j, i, Color.CYAN);
                        /*compose_pixel[compose_pixel_length][0] = j;
                        compose_pixel[compose_pixel_length][1] = i;
                        compose_pixel_length++;
                    */}
                }
                founded_edge=false;
            }
        }


        return bitmap;
    }


    private static Bitmap getRoundedBitmap(Bitmap bitmap) {
        final Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(output);

        final int color = Color.GRAY;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, 85, 85, paint);
        //canvas.drawOval(rectF, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        bitmap.recycle();

        return output;
    }
    private Bitmap rotateImage(Bitmap src, float degree) {

        // Matrix 객체 생성
        Matrix matrix = new Matrix();
        // 회전 각도 셋팅
        matrix.setScale(1, -1);  // 상하반전
        matrix.setScale(-1, 1);  // 좌우반전

        matrix.postRotate(degree);
        // 이미지와 Matrix 를 셋팅해서 Bitmap 객체 생성
        return Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
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
            image.compress(Bitmap.CompressFormat.JPEG, 50, out);
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