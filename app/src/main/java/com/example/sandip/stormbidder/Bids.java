package com.example.sandip.stormbidder;

public class Bids
{
    private String itemId,userId,biddingDate;
    private double latestBid;
    public Bids()
    {

    }
    public Bids(String itemid,String userid,String biddingdate,double bid)
    {
        itemId=itemid;
        userId=userid;
        biddingDate=biddingdate;
        latestBid=bid;
    }

    public String getItemId() {
        return itemId;
    }

    public String getUserId() {
        return userId;
    }

    public String getBiddingDate() {
        return biddingDate;
    }

    public double getLatestBid() {
        return latestBid;
    }

    public void setItemId( String itemId ) {
        this.itemId = itemId;
    }

    public void setUserId( String userId ) {
        this.userId = userId;
    }

    public void setBiddingDate( String biddingDate ) {
        this.biddingDate = biddingDate;
    }

    public void setLatestBid( double latestBid ) {
        this.latestBid = latestBid;
    }
}
