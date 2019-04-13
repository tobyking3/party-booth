package king.toby.partybooth;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import king.toby.partybooth.Utils.FirebaseMethods;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, PopupMenu.OnMenuItemClickListener, myCallbackInterface {

    private static final String TAG = "MainActivity";
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private ImageButton mCameraBtn, mPartyCreationBtn;
    private AppCompatButton mJoinPartyBtn;
    private EditText mJoinPartyInput, mCreatePartyInput;
    private String joinPartyID, createPartyID;
    private FirebaseMethods firebaseMethods;

    private DatabaseReference userRef;
    private TextView partyNameTextView;
    private TextView partyDescriptionTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        //Create Firebase Object
        firebaseMethods = new FirebaseMethods(this);

        mCameraBtn = findViewById(R.id.btn_camera);

        mJoinPartyBtn = findViewById(R.id.btn_join_party);
        mJoinPartyInput = findViewById(R.id.input_join_party);

        mPartyCreationBtn = findViewById(R.id.btn_party_creation);

        mJoinPartyBtn.setOnClickListener(this);
        mPartyCreationBtn.setOnClickListener(this);

        mCameraBtn.setOnClickListener(this);

//        String id = FirebaseAuth.getInstance().getCurrentUser().getUid();
//        userRef = FirebaseDatabase.getInstance().getReference("users/").child(id);
//        userRef.child("party_id").exists();

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
        //Get user ID
        String id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        firebaseMethods.getUserPartyID(id, MainActivity.this);
    }

    // =====================ON CLICK HANDLER======================
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_camera:
                Log.d(TAG, "onClick: starting camera activity");
                Intent startCameraActivity = new Intent(this, CameraActivity.class);
                startActivity(startCameraActivity);
                break;
            case R.id.btn_join_party:
                joinPartyID = String.valueOf(mJoinPartyInput.getText());
                firebaseMethods.addUserPartyID(joinPartyID, this);
                break;
            case R.id.btn_party_creation:
                Toast.makeText(this, "PARTY CREATION", Toast.LENGTH_LONG).show();
                Intent startCreatePartyActivity = new Intent(this, CreatePartyActivity.class);
                startActivity(startCreatePartyActivity);
                break;
        }
    }

    public void showSettings(View v){
        android.support.v7.widget.PopupMenu popupMenu = new android.support.v7.widget.PopupMenu(this, v);
        popupMenu.setOnMenuItemClickListener(this);
        popupMenu.inflate(R.menu.settings_menu);
        popupMenu.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.settings_menu_logout:
                Toast.makeText(this, "logout clicked", Toast.LENGTH_LONG).show();
                FirebaseAuth.getInstance().signOut();
                return true;
                default:
                    return false;
        }
    }

    @Override
    public void onCallback(String value) {
        if(value != null){
            firebaseMethods.addUserPartyID(value, this);
            Log.i(TAG, "onCallback: " + value);
        }
    }
}
