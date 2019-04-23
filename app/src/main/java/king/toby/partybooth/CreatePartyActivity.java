package king.toby.partybooth;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import king.toby.partybooth.Utils.FirebaseMethods;

public class CreatePartyActivity extends AppCompatActivity implements View.OnClickListener, android.support.v7.widget.PopupMenu.OnMenuItemClickListener {

    private static final String TAG = "CreatePartyActivity";

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private EditText mCreatePartyInputName, mCreatePartyInputDescription;
    private FirebaseMethods firebaseMethods;
    private TextView mGeneratedPartyCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_create_party);

        firebaseMethods = new FirebaseMethods(this);

        mCreatePartyInputName = findViewById(R.id.input_create_party_name);
        mCreatePartyInputDescription = findViewById(R.id.input_create_party_description);
        mGeneratedPartyCode = findViewById(R.id.text_party_code);

        ImageButton mReturnBtn = findViewById(R.id.btn_return);
        mReturnBtn.setOnClickListener(this);

        AppCompatButton mCreatePartyBtn = findViewById(R.id.btn_create_party);
        mCreatePartyBtn.setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser() == null){
                    startActivity(new Intent(CreatePartyActivity.this, LoginActivity.class));
                }
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_return:
                Intent startMainActivity = new Intent(CreatePartyActivity.this, MainActivity.class);
                startActivity(startMainActivity);
                break;

            case R.id.btn_create_party:
                String createName = String.valueOf(mCreatePartyInputName.getText());
                String createDescription = String.valueOf(mCreatePartyInputDescription.getText());
                mGeneratedPartyCode.setText(firebaseMethods.addNewParty(createName, createDescription));
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
}
