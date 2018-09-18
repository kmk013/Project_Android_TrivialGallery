package com.example.kimmingyu.androidproject_trivialgallery;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;

public class EditImageActivity extends AppCompatActivity {

    ImageView edit_imageView;
    EditText edit_editText;
    LinearLayout edit_linearLayout;

    ArrayList<String> tags = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_image);

        edit_imageView = findViewById(R.id.edit_imageView);
        edit_editText = findViewById(R.id.edit_editText);
        edit_editText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (i == KeyEvent.KEYCODE_ENTER)) {
                    final TextView tag = new TextView(EditImageActivity.this);
                    tag.setText("#" + edit_editText.getText().toString());
                    tag.setBackgroundResource(R.drawable.ic_tag);
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT);
                    lp.setMargins(0,0,20,0);
                    tag.setLayoutParams(lp);
                    tag.setPadding(20, 20,20,20);
                    tag.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View view) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                            builder.setTitle("삭제하시겠습니까?");
                            builder.setMessage("삭제하면 복구되지 않습니다.");
                            builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    tags.remove(tag.getText().toString());
                                    edit_linearLayout.removeView(tag);
                                }
                            });
                            builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.cancel();
                                }
                            });
                            AlertDialog alertDialog = builder.create();
                            alertDialog.show();
                            return false;
                        }
                    });
                    tags.add(tag.getText().toString());
                    edit_linearLayout.addView(tag);
                    edit_editText.setText(null);
                    return true;
                }
                return false;
            }
        });
        edit_linearLayout = findViewById(R.id.edit_linearLayout);
        Load();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_actionbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.edit_check) {
            Save();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
        return true;
    }

    private boolean Save() {
        SharedPreferences sharedPreferences = getSharedPreferences("pref", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putInt("image_" + Singleton.getInstance().getSelect_img_num() + "_size", tags.size());

        for(int i = 0; i < tags.size(); i++)
            editor.putString("image_" + Singleton.getInstance().getSelect_img_num() + "_" + i, tags.get(i));

        return editor.commit();
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
        int exifDegree = ExifOrientationToDegrees(exifOrientation);

        Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
        edit_imageView.setImageBitmap(Rotate(bitmap, exifDegree));

        for (int i = 0; i < sharedPreferences.getInt("image_" + Singleton.getInstance().getSelect_img_num() + "_size", 0); i++) {
            final TextView tag = new TextView(this);
            tag.setText(sharedPreferences.getString("image_" + Singleton.getInstance().getSelect_img_num() + "_" + i, ""));
            tag.setBackgroundResource(R.drawable.ic_tag);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.setMargins(0, 0, 20, 0);
            tag.setPadding(20, 20, 20, 20);
            tag.setLayoutParams(lp);
            tag.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                    builder.setTitle("삭제하시겠습니까?");
                    builder.setMessage("삭제하면 복구되지 않습니다.");
                    builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            edit_linearLayout.removeView(tag);
                            tags.remove(tag.getText().toString());
                        }
                    });
                    builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                    return false;
                }
            });
            edit_linearLayout.addView(tag);
            tags.add(tag.getText().toString());
        }
    }

    private Bitmap Rotate(Bitmap src, float degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);

        return Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
    }

    private int ExifOrientationToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90)
            return 90;
        else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180)
            return 180;
        else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270)
            return 270;
        return 0;
    }
}
