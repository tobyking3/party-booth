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

public class AddPartyToUserListener implements ValueEventListener {
    private static final String TAG = "AddPartyToUserListener";

    private final Activity activity;
    private final DatabaseReference userRef;
    private final TextView partyNameTextView;
    private final TextView partyDescriptionTextView;
    private final AppCompatButton joinButton;
    private final AppCompatEditText joinInput;

    public AddPartyToUserListener(Activity activity) {
        userRef = FirebaseDatabase.getInstance().getReference("users/");
        this.activity = activity;
        this.partyNameTextView = activity.findViewById(R.id.text_party_name);
        this.partyDescriptionTextView = activity.findViewById(R.id.text_party_description);

        this.joinButton = activity.findViewById(R.id.btn_join_party);
        this.joinInput = activity.findViewById(R.id.input_join_party);
    }

    @Override
    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        Party p = dataSnapshot.getValue(Party.class);

        if (p != null) {
            String id = FirebaseAuth.getInstance().getCurrentUser().getUid();

            userRef.child(id)
                    .child("party_id")
                    .setValue(dataSnapshot.getKey());

            partyNameTextView.setText(p.getPartyName());
            partyDescriptionTextView.setText(p.getPartyDescription());
            joinInput.setVisibility(View.GONE);
            joinButton.setVisibility(View.GONE);

        } else {
            partyDescriptionTextView.setText("Party code not recognized, please try again!");
            partyDescriptionTextView.setTextColor(Color.RED);
            Log.e(TAG, "Party doesn't exist");
        }
    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {

    }
}
