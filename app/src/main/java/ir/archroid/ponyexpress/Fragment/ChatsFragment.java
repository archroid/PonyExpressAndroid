package ir.archroid.ponyexpress.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
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
import ir.archroid.ponyexpress.Adapter.ChatAdapter;
import ir.archroid.ponyexpress.Model.Chat;
import ir.archroid.ponyexpress.Model.User;
import ir.archroid.ponyexpress.R;

public class ChatsFragment extends Fragment {

    private RecyclerView recyclerView;
    private TextView tv_noItem;

    private FirebaseUser firebaseUser;
    private DatabaseReference usersRef;
    private DatabaseReference chatsRef;

    private List<User> allUsers = new ArrayList<>();
    private List<User> chatUsers = new ArrayList<>();
    private List<String> newMessages = new ArrayList<>();
    private List<Chat> data = new ArrayList<>();


    public ChatsFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chats, container, false);

        tv_noItem = view.findViewById(R.id.tv_noItem);

        recyclerView = view.findViewById(R.id.recyclerView_friends);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        usersRef = FirebaseDatabase.getInstance().getReference("Users");
        chatsRef = FirebaseDatabase.getInstance().getReference("Chat").child(firebaseUser.getUid());

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


        chatsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatUsers.clear();
                newMessages.clear();
                data.clear();
                tv_noItem.setVisibility(View.VISIBLE);
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String userid = dataSnapshot.getKey();
                    for (User user : allUsers) {
                        if (user.getId().equals(userid)) {
                            chatUsers.add(user);
                            Chat chat = new Chat();
                            chat.setLastMessage(dataSnapshot.child("lastMessage").getValue().toString());
                            chat.setLastSeen(dataSnapshot.child("lastSeen").getValue().toString());
                            chat.setLastTime(Long.parseLong(dataSnapshot.child("lastTime").getValue().toString()));
                            chat.setLastSender(dataSnapshot.child("lastSender").getValue().toString());
                            data.add(chat);

                            newMessages.add(String.valueOf(dataSnapshot.child("notSeenMSG").getValue()));

                            tv_noItem.setVisibility(View.GONE);

                        }
                    }
                }

                ChatAdapter chatAdapter = new ChatAdapter(chatUsers, data, newMessages, getContext());
                recyclerView.setAdapter(chatAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });


        return view;
    }
}