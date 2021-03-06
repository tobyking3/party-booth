package king.toby.partybooth;

import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
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
        final int randomNumber = (int) (Math.random() * poseList.size());
        final String pose = poseList.get(randomNumber);

        if(currentPoses.contains(pose)){
            return generatePose();
        } else {
            currentPoses.add(pose);
            return pose;
        }
    }

    private void startTimer() {

        final MediaPlayer cameraSnap = MediaPlayer.create(this, R.raw.camera_sound);

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
                    cameraSnap.start();
                    takePhoto(imgNum);
                    imgNum++;
                }
            }

            public void onFinish() {
                Intent startPreviewActivity = new Intent(CameraActivity.this, PreviewActivity.class);
                Bundle b = new Bundle();
                b.putStringArrayList("poses", currentPoses);
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
                    outputStream.close();

                } catch (java.io.IOException e) {
                    Log.e(TAG, "Capture Failed");
                }
            }
        });
    }

}