package ir.archroid.ponyexpress;


import android.text.format.DateFormat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;


public class Util {

    //    Offline Data Store
    private static FirebaseDatabase mDatabase;

    public static FirebaseDatabase enableDatabasePresistance() {
        if (mDatabase == null) {
            mDatabase = FirebaseDatabase.getInstance();
            mDatabase.setPersistenceEnabled(true);
        }
        return mDatabase;
    }


    //    Set User Status
    public static void setStatus(String status) {
        DatabaseReference mdatabaseReference;
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (firebaseUser != null) {
            mdatabaseReference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("status", status);
            hashMap.put("lastSeen", ServerValue.TIMESTAMP);


            mdatabaseReference.updateChildren(hashMap);

            hashMap.clear();
            hashMap.put("status", "offline");
            hashMap.put("lastSeen", ServerValue.TIMESTAMP);


            mdatabaseReference.onDisconnect().updateChildren(hashMap);
        }

    }

    //    Get User Last Seen
    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;

    public static String getTimeAgo(long time) {
        if (time < 1000000000000L) {
            // if timestamp given in seconds, convert to millis
            time *= 1000;
        }
        long now = System.currentTimeMillis();
        if (time > now || time <= 0) {
            return "";
        }
        // TODO: localize
        final long diff = now - time;
        if (diff < MINUTE_MILLIS) {
            return "Last seen just now";
        } else if (diff < 2 * MINUTE_MILLIS) {
            return "Last seen a minute ago";
        } else if (diff < 50 * MINUTE_MILLIS) {
            return diff / MINUTE_MILLIS + " minutes ago";
        } else if (diff < 90 * MINUTE_MILLIS) {
            return "Last seen an hour ago";
        } else if (diff < 24 * HOUR_MILLIS) {
            return diff / HOUR_MILLIS + " hours ago";
        } else if (diff < 48 * HOUR_MILLIS) {
            return "Last seen yesterday";
        } else {
            return diff / DAY_MILLIS + " Days ago";
        }

    }

    //    Covert timeStamp to Normal Time
    public static String getTime(long timestamp) {
        Calendar cal = Calendar.getInstance(Locale.US);
        cal.setTimeInMillis(timestamp);
        String date = DateFormat.format("hh:mm", cal).toString();
        return date;
    }

    //    Covert timeStamp to Normal Date
    public static String getDate(long timestamp) {
        Calendar cal = Calendar.getInstance(Locale.US);
        cal.setTimeInMillis(timestamp);
        String date = DateFormat.format("dd-MM-yyyy", cal).toString();
        return date;
    }


}


