package me.adeen.foodles;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import me.adeen.foodles.models.PlacedOrder;

public class CustomerOrderActivity extends AppCompatActivity {

    DatabaseReference mDatabaseReference;
    List<PlacedOrder> placedOrders;
    TextView placeholderText, latestOrder, previousOrders;
    final String TAG = "CUSTOMERORDERACTIVITY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_order);

        findViewById(R.id.customerOrderShowRestaurantList).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CustomerOrderActivity.this, CustomerActivity.class));
                CustomerOrderActivity.this.finish();
            }
        });

        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("orders").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        placedOrders = new ArrayList<>();
        placeholderText = findViewById(R.id.customerOrderPlaceholder);
        latestOrder = findViewById(R.id.customerOrderLatestOrder);
        previousOrders = findViewById(R.id.customerOrderPreviousOrders);

        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                placedOrders = new ArrayList<>();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    PlacedOrder placedOrder = snapshot.getValue(PlacedOrder.class);
                    placedOrders.add(placedOrder);
                }
                if(placedOrders.size() > 0) {
                    Collections.sort(placedOrders, new Comparator<PlacedOrder>() {
                        @Override
                        public int compare(PlacedOrder o1, PlacedOrder o2) {
                            return o2.getTime().compareTo(o1.getTime());
                        }
                    });
                    Log.d(TAG, "onDataChange: " + placedOrders.get(0).getTime());
                    placeholderText.setVisibility(View.GONE);
                    String time = placedOrders.get(0).getTime().substring(8,10)
                            + ":"
                            + placedOrders.get(0).getTime().substring(10,12)
                            + " "
                            + placedOrders.get(0).getTime().substring(6,8)
                            + "/"
                            + placedOrders.get(0).getTime().substring(4,6)
                            + "/"
                            + placedOrders.get(0).getTime().substring(0,4);
                    latestOrder.setText("Latest Order:" + "\n" + placedOrders.get(0).getToRestaurant() + "\nOrder Time: " + time + "\nOrder Details: ");
                    for (int i = 0; i < placedOrders.get(0).getOrders().size(); i++) {
                        latestOrder.setText(latestOrder.getText() + "\n" + placedOrders.get(0).getOrders().get(i).getName() + "\tx" + placedOrders.get(0).getOrders().get(i).getQuantity());
                    }

                    // Previous Orders
                    previousOrders.setText("\n\nPrevious Orders:\n");
                    for (int i = 1; i < placedOrders.size(); i++) {
                        time = placedOrders.get(i).getTime().substring(8,10)
                                + ":"
                                + placedOrders.get(i).getTime().substring(10,12)
                                + " "
                                + placedOrders.get(i).getTime().substring(6,8)
                                + "/"
                                + placedOrders.get(i).getTime().substring(4,6)
                                + "/"
                                + placedOrders.get(i).getTime().substring(0,4);
                        previousOrders.setText(previousOrders.getText() + "\n" + placedOrders.get(i).getToRestaurant() + "\nOrder Time: " + time + "\nOrder Details: ");
                        for (int j = 0; j < placedOrders.get(i).getOrders().size(); j++) {
                            previousOrders.setText(previousOrders.getText() + "\n" + placedOrders.get(i).getOrders().get(j).getName() + "\tx" + placedOrders.get(i).getOrders().get(j).getQuantity());
                        }
                        previousOrders.setText(previousOrders.getText() + "\n\n");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}