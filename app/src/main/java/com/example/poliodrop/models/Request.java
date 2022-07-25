package com.example.poliodrop.models;

public class Request {
    private String address, area, assignedId, city, contact, date,
            email, isAssigned, parentName, noChild, phone, requestId, status, userId, userApproval, workerApproval,
            worker, lastVaccination, nextScheduleDate, nextScheduleTime;

    public Request(String address, String area, String assignedId, String city, String contact, String date, String email,
                   String isAssigned, String parentName, String noChild, String phone, String requestId, String status,
                   String userId, String userApproval, String workerApproval, String worker, String lastVaccination, String nextScheduleDate,
                   String nextScheduleTime) {
        this.address = address;
        this.area = area;
        this.assignedId = assignedId;
        this.city = city;
        this.contact = contact;
        this.date = date;
        this.email = email;
        this.isAssigned = isAssigned;
        this.parentName = parentName;
        this.noChild = noChild;
        this.phone = phone;
        this.requestId = requestId;
        this.status = status;
        this.userId = userId;
        this.userApproval = userApproval;
        this.workerApproval = workerApproval;
        this.worker = worker;
        this.lastVaccination = lastVaccination;
        this.nextScheduleDate = nextScheduleDate;
        this.nextScheduleTime = nextScheduleTime;
    }

    public Request() {
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getAssignedId() {
        return assignedId;
    }

    public void setAssignedId(String assignedId) {
        this.assignedId = assignedId;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    public String getNoChild() {
        return noChild;
    }

    public void setNoChild(String noChild) {
        this.noChild = noChild;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getIsAssigned() {
        return isAssigned;
    }

    public void setIsAssigned(String isAssigned) {
        this.isAssigned = isAssigned;
    }

    public String getUserApproval() {
        return userApproval;
    }

    public void setUserApproval(String userApproval) {
        this.userApproval = userApproval;
    }

    public String getWorkerApproval() {
        return workerApproval;
    }

    public void setWorkerApproval(String workerApproval) {
        this.workerApproval = workerApproval;
    }

    public String getWorker() {
        return worker;
    }

    public void setWorker(String worker) {
        this.worker = worker;
    }

    public String getLastVaccination() {
        return lastVaccination;
    }

    public void setLastVaccination(String lastVaccination) {
        this.lastVaccination = lastVaccination;
    }

    public String getNextScheduleDate() {
        return nextScheduleDate;
    }

    public void setNextScheduleDate(String nextScheduleDate) {
        this.nextScheduleDate = nextScheduleDate;
    }

    public String getNextScheduleTime() {
        return nextScheduleTime;
    }

    public void setNextScheduleTime(String nextScheduleTime) {
        this.nextScheduleTime = nextScheduleTime;
    }
}
