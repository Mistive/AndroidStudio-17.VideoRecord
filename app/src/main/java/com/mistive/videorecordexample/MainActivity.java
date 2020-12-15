package com.mistive.videorecordexample;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements SurfaceHolder.Callback { //SurfaceHoder.Callback을 implement함

    private Camera camera;  //하드웨어 카메라로 생성해야 됨
    private MediaRecorder mediaRecorder;
    private Button btn_record;
    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    private boolean recording = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //gradle에 추가한 library. 권한 허용을 손쉽게 시켜주는 Library
        TedPermission.with(this)
                .setPermissionListener(permission)  //PermissionListener
                .setRationaleMessage("녹화를 위하여 권한을 허용해주세요.")
                .setDeniedMessage("권한이 거부되었습니다.")
                //Manifest에서 선언한 퍼미션들을 작성. 카메라/저장공간/오디오
                .setPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO)
                .check();

        btn_record = findViewById(R.id.btn_record);
        //녹화시작 버튼 클릭
        btn_record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //녹화 진행 중이라면?
                if(recording){
                    mediaRecorder.stop();
                    mediaRecorder.release();
                    camera.lock();
                    recording=false;
                }
                //녹화 진행 중이 아니라면?
                else{
                    //???Ui 관련된 처리는 해당 Thread 안에서 처리해주는게 좋다고 함.
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, "녹화가 시작되었습니다.", Toast.LENGTH_SHORT).show();
                            //실제 버튼을 눌렀을 때 동작하는 부분
                            try {
                                mediaRecorder = new MediaRecorder();
                                camera.unlock();
                                mediaRecorder.setCamera(camera);
                                //녹화 소리 발생
                                mediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
                                //비디오 소스에 카메라를 넣어줘라?
                                mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
                                //동영상 녹화 화질 관련 구문 *****
                                mediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH));
                                //촬영 각도 설정
                                mediaRecorder.setOrientationHint(90);
                                //저장 경로 sdcard: Phone의 root 경로
                                mediaRecorder.setOutputFile("/sdcard/test.mp4");
                                //카메라가 현재 바라보고 있는 화면을 surfaceView에 보여줌
                                mediaRecorder.setPreviewDisplay(surfaceHolder.getSurface());
                                mediaRecorder.prepare();
                                mediaRecorder.start();
                                recording=true;
                            } catch (Exception e){
                                e.printStackTrace();
                                mediaRecorder.release();
                            }
                        }
                    });
                }
            }
        });
    }

    PermissionListener permission = new PermissionListener() {

        //권한이 모두 승인되었을 때 호출
        @Override
        public void onPermissionGranted() { //퍼미션이 모두 허용 되었을 때
            Toast.makeText(MainActivity.this, "권한 허가", Toast.LENGTH_SHORT).show();

            //사용할 하드웨어들의 권한이 설정되었을 때 open 등의 작업을 수행함.
            camera = Camera.open();
            camera.setDisplayOrientation(90);
            surfaceView = findViewById(R.id.surfaceView);
            surfaceHolder = surfaceView.getHolder();
            surfaceHolder.addCallback(MainActivity.this);
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }

        @Override
        public void onPermissionDenied(ArrayList<String> deniedPermissions) {   //거부가 된 퍼미션이 존재할 때
            Toast.makeText(MainActivity.this, "권한 거부", Toast.LENGTH_SHORT).show();
        }
    };


    //surfaceView 생명 주기
    @Override
    public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {

    }

    private void refreshCamera(Camera camera) {
        if(surfaceHolder.getSurface() == null){
            return;
        }

        try{
            camera.stopPreview();
        }catch(Exception e){
            e.printStackTrace();
        }

        setCamera(camera);
    }

    private void setCamera(Camera cam) {
        camera = cam;
    }

    //surfaceView의 변화를 감지해서 계속 호출
    @Override
    public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        refreshCamera(camera);
    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {

    }
}