package com.example.nearest_dept;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewParent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.android.gms.tasks.OnFailureListener;
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
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.UUID;

import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;

public class Profile_Activity extends AppCompatActivity {
    RelativeLayout logout_lay,l1;
    private Uri filePath;
    private final int PICK_IMAGE_REQUEST = 71;
    ImageView imageView;
    //Firebase
    FirebaseStorage storage;
    StorageReference storageReference;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    RelativeLayout about_univ_layout;
    private String mUserId;
    LinearLayout layout1;
    EditText address,name,phone,mail;
    Button update;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        logout_lay=findViewById(R.id.logout_lay);
        l1=findViewById(R.id.l1);
        name=findViewById(R.id.name);
        address=findViewById(R.id.address);
        phone=findViewById(R.id.phone);
        mail=findViewById(R.id.mail);
        update=findViewById(R.id.update);
        imageView=findViewById(R.id.iv_pro);
        about_univ_layout=findViewById(R.id.about_univ_layout);
        layout1=findViewById(R.id.layout1);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        mUserId = mFirebaseUser.getUid();
        DatabaseReference myRef = database.getReference("user_details").child(mUserId);

        about_univ_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(Profile_Activity.this,About_University.class);
                startActivity(i);
            }
        });

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {

                //if user is not null, by now will execute.
                if (mFirebaseAuth.getCurrentUser() != null&&!String.valueOf(dataSnapshot.child("name").getValue()).equals("null")) {
                    name.setText(String.valueOf(dataSnapshot.child("name").getValue()));
                    mail.setText(String.valueOf(dataSnapshot.child("email").getValue()));
                    phone.setText(String.valueOf(dataSnapshot.child("phone").getValue()));
                    address.setText(String.valueOf(dataSnapshot.child("address").getValue()));

                    Glide.with(getApplicationContext()).load(String.valueOf(dataSnapshot.child("img_url").getValue())).into(imageView);

                }
                else{
                    name.setText("");
                    mail.setText("");
                    phone.setText("");
                    address.setText("");
                }


                update.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                         if(imageView.getDrawable()==null){
                            Toast.makeText(Profile_Activity.this, "Please capture image", Toast.LENGTH_SHORT).show();
                        }else if(name.getText().toString().equals("")){
                            Toast.makeText(Profile_Activity.this, "Please enter Name", Toast.LENGTH_SHORT).show();
                        }else if(mail.getText().toString().equals("")){
                            Toast.makeText(Profile_Activity.this, "Please enter Email", Toast.LENGTH_SHORT).show();
                        }else if(phone.getText().toString().equals("")){
                            Toast.makeText(Profile_Activity.this, "Please enter Phone no.", Toast.LENGTH_SHORT).show();
                        }else{
                            uploadImage();
                        }}
                });

            }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(Profile_Activity.this, databaseError.toString(), Toast.LENGTH_SHORT).show();
                    }
                });

                logout_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        layout1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //alert dialogue for prompting user to delete item.
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(Profile_Activity.this);
                builder.setMessage("are you sure you want to LogOut")
                        .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                mFirebaseAuth.signOut();

                                //calling home screen by tapping on logout button.
                                Intent intent = new Intent(Profile_Activity.this, LoginActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                Toast.makeText(Profile_Activity.this, "Logged out", Toast.LENGTH_SHORT).show();

                            }
                        }).setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });

                android.app.AlertDialog alertDialogue = builder.create();
                alertDialogue.show();

            }
        });
l1.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Profile_Activity.this);
        alertDialogBuilder.setMessage("Pick Image From");
        alertDialogBuilder.setPositiveButton("Camera",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);//Start intent with Action_Image_Capture
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, filePath);//Send fileUri with intent
                        startActivityForResult(intent, PICK_IMAGE_REQUEST);//start activity for result with CAMERA_REQUEST_CODE

                    }
                });

        alertDialogBuilder.setNegativeButton("Gallary",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
               }
        });
        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
});
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                imageView.setImageBitmap(bitmap);

            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }else{
            if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK ) {
               try {
                    Bitmap photo = (Bitmap) data.getExtras().get("data");
                    imageView.setImageBitmap(photo);
                   filePath= getImageUri(this,photo);

                }catch (Exception ae){
                    Toast.makeText(this, "User Cancel Capture image", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }
    private void uploadImage() {

        if(filePath != null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.setCancelable(false);
            progressDialog.show();

            StorageReference ref = storageReference.child("images/"+ UUID.randomUUID().toString());
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            if (taskSnapshot.getMetadata() != null) {
                                if (taskSnapshot.getMetadata().getReference() != null) {
                                    Task<Uri> result = taskSnapshot.getStorage().getDownloadUrl();
                                    result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            String imageUrl = uri.toString();
                                            Log.d("image_url",imageUrl);
                                            mFirebaseAuth = FirebaseAuth.getInstance();
                                            mFirebaseUser = mFirebaseAuth.getCurrentUser();

                                            final FirebaseDatabase database = FirebaseDatabase.getInstance();
                                            mUserId = mFirebaseUser.getUid();
                                            DatabaseReference myRef = database.getReference("user_details").child(mUserId);

                                            myRef.child("name").setValue(name.getText().toString());
                                            myRef.child("img_url").setValue(imageUrl);
                                            myRef.child("email").setValue(mail.getText().toString());
                                            myRef.child("phone").setValue(phone.getText().toString());
                                            myRef.child("address").setValue(address.getText().toString());
                                            Toast.makeText(Profile_Activity.this, "UPDATED", Toast.LENGTH_SHORT).show();
                                            progressDialog.dismiss();
                                        }
                                    });
                                }
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(Profile_Activity.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded "+(int)progress+"%");
                        }
                    });
        }
    }
}
