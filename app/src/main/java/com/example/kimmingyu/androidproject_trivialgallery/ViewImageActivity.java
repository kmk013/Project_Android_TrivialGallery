package com.example.kimmingyu.androidproject_trivialgallery;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.IOException;

public class ViewImageActivity extends AppCompatActivity {

    ImageView view_ImageView;
    LinearLayout view_LinearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_image);

        view_ImageView = findViewById(R.id.view_imageView);
        view_LinearLayout = findViewById(R.id.view_linearLayout);
        Load();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.view_actionbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.view_edit) {
            Intent intent = new Intent(this, EditImageActivity.class);
            startActivity(intent);
        }
        return true;
    }

    private void Load() {
        SharedPreferences sharedPreferences = getSharedPreferences("pref", MODE_PRIVATE);

        String imagePath = sharedPreferences.getString("image_" + Singleton.getInstance().getSelect_img_num() + "_resource", "0");
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(imagePath);
        } catch (IOException e) {
            e.printStackTrace();
        }

        int exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
        int exifDegree = Singleton.getInstance().ExifOrientationToDegrees(exifOrientation);

        Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
        view_ImageView.setImageBitmap(Singleton.getInstance().Rotate(bitmap, exifDegree));

        for (int i = 0; i < sharedPreferences.getInt("image_" + Singleton.getInstance().getSelect_img_num() + "_size", 0); i++) {
            TextView tag = new TextView(this);
            tag.setText(sharedPreferences.getString("image_" + Singleton.getInstance().getSelect_img_num() + "_" + i, ""));
            tag.setBackgroundResource(R.drawable.ic_tag);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.setMargins(0, 0, 20, 0);
            tag.setPadding(20, 20,20,20);
            tag.setLayoutParams(lp);
            view_LinearLayout.addView(tag);
        }
    }
}
