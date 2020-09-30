package ir.archroid.ponyexpress;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;
import ir.archroid.ponyexpress.Adapter.MessageAdapter;
import ir.archroid.ponyexpress.Model.Message;
import ir.archroid.ponyexpress.Model.User;


public class ChatActivity extends AppCompatActivity {

    private String userId;

    private Toolbar toolbar;
    private CircleImageView iv_profile;
    private TextView tv_username;
    private EditText et_message;
    private RelativeLayout btn_submit;
    private RelativeLayout btn_addpic;
    private TextView tv_status;

    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;

    private FirebaseUser firebaseUser;
    private DatabaseReference userRef;
    private DatabaseReference messagesRef;
    private DatabaseReference databaseReference;

    private ValueEventListener listener;
    private ValueEventListener listenerr;

    private List<Message> messages = new ArrayList<>();

    private MessageAdapter messageAdapter;
    private NewMessages newMessages;

    private static final int GALARY_PICK = 1;
    private ProgressDialog progressDialog;
    private StorageReference profileRef;
    private StorageTask uploadtask;

    String userTyping;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        userId = getIntent().getStringExtra("userid");
        Util.enableDatabasePresistance();

        //Modify Toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        // Modify RecyclerView
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        messageAdapter = new MessageAdapter(messages, getApplicationContext());
        recyclerView.setAdapter(messageAdapter);

        iv_profile = findViewById(R.id.iv_profile);
        tv_username = findViewById(R.id.tv_username);
        et_message = findViewById(R.id.et_message);
        tv_status = findViewById(R.id.tv_status);
        btn_submit = findViewById(R.id.btn_submit);
        btn_addpic = findViewById(R.id.btn_addpic);


        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        userRef = FirebaseDatabase.getInstance().getReference("Users").child(userId);


        loadMessages();

        // Read User Data
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                final User user = snapshot.getValue(User.class);

                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Chat")
                        .child(userId).child(firebaseUser.getUid());

                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        try {
                            userTyping = snapshot.child("typing").getValue().toString();
                        } catch (Exception e) {
                            userTyping = "false";
                        }

