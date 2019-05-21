package com.mdd.ecglove;

public class UserData {
    private String name;
    private String email;
    private String DOB;
    private String sex;
    private String phone;
    private String heartCondition;
    private String clinic;
    private String clinician;

    public String getHeartConditions() {
        return heartCondition;
    }

    public void setHeartConditions(String heartConditions) {
        this.heartCondition = heartConditions;
    }

    public String getClinic() {
        return clinic;
    }

    public void setClinic(String clinic) {
        this.clinic = clinic;
    }

    public String getClinician() {
        return clinician;
    }

    public void setClinician(String clinician) {
        this.clinician = clinician;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }


    public String getDOB() {
        return DOB;
    }

    public String getSex() {
        return sex;
    }

    public void setDOB(String DOB) {
        this.DOB = DOB;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public void setEmail(String mEmail) {
        email = mEmail;
    }

    public void setName(String mName) {
        name = mName;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }


    public void UserData() {
    }
}

