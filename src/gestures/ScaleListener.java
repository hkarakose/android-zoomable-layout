package gestures;

import android.view.ScaleGestureDetector;

public class ScaleListener extends
		ScaleGestureDetector.SimpleOnScaleGestureListener {

	// @Override
	// public boolean onScale(ScaleGestureDetector detector) {
	// float mScaleFactor = detector.getScaleFactor();
	// float origScale = saveScale;
	// saveScale *= mScaleFactor;
	// if (saveScale > maxScale) {
	// saveScale = maxScale;
	// mScaleFactor = maxScale / origScale;
	// } else if (saveScale < minScale) {
	// saveScale = minScale;
	// mScaleFactor = minScale / origScale;
	// }
	//
	// if (origWidth * saveScale <= viewWidth
	// || origHeight * saveScale <= viewHeight)
	// matrix.postScale(mScaleFactor, mScaleFactor, viewWidth / 2,
	// viewHeight / 2);
	// else
	// matrix.postScale(mScaleFactor, mScaleFactor, detector.getFocusX(),
	// detector.getFocusY());
	//
	// fixTrans();
	// return true;
	// }
	//
	// @Override
	// public boolean onScaleBegin(ScaleGestureDetector detector) {
	// mode = ZOOM;
	// return true;
	// }
	//
	// @Override
	// public void onScaleEnd(ScaleGestureDetector detector) {
	// // TODO Auto-generated method stub
	// super.onScaleEnd(detector);
	// }

}
