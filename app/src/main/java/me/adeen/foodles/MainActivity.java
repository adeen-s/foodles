package me.adeen.foodles;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    FirebaseUser mFirebaseUser;
    DatabaseReference mDatabase;
    Button signoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        signoutButton = (Button) findViewById(R.id.mainSignout);
        signoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AuthUI.getInstance()
                        .signOut(MainActivity.this)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            public void onComplete(@NonNull Task<Void> task) {
                                startActivity(new Intent(MainActivity.this, WelcomeActivity.class));
                                MainActivity.this.finish();
                            }
                        });
            }
        });


        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (mFirebaseUser != null) {
            mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(mFirebaseUser.getUid());
            mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    boolean isRestaurant = Boolean.parseBoolean(Objects.requireNonNull(dataSnapshot.child("isRestaurant").getValue()).toString());
                    if (isRestaurant) {
                        startActivity(new Intent(MainActivity.this, RestaurantActivity.class));
                        Toast.makeText(MainActivity.this, "Restaurant", Toast.LENGTH_SHORT).show();
                    } else {
                        startActivity(new Intent(MainActivity.this, CustomerActivity.class));
                        Toast.makeText(MainActivity.this, "Customer", Toast.LENGTH_SHORT).show();
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }
}
