package me.adeen.foodles;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import me.adeen.foodles.models.Restaurant;

public class RestaurantActivity extends AppCompatActivity {

    static final int IMAGE_PICKER = 123;
    Button uploadMenuBtn, viewOrderBtn, imageUploadBtn;
    EditText nameInput, addressInput, numberInput;
    DatabaseReference restaurantSnapshot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant);

        nameInput = findViewById(R.id.restaurantNameInput);
        numberInput = findViewById(R.id.restaurantNumberInput);
        addressInput = findViewById(R.id.restaurantAddressInput);

        restaurantSnapshot = FirebaseDatabase.getInstance().getReference().child("restaurants");
        restaurantSnapshot.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.hasChild(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                    Restaurant mRestaurant = snapshot.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).getValue(Restaurant.class);
                    nameInput.setText(mRestaurant.name);
                    numberInput.setText(mRestaurant.number);
                    addressInput.setText(mRestaurant.address);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        uploadMenuBtn = findViewById(R.id.restaurantUploadButton);
        imageUploadBtn = findViewById(R.id.restaurantImageUploadButton);
        viewOrderBtn = findViewById(R.id.restaurantViewOrderButton);

        viewOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RestaurantActivity.this, RestaurantOrderActivity.class));
            }
        });

        uploadMenuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (numberInput.getText().toString().trim().equals("")
                        && nameInput.getText().toString().trim().equals("")
                        && addressInput.getText().toString().trim().equals("")) {
                    Toast.makeText(RestaurantActivity.this, "Please fill all entries", Toast.LENGTH_SHORT).show();
                } else {
                    Restaurant mRestaurant = new Restaurant(numberInput.getText().toString().trim(),
                            nameInput.getText().toString().trim(),
                            addressInput.getText().toString().trim(),
                            FirebaseAuth.getInstance().getCurrentUser().getUid());
                    if (FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl() != null) {
                        mRestaurant.setImageUri(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl().toString());
                    }
                    restaurantSnapshot.child(mRestaurant.getMenuId()).setValue(mRestaurant);
                }
                FirebaseAuth.getInstance().getCurrentUser().updateProfile(new UserProfileChangeRequest.Builder().setDisplayName(nameInput.getText().toString().trim()).build());
                startActivity(new Intent(RestaurantActivity.this, MenuInputActivity.class));
            }
        });

        imageUploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                startActivityForResult(intent, IMAGE_PICKER);
            }
        });
    }

    private String getRealPathFromURI(Uri uri) {
        Cursor cursor = null;
        try {
            if (uri != null) {
                String[] tempArray = {MediaStore.Images.Media.DATA};
                cursor = this.getContentResolver().query(uri, tempArray, null, null, null);
                if (cursor != null) {
                    int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    cursor.moveToFirst();
                    return cursor.getString(column_index);
                } else {
                    return null;
                }
            } else {
                return null;
            }
        } catch (Exception e) {
            Log.d("ERROR!!!!", "getRealPathFromURI: NULL");
            return null;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        if (requestCode == IMAGE_PICKER && resultCode == RESULT_OK) {
            if (resultData != null) {
                Uri imageUri = resultData.getData();
                try {
                    FileInputStream importdb = new FileInputStream(this.getContentResolver().openFileDescriptor(imageUri, "r").getFileDescriptor());
                    FirebaseStorage storage = FirebaseStorage.getInstance();
                    final StorageReference storageRef = storage.getReference().child(FirebaseAuth.getInstance().getCurrentUser().getUid() + "/image.jpg");
                    UploadTask uploadTask = storageRef.putStream(importdb);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle unsuccessful uploads
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                            storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    FirebaseAuth.getInstance().getCurrentUser().updateProfile(new UserProfileChangeRequest.Builder().setPhotoUri(uri).build());
                                }
                            });
                        }
                    });
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }

        }
    }
}
