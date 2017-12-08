package com.example.admin.drawtextimagecanvas;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class MainActivity extends AppCompatActivity {

    private Button btnLoad, drawBitmap;
    private TextView text;
    private ImageView img;
    private final int RQS_IMAGE = 1;
    Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

        btnLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(getFile(getBaseContext())));
                String a = String.valueOf(Uri.fromFile(getFile(getBaseContext())));
                intent.putExtra("photo_uri", a);
                startActivityForResult(intent, RQS_IMAGE);
            }
        });

        drawBitmap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, RQS_IMAGE);
                if(uri != null){
                    Bitmap bitmapDraw = bitmapDraw();
                    if(bitmapDraw != null){
                        img.setImageBitmap(bitmapDraw);
                        Toast.makeText(MainActivity.this, "Done", Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(MainActivity.this, "Something wrong in processing!", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(MainActivity.this, "Select both image", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            if (requestCode == RQS_IMAGE) {
                if(data != null) {
                    uri = data.getData();
                    text.setText(uri.toString());
                }
            }
        }
    }

    private Bitmap bitmapDraw(){
        Bitmap bitmap = null;
        Bitmap newBitmap = null;

        try{
            bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
            Bitmap.Config config = bitmap.getConfig();
            if (config == null){
                config = Bitmap.Config.ARGB_8888;
            }

            newBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), config);
            Canvas canvas = new Canvas(newBitmap);
            canvas.drawBitmap(bitmap, 0, 0, null);
            DateFormat dateFormatter1 = new SimpleDateFormat("dd-MM-yyyy");
            DateFormat dateFormatter2 = new SimpleDateFormat("hh:mm:ss");
            dateFormatter1.setLenient(false);
            dateFormatter2.setLenient(false);
            java.util.Date today = new java.util.Date();
            String caption = dateFormatter1.format(today);
            String time = dateFormatter2.format(today);

            if(caption != null){
                Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
                paint.setColor(Color.RED);
                paint.setTextSize(24);
                paint.setStyle(Paint.Style.FILL);
                paint.setShadowLayer(10f, 10f, 10f, Color.BLACK);

                Rect rect = new Rect();
                paint.getTextBounds("" + caption + ", " + time, 0, caption.length(), rect);

                canvas.drawText("" + caption + ", " + time, 0, rect.height(), paint);
                canvas.save();

                Toast.makeText(this, "drawText: " + caption, Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(this, "Caption empty", Toast.LENGTH_SHORT).show();
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return newBitmap;
    }

    private File getFile(Context context){
        File sdCard = Environment.getExternalStorageDirectory();
        File directory = new File (sdCard.getAbsolutePath() + "/Myapp");
        if (!directory.exists()) {
            directory.mkdirs();
        }
        String filename = "bl" + System.currentTimeMillis() + ".jpg";
        File newFile = new File(directory, filename);

        return newFile;
    }

    private void init(){
        btnLoad = (Button) findViewById(R.id.button);
        drawBitmap = (Button) findViewById(R.id.button2);
        text = (TextView) findViewById(R.id.text);
        img = (ImageView) findViewById(R.id.image);
    }
}
