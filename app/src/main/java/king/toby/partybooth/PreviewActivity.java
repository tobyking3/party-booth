package king.toby.partybooth;

import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import java.io.File;

public class PreviewActivity extends AppCompatActivity {

    private static final String TAG = "PreviewActivity";
    PhotoCanvas photoCanvas;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);
        readFileFromInternalStorage();
        styleImage();

    }

    private void readFileFromInternalStorage() {
        File mypath = new File(getApplicationContext().getFilesDir(), "image2.jpg");
        ImageView imageView = findViewById(R.id.img_preview);
        Drawable draw = Drawable.createFromPath(mypath.toString());
        imageView.setImageDrawable(draw);
    }

    private void styleImage(){
        ImageView imageview = findViewById(R.id.img_preview);
        ColorMatrix matrix = new ColorMatrix();
        matrix.setSaturation(0);

        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
        imageview.setColorFilter(filter);
    }



}
