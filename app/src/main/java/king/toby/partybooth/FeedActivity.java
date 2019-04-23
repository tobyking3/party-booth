package king.toby.partybooth;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import king.toby.partybooth.Utils.FirebaseMethods;

public class FeedActivity extends AppCompatActivity implements myCallbackInterface, ImageAdapter.OnItemClickListener {
    private FirebaseMethods firebaseMethods;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseRef;

    public FeedActivity() {
        this.firebaseMethods = new FirebaseMethods(this);
    }

    private RecyclerView mRecyclerView;
    private ImageAdapter mAdapter;

    private ProgressBar mProgressCircle;

    private List<Upload> mUploads;

    String currentPartyID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        String userID = user.getUid();
        firebaseMethods.getUserPartyID(userID, FeedActivity.this);

        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mProgressCircle = findViewById(R.id.progress_circle);

        ImageButton mCameraBtn = findViewById(R.id.btn_camera);
        mCameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent startCameraActivity = new Intent(FeedActivity.this, CameraActivity.class);
                startActivity(startCameraActivity);
            }
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.nav_bottom_bar);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.btn_feed:
                        break;
                    case R.id.btn_back:
                        Intent startMainActivity = new Intent(FeedActivity.this, MainActivity.class);
                        startActivity(startMainActivity);
                        break;
                }

                return false;
            }
        });

        Menu mMainMenu = bottomNavigationView.getMenu();
        MenuItem mMainMenuItem = mMainMenu.getItem(2);
        mMainMenuItem.setChecked(true);

        mUploads = new ArrayList<>();

        mDatabaseRef = FirebaseDatabase.getInstance().getReference("uploads");
    }

    public void onCallback(String partyID){

        currentPartyID = partyID;

        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.child(currentPartyID).getChildren()) {
                    Upload upload = postSnapshot.getValue(Upload.class);
                    mUploads.add(upload);
                }

                mAdapter = new ImageAdapter(FeedActivity.this, mUploads);
                mRecyclerView.setAdapter(mAdapter);
                mAdapter.setOnItemClickListener(FeedActivity.this);

                mProgressCircle.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(FeedActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                mProgressCircle.setVisibility(View.INVISIBLE);
            }
        });
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    public void onItemClick(int position) {
        Intent fullScreenIntent = new Intent(this, FullScreenActivity.class);
        Upload clickedImage = mUploads.get(position);
        fullScreenIntent.putExtra("image_url", clickedImage.getImageUrl());
        fullScreenIntent.putExtra("image_name", clickedImage.getName());

        startActivity(fullScreenIntent);
    }
}
