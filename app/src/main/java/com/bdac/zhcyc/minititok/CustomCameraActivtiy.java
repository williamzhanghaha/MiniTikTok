package com.bdac.zhcyc.minititok;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewTreeObserver;
import com.bdac.zhcyc.minititok.Utilities.MediaFileUtils;
import com.bdac.zhcyc.minititok.Utilities.NetworkUtils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.function.LongFunction;

import androidx.appcompat.app.AppCompatActivity;

import static com.bdac.zhcyc.minititok.Utilities.MediaFileUtils.getOutputMediaFile;
import static com.bdac.zhcyc.minititok.Utilities.MediaFileUtils.MEDIA_TYPE_IMAGE;
import static com.bdac.zhcyc.minititok.Utilities.MediaFileUtils.MEDIA_TYPE_VIDEO;
import static java.lang.Math.abs;

/**
 * @author Sebb,
 * @date 2019/1/26
 */

public class CustomCameraActivtiy extends AppCompatActivity implements SurfaceHolder.Callback{

    private static final String TAG = "Seb";

    private SurfaceView mSurfaceView;
    private SurfaceHolder mSurfaceHolder;

    private FloatingActionButton btnPost;
    private FloatingActionButton btnGalley;
    private FloatingActionButton btnSwitch;

    private Camera mCamera;
    private MediaRecorder mMediaRecorder;

    private boolean isRecording = false;
    private boolean recordFinshed = false;
    private int mCameraStatus = Camera.CameraInfo.CAMERA_FACING_FRONT;

    private String shootImagePath = null;
    private String shootVideoPath = null;

    private Uri selectImageUri = null;
    private Uri selectVideoUri = null;

    private boolean hasSelectedImage = false;
    private boolean hasSelectedVideo = false;

    private static final int DEGREE_90 = 90;
    private static final int DEGREE_180 = 180;
    private static final int DEGREE_270 = 270;
    private static final int DEGREE_360 = 360;

    private static final int CODE_SELECT_IMAGE = 101;
    private static final int CODE_SELECT_VIDEO = 102;

    private static final float SCALE_NUM = 1.4f;

    private int ZOOM_MAX = 0;

    private float INI_X;
    private float INI_Y;

    private float MAX_X;
    private float MAX_Y;

    private float x0,y0,x1,y1,dx,dy;
    private int zoomValue;

