package ir.archroid.ponyexpress.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import ir.archroid.ponyexpress.Model.User;
import ir.archroid.ponyexpress.R;

public class FriendsFragment extends Fragment {

    private RecyclerView recyclerView;

    private FirebaseUser firebaseUser;
    private DatabaseReference usersRef;
    private DatabaseReference friendsRef;

    private List<User> allUsers;
    private List<User> friendUsers;
    private List<String> dates;

    public FriendsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friends, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                new LinearLayoutManager(getContext()).getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);

        allUsers = new ArrayList<>();
        friendUsers = new ArrayList<>();
        dates = new ArrayList<>();

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

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
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String userid = dataSnapshot.getKey();
                    String date = dataSnapshot.getValue().toString();
                    for (User user : allUsers) {
                        assert userid != null;
                        if (userid.equals(user.getId())) {
                            friendUsers.add(user);
                            dates.add(date);
                        }
                    }

                }
                FriendAdapter friendAdapter = new FriendAdapter(friendUsers, getContext(), dates);
                recyclerView.setAdapter(friendAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        return view;
    }
}