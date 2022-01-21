package sample;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.controlsfx.control.textfield.TextFields;
import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class Controller implements Initializable  {

    int bidderCount;
    int auctionedCount;

    String bidDate;
    String auctionedDate;
    String aDate;
    String bDate;

    String bidName;
    String amount;
    String bidderDate;
    String auctName;

    int placeHolder = 0;

    public static Boolean nameReset = false;

    Boolean bidderName = false;

    boolean remove = false;

    boolean exported = false;


    @FXML
    public TextField name = new TextField();

    @FXML
    CheckBox bidder = new CheckBox();

    @FXML
    CheckBox auctioned = new CheckBox();

    @FXML
    private Label checker = new Label();

    @FXML
    private TextField number = new TextField();

    @FXML
    private TableView<AuctionList> table = new TableView<>();

    @FXML
    private TableColumn<AuctionList,String> auctionNumber = new TableColumn<>();

    @FXML
    private TableColumn<AuctionList,String> auctionName = new TableColumn<>();

    @FXML
    private TableColumn<AuctionList,String> sold_for_column = new TableColumn<>();

    @FXML
    private TableColumn<AuctionList,String> bidder_column = new TableColumn<>();

    @FXML
    CheckBox removeBox = new CheckBox();

    ObservableList<AuctionList> obList = FXCollections.observableArrayList();

    ConnectDB connectDB = new ConnectDB();


   static String nameStr;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        TextFields.bindAutoCompletion(name,connectDB.getProfileName());
        selectList();

        name.focusedProperty().addListener((ov,oldV,newV)->{
            if(!newV ){
                nameCheck(name);
            }
        });
    }

    //Save Button Function
    public void onClick_Button_Save() {

        if(bidderName) {
            connectDB.setBidderName(name);
            bidderName = false;
        }

        remove();

        if(!auctioned.isSelected()) {
            aDate = connectDB.getAuctionDate();
            auctionedDate = null;
        }

        if(!bidder.isSelected()) {
            bDate = connectDB.getBidDates();
            bidDate = null;
        }

        //if-else statement checks the boxs
        if(bidder.isSelected() && auctioned.isSelected()){
            auctionedCount = 0;
            connectDB.reset();
        }

        else if(auctioned.isSelected() && aDate.equals(bDate)){
            auctionedCount = 0;
            connectDB.reset();
        }

        else if(auctioned.isSelected()){
            connectDB.setPreviousAuctionCount(name.getText());

        }


        if(remove){
            System.out.println("IF ST");
            auctionedCount = connectDB.getPreviousAuctionCount();
            remove = false;
        }

        else if(!removeBox.isSelected() && (connectDB.getbCount() == 1 || bidder.isSelected())){

            auctionedCount = 0;
            connectDB.reset();
        }


        if(ConnectDB.inDatabase == true){


            if(!(bDate.equals(connectDB.getPreviousBidDate())) && bidder.isSelected()) {
                connectDB.setPreviousBidDate();
            }

            if(!(aDate.equals(connectDB.getPreviousAuctionDate())) && auctioned.isSelected()){
                connectDB.setPreviousAuctionDate();
            }

            connectDB.update(name.getText(),getBidderCount(),bDate,getAuctionedCount(),aDate);

            connectDB.setListOrder(number);
        }

        else if(ConnectDB.inDatabase == false) {

            if(bidDate == null){
                bidDate = "0";
            }

            if(auctionedDate == null){
                auctionedDate = "0";
            }

            connectDB.setInsert(name, bidderCount, bidDate, getAuctionedCount(), auctionedDate);
            connectDB.setListOrder(number);
        }

        name.clear();
        number.clear();
        bidder.setSelected(false);
        auctioned.setSelected(false);
        checker.setText("Status:");
        auctioned.setDisable(false);

        upDateTable();


        //Make Connection to Auction List to Comapare Names
        if(nameReset){
            connectDB.setListOrder(number);
        }

    }

    //CheckBox function for Bidder
    public void bidderCheckBox(ActionEvent event) {

        if ((!removeBox.isSelected()) && bidder.isSelected() && connectDB.getBidderName().contains(name.getText())){
                return;
        }

        if(!(bidder.isSelected())&& connectDB.getaCount() == 3){
            auctioned.setDisable(true);
        }

        else if((!removeBox.isSelected())&& bidder.isSelected()) {

            bidderCount++;
            bDate = null;
            bDate = getBidDate();

            bidderCount = connectDB.getbCount();
            bidderName = true;

            connectDB.setPreviousAuctionCount(name.getText());

            auctioned.setDisable(false);

        }

    }

    public int getBidderCount(){
        return bidderCount;
    }

    //CheckBox function for Auctioned
    public void auctionedCheckBox(ActionEvent event) {

        if (auctioned.isSelected() && (!removeBox.isSelected())) {

            auctionedCount = 0;
            auctionedCount++;

            aDate = getAuctionedDate();

            auctionedCount += connectDB.getaCount();
        }
    }

    public int getAuctionedCount(){
        return auctionedCount;
    }


    public String getBidDate() {
        Date date = new Date();
        DateFormat df = new SimpleDateFormat("MM-dd-yyyy");
        bidDate = df.format(date);
        return bidDate;
    }


    public String getAuctionedDate() {
        Date date = new Date();
        DateFormat df = new SimpleDateFormat("MM-dd-yyyy");

        if(auctioned.isSelected())
            auctionedDate = df.format(date);

        return auctionedDate;
    }

    //Remove Method
    public void remove() {

        if (removeBox.isSelected() && bidder.isSelected()) {

            remove = true;

            bDate = null;
            bDate = connectDB.getPreviousBidDate();

            auctionedCount = connectDB.getPreviousAuctionCount();

            connectDB.remove(name);

            removeBox.setSelected(false);
        }

        if(removeBox.isSelected() && auctioned.isSelected()){

            aDate = null;
            aDate = connectDB.getPreviousAuctionDate();

            nameReset = true;

            auctionedCount = connectDB.getPreviousAuctionCount();

            removeBox.setSelected(false);
        }
    }


    //Check Name Entered In Database
    public void nameCheck(TextField tf) {

        StringBuilder dbNames = new StringBuilder();

        try {
            PreparedStatement statement = ConnectDB.connection.prepareStatement("Select * FROM auctiondata " +
                    "Where MeetMeName ='" + tf.getText() + "'");
            ResultSet resultset = statement.executeQuery();

            if (resultset.next()) {
                dbNames.append(resultset.getString("MeetMeName"));
            }

            //Fix database to pull from one location
            if (dbNames.toString().equalsIgnoreCase(tf.getText())) {
                checker.setText("In Database");
                ConnectDB.inDatabase = true;
                connectDB.Auction(tf.getText());
                connectDB.DatabaseAuctionDates(tf.getText());
                connectDB.DatabaseBidDates(tf.getText());

                nameStr = name.getText();

                if(connectDB.getaCount() == 3){
                    checker.setText("Must Be A Bidder!!");
                    auctioned.setDisable(true);
                }


            } else if (!(dbNames.toString().equalsIgnoreCase(tf.getText()))) {
                checker.setText("Not In Database");
                ConnectDB.inDatabase = false;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //Key Functions
    public void next() {

        name.setOnKeyPressed((KeyEvent event) ->{
            switch (event.getCode()){
                case ENTER: number.requestFocus();
                case TAB:
           }});
        number.setOnKeyPressed((KeyEvent event) ->{
            switch(event.getCode()){
                case ENTER: bidder.requestFocus();}});
        bidder.setOnKeyPressed((KeyEvent event)->{
            switch(event.getCode()){
                case ENTER: auctioned.requestFocus();}});
        auctioned.setOnKeyPressed((KeyEvent event)->{
            switch(event.getCode()){
                case ENTER: table.requestFocus();}});
    }

    public void upDateTable(){
        obList.clear();
        connectDB.setDatabaseList();
        selectList();
    }


    public void selectList(){

        try {
            while(connectDB.getResultset().next()){
                obList.add(new AuctionList(connectDB.getResultset().getString("AuctionNumber"),
                        connectDB.getResultset().getString("AuctionName"),
                        connectDB.getResultset().getString("SoldFor"),
                        connectDB.getResultset().getString("Bidder")));
            }
        }catch (SQLException sqlException){
            sqlException.getSQLState();
        }

        auctionNumber.setCellValueFactory(new PropertyValueFactory<>("number"));
        auctionName.setCellValueFactory(new PropertyValueFactory<>("name"));
        sold_for_column.setCellValueFactory(new PropertyValueFactory<>("sold_for"));
        bidder_column.setCellValueFactory(new PropertyValueFactory<>("bidder"));

        table.setItems(obList);
    }


    public void display_screen(ActionEvent event) throws IOException {

            Stage stage = new Stage();
            Parent root = FXMLLoader.load(getClass().getResource("Resources/DisplayScreen.fxml"));
            Scene scene = new Scene(root);
            stage.setTitle("Auction App");
            stage.setScene(scene);
            scene.getStylesheets().add("sample/StyleSheets/stylesheetS1.css");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();
    }


    public void export_button()throws IOException{

        upDateTable();

        int count = -1;
        int exportTracker = 0;


        Date date = new Date();
        DateFormat df = new SimpleDateFormat("MM-dd-yyyy");

        ButtonType yesButton = new ButtonType("Yes",ButtonBar.ButtonData.YES);
        ButtonType noButton = new ButtonType("No",ButtonBar.ButtonData.NO);

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.getButtonTypes().setAll(yesButton,noButton);
        alert.setContentText("Are You Sure You Want To Export To Database?");

        Optional<ButtonType> type = alert.showAndWait();

        if(type.get() == yesButton) {

            while (count < obList.size() - 1) {

                placeHolder = exportTracker;
                count++;


                if (obList.get(count).getBidder().equalsIgnoreCase("EMPTY")) {
                    exportTracker++;
                    continue;
                }


                if (exported) {
                    bidName = obList.get(count).getBidder();
                    amount = obList.get(count).getSold_for();
                    bidderDate = df.format(date);
                    auctName = obList.get(count).getName();

                    connectDB.updateRecords(bidName, amount, bidderDate, auctName, count + 1);

                } else {
                    bidName = obList.get(count).getBidder();
                    amount = obList.get(count).getSold_for();
                    bidderDate = df.format(date);
                    auctName = obList.get(count).getName();

                    connectDB.setBidderData(bidName, amount, bidderDate, auctName, count + 1);
                }
            }
        }
        exported = true;
    }

    public void refresh_button(ActionEvent event){
        upDateTable();
        name.clear();
        number.clear();
        bidder.setSelected(false);
        auctioned.setSelected(false);
        checker.setText("Status:");
        auctioned.setDisable(false);
    }

    public void exit(){

        ButtonType yesButton = new ButtonType("Yes",ButtonBar.ButtonData.YES);
        ButtonType noButton = new ButtonType("No",ButtonBar.ButtonData.NO);

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.getButtonTypes().setAll(yesButton, noButton);
        alert.setContentText("Are You Sure You Want To Exit?");

        Optional<ButtonType> type = alert.showAndWait();

            if(type.get() == yesButton){
                Platform.exit();
            }
    }

    public void clear(){
        upDateTable();

        ButtonType yesButton = new ButtonType("Yes",ButtonBar.ButtonData.YES);
        ButtonType noButton = new ButtonType("No",ButtonBar.ButtonData.NO);

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.getButtonTypes().setAll(yesButton,noButton);
        alert.setContentText("Are You Sure You Want To Clear Everything?");

        Optional<ButtonType> type = alert.showAndWait();


        if(type.get() == yesButton && exported){
            connectDB.clearList();
            connectDB.clearBidderList();
            upDateTable();

        }

        else if(type.get() == yesButton){

            ButtonType yButton = new ButtonType("Yes",ButtonBar.ButtonData.YES);
            ButtonType nButton = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);

            Alert a = new Alert(Alert.AlertType.CONFIRMATION);
            a.getButtonTypes().setAll(yButton,nButton);
            a.setContentText("Nothing Has Been Exported. Do You Want To Continue \nWithout Exporting?");

            Optional<ButtonType> buttonType = a.showAndWait();

            if(buttonType.get() == yButton){
                connectDB.clearList();
                connectDB.clearBidderList();
                upDateTable();
            }

            else if(buttonType.get() == nButton){
                Button cancelButton = (Button) a.getDialogPane().lookupButton(buttonType.get());
                cancelButton.fire();
            }
        }
    }
}
