package sample;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AuctionList {

    private String name;
    private String number;
    private String sold_for;
    private String bidder;

    private String date;


    private ConnectDB connectDB;

    public AuctionList(){

    }

    public AuctionList(String number, String name){

        this.number = number;
        this.name = name;

    }

    public AuctionList(String name, String sold_for,Date date,String bidder){
        DateFormat df = new SimpleDateFormat("MM-dd-yyyy");

        this.name = name;
        this.sold_for = sold_for;
        this.date = df.format(date.toString());
        this.bidder = bidder;
    }

    public AuctionList(String number, String name, String sold_for,String bidder){
        this.number = number;
        this.name = name;
        this.sold_for = sold_for;
        this.bidder = bidder;
    }


    public String getNumber(){
        return number;
    }

    public String getName(){
        return name;
    }

    public String getSold_for(){
        return sold_for;
    }

    public String getBidder(){return bidder;}


}
