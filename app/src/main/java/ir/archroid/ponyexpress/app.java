package ir.archroid.ponyexpress;

import android.app.Application;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.HashMap;

public class app extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        DatabaseReference mdatabaseReference;
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        mdatabaseReference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status", "online");

        mdatabaseReference.updateChildren(hashMap);

        hashMap.clear();
        hashMap.put("status", "offline");

        mdatabaseReference.onDisconnect().updateChildren(hashMap);
    }
}
