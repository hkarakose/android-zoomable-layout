package com.kodfarki.zoomablelayout;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

/**
 * User: Halil Karakose
 * Date: 7/10/14
 * Time: 8:17 PM
 */
public class ZoomableFragment extends Fragment {
    private FragmentActivity parentActivity;

    public ZoomableFragment(FragmentActivity parentActivity) {
        this.parentActivity = parentActivity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.zoomable_fragment, container, false);

        ImageView image = new ImageView(parentActivity);
        image.setImageResource(R.drawable.andre);
        layout.addView(image);

        return layout;
    }

    /**
     *
     * @param bm required to learn original image width/height so that we can calculate left,top,width,height of the
     *           clickable region during zoom in/out.
     * @param left
     * @param top
     * @param width
     * @param height
     * @param url
     */
    public void addClickableArea(Bitmap bm, int left, int top, int width, int height, final String url) {
        //TODO not implemented
        View view = null;

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) parentActivity).displayFragment(new WebViewFragment(url));
            }
        });
    }

    /**
     *
     * @param bm required to learn original image width/height so that we can calculate left,top,width,height of the
     *           clickable region during zoom in/out.
     * @param left
     * @param top
     * @param width
     * @param height
     */
    public void addVideoView(Bitmap bm, int left, int top, int width, int height) {
       //TODO not implemented
    }
}