package com.example.cpyipee;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MentorRegActivity extends AppCompatActivity {

    private String fullNam, userNam, pass, conPass, Email, t = "t";
    private ImageView profileImg;
    private EditText userName, fullName, email, password, confirmPass;
    private Button register;
    private TextView wannaLogin;
    private ProgressDialog loader;
    private Uri resultUri;
    private FirebaseAuth mAuth;
    private DatabaseReference reference;
    private String userId;
    private String downUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mentor_reg);

        profileImg = (ImageView) findViewById(R.id.imageView2);
        fullName = (EditText) findViewById(R.id.fullNameId);
        email = (EditText) findViewById(R.id.emailId);
        password = (EditText)findViewById(R.id.passId);
        confirmPass = (EditText) findViewById(R.id.confirmPassID);
        userName = (EditText) findViewById(R.id.usernameId) ;
        loader = new ProgressDialog(this);
        register = (Button) findViewById(R.id.button3);
        mAuth = FirebaseAuth.getInstance();
        wannaLogin = (TextView) findViewById(R.id.regtextView);

        wannaLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MentorRegActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
        profileImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "Select Picture"),1);
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {

                    if(check() == true) {
                        loader.setMessage("Sign up in progress");
                        loader.setCanceledOnTouchOutside(false);
                        loader.show();
                        userRegister();

                    }
                    else {
                        Toast.makeText(MentorRegActivity.this, "something went wrong", Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    Toast.makeText(MentorRegActivity.this, "something went wrong", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void userRegister() {
        mAuth.createUserWithEmailAndPassword(Email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    uploadImg();
                    keepData();
                    loader.dismiss();
                    Intent intent = new Intent(MentorRegActivity.this, WelcomeActivity.class);
                    startActivity(intent);
                    finish();

                } else {
                    finish();
                    Toast.makeText(MentorRegActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                    loader.dismiss();
                }

            }
        });
    }
    private void keepData() {
        userId = mAuth.getCurrentUser().getUid();
        reference = FirebaseDatabase.getInstance().getReference().child("users").child(userId);
        Map hashMap = new HashMap();
        hashMap.put("UserName", userNam);
        hashMap.put("FullName", fullNam);
        hashMap.put("UserID", userId);
        hashMap.put("Email", Email);
        hashMap.put("Mentor", t);

        reference.updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if(task.isSuccessful()) {
                    Toast.makeText(MentorRegActivity.this, "register successful", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MentorRegActivity.this, "failed to upload data" + task.getException().toString(), Toast.LENGTH_SHORT).show();
                }
                finish();
                loader.dismiss();

            }
        });
    }
    private void uploadImg() {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReference();
        userId = mAuth.getCurrentUser().getUid();

        StorageReference ref = storageReference.child("Profile Images").child(userId);
        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), resultUri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 20, byteArrayOutputStream);
        byte[] data = byteArrayOutputStream.toByteArray();
        UploadTask uploadTask = ref.putBytes(data);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                if(taskSnapshot.getMetadata().getReference() != null) {
                    Task<Uri> result = taskSnapshot.getStorage().getDownloadUrl();
                    result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            downUrl = uri.toString();
                            reference = FirebaseDatabase.getInstance().getReference().child("users").child(userId);
                            Map hashMap = new HashMap();
                            hashMap.put("DownloadUrl", downUrl);

                            reference.updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
                                @Override
                                public void onComplete(@NonNull Task task) {
                                    Toast.makeText(MentorRegActivity.this, "Image uploaded", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
                }
            }
        });
    }
    private boolean check() {
        fullNam = fullName.getText().toString();
        userNam = userName.getText().toString();
        Email = email.getText().toString();
        pass = password.getText().toString();
        conPass = confirmPass.getText().toString();
        if(fullNam.length() == 0) {
            fullName.setError("full name is required");
            fullName.requestFocus();
        }
        if(Email.length() == 0) {
            email.setError("email address is required");
            email.requestFocus();
        }
        if(userNam.length() == 0) {
            userName.setError("user name is required");
            userName.requestFocus();
        }
        if(pass.length() == 0) {
            password.setError("enter password");
            password.requestFocus();
        }
        if(conPass.length() == 0) {
            confirmPass.setError("re-enter the password");
            confirmPass.requestFocus();
        }
        if(pass.length() != 0 && conPass.length() != 0) {
            if(!pass.equals(conPass)) {
                confirmPass.setError("password doesn't matched");
                confirmPass.requestFocus();

            }
        }
        if(resultUri == null) {
            Toast.makeText(MentorRegActivity.this, "profile image is required", Toast.LENGTH_SHORT).show();
        }
        if(fullNam.length() != 0 && userNam.length() != 0 && pass.length()  != 0 && conPass.length()  != 0&& pass.equals(conPass) && Email.length() != 0 && resultUri != null) {
            return  true;
        }
        return  false;

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (resultCode == RESULT_OK && resultCode == RESULT_OK) {
                if (requestCode == 1) {
                    resultUri = data.getData();
                    profileImg.setImageURI(resultUri);
                }
            }
        } catch (Exception e) {
            Toast.makeText(MentorRegActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
        }
    }
}