package ir.archroid.ponyexpress.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
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
import ir.archroid.ponyexpress.Model.Message;
import ir.archroid.ponyexpress.R;
import ir.archroid.ponyexpress.Util;


public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {
    private List<Message> messages;
    private Context context;

    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_LEFT_PIC = 2;
    public static final int MSG_TYPE_RIGHT = 1;
    public static final int MSG_TYPE_RIGHT_PIC = 3;

    private FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();


    public MessageAdapter(List<Message> messages, Context context) {
        this.messages = messages;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        view = null;

        switch (viewType) {
            case MSG_TYPE_LEFT:
                view = LayoutInflater.from(context).inflate(R.layout.item_left, parent, false);
                break;
            case MSG_TYPE_RIGHT:
                view = LayoutInflater.from(context).inflate(R.layout.item_right, parent, false);
                break;
            case MSG_TYPE_LEFT_PIC:
                view = LayoutInflater.from(context).inflate(R.layout.item_left_pic, parent, false);
                break;
            case MSG_TYPE_RIGHT_PIC:
                view = LayoutInflater.from(context).inflate(R.layout.item_right_pic, parent, false);
                break;
        }
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Message message = messages.get(position);

        if (message.getType().equals("text")) {
            holder.tv_message.setText(message.getMessage());
        } else {
            holder.tv_message.setVisibility(View.GONE);
            Glide.with(context).load(message.getMessage()).into(holder.iv_content);
        }

        holder.tv_time.setText(Util.getTime(message.getTime()));


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (message.getSender().equals(firebaseUser.getUid())) {
                if (message.getSeen().equals("true")) {
                    holder.iv_seen.setImageResource(R.drawable.ic_seen);
                    holder.iv_seen.setColorFilter(Color.argb(255, 144, 188, 223));
                } else {
                    holder.iv_seen.setImageResource(R.drawable.ic_check);
                    holder.iv_seen.setColorFilter(Color.argb(255, 144, 188, 223));
                }
            }
        }


    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tv_message;
        private TextView tv_time;
        private ImageView iv_seen;
        private ImageView iv_content;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_message = itemView.findViewById(R.id.tv_message);
            tv_time = itemView.findViewById(R.id.tv_time);
            iv_seen = itemView.findViewById(R.id.iv_seen);
            iv_content = itemView.findViewById(R.id.iv_content);


        }
    }

    @Override
    public int getItemViewType(int position) {
        if (messages.get(position).getSender().equals(firebaseUser.getUid())) {
            if (messages.get(position).getType().equals("text")) {
                return MSG_TYPE_RIGHT;
            } else {
                return MSG_TYPE_RIGHT_PIC;
            }
        } else {
            if (messages.get(position).getType().equals("text")) {
                return MSG_TYPE_LEFT;
            } else {
                return MSG_TYPE_LEFT_PIC;
            }
        }
    }
}
