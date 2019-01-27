package com.bdac.zhcyc.minititok;

import android.content.Intent;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;

import com.bdac.zhcyc.minititok.Utilities.DatabaseUtils;
import com.bdac.zhcyc.minititok.Utilities.NetworkUtils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;

import static com.bdac.zhcyc.minititok.Utilities.MediaFileUtils.getOutputMediaFile;
import static com.bdac.zhcyc.minititok.Utilities.MediaFileUtils.MEDIA_TYPE_IMAGE;
import static com.bdac.zhcyc.minititok.Utilities.MediaFileUtils.MEDIA_TYPE_VIDEO;

/**
 * @author Sebb,
 * @date 2019/1/26
 */

public class CustomCameraActivtiy extends AppCompatActivity implements SurfaceHolder.Callback{

    private static final String TAG = "Seb";

    private SurfaceView mSurfaceView;
    private SurfaceHolder mSurfaceHolder;

    private FloatingActionButton btnPost;
    private Button btnGalley;

    private Camera mCamera;
    private MediaRecorder mMediaRecorder;

    private int rotationDegree = 0;

    private boolean isRecording = false;
    private int mCameraStatus = Camera.CameraInfo.CAMERA_FACING_FRONT;

    private String imagePath = null;
    private String videoPath = null;
    private Uri imageUri = null;
    private Uri videoUri = null;

    private boolean hasSelectedImage = false;
    private boolean hasSelectedVideo = false;

    private static final int DEGREE_90 = 90;
    private static final int DEGREE_180 = 180;
    private static final int DEGREE_270 = 270;
    private static final int DEGREE_360 = 360;

