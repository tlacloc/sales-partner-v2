package com.example.sales_partner.model;

public class NeededProducts {
    private int orderId;
    private int statusId;
    private int customerId;
    private int assembly_id;
    private int assembliesQty;
    private int productId;
    private int productQtyPerAssembly;
    private int pendingProductsQty;
    private int stockProductsQty;
    private String productDescription;
    private int diffProductsQty;

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getStatusId() {
        return statusId;
    }

    public void setStatusId(int statusId) {
        this.statusId = statusId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public int getAssembly_id() {
        return assembly_id;
    }

    public void setAssembly_id(int assembly_id) {
        this.assembly_id = assembly_id;
    }

    public int getAssembliesQty() {
        return assembliesQty;
    }

    public void setAssembliesQty(int assembliesQty) {
        this.assembliesQty = assembliesQty;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getProductQtyPerAssembly() {
        return productQtyPerAssembly;
    }

    public void setProductQtyPerAssembly(int productQtyPerAssembly) {
        this.productQtyPerAssembly = productQtyPerAssembly;
    }

    public int getPendingProductsQty() {
        return pendingProductsQty;
    }

    public void setPendingProductsQty(int pendingProductsQty) {
        this.pendingProductsQty = pendingProductsQty;
    }

    public int getStockProductsQty() {
        return stockProductsQty;
    }

    public void setStockProductsQty(int stockProductsQty) {
        this.stockProductsQty = stockProductsQty;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    public int getDiffProductsQty() {
        return diffProductsQty;
    }

    public void setDiffProductsQty(int diffProductsQty) {
        this.diffProductsQty = diffProductsQty;
    }

    public String toString() {
        int descriptionLength =  20;
        String desc = "";
        if(this.getProductDescription().length() > descriptionLength)
            desc = this.getProductDescription().substring(0,descriptionLength);
        else desc = this.getProductDescription();

        return "Producto: " +  "\n" +
                desc + " \n" +
                "productos pedidos: " + this.getPendingProductsQty() + "\n" +
                "productos stock: " + this.getStockProductsQty() + "\n" +
                "faltan: " + this.getDiffProductsQty();
    }



}

