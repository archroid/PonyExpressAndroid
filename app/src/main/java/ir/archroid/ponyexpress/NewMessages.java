package ir.archroid.ponyexpress;

import androidx.annotation.NonNull;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class NewMessages {

    private int firebaseUserNewMSG = 0;
    private int targetUserNewMSG = 0;
    private String thisUserTyping = "false";
    private String targetUserTyping = "false";

    public NewMessages(String userId) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference firebaseUserNewMSGRef = FirebaseDatabase.getInstance().getReference("Chat").child(firebaseUser.getUid())
                .child(userId);

        firebaseUserNewMSGRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                try {
                    firebaseUserNewMSG = Integer.parseInt(snapshot.child("notSeenMSG").getValue().toString());
                    thisUserTyping = snapshot.child("typing").getValue().toString();
                } catch (Exception e){
                    firebaseUserNewMSG = 0;
                    thisUserTyping = "false";
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        final DatabaseReference targetUserNewMSGRef = FirebaseDatabase.getInstance().getReference("Chat").child(userId)
                .child(firebaseUser.getUid());

        targetUserNewMSGRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    targetUserNewMSG = Integer.parseInt(snapshot.child("notSeenMSG").getValue().toString());
                    targetUserTyping = snapshot.child("typing").getValue().toString();

                } catch (Exception e){
                    targetUserNewMSG = 0;
                    targetUserTyping = "false";
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    public int getFirebaseUserNewMSG() {
        return firebaseUserNewMSG;
    }

    public int getTargetUserNewMSG() {
        return targetUserNewMSG;
    }

    public String getThisUserTyping() {
        return thisUserTyping;
    }

    public String getTargetUserTyping() {
        return targetUserTyping;
    }
}
