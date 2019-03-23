package king.toby.partybooth;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

public class PreviewActivity extends AppCompatActivity {

    private static final String TAG = "PreviewActivity";

    PhotoCanvas photoCanvas;
    String pose1;
    String pose2;
    String pose3;
    String pose4;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle b = getIntent().getExtras();
        if(b != null) {
            pose1 = b.getString("pose1");
            pose2 = b.getString("pose2");
            pose3 = b.getString("pose3");
            pose4 = b.getString("pose4");
        };

        String internalDir = getApplicationContext().getFilesDir().toString();
        photoCanvas = new PhotoCanvas(this, internalDir, pose1, pose2, pose3, pose4);
        setContentView(photoCanvas);
    }
}
