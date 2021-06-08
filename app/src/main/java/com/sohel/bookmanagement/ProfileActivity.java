package com.sohel.bookmanagement;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.sohel.bookmanagement.Model.Users;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {
  private  static final int IMAGE_PICKER=1;
  private CircleImageView userProfile;
  private EditText nameEt,phoneEt;
  private TextView emailTv,uidTv;
  private  Button updateButton;
  private  ProgressDialog progressBar;
  private StorageReference storageReference;

  Uri imageUri;

  private DatabaseReference userRef;
  private FirebaseAuth mAuth;
  private Toolbar toolbar;

  Users user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        init();

        toolbar=findViewById(R.id.profileAppBarid);
        setSupportActionBar(toolbar);
        this.setTitle("Profile");


        progressBar=new ProgressDialog(this);

        loadUserData();


        userProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermissionWithDexter();
            }
        });
        uidTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                  copyToClipBoard(user.getUid());
            }
        });

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name=nameEt.getText().toString();
                String phone=phoneEt.getText().toString().trim();

                if(name.isEmpty()){
                    nameEt.setError("Please Write Your Name");
                    nameEt.requestFocus();
                }else if(phone.isEmpty()){
                    phoneEt.setError("Please Write Your Phone Number");
                    phoneEt.requestFocus();
                }else if(phone.length()!=11){
                    phoneEt.setError("Please Write Correct Phone Number");
                    phoneEt.requestFocus();
                }else{
                    saveData(name,phone);
                }


            }
        });


    }

    private void loadUserData() {
        userRef.child(mAuth.getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            user=snapshot.getValue(Users.class);
                            String profileImage=snapshot.child("image").getValue().toString();
                            emailTv.setText(user.getEmail());
                            phoneEt.setText(user.getPhone());
                            nameEt.setText(user.getName());
                            uidTv.setText(user.getUid());

                            Picasso.get().load(profileImage).placeholder(R.drawable.profile)
                                    .into(userProfile);

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void saveData(String name,String phone) {
        if(imageUri==null){
            saveDataIntoDataBase(name,phone,null);
        }else{
            uploadImage(name,phone);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==IMAGE_PICKER && resultCode==RESULT_OK && data.getData()!=null){
            imageUri=data.getData();
            userProfile.setImageURI(data.getData());
        }

    }
    private void copyToClipBoard(String text) {

        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(
                "uid", // What should I set for this "label"?
                text);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(ProfileActivity.this, "Uid Copy to clip board", Toast.LENGTH_SHORT).show();
    }

    private void uploadImage(String name,String phone) {
        progressBar.setMessage("Uploading Image");
        progressBar.setTitle("Please Wait..");
        progressBar.show();

        String key=userRef.push().getKey();

        StorageReference filePath=storageReference.child(key+System.currentTimeMillis()+"."+getFileExtension(imageUri));
        filePath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> urlTask=taskSnapshot.getStorage().getDownloadUrl();
                while(!urlTask.isSuccessful());
                Uri downloaduri=urlTask.getResult();

               saveDataIntoDataBase(name,phone,downloaduri.toString());

            }
        });


    }

    private void saveDataIntoDataBase(String name, String phone, String image) {

        progressBar.setMessage("Saving Data..");
        progressBar.setTitle("Please Wait..");
        progressBar.show();

        HashMap<String,Object> userMap=new HashMap<>();
        userMap.put("name",name);
        userMap.put("phone",phone);
        if(image!=null){
            userMap.put("image",image);
        }

        userRef.child(mAuth.getCurrentUser().getUid())
                .updateChildren(userMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        progressBar.dismiss();
                        if(task.isSuccessful()){
                            Toast.makeText(ProfileActivity.this, "Profile Updated Successful", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(ProfileActivity.this, "Errro: "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });


    }


    public void init(){
        userProfile=findViewById(R.id.profile_UserProfileImage);
        emailTv=findViewById(R.id.profile_EmailTv);
        nameEt=findViewById(R.id.profile_nameEt);
        phoneEt=findViewById(R.id.profile_phoneEt);
        uidTv=findViewById(R.id.profile_UIDTv);
        updateButton=findViewById(R.id.updateDetailsButton);

        userRef=FirebaseDatabase.getInstance().getReference().child("users");
        mAuth=FirebaseAuth.getInstance();
        storageReference= FirebaseStorage.getInstance().getReference().child("ProjectImage");


    }


    private void checkPermissionWithDexter() {
        Dexter.withContext(ProfileActivity.this)
                .withPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener(){

                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                        if(multiplePermissionsReport.areAllPermissionsGranted()){
                            openfilechooser();
                        }else{
                            Toast.makeText(ProfileActivity.this, "Please Allow permissions", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {

                    }
                }).check();
    }

    private void openfilechooser() {
        Intent intentf=new Intent();
        intentf.setType("image/*");
        intentf.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intentf,IMAGE_PICKER);
    }

    public String getFileExtension(Uri imageuri){
        ContentResolver contentResolver=getContentResolver();
        MimeTypeMap mimeTypeMap=MimeTypeMap.getSingleton();
        return  mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(imageuri));
    }
}