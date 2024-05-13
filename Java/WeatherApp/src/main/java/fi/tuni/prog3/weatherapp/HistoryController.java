package fi.tuni.prog3.weatherapp;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import java.util.ArrayList;
import java.util.List;


/**
 * Controller class for history.fxml.
 * Show 20 latest cities that have been added to history
 * @author Sofia Mustajärvi
 */
public class HistoryController implements Initializable{
    
    // add history to list from json file
    private List<String> cities = new ArrayList();
    private String cityName ="";
    
    /**
     * Read history from JSON and return latest city
     * @return the last city from history as a String
     */
    public String getLatestCity() {
        iReadAndWriteToFile reader = new iReadAndWriteToFile();
        cities = reader.getHistory();
        int size = cities.size();
        if (size <= 0){
            System.out.println("NO history available");
            return "NO_HISTORY";
        }
        String latestCity = ((ArrayList<String>) cities).get(0);
        return latestCity;
    }
    
    @FXML
    ListView listViewHistory;
    
     /**
     * Called to initialize a controller after its root element has been completely processed.
     * Fulfill ListView with history from JSON (add cities to this.cities from JSON)
     * and when cityname is clicked shows the forecast in primary and add the city to history
     * @param location The location used to resolve relative paths for the root object,
     * or null if the location is not known
     * @param resources The resources used to localize the root object, 
     * or null if the root object was not localized.
     */
     @Override
    public void initialize(URL location, ResourceBundle resources) {
        // read json file and save history to cities list
        iReadAndWriteToFile reader = new iReadAndWriteToFile();
        cities = reader.getHistory();
        
        listViewHistory.getItems().addAll(cities);
        // Listener to listview
        listViewHistory.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> ov, String t, String t1) {
                cityName = (String) listViewHistory.getSelectionModel().getSelectedItem();
                iReadAndWriteToFile writer = new iReadAndWriteToFile();
                writer.addToHistory(cityName);
                try {
                    switchToPrimary();
                } catch (IOException ex) {
                    Logger.getLogger(favoritesController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }
    
    /**
     * Set primary to scene with new forecast at this.cityName
     * @throws IOException 
     */
    @FXML
    private void switchToPrimary() throws IOException {
        WeatherApp.setRootWithCity(this.cityName);
    }
    /**
     * Set primary to scene with a forecast at the latest city in history
     * @throws IOException 
     */
    @FXML
    private void goBack() throws IOException {
        this.cityName = getLatestCity();
        if ("NO_HISTORY".equals(this.cityName)){
            WeatherApp.setRoot("primary");
            return;
        }
        WeatherApp.setRootWithCity(this.cityName);
    }
}
