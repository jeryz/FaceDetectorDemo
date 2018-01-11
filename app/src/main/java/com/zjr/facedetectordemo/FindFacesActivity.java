package com.zjr.facedetectordemo;

import android.app.Activity;
import android.graphics.Bitmap;
import android.media.FaceDetector;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.Random;

/**
 * Created by zjr on 2018/1/9.
 */

public class FindFacesActivity extends Activity{

    private ImageView imageView;
    private boolean isCropface;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_findface);
        imageView = findViewById(R.id.imageView);

        findFaces(true,false);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isCropface){
                    isCropface = false;
                    findFaces(false,true);
                }else{
                    isCropface = true;
                    findFaces(true,false);
                }
            }
        });

    }

    int[] res = new int[]{R.drawable.fc1,R.drawable.fc2};
    private void findFaces(boolean showFaceArea,boolean cropface) {
        int nextRes = new Random().nextInt(res.length);
        FaceDetectorHelp faceDetectorHelp = new FaceDetectorHelp(5, this, res[nextRes]);
        faceDetectorHelp.setShowFaceArea(showFaceArea);
        faceDetectorHelp.setCropFace(cropface);
        faceDetectorHelp.setOnFaceDetectorListener(new FaceDetectorHelp.OnFaceDetectorListener() {
            @Override
            public void onResult(Bitmap bitmap, int faceCount, FaceDetector.Face[] faces) {
                Toast.makeText(FindFacesActivity.this,"onResult faces="+faceCount,Toast.LENGTH_SHORT).show();
                imageView.setImageBitmap(bitmap);
            }
        });

        faceDetectorHelp.startDetect();
    }

}
