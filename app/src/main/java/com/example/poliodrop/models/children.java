package com.example.poliodrop.models;

public class children {

    private String name,age, childNumber;

    public children (String name , String age, String childNumber){
        this.name = name;
        this.age = age;
        this.childNumber = childNumber;
    }

    public children() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getChildNumber() {
        return childNumber;
    }

    public void setChildNumber(String childNumber) {
        this.childNumber = childNumber;
    }
}
