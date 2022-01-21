package sample.Resources;

import sample.ConnectDB;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DataBaseDates extends ConnectDB {

    ConnectDB connectDB;
    private String dates = "";

    public DataBaseDates(String name){

        try {

            String sql = "Select LastAuctionedDate FROM auctiondata WHERE MeetMeName = ?";
            PreparedStatement statement =connectDB.connection.prepareStatement(sql);

            statement.setString(1,name);

            ResultSet resultSet = statement.executeQuery();


            if (resultSet.next()){

                dates = resultSet.getString("LastAuctionedDate");

            }
            System.out.println("LastAuctioned" + dates);


    }catch (SQLException e) {
            e.getSQLState();
        }
    }

    public String getDates(){
        return dates;
    }
}
