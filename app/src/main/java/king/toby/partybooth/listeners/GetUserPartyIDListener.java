package king.toby.partybooth.listeners;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import king.toby.partybooth.myCallbackInterface;

public class GetUserPartyIDListener implements ValueEventListener {
    private static final String TAG = "GetUserPartyIDListener";

    private myCallbackInterface callback;

    public GetUserPartyIDListener(myCallbackInterface cb) {
        callback = cb;
    }

    @Override
    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        String partyID = dataSnapshot.child("party_id").getValue(String.class);
        Log.i(TAG, String.valueOf(partyID));
        callback.onCallback(partyID);
    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {
    }
}