    private Uri woyebuzhidaoshiganshenmedeUri = null;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //TODO 这个activity_layout就先这样写吧 最后再改
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_camera_activity);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        MAX_X = size.x;
        MAX_Y = size.y;

        btnPost = findViewById(R.id.btn_post);
        btnGalley = findViewById(R.id.btn_gallery);
        btnSwitch = findViewById(R.id.btn_switch);

        btnPost.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                INI_X = btnPost.getX()+btnPost.getWidth()/2f;
                INI_Y = btnPost.getY()+btnPost.getHeight()/2f;

                btnPost.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });

        btnPost.setOnClickListener(null);

        btnPost.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:{
                        x0 = event.getRawX();
                        y0 = event.getRawY();
                        zoomValue = -1000;

                        setBtnToScale(btnPost,SCALE_NUM);
                        btnPost.setColorFilter(Color.RED);
                        if(!isRecording){
                            prepareMediaRecorder();
                        }
                        break;
                    }
                    case MotionEvent.ACTION_MOVE:{
                        if(isRecording){
                            x1 = event.getRawX();
                            y1 = event.getRawY();

                            v.setX(x1 - v.getWidth() / 2.0f);
                            v.setY(y1 - v.getHeight());

                            dx = x1 - x0;
                            dy = y1 - y0;
                            x0 = x1;
                            y0 = y1;

                            if(dy>5){
                                zoomValue = -1;
                            }else if(dy<-5){
                                zoomValue = 1;
                            }
//                        zoomValue = ((-dy*ZOOM_MAX)/(MAX_Y-y0));

                            zoomByValue(zoomValue);
                        }
                        break;
                    }
                    case MotionEvent.ACTION_UP:{
                        try{
                            setBtnToScale(btnPost,1);
                            setBtnBack(btnPost);
                        }catch (Exception e){
                            Log.d(TAG,"animation wrong!");
                        }

                        btnPost.clearColorFilter();
                        try{
                            zoomByValue(-1000);
                        }catch (Exception e){
                            Log.d(TAG,"Wrong!");
                        }

                        if(isRecording){

                            releaseMediaRecorder();

                            if(recordFinshed){
                                Uri imageUri = generateThumbnail(shootVideoPath);
                                Uri videoUri = Uri.fromFile(new File(shootVideoPath));

                                lauchVideoview(imageUri,videoUri);
                            }
                        }

                        break;
                    }
                }
                return true;
            }

        });

        btnGalley.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseVideo();
            }
        });

        btnSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                swithCamera();
            }
        });

        mSurfaceView = findViewById(R.id.sv_camera);
        mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        mSurfaceHolder.addCallback(this);
    }


    @Override
    protected void onPause() {
        super.onPause();
        stopRecord();
    }

    private void stopRecord () {
        try {
            mMediaRecorder.stop();
            releaseMediaRecorder();
        } catch (Exception e) {
            e.printStackTrace();
        }
        isRecording = false;
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaScanIntent.setData(woyebuzhidaoshiganshenmedeUri);
        this.sendBroadcast(mediaScanIntent);
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        mCamera = getmCamera(mCameraStatus);
        zoomInit();
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

    private void setBtnToScale(FloatingActionButton btn,float scaleNum){
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(btn,"scaleX",scaleNum);
        scaleX.setDuration(500);

        ObjectAnimator scaleY = ObjectAnimator.ofFloat(btn,"scaleY",scaleNum);
        scaleY.setDuration(500);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(scaleX);
        animatorSet.playTogether(scaleY);
        animatorSet.start();
    }

    private void setBtnBack(FloatingActionButton btn){
        ObjectAnimator transX = ObjectAnimator.ofFloat(btn,"X",INI_X-btnPost.getWidth()/2f);
        transX.setDuration(500);

        ObjectAnimator transY = ObjectAnimator.ofFloat(btn,"Y",INI_Y-btnPost.getHeight()/2f);
        transY.setDuration(500);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(transX);
        animatorSet.playTogether(transY);
        animatorSet.start();
    }

    /**
     * 重载了方法
     * @param videoUri 参数可以是绝对路径或者uri
     * @return
     */

    private Uri generateThumbnail(Uri videoUri){
        String videoPath = MediaFileUtils.convertUriToPath(this,videoUri);
        Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, MediaStore.Video.Thumbnails.MINI_KIND);
        Uri imageUri = Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, null,null));

        return imageUri;
    }

    private Uri generateThumbnail(String videoPath){
        Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, MediaStore.Video.Thumbnails.MINI_KIND);
        Uri imageUri = Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, null,null));
        return imageUri;
    }

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
                selectImageUri = data.getData();
                hasSelectedImage = true;
