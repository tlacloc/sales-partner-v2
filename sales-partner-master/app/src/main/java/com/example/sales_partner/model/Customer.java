package com.example.sales_partner.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.databinding.BaseObservable;
import android.databinding.Bindable;

import java.io.Serializable;
import java.util.Objects;

@Entity(tableName="customers")
public class Customer extends BaseObservable implements Serializable {
    @PrimaryKey
    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "first_name")
    private String firstName;

    @ColumnInfo(name = "last_name")
    private String lastName;

    @ColumnInfo(name = "address")
    private String address;

    @ColumnInfo(name = "phone1")
    private String phone1;

    @ColumnInfo(name = "phone2")
    private String phone2;

    @ColumnInfo(name = "phone3")
    private String phone3;

    @ColumnInfo(name = "e_mail")
    private String email;

    // CONSTRUCTORS

    public Customer() {
    }


    // GETTERS AND SETTERS

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    @Bindable
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone1() {
        String p = "";
        if(this.phone1!=null && !this.phone1.isEmpty())
            p = this.phone1;
        return p;
    }

    public void setPhone1(String phone1) {
        this.phone1 = phone1;
    }

    public String getPhone2() {
        return phone2;
    }

    public void setPhone2(String phone2) {
        this.phone2 = phone2;
    }

    public String getPhone3() {
        return phone3;
    }

    public void setPhone3(String phone3) {
        this.phone3 = phone3;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Customer(int id, String firstName, String lastName){
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String toString(){
        return this.lastName + " " + this.firstName + " " +
                this.getPhone1() + " " + this.getEmail();
    }

    // IMPLEMENT EQUALS

    @Override
    public int hashCode() {
        String s = id+firstName+lastName;
        return Objects.hash(s);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Customer customer = (Customer) o;
        return id == customer.id &&
                firstName.equals(customer.firstName) &&
                lastName.equals(customer.lastName) ;
    }
}