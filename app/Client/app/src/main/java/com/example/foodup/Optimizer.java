package com.example.foodup;

import java.util.ArrayList;

public class Optimizer {

    //    Berechnet den durchschnittlichen Verbrauch eines Lebensmittels

    private double calcAverageWaste(Float amount,Float count){
        double averageWaste = amount/count; // Formel: Arithmetisches Mittel

        return averageWaste;
    }



    //    Überprüft, ob für mind. ein Produkt eine Optimierung vorliegt.
    //    Falls ein Produkt gefunden wurde bricht der Suchvorgang
    //    ab und eine Einkaufsliste kann optimiert werden.

    public boolean checkOptimizationInList(ArrayList<Product> productList, ArrayList<WastedFood> wastedFoods){
        boolean canBeOptimized = false;
        for(int i = 0; i < productList.size();i++){
            Product product = productList.get(i);

            // Bei einem gefundenen zu optimierenden Produkt, gilt die
            // Einkaufsliste als optimierbar und weitere Überprüfungen sind nicht nötig.
            if(checkOptimization(product,wastedGroceries)){
                canBeOptimized = true;
                break;
            }

        }
        return canBeOptimized;
    }


    //    Bezieht die Einträge eines Produktes innerhalb der Lebensmittelverschwendung
    //    und berechnet anschließend den Wert der durchschnittlichen Verschwendung

    public double getAverageWaste(Product product,ArrayList<WastedFood> wastedFoods){
        int count = 0;              // Anzahl der Einträge des verschwendeten Produkts.
        int wastedAmount = 0;       // Anzahl der Verschwendungen eines Produkts
        double averageWaste = 0;    // Durchschnittliche Verschwendung eines Produktes.

        // Überprüfung wieviele Einträge das Produkt in der
        // Lebensmittelverschwendung hat und die Menge.
        for(int i = 0;i < wastedFoods.size();i++){
            WastedFood singleEntry = wastedFoods.get(i);
            if(product.getId() == singleEntry.getId()){
                count += 1;
                wastedAmount += singleEntry.getWasted();
            }
        }
        // Falls das Produkt mind. ein Eintrag in der Lebensmittelverschwendung hat
        if(count > 0){

            averageWaste= calcAverageWaste((float)wastedAmount,(float)count); // Berechnung des durchschnittlichen Verbrauchs.
        }

        return averageWaste;
    }



    //    Überprüft ob für mind. ein Produkt eine Optimierung vorliegt.

    public boolean checkOptimization(Product product,ArrayList<WastedFood> wastedFoods) {
        boolean canBeOptimized = false;
        double averageWaste = getAverageWaste(product,wastedFoods);

        // Wenn die Anzahl des Produktes die durchschnittliche Verschwendung überschreitet
        if(product.getAmount() > averageWaste){
            canBeOptimized = true; // Produkt ist optimierbar
        }

        return canBeOptimized;
    }


    //

    public int getOptimizedAmount(int amount,double averageWaste){
        int optimizedAmount = 0;

        if(amount == Math.round(averageWaste)){
            optimizedAmount = (int) (amount - Math.floor(averageWaste));
        }
        else{
            optimizedAmount = (int) (amount - Math.round(averageWaste));
        }
        return optimizedAmount;

    }
}