package com.example.kimmingyu.androidproject_trivialgallery;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.util.Set;

public class ImageAdapter extends BaseAdapter {
    Context context;
    int layout;
    Set<Drawable> img;
    LayoutInflater inf;

    public ImageAdapter(Context context, int layout, Set<Drawable> img) {
        this.context = context;
        this.layout = layout;
        this.img = img;
        inf = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) convertView = inf.inflate(layout, null);

        ImageView iv = convertView.findViewById(R.id.imageView1);
        //iv.setImageDrawable(img.);
        return convertView;
    }
}
