package ir.archroid.ponyexpress;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.HashMap;
import java.util.Objects;

public class ChangeProfileActivity extends AppCompatActivity {

    private Toolbar toolbar;

    private EditText editText;
    private TextView tv_desc;

    private String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_profile);

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

        tv_desc = findViewById(R.id.tv_desc);
        editText = findViewById(R.id.editText);
        editText.requestFocus();

        type = getIntent().getStringExtra("type");

        if (type.equals("username")) {
            toolbar.setTitle("Change Username");
            tv_desc.setText(getResources().getString(R.string.ChangeUsername));
            editText.setSingleLine(true);
            editText.setMaxEms(10);
            editText.setHint("Username");
            editText.setText(getIntent().getStringExtra("username"));
        } else {
            toolbar.setTitle("Change Bio");
            tv_desc.setText(getResources().getString(R.string.ChangeBio));
            editText.setMaxLines(5);
            editText.setMaxEms(30);
            editText.setHint("Bio");
            editText.setText(getIntent().getStringExtra("bio"));
        }

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                toolbar.getMenu().getItem(0).setVisible(true);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.changeprofile_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);

        if (item.getItemId() == R.id.btn_submit) {
            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
            HashMap<String, Object> hashMap = new HashMap<>();
            switch (type) {
                case "username":
                    hashMap.put("username", editText.getText().toString());
                    break;
                case "bio":
                    hashMap.put("bio", editText.getText().toString());
                    break;
            }
            databaseReference.updateChildren(hashMap);
            onBackPressed();
        }

        return true;
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}