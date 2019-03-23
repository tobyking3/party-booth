package king.toby.partybooth.Utils;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.InvalidParameterException;

import king.toby.partybooth.listeners.AddPartyToUserListener;
import king.toby.partybooth.models.Party;
import king.toby.partybooth.models.User;

public class FirebaseMethods {
    private static final String TAG = "FirebaseMethods";

//    private String userID;

    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private DatabaseReference myRef;
    private DatabaseReference partiesRef;
    private DatabaseReference userRef;
    private Context mContext;

    public FirebaseMethods(Context context) {
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        myRef = mDatabase.getReference();
        partiesRef = mDatabase.getReference("parties/");
        userRef = mDatabase.getReference("users/");
        mContext = context;

//        //sets user ID if current user is null
//        if(mAuth.getCurrentUser() != null){
//            userID = mAuth.getCurrentUser().getUid();
//        }
    }

    public void registerNewEmail(final String email, final String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "onComplete: Authstate changed: " + mAuth.getCurrentUser().getUid());

                        } else {
                            Toast.makeText(mContext, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
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
                            Log.d(TAG, "signInWithEmail:success: loggen in " + user.getUid() + " " + user.getEmail());
                        } else {
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(mContext, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
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

    public void addNewParty(String partyName) {

        Party party = new Party(partyName);

        try {
            DatabaseReference partyID = partiesRef.push();
            partyID.setValue(party);

        } catch (Exception e) {
            Log.e(TAG, "firebase fucked up");
            e.printStackTrace();
        }
    }

    public void addUserPartyID(String partyID, Activity activity) {
        partiesRef.child(partyID).addListenerForSingleValueEvent(new AddPartyToUserListener(activity));
    }


}