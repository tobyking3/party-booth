package king.toby.partybooth.listeners;

import android.app.Activity;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import king.toby.partybooth.R;
import king.toby.partybooth.models.Party;
import king.toby.partybooth.myCallbackInterface;

public class AddPartyToUserListener implements ValueEventListener {
    private static final String TAG = "AddPartyToUserListener";

    private final DatabaseReference userRef;


    private myCallbackInterface callback;

    public AddPartyToUserListener(Activity activity, myCallbackInterface cb) {

        callback = cb;

        userRef = FirebaseDatabase.getInstance().getReference("users/");

    }

    @Override
    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        Party p = dataSnapshot.getValue(Party.class);

        if ( p != null && !p.isNull()) {
            String id = FirebaseAuth.getInstance().getCurrentUser().getUid();

            userRef.child(id)
                    .child("party_id")
                    .setValue(dataSnapshot.getKey());

        } else {

            Log.e(TAG, "Party doesn't exist");
        }
        callback.onCallback(p);
    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {

    }
}
