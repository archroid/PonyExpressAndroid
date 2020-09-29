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
import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private Toolbar toolbar;

    private Button btn_submit;
    private EditText et_email;
    private EditText et_password;

    private ProgressDialog progressDialog;

    private CoordinatorLayout coordinatorLayout;

    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        coordinatorLayout = findViewById(R.id.coordinator);

        firebaseAuth = FirebaseAuth.getInstance();

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

        progressDialog = new ProgressDialog(this);

        et_email = findViewById(R.id.et_email);
        et_password = findViewById(R.id.et_password);

        btn_submit = findViewById(R.id.btn_submit);
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                dismiss keyboard
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                if (imm.isAcceptingText()) {
                    imm.hideSoftInputFromWindow(Objects.requireNonNull(getCurrentFocus()).getWindowToken(), 0);
                }

                String email = et_email.getText().toString().toLowerCase().trim();
                String password = et_password.getText().toString().trim();

                if (checkValid(email, password)) {
                    progressDialog.setTitle("Logging In");
                    progressDialog.setMessage("Please wait until we check your credentials.");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();
                    login(email, password);
                }
            }
        });


    }

    /*
    Check if the information item_user entered is valid or not
     */
    boolean checkValid(String Email, String Password) {
        if (
                Email.isEmpty() ||
                        Email.lastIndexOf('@') <= 0 ||
                        !Email.contains(".") ||
                        Email.lastIndexOf('.') < Email.lastIndexOf('@') ||
                        Email.split("@").length > 2
        ) {
            et_email.setError("Invalid Email Address");
            makeSnackbar.Alert(getApplicationContext(), coordinatorLayout, "Invalid Email!");
            return false;
        }

        if (Password.isEmpty() || Password.length() < 10 || Password.contains(" ") || Password.contains(".")) {
            et_password.setError("Invalid Password");
            return false;
        } else return true;
    }


    /*
Try to set data in the firebase API and check if it was successful
 */
    void login(final String email, String password) {

        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        Check if the Task was Successful
                        if (task.isSuccessful()) {
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            progressDialog.dismiss();
                            startActivity(intent);
                            finish();
                        } else {
                            progressDialog.dismiss();
                            makeSnackbar.Alert(getApplicationContext(), coordinatorLayout, Objects.requireNonNull(task.getException()).getMessage());
                        }
                    }
                });
    }


    @Override
    public void onBackPressed() {
        finish();
    }
}