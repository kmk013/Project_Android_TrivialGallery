package com.example.kimmingyu.androidproject_trivialgallery;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private final int GALLERY_CODE = 1112;

    GridView main_gridView;
    FloatingActionButton floatingActionButton;

    LayoutInflater addViewInflater;
    LayoutInflater tagInflater;
    TextView tag;

    ImageView add_imageView;
    EditText add_editText;
    LinearLayout add_linearLayout;

    Set<Drawable> img;
    Set<String> tags;

    ImageAdapter imageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        main_gridView = findViewById(R.id.main_gridView);
        imageAdapter = new ImageAdapter(MainActivity.this, R.layout.row, img);
        img = new HashSet<Drawable>();
        floatingActionButton = findViewById(R.id.floatingActionButton);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tags = new HashSet<String>();
                addViewInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                View view = addViewInflater.inflate(R.layout.activity_add_image, null);
                add_imageView = view.findViewById(R.id.add_imageView);
                add_imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (view.getId() == R.id.add_imageView) SelectGallery();
                    }
                });
                add_editText = view.findViewById(R.id.add_editText);
                add_editText.setOnKeyListener(new View.OnKeyListener() {
                    @Override
                    public boolean onKey(View view, int i, KeyEvent keyEvent) {
                        if (keyEvent.getAction() == KeyEvent.ACTION_DOWN && i == KeyEvent.KEYCODE_ENTER && !add_editText.getText().toString().equals("")) {
                            tagInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                            tag = (TextView) tagInflater.inflate(R.layout.tag, add_linearLayout, false);

                            tag.setText("#" + ((EditText) view).getText().toString());

                            tags.add(tag.getText().toString());
                            add_linearLayout.addView(tag);

                            add_editText.setText(null);
                        }
                        return false;
                    }
                });
                add_linearLayout = view.findViewById(R.id.add_linearLayout);

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setView(view);
                builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (add_imageView.getDrawable() == null)
                            Toast.makeText(MainActivity.this, "사진을 선택해주세요", Toast.LENGTH_SHORT).show();
                        else if (add_linearLayout.getChildCount() <= 0)
                            Toast.makeText(MainActivity.this, "태그는 1개 이상 입력하셔야 합니다", Toast.LENGTH_SHORT).show();
                        else {
                            Save(add_imageView.getDrawable());
                            imageAdapter.notifyDataSetChanged();
                        }
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                SelectGallery();
            }
        });

        main_gridView.setAdapter(imageAdapter);
    }

    private boolean Save(Drawable image) {
        img.add(image);

        SharedPreferences sharedPreferences = getSharedPreferences("pref", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        //"image_0_tags"
        editor.putStringSet("image_" + Singleton.getInstance().getCnt() + "_tags", tags);

        return editor.commit();
    }

    //private void Load() {
    //    SharedPreferences sharedPreferences = getSharedPreferences("pref", MODE_PRIVATE);
    //
    //    for (int i = 0; ; i++) {
    //        String imageRoot = sharedPreferences.getString("image_" + i + "_resource", "");
    //        if (imageRoot.equals("")) break;
    //        else {
    //            ExifInterface exif = null;
    //            try {
    //                exif = new ExifInterface(imageRoot);
    //            } catch (IOException e) {
    //                e.printStackTrace();
    //            }
    //
    //            int exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
    //            int exifDegree = Singleton.getInstance().ExifOrientationToDegrees(exifOrientation);
    //
    //            Bitmap bitmap = BitmapFactory.decodeFile(imageRoot);
    //            img.add(Singleton.getInstance().Rotate(bitmap, exifDegree));
    //        }
    //    }
    //}

    private void SelectGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, GALLERY_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == GALLERY_CODE) SendPicture(data.getData());
    }

    private void SendPicture(Uri imgUri) {
        String imagePath = getRealPathFromURI(imgUri);
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(imagePath);
        } catch (IOException e) {
            e.printStackTrace();
        }

        int exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
        int exifDegree = ExifOrientationToDegrees(exifOrientation);

        Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
        add_imageView.setImageBitmap(Rotate(bitmap, exifDegree));
    }

    private String getRealPathFromURI(Uri contentUri) {
        int column_index = 0;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor.moveToFirst())
            column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        return cursor.getString(column_index);
    }

    public int ExifOrientationToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90)
            return 90;
        else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180)
            return 180;
        else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270)
            return 270;
        return 0;
    }

    public Bitmap Rotate(Bitmap src, float degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);

        return Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
    }
}

