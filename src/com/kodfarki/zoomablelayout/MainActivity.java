package com.kodfarki.zoomablelayout;

import widgets.CompoundVideoView;
import widgets.ScalableVideoWidget;
import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.view.TextureView;
import android.widget.SeekBar;
import android.widget.VideoView;

public class MainActivity extends Activity {

	private VideoView videoView;
	private CompoundVideoView compoundWebView;
	private SeekBar seekBarView;

	private TextureView tv;

	private ScalableVideoWidget scalableVideoWdiget;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		// seekBarView = (SeekBar) findViewById(R.id.seekBarView);
		// seekBarView.setProgress(0);

		videoView = (VideoView) findViewById(R.id.videoView);
		// Uri uri = Uri.parse("android.resource://" + getPackageName() + "/"
		// + R.raw.sample2);

		// MediaController controller = new MediaController(this);
		// controller.setAnchorView(videoView);
		// controller.setMediaPlayer(videoView);
		// videoView.setMediaController(controller);
		// videoView.setVideoURI(uri);
		// videoView.start();
		// MediaController mc2 = new MediaController(getContext());

		// mc.setMediaPlayer(player);

		scalableVideoWdiget = (ScalableVideoWidget) findViewById(R.id.scalableVideoWdiget);
		scalableVideoWdiget.setVideoComponent(
				Uri.parse("android.resource://" + getPackageName() + "/"
						+ R.raw.sample2), 100, 200, 200, 120);
		scalableVideoWdiget.setClickableButtonComponent(100, 100, 100, 100);
		// scalableVideoWdiget.set_ctx(this);
		// MediaController mc = new MediaController(this);
		//
		// seekBarView.setOnDragListener(new OnDragListener() {
		//
		// @Override
		// public boolean onDrag(View v, DragEvent event) {
		//
		// return false;
		// }
		// });

	}

}
