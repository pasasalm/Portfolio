package fi.tuni.prog3.weatherapp;

import java.io.IOException;
import java.util.ArrayList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.TextField;

/**
 * FXML Controller class for search scene. 
 * Search cities and add them to favourites.
 * @author Sofia Mustajärvi, Patrik Salmensaari
 */
public class SecondaryController{
    
    private String cityName ="";
    
    // boolean value to check if usear has already searched for city.
    private boolean isAlreadySearched = false;

    @FXML
    TextField textFieldCity; 
    @FXML
    Label labelSearchResult;
    @FXML
    RadioButton radioButtonFavorite;
    @FXML
    Label errorMessage;
    @FXML
    Label addFavoriteLabel;
    
    /**
     * Set primary to scene with a new forecast from this.cityName
     * @throws IOException 
     */
    @FXML
    private void switchToPrimary() throws IOException {
        if ("".equals(this.cityName)) {
            errorMessage.setText("Enter an existing city and click search!");
        }
        else{
            WeatherApp.setRootWithCity(this.cityName);
        }
    }
    /**
     * Set primary to scene with a forecast at the latest city in history
     * @throws IOException 
     */
    @FXML
    private void goBack() throws IOException {
        // if city is already finded user cant go back
        if (isAlreadySearched) {
            labelSearchResult.setText("After finding city you can't go back, you need to press See the weather");
        // if user have not found city, go back is able to use.
        } else {
            HistoryController historyController = new HistoryController();
            this.cityName = historyController.getLatestCity();
            WeatherApp.setRootWithCity(this.cityName);
        }
    }
    /**
     * Takes a name of a city from textField and notify if the city is found.
     * If it is found add the city to history.
     * @throws IOException 
     */
    @FXML
    private void searchCity() throws IOException{
        try{
            isAlreadySearched = false;
            // clean messages
            errorMessage.setText("");
            addFavoriteLabel.setText("");
            // delete active radiobutton
            boolean selected = radioButtonFavorite.isSelected();
            if (selected) {
                radioButtonFavorite.setSelected(false);
            }
            
            String input = textFieldCity.getText().toLowerCase();
            String name = input.substring(0, 1).toUpperCase() + input.substring(1);
            String apiKey = "1a5d7f7739ee0d8f6ecac84076370966";   
            // if weather is found by given city, search was succesfull
            iAPI.OpenWeatherMapAPI weatherAPI = new iAPI.OpenWeatherMapAPI();
            ArrayList<Double> coordinates = weatherAPI.lookUpLocationCoordinates(name);
            if (coordinates.get(0) == 0.0 && coordinates.get(1) == 0.0) {
                labelSearchResult.setText("Not found a city named: "+ name);
                return;
            }

            // Get weather based on cordinates
            String weatherData = weatherAPI.getCurrentWeather(coordinates.get(0), coordinates.get(1));
            if (weatherData != null) {
                this.cityName = name;
                iReadAndWriteToFile writer = new iReadAndWriteToFile();
                boolean added = writer.addToHistory(cityName);
                isAlreadySearched = true;
                if (added){
                    labelSearchResult.setText(name);
                }
                else{
                    errorMessage.setText("Can not save city to history.");
                }
            } else {
                labelSearchResult.setText("Not found a city named: "+ name);
            }
        }
        catch(Exception e){
            String content = textFieldCity.getText();
            labelSearchResult.setText("Not found a city named: "+ content);
        }
    }
    
    /**
    * Adds the currently searched city to the list of favourite cities if it's not already there
    * and the list is not full. Displays appropriate messages based on the result.
    */
    @FXML
    private void addFavorite(){
        if (!"".equals(this.cityName)) {
            iReadAndWriteToFile fileHandler = new iReadAndWriteToFile();
            if (fileHandler.isFavouriteFull()) {
                addFavoriteLabel.setText("Cannot add city to favorites: Favorites list is full.");
            } else if (fileHandler.isCityInFavourites(this.cityName)) {
                addFavoriteLabel.setText("City is already in favorites.");
            } else {
                if (fileHandler.addFavourite(this.cityName)) {
                    addFavoriteLabel.setText("City added to favorites: " + this.cityName);
                } else {
                    addFavoriteLabel.setText("Failed to add city to favorites.");
                }
            }
        } else {
            addFavoriteLabel.setText("Please search for a city before adding it to favorites.");
        }
    }
}
