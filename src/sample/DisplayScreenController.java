package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;


public class DisplayScreenController implements Initializable {

    @FXML
    TableView<AuctionList> table = new TableView();

    @FXML
    TextField number = new TextField();

    @FXML
    TextField sold_for = new TextField();

    @FXML
    ComboBox bidders = new ComboBox();

    @FXML
    Button save = new Button();

    @FXML
    TableColumn<AuctionList,String> number_column = new TableColumn<>();

    @FXML
    TableColumn<AuctionList, String> name_column = new TableColumn<>();

    @FXML
    TableColumn<AuctionList,String> sold_for_column = new TableColumn<>();

    @FXML
    TableColumn<AuctionList,String>bidder_column = new TableColumn<>();

    @FXML
    Button back = new Button();

    ConnectDB connectDB = new ConnectDB();

    ObservableList<AuctionList> obList = FXCollections.observableArrayList();

    public static ObservableList<String> names = FXCollections.observableArrayList();

    static int numberCount = 1;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        selectList();

        sold_for.requestFocus();

        number.setFocusTraversable(false);


        if(true) {
            sold_for.requestFocus();
        }


        if(!bidders.getItems().isEmpty()){
            bidders.getItems().clear();
            bidders.setItems(connectDB.getBidders());
        }

        else if(bidders.getItems().isEmpty()) {
            bidders.setItems(connectDB.getBidders());
        }

        number.setText(String.valueOf(numberCount));
    }

    public void updateDisplayScreen(){

        connectDB.displayScreenUpdate(number.getText(),sold_for.getText(),
                bidders.getSelectionModel().getSelectedItem().toString());
        upDateTable();

        numberCount = Integer.parseInt(number.getText());

        number.clear();
        sold_for.clear();
        bidders.setValue(null);

        numberCount++;

        number.setText(String.valueOf(numberCount));
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

        number_column.setCellValueFactory(new PropertyValueFactory<>("number"));
        name_column.setCellValueFactory(new PropertyValueFactory<>("name"));
        sold_for_column.setCellValueFactory(new PropertyValueFactory<>("sold_for"));
        bidder_column.setCellValueFactory(new PropertyValueFactory<>("bidder"));

        table.setItems(obList);
    }


    public void upDateTable(){
        obList.clear();
        connectDB.setDatabaseList();
        selectList();
    }


    public void back_button(ActionEvent event) throws IOException {

       Stage stage = (Stage) back.getScene().getWindow();
       stage.close();
    }

    //Key Functions
    public void next() {

        sold_for.setOnKeyPressed((KeyEvent event) ->{
            switch (event.getCode()){
                case ENTER: bidders.requestFocus();
                case TAB:
            }});
        bidders.setOnKeyPressed((KeyEvent event) ->{
            switch(event.getCode()){
                case ENTER: save.requestFocus();
                case TAB:}});
        save.setOnKeyPressed((KeyEvent event)->{
            switch(event.getCode()){
                case ENTER: back.requestFocus();
                case TAB: }});
        number.setFocusTraversable(true);
        back.setOnKeyPressed((KeyEvent event)->{
            switch(event.getCode()){
                case ENTER: number.requestFocus();
                case TAB:}});
        number.setOnKeyPressed((KeyEvent event)->{
            switch(event.getCode()){
                case ENTER: sold_for.requestFocus();
                case TAB:}});
    }

    public void refocus() {
        sold_for.requestFocus();
    }
}
