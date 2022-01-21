package sample;

import javafx.collections.ObservableList;
import javafx.scene.control.TextField;

import java.util.ArrayList;
import java.util.List;


public class BidderAuctionList {

    private String name;

    public static List<String> array = new ArrayList();


    public void setBidderList(TextField textField){
        name = textField.getText();
        System.out.println("Name in bidderAList " + name);
        System.out.println();
    }

    //Inputs Bidder Into List
    public void getName(){

        System.out.println("Return name "  + name);
        System.out.println();

        array.add(name);
        System.out.println("Size " + array.size());
        System.out.println("List " + array);
        System.out.println();
    }

    public void removeName(TextField textField){
        array.remove(textField.getText());
    }

    public String getTheName(){

        for(int i = 0; i < array.size(); i++){
            String str = new String();
            str = array.get(i);
            return str;
        }

        return "";
    }

    public void changeArray(ObservableList list){
        System.out.println("Before " + array.toString());
        array.clear();
        array.addAll(list);
        System.out.println("After " + array.toString() );
        System.out.println();
    }


    public static ObservableList getList(){
        DisplayScreenController.names.addAll(array);
        return DisplayScreenController.names;
    }

}
