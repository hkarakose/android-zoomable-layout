package widgets;


//
//VideoView
//
//
//Created by Alex Ross on 1/29/13
//Modified to accept a Matrix by Wiseman Designs
//

import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.MediaController;
import android.widget.MediaController.MediaPlayerControl;

public class CompoundVideoView extends TextureView implements MediaPlayerControl {

	private static final String LOG_TAG = "VideoView";

	// all possible internal states
	private static final int STATE_ERROR = -1;
	private static final int STATE_IDLE = 0;
	private static final int STATE_PREPARING = 1;
	private static final int STATE_PREPARED = 2;
	private static final int STATE_PLAYING = 3;
	private static final int STATE_PAUSED = 4;
	private static final int STATE_PLAYBACK_COMPLETED = 5;

	public int number;

	// currentState is a VideoView object's current state.
	// targetState is the state that a method caller intends to reach.
	// For instance, regardless the VideoView object's current state,
	// calling pause() intends to bring the object to a target state
	// of STATE_PAUSED.
	private int currentState = STATE_IDLE;
	private int targetState = STATE_IDLE;

	// Stuff we need for playing and showing a video
	private MediaPlayer mediaPlayer;
	private int videoWidth;
	private int videoHeight;
	private int surfaceWidth;
	private int surfaceHeight;
	private int audioSession;
	private SurfaceTexture surfaceTexture;
	private Surface surface;
	private MediaController mediaController;
	private MediaPlayer.OnCompletionListener onCompletionListener;
	private MediaPlayer.OnPreparedListener onPreparedListener;
	private int currentBufferPercentage;
	private MediaPlayer.OnErrorListener onErrorListener;
	private MediaPlayer.OnInfoListener onInfoListener;

	private int mSeekWhenPrepared; // recording the seek position while
									// preparing
	private boolean mCanPause;
	private boolean mCanSeekBack;
	private boolean mCanSeekForward;
	private Uri uri;

	// scale stuff
	private float widthScale = 1.0f;
	private float heightScale = 1.0f;

	private Context mContext;

	public CompoundVideoView(final Context context) {
		super(context);
		mContext = context;
		initVideoView();
	}

	public CompoundVideoView(final Context context, final AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		initVideoView();
	}

