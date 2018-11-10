package com.example.sandip.stormbidder;

import com.google.firebase.database.Exclude;

public class Upload {
    private String mName,mDescription,mUserName,mDate,mStatus,mBidder;
    private String mImageUrl;
    private String mKey;
    private double mBid,mPrice;


    public Upload()
    {
        //EMPTY CONSTRUCTOR NEEDED
    }
    public Upload(String name, String url,String description,double price,double bid,String userName,String date,String status,String bidder)
    {
        if(name.trim().equals(""))
        {
            name="No Name";
        }
        mName=name;
        mImageUrl=url;
        mDescription=description;
        mPrice=price;
        mBid=bid;
        mUserName=userName;
        mDate=date;
        mStatus=status;
        mBidder=bidder;
    }

    public String getmBidder() {
        return mBidder;
    }

    public void setmBidder( String mBidder ) {
        this.mBidder = mBidder;
    }

    public String getmStatus() {
        return mStatus;
    }

    public void setmStatus( String mStatus ) {
        this.mStatus = mStatus;
    }

    public String getmDate() {
        return mDate;
    }

    public void setmDate( String mDate ) {
        this.mDate = mDate;
    }

    public String getmName() {
        return mName;
    }

    public String getmImageUrl() {
        return mImageUrl;
    }

    public String getmDescription() {
        return mDescription;
    }

    public double getmPrice() {
        return mPrice;
    }

    public String getmUserName() {
        return mUserName;
    }

    public double getmBid() {
        return mBid;
    }

    public void setmName( String mName ) {
        this.mName = mName;
    }

    public void setmImageUrl( String mImageUrl ) {
        this.mImageUrl = mImageUrl;
    }

    @Exclude
    public String getmKey() {
        return mKey;
    }

    @Exclude
    public void setmKey( String Key ) {
        this.mKey = Key;
    }

    public void setmDescription( String mDescription ) {
        this.mDescription = mDescription;
    }

    public void setmPrice( double mPrice ) {
        this.mPrice = mPrice;
    }

    public void setmUserName( String mUserName ) {
        this.mUserName = mUserName;
    }

    public void setmBid( double mBid ) {
        this.mBid = mBid;
    }
}
