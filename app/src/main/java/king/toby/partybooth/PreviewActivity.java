package king.toby.partybooth;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import king.toby.partybooth.Utils.FirebaseMethods;

public class PreviewActivity extends AppCompatActivity implements View.OnClickListener, myCallbackInterface {

    private static final String TAG = "PreviewActivity";

    private PhotoCanvas photoCanvas;

    String internalDir;
    String currentPartyID;

    private final FirebaseMethods firebaseMethods;
    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;
    private FirebaseAuth mAuth;

    public PreviewActivity() {
        this.firebaseMethods = new FirebaseMethods(this);
    }

    private AppCompatButton uploadBtn;
    private ProgressBar mProgressBar;
    private EditText mEditTextFileName;

    private Uri mImageUri;

    private StorageTask mUploadTask;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);

        mAuth = FirebaseAuth.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference("uploads");
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("uploads");;

        uploadBtn = findViewById(R.id.btn_upload);
        uploadBtn.setOnClickListener(this);

        mProgressBar = findViewById(R.id.progress_bar);
        mEditTextFileName = findViewById(R.id.edit_text_file_name);

        internalDir = getApplicationContext().getFilesDir().toString();

        List<String> poses = new ArrayList<>();

        Bundle b = getIntent().getExtras();
        if(b != null) {
            poses = b.getStringArrayList("poses");
        };

        photoCanvas = new PhotoCanvas(this, internalDir, poses);
        Canvas finalCanvas = new Canvas();
        photoCanvas.draw(finalCanvas);
        ImageView iv = findViewById(R.id.img_bitmap);
        iv.setImageBitmap(BitmapFactory.decodeFile(internalDir + "/finalimage.png"));

        FirebaseUser user = mAuth.getCurrentUser();
        String userID = user.getUid();

        firebaseMethods.getUserPartyID(userID, PreviewActivity.this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_upload:

                mImageUri = Uri.fromFile(new File(internalDir + "/finalimage.png"));

                if (mUploadTask != null && mUploadTask.isInProgress()) {
                    Toast.makeText(PreviewActivity.this, "Upload in progress", Toast.LENGTH_SHORT).show();
                } else {
                    uploadFile();
                }

                break;
        }
    }

    public void onCallback(String partyID){
        currentPartyID = partyID;
    }

    private void uploadFile() {
        if (mImageUri != null) {

//            currentPartyID + "/" +

            //final StorageReference fileReference = mStorageRef.child(System.currentTimeMillis() + ".jpg");

            final StorageReference fileReference = mStorageRef.child(System.currentTimeMillis() + ".jpg");

            mUploadTask = fileReference.putFile(mImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mProgressBar.setProgress(0);
                                }
                            }, 500);

                            Toast.makeText(PreviewActivity.this, "Upload successful", Toast.LENGTH_LONG).show();

                            fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    final String downloadUrl = uri.toString();
                                    Upload upload = new Upload(mEditTextFileName.getText().toString().trim(), downloadUrl);
                                    String uploadId = mDatabaseRef.push().getKey();
                                    mDatabaseRef.child(uploadId).setValue(upload);
                                }
                            });

                            openFeedActivity();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(PreviewActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            mProgressBar.setProgress((int) progress);
                        }
                    });
        } else {
            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void openFeedActivity() {
        Intent intent = new Intent(this, FeedActivity.class);
        startActivity(intent);
    }
}
