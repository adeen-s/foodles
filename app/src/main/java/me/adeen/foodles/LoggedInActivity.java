package me.adeen.foodles;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

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

public class LoggedInActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logged_in);

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("users");
        final FirebaseUser mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(mFirebaseUser != null) {
            mDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.hasChild(mFirebaseUser.getUid())) {
                        startActivity(new Intent(LoggedInActivity.this, MainActivity.class));
                        LoggedInActivity.this.finish();
                    } else {
                        startActivity(new Intent(LoggedInActivity.this, SignupActivity.class));
                        LoggedInActivity.this.finish();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }
}