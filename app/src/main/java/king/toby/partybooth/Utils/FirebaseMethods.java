package king.toby.partybooth.Utils;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import king.toby.partybooth.R;
import king.toby.partybooth.listeners.AddPartyToUserListener;
import king.toby.partybooth.listeners.GetUserPartyIDListener;
import king.toby.partybooth.models.Party;
import king.toby.partybooth.models.User;
import king.toby.partybooth.myCallbackInterface;

public class FirebaseMethods {
    private static final String TAG = "FirebaseMethods";
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private DatabaseReference myRef;
    private DatabaseReference partiesRef;
    private DatabaseReference userRef;
    private Context mContext;
    private DatabaseReference partyID;

    public FirebaseMethods(Context context) {
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        myRef = mDatabase.getReference();
        partiesRef = mDatabase.getReference("parties/");
        userRef = mDatabase.getReference("users/");
        mContext = context;
    }

    public void registerNewEmail(final String email, final String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "onComplete: Authstate changed: " + mAuth.getCurrentUser().getUid());
                        } else {
                            Toast.makeText(mContext, "Authentication failed.", Toast.LENGTH_SHORT).show();

                            ProgressBar mProgressBar = ((Activity) mContext).findViewById(R.id.progressBar);
                            TextView mPleaseWait = ((Activity) mContext).findViewById(R.id.pleaseWait);

                            mProgressBar.setVisibility(View.GONE);
                            mPleaseWait.setVisibility(View.GONE);
                        }
                    }
                });
    }

    public void loginExistingUser(final String email, final String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            Log.d(TAG, "signInWithEmail:success: logged in " + user.getUid() + " " + user.getEmail());
                        } else {
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(mContext, "Authentication failed.", Toast.LENGTH_SHORT).show();

                            ProgressBar mProgressBar = ((Activity) mContext).findViewById(R.id.progressBar);
                            TextView mPleaseWait = ((Activity) mContext).findViewById(R.id.pleaseWait);

                            mProgressBar.setVisibility(View.GONE);
                            mPleaseWait.setVisibility(View.GONE);
                        }
                    }
                });
    }

    public void addNewUser(String displayName) {
        User user = new User(displayName);

        try {
            String id = mAuth.getCurrentUser().getUid();

            myRef.child("users")
                    .child(id)
                    .setValue(user);

        } catch (NullPointerException npe) {
            Log.e(TAG, "User is not logged in");
        }
    }

    public String addNewParty(String partyName, String partyDescription) {

        Party party = new Party(partyName, partyDescription);

        try {
            partyID = partiesRef.push();
            partyID.setValue(party);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return partyID.getKey();
    }

    public void addUserPartyID(String partyID, Activity activity) {
        partiesRef.child(partyID).addListenerForSingleValueEvent(new AddPartyToUserListener(activity));
    }

    public void getUserPartyID(String userID, myCallbackInterface cb) {
        userRef.child(userID).addListenerForSingleValueEvent(new GetUserPartyIDListener(cb));
    }

}