//                Log.d(TAG, selectImageUri.toString());
            }else if(requestCode == CODE_SELECT_VIDEO){
                selectVideoUri = data.getData();
                hasSelectedVideo = true;
                Log.d(TAG, selectVideoUri.toString());
            }

            if(hasSelectedVideo){
                //Todo
                selectImageUri = generateThumbnail(selectVideoUri);
                resetSelection();
                lauchVideoview(selectImageUri,selectVideoUri);
            }
        }
    }

    private void resetSelection(){
        hasSelectedImage = false;
        hasSelectedVideo = false;
    }

    private void lauchVideoview(Uri imageUri,Uri videoUri){
        String stringimageUri = imageUri.toString();
        String stringvideoUri = videoUri.toString();

        Intent intent = new Intent();

        intent.putExtra("imageUri",stringimageUri);
        intent.putExtra("videoUri",stringvideoUri);

        intent.setClass(CustomCameraActivtiy.this,PostingActivity.class);
        startActivity(intent);
    }

    private Camera getmCamera(final int CAMERA_TYPE){
        if(mCamera != null){
            releaseCameraAndPreview();
        }

        Camera camera = Camera.open(CAMERA_TYPE);
        camera.setDisplayOrientation(getCameraDisplayOrientation(CAMERA_TYPE));

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
        if(mCamera == null){
            return;
        }
        mCamera.stopPreview();
        mCamera.release();
        mCamera=null;
    }

    private void zoomInit(){
        if(mCamera == null){
            return;
        }
        Camera.Parameters parameters = mCamera.getParameters();
        if(!parameters.isZoomSupported()){
            return;
        }
        List<Integer> zoomlist = parameters.getZoomRatios();

        ZOOM_MAX = zoomlist.size();
    }

    private void zoomByValue(int zoomvalue){
        if(mCamera == null){
            return;
        }

        Camera.Parameters parameters = mCamera.getParameters();

        if(!parameters.isZoomSupported()){
            return;
        }

        int nowZoomValue = parameters.getZoom();
        int nextZoomValue = nowZoomValue+zoomvalue;

        if(0<=nextZoomValue&&nextZoomValue<ZOOM_MAX){
            parameters.setZoom(nextZoomValue);
        }else if(nextZoomValue>=ZOOM_MAX){
            parameters.setZoom(ZOOM_MAX-1);
        }else if(nextZoomValue<0){
            parameters.setZoom(0);
        }
        mCamera.setParameters(parameters);
    }

    private void swithCamera(){
        releaseCameraAndPreview();
        if(mCameraStatus == Camera.CameraInfo.CAMERA_FACING_FRONT){
            mCameraStatus = Camera.CameraInfo.CAMERA_FACING_BACK;
        }else if(mCameraStatus == Camera.CameraInfo.CAMERA_FACING_BACK){
            mCameraStatus = Camera.CameraInfo.CAMERA_FACING_FRONT;
        }
        mCamera = getmCamera(mCameraStatus);
        zoomInit();
        startPreview(mSurfaceHolder);
    }

    private void shotPhoto(){
        mCamera.takePicture(null,null, mPicture);
    }

    private void prepareMediaRecorder(){
        btnGalley.setVisibility(View.GONE);
        btnSwitch.setVisibility(View.GONE);

        mMediaRecorder = new MediaRecorder();
        mCamera.unlock();
        mMediaRecorder.setCamera(mCamera);

        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

        mMediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH));

        File shootVideo = getOutputMediaFile(MEDIA_TYPE_VIDEO);
        woyebuzhidaoshiganshenmedeUri = Uri.fromFile(shootVideo);
        shootVideoPath = shootVideo.toString();

        mMediaRecorder.setOutputFile(shootVideoPath);

        mMediaRecorder.setPreviewDisplay(mSurfaceHolder.getSurface());

        int mRotationDegree;

        switch (mCameraStatus) {
            case Camera.CameraInfo.CAMERA_FACING_BACK:
                mRotationDegree = 90;
                break;
            case Camera.CameraInfo.CAMERA_FACING_FRONT:
                mRotationDegree = 270;
                break;
            default:
                mRotationDegree = 0;
                break;
        }

        mMediaRecorder.setOrientationHint(mRotationDegree);

        try{
            mMediaRecorder.prepare();
            mMediaRecorder.start();
        }catch (Exception e){
            releaseMediaRecorder();
            e.printStackTrace();
        }

        isRecording = true;
    }

    private void releaseMediaRecorder(){
        if(mMediaRecorder == null){
            return;
        }
        try{
            mMediaRecorder.stop();
            mMediaRecorder.reset();
            mMediaRecorder.release();
            mMediaRecorder = null;
            mCamera.lock();
            recordFinshed = true;
        }catch (Exception e){
            e.printStackTrace();
            recordFinshed = false;
        }

        btnGalley.setVisibility(View.VISIBLE);
        btnSwitch.setVisibility(View.VISIBLE);


        try{
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(shootVideoPath))));
        }catch (Exception e){
            e.printStackTrace();
        }

        isRecording = false;
    }

    private Camera.PictureCallback mPicture = (data, camera) -> {
        File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
        shootImagePath = pictureFile.toString();

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
            if (abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
            if (abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = abs(size.height - targetHeight);
            }
        }

        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
    }
}
