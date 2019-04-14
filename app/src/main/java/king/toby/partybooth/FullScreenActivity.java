package king.toby.partybooth;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;

public class FullScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen);


        Intent intent = getIntent();
        final String imageUrl = intent.getStringExtra("image_url");
        String imageName = intent.getStringExtra("image_name");

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

                                OutputStream fOut = null;
                                Uri outputFileUri;

                                try {
                                    File root = new File(Environment.getExternalStorageDirectory()
                                            + File.separator + "PartyBooth" + File.separator);
                                    root.mkdirs();
                                    File sdImageMainDirectory = new File(root, "party-booth-" + System.currentTimeMillis() + ".jpg");
                                    outputFileUri = Uri.fromFile(sdImageMainDirectory);
                                    fOut = new FileOutputStream(sdImageMainDirectory);
                                    Toast.makeText(getApplicationContext(), "Save", Toast.LENGTH_LONG).show();
                                } catch (Exception e) {
                                    Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_LONG).show();
                                }
                                try {
                                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
                                    fOut.flush();
                                    fOut.close();
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























//        Bitmap bm=((BitmapDrawable)imageView.getDrawable()).getBitmap();
//        saveImageFile(bm);
//
//        imageView.buildDrawingCache();
//        final Bitmap bm =imageView.getDrawingCache();
//
//        imgButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.i("DOWNLOAD", "onClick: DOWNLOAD BUTTON CLICKED");
//
//                OutputStream fOut = null;
//                Uri outputFileUri;
//                try {
//                    File root = new File(Environment.getExternalStorageDirectory()
//                            + File.separator + "folder_name" + File.separator);
//                    root.mkdirs();
//                    File sdImageMainDirectory = new File(root, "myPicName.jpg");
//                    outputFileUri = Uri.fromFile(sdImageMainDirectory);
//                    fOut = new FileOutputStream(sdImageMainDirectory);
//                } catch (Exception e) {
//                    Log.i("ERROR IN SAVING IMAGE", "onClick: ");
//                }
//                try {
//                    bm.compress(Bitmap.CompressFormat.PNG, 100, fOut);
//                    fOut.flush();
//                    fOut.close();
//                } catch (Exception e) {
//                }
//            }
//        });


    }
}
