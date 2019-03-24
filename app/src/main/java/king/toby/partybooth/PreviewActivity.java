package king.toby.partybooth;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class PreviewActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "PreviewActivity";
    private AppCompatButton uploadBtn;
    Bitmap saveBitmap;
    PhotoCanvas photoCanvas;
    String pose1;
    String pose2;
    String pose3;
    String pose4;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);

        uploadBtn = findViewById(R.id.btn_upload);
        uploadBtn.setOnClickListener(this);

        Bundle b = getIntent().getExtras();
        if(b != null) {
            pose1 = b.getString("pose1");
            pose2 = b.getString("pose2");
            pose3 = b.getString("pose3");
            pose4 = b.getString("pose4");
        };

        String internalDir = getApplicationContext().getFilesDir().toString();

//        photoCanvas = new PhotoCanvas(this, internalDir, pose1, pose2, pose3, pose4);
//        photoCanvas.setScaleX(0.88f);
//        photoCanvas.setScaleY(0.88f);
//        photoCanvas.setY(-100);
//
//        ConstraintLayout layout = (ConstraintLayout) findViewById(R.id.activity_preview);
//        layout.addView(photoCanvas);

        // ** This is the original working code to display the preview... but the image can't be saved like this **
        
        //-------------------------------------------------------------------------------

        View v = new PhotoCanvas(this, internalDir, pose1, pose2, pose3, pose4);


        Bitmap finalBitmap = Bitmap.createBitmap(1090, 1930, Bitmap.Config.ARGB_8888);
        Canvas finalCanvas = new Canvas(finalBitmap);
        finalCanvas.drawColor(Color.YELLOW);
        v.draw(finalCanvas);

        ImageView iv = findViewById(R.id.img_bitmap);
        iv.setImageBitmap(finalBitmap);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_upload:
                Log.i(TAG, "onClick: image upload btn clicked");
                break;
        }
    }
}
