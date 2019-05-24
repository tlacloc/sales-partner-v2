package com.example.sales_partner.model;

public class Income {
    private String monthYear;
    private int sales;
    private int income;

    public String getMonthYear() {
        return monthYear;
    }

    public void setMonthYear(String monthYear) {
        this.monthYear = monthYear;
    }

    public int getSales() {
        return sales;
    }

    public void setSales(int sales) {
        this.sales = sales;
    }

    public int getIncome() {
        return income;
    }

    public void setIncome(int income) {
        this.income = income;
    }

    public String toString() {
        return getMonthYear() + " \n" +
                "Ingresos: " + getSales() + "\n" +
                "Ventas: " + getIncome() + "\n" ;
    }



}

