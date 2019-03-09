package me.adeen.foodles;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import me.adeen.foodles.models.Restaurant;

public class CustomerActivity extends AppCompatActivity {

    EditText searchInput;
    ListView restaurantList;
    List<Restaurant> restaurants;
    DatabaseReference mDatabaseReference;
    CustomerAdapter adapter;

    final String TAG = "Customer Activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer);

        findViewById(R.id.customerShowPreviousOrders).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CustomerActivity.this, CustomerOrderActivity.class));
                CustomerActivity.this.finish();
            }
        });

        searchInput = findViewById(R.id.customerRestaurantSearchInput);
        restaurantList = findViewById(R.id.customerRestaurantList);
        restaurantList.setTextFilterEnabled(true);
        restaurants = new ArrayList<Restaurant>();
        adapter = new CustomerAdapter(CustomerActivity.this, restaurants);
        restaurantList.setAdapter(adapter);
        restaurantList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(CustomerActivity.this, OrderActivity.class);
                intent.putExtra("name", restaurants.get(position).name);
                intent.putExtra("UID", restaurants.get(position).menuId);
                startActivity(intent);
            }
        });

        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("restaurants");
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Log.d(TAG, "onDataChange: " + snapshot.getValue(Restaurant.class).getName());
                    Restaurant restaurant = snapshot.getValue(Restaurant.class);
                    restaurants.add(restaurant);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }
}

class CustomerAdapter extends BaseAdapter implements Filterable {
    Context context;
    List<Restaurant> restaurants;
    List<Restaurant> mOriginalValues;


    public CustomerAdapter(Context context, List<Restaurant> restaurants) {
        this.context = context;
        this.restaurants = restaurants;
        this.mOriginalValues = restaurants;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                if (constraint == null || constraint.length() == 0) {
                    //no constraint given, just return all the data. (no search)
                    results.count = mOriginalValues.size();
                    results.values = mOriginalValues;
                } else {//do the search
                    List<Restaurant> resultsData = new ArrayList<>();
                    String searchStr = constraint.toString().toUpperCase();
                    for (Restaurant o : restaurants)
                        if (o.name.toUpperCase().startsWith(searchStr)) resultsData.add(o);
                    results.count = resultsData.size();
                    results.values = resultsData;
                }
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                restaurants = (ArrayList<Restaurant>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    /*private view holder class*/
    private class ViewHolder {
        TextView name;
        TextView address;
        ImageView imageView;
    }

    @SuppressLint("SetTextI18n")
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;

        LayoutInflater mInflater = (LayoutInflater)
                context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_customer, null);
            holder = new ViewHolder();
            holder.name = (TextView) convertView.findViewById(R.id.listCustomerRestaurantName);
            holder.address = (TextView) convertView.findViewById(R.id.listCustomerRestaurantAddress);
            holder.imageView = convertView.findViewById(R.id.listCustomerRestaurantImage);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        Restaurant restaurant = (Restaurant) getItem(position);

        holder.name.setText(restaurant.getName());
        holder.address.setText(restaurant.getAddress());

        if(restaurant.getImageUri() != null) {
//            new ImageLoadTask(restaurant.getImageUri().toString(), holder.imageView).execute();
//            holder.imageView.setImageBitmap(getBitmapFromURL(restaurant.getImageUri()));
//            new ImageLoadTask(restaurant.getImageUri(),holder.imageView);
            Picasso.get().load(restaurant.getImageUri()).into(holder.imageView);

        }

        return convertView;
    }

    @Override
    public int getCount() {
        return restaurants.size();
    }

    @Override
    public Object getItem(int position) {
        return restaurants.get(position);
    }

    @Override
    public long getItemId(int position) {
        return restaurants.indexOf(getItem(position));
    }

    public static Bitmap getBitmapFromURL(String src) {
        try {
            Log.e("src",src);
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            Log.e("Bitmap","returned");
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("Exception",e.getMessage());
            return null;
        }
    }

}


class ImageLoadTask extends AsyncTask<Void, Void, Bitmap> {

    final String TAG = "Asynctask CustActivity";

    private String url;
    @SuppressLint("StaticFieldLeak")
    private ImageView image;

    public ImageLoadTask(String url, ImageView image) {
        this.url = url;
        this.image = image;
    }

    @Override
    protected Bitmap doInBackground(Void... params) {
        try {
            URL urlConnection = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) urlConnection
                    .openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Bitmap result) {
        super.onPostExecute(result);
        Log.e("ADAPTER", "getView: DONE");
        image.setImageBitmap(result);
        Log.d(TAG, "onPostExecute: ");
    }

}

