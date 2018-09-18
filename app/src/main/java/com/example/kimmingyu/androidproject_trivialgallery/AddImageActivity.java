package com.example.kimmingyu.androidproject_trivialgallery;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class AddImageActivity extends AppCompatActivity {

    private final int GALLERY_CODE = 1112;

    ImageView add_imageView;
    EditText add_editText;
    LinearLayout add_linearLayout;
    LayoutInflater layoutInflater;
    TextView tag;

    Set<String> tags = new HashSet<String>();
    String imagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_image);

        add_imageView = findViewById(R.id.add_imageView);
        add_imageView.setOnClickListener(onClickListener);

        add_editText = findViewById(R.id.add_editText);
        add_editText.setOnKeyListener(onKeyListener);

        add_linearLayout = findViewById(R.id.add_linearLayout);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_actionbar, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.add_check) {
            if(add_imageView.getDrawable() == null)
                Toast.makeText(this, "사진을 선택해주세요", Toast.LENGTH_SHORT).show();
            else if (tags.size() <= 0)
                Toast.makeText(this, "태그는 1개 이상 입력하셔야 합니다", Toast.LENGTH_SHORT).show();
            else {
                Save();

                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
            }
        }
        return true;
    }

    private boolean Save() {
        SharedPreferences sharedPreferences = getSharedPreferences("pref", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        //"image_0"
        editor.putString("image_" + Singleton.getInstance().getCnt(), imagePath);
        //"image_0_tags"
        editor.putStringSet("image_" + Singleton.getInstance().getCnt() + "_tags", tags);

        return editor.commit();
    }

    private void SelectGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, GALLERY_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == GALLERY_CODE) {
            SendPicture(data.getData());
        }
    }

    private void SendPicture(Uri imgUri) {
        imagePath = getRealPathFromURI(imgUri);
        ExifInterface exif = null;
        try { exif = new ExifInterface(imagePath); } catch (IOException e) { e.printStackTrace(); }

        int exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
        int exifDegree = Singleton.getInstance().ExifOrientationToDegrees(exifOrientation);

        Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
        add_imageView.setImageBitmap(Singleton.getInstance().Rotate(bitmap, exifDegree));
    }

    private String getRealPathFromURI(Uri contentUri) {
        int column_index = 0;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor.moveToFirst())
            column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        return cursor.getString(column_index);
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.add_imageView) SelectGallery();
        }
    };
    View.OnKeyListener onKeyListener = new View.OnKeyListener() {
        @Override
        public boolean onKey(View view, int i, KeyEvent keyEvent) {
            if (keyEvent.getAction() == KeyEvent.ACTION_DOWN && i == KeyEvent.KEYCODE_ENTER) {
                layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                tag = (TextView) layoutInflater.inflate(R.layout.tag, add_linearLayout, false);
                tag.setText("#" + ((EditText) view).getText().toString());
                tags.add(tag.getText().toString());
                add_linearLayout.addView(tag);
                add_editText.setText(null);
                return true;
            }
            return false;
        }
    };
}

