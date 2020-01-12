package com.example.foodup;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class sglFoodListFragment {

    private ListView productListView;
    private int foodListID;
    private String foodListName;
    private ArrayList<Product> productList;
    private ArrayList<WastedFood> wastedFoods;
    private ProductListAdapter productListAdapter;


    public void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_singlefoodlist, container, false);
        productListView = v.findViewById(R.id.list_product);
        System.out.println("Test");
        setUI(v);
        return v;
    }


    //Bereitet die Ansicht einer einzelnen Einkaufsliste vor
    private void setUI(View v) {
        TextView foodListName = v.findViewById(R.id.foodlist_name);

        foodListName.setText(this.foodListName);

        productListAdapter = new ProductListAdapter();
        productListView.setAdapter(productListAdapter);

        registerForContextMenu(productListView);
    }

    //Die Daten einer Einkaufsliste werden von der Einkaufslistenansicht übertragen und anschließend gesetzt
    public void setFoodList(sglFoodList foodList, ArrayList<WastedFood> wastedFoods){
        this.foodListID = foodList.getID();
        this.foodListName = foodList.getName();
        this.productList = foodList.getProductList();
        this.wastedFoods = wastedFoods;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo){
        super.onCreateContextMenu(menu, v, menuInfo);

        //Rendert das Optionsmenü
        getActivity().getMenuInflater().inflate(R.menu.food_option, menu);

        // Wird benötigt um auf die Position des geklickten Listitems zuzugreifen -> selected.position
        AdapterView.AdapterContextMenuInfo selected = (AdapterView.AdapterContextMenuInfo) menuInfo;
    }

    @Override
    public boolean onContextItemSelected (MenuItem item){
        AdapterView.AdapterContextMenuInfo selectedProduct = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        Product product = productList.get(selectedProduct.position);

        //Mit einem Switch gab es Probleme die IDs zu vergleichen
        if(item.getItemID() == R.id.delete){
            //Löschvorgang
        }
        
        if(item.getItemid() == R.id.optimize){
            //Optimierungsvorgang
        }
        if(item.getItemId() == R.id.edit){
            //Bearbeitungsvorgang
        }

        return super.onContextItemSelected(item);
    }



    // Aktualisieren des Wertes des Produktes
    public void setOptimizedAmountForProduct(){

    }


    private class ProductListAdapter {

        @Override
        public int getCount(){
            return productList.size();
        }

        @Override
        public Object getItem(int position){
            return null;
        }

        @Override
        public long getItemId(int position){
            return 0; 
        }

        @Override
        public View getView (int position, View view, ViewGroup parent){
            view = getLayoutInflater().inflate(R.layout.foodlist_entries, null); //setzt das Layout aktiv

            

        }

    }
    


}
