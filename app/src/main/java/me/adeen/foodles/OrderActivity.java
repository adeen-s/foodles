package me.adeen.foodles;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import me.adeen.foodles.models.Order;
import me.adeen.foodles.models.PlacedOrder;
import me.adeen.foodles.models.User;

public class OrderActivity extends AppCompatActivity {

    private static OrderActivity sOrderActivity;
    DatabaseReference mDatabaseReference;
    OrderActivityAdapter adapter;
    List<Order> orders;
    List<Order> finalOrders;
    List<String> codes;
    PlacedOrder placedOrder;
    String time, code;
    ListView orderList;
    EditText name, number, address, promoCode;
    TextView amountTextView;
    Button placeOrderButton;
    String uid, restaurantName;
    int amount = 0;
    final String TAG = "ORDERACTIVITY";

    public void updateAmount(float change) {
        amount += change;
        amountTextView.setText("Total: Rs. " + amount);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        sOrderActivity = this;

        uid = getIntent().getStringExtra("UID");
        restaurantName = getIntent().getStringExtra("name");

        Toast.makeText(this, "UID: " + uid, Toast.LENGTH_SHORT).show();

        amountTextView = findViewById(R.id.orderAmount);

        finalOrders = new ArrayList<>();
        orders = new ArrayList<>();
        adapter = new OrderActivityAdapter(OrderActivity.this, orders);
        orderList = findViewById(R.id.orderList);
        orderList.setAdapter(adapter);
        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("menu").child(uid);
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    GenericTypeIndicator<List<Order>> t = new GenericTypeIndicator<List<Order>>() {};
                    orders.addAll(dataSnapshot.getValue(t));
                    adapter.notifyDataSetChanged();
                    Log.d(TAG, "onDataChange: " + orders.get(0).getName());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        name = findViewById(R.id.orderName);
        number = findViewById(R.id.orderNumber);
        address = findViewById(R.id.orderAddress);
        promoCode = findViewById(R.id.orderPromoCode);
        placeOrderButton = findViewById(R.id.placeOrderButton);

        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                name.setText(user.getName());
                number.setText(user.getNumber());
                address.setText(user.getAddress());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        placeOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finalOrders = new ArrayList<>();
                for(int i = 0; i < orders.size(); i++) {
                    if(orders.get(i).getQuantity() > 0) {
                        finalOrders.add(orders.get(i));
                    }
                }
                if(finalOrders.size() > 0) {
                    time = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
                    code = promoCode.getText().toString().trim();
                    if(code.equals("")) {
                        placedOrder = new PlacedOrder(finalOrders, FirebaseAuth.getInstance().getCurrentUser().getDisplayName(), restaurantName, time, amount);
                    } else {
                        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("codes");
                        codes = new ArrayList<>();
                        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                GenericTypeIndicator<List<String>> t = new GenericTypeIndicator<List<String>>() {};
                                codes.addAll(dataSnapshot.getValue(t));
                                if(codes.contains(code)) {
                                    placedOrder = new PlacedOrder(finalOrders, FirebaseAuth.getInstance().getCurrentUser().getDisplayName(), restaurantName, time, amount, code);
                                } else {
                                    Toast.makeText(OrderActivity.this, "Invalid Promo Code", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                    if(placedOrder != null) {
                        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("orders");
                        String mPushKey = mDatabaseReference.push().getKey();
                        mDatabaseReference.child(uid).child(mPushKey).setValue(placedOrder);
                        mDatabaseReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(mPushKey).setValue(placedOrder);
                        Toast.makeText(OrderActivity.this, "SUCCESS! Order Placed", Toast.LENGTH_LONG).show();
                        OrderActivity.this.finish();
                    }
                } else {
                    Toast.makeText(OrderActivity.this, "Please select some items before placing order", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public static OrderActivity getInstance() {
        return sOrderActivity;
    }
}

class OrderActivityAdapter extends BaseAdapter {
    Context context;
    List<Order> orders;

    public OrderActivityAdapter(Context context, List<Order> orders) {
        this.context = context;
        this.orders = orders;
    }

    /*private view holder class*/
    private class ViewHolder {
        TextView name;
        TextView price;
        TextView quantity;
        Button incButton;
        Button decButton;
    }

    @SuppressLint("SetTextI18n")
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;

        LayoutInflater mInflater = (LayoutInflater)
                context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_order, null);
            holder = new ViewHolder();
            holder.name = (TextView) convertView.findViewById(R.id.listOrderName);
            holder.price = (TextView) convertView.findViewById(R.id.listOrderPrice);
            holder.quantity = (TextView) convertView.findViewById(R.id.listOrderQuantity);
            holder.incButton = (Button) convertView.findViewById(R.id.listIncButton);
            holder.decButton = (Button) convertView.findViewById(R.id.listDecButton);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        final Order order = (Order) getItem(position);

        holder.name.setText(order.getName());
        holder.price.setText(Float.toString(order.getPrice()));
        holder.quantity.setText(Integer.toString(order.getQuantity()));
        holder.incButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                orders.get(position).setQuantity(order.getQuantity() + 1);
                OrderActivity.getInstance().updateAmount(orders.get(position).getPrice());
                notifyDataSetChanged();
            }
        });
        holder.decButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(orders.get(position).getQuantity() > 0) {
                    orders.get(position).setQuantity(order.getQuantity() - 1);
                    OrderActivity.getInstance().updateAmount(orders.get(position).getPrice() * -1);
                    notifyDataSetChanged();
                }
            }
        });

        return convertView;
    }

    @Override
    public int getCount() {
        return orders.size();
    }

    @Override
    public Object getItem(int position) {
        return orders.get(position);
    }

    @Override
    public long getItemId(int position) {
        return orders.indexOf(getItem(position));
    }
}
