package ir.archroid.ponyexpress.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import de.hdodenhof.circleimageview.CircleImageView;
import ir.archroid.ponyexpress.Model.User;
import ir.archroid.ponyexpress.ProfileActivity;
import ir.archroid.ponyexpress.R;

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.ViewHolder> {
    private List<User> users;
    private Context context;

    private FirebaseUser firebaseUser;
    private DatabaseReference friendsRef;
    private DatabaseReference friendsReqRef;

    public RequestAdapter(List<User> users, Context context) {
        this.users = users;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_request, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        final User user = users.get(position);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        friendsReqRef = FirebaseDatabase.getInstance().getReference("FriendRequests");
        friendsRef = FirebaseDatabase.getInstance().getReference("Friends");

        holder.tv_username.setText(user.getUsername());
        holder.tv_bio.setText(user.getBio());

        if (!user.getImageURL().equals("default")) {
            Glide.with(context).load(user.getImageURL()).into(holder.iv_profile);
        } else {
            holder.iv_profile.setImageDrawable(context.getResources().getDrawable(R.drawable.avatar_male));
        }

        if (user.getStatus().equals("online")){
            holder.iv_status.setVisibility(View.VISIBLE);
        } else {
            holder.iv_status.setVisibility(View.GONE);
        }

        if (user.getVerified().equals("yes")){
            holder.iv_verified.setVisibility(View.VISIBLE);
        } else {
            holder.iv_verified.setVisibility(View.GONE);
        }

        holder.btn_accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String currentDate = DateFormat.getDateTimeInstance().format(new Date());

                friendsReqRef.child(firebaseUser.getUid()).child(user.getId()).removeValue();
                friendsReqRef.child(user.getId()).child(firebaseUser.getUid()).removeValue();

                friendsRef.child(firebaseUser.getUid()).child(user.getId()).setValue(currentDate);
                friendsRef.child(user.getId()).child(firebaseUser.getUid()).setValue(currentDate).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(context, "Friend request accepted", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        holder.btn_decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                friendsReqRef.child(firebaseUser.getUid()).child(user.getId()).removeValue();
                friendsReqRef.child(user.getId()).child(firebaseUser.getUid()).removeValue();
                Toast.makeText(context, "Request rejected!", Toast.LENGTH_SHORT).show();
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context , ProfileActivity.class);
                intent.putExtra("userid" , user.getId());
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });



    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_username;
        private TextView tv_bio;
        private CircleImageView iv_profile;
        private CircleImageView iv_status;
        private ImageView iv_verified;

        private ImageView btn_accept;
        private ImageView btn_decline;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_username = itemView.findViewById(R.id.tv_username);
            tv_bio = itemView.findViewById(R.id.tv_bio);
            iv_profile = itemView.findViewById(R.id.iv_profile);
            iv_status = itemView.findViewById(R.id.iv_status);
            iv_verified = itemView.findViewById(R.id.iv_verified);

            btn_accept = itemView.findViewById(R.id.btn_accept);
            btn_decline = itemView.findViewById(R.id.btn_decline);

        }
    }
}
