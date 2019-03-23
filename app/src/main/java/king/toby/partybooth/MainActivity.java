package king.toby.partybooth;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import king.toby.partybooth.Utils.FirebaseMethods;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MainActivity";
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private AppCompatButton mLogoutBtn, mCameraBtn, mJoinPartyBtn, mCreatePartyBtn;
    private EditText mJoinPartyInput, mCreatePartyInput;
    private String joinPartyID, createPartyID;
    private FirebaseMethods firebaseMethods;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        //Create Firebase Object
        firebaseMethods = new FirebaseMethods(this);

        mLogoutBtn = findViewById(R.id.btn_logout);
        mCameraBtn = findViewById(R.id.btn_camera);

        mJoinPartyBtn = findViewById(R.id.btn_join_party);
        mJoinPartyInput = findViewById(R.id.input_join_party);

        mCreatePartyBtn = findViewById(R.id.btn_create_party);
        mCreatePartyInput = findViewById(R.id.input_create_party);

        mJoinPartyBtn.setOnClickListener(this);
        mCreatePartyBtn.setOnClickListener(this);

        mLogoutBtn.setOnClickListener(this);
        mCameraBtn.setOnClickListener(this);

        //INITIALIZE FIREBASE
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser() == null){
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                }
            }
        };
    }

    // =====================ON START======================
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        //updateUI(currentUser);
        mAuth.addAuthStateListener(mAuthListener);
    }

    // =====================ON CLICK HANDLER======================
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_logout:
                FirebaseAuth.getInstance().signOut();
                break;
            case R.id.btn_camera:
                Log.d(TAG, "onClick: starting camera activity");
                Intent startCameraActivity = new Intent(this, CameraActivity.class);
                startActivity(startCameraActivity);
                break;
            case R.id.btn_join_party:
                joinPartyID = String.valueOf(mJoinPartyInput.getText());
                firebaseMethods.addUserPartyID(joinPartyID, this);
                break;
            case R.id.btn_create_party:
                createPartyID = String.valueOf(mCreatePartyInput.getText());
                firebaseMethods.addNewParty(createPartyID);
                break;
        }
    }
}
