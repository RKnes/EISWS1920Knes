package com.example.foodup;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.ContextMenu;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;



public class sglFoodListFragment extends Fragment {

    private ListView productListView;
    private int foodListID;
    private String foodListName;
    private ArrayList<Product> productList;
    private ArrayList<WastedFood> wastedFoods;
    private ProductListAdapter productListAdapter;

    private Optimizer optimizer = new Optimizer(); // Klasse mit Optimierungsfunktion -> Anwendungslogik

    public void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }


    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_singlefoodlist, container, false);
        productListView = v.findViewById(R.id.list_product);
        System.out.println("Test");
        setUI(v);
        return v;
    }




    // *******************************************************
    // Bereitet die Ansicht einer einzelnen Einkaufsliste vor
    // *******************************************************

    private void setUI(View v) {
        TextView foodListName = v.findViewById(R.id.foodlist_name);

        foodListName.setText(this.foodListName);

        productListAdapter = new ProductListAdapter();
        productListView.setAdapter(productListAdapter);

        // Ermöglicht ein Optionsmenü für jedes Produkt
        registerForContextMenu(productListView);
    }




    // ********************************************************************************************************
    //  Die Daten einer Einkaufsliste werden von der Einkaufslistenansicht übertragen und anschließend gesetzt
    // ********************************************************************************************************
    public void setFoodList(sglFoodList foodList, ArrayList<WastedFood> wastedFoods){
        this.foodListID = foodList.getID();
        this.foodListName = foodList.getName();
        this.productList = foodList.getProductList();
        this.wastedFoods = wastedFoods;
    }




    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo){
        super.onCreateContextMenu(menu, v, menuInfo);

        //Rendert das Optionsmenü
        getActivity().getMenuInflater().inflate(R.menu.food_option, menu);

        // Optimieren Auswahlmöglichkeit
        MenuItem optimizeSelection = menu.findItem(R.id.optimize);

        // Wird benötigt um auf die Position des geklickten Listitems zuzugreifen -> selected.position
        AdapterView.AdapterContextMenuInfo selected = (AdapterView.AdapterContextMenuInfo) menuInfo;

        // Nach Optimierung für das Produkt fragen
        if(optimizer.checkOptimization(productList.get(selected.position), wastedFoods)){
            optimizeSelection.setVisible(true);
        }
    }


    public boolean onContextItemSelected (MenuItem item){
        AdapterView.AdapterContextMenuInfo selectedProduct = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        Product product = productList.get(selectedProduct.position);
        double averageWaste = optimizer.getAverageWaste(product, wastedFoods);

        //Mit einem Switch gab es Probleme die IDs zu vergleichen
        if(item.getItemId() == R.id.delete){
            //Löschvorgang
        }
        
        if(item.getItemId() == R.id.optimize){
            //Optimierungsvorgang
            int optimizedAmount = optimizer.getOptimizedAmount(product.getAmount(), averageWaste);

            // Optimierungsvorgang -> PUT REQUEST an die API
            setOptimizedAmountForProduct(product, optimizedAmount);
        }
        if(item.getItemId() == R.id.edit){
            //Bearbeitungsvorgang
        }

        return super.onContextItemSelected(item);
    }




    // ****************************************
    // Aktualisieren des Wertes des Produktes
    // ****************************************
    public void setOptimizedAmountForProduct(Product product, int optimizedAmount){
        String url = "http://10.0.2.2:3000/user/1/einkaufsliste" + foodListID;   // API URL um den Wert zu aktualisieren

        RequestQueue queue = Volley.newRequestQueue(getActivity());

        product.setAmount(optimizedAmount);   // setzt den optimierten Wert
        productListAdapter.notifyDataSetChanged();  // sorgt für eine Aktualisierung der Adapter

        final JSONObject updatedEntry = new JSONObject();

        try {
            updatedEntry.put("lebensmittel", product.getID());
            updatedEntry.put("menge", optimizedAmount);

            JsonObjectRequest putRequest = new JsonObjectRequest(Request.Method.PUT, url, updatedEntry, new Response.Listener<JSONObject>(){
               public void onResponse(JSONObject response){
                   // Änderungen wurden vorgenommen
               }
            }, new Response.ErrorListener(){
                public void onErrorResponse(VolleyError err){
                    VolleyLog.d("ERROR", err.toString());
                }
            });
            queue.add(putRequest);
        }catch (JSONException e){
            e.printStackTrace();
        }
    }


    private class ProductListAdapter extends BaseAdapter {

        public int getCount(){
            return productList.size();
        }

        public Object getItem(int position){
            return null;
        }

        public long getItemId(int position){
            return 0; 
        }

        public View getView (int position, View view, ViewGroup parent){
            view = getLayoutInflater().inflate(R.layout.foodlist_entries, null); //setzt das Layout aktiv

            TextView productName = view.findViewById(R.id.sglProductName);
            TextView productBrand = view.findViewById(R.id.sglProductBrand);
            TextView productAmount = view.findViewById(R.id.sglProductAmount);
            TextView productImage = view.findViewById(R.id.sglProductImage);

            // graphische Optimierungselemente
            TextView sglProductOpt = view.findViewById(R.id.sglProductOpt);
            ImageView sglOptimizationSign = view.findViewById(R.id.sglOptimizationSign);

            if(optimizer.checkOptimization(productList.get(position), wastedFoods)){
                sglProductOpt.setVisibility(view.VISIBLE);
                sglOptimizationSign.setVisibility(view.VISIBLE);
            }

            productName.setText(productList.get(position).getName());
            productBrand.setText(productList.get(position).getBrand());
            productAmount.setText(Integer.toString(productList.get(position).getAmount()));
            return view;
        }
    }
}
