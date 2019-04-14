package king.toby.partybooth;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import king.toby.partybooth.Utils.FirebaseMethods;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "RegisterActivity";

    private ProgressBar mProgressBar;
    private TextView mPleaseWait;
    private EditText mEmail, mPassword, mDisplayName;
    private String registerDisplayName;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseMethods firebaseMethods;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        firebaseMethods = new FirebaseMethods(this);

        mProgressBar = findViewById(R.id.progressBar);
        mPleaseWait = findViewById(R.id.pleaseWait);
        mEmail = findViewById(R.id.input_register_email);
        mPassword = findViewById(R.id.input_register_password);
        mDisplayName = findViewById(R.id.input_register_username);

        mProgressBar.setVisibility(View.GONE);
        mPleaseWait.setVisibility(View.GONE);

        AppCompatButton mRegisterBtn = findViewById(R.id.btn_register);
        mRegisterBtn.setOnClickListener(this);

        setupFirebaseAuth();
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);

    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    private boolean isStringNull(String string){
        return TextUtils.isEmpty(string);
    }

    private void setupFirebaseAuth(){

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                FirebaseUser user = firebaseAuth.getCurrentUser();

                if(user != null){
                    firebaseMethods.addNewUser(registerDisplayName);
                    Toast.makeText(RegisterActivity.this, "Signup successful.", Toast.LENGTH_SHORT).show();

                } else {
                    Log.d(TAG, "onAuthStateChanged: signed out");
                }
            }
        };
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_register:

                String registerEmail = String.valueOf(mEmail.getText());
                String registerPassword = String.valueOf(mPassword.getText());

                registerDisplayName = String.valueOf(mDisplayName.getText());

                if(isStringNull(registerEmail) || isStringNull(registerPassword) || isStringNull(registerDisplayName)){
                    Toast.makeText(RegisterActivity.this, "You must fill out all the fields.", Toast.LENGTH_LONG).show();
                } else {
                    mProgressBar.setVisibility(View.VISIBLE);
                    mPleaseWait.setVisibility(View.VISIBLE);
                    firebaseMethods.registerNewEmail(registerEmail, registerPassword);
                }
                break;
        }
    }
}