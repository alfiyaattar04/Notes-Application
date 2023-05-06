package easy.tuto.notespro;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CreateAccountActivity extends AppCompatActivity {
    FirebaseDatabase database;
    DatabaseReference reference;
    private FirebaseAuth auth;
    EditText emailEditText, passwordEditText, confirmPasswordEditText;
    Button createAccountBtn;
    ProgressBar progressBar;
    TextView loginBtnTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        emailEditText = findViewById(R.id.email_edit_text);
        passwordEditText = findViewById(R.id.password_edit_text);
        confirmPasswordEditText = findViewById(R.id.confirm_password_edit_text);
        createAccountBtn = findViewById(R.id.create_account_btn);
        progressBar = findViewById(R.id.progress_bar);
        loginBtnTextView = findViewById(R.id.login_text_view_btn);

        createAccountBtn.setOnClickListener(v -> createAccount());
        loginBtnTextView.setOnClickListener(v -> finish());


    }

    void createAccount() {
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        String confirmPassword = confirmPasswordEditText.getText().toString();

        boolean isValidated = validateData(email, password, confirmPassword);
        if (!isValidated) {
            return;
        }
        createAccountInFirebase(email, password);
    }

    public boolean validateData(String email, String password, String confirmPassword) {

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Email is invalid");
            return false;
        }
        if (password.length() < 6) {
            passwordEditText.setError("Password length is invalid");
            return false;
        }
        if (!password.equals(confirmPassword)) {
            confirmPasswordEditText.setError("Password not matched");
            return false;
        }
        return true;
    }

    void changeInProgress(boolean inProgress) {
        if (inProgress) {
            progressBar.setVisibility(View.VISIBLE);
            createAccountBtn.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.GONE);
            createAccountBtn.setVisibility(View.VISIBLE);
        }
    }

    void createAccountInFirebase(String email, String password) {
        changeInProgress(true);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
//        firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(CreateAccountActivity.this,
//                new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        changeInProgress(false);
//                        if(task.isSuccessful()){
//                            //creating acc is done
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {

                    database = FirebaseDatabase.getInstance();
                    reference = database.getReference("Users");

                    String emailRTDB = emailEditText.getText().toString().trim();
                    String passwordRTDB = passwordEditText.getText().toString().trim();
                    String uidRTDB = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    HelperRTDB helperReference = new HelperRTDB(emailRTDB, passwordRTDB, uidRTDB);
                    reference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(helperReference);

                    Toast.makeText(CreateAccountActivity.this, "SignUp Successful", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(CreateAccountActivity.this, MainActivity.class));
                } else {
                    Toast.makeText(CreateAccountActivity.this, "SignUp Failed" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
//                        else{
//                            //failure
//                            Utility.showToast(CreateAccountActivity.this,task.getException().getLocalizedMessage());
//                        }
    }
}
