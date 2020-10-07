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
import ir.archroid.ponyexpress.Adapter.FriendAdapter;
import ir.archroid.ponyexpress.Adapter.RequestAdapter;
import ir.archroid.ponyexpress.Model.User;
import ir.archroid.ponyexpress.R;

public class FriendsFragment extends Fragment {

    private RecyclerView recyclerView_requests;
    private RecyclerView recyclerView_friends;

    private TextView tv_requests;
    private TextView tv_friends;

    private TextView tv_noItem;

    private FirebaseUser firebaseUser;
    private DatabaseReference usersRef;
    private DatabaseReference friendsRef;
    private DatabaseReference friendsReqRef;

    private List<User> allUsers;
    private List<User> friendUsers;
    private List<User> requestedUsers;
    private List<String> dates;



    public FriendsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friends, container, false);


        tv_noItem = view.findViewById(R.id.tv_noItem);

        tv_friends = view.findViewById(R.id.tv_friends);
        tv_requests = view.findViewById(R.id.tv_requests);


        recyclerView_friends = view.findViewById(R.id.recyclerView_friends);
        recyclerView_friends.setHasFixedSize(true);
        recyclerView_friends.setLayoutManager(new LinearLayoutManager(getContext()));

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView_friends.getContext(),
                new LinearLayoutManager(getContext()).getOrientation());
        recyclerView_friends.addItemDecoration(dividerItemDecoration);

        recyclerView_requests = view.findViewById(R.id.recyclerView_requests);
        recyclerView_requests.setHasFixedSize(true);
        recyclerView_requests.setLayoutManager(new LinearLayoutManager(getContext()));

        DividerItemDecoration dividerItemDecoration2 = new DividerItemDecoration(recyclerView_requests.getContext(),
                new LinearLayoutManager(getContext()).getOrientation());
        recyclerView_friends.addItemDecoration(dividerItemDecoration2);

        allUsers = new ArrayList<>();
        requestedUsers = new ArrayList<>();
        friendUsers = new ArrayList<>();
        dates = new ArrayList<>();

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
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


        friendsRef = FirebaseDatabase.getInstance().getReference("Friends").child(firebaseUser.getUid());
        friendsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                friendUsers.clear();
                dates.clear();
                tv_noItem.setVisibility(View.VISIBLE);
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String userid = dataSnapshot.getKey();
                    String date = dataSnapshot.getValue().toString();
                    for (User user : allUsers) {
                        assert userid != null;
                        if (userid.equals(user.getId())) {
                            friendUsers.add(user);
                            dates.add(date);
                            tv_noItem.setVisibility(View.GONE);
                        }
                    }

                }
                FriendAdapter friendAdapter = new FriendAdapter(friendUsers, getContext(), dates);
                tv_friends.setText("FREINDS - " + dates.size());
                recyclerView_friends.setAdapter(friendAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        friendsReqRef.child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                requestedUsers.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
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
                tv_requests.setText("PENDING REQUESTS - " + requestAdapter.getItemCount());
                recyclerView_requests.setAdapter(requestAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });





        return view;
    }
}