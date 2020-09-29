package ir.archroid.ponyexpress;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.Objects;
import ir.archroid.ponyexpress.Model.User;

public class ProfileActivity extends AppCompatActivity {

    private String userid;
    private DatabaseReference userRef;
    private DatabaseReference friendsReqRef;
    private DatabaseReference friendsRef;
    private FirebaseUser firebaseUser;

    private CoordinatorLayout coordinatorLayout;
    private ImageView iv_profile;
    private TextView tv_bio;
    private FloatingActionButton floatingActionButton;

    private Toolbar toolbar;
    private CollapsingToolbarLayout toolbar_layout;

    private String STATUS = "not_friends";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Util.enableDatabasePresistance();

        coordinatorLayout = findViewById(R.id.coordinator);
        iv_profile = findViewById(R.id.iv_profile);
        tv_bio = findViewById(R.id.tv_bio);
        toolbar_layout = findViewById(R.id.toolbar_layout);
        floatingActionButton = findViewById(R.id.floatingActionButton);

        //        modify toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        userid = getIntent().getStringExtra("userid");

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        friendsRef = FirebaseDatabase.getInstance().getReference().child("Friends").child(firebaseUser.getUid());
        friendsReqRef = FirebaseDatabase.getInstance().getReference().child("FriendRequests");
        userRef = FirebaseDatabase.getInstance().getReference("Users").child(userid);
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);

                tv_bio.setText(user.getBio());
                toolbar_layout.setTitle(user.getUsername());

                if (!user.getImageURL().equals("default")) {
                    Glide.with(getApplicationContext()).load(user.getImageURL()).into(iv_profile);
                } else {
                    iv_profile.setImageDrawable(getResources().getDrawable(R.drawable.profile_bg));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        setStatus();

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                floatingActionButton.setEnabled(false);

                // --------------------------- NOT FRIEND STATUS --------------------------- //
                if (STATUS.equals("not_friends")) {
                    friendsReqRef.child(firebaseUser.getUid()).child(userid).child("request_type").setValue("sent");
                    friendsReqRef.child(userid).child(firebaseUser.getUid()).child("request_type").setValue("received")
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                makeSnackbar.message(getApplicationContext(), coordinatorLayout, "Request sent!");
                                floatingActionButton.setEnabled(true);
                                STATUS = "request_sent";

                            } else {
                                makeSnackbar.Alert(getApplicationContext(), coordinatorLayout, task.getException().getMessage());
                                floatingActionButton.setEnabled(true);
                            }
                        }
                    });
                }
                if (STATUS.equals("request_sent")){
                    makeSnackbar.info(getApplicationContext() , coordinatorLayout , "Request already sent!");
                }
                if (STATUS.equals("request_received")){
                    makeSnackbar.info(getApplicationContext() , coordinatorLayout , "Request already received!");
                }
                if (STATUS.equals("friends")) {
                    // intent to message activity

                }
            }
        });


    }

    private void setStatus() {
        // ------------------------- Check if are Friends ----------------------
        friendsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild(userid)){
                    STATUS = "friends";
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        // ------------------------- Check if requested ----------------------
        friendsReqRef.child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild(userid)){
                        String req_type = snapshot.child(userid).child("request_type").getValue().toString();
                        if (req_type.equals("sent")){
                            STATUS = "request_sent";
                        } else {
                            STATUS = "request_received";
                        }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }

    @Override
    public void onBackPressed() {
        finish();
    }
}