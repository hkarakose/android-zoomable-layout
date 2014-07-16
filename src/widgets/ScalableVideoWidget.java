package widgets;

import android.content.Context;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.VideoView;

import com.kodfarki.zoomablelayout.R;

public class ScalableVideoWidget extends RelativeLayout {

	// private class xx extends Activity {
	//
	// @Override
	// protected void onCreate(Bundle savedInstanceState) {
	// // TODO Auto-generated method stub
	// super.onCreate(savedInstanceState);
	// }
	//
	// }
	private Context _ctx;

	protected VideoView videoView;
	protected Button playButtonView;
	protected RelativeLayout relativeLayout;
	protected Uri uri;

	public void setVideoComponent(Uri pathToYourVideoFile, int x, int y,
			int width, int height) {

		playButtonView.setOnClickListener(new PlayButtonListener(
				pathToYourVideoFile));
		// LayoutParams params = (LayoutParams) videoView.getLayoutParams();
		RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) videoView
				.getLayoutParams();

		// videoView.setLeft(x);
		// videoView.setWidth(width);
		// videoView.setTop(y);
		// videoView.setHeight(height);
		// params.setMargins(x, y, x + width, y + height);
		// videoView.setLayoutParams(params);
		videoView.invalidate();

	}

	public void setClickableButtonComponent(int x, int y, int width, int height) {

		LayoutParams params = (LayoutParams) playButtonView.getLayoutParams();
		// params.setMargins(x, y, x + width, y + height);
		playButtonView.setLeft(x);
		playButtonView.setWidth(width);
		playButtonView.setTop(y);
		playButtonView.setHeight(height);
		// playButtonView.setLayoutParams(params);
		playButtonView.invalidate();
	}

	public ScalableVideoWidget(Context context, AttributeSet attrs) {
		super(context, attrs);

		LayoutInflater inflater = (LayoutInflater) getContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.widget_scalable_video, this);

		videoView = (VideoView) findViewById(R.id.videoView);
		playButtonView = (Button) findViewById(R.id.playButtonView);

	}

	public Context get_ctx() {
		return _ctx;
	}

	public void set_ctx(Context _ctx) {
		this._ctx = _ctx;
	}

	class PlayButtonListener implements OnClickListener {
		// private VideoView videoView;
		private Uri videoUri;

		public PlayButtonListener(Uri videoUri) {
			super();
			// this.videoView = videoView;
			this.videoUri = videoUri;
		}

		@Override
		public void onClick(View v) {

			MediaController mc = new MediaController(getContext());

			videoView.setVideoURI(videoUri);
			videoView.setMediaController(mc);
			videoView.start();

		}
	}
}
