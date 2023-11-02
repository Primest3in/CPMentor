package com.example.cpyipee;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MakeBlogActivity extends AppCompatActivity {

    private Button post;
    private ImageButton choseImg;
    private EditText writePost;
    private ImageView showImg;
    private String askBy;
    private DatabaseReference askByRef;
    private String myUrl;
    private String askByImgUrl;
    private StorageTask uploadTask;
    private TextView askByN;
    private ImageView askByI;
    private StorageReference storageReference;
    private Uri imgUri;
    private Spinner spinner;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private ProgressDialog loader;
    private String userId;
    private Context context = MakeBlogActivity.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_blog);
        askByN = findViewById(R.id.askByName);
        askByI = findViewById(R.id.askByImg);
        post = findViewById(R.id.toolbar_button);
        choseImg = findViewById(R.id.choseImg_id);
        writePost = findViewById(R.id.post_edit_text_id);
        showImg = findViewById(R.id.gallery_img_view);
        spinner = findViewById(R.id.topic_spinner);

        loader = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        userId = mUser.getUid();
        askByRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId);
        askByRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                askBy = snapshot.child("FullName").getValue(String.class);
                askByImgUrl = snapshot.child("DownloadUrl").getValue(String.class);
                askByN.setText(askBy);
                Glide.with(context).load(askByImgUrl).transform(new CircleCrop()).into(askByI);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        storageReference = FirebaseStorage.getInstance().getReference("Blogs");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.topics));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(spinner.getSelectedItem().equals("select topic")) {
                    Toast.makeText(MakeBlogActivity.this, "select a topic", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        choseImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "Select Image"),1);
            }
        });
        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkValidity() == true) {
                    startLoader();
                    uploadPost();
                    if(imgUri != null) uploadImg();
                }
            }
        });


    }
    private void uploadImg() {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        DatabaseReference refRealtime = FirebaseDatabase.getInstance().getReference("Blogs");


        StorageReference storageReference = storage.getReference();

        StorageReference ref = storageReference.child("Blogs").child(System.currentTimeMillis() + "." + getFileExe(imgUri));
        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), imgUri);
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
                            myUrl = uri.toString();

                            Map hashMap = new HashMap();
                            hashMap.put("DownloadUrl", myUrl);

                            refRealtime.child(postId).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
                                @Override
                                public void onComplete(@NonNull Task task) {
                                    Toast.makeText(getApplicationContext(), "Image uploaded", Toast.LENGTH_SHORT).show();

                                }
                            });
                        }
                    });
                }
            }
        });
    }
    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
    String postId = ref.push().getKey();
    private void uploadPost() {


        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("postId", postId);
        hashMap.put("postTexts", getPost());
        hashMap.put("Publisher", userId);
        hashMap.put("Topic", getTopic());
        hashMap.put("AskedBy", askBy);
        hashMap.put("Date", getDate());
        hashMap.put("isPost", "f");
        hashMap.put("whoImg", askByImgUrl);


        ref.child(postId).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    Toast.makeText(MakeBlogActivity.this, "posted successfully", Toast.LENGTH_SHORT).show();
                    loader.dismiss();
                    startActivity(new Intent(MakeBlogActivity.this, BlogActivity.class));
                    finish();
                }
                else {
                    Toast.makeText(MakeBlogActivity.this, "failed to upload" + task.getException().toString(), Toast.LENGTH_SHORT).show();
                    loader.dismiss();
                }
            }
        });

    }
    private String getFileExe(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private boolean checkValidity() {
        if(spinner.getSelectedItem().equals("select topic")) {
            Toast.makeText(MakeBlogActivity.this, "select a topic", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(writePost.getText().length() == 0) {
            writePost.setError("This shouldn't be empty");
            return false;
        }
        return true;

    }

    String getPost() {
        return writePost.getText().toString().trim();
    }
    String getTopic() {
        return spinner.getSelectedItem().toString();
    }
    String getDate() {
        return DateFormat.getDateInstance().format(new Date());
    }
    void startLoader() {
        loader.setMessage("Uploading...");
        loader.setCanceledOnTouchOutside(false);
        loader.show();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (resultCode == RESULT_OK && resultCode == RESULT_OK) {
                if (requestCode == 1) {
                    imgUri = data.getData();
                    showImg.setImageURI(imgUri);
                    ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#85170F0F"));
                    showImg.setBackgroundDrawable(colorDrawable);
                }
            }
        } catch (Exception e) {
            Toast.makeText(MakeBlogActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
        }
    }
}