    private static final int CODE_SELECT_IMAGE = 101;
    private static final int CODE_SELECT_VIDEO = 102;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //TODO 这个activity_layout就先这样写吧 最后再改
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_camera_activity);

        btnPost = findViewById(R.id.btn_post);
        btnGalley = findViewById(R.id.btn_gallery);

        //TODO 照相 录像
        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isRecording){
                    releaseMediaRecorder();
                    isRecording = false;
                }else if(!isRecording){
                    prepareMediaRecorder();
                    isRecording = true;
                }
            }
        });

        btnPost.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return true;
            }
        });

        btnGalley.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseVideo();
                chooseImage();
            }
        });

        mSurfaceView = findViewById(R.id.sv_camera);
        mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        mSurfaceHolder.addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        mCamera = getmCamera(mCameraStatus);
        startPreview(surfaceHolder);
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int format, int width, int height) {
        if (surfaceHolder.getSurface() == null) {
            return;
        }

        try {
            mCamera.stopPreview();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            Camera.Parameters parameters = mCamera.getParameters();
            List<Camera.Size> sizes = parameters.getSupportedPreviewSizes();
            Camera.Size optimalSize = getOptimalPreviewSize(sizes, width, height);
            parameters.setPreviewSize(optimalSize.width, optimalSize.height);
            mCamera.setParameters(parameters);
        } catch (Exception e) {
            e.printStackTrace();
        }

        startPreview(surfaceHolder);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        releaseMediaRecorder();
        releaseCameraAndPreview();
    }

    //TODO 自动生成封面图

    private void chooseImage(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"选择一张封面图"),CODE_SELECT_IMAGE);
    }

    private void chooseVideo(){
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"选择一段视频"),CODE_SELECT_VIDEO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data){
        super.onActivityResult(requestCode,resultCode,data);

        if(resultCode == RESULT_OK && data!=null){
            if(requestCode == CODE_SELECT_IMAGE){
                imageUri = data.getData();
                hasSelectedImage = true;
                Log.d(TAG,imageUri.toString());
            }else if(requestCode == CODE_SELECT_VIDEO){
                videoUri = data.getData();
                hasSelectedVideo = true;
                Log.d(TAG,videoUri.toString());
            }

            if(hasSelectedImage&&hasSelectedVideo){
                Log.d(TAG,"posting!");
                NetworkUtils.postVideo(imageUri,videoUri,CustomCameraActivtiy.this,null);
                resetSelection();
            }
        }
    }

    private void resetSelection(){
        hasSelectedImage = false;
        hasSelectedVideo = false;
    }

    private Camera getmCamera(final int CAMERA_TYPE){
        if(mCamera != null){
            releaseCameraAndPreview();
        }

        Camera camera = Camera.open(CAMERA_TYPE);
        rotationDegree = getCameraDisplayOrientation(CAMERA_TYPE);
        camera.setDisplayOrientation(rotationDegree);

        Log.d(TAG,rotationDegree+"");

        Camera.Parameters params = camera.getParameters();
        List<String> focusModes = params.getSupportedFocusModes();

        if (focusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
            Camera.Parameters mParams = camera.getParameters();
            mParams.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
            camera.setParameters(mParams);
        }

        return camera;
    }

    private void startPreview(SurfaceHolder surfaceHolder){
        try{
            mCamera.setPreviewDisplay(surfaceHolder);
            mCamera.startPreview();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void releaseCameraAndPreview(){
        mCamera.stopPreview();
        mCamera.release();
        mCamera=null;
    }

    //TODO 加zoom

    private void zoomIn(){

    }

    private void swithCamera(){
        releaseCameraAndPreview();
        if(mCameraStatus == Camera.CameraInfo.CAMERA_FACING_FRONT){
            mCameraStatus = Camera.CameraInfo.CAMERA_FACING_BACK;
        }else if(mCameraStatus == Camera.CameraInfo.CAMERA_FACING_BACK){
            mCameraStatus = Camera.CameraInfo.CAMERA_FACING_FRONT;
        }
        mCamera = getmCamera(mCameraStatus);
        startPreview(mSurfaceHolder);
    }

    private void prepareMediaRecorder(){
        mMediaRecorder = new MediaRecorder();
        mCamera.unlock();
        mMediaRecorder.setCamera(mCamera);

        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

        mMediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH));

        videoPath = getOutputMediaFile(MEDIA_TYPE_VIDEO).toString();
        mMediaRecorder.setOutputFile(videoPath);

        mMediaRecorder.setPreviewDisplay(mSurfaceHolder.getSurface());
        mMediaRecorder.setOrientationHint(rotationDegree);

        try{
            mMediaRecorder.prepare();
            mMediaRecorder.start();
        }catch (Exception e){
            releaseMediaRecorder();
            e.printStackTrace();
        }
    }

    private void releaseMediaRecorder(){
        if(mMediaRecorder == null){
            return;
        }
        mMediaRecorder.stop();
        mMediaRecorder.reset();
        mMediaRecorder.release();
        mMediaRecorder = null;
        mCamera.lock();

        try{
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(videoPath))));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private Camera.PictureCallback mPicture = (data, camera) -> {
        File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
        if (pictureFile == null) {
            return;
        }
        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            fos.write(data);
            fos.close();
        } catch (IOException e) {
            Log.d("mPicture", "Error accessing file: " + e.getMessage());
        }
        try{
            mCamera.startPreview();
        }catch (Exception e){
            Log.d(TAG,"preview Wrong");
        }
        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(pictureFile)));
    };


    private int getCameraDisplayOrientation(int cameraId) {
        android.hardware.Camera.CameraInfo info =
                new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(cameraId, info);
        int rotation = getWindowManager().getDefaultDisplay()
                .getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = DEGREE_90;
                break;
            case Surface.ROTATION_180:
                degrees = DEGREE_180;
                break;
            case Surface.ROTATION_270:
                degrees = DEGREE_270;
                break;
            default:
                break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % DEGREE_360;
            result = (DEGREE_360 - result) % DEGREE_360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + DEGREE_360) % DEGREE_360;
        }
        return result;
    }

    private Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int w, int h) {
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) h / w;

        if (sizes == null) return null;

        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = Math.min(w, h);

        for (Camera.Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
    }
}
