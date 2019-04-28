package king.toby.partybooth;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.Menu;
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
import king.toby.partybooth.models.Party;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, PopupMenu.OnMenuItemClickListener, myCallbackInterface {

    private static final String TAG = "MainActivity";
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private Menu mMainMenu;
    private MenuItem mMainMenuItem;
    private FirebaseMethods firebaseMethods;
    private Boolean partyJoined = false;

    private TextView mPartyNameTextView;
    private TextView mPartyDescriptionTextView;
    private AppCompatButton mJoinPartyBtn;
    private EditText mJoinPartyInput;
    private TextView mPartyInstructionsTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        firebaseMethods = new FirebaseMethods(this);


        mPartyNameTextView = findViewById(R.id.text_party_name);
        mPartyDescriptionTextView = findViewById(R.id.text_party_description);
        mPartyInstructionsTextView = findViewById(R.id.text_party_instructions);
        mJoinPartyBtn = findViewById(R.id.btn_join_party);
        mJoinPartyInput = findViewById(R.id.input_join_party);


        BottomNavigationView bottomNavigationView = findViewById(R.id.nav_bottom_bar);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.btn_feed:
                        Intent startFeedActivity = new Intent(MainActivity.this, FeedActivity.class);
                        if(partyJoined){
                            startActivity(startFeedActivity);
                        } else {
                            Toast.makeText(MainActivity.this, "You must first join a party.", Toast.LENGTH_LONG).show();
                        }
                        break;
                    case R.id.btn_back:
                        break;
                }
                return false;
            }
        });

        mMainMenu = bottomNavigationView.getMenu();
        mMainMenuItem = mMainMenu.getItem(0);
        mMainMenuItem.setChecked(true);

        mJoinPartyBtn.setOnClickListener(this);

        ImageButton mPartyCreationBtn = findViewById(R.id.btn_party_creation);
        mPartyCreationBtn.setOnClickListener(this);

        ImageButton mCameraBtn = findViewById(R.id.btn_camera);
        mCameraBtn.setOnClickListener(this);

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

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        //updateUI(currentUser);
        mAuth.addAuthStateListener(mAuthListener);
        //Get user ID
        if(currentUser != null){
            String id = FirebaseAuth.getInstance().getCurrentUser().getUid();
            firebaseMethods.getUserPartyID(id, MainActivity.this);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_camera:
                Intent startCameraActivity = new Intent(this, CameraActivity.class);
                if(partyJoined){
                    startActivity(startCameraActivity);
                } else {
                    Toast.makeText(MainActivity.this, "You must first join a party.", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.btn_join_party:
                String joinPartyID = String.valueOf(mJoinPartyInput.getText());
                firebaseMethods.addUserPartyID(joinPartyID, this, this);

                break;
            case R.id.btn_party_creation:
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
                Toast.makeText(this, "Logging out.", Toast.LENGTH_LONG).show();
                FirebaseAuth.getInstance().signOut();
                return true;
                default:
                    return false;
        }
    }

    public void updateUI(Party p) {
        Log.i(TAG, "Updating UI");
        if ( p != null && !p.isNull()) {
            mPartyNameTextView.setText(p.getPartyName());
            mPartyDescriptionTextView.setText(p.getPartyDescription());
            mPartyDescriptionTextView.setVisibility(View.VISIBLE);
            mJoinPartyInput.setVisibility(View.GONE);
            mJoinPartyBtn.setVisibility(View.GONE);
            mPartyInstructionsTextView.setVisibility(View.GONE);
        } else {
            //SHOW NOT IN PARTY UI
            partyJoined = false;
            mPartyInstructionsTextView.setText("Party code not recognized, please try again!");
            Log.i(TAG, "Party is null");
        }
    }

    @Override
    public void onCallback(String value) {
        Log.i(TAG, "Party Id callback: " + value);
        if(value != null){
            firebaseMethods.addUserPartyID(value, this, this);
            updateUI(null);
        }
    }

    @Override
    public void onCallback(Party party) {
        if (party != null) {
            partyJoined = true;
            updateUI(party);
        }
    }
}
