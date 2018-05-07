package com.health.pengfei.chestlearn2;

import java.io.Serializable;

public class Account implements Serializable {
    private String patientName;
    private boolean firstPic, secondPic, thirdPic;

    public Account(String patientName, boolean firstPic, boolean secondPic, boolean thirdPic){
        this.patientName = patientName;
        this.firstPic = firstPic;
        this.secondPic = secondPic;
        this.thirdPic = thirdPic;
    }

    public boolean equals(Object o) {
        if (o instanceof Account) {
            Account u = (Account) o;
            if (patientName == null) {
                return u.getPatientName() == null;
            } else {
                return patientName.equals(u.getPatientName());
            }
        } else {
            return false;
        }
    }
    public String getPatientName() {
        return this.patientName;
    }

    public boolean isFirstPic(){
        return this.firstPic;
    }

    public boolean isSecondPic(){
        return this.secondPic;
    }

    public boolean isThirdPic(){
        return this.thirdPic;
    }

    public void setPatientName(String patientName){
        this.patientName = patientName;
    }

    public void setFirstPic(boolean firstPic){
        this.firstPic = firstPic;
    }

    public void setSecondPic(boolean secondPic){
        this.secondPic = secondPic;
    }

    public void setThirdPic(boolean thirdPic){
        this.thirdPic = thirdPic;
    }

}
