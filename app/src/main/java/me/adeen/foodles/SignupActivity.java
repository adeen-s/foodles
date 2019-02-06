package me.adeen.foodles;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import me.adeen.foodles.models.User;

public class SignupActivity extends AppCompatActivity {

    DatabaseReference mDatabaseReference;
    FirebaseUser mFirebaseUser;
    EditText numberInput, nameInput, addressInput;
    RadioButton radioRestaurant;
    Button signupSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        numberInput = findViewById(R.id.numberInput);
        nameInput = findViewById(R.id.nameInput);
        addressInput = findViewById(R.id.addressInput);
        signupSubmit = findViewById(R.id.signupSubmit);
        radioRestaurant = findViewById(R.id.signupTypeRestaurant);

        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(mFirebaseUser != null) {
            mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("users");

            String number = mFirebaseUser.getPhoneNumber();
            String name = mFirebaseUser.getDisplayName();
            if (number != null) {
                numberInput.setText(number);
            }
            if (name != null) {
                nameInput.setText(name);
            }

            signupSubmit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(numberInput.getText().toString().trim().equals("")
                            && nameInput.getText().toString().trim().equals("")
                            && addressInput.getText().toString().trim().equals("")) {
                        Toast.makeText(SignupActivity.this, "Please fill all entries", Toast.LENGTH_SHORT).show();
                    } else {
                        User user = new User(numberInput.getText().toString().trim(),
                                nameInput.getText().toString().trim(),
                                addressInput.getText().toString().trim(),
                                radioRestaurant.isChecked());
                        mDatabaseReference.child(mFirebaseUser.getUid()).setValue(user);
                        startActivity(new Intent(SignupActivity.this, MainActivity.class));
                        SignupActivity.this.finish();
                    }
                }
            });

        } else {
            startActivity(new Intent(SignupActivity.this, WelcomeActivity.class));
            SignupActivity.this.finish();
        }
    }
}
