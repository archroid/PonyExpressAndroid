package ir.archroid.ponyexpress.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import java.util.List;
import de.hdodenhof.circleimageview.CircleImageView;
import ir.archroid.ponyexpress.ChatActivity;
import ir.archroid.ponyexpress.Model.Chat;
import ir.archroid.ponyexpress.Model.User;
import ir.archroid.ponyexpress.ProfileActivity;
import ir.archroid.ponyexpress.R;
import ir.archroid.ponyexpress.Util;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {
    private List<User> users;
    private List<Chat> data;
    private List<String> newMessages;
    private Context context;

    public ChatAdapter(List<User> users, List<Chat> data, List<String> newMessages, Context context) {
        this.users = users;
        this.data = data;
        this.newMessages = newMessages;
        this.context = context;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_chat, parent, false);

        return new ChatAdapter.ViewHolder(view);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final User user = users.get(position);

        String newMessage = newMessages.get(position);

        Chat chat = data.get(position);

        holder.tv_username.setText(user.getUsername());
        holder.tv_lastMessage.setText(chat.getLastMessage());
        holder.tv_time.setText(Util.getTime(chat.getLastTime()));

        if (!user.getImageURL().equals("default")) {
            Glide.with(context).load(user.getImageURL()).into(holder.iv_profile);
        } else {
            holder.iv_profile.setImageDrawable(context.getResources().getDrawable(R.drawable.avatar_male));
        }

        holder.iv_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ProfileActivity.class);
                intent.putExtra("userid", user.getId());
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("userid", user.getId());
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });

        if (user.getStatus().equals("online")) {
            holder.iv_status.setVisibility(View.VISIBLE);
        } else {
            holder.iv_status.setVisibility(View.GONE);
        }

        if (user.getVerified().equals("yes")) {
            holder.iv_verified.setVisibility(View.VISIBLE);
        } else {
            holder.iv_verified.setVisibility(View.GONE);
        }

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (chat.getLastSender().equals(firebaseUser.getUid())) {

            holder.iv_seen.setVisibility(View.VISIBLE);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (chat.getLastSeen().equals("true")) {
                    holder.iv_seen.setImageResource(R.drawable.ic_seen);
                    holder.iv_seen.setColorFilter(Color.argb(255, 144, 188, 223));
                } else {
                    holder.iv_seen.setImageResource(R.drawable.ic_check);
                    holder.iv_seen.setColorFilter(Color.argb(255, 144, 188, 223));
                }
            }
        }



        holder.tv_newMSG.setVisibility(View.GONE);
        if (!newMessage.equals("null")){
            if (!newMessage.equals("0")){
                holder.tv_newMSG.setText(newMessage);
                holder.tv_newMSG.setVisibility(View.VISIBLE);
            }
        }

    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_username;
        private TextView tv_lastMessage;
        private CircleImageView iv_profile;
        private CircleImageView iv_status;
        private ImageView iv_verified;
        private TextView tv_time;
        private ImageView iv_seen;
        private TextView tv_newMSG;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_username = itemView.findViewById(R.id.tv_username);
            tv_lastMessage = itemView.findViewById(R.id.tv_lastMessage);
            iv_profile = itemView.findViewById(R.id.iv_profile);
            iv_status = itemView.findViewById(R.id.iv_status);
            iv_verified = itemView.findViewById(R.id.iv_verified);
            tv_time = itemView.findViewById(R.id.tv_time);
            iv_seen = itemView.findViewById(R.id.iv_seen);
            tv_newMSG = itemView.findViewById(R.id.tv_newMSG);
        }
    }
}
