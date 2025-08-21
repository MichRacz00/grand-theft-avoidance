package model;
import com.google.gson.Gson;

import java.util.ArrayList;

public class GTAModel {

    private static GTAModel instance = null;

    private GTAModel() {
        //empty constructor
    }

    public GTAModel getInstance() {
        if (instance == null)
            instance = new GTAModel();

        return instance;
    }

//
//    public void run() {
//        ArrayList<Location> test = new ArrayList<>();
//
//        for (int i = 0; i < 100; i++) {
//            test.add(new Location(i, i + 102, i + 8433));
//        }
//
//        System.out.println(locationToJson(test));
//    }

    public String articlesToJson(ArrayList<Article> articles) {
        return new Gson().toJson(articles);
    }

    public String locationToJson(ArrayList<Location> locations) {
        return new Gson().toJson(locations);
    }

    public String theftToJson(ArrayList<Theft> thefts) {
        return new Gson().toJson(thefts);
    }

//    public static void main(String[] args) {
//        new GTAModel().run();
//    }
}
