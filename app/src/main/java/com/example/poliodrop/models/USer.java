package com.example.poliodrop.models;

public class USer {

    private String id,name,email,phone,area,city;

    public USer() {
    }

    public USer(String id, String name, String email, String phone, String area, String city) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.area = area;
        this.city = city;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
