package king.toby.partybooth;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.camerakit.CameraKitView;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static king.toby.partybooth.models.Pose.poseList;

public class CameraActivity extends AppCompatActivity {

    private static final String TAG = "CameraActivity";
    private CameraKitView cameraKitView;
    private TextView countdownText;
    private TextView poseContainer;

    public static final ArrayList<String> currentPoses = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        cameraKitView = findViewById(R.id.camera);
        countdownText = findViewById(R.id.text_countdown);
        poseContainer = findViewById(R.id.text_pose);
        currentPoses.clear();
        poseContainer.setText(generatePose());
        startTimer();
        Log.d(TAG, "onCreate: cameraKitView => " + cameraKitView);
    }

    @Override
    protected void onStart() {
        super.onStart();
        cameraKitView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        cameraKitView.onResume();
    }

    @Override
    protected void onPause() {
        cameraKitView.onPause();
        super.onPause();
    }

    @Override
    protected void onStop() {
        cameraKitView.onStop();
        super.onStop();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        cameraKitView.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    String generatePose() {
        final int randomNumber = (int) (Math.random() * poseList.size() + 1);
        final String pose = poseList.get(randomNumber);

        if(currentPoses.contains(pose)){
            return generatePose();
        } else {
            currentPoses.add(pose);
            for (String poseItem : currentPoses){
                Log.i("Pose name: ", poseItem);
            }
            return pose;
        }
    }

    private void startTimer() {

        new CountDownTimer(21000, 1000) {
            private int imgNum = 0;
            private int displayTimer = 6;

            public void onTick(long millisUntilFinished) {
                long seconds = (millisUntilFinished / 1000);
                displayTimer = displayTimer - 1;

                if (seconds == 0) {
                    countdownText.setBackgroundColor(Color.BLACK);
                    countdownText.setText("DONE!");
                } else {
                    countdownText.setText(String.valueOf(displayTimer - 1));
                }

                if (seconds == 16 || seconds == 11 || seconds == 6 || seconds == 1) {

                    if (seconds != 1) {
                        poseContainer.setText(generatePose());
                    }
                    displayTimer = 6;
                    countdownText.setText("SNAP!");
                    takePhoto(imgNum);
                    imgNum++;
                }
            }

            public void onFinish() {
                Intent startPreviewActivity = new Intent(CameraActivity.this, PreviewActivity.class);
                Bundle b = new Bundle();
                b.putString("pose1", currentPoses.get(0));
                b.putString("pose2", currentPoses.get(1));
                b.putString("pose3", currentPoses.get(2));
                b.putString("pose4", currentPoses.get(3));
                startPreviewActivity.putExtras(b);
                startActivity(startPreviewActivity);
                finish();
            }

        }.start();
    }

    private void takePhoto(final int imageName) {
        cameraKitView.captureImage(new CameraKitView.ImageCallback() {
            @Override
            public void onImage(CameraKitView cameraKitView, byte[] photo) {
                String filePath = getApplicationContext().getFilesDir().toString() + "/image" + imageName + ".jpg";
                File savedPhoto = new File(filePath);
                try {
                    FileOutputStream outputStream = new FileOutputStream(savedPhoto.getPath());
                    outputStream.write(photo);

                    String[] instrumentFileList = CameraActivity.this.fileList();
                    Log.d(TAG, "onImage: list of files => " + Arrays.toString(instrumentFileList));
                    Log.d(TAG, "imageCreated: " + filePath);

                    outputStream.close();

                } catch (java.io.IOException e) {
                    Log.e("Camera", "Capture Failed");
                }
            }
        });
    }

}