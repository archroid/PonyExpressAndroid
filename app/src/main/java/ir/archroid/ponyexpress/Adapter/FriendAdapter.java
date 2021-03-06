package ir.archroid.ponyexpress.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;
import de.hdodenhof.circleimageview.CircleImageView;
import ir.archroid.ponyexpress.ChatActivity;
import ir.archroid.ponyexpress.Model.User;
import ir.archroid.ponyexpress.ProfileActivity;
import ir.archroid.ponyexpress.R;

public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.ViewHolder> {
    private List<User> users;
    private Context context;
    private List<String> dates;

    public FriendAdapter(List<User> users, Context context, List<String> dates) {
        this.users = users;
        this.context = context;
        this.dates = dates;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_friend, parent, false);

        return new FriendAdapter.ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final User user = users.get(position);
        String date = dates.get(position);

        holder.tv_username.setText(user.getUsername());
        holder.tv_date.setText(date);

        if (!user.getImageURL().equals("default")) {
            Glide.with(context).load(user.getImageURL()).into(holder.iv_profile);
        } else {
            holder.iv_profile.setImageDrawable(context.getResources().getDrawable(R.drawable.avatar_male));
        }

        holder.iv_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context , ProfileActivity.class);
                intent.putExtra("userid" , user.getId());
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context , ChatActivity.class);
                intent.putExtra("userid" , user.getId());
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });

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


    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_username;
        private TextView tv_date;
        private CircleImageView iv_profile;
        private CircleImageView iv_status;
        private ImageView iv_verified;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_username = itemView.findViewById(R.id.tv_username);
            tv_date = itemView.findViewById(R.id.tv_date);
            iv_profile = itemView.findViewById(R.id.iv_profile);
            iv_status = itemView.findViewById(R.id.iv_status);
            iv_verified = itemView.findViewById(R.id.iv_verified);
        }
    }
}
