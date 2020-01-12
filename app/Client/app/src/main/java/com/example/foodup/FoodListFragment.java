package com.example.foodup;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class FoodListFragment  extends Fragment {
    private FoodListFragmentListener listener; // Um später eine einzelne Einkaufsliste beziehen zu können.
    private String fragmentTitle = "Einkaufsliste"; // Titel der aktuellen Ansicht
    private ListView mListView;
    private ArrayList<sglFoodList> sglFoodLists = new ArrayList<sglFoodList>();
    private ArrayList<WastedFood> wastedFoods = new ArrayList<WastedFood>();

    public interface FoodListFragmentListener {
        void passFoodList(sglFoodList foodList, ArrayList<WastedFood> wastedFoods);
    }

    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_foodlist, container, false);
        TextView sub_title = getActivity().findViewById(R.id.FoodUP);
        sub_title.setText(fragmentTitle);

        mListView = v.findViewById(R.id.foodList);

        setUI();

        return v;
    }



    // ************************************************
    //    setUI
    //    Bereitet die Ansicht der Einkaufslisten vor
    // ************************************************

    private void setUI() {
        if (sglFoodLists.size() < 1 && wastedFoods.size() < 1) { // Falls die Daten nicht ausgefüllt sind werden die Daten bezogen

            // "Ladebildschirm", bis die Daten bezogen wurden
            final ProgressDialog pDialog = CustomDialog.showCustomProgressDialog(getActivity(), "Einkaufslisten werden geladen");

            // API URL zum beziehen der Einkaufslisten
            String url = "http://10.0.2.2:3000/user/1/einkaufsliste";



            // Aufbau des HTTP Request
            JsonArrayRequest jsonRequest = new JsonArrayRequest(url, new Response.Listener<JSONArray>(){
                @Override
                public void onResponse(JSONArray response){
                    try{
                        JSONArray foodLists = response;

                        // Empfangene JSON parsen
                        for(int i=0; i<foodLists.length(); i++){

                            // Zwischenspeicherung eines einzelnen JSONObjektes
                            JSONObject sglFoodList = foodLists.getJSONObject(i);

                            // Einkaufslisten Klasse wird erstellt
                            sglFoodList foodListClass = new sglFoodList();

                            // Attribute der Klasse werden gefüllt
                            foodListClass.setName(sglFoodList.getString("name"));
                            foodListClass.setID(sglFoodList.getInt("id"));
                            foodListClass.setPosts(sglFoodList.getJSONArray("products").length());

                            // Liste von der Klasse Products wird deklariert
                            ArrayList<Product> productArrayList = new ArrayList<Product>();

                            // Produkte innerhalb der Einkaufsliste parsen
                            for(int j=0; j<sglFoodList.getJSONArray("products").length(); j++){
                                // temporäres Speichern des Produktes aus dem Request
                                JSONObject requestedProduct = sglFoodList.getJSONArray("products").getJSONObject(j);

                                // Für jedes Produkt wird eine Klasse erstellt
                                Product sglProduct = new Product();
                                sglProduct.setID(requestedProduct.getInt("lebensmittelID"));
                                sglProduct.setName(requestedProduct.getString("bezeichnung"));
                                sglProduct.setBrand(requestedProduct.getString("marke"));
                                sglProduct.setAmount(requestedProduct.getInt("menge"));
                                sglProduct.setImagePath(requestedProduct.getString("imagePath"));
                                productArrayList.add(sglProduct);
                            }
                            foodListClass.setProductList(productArrayList);

                            // Die zuvor global deklarierte Arraylist wird mit den einzelnen Einkaufslisten gefüllt
                            sglFoodLists.add(foodListClass);
                        }

                        //Nachdem die Einkaufslisten mit ihren Produkten geladen wurden, werden die Einträge der verschwendeten Lebensmittel bezogen
                        requestWastedFoods(pDialog);
                    }catch(JSONException e){
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener(){

                public void onErrorResponse(VolleyError err){}
            });

            //Request wird ausgelöst
            Volley.newRequestQueue(getActivity()).add(jsonRequest);
        }else{
            setListView();
        }
    }


    // Adapter um die Listen zu füllen.
    class CustomAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return sglFoodLists.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            view = getLayoutInflater().inflate(R.layout.food_list, null); // setzt das Layout aktiv
            TextView textView_description = view.findViewById(R.id.description_name);
            TextView textView_amount = view.findViewById(R.id.amount);
            TextView textView_amountLabel = view.findViewById(R.id.amount_label);
            Optimizer optimizer = new Optimizer();

            // Überprüft, ob innerhalb der Einkaufslisten Optimierungen vorliegen könnten.
            if (optimizer.checkOptimizationInList(sglFoodLists.get(position).getProductList(), wastedFoods)) {
                setOptimizationText(view);
            }

            textView_description.setText(sglFoodLists.get(position).getName()); // Setzt den Namen der entnommenen Einkaufsliste.
            textView_amount.setText(Integer.toString(sglFoodLists.get(position).getPosts())); // Setzt die Menge der Einträge.


            if (sglFoodLists.get(position).getPosts() < 2) {
                textView_amountLabel.setText("Eintrag");
            }


            return view;
        }
    }



    // **********************************************************************
    //    setOptimizationText
    //    Zeigt den vordefinierten Text an, falls eine Optimierung vorliegt
    // **********************************************************************
    private void setOptimizationText (View v){
        TextView optimizationText = v.findViewById(R.id.listItemInfo);
        optimizationText.setVisibility(v.VISIBLE);
    }


    private void setListView(){
        // Die Listview wird mit den Einkaufslisten gefüllt
        CustomAdapter customAdapter = new CustomAdapter();
        mListView.setAdapter(customAdapter);

        // List Items mit einem onItemClickListener versehen
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(getActivity(),sglFoodLists.get(position).getName(),Toast.LENGTH_SHORT).show();
                listener = (FoodListFragmentListener) getActivity();
                // Daten einer einzelnen Einkaufsliste übertragen
                listener.passFoodList(sglFoodLists.get(position), wastedFoods);

            }
        });
    }


    // *******************************************************************************
    // request Wasted Foods
    // Dokumentationseinträge des Benutzers (weggeworfene Lebensmittel) werden aus
    // der Datenbank entnommen und gepspeichert
    // *******************************************************************************

    private void requestWastedFoods(final ProgressDialog pDialog){
        String url = "http://10.0.2.2:3000/user/1/verLebensmittel";

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url ,new Response.Listener<JSONArray>(){
            @Override
            public void onResponse(JSONArray response){
                try{
                    for(int i=0; i<response.length(); i++){
                        WastedFood food = new WastedFood();
                        food.setId(response.getJSONObject(i).getInt("id_fkLebensmittel"));
                        food.setWastedFood(response.getJSONObject(i).getInt("verschwendet"));

                        wastedFoods.add(food);
                    }
                    pDialog.dismiss();  // "Ladebildschirm" schließt, sobald die Daten befüllt wurden
                    setListView();
                }catch(JSONException e){
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error){}
        });
        Volley.newRequestQueue(getActivity()).add(jsonArrayRequest);
    }
}


