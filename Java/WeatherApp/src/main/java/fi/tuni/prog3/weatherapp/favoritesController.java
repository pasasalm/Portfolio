package fi.tuni.prog3.weatherapp;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseButton;

/**
 * Controller class for favorites.fxml.
 * Shows maximum of eight cities that have been added to favorites. 
 * @author Sofia Mustajärvi
 */
public class favoritesController implements Initializable{
    
    private String cityName ="";
    
    @FXML
    ListView listViewFavorites;
    
    @FXML
    MenuItem menuItem;
    
    private iReadAndWriteToFile fileHandler;
    
    List<String> cities = new ArrayList();
    
     /**
     * Called to initialize a controller after its root element has been completely processed.
     * Fulfill ListView with favorites (from JSON). 
     * When cityname is clicked it shows the forecast in primary and adds the city to history
     * @param location The location used to resolve relative paths for the root object,
     * or null if the location is not known.
     * @param resources The resources used to localize the root object, 
     * or null if the root object was not localized.
     */
     @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Add favorites from the JSON file to the cities list
        iReadAndWriteToFile reader = new iReadAndWriteToFile();
        cities = reader.getFavourites();
        
        listViewFavorites.setItems(FXCollections.observableArrayList(cities));
        
        // Set a listener for the listView that reacts to left mouse clicks
        listViewFavorites.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                listViewFavorites.setOnMouseClicked(event -> {
                    // With the left mouse button, view the weather forecast
                    if (event.getButton() == MouseButton.PRIMARY) {
                        cityName = (String) newValue;
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
        });
    }
    
    /**
     * Delete item on listview
     */
    @FXML
    private void deleteItem() {
        String selectedItem = (String) listViewFavorites.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            iReadAndWriteToFile fileHandler = new iReadAndWriteToFile();
            if(fileHandler.removeFavourite(selectedItem)) {
                cities.remove(selectedItem);
                listViewFavorites.setItems(FXCollections.observableArrayList(cities));
                listViewFavorites.refresh();   
            }
        }
    }
    
    /**
     * Set primary to scene with a new forecast at this.cityName
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
        HistoryController historyController = new HistoryController();
        this.cityName = historyController.getLatestCity();
        if ("NO_HISTORY".equals(this.cityName)){
            WeatherApp.setRoot("primary");
        }
        else{
            WeatherApp.setRootWithCity(this.cityName);
        }
    }
}