                        if (user.getStatus().equals("online")) {
                            if (userTyping.equals("true")) {
                                tv_status.setText("Typing...");
                            } else {
                                tv_status.setText("Online");
                            }
                        } else {
                            String lastSeenn = Util.getTimeAgo(user.getLastSeen());
                            if (!lastSeenn.isEmpty()) {
                                tv_status.setVisibility(View.VISIBLE);
                                tv_status.setText(lastSeenn);
                            } else {
                                tv_status.setVisibility(View.GONE);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                tv_username.setText(user.getUsername());
                if (!user.getImageURL().equals("default")) {
                    Glide.with(getApplicationContext()).load(user.getImageURL()).into(iv_profile);
                } else {
                    iv_profile.setImageDrawable(getResources().getDrawable(R.drawable.profile_bg));
                }


            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        loadMessages();
        addUserToChat();

        iv_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChatActivity.this, ProfileActivity.class);
                intent.putExtra("userid", userId);
                startActivity(intent);
            }
        });

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });

        btn_addpic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendPic();
            }
        });


        et_message.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                Map hashMap = new HashMap();
                hashMap.put("typing", "true");
                FirebaseDatabase.getInstance().getReference("Chat")
                        .child(firebaseUser.getUid()).child(userId).updateChildren(hashMap);

                String message = et_message.getText().toString().trim();
                if (message.isEmpty()) {
                    btn_submit.setVisibility(View.GONE);
                } else {
                    btn_submit.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String message = et_message.getText().toString().trim();
                if (message.isEmpty()) {
                    Map hashMap = new HashMap();
                    hashMap.put("typing", "false");
                    FirebaseDatabase.getInstance().getReference("Chat")
                            .child(firebaseUser.getUid()).child(userId).updateChildren(hashMap);
                }
            }
        });

        seenMessages(userId);


    }


    // Add user to Chat for the first time
    private void addUserToChat() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Chat");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.child(firebaseUser.getUid()).hasChild(userId)) {
                    Map chatAddMap = new HashMap();
                    chatAddMap.put("lastSender", firebaseUser.getUid());
                    chatAddMap.put("lastMessage", "");
                    chatAddMap.put("lastSeen", "false");
                    chatAddMap.put("lastTime", ServerValue.TIMESTAMP);
                    chatAddMap.put("typing", "false");
                    chatAddMap.put("notSeenMSG", 0);

                    Map chatAddMap2 = new HashMap();
                    chatAddMap2.put("lastSender", firebaseUser.getUid());
                    chatAddMap2.put("lastMessage", "");
                    chatAddMap2.put("lastSeen", "false");
                    chatAddMap2.put("lastTime", ServerValue.TIMESTAMP);
                    chatAddMap2.put("typing", "false");
                    chatAddMap2.put("notSeenMSG", 0);

                    Map messageKeysMap = new HashMap();
                    messageKeysMap.put("Chat/" + firebaseUser.getUid() + "/" + userId, chatAddMap);
                    messageKeysMap.put("Chat/" + userId + "/" + firebaseUser.getUid(), chatAddMap2);

                    DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
                    rootRef.updateChildren(messageKeysMap);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    /// Read Messages
    private void loadMessages() {
        messagesRef = FirebaseDatabase.getInstance().getReference("Messages").child(firebaseUser.getUid()).child(userId);
        listenerr = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messages.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Message message = dataSnapshot.getValue(Message.class);
                    messages.add(message);
                }

                messageAdapter.notifyDataSetChanged();

                Toast.makeText(ChatActivity.this, " " + linearLayoutManager.findFirstVisibleItemPosition(), Toast.LENGTH_SHORT).show();
                if (linearLayoutManager.findFirstVisibleItemPosition() + 14 >= recyclerView.getAdapter().getItemCount()){
                    recyclerView.scrollToPosition(recyclerView.getAdapter().getItemCount() - 1);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        };
        messagesRef.addValueEventListener(listenerr);
    }

    //Seen Message
    private void seenMessages(final String userId) {
        databaseReference = FirebaseDatabase.getInstance().getReference("Messages");
        listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Message message = dataSnapshot.getValue(Message.class);

                    if (message.getSeen().equals("false")) {
                        if (message.getSender().equals(userId)) {

                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("seen", "true");

                            Map messageKeysMap = new HashMap();
                            messageKeysMap.put("Chat/" + firebaseUser.getUid() + "/" + userId + "/lastSeen", "true");
                            messageKeysMap.put("Chat/" + userId + "/" + firebaseUser.getUid() + "/lastSeen", "true");
                            messageKeysMap.put("Chat/" + firebaseUser.getUid() + "/" + userId + "/notSeenMSG", 0);

                            FirebaseDatabase.getInstance().getReference().updateChildren(messageKeysMap);
                            dataSnapshot.getRef().updateChildren(hashMap);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        };
        databaseReference.child(firebaseUser.getUid()).child(userId).addValueEventListener(listener);
        databaseReference.child(userId).child(firebaseUser.getUid()).addValueEventListener(listener);


    }

    /// Send Message
    private void sendMessage() {
        final String message = et_message.getText().toString().trim();
        if (!message.isEmpty()) {
            final String thisUserRef = "Messages/" + firebaseUser.getUid() + "/" + userId;
            final String targetUserRef = "Messages/" + userId + "/" + firebaseUser.getUid();

            et_message.setText("");

            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Messages").child(firebaseUser.getUid())
                    .child(userId).push();
            final String push_id = databaseReference.getKey();

            final Map messageDataMap = new HashMap();
            messageDataMap.put("message", message);
            messageDataMap.put("seen", "false");
            messageDataMap.put("type", "text");
            messageDataMap.put("sender", firebaseUser.getUid());
            messageDataMap.put("time", ServerValue.TIMESTAMP);

            final HashMap<String, Object> chatAddMap = new HashMap();
            chatAddMap.put("lastSender", firebaseUser.getUid());
            chatAddMap.put("lastMessage", message);
            chatAddMap.put("lastSeen", "false");
            chatAddMap.put("lastTime", ServerValue.TIMESTAMP);


            DatabaseReference targetUserNewMSGRef = FirebaseDatabase.getInstance().getReference("Chat").child(userId)
                    .child(firebaseUser.getUid());
            targetUserNewMSGRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    int targetUserNewMSG;
                    try {
                        targetUserNewMSG = Integer.parseInt(snapshot.child("notSeenMSG").getValue().toString());
                    } catch (Exception e) {
                        targetUserNewMSG = 0;
                    }

                    HashMap<String, Object> chatAddMap2 = new HashMap();
                    chatAddMap2.put("lastSender", firebaseUser.getUid());
                    chatAddMap2.put("lastMessage", message);
                    chatAddMap2.put("lastSeen", "false");
                    chatAddMap2.put("lastTime", ServerValue.TIMESTAMP);
                    chatAddMap2.put("notSeenMSG", targetUserNewMSG + 1);

                    HashMap<String, Object> messageKeysMap = new HashMap();
                    messageKeysMap.put(thisUserRef + "/" + push_id, messageDataMap);
                    messageKeysMap.put(targetUserRef + "/" + push_id, messageDataMap);
                    messageKeysMap.put("Chat/" + firebaseUser.getUid() + "/" + userId, chatAddMap);
                    messageKeysMap.put("Chat/" + userId + "/" + firebaseUser.getUid(), chatAddMap2);

                    DatabaseReference RootRef = FirebaseDatabase.getInstance().getReference();
                    RootRef.updateChildren(messageKeysMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                            recyclerView.scrollToPosition(recyclerView.getAdapter().getItemCount() - 1);
                        }
                    });

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


        }
    }

    // Send Picture
    private void sendPic() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "SELECT IMAGE"), GALARY_PICK);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == GALARY_PICK && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();
            CropImage.activity(imageUri)
                    .start(this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                progressDialog = new ProgressDialog(this);
                progressDialog.setTitle("Uploading image...");
                progressDialog.setMessage("Please wait.");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();
                assert result != null;
                Uri resultUri = result.getUri();

                File filepath = new File(resultUri.getPath());

                Bitmap compressedBitmap = new Compressor.Builder(this)
                        .setMaxWidth(1200)
                        .setMaxHeight(1200)
                        .setQuality(100)
                        .setCompressFormat(Bitmap.CompressFormat.JPEG)
                        .setDestinationDirectoryPath(Environment.getExternalStoragePublicDirectory(
                                Environment.DIRECTORY_PICTURES).getAbsolutePath())
                        .build()
                        .compressToBitmap(filepath);


                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                compressedBitmap.compress(Bitmap.CompressFormat.JPEG, 65, baos);
                byte[] mData = baos.toByteArray();

                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Messages").child(firebaseUser.getUid())
                        .child(userId).push();
                String push_id = databaseReference.getKey();
                profileRef = FirebaseStorage.getInstance().getReference().child("Messages_Pic").child(push_id + ".jpg");
                uploadtask = profileRef.putBytes(mData);
                uploadtask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw Objects.requireNonNull(task.getException());
                        }
                        return profileRef.getDownloadUrl();

                    }
                }).addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            String downlaodUrl = task.getResult().toString();

                            final String thisUserRef = "Messages/" + firebaseUser.getUid() + "/" + userId;
                            final String targetUserRef = "Messages/" + userId + "/" + firebaseUser.getUid();

                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Messages").child(firebaseUser.getUid())
                                    .child(userId).push();
                            final String push_id = databaseReference.getKey();

                            final Map messageDataMap = new HashMap();
                            messageDataMap.put("message", downlaodUrl);
                            messageDataMap.put("seen", "false");
                            messageDataMap.put("type", "pic");
                            messageDataMap.put("sender", firebaseUser.getUid());
                            messageDataMap.put("time", ServerValue.TIMESTAMP);


                            final HashMap<String, Object> chatAddMap = new HashMap();
                            chatAddMap.put("lastSender", firebaseUser.getUid());
                            chatAddMap.put("lastMessage", "Uploaded Photo");
                            chatAddMap.put("lastSeen", "false");
                            chatAddMap.put("lastTime", ServerValue.TIMESTAMP);

                            DatabaseReference targetUserNewMSGRef = FirebaseDatabase.getInstance().getReference("Chat").child(userId)
                                    .child(firebaseUser.getUid());
                            targetUserNewMSGRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    int targetUserNewMSG;
                                    try {
                                        targetUserNewMSG = Integer.parseInt(snapshot.child("notSeenMSG").getValue().toString());
                                    } catch (Exception e) {
                                        targetUserNewMSG = 0;
                                    }

                                    HashMap<String, Object> chatAddMap2 = new HashMap();
                                    chatAddMap2.put("lastSender", firebaseUser.getUid());
                                    chatAddMap2.put("lastMessage", "Uploaded Photo");
                                    chatAddMap2.put("lastSeen", "false");
                                    chatAddMap2.put("lastTime", ServerValue.TIMESTAMP);
                                    chatAddMap2.put("notSeenMSG", targetUserNewMSG + 1);

                                    HashMap<String, Object> messageKeysMap = new HashMap();
                                    messageKeysMap.put(thisUserRef + "/" + push_id, messageDataMap);
                                    messageKeysMap.put(targetUserRef + "/" + push_id, messageDataMap);
                                    messageKeysMap.put("Chat/" + firebaseUser.getUid() + "/" + userId, chatAddMap);
                                    messageKeysMap.put("Chat/" + userId + "/" + firebaseUser.getUid(), chatAddMap2);

                                    DatabaseReference RootRef = FirebaseDatabase.getInstance().getReference();
                                    RootRef.updateChildren(messageKeysMap);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                            Toast.makeText(ChatActivity.this, "Successfully updated!", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();

                            recyclerView.scrollToPosition(recyclerView.getAdapter().getItemCount() - 1);

                        } else {
                            Toast.makeText(ChatActivity.this, "Upload Failed!", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    }
                });
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(ChatActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();


            }
        }

    }

    @Override
    public void onBackPressed() {
        messagesRef.removeEventListener(listenerr);
        databaseReference.child(firebaseUser.getUid()).child(userId).removeEventListener(listener);
        databaseReference.child(userId).child(firebaseUser.getUid()).removeEventListener(listener);
        finish();
    }

    @Override
    protected void onDestroy() {
        messagesRef.removeEventListener(listenerr);
        databaseReference.child(firebaseUser.getUid()).child(userId).removeEventListener(listener);
        databaseReference.child(userId).child(firebaseUser.getUid()).removeEventListener(listener);
        super.onDestroy();
    }
}