	public CompoundVideoView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mContext = context;
		initVideoView();
	}

	public void initVideoView() {
		Log.d(LOG_TAG, "Initializing video view.");
		videoHeight = 0;
		videoWidth = 0;
		setFocusable(false);
		setSurfaceTextureListener(surfaceTextureListener);
	}

	public int resolveAdjustedSize(int desiredSize, int measureSpec) {
		Log.d(LOG_TAG, "Resolve called.");
		int result = desiredSize;
		int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);

		switch (specMode) {
		case MeasureSpec.UNSPECIFIED:
			/*
			 * Parent says we can be as big as we want. Just don't be larger
			 * than max size imposed on ourselves.
			 */
			result = desiredSize;
			break;

		case MeasureSpec.AT_MOST:
			/*
			 * Parent says we can be as big as we want, up to specSize. Don't be
			 * larger than specSize, and don't be larger than the max size
			 * imposed on ourselves.
			 */
			result = Math.min(desiredSize, specSize);
			break;

		case MeasureSpec.EXACTLY:
			// No choice. Do what we are told.
			result = specSize;
			break;
		}
		return result;
	}

	public void setVideoPath(String path) {
		Log.d(LOG_TAG, "Setting video path to: " + path);
		setVideoURI(Uri.parse(path));
	}

	public void setVideoURI(Uri _videoURI) {
		uri = _videoURI;
		requestLayout();
		invalidate();
		openVideo();
	}

	public void setSurfaceTexture(SurfaceTexture _surfaceTexture) {
		surfaceTexture = _surfaceTexture;
	}

	public void openVideo() {
		if ((uri == null) || (surfaceTexture == null)) {
			Log.d(LOG_TAG, "Cannot open video, uri or surface is null number "
					+ number);
			return;
		}
		// Tell the music playback service to pause
		// TODO: these constants need to be published somewhere in the
		// framework.
		Intent i = new Intent("com.android.music.musicservicecommand");
		i.putExtra("command", "pause");
		mContext.sendBroadcast(i);
		Log.d(LOG_TAG, "Opening video.");
		release(false);
		try {
			surface = new Surface(surfaceTexture);
			Log.d(LOG_TAG, "Creating media player number " + number);
			mediaPlayer = new MediaPlayer();

			if (audioSession != 0) {
				mediaPlayer.setAudioSessionId(audioSession);
			} else {
				audioSession = mediaPlayer.getAudioSessionId();
			}

			Log.d(LOG_TAG, "Setting surface.");
			mediaPlayer.setSurface(surface);
			Log.d(LOG_TAG, "Setting data source.");
			mediaPlayer.setDataSource(mContext, uri);
			Log.d(LOG_TAG, "Setting media player listeners.");
			mediaPlayer.setOnBufferingUpdateListener(bufferingUpdateListener);
			mediaPlayer.setOnCompletionListener(completeListener);
			mediaPlayer.setOnPreparedListener(preparedListener);
			mediaPlayer.setOnErrorListener(errorListener);
			mediaPlayer.setOnVideoSizeChangedListener(videoSizeChangedListener);
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			Log.d(LOG_TAG, "Preparing media player.");
			mediaPlayer.prepareAsync();
			currentState = STATE_PREPARING;
			attachMediaController();
		} catch (IllegalStateException e) {
			currentState = STATE_ERROR;
			targetState = STATE_ERROR;
			Log.d(LOG_TAG, e.getMessage()); // TODO auto-generated catch block
		} catch (IOException e) {
			currentState = STATE_ERROR;
			targetState = STATE_ERROR;
			Log.d(LOG_TAG, e.getMessage()); // TODO auto-generated catch block
		}
	}

	public void stopPlayback() {
		if (mediaPlayer != null) {
			mediaPlayer.stop();
			mediaPlayer.release();
			mediaPlayer = null;
		}
	}

	@Override
	protected void onMeasure(final int widthMeasureSpec,
			final int heightMeasureSpec) {
		// Will resize the view if the video dimensions have been found.
		// video dimensions are found after onPrepared has been called by
		// MediaPlayer
		Log.d(LOG_TAG, "onMeasure number " + number);
		int width = getDefaultSize(videoWidth, widthMeasureSpec);
		int height = getDefaultSize(videoHeight, heightMeasureSpec);
		if ((videoWidth > 0) && (videoHeight > 0)) {
			if ((videoWidth * height) > (width * videoHeight)) {
				Log.d(LOG_TAG, "Image too tall, correcting.");
				height = (width * videoHeight) / videoWidth;
			} else if ((videoWidth * height) < (width * videoHeight)) {
				Log.d(LOG_TAG, "Image too wide, correcting.");
				width = (height * videoWidth) / videoHeight;
			} else {
				Log.d(LOG_TAG, "Aspect ratio is correct.");
			}
		}
		Log.d(LOG_TAG, "Setting size: " + width + '/' + height + " for number "
				+ number);
		setMeasuredDimension((int) (width * widthScale),
				(int) (height * heightScale));
	}

	// Listeners
	private MediaPlayer.OnBufferingUpdateListener bufferingUpdateListener = new MediaPlayer.OnBufferingUpdateListener() {
		@Override
		public void onBufferingUpdate(final MediaPlayer mp, final int percent) {
			currentBufferPercentage = percent;
		}
	};

	private MediaPlayer.OnCompletionListener completeListener = new MediaPlayer.OnCompletionListener() {
		@Override
		public void onCompletion(final MediaPlayer mp) {
			currentState = STATE_PLAYBACK_COMPLETED;
			targetState = STATE_PLAYBACK_COMPLETED;
			Log.d(LOG_TAG, "Video completed number " + number);
			surface.release();

			((Activity) getContext()).finish();
		}
	};

	private MediaPlayer.OnPreparedListener preparedListener = new MediaPlayer.OnPreparedListener() {
		@Override
		public void onPrepared(final MediaPlayer mp) {
			currentState = STATE_PREPARED;
			Log.d(LOG_TAG, "Video prepared for " + number);
			videoWidth = mp.getVideoWidth();
			videoHeight = mp.getVideoHeight();

			mCanPause = mCanSeekBack = mCanSeekForward = true;

			if (mediaController != null) {
				mediaController.setEnabled(true);
			}

			requestLayout();
			invalidate();
			if ((videoWidth != 0) && (videoHeight != 0)) {
				Log.d(LOG_TAG, "Video size for number " + number + ": "
						+ videoWidth + '/' + videoHeight);
				if (targetState == STATE_PLAYING) {
					mediaPlayer.start();
				}
			} else {
				if (targetState == STATE_PLAYING) {
					mediaPlayer.start();
				}
			}
		}
	};

	private MediaPlayer.OnVideoSizeChangedListener videoSizeChangedListener = new MediaPlayer.OnVideoSizeChangedListener() {
		@Override
		public void onVideoSizeChanged(final MediaPlayer mp, final int width,
				final int height) {
			Log.d(LOG_TAG, "Video size changed " + width + '/' + height
					+ " number " + number);
		}
	};

	private MediaPlayer.OnErrorListener errorListener = new MediaPlayer.OnErrorListener() {
		@Override
		public boolean onError(final MediaPlayer mp, final int what,
				final int extra) {
			currentState = STATE_ERROR;
			targetState = STATE_ERROR;
			Log.e(LOG_TAG, "There was an error during video playback.");
			return true;
		}
	};

	public void setMediaController(MediaController controller) {
		if (mediaController != null) {
			mediaController.hide();
		}
		mediaController = controller;
		attachMediaController();
	}

	private void attachMediaController() {
		if (mediaPlayer != null && mediaController != null) {
			mediaController.setMediaPlayer(this);
			View anchorView = this.getParent() instanceof View ? (View) this
					.getParent() : this;
			mediaController.setAnchorView(anchorView);
			mediaController.setEnabled(isInPlaybackState());
		}
	}

	/*
	 * release the media player in any state
	 */
	private void release(boolean cleartargetstate) {
		Log.d(LOG_TAG, "Releasing media player.");
		if (mediaPlayer != null) {
			mediaPlayer.reset();
			mediaPlayer.release();
			mediaPlayer = null;
			currentState = STATE_IDLE;
			if (cleartargetstate) {
				targetState = STATE_IDLE;
			}
			Log.d(LOG_TAG, "Released media player.");
		} else {
			Log.d(LOG_TAG, "Media player was null, did not release.");
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if (isInPlaybackState() && mediaController != null) {
			toggleMediaControlsVisiblity();
		}
		return false;
	}

	@Override
	public boolean onTrackballEvent(MotionEvent ev) {
		if (isInPlaybackState() && mediaController != null) {
			toggleMediaControlsVisiblity();
		}
		return false;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		boolean isKeyCodeSupported = keyCode != KeyEvent.KEYCODE_BACK
				&& keyCode != KeyEvent.KEYCODE_VOLUME_UP
				&& keyCode != KeyEvent.KEYCODE_VOLUME_DOWN
				&& keyCode != KeyEvent.KEYCODE_VOLUME_MUTE
				&& keyCode != KeyEvent.KEYCODE_MENU
				&& keyCode != KeyEvent.KEYCODE_CALL
				&& keyCode != KeyEvent.KEYCODE_ENDCALL;
		if (isInPlaybackState() && isKeyCodeSupported
				&& mediaController != null) {
			if (keyCode == KeyEvent.KEYCODE_HEADSETHOOK
					|| keyCode == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE) {
				if (mediaPlayer.isPlaying()) {
					pause();
					mediaController.show();
				} else {
					start();
					mediaController.hide();
				}
				return true;
			} else if (keyCode == KeyEvent.KEYCODE_MEDIA_PLAY) {
				if (!mediaPlayer.isPlaying()) {
					start();
					mediaController.hide();
				}
				return true;
			} else if (keyCode == KeyEvent.KEYCODE_MEDIA_STOP
					|| keyCode == KeyEvent.KEYCODE_MEDIA_PAUSE) {
				if (mediaPlayer.isPlaying()) {
					pause();
					mediaController.show();
				}
				return true;
			} else {
				toggleMediaControlsVisiblity();
			}
		}

		return super.onKeyDown(keyCode, event);
	}

	private void toggleMediaControlsVisiblity() {
		if (mediaController.isShowing()) {
			mediaController.hide();
		} else {
			mediaController.show();
		}
	}

	@Override
	public void start() {
		if (isInPlaybackState()) {
			mediaPlayer.start();
			currentState = STATE_PLAYING;
		}
		targetState = STATE_PLAYING;
	}

	@Override
	public void pause() {
		if (isInPlaybackState()) {
			if (mediaPlayer.isPlaying()) {
				mediaPlayer.pause();
				currentState = STATE_PAUSED;
			}
		}
		targetState = STATE_PAUSED;
	}

	public void suspend() {
		release(false);
	}

	public void resume() {
		openVideo();
	}

	@Override
	public int getDuration() {
		if (isInPlaybackState()) {
			return mediaPlayer.getDuration();
		}

		return -1;
	}

	@Override
	public int getCurrentPosition() {
		if (isInPlaybackState()) {
			return mediaPlayer.getCurrentPosition();
		}
		return 0;
	}

	@Override
	public void seekTo(int msec) {
		if (isInPlaybackState()) {
			mediaPlayer.seekTo(msec);
			mSeekWhenPrepared = 0;
		} else {
			mSeekWhenPrepared = msec;
		}
	}

	@Override
	public boolean isPlaying() {
		return isInPlaybackState() && mediaPlayer.isPlaying();
	}

	@Override
	public int getBufferPercentage() {
		if (mediaPlayer != null) {
			return currentBufferPercentage;
		}
		return 0;
	}

	private boolean isInPlaybackState() {
		return (mediaPlayer != null && currentState != STATE_ERROR
				&& currentState != STATE_IDLE && currentState != STATE_PREPARING);
	}

	@Override
	public boolean canPause() {
		return mCanPause;
	}

	@Override
	public boolean canSeekBackward() {
		return mCanSeekBack;
	}

	@Override
	public boolean canSeekForward() {
		return mCanSeekForward;
	}

	public int getAudioSessionId() {
		if (audioSession == 0) {
			MediaPlayer foo = new MediaPlayer();
			audioSession = foo.getAudioSessionId();
			foo.release();
		}
		return audioSession;
	}

	public void setMatrix(Matrix mMatrix) {
		this.setTransform(mMatrix);
	}

	public void setViewScale(float width, float height) {
		this.widthScale = width;
		this.heightScale = height;
		this.invalidate();
	}

	SurfaceTextureListener surfaceTextureListener = new SurfaceTextureListener() {
		@Override
		public void onSurfaceTextureAvailable(final SurfaceTexture surface,
				final int width, final int height) {
			Log.d(LOG_TAG, "Surface texture now avaialble.");
			surfaceTexture = surface;
			openVideo();
		}

		@Override
		public void onSurfaceTextureSizeChanged(final SurfaceTexture surface,
				final int width, final int height) {
			Log.d(LOG_TAG, "Resized surface texture: " + width + '/' + height);
			surfaceWidth = width;
			surfaceHeight = height;
			boolean isValidState = (targetState == STATE_PLAYING);
			boolean hasValidSize = (videoWidth == width && videoHeight == height);
			if (mediaPlayer != null && isValidState && hasValidSize) {
				start();
			}
		}

		@Override
		public boolean onSurfaceTextureDestroyed(final SurfaceTexture surface) {
			Log.i(LOG_TAG, "Destroyed surface number " + number);

			if (mediaController != null)
				mediaController.hide();

			if (mediaPlayer != null) {
				mediaPlayer.reset();
				mediaPlayer.release();
				mediaPlayer = null;
			}

			return false;
		}

		@Override
		public void onSurfaceTextureUpdated(final SurfaceTexture surface) {

		}
	};
}