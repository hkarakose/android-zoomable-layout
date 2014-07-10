package com.kodfarki.zoomablelayout;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

public class MainActivity extends FragmentActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        displayZoomableFragment();
    }

    public void displayZoomableFragment() {
        ZoomableFragment fragment = new ZoomableFragment(this);

        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.andre);
        fragment.addClickableArea(bm, 121, 1453, 439, 171, "http://www.google.com");
        fragment.addVideoView(bm, 203, 689, 1280, 756, "http://huripadws.hurriyet.com.tr/galeri/Video/2014/6/47E6B0BC-3000-485B-A6E1.mp4");

        displayFragment(fragment);
    }

    public void displayFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.contentFrame, fragment);
        transaction.commit();
    }
}
