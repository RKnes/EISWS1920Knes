package com.example.foodup;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements FoodListFragment.FoodListFragmentListener {

    String[] names = {"Milch", "Banane"};
    private sglFoodListFragment sglFoodFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNav = findViewById(R.id.menu_main);

        bottomNav.setOnNavigationItemSelectedListener(navListener);

        sendTokenToServer();
    }

    //Navigation Listener
    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            Fragment selectedFragment = null;

            switch (menuItem.getItemId()){
                case R.id.nav_food:  selectedFragment = new FoodListFragment();
                                        break;
                case R.id.nav_capture:  selectedFragment = new CaptureFragment();
                                        break;
            }
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).addToBackStack("tag").commit();

            return true;
        }
    };


    public void passFoodList(sglFoodList foodList, ArrayList<WastedFood> wastedFoods){
        sglFoodFragment = new sglFoodListFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, sglFoodFragment).addToBackStack("tag").commit();
        sglFoodFragment.setFoodList(foodList, wastedFoods);
    }

    private void sendTokenToServer() {
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                // Token wird für den Benutzer generiert, für den Prototypen muss dieser
                // dann an den Server geschickt werden um das Firebase Messaging ermöglichen zu können
                String deviceToken = instanceIdResult.getToken();

                String url = "http://10.0.2.2:3000/user/1/token";   // API URL um den Wert zu aktualisieren
                RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
                final JSONObject token = new JSONObject();
                try{
                    token.put("token", deviceToken);

                    JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST, url, token, new Response.Listener<JSONObject>(){
                        public void onResponse(JSONObject response){
                            // Token wurde erfolgreich übertragen
                        }
                    }, new Response.ErrorListener(){
                        public void onErrorResponse(VolleyError err){
                            VolleyLog.d("ERROR", err.toString());
                        }
                    });
                    queue.add(postRequest);
                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
        });
    }


}
