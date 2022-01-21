package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TextField;
import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class ConnectDB{

    protected static Connection connection = null;

    static String databaseName = "auctionlist";
    static String url = "jdbc:mysql://localhost:3306/" + databaseName;
    static String username = "root";
    static String password = "Curt1020$";

    private PreparedStatement query;
    private PreparedStatement bidderStatement;

    private String profileName = "";
    private int profileBidCount = 0;
    private String profileLastBidDate = "";
    private int profileAuctionedCount = 0;
    private String LastAuctionedDate = "";

    private int bCount;
    private int aCount;
    private String newPerson;

    List<String> list = new ArrayList<>();
    List<String> bList = new ArrayList<>();

    ObservableList<String> nameList = null;
    ObservableList<String> numberList;
    ResultSet rs = null;

    ObservableList<String> bidderNameList = FXCollections.observableArrayList();

    private String auctionDates = "";
    private String bidDates = "";
    private String previousBidDate = "";
    private String previousAuctionDate = "";
    private int previousAuctionCount = 0;

    static boolean inDatabase = false;

    private static final String INSERT_DATA_SQL = "INSERT INTO auctiondata"
            +"(MeetMeName,Bidder,LastBidDate,Auctioned,LastAuctionedDate)VALUES" + "(?,?,?,?,?);";

    private static final String UPDATE_LIST_DATA_SQL = "UPDATE list SET AuctionName = ? WHERE AuctionNumber = ?";

    private String sqlUpdate = "UPDATE auctiondata SET Bidder = '0', Auctioned = '0' WHERE MeetMeName = ?";

    private String view = "SELECT * FROM list";

    private String displayUpdate = "UPDATE list SET SoldFor = ?, Bidder = ? WHERE AuctionNumber = ?";

    private String updatePreviousBidDate = "UPDATE auctiondata SET PreviousBidDate = ? WHERE MeetMeName = ?";

    private String selectPreviousDate = "Select PreviousBidDate FROM auctiondata WHERE MeetMeName = ?";

    private String selectPreviousAuctionDate = "Select PreviousAuctionDate FROM auctiondata WHERE MeetMeName = ?";

    private String updatePreviousAuctionDate = "UPDATE auctiondata SET PreviousAuctionDate = ? WHERE MeetMeName = ?";

    private String updatePreviousAuctionCount = "UPDATE auctiondata SET PreviousAuctionCount = ? WHERE MeetMeName = ?";

    private String selectPreviousAuctionCount = "Select PreviousAuctionCount FROM auctiondata WHERE MeetMeName = ?";

    private String insertIntoBidderList = "INSERT INTO bidders(BiddersName) VALUE" + "(?); ";

    private String bidderList = "SELECT BiddersName From bidders ORDER BY BiddersName";

    private String remove = "DELETE FROM bidders WHERE BiddersName = ? AND BidderCount > 0";

    private String profileNameList = "Select MeetMeName FROM auctiondata";

    private String insertBidderData = "INSERT INTO bidderdata(BidderName,Amount,DateSold,AuctionName,AuctionNumber)" +
            "VALUE" + "(?,?,?,?,?);";

    private String selectBidderRecords = "SELECT Bidder, SoldFor, AuctionName FROM list";

    private String updateBidderRecords = "UPDATE bidderdata SET BidderName = ?, Amount = ?, DateSold = ?, " +
            "AuctionName = ? " +
            "WHERE DateSold = ? AND AuctionNumber = ?";

    private String updateTableInfo = "UPDATE list SET AuctionName = ?, SoldFor = ?, Bidder = ? WHERE AuctionNumber > 0";

    private String updateBidderInfo = "DELETE FROM bidders WHERE BiddersName = ? AND BidderCount > 0";



    public ConnectDB(){

        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(url,username,password);
        }
        catch (Exception e){
            e.printStackTrace();
        }

        setDatabaseList();
    }

    public void update(String name, int bidder,String bidDate, int auctioned, String auctionedDate ) {
        try {

           String sqlStatement = "UPDATE auctiondata SET Bidder = ?, LastBidDate = ?, Auctioned = ?," +
                   "LastAuctionedDate = ? WHERE MEETMENAME = '"+name+"'";

           PreparedStatement statement = ConnectDB.connection.prepareStatement(sqlStatement);

           statement.setInt(1,bidder);
           statement.setString(2,bidDate);
           statement.setInt(3, auctioned);
           statement.setString(4,auctionedDate);
           statement.execute();
           statement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void setInsert(TextField name, int bidderCount, String bidDate, int auctionedCount, String auctionedDate){

        try {
            query = connection.prepareStatement(INSERT_DATA_SQL, PreparedStatement.RETURN_GENERATED_KEYS);

            query.setString(1,name.getText());
            query.setInt(2,bidderCount);
            query.setString(3,bidDate);
            query.setInt(4,auctionedCount);
            query.setString(5,auctionedDate);

            newPerson = name.getText();
            query.executeUpdate();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }


    public void DatabaseAuctionDates(String name){

        try {

            String sql = "Select LastAuctionedDate FROM auctiondata WHERE MeetMeName = ?";
            PreparedStatement statement = ConnectDB.connection.prepareStatement(sql);

            statement.setString(1,name);

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()){
                auctionDates = resultSet.getString("LastAuctionedDate");
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public String getAuctionDate(){
        return auctionDates;
    }

    public void setPreviousAuctionDate(){

        try {
            PreparedStatement statement = ConnectDB.connection.prepareStatement(updatePreviousAuctionDate);

            statement.setString(1,auctionDates);
            statement.setString(2,profileName);

            statement.executeUpdate();
            statement.close();

        }catch (SQLException sqlException){
            sqlException.getSQLState();
        }

    }

    public String getPreviousAuctionDate(){

        try {

            PreparedStatement statement =ConnectDB.connection.prepareStatement(selectPreviousAuctionDate);

            statement.setString(1,profileName);

            ResultSet resultSet = statement.executeQuery();


            if (resultSet.next()){
                previousAuctionDate = resultSet.getString("PreviousAuctionDate");
            }
        }catch (SQLException sqlException){
            sqlException.getSQLState();
        }
        return previousAuctionDate;
    }


    public void DatabaseBidDates(String name){

        try {

            String sql = "Select LastBidDate FROM auctiondata WHERE MeetMeName = ?";
            PreparedStatement statement =ConnectDB.connection.prepareStatement(sql);

            statement.setString(1,name);

            ResultSet resultSet = statement.executeQuery();


            if (resultSet.next()){
                bidDates = resultSet.getString("LastBidDate");
            }
        }catch (SQLException e) {
            e.getSQLState();
        }
    }

    public String getBidDates(){
        return bidDates;
    }


    public void setPreviousBidDate(){

        try {

            PreparedStatement statement = ConnectDB.connection.prepareStatement(updatePreviousBidDate);

            statement.setString(1,bidDates);
            statement.setString(2,profileName);

            statement.executeUpdate();
            statement.close();

        }catch (SQLException sqlException){
            sqlException.getSQLState();
        }

    }

    public String getPreviousBidDate(){
        try {

            PreparedStatement statement =ConnectDB.connection.prepareStatement(selectPreviousDate);

            statement.setString(1,profileName);

            ResultSet resultSet = statement.executeQuery();


            if (resultSet.next()){

                previousBidDate = resultSet.getString("PreviousBidDate");

            }
        }catch (SQLException sqlException){
            sqlException.getSQLState();
        }

        return previousBidDate;
    }

    public void setPreviousAuctionCount(String name){

        try {

            PreparedStatement statement = ConnectDB.connection.prepareStatement(updatePreviousAuctionCount);
            statement.setInt(1,profileAuctionedCount);
            statement.setString(2,name);

            statement.executeUpdate();
            statement.close();

        }catch (SQLException sqlException){
            sqlException.getSQLState();
        }
    }

    public int getPreviousAuctionCount(){

        try {
            PreparedStatement statement =ConnectDB.connection.prepareStatement(selectPreviousAuctionCount);
            statement.setString(1,profileName);


            ResultSet resultSet = statement.executeQuery();


            if (resultSet.next()){
                previousAuctionCount = resultSet.getInt("PreviousAuctionCount");
            }
        }catch (SQLException sqlException){
            sqlException.getSQLState();
        }
        return previousAuctionCount;
    }


    //Gets Information From DataBase
    public void Auction(String name){

        try {
            String sql = "Select *  FROM auctiondata WHERE MeetMeName = ?";
            PreparedStatement statement =ConnectDB.connection.prepareStatement(sql);

            statement.setString(1,name);

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()){
                profileAuctionedCount = resultSet.getInt("Auctioned");
                profileName = resultSet.getString("MeetMeName");
                profileBidCount = resultSet.getInt("Bidder");
                profileLastBidDate = resultSet.getString("LastBidDate");
                LastAuctionedDate = resultSet.getString("LastAuctionedDate");
            }
            setaCount();
            setbCount();
        }catch (SQLException e) {
            e.getSQLState();
        }
    }

    public List getProfileName(){

        try {
            PreparedStatement statement =ConnectDB.connection.prepareStatement(profileNameList);

            ResultSet resultSet = statement.executeQuery();


            while(resultSet.next()){
                String name = resultSet.getString("MeetMeName");

                list.add(name);
            }


        }catch (SQLException sqlException){
            sqlException.getSQLState();
        }
        return list;
    }

    public void reset(){

        try {
            PreparedStatement preparedStatement = ConnectDB.connection.prepareStatement(sqlUpdate);
            preparedStatement.setString(1,profileName);

            preparedStatement.close();

        }catch (SQLException sqlEx){
            sqlEx.getSQLState();
        }
    }


    public void setaCount(){
        aCount = profileAuctionedCount;
    }

    public int getaCount(){
        return aCount;
    }

    public void setbCount(){
        bCount = profileBidCount;
    }

    public int getbCount(){
        return bCount;
    }
    

    public void setListOrder(TextField textField){

        if(inDatabase == false){
            profileName = newPerson;
        }

        if(Controller.nameReset ){
            profileName = "EMPTY";
            Controller.nameReset = false;
        }


        try {
            PreparedStatement statement = ConnectDB.connection.prepareStatement(UPDATE_LIST_DATA_SQL);

            statement.setString(1,profileName);
            statement.setString(2,textField.getText());
            statement.execute();
            statement.close();

        }catch (SQLException sqlE){
            sqlE.getSQLState();
        }
    }

    public void setDatabaseList(){

        try {
            PreparedStatement statement = ConnectDB.connection.prepareStatement(view);
            rs = statement.executeQuery();

            nameList = FXCollections.observableArrayList();
            numberList = FXCollections.observableArrayList();
        }catch (SQLException sqlException){
            sqlException.getSQLState();
        }
    }

    public ResultSet getResultset(){
        return rs;
    }


    public void displayScreenUpdate(String number, String sold, String bidder){

        try {
            PreparedStatement statement = ConnectDB.connection.prepareStatement(displayUpdate);
            statement.setString(1,sold + "K");
            statement.setString(2,bidder);
            statement.setString(3,number);
            statement.execute();
            statement.close();

        }catch (SQLException sqlException){
            sqlException.getSQLState();
        }
    }

    public void setBidderName(TextField textField){

        try {
            bidderStatement = ConnectDB.connection.prepareStatement(insertIntoBidderList);
            bidderStatement.setString(1,textField.getText());
            bidderStatement.execute();
            bidderStatement.close();

        }catch (SQLException sqlException){
            sqlException.getSQLState();
        }
    }

    public List<String> getBidderName(){

        bList.clear();

        try {
            PreparedStatement statement =ConnectDB.connection.prepareStatement(bidderList);
            ResultSet resultSet = statement.executeQuery();

            while(resultSet.next()){
                String name = resultSet.getString("BiddersName");

                bList.add(name);
            }
        }catch (SQLException sqlException){
            sqlException.getSQLState();
        }
        return bList;
    }

    public ObservableList<String> getBidders(){

        bidderNameList.addAll(getBidderName());

        return bidderNameList;
    }


    public void setBidderData(String bidder,String num, String date,String name,int spot){

        System.out.print("yyyy");
        Date tempDate = new Date();
        DateFormat df = new SimpleDateFormat("MM-dd-yyyy");

        try {
            PreparedStatement  bidderData = ConnectDB.connection.prepareStatement(insertBidderData);
            bidderData.setString(1,bidder);
            bidderData.setString(2,num);
            bidderData.setString(3,date);
            bidderData.setString(4,name);
            bidderData.setInt(5,spot);

            bidderData.execute();
            bidderData.close();
        }catch (SQLException sqlException){
            sqlException.getSQLState();
        }

    }

    public void updateRecords(String bidder,String num, String date,String name, int spot){

        Date tempDate = new Date();
        DateFormat df = new SimpleDateFormat("MM-dd-yyyy");
        String todaysDate = df.format(tempDate);

        try {

            PreparedStatement statement = ConnectDB.connection.prepareStatement(updateBidderRecords);

            statement.setString(1,bidder);
            statement.setString(2,num);
            statement.setString(3,date);
            statement.setString(4,name);
            statement.setString(5,todaysDate);
            statement.setInt(6,spot);

            statement.executeUpdate();
            statement.close();

        }catch (SQLException sqlException){
            sqlException.getSQLState();
        }
    }

    public void clearList(){

        try {
            PreparedStatement statement = ConnectDB.connection.prepareStatement(updateTableInfo);

            statement.setString(1,"EMPTY");
            statement.setString(2,"0");
            statement.setString(3,"EMPTY");

            statement.executeUpdate();
            statement.close();

        }catch (SQLException sqlException){
            sqlException.getSQLState();
        }

    }

    public void clearBidderList() {

        while(0 <= getBidderName().size()){

            int i = getBidderName().size();

            i--;

            if(i < 0){
                break;
            }


            try {
                PreparedStatement statement = ConnectDB.connection.prepareStatement(updateBidderInfo);

                statement.setString(1, getBidderName().get(i));

                statement.executeUpdate();
                statement.close();

            } catch (SQLException sqlException) {
                sqlException.getSQLState();
            }
         }
    }

    public void remove(TextField textField){
        try {

            PreparedStatement statement = ConnectDB.connection.prepareStatement(remove);

            statement.setString(1,textField.getText());
            statement.execute();
            statement.close();

        }catch (SQLException sqlE){
            sqlE.getSQLState();
        }

    }
}

