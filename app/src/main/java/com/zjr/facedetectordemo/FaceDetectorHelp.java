package com.zjr.facedetectordemo;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.media.FaceDetector;
import android.os.AsyncTask;
import android.util.Log;

/**
 * Created by zjr on 2018/1/9.
 */

public class FaceDetectorHelp {

    int maxFaces = 1;
    private Resources resources;
    private int imageRes;
    private String imagePath;
    private Paint paint;
    private int faceAreaColor = Color.GREEN;
    private float strokeWidth = 5;
    private OnFaceDetectorListener mFaceDetectorListener;
    private boolean showFaceArea;
    private boolean cropFace;
    private float cropRatio = 1;

    public FaceDetectorHelp(int maxFaces, Context context, int imageRes) {
        this.maxFaces = maxFaces;
        this.imageRes = imageRes;
        this.resources = context.getResources();
        initPaint();
    }

    public FaceDetectorHelp(int maxFaces, String imagePath) {
        this.maxFaces = maxFaces;
        this.imagePath = imagePath;
        initPaint();
    }

    private void initPaint() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(faceAreaColor);
        paint.setStrokeWidth(strokeWidth);
    }

    public void setFaceAreaColor(int faceAreaColor) {
        this.faceAreaColor = faceAreaColor;
    }

    public void setStrokeWidth(float strokeWidth) {
        this.strokeWidth = strokeWidth;
    }

    public void startDetect() {
        new FaceDetectorTask().execute("");
    }

    private Bitmap getSrcBitmap() {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        Bitmap bitmap = null;
        if (imageRes > 0) {
            bitmap = BitmapFactory.decodeResource(resources, imageRes, options);
        } else if (imagePath != null) {
            bitmap = BitmapFactory.decodeFile(imagePath, options);
        }
        return bitmap;
    }

    private Bitmap cropFace(Bitmap srcBitmap, FaceDetector.Face face) {
        PointF pointF = new PointF();
        face.getMidPoint(pointF);
        float eyesDistance = face.eyesDistance();
        float distance = eyesDistance + eyesDistance * cropRatio / 2;

        float maxX = srcBitmap.getWidth() - pointF.x;
        float maxY = srcBitmap.getHeight() - pointF.y;
        if (distance > maxX || distance > maxY) {
            distance = Math.min(maxX, maxY) / 2;
        }
        RectF rectF = new RectF(pointF.x - distance, pointF.y - distance / 2, pointF.x + distance, pointF.y + distance + distance / 2);

        Bitmap bitmap = Bitmap.createBitmap(srcBitmap, (int) rectF.left, (int) rectF.top, (int) rectF.width(), (int) rectF.height());
//      BitmapRegionDecoder.newInstance()
        if (!srcBitmap.isRecycled()) {
            srcBitmap.recycle();
        }
        return bitmap;
    }

    private Bitmap drawFaceArea(int faceCount, Bitmap srcBitmap, FaceDetector.Face[] faces) {
        Bitmap newBitmap = null;
        if (faceCount > 0) {
            newBitmap = Bitmap.createBitmap(srcBitmap.getWidth(), srcBitmap.getHeight(), srcBitmap.getConfig());
            Canvas canvas = new Canvas(newBitmap);
            canvas.drawBitmap(srcBitmap, 0, 0, null);
            RectF rectf = new RectF();
            for (int i = 0; i < faceCount; i++) {
                FaceDetector.Face face = faces[i];
                float eyesDistance = face.eyesDistance();
                //if (eyesDistance < 10) continue;
                PointF pointF = new PointF();
                face.getMidPoint(pointF);
                rectf.set(pointF.x - eyesDistance, pointF.y - eyesDistance / 2, pointF.x + eyesDistance, pointF.y + eyesDistance + eyesDistance / 2);
                canvas.drawRect(rectf, paint);
            }

            if (!srcBitmap.isRecycled()) {
                srcBitmap.recycle();
            }
        }
        return newBitmap;
    }

    public void setFaceCrop(float ratio) {
        cropRatio = ratio;
        if(cropRatio<1){
            cropRatio = 1f;
        }
    }

    public void setShowFaceArea(boolean showFaceArea) {
        this.showFaceArea = showFaceArea;
    }

    public void setCropFace(boolean cropFace) {
        this.cropFace = cropFace;
    }

    private class FaceDetectorTask extends AsyncTask<String, Void, Bitmap> {
        int faceCount = 0;
        FaceDetector.Face[] faces;
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (mFaceDetectorListener != null) {
                mFaceDetectorListener.onResult(bitmap,faceCount,faces);
            }
        }

        @Override
        protected Bitmap doInBackground(String... strings) {
            Bitmap srcBitmap = getSrcBitmap();
            if (srcBitmap == null) return null;

            faces = new FaceDetector.Face[maxFaces];
            FaceDetector faceDetector = new FaceDetector(srcBitmap.getWidth(), srcBitmap.getHeight(), maxFaces);
            faceCount = faceDetector.findFaces(srcBitmap, faces);

            Bitmap faceBitmap = srcBitmap;
            if (faceCount > 0) {
                if (showFaceArea) {
                    faceBitmap = drawFaceArea(faceCount, srcBitmap, faces);
                } else {
                    if (cropFace) {
                        faceBitmap = cropFace(srcBitmap, faces[0]);
                    }
                }
            } else {
                Log.d("FaceDetectorHelp", "findFace: not found face");
            }
            return faceBitmap;
        }
    }

    public void setOnFaceDetectorListener(OnFaceDetectorListener onFaceDetectorListener) {
        mFaceDetectorListener = onFaceDetectorListener;
    }

    public interface OnFaceDetectorListener {
        void onResult(Bitmap bitmap, int faceCount, FaceDetector.Face[] faces);
    }
}
