package ir.archroid.ponyexpress;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
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
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Objects;
import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;


public class SettingsActivity extends AppCompatActivity {

    private Button btn_logout;
    private TextView tv_username;
    private TextView tv_bio;
    private CircleImageView iv_profile;

    private CoordinatorLayout coordinatorLayout;

    private Toolbar toolbar;

    private DatabaseReference userRef;
    private FirebaseUser firebaseUser;
    private StorageReference profileRef;
    private StorageTask uploadtask;

    private String username, imageURL, bio;

    private static final int GALARY_PICK = 1;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Util.enableDatabasePresistance();

        //        modify toolbar
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

        coordinatorLayout = findViewById(R.id.coordinator);

        btn_logout = findViewById(R.id.btn_logout);
        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Util.setStatus("offline");
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(SettingsActivity.this, StartActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });

        tv_username = findViewById(R.id.tv_username);
        tv_bio = findViewById(R.id.tv_bio);
        iv_profile = findViewById(R.id.iv_profile);


        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        userRef = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                username = snapshot.child("username").getValue().toString();
                imageURL = snapshot.child("imageURL").getValue().toString();
                bio = snapshot.child("bio").getValue().toString();

                tv_username.setText(username);
                tv_bio.setText(bio);

                if (!imageURL.equals("default")) {
                    Glide.with(getApplicationContext()).load(imageURL).into(iv_profile);
                } else {
                    iv_profile.setImageResource(R.drawable.avatar_male);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        tv_username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingsActivity.this, ChangeProfileActivity.class);
                intent.putExtra("type", "username");
                intent.putExtra("username", username);
                startActivity(intent);
            }
        });
        tv_bio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingsActivity.this, ChangeProfileActivity.class);
                intent.putExtra("type", "bio");
                intent.putExtra("bio", bio);
                startActivity(intent);
            }
        });

        iv_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "SELECT IMAGE"), GALARY_PICK);


            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALARY_PICK && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();
            CropImage.activity(imageUri)
                    .setAspectRatio(1, 1)
                    .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                progressDialog = new ProgressDialog(this);
                progressDialog.setTitle("Uploading profile image...");
                progressDialog.setMessage("Please wait until we upload your new profile image");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();
                assert result != null;
                Uri resultUri = result.getUri();

                File filepath = new File(resultUri.getPath());

                Bitmap compressedBitmap = new Compressor.Builder(this)
                        .setMaxWidth(1000)
                        .setMaxHeight(1000)
                        .setQuality(100)
                        .setCompressFormat(Bitmap.CompressFormat.JPEG)
                        .setDestinationDirectoryPath(Environment.getExternalStoragePublicDirectory(
                                Environment.DIRECTORY_PICTURES).getAbsolutePath())
                        .build()
                        .compressToBitmap(filepath);


                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                compressedBitmap.compress(Bitmap.CompressFormat.JPEG, 65, baos);
                byte[] mData = baos.toByteArray();

                profileRef = FirebaseStorage.getInstance().getReference().child("profile_images").child(firebaseUser.getUid()+ ".jpg");
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
                            userRef.child("imageURL").setValue(downlaodUrl);
                            makeSnackbar.message(SettingsActivity.this, coordinatorLayout, "Successfully updated!");
                            progressDialog.dismiss();
                        } else {
                            makeSnackbar.Alert(getApplicationContext(), coordinatorLayout, "Upload Failed!");
                            progressDialog.dismiss();
                        }
                    }
                });
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                makeSnackbar.Alert(this, coordinatorLayout, error.getMessage());
                progressDialog.dismiss();
            }
        }

    }


    @Override
    public void onBackPressed() {
        finish();
    }

}