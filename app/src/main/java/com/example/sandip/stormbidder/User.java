package com.example.sandip.stormbidder;

import com.google.firebase.database.Exclude;

public class User {
    private String uName;
    private String eMail;
    private String mKey;
    public User()
    {

    }

    public User(String uName,String eMail)
    {
        this.uName=uName;
        this.eMail=eMail;
    }

    public String getuName() {
        return uName;
    }

    public String geteMail() {
        return eMail;
    }

    public void setuName( String uName ) {
        this.uName = uName;
    }

    public void seteMail( String eMail ) {
        this.eMail = eMail;
    }
    @Exclude
    public String getmKey() {
        return mKey;
    }

    @Exclude
    public void setmKey( String Key ) {
        this.mKey = Key;
    }
}
