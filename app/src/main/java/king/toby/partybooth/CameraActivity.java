package king.toby.partybooth;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.camerakit.CameraKitView;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Arrays;

public class CameraActivity extends AppCompatActivity {

    private static final String TAG = "CameraActivity";
    private CameraKitView cameraKitView;
    private AppCompatButton captureBtn;
    private AppCompatButton previewBtn;
    private TextView countdownText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        cameraKitView = findViewById(R.id.camera);
        previewBtn = findViewById(R.id.btn_preview_photo);
        countdownText = findViewById(R.id.text_countdown);

        previewBtn.setOnClickListener(previewOnClickListener);

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

    private void startTimer(){
        new CountDownTimer(40000, 1000) {
            private int imgNum = 0;

            public void onTick(long millisUntilFinished) {
                long seconds = (millisUntilFinished / 1000);
                countdownText.setText(String.valueOf(seconds));
                if(seconds == 30 || seconds == 20 || seconds == 10){
                    countdownText.setText("SNAP!");
                    takePhoto(imgNum);
                    imgNum++;
                }
            }
            public void onFinish() {
                countdownText.setText("SNAP!");
                takePhoto(imgNum);
                Intent startPreviewActivity = new Intent(CameraActivity.this, PreviewActivity.class);
                startActivity(startPreviewActivity);
            }
        }.start();
    }

    private void takePhoto(final int imageName){
        cameraKitView.captureImage(new CameraKitView.ImageCallback() {
            @Override
            public void onImage(CameraKitView cameraKitView, byte[] photo) {
//                    File savedPhoto = new File(Environment.getExternalStorageDirectory(), "newPhoto.jpg");
                File savedPhoto = new File(getApplicationContext().getFilesDir(), "image" + imageName + ".jpg");
                try {
                    FileOutputStream outputStream = new FileOutputStream(savedPhoto.getPath());
                    outputStream.write(photo);

                    String[] instrumentFileList = CameraActivity.this.fileList();
                    Log.d(TAG, "onImage: list of files => " + Arrays.toString(instrumentFileList));

                    outputStream.close();

                } catch (java.io.IOException e) {
                    Log.e("Camera", "Capture Failed");
                }
            }
        });
    }

    private View.OnClickListener previewOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Log.d(TAG, "onClick: preview a photo");
            Intent startPreviewActivity = new Intent(CameraActivity.this, PreviewActivity.class);
            startActivity(startPreviewActivity);
        }
    };

}
