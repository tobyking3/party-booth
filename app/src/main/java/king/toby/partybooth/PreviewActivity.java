package king.toby.partybooth;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import king.toby.partybooth.Utils.FirebaseMethods;

public class PreviewActivity extends AppCompatActivity implements View.OnClickListener, myCallbackInterface {

    private static final String TAG = "PreviewActivity";
    private final FirebaseMethods fm;
    private PhotoCanvas pc;
    String internalDir;
    String currentPartyID;

    private StorageReference mStorageRef;
    private FirebaseDatabase mDatabase;
    private FirebaseAuth auth;

    public PreviewActivity() {
        this.fm = new FirebaseMethods(this);
    }

    private AppCompatButton uploadBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);

        auth = FirebaseAuth.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        mDatabase = FirebaseDatabase.getInstance();

        checkFilePermissions();

        uploadBtn = findViewById(R.id.btn_upload);
        uploadBtn.setOnClickListener(this);

        List<String> poses = new ArrayList<>();

        Bundle b = getIntent().getExtras();
        if(b != null) {
            poses = b.getStringArrayList("poses");
        };

        internalDir = getApplicationContext().getFilesDir().toString();

        pc = new PhotoCanvas(this, internalDir, poses);
        Canvas finalCanvas = new Canvas();
        finalCanvas.drawColor(Color.YELLOW);
        pc.draw(finalCanvas);
        ImageView iv = findViewById(R.id.img_bitmap);
        Log.i(TAG, "Finished drawing");
        iv.setImageBitmap(BitmapFactory.decodeFile(internalDir + "/finalimage.png"));

        //get the signed in user
        FirebaseUser user = auth.getCurrentUser();
        String userID = user.getUid();
        fm.getUserPartyID(userID, PreviewActivity.this);
    }

    private void checkFilePermissions() {
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP){
            int permissionCheck = PreviewActivity.this.checkSelfPermission("Manifest.permission.READ_EXTERNAL_STORAGE");
            permissionCheck += PreviewActivity.this.checkSelfPermission("Manifest.permission.WRITE_EXTERNAL_STORAGE");
            if (permissionCheck != 0) {
                this.requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE,android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1001); //Any number
            }
        }else{
            Log.d(TAG, "checkBTPermissions: No need to check permissions. SDK version < LOLLIPOP.");
        }
    }

    private void toastMessage(String message){
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_upload:

                Uri uri = Uri.fromFile(new File(internalDir + "/finalimage.png"));

                StorageReference storageReference = mStorageRef.child("partybooth/parties/" + currentPartyID + "/newImage.jpg");

                storageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Log.i(TAG, "onSuccess: Image uploaded");
                        toastMessage("Upload Success");

                        startActivity(new Intent(PreviewActivity.this, FeedActivity.class));

                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i(TAG, "onFailure: Error while uploading image");
                        toastMessage("Upload Failed");
                    }
                });
                break;
        }
    }

    public void onCallback(String partyID){
        currentPartyID = partyID;
    }
}
