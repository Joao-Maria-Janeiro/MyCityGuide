package com.cityguide.joaomjaneiro.cityguide.User;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cityguide.joaomjaneiro.cityguide.MainActivity;
import com.cityguide.joaomjaneiro.cityguide.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import de.hdodenhof.circleimageview.CircleImageView;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener{
    private static final int PICK_IMAGE = 1;
    private Button btnRegister;
    private EditText etEmail, etPassword, etUsername;
    private TextView tvLogin;
    private CircleImageView accountImage;

    private ProgressDialog progressDialog;
    private Uri imageUri;
    private StorageReference mStorage;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference db;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance().getReference();
        //If user already logged in
        if(firebaseAuth.getCurrentUser() != null) {
            finish();
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        }

        imageUri = null;
        mStorage = FirebaseStorage.getInstance().getReference().child("userImages");

        progressDialog = new ProgressDialog(this);

        btnRegister = (Button) findViewById(R.id.btnRegister);
        accountImage = (CircleImageView) findViewById(R.id.register_image_btn);
        etEmail = (EditText) findViewById(R.id.etEmail);
        etPassword = (EditText) findViewById(R.id.etPassword);
        etUsername = (EditText) findViewById(R.id.etUsername);
        tvLogin = (TextView) findViewById(R.id.tvLogin);

        btnRegister.setOnClickListener(this);
        tvLogin.setOnClickListener(this);
        accountImage.setOnClickListener(this);

    }

    private void registerUser() {
        final String email = etEmail.getText().toString().trim();
        final String password = etPassword.getText().toString().trim();
        final String username = etUsername.getText().toString().trim();

        if(TextUtils.isEmpty(email)) {
            //email is empty
            Toast.makeText(this, "Please enter email", Toast.LENGTH_SHORT);
            return;
        }
        if(TextUtils.isEmpty(password)) {
            //pass is empty
            Toast.makeText(this, "Please enter password", Toast.LENGTH_SHORT);
            return;
        }
        if(TextUtils.isEmpty(username)) {
            //username is empty
            Toast.makeText(this, "Please enter username", Toast.LENGTH_SHORT);
            return;
        }

        //If fields are filled
        progressDialog.setMessage("Registering User...");
        progressDialog.show();

        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    //user is sucessfully registered and logged in
                    final String user_id = firebaseAuth.getCurrentUser().getUid();
                    db.child("Users").child(user_id).child("username").setValue(username);
                    db.child("Users").child(user_id).child("points").setValue(0);

                    StorageReference user_profile = mStorage.child(user_id + ".jpg");  //Pode ter de ser .png
                    user_profile.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> uploadTask) {
                            if(uploadTask.isSuccessful()) {
                                String download_url = uploadTask.getResult().getDownloadUrl().toString();
                                db.child("Users").child(user_id).child("image").setValue(download_url);
                            } else {
                                Toast.makeText(RegisterActivity.this, "Error: " + uploadTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                    progressDialog.dismiss();
                    finish();
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                }else {
                    Toast.makeText(RegisterActivity.this, "Could not register. Please try again...", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            }
        });
    }

    private void uploadImage() {

    }

    @Override
    public void onClick(View view) {
        if(view == btnRegister) {
            if(imageUri == null) {
                registerUser();
            }else {
                uploadImage();
                registerUser();
            }
        }else if(view == tvLogin) {
            //opens login activity
            startActivity(new Intent(this, LoginActivity.class));
        }else if(view == accountImage) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_IMAGE) {
            imageUri = data.getData();
            accountImage.setImageURI(imageUri);

        }
    }

}

