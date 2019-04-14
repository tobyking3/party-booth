package king.toby.partybooth;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class FullScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen);

        Intent intent = getIntent();

        final String imageUrl = intent.getStringExtra("image_url");
        final String imageName = intent.getStringExtra("image_name");

        ImageView imageView = findViewById(R.id.image_view_detail);
        TextView textViewCreator = findViewById(R.id.text_view_creator_detail);
        ImageButton imgButton = findViewById(R.id.image_view_save);

        textViewCreator.setText(imageName);

        Picasso.get()
                .load(imageUrl)
                .fit()
                .centerInside()
                .into(imageView);

        imgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Picasso.get()
                        .load(imageUrl)
                        .into(new Target() {
                            @Override
                            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {

                                OutputStream fileOutput = null;
                                Uri outputFileUri;

                                try {
                                    File root = new File(Environment.getExternalStorageDirectory() + File.separator + "PartyBooth" + File.separator);
                                    root.mkdirs();
                                    File sdImageMainDirectory = new File(root, "party-booth-" + System.currentTimeMillis() + ".jpg");
                                    outputFileUri = Uri.fromFile(sdImageMainDirectory);
                                    fileOutput = new FileOutputStream(sdImageMainDirectory);
                                    Toast.makeText(getApplicationContext(), "Image Saved.", Toast.LENGTH_LONG).show();
                                } catch (Exception e) {
                                    Toast.makeText(getApplicationContext(), "An Error Has Occurred", Toast.LENGTH_LONG).show();
                                }
                                try {
                                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutput);
                                    fileOutput.flush();
                                    fileOutput.close();
                                } catch (Exception e) {
                                }
                            }

                            @Override
                            public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                            }

                            @Override
                            public void onPrepareLoad(Drawable placeHolderDrawable) {

                            }
                        });
            }
        });
    }
}
