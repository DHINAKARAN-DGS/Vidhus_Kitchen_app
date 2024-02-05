package com.daatstudios.vidhus_kitchen;

public class OrdersModel {

    String orderID, products, price, status, prescription, date,docID;

    public OrdersModel(String orderID, String products, String price, String status, String prescription, String date, String docID) {
        this.orderID = orderID;
        this.products = products;
        this.price = price;
        this.status = status;
        this.prescription = prescription;
        this.date = date;
        this.docID = docID;
    }

    public String getOrderID() {
        return orderID;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    public String getProducts() {
        return products;
    }

    public void setProducts(String products) {
        this.products = products;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPrescription() {
        return prescription;
    }

    public void setPrescription(String prescription) {
        this.prescription = prescription;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDocID() {
        return docID;
    }

    public void setDocID(String docID) {
        this.docID = docID;
    }
}
