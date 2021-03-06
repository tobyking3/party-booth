package king.toby.partybooth;

import android.content.Intent;
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

import king.toby.partybooth.Utils.FirebaseMethods;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "LoginActivity";

    private ProgressBar mProgressBar;
    private TextView mPleaseWait;
    private EditText mEmail, mPassword;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseMethods firebaseMethods;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseMethods = new FirebaseMethods(this);

        mProgressBar = findViewById(R.id.progressBar);
        mPleaseWait = findViewById(R.id.pleaseWait);
        mEmail = findViewById(R.id.input_login_email);
        mPassword = findViewById(R.id.input_login_password);

        mProgressBar.setVisibility(View.GONE);
        mPleaseWait.setVisibility(View.GONE);

        AppCompatButton mLoginBtn = findViewById(R.id.btn_login);
        mLoginBtn.setOnClickListener(this);

        TextView mRegisterBtn = findViewById(R.id.link_create_account);
        mRegisterBtn.setOnClickListener(this);

        setupFirebaseAuth();
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    private boolean isStringNull(String string){
        return TextUtils.isEmpty(string);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_login:
                String loginEmail = String.valueOf(mEmail.getText());
                String loginPassword = String.valueOf(mPassword.getText());

                if(isStringNull(loginEmail) || isStringNull(loginPassword)){
                    Toast.makeText(LoginActivity.this, "You must fill out all the fields.", Toast.LENGTH_LONG).show();
                } else {
                    mProgressBar.setVisibility(View.VISIBLE);
                    mPleaseWait.setVisibility(View.VISIBLE);
                    firebaseMethods.loginExistingUser(loginEmail, loginPassword);
                }
                break;

            case R.id.link_create_account:
                Intent startRegisterActivity = new Intent(this, RegisterActivity.class);
                startActivity(startRegisterActivity);
                break;
        }
    }

    private void setupFirebaseAuth(){
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser() != null){
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                }
            }
        };
    }
}
