package com.example.sales_partner.model;

public class SalesData {
    public String monthYear;
    public int sales;
    public int income;


    public String assemblyDescription;
    public int assemblyId;

    public String toString() {
        return monthYear + " \n" +
            "ventas: " + sales + "\n" +
            "Ingresos: $" + income ;
    }



}

