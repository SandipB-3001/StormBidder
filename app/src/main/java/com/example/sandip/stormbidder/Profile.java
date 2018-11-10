package com.example.sandip.stormbidder;

import com.google.firebase.database.Exclude;

public class Profile {
    String name,emailid,address,phno,dob;
    public Profile()
    {

    }

    public Profile( String name, String emailid, String address, String phno, String dob ) {
        this.name = name;
        this.emailid = emailid;
        this.address = address;
        this.phno = phno;
        this.dob = dob;
    }

    public String getName() {
        return name;
    }

    public String getEmailid() {
        return emailid;
    }

    public String getAddress() {
        return address;
    }

    public String getPhno() {
        return phno;
    }

    public String getDob() {
        return dob;
    }

    public void setName( String name ) {
        this.name = name;
    }

    public void setEmailid( String emailid ) {
        this.emailid = emailid;
    }

    public void setAddress( String address ) {
        this.address = address;
    }

    public void setPhno( String phno ) {
        this.phno = phno;
    }

    public void setDob( String dob ) {
        this.dob = dob;
    }
}
