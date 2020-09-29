package ir.archroid.ponyexpress;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import java.util.HashMap;
import java.util.Objects;
import java.util.Random;


public class RegisterActivity extends AppCompatActivity {

    private EditText et_username, et_email, et_password, et_confirm;
    private Button btn_submit;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private DatabaseReference usersRef;
    private Toolbar toolbar;
    private CoordinatorLayout coordinator;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_register);
        et_username = findViewById(R.id.et_username);
        et_email = findViewById(R.id.et_email);
        et_password = findViewById(R.id.et_password);
        et_confirm = findViewById(R.id.et_confirm);

        btn_submit = findViewById(R.id.btn_submit);

        coordinator = findViewById(R.id.coordinator);

        progressDialog = new ProgressDialog(this);

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


        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                dismiss keyboard
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                if (imm.isAcceptingText()) {
                    imm.hideSoftInputFromWindow(Objects.requireNonNull(getCurrentFocus()).getWindowToken(), 0);
                }

//                Get Data
                String username = et_username.getText().toString().trim();
                String email = et_email.getText().toString().toLowerCase().trim();
                String password = et_password.getText().toString().trim();
                String confirm = et_confirm.getText().toString().trim();

                if (checkValid(username, password, email, confirm)) {
                    progressDialog.setTitle("Registering User");
                    progressDialog.setMessage("Creating your new account.");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();
                    register(username, password, email);
                }

            }
        });

    }

    /*
    Check if the information item_user entered is valid or not
     */
    private boolean checkValid(String Username, String Password, String Email, String Confirm) {

//        Check username
        if (Username.isEmpty() || Username.length() < 4) {
            et_username.setError("Invalid Username");
            makeSnackbar.Alert(getApplicationContext(), coordinator, "Invalid Username!");
            return false;
        }

//        Check Email
        if (
                Email.isEmpty() ||
                        Email.lastIndexOf('@') <= 0 ||
                        !Email.contains(".") ||
                        Email.contains(" ") ||
                        Email.lastIndexOf('.') < Email.lastIndexOf('@') ||
                        Email.split("@").length > 2
        ) {
            et_email.setError("Invalid Email");
            makeSnackbar.Alert(getApplicationContext(), coordinator, "Invalid Email!");
            return false;
        }

//        Check Password
        if (Password.isEmpty() || Password.length() < 10 || Password.contains(" ") || Password.contains(".")) {
            et_password.setError("Invalid password");
            makeSnackbar.Alert(getApplicationContext(), coordinator, "Invalid password!");
            return false;
        }

//        Check if username == password
        if (Username.equals(Password)) {
            makeSnackbar.Alert(getApplicationContext(), coordinator, "Username and password can't be equal!");
            return false;
        }
//        Check if passowrd == confirm
        if (!Password.equals(Confirm)) {
            et_confirm.setError("Invalid");
            makeSnackbar.Alert(getApplicationContext(), coordinator, "Password and confirm are not equal!");
            return false;

        }
//        if no Error
        else {
            return true;
        }
    }


    /*
    Try to set data in the firebase API and check if it was successful
     */
    private void register(final String username, final String password, final String email) {

        firebaseAuth = FirebaseAuth.getInstance();

        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            firebaseUser = firebaseAuth.getCurrentUser();
                            String userId = firebaseUser.getUid();
                            usersRef = FirebaseDatabase.getInstance().getReference("Users").child(userId);

                            HashMap<String, Object> hashMap = new HashMap<>();
                            Random random = new Random();

                            hashMap.put("id", userId);
                            hashMap.put("imageURL", "default");
                            hashMap.put("email", email);
                            hashMap.put("username", username);
                            hashMap.put("bio", getResources().getString(R.string.Default_bio));
                            hashMap.put("verified", "no");
                            hashMap.put("status", "online");
                            hashMap.put("lastSeen" , ServerValue.TIMESTAMP);

                            usersRef.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        firebaseAuth.signInWithEmailAndPassword(email, password);
                                        progressDialog.dismiss();
                                        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                        progressDialog.dismiss();
                                        startActivity(intent);
                                        finish();
                                    }
                                }
                            });
                        } else {
                            progressDialog.dismiss();
                            makeSnackbar.Alert(getApplicationContext(), coordinator, task.getException().getMessage());
                        }
                    }
                });
    }


}