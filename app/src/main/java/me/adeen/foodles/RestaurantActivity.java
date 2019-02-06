package me.adeen.foodles;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import me.adeen.foodles.models.Restaurant;

public class RestaurantActivity extends AppCompatActivity {

    Button uploadMenuBtn;
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

        uploadMenuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(numberInput.getText().toString().trim().equals("")
                        && nameInput.getText().toString().trim().equals("")
                        && addressInput.getText().toString().trim().equals("")) {
                    Toast.makeText(RestaurantActivity.this, "Please fill all entries", Toast.LENGTH_SHORT).show();
                } else {
                    Restaurant mRestaurant = new Restaurant(numberInput.getText().toString().trim(),
                            nameInput.getText().toString().trim(),
                            addressInput.getText().toString().trim(),
                            FirebaseAuth.getInstance().getCurrentUser().getUid());
                    restaurantSnapshot.child(mRestaurant.getMenuId()).setValue(mRestaurant);
                }
                startActivity(new Intent(RestaurantActivity.this, MenuInputActivity.class));
            }
        });

    }
}
