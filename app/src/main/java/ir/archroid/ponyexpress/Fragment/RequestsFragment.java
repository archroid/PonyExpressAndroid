package ir.archroid.ponyexpress.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;
import ir.archroid.ponyexpress.Adapter.RequestAdapter;
import ir.archroid.ponyexpress.Model.User;
import ir.archroid.ponyexpress.R;

public class RequestsFragment extends Fragment {

    private FirebaseUser firebaseUser;
    private DatabaseReference usersRef;
    private DatabaseReference friendsReqRef;
    private DatabaseReference friendsRef;

    private RecyclerView recyclerView;
    private TextView tv_noItem;

    private List<User> allUsers;
    private List<User> requestedUsers;

    public RequestsFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_requests, container, false);

        tv_noItem = view.findViewById(R.id.tv_noItem);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                new LinearLayoutManager(getContext()).getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);

        allUsers = new ArrayList<>();
        requestedUsers = new ArrayList<>();

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        friendsRef = FirebaseDatabase.getInstance().getReference("Friends").child(firebaseUser.getUid());
        friendsReqRef = FirebaseDatabase.getInstance().getReference("FriendRequests");
        usersRef = FirebaseDatabase.getInstance().getReference("Users");

        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                allUsers.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    User user = dataSnapshot.getValue(User.class);
                    allUsers.add(user);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });


        friendsReqRef.child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                requestedUsers.clear();
                tv_noItem.setVisibility(View.VISIBLE);
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    final String userid = dataSnapshot.getKey();
                    String req_type = dataSnapshot.child("request_type").getValue().toString();
                    if (req_type.equals("received")) {
                        for (User user : allUsers) {
                            if (user.getId().equals(userid)) {
                                requestedUsers.add(user);
                                tv_noItem.setVisibility(View.GONE);
                            }
                        }
                    }

                }
                RequestAdapter requestAdapter = new RequestAdapter(requestedUsers, getContext());
                recyclerView.setAdapter(requestAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        return view;
    }
}