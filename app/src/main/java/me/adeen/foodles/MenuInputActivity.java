package me.adeen.foodles;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import me.adeen.foodles.models.Item;
import me.adeen.foodles.models.Restaurant;

public class MenuInputActivity extends AppCompatActivity {

    final String TAG = "MENUINPUTACTIVITY";
    ListView listView;
    List<Item> items;
    EditText nameInput;
    EditText priceInput;
    Item item;
    DatabaseReference mDatabaseReference;
    CustomBaseAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_input);

        items = new ArrayList<Item>();
        adapter = new CustomBaseAdapter(MenuInputActivity.this, items);
        listView = (ListView) findViewById(R.id.menuInputList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                items.remove(position);
                adapter.notifyDataSetChanged();
            }
        });

        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("menu");
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                    GenericTypeIndicator<List<Item>> t = new GenericTypeIndicator<List<Item>>() {};
                    items.addAll(dataSnapshot.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).getValue(t));
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        nameInput = findViewById(R.id.menuInputName);
        priceInput = findViewById(R.id.menuInputPrice);

        Button addItemBtn = findViewById(R.id.menuInputAddButton);

        addItemBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isEditTextEmpty(nameInput) && !isEditTextEmpty(priceInput)) {
                    item = new Item(nameInput.getText().toString(), Float.parseFloat(priceInput.getText().toString()));
                    items.add(item);
                    adapter.notifyDataSetChanged();
                    nameInput.getText().clear();
                    priceInput.getText().clear();
                }
            }
        });

        Button submitBtn = findViewById(R.id.menuInputSubmitButton);
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(items.size() > 0) {
                    mDatabaseReference.child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).setValue(items);
                    startActivity(new Intent(MenuInputActivity.this, RestaurantActivity.class));
                } else {
                    Toast.makeText(MenuInputActivity.this, "Add some data first", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean isEditTextEmpty(EditText etText) {
        return etText.getText().toString().trim().length() == 0;
    }
}

class CustomBaseAdapter extends BaseAdapter {
    Context context;
    List<Item> items;

    public CustomBaseAdapter(Context context, List<Item> items) {
        this.context = context;
        this.items = items;
    }

    /*private view holder class*/
    private class ViewHolder {
        TextView name;
        TextView price;
    }

    @SuppressLint("SetTextI18n")
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;

        LayoutInflater mInflater = (LayoutInflater)
                context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_menu_input, null);
            holder = new ViewHolder();
            holder.name = (TextView) convertView.findViewById(R.id.listMenuInputName);
            holder.price = (TextView) convertView.findViewById(R.id.listMenuInputPrice);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        Item item = (Item) getItem(position);

        holder.name.setText(item.getName());
        holder.price.setText(Float.toString(item.getPrice()));

        return convertView;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return items.indexOf(getItem(position));
    }
}
