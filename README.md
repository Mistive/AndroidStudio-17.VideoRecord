# Android Studio - 17. VideoRecord
카메라를 활용하여 녹화하는 기능을 구현한다.

## 처음 보는 것들

### TedPermission
* 권한 허용을 쉽게 해주는 Library
* Gradle에서 추가해서 사용한다.
`implementation 'gun0912.ted:tedpermission:2.0.0'`

### SurfaceView??
카메라 관련해서 사용하는 거라던데...
SurfaceHolder를 이용해서 이런저런 처리를 한다.

## 순서
### 1. TedPermission을 통해 권한 추가

1. Camera
2. Write_External_Storage
3. Record_Audio


### 2. PermissionListener
권한이 모두 승인되었을 때에 대한 함수들을 오버라이드하여 구현한다.
ex) 카메라 오픈, surfaceView & surfaceHolder 설정


### 3. 버튼 클릭 시 녹화 시작
녹화가 진행 중이라면 끝내고, 그게 아니라면 시작
**mediaRecorder** 객체를 사용하는데 **runOnUiThread(new Runnable() { }** 에서 모두 구현하였다.
<details>
<summary>자세히</summary>

    mediaRecorder = new MediaRecorder();  
    camera.unlock();  
    mediaRecorder.setCamera(camera);  
    //녹화 소리 발생  
    mediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);  
    //비디오 소스에 카메라를 넣어줘라?  
    mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);  
    //동영상 녹화 화질 관련 구문 *****
    mediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_720P));  
    //촬영 각도 설정  
    mediaRecorder.setOrientationHint(90);  
    //저장 경로 sdcard: Phone의 root 경로  
    mediaRecorder.setOutputFile("/sdcard/test.mp4");  
    //카메라가 현재 바라보고 있는 화면을 surfaceView에 보여줌  
    mediaRecorder.setPreviewDisplay(surfaceHolder.getSurface());  
    mediaRecorder.prepare();  
    mediaRecorder.start();  
    recording=true;

</details>

 

## 공부
### runOnUiThread
안드로이드는 결과를 표시하기 위해서는 MainThread에서 진행이 되어야 한다.
그리고 Ui가 변화하는 것(카메라가 찍은 사진을 계속 가져와 surfaceView에 갱신하는 것)은 Ui가 계속 변화하는 것이다.

따라서 runOnUiThread를 사용하여 Thread 내에서 mediaRecord를 실행해야 한다.

그런데 runOnUiThread가 무엇인가??

이를 알기 위해서는 Thread에 대해 먼저 알아야 한다.

### Thread
> 참고 자료 : https://recipes4dev.tistory.com/172

Thread란 독립적인 이를 하는 프로세스를 의미한다. 그럼 이 Thread는 언제 사용이 될까?

#### Thread는 언제 사용이 되어야 하는가?
* 통신 대기
* 실행 시간이 오래 걸리는 작업

**즉, Main Thread의 실행에 Critical한 영향을 줄 수 있는 작업** 을 수행해야 할 때 Thread를 사용하게 된다.

그런데 이 때 다른 Thread에서 진행한 작업을 Main Thread로 가져오고 싶다면?

이때 사용하는 것이 바로 **Message**이다.

Message에는 정수형 타입의 what과 string 타입의 arg1, arg2 등등의 데이터가 들어간다.
그리고 이 Message는 **Handler**에 의해 Main Thread로 전달된다.

### Runnable 객체
Handler로 보낼 수 있는 것은 Message 뿐만 아니라 Runnable 도 보낼 수 있다.

Runnable은 단순히 데이터를 전달하는 것이 아니라 "실행 코드" 자체를 전달하는 것이다.

그런데 Main Thread에서 Runnable을 써야 하는 경우가 있는데 사용자가 코드를 짜면 Main에서 handler.post(runnable)하고 그걸 main에서 받아오는 것보다 바로 run()시키는게 더 낫지 않겠냐라는 생각에서 나온게 **runOnUiThread()** 이다.


## 이슈
### 외부 저장소 사용
안드로이드 10 이상에서는 외부 저장소를 사용할 때, Manifest에서 `android:requestLegacyExternalStorage="true"`를 추가해줘야 한다.





