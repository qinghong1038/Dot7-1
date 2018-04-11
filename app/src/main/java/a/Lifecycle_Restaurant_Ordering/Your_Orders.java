package a.Lifecycle_Restaurant_Ordering;

import android.inputmethodservice.Keyboard;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import a.common.CheckConnection;
import a.common.MySingleton;
import a.dot7.R;
import a.getter_setter.RestaurantOrders;
import a.getter_setter.Restaurants;

/**
 * Created by Pooja on 09-04-2018.
 */


public class Your_Orders extends AppCompatActivity implements View.OnClickListener {

    private RecyclerView orders_recyclerView;
    private DrawerLayout mDrawerLayout;
    private RecyclerView.LayoutManager  Layout;
    ArrayList<RestaurantOrders> AllRowData;
    YourOrderAdapter adapter;

    ImageView Error_Image;
    TextView Error_message;
    Button Error_Button;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        Toolbar toolbar=findViewById(R.id.Cart_Toolbar);
        Error_Button = findViewById(R.id.OrderRetryButton);
        Error_Image = findViewById(R.id.OrderView_Error);
        Error_message = findViewById(R.id.OrderError_Message);
        Error_Button.setOnClickListener(this);
        setSupportActionBar(toolbar);
        if(getSupportActionBar()!=null)
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        set_RecyclerView_Details();
        fillData();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
            default:
                return true;
        }
    }

    private void set_RecyclerView_Details()
    {
        AllRowData = new ArrayList<>();
       orders_recyclerView = findViewById(R.id.your_order);
        orders_recyclerView.setHasFixedSize(true);
        Layout = new LinearLayoutManager(this);
        orders_recyclerView.setLayoutManager(Layout);
    }

    private void fillData()
    {

        if(CheckConnection.getInstance(this).getNetworkStatus())
            callService();
        else
        {
            orders_recyclerView.setVisibility(View.GONE);
           Error_message.setVisibility(View.VISIBLE);
           Error_Image.setVisibility(View.VISIBLE);
           Error_Button.setVisibility(View.VISIBLE);
        }

    }

    private void callService() {

        final String URL = " ";
        JsonArrayRequest jsonArrayRequest = new
                JsonArrayRequest(URL, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response1) {
                JSONArray response = null;
                JSONObject json;
                //Json_Parse_Data(response);
                Log.d("HAR", "Response aaya");
                Log.d("HAR", response1.toString());
                String str = response1.toString();

                try {
                    response = new JSONArray(str);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                RestaurantOrders RowData;
                if (response != null) {

                    int length = response.length();
                    for (int i = 0; i < length; i++) {
                        RowData = new RestaurantOrders();

                        try {
                            json = response.getJSONObject(i);
                            RowData.setOrderDate(json.getString(""));   //harneet fill ids
                            RowData.setOrderId(json.getString(""));
                            RowData.setRName(json.getString(""));
                            RowData.setTotalPrice(json.getString(""));

                            AllRowData.add(RowData);

                        } catch (Exception e) {
                            Log.e("Error: ", String.valueOf(e));
                        }

                    }
                }
                    adapter = new
                            YourOrderAdapter(Your_Orders.this, AllRowData);
                    orders_recyclerView.setAdapter(adapter);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(CheckConnection.getInstance(Your_Orders.this).getNetworkStatus())
                {
                    Log.e("VolleyError: ",error.toString());
                }
                else {
                    orders_recyclerView.setVisibility(View.GONE);
                    Error_message.setVisibility(View.VISIBLE);
                    Error_Image.setVisibility(View.VISIBLE);
                    Error_Button.setVisibility(View.VISIBLE);


                    //******************************************set error internet connection image*********************************
                }
            }
        });

        MySingleton.getInstance(this).addToJsonRequestQueue(jsonArrayRequest);
    }

    @Override
    public void onClick(View v) {
        if(CheckConnection.getInstance(this).getNetworkStatus()) {
            Error_Image.setVisibility(View.GONE);
            Error_Button.setVisibility(View.GONE);
            Error_message.setVisibility(View.GONE);
            orders_recyclerView.setVisibility(View.VISIBLE);
            callService();
        }
    }
}

