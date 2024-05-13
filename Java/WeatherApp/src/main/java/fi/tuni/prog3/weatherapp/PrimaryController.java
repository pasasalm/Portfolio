package fi.tuni.prog3.weatherapp;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Locale;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;


/**
 * FXML Controller class.
 * Shows the current weather and weather forecast for the city.
 * @author Tommi Paavola, Sofia Mustajärvi
 */
public class PrimaryController implements Initializable{

    iAPI.OpenWeatherMapAPI weatherAPI = new iAPI.OpenWeatherMapAPI();
    WeatherDataMap weatherdatamap = new WeatherDataMap();
    ArrayList<Label> currentWeatherLabels = new ArrayList<>();
    ArrayList<Label> dailyForecastLabels = new ArrayList<>();
    ArrayList<Label> hourlyForecastLabels = new ArrayList<>();
    String currentFileName = "";
    String forecastFileName = "";
    int dayCounter = 0;
    String cityName = "";
    boolean isSIunits = true;
    
    
    ArrayList<String> windArrows = new ArrayList<>(
        Arrays.asList(
            "\u2193", // North wind (arrow points south)
            "\u2199", // Northeast wind (arrow points southwest)
            "\u2190", // East wind (arrow points west)
            "\u2196", // Southeast wind (arrow points northwest)
            "\u2191", // South wind (arrow points north)
            "\u2197", // Southwest wind (arrow points northeast)
            "\u2192", // West wind (arrow points east)
            "\u2198"  // Northwest wind (arrow points southeast)
            ));
    
    
    @FXML
    private GridPane gridPane;
    
    @FXML
    private VBox forecast_0;
    @FXML
    private VBox forecast_1;
    @FXML
    private VBox forecast_2;
    @FXML
    private VBox forecast_3;

    @FXML
    private Label temperature_current;
    @FXML
    private Label feelsLike_current;
    @FXML
    private Label windDir_current;
    @FXML
    private Label windVelo_current;
    @FXML
    private Label desc_current;
    @FXML
    private Label desc1_current;
    @FXML
    private Label rainAmount_current;
    @FXML
    private Label rainAmount_forecast_0;
    @FXML
    private Label rainAmount_forecast_1;
    @FXML
    private Label rainAmount_forecast_2;
    @FXML
    private Label rainAmount_forecast_3;
    @FXML
    private Label temperatureMax_forecast_0;
    @FXML
    private Label temperatureMax_forecast_1;
    @FXML
    private Label temperatureMax_forecast_2;
    @FXML
    private Label temperatureMax_forecast_3;
    @FXML
    private Label temperatureMin_forecast_0;
    @FXML
    private Label temperatureMin_forecast_1;
    @FXML
    private Label temperatureMin_forecast_2;
    @FXML
    private Label temperatureMin_forecast_3;
    @FXML
    private Label cityNameLabel;
    @FXML
    private Label day_2;
    @FXML
    private Label day_3;
    
    
    private final int numRows = 6;
    private final int numColumns = 25;
    
     /**
     * Called to initialize a controller after its root element has been completely processed.
     * Fulfill gridpane with labels and add all labels to arraylists.
     * @param location The location used to resolve relative paths for the root object,
     * or null if the location is not known
     * @param resources The resources used to localize the root object, 
     * or null if the root object was not localized.
     */
     @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Create labels for the GridPane, which represents hourly forecast
        for (int i = 0; i < numRows; i++) {
            for (int j = 1; j < numColumns; j++) {
                // Number expressed in two digits
                int hour = j-1;
                String number = String.format("%02d", hour);
                Label label = new Label("");
                if (i == 0) {
                    label.setText(number);
                }
                else if (i == 1) {
                    label.setId("desc_forecast_" + number);                    
                }
                else if (i==2) {                   
                    label.setId("temperature_forecast_" + number);               
                }
                else if (i==3) {
                    label.setId("windDir_forecast_" + number);              
                }
                else if (i==4) {
                    label.setId("windVelo_forecast_" + number);             
                }
                else if (i==5) {
                    label.setId("rainAmount_forecast_" + number);             
                }
                
                GridPane.setRowIndex(label, i);
                GridPane.setColumnIndex(label, j);
                gridPane.getChildren().add(label);
                // Add the labels of the GridPane to the list of hourly forecast
                if (i > 0) {
                    hourlyForecastLabels.add(label);
                }
            }
        }
        // Add the remaining labels to the correct lists
        currentWeatherLabels.add(desc_current);
        currentWeatherLabels.add(desc1_current);
        currentWeatherLabels.add(temperature_current);
        currentWeatherLabels.add(feelsLike_current);
        currentWeatherLabels.add(windDir_current);
        currentWeatherLabels.add(windVelo_current);
        currentWeatherLabels.add(rainAmount_current);
        
        dailyForecastLabels.add(temperatureMax_forecast_0);
        dailyForecastLabels.add(temperatureMax_forecast_1);
        dailyForecastLabels.add(temperatureMax_forecast_2);
        dailyForecastLabels.add(temperatureMax_forecast_3);
        dailyForecastLabels.add(temperatureMin_forecast_0);
        dailyForecastLabels.add(temperatureMin_forecast_1);
        dailyForecastLabels.add(temperatureMin_forecast_2);
        dailyForecastLabels.add(temperatureMin_forecast_3);
        dailyForecastLabels.add(rainAmount_forecast_0);
        dailyForecastLabels.add(rainAmount_forecast_1);
        dailyForecastLabels.add(rainAmount_forecast_2);
        dailyForecastLabels.add(rainAmount_forecast_3);
        dailyForecastLabels.add(day_2);
        dailyForecastLabels.add(day_3);
        
        
    }
    /**
     * Switch scene to secondary.FXML when historybutton is pressed
     * @throws IOException 
     */
    @FXML
    private void switchToSecondary() throws IOException {
        WeatherApp.setRoot("secondary");
    }
    /**
     * Switch scene to favorites.FXML when historybutton is pressed
     * @throws IOException 
     */
    @FXML
    private void switchToFavorites() throws IOException {
        WeatherApp.setRoot("favorites");
    }
    /**
     * Switch scene to history.FXML when historybutton is pressed
     * @throws IOException 
     */
    @FXML
    private void switchToHistory() throws IOException {
        WeatherApp.setRoot("history");
    }
    
    
    /**
     * Sets the current weather for the chosen location.
     * @param cityName Name of city as String. (Case-insensitive)
     */
    @FXML    
    public void setCurrentWeather(String cityName){
        this.cityName = cityName;
        cityNameLabel.setText(cityName);
        ArrayList<Double> coordinates = weatherAPI.lookUpLocationCoordinates(cityName);
        
        if (coordinates.get(0) == 0.0 && coordinates.get(1) == 0.0) {
            return;
        }       
        // This calls the API and saves info to Json file.
        currentFileName = weatherAPI.getCurrentWeather
                                    (coordinates.get(0), coordinates.get(1));
        
        String[] parts = currentFileName.split("_");
        String timestamp = parts[2] + "_" + parts[3].replace(".json", "");
        
        
        try {
            weatherdatamap.addJsonDataToMap(currentFileName);     
        }
        catch (Exception e) {
            e.printStackTrace();
        }
       
        // Here the Label id tells us, which data we want to look for in weatherdatamap
        for (Label label : currentWeatherLabels) {
            
            label.setText("");
            
            String string = "";
            
            if (label.getId().equals("temperature_current")){
                string += String.format(Locale.US, "%.1f °C", 
                        weatherdatamap.getCurrentTemperature(cityName, timestamp));

                label.setText(string);
            }
            if (label.getId().equals("feelsLike_current")){
                string = String.format(Locale.US, "Feels like %.1f °C",
                        weatherdatamap.getCurrentFeelsLike(cityName, timestamp));
                
                label.setText(string);
            }
            if (label.getId().equals("windDir_current")){
                int windDir = weatherdatamap.getCurrentWindDir(cityName, timestamp);

                // Dividing by 45 and rounding gets us the index, where we look
                // for the windArrow. Arrows are available for all cardinal
                // and intercardinal directions. Note that the last quarter of the compass
                // (337,5 - 360 degrees) returns a zero, which corresponds to 
                // the north arrow.
                int index = (int) Math.round(windDir / 45.0) % 8;
                string = windArrows.get(index);
                string += "  " + windDir + "°";
                label.setText(string);
            }
            if (label.getId().equals("windVelo_current")) {
                
                string += String.format(Locale.US, "%.1f m/s",
                        weatherdatamap.getCurrentWindVelo(cityName, timestamp));

                label.setText(string);
            }
            if (label.getId().equals("desc_current")) {
                String iconUrl = weatherdatamap.getCurrentWeatherIcon(cityName, timestamp);

                if (iconUrl == null) {
                    label.setText(""); 
                } else {
                    Image image = new Image(iconUrl);
                    ImageView imageView = new ImageView(image);
                    imageView.setFitWidth(50); 
                    imageView.setFitHeight(50); 
                    label.setGraphic(imageView);
                }
            }
            if (label.getId().equals("rainAmount_current")) {
                string = String.format(Locale.US, "Rain: %.1f mm",
                    weatherdatamap.getCurrentRainAmount(cityName, timestamp));
                label.setText(string);
            }
            // This is the verbal representation of the weather symbol(i.e. "heavy rain").
            if (label.getId().equals("desc1_current")) {
                string += weatherdatamap.getCurrentDesc(cityName, timestamp);
                label.setText(string);
            }  
        }
        if (!isSIunits) {
            changeSpecificUnits(currentWeatherLabels);
        }
    }
    /**
     * Sets the day recap forecast. Includes daily max and min temperature
     * as well as daily total rainfall.
     * @param cityName Name of city as String. (Case-insensitive)
     */
    @FXML
    public void setDayForecast(String cityName) {
        this.cityName = cityName;
        ArrayList<Double> coordinates = weatherAPI.lookUpLocationCoordinates(cityName);
        
        if (coordinates.get(0) == 0.0 && coordinates.get(1) == 0.0) {
            return;
        }
 
        for (Label label : dailyForecastLabels) {
            
            label.setText("");
            
            String string = "";
            
            int dayId = 0;
            // Naming convection such as current day's dayId = 0, tomorrow = 1, etc.
            if (!label.getId().contains("day")) {
                dayId = Integer.parseInt(label.getId().split("_")[2]);
            }
   
            if (label.getId().contains("temperatureMax")){
                double maxTemp = -100;
                
                for (int i = 0; i < 24; ++i){
                    double temp = weatherdatamap.getForecastMaxTemp(cityName, String.valueOf(dayId), i);                 
                    if (!Double.isNaN(temp) && temp > maxTemp) {
                        maxTemp = temp;
                    }    
                }
                string += String.format(Locale.US, "%.1f °C", maxTemp);

                label.setText(string);    
            }
            if (label.getId().contains("temperatureMin")){
                double minTemp = 100;
                
                for (int i = 0; i < 24; ++i){
                    
                    double temp = weatherdatamap.getForecastMinTemp(cityName, String.valueOf(dayId), i);
                    if (!Double.isNaN(temp) && temp < minTemp) {
                        minTemp = temp;
                    }
                                       
                }
                string = String.format(Locale.US, "%.1f °C",minTemp);

                label.setText(string);    
            }
            if (label.getId().contains("rainAmount")){
                double cumulativeRain = 0.0;
                
                for (int i = 0; i < 24; ++i){
                    
                    double rainAmount = weatherdatamap.getForecastRainAmount(cityName, String.valueOf(dayId), i);
                    // Check if rainamount is Nan
                    // if it is, we don't need to sum it
                    if (!Double.isNaN(rainAmount)) {
                        cumulativeRain += rainAmount;
                    }
                }
                string += String.format(Locale.US,"%.1f mm", cumulativeRain);
                label.setText(string);    
            }
            if (label.getId().contains("day")) {
                //Note: We only do this part for the days after tomorrow.
                // The texts for the earlier labels remain as TODAY and TOMORROW
                int daysToAdd = Integer.parseInt(label.getId().split("_")[1]);
                
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.");
                LocalDate currentDate = LocalDate.now();
                LocalDate targetDate = currentDate.plusDays(daysToAdd);
                
                String date = targetDate.format(formatter);

                string += date;
                label.setText(string);
            }
        }
        if (!isSIunits) {
            changeSpecificUnits(dailyForecastLabels);
        }
    }
    /**
     * Sets the hourly forecast to the UI. Includes temperature, wind velocity
     * and direction, description as a png image and rainfall amount.
     * @param cityName Name of city as String. (Case-insensitive).
     */   
    @FXML
    public void setHourForecast(String cityName) {
        this.cityName = cityName;
        ArrayList<Double> coordinates = weatherAPI.lookUpLocationCoordinates(cityName);
        
        if (coordinates.get(0) == 0.0 && coordinates.get(1) == 0.0) {
            return;
        }
        // As a general note in the below loop, if the return values 
        // for the getter are invalid, the labels remain empty.
        for (Label label : hourlyForecastLabels) {
 
            label.setText("");

            String string = "";

            String labelDay = String.valueOf(dayCounter);

            // Currently integer, going from 0 to 23.
            int labelHour = Integer.parseInt(label.getId().split("_")[2]);

            if (label.getId().contains("temperature")){
                double temp = weatherdatamap.getForecastTemp(cityName, labelDay, labelHour);

                if (!Double.isNaN(temp)) {
                    string += String.format(Locale.US, "%.1f °C", temp);
                    label.setText(string); 
                }
            }
            if (label.getId().contains("windDir")){
                int windDir =  weatherdatamap.getForecastWindDir(cityName, labelDay, labelHour);
                if (windDir < 0) {
                    continue;
                } else {
                    // see the setCurrent() comments for explanation for the below code
                    int index = (int) Math.round(windDir / 45.0) % 8;
                    string = windArrows.get(index);
                    label.setText(string);
                }
            }            
            if (label.getId().contains("windVelo")){
                double windVelo =  weatherdatamap.getForecastWindVelo(cityName, labelDay, labelHour);

                if (!Double.isNaN(windVelo)) {
                    string += String.format(Locale.US,"%.1f m/s", windVelo);
                    label.setText(string); 
                }
            } 
            if (label.getId().contains("desc")){
                // Get icon URL
                String iconUrl = weatherdatamap.getForecastWeatherIcon(cityName, labelDay, labelHour);

                // Check if the url is empty
                if (iconUrl.isEmpty()) {
                    continue;
                } else {
                    // Create an Image object for the picture
                    Image image = new Image(iconUrl);

                    // Create an ImageView object and set the image to it
                    ImageView imageView = new ImageView(image);

                    // Set the size of the image
                    imageView.setFitWidth(50); // set the desired width of the image
                    imageView.setFitHeight(50); // set the desired height of the image

                    // Set the VBox container as the content of the label
                    label.setGraphic(imageView);
                }
            } 
            if (label.getId().contains("rainAmount")){
                double rainAmount =  weatherdatamap.getForecastRainAmount(cityName, labelDay, labelHour);

                if (!Double.isNaN(rainAmount)) {
                    string += String.format(Locale.US, "%.1f mm", rainAmount);
                    label.setText(string); 
                }
            } 
        }
        if (!isSIunits) {
            changeSpecificUnits(hourlyForecastLabels);
        }
        
    }
    /**
     * Uses the iAPI to call for the hourly forecast of the specific city,
     * and saves it first to a json file and then to a data structure.
     * For more info, see classes iAPI and WeatherDataMap.
     * @param cityName Name of city as String. (Case-insensitive)
     */
    public void setForecast(String cityName){
        ArrayList<Double> coordinates = weatherAPI.lookUpLocationCoordinates(cityName);
        
        if (coordinates.get(0) == 0.0 && coordinates.get(1) == 0.0) {
            return;
        }
        
        //Saving forecast information to file, and saves the file name locally,
        //so we can use it in the forecast displaying methods.
        forecastFileName = weatherAPI.getForecast
                                    (coordinates.get(0), coordinates.get(1));
        

        try {
            weatherdatamap.addJsonDataToMap(forecastFileName);
        } 
        catch (Exception e){
            e.printStackTrace();
        }
   
    }
     /**
     * Changes the day of hourly forecast.
     * @param event mouse clicked event
     */
    @FXML
    public void changeForecastDay(MouseEvent event){
        // Determine which VBox was clicked
        VBox clickedVBox = (VBox) event.getSource();

        // Do nothing if the correct hourly forecast is already being displayed
        int previous = this.dayCounter;
        String previousId = "forecast_" + previous;
        if (clickedVBox.getId().equals(previousId)) {
            return;
        }
        // Change background colors to white
        else {
            forecast_0.setStyle("-fx-background-color: WHITE");
            forecast_1.setStyle("-fx-background-color: WHITE");
            forecast_2.setStyle("-fx-background-color: WHITE");
            forecast_3.setStyle("-fx-background-color: WHITE");
        }
        // Change dayCounter and background color according to the clicked day
        if ( clickedVBox == forecast_0) {
            this.dayCounter = 0;
            forecast_0.setStyle("-fx-background-color:  LIGHTBLUE");
        }
        else if ( clickedVBox == forecast_1) {
            this.dayCounter = 1;
            forecast_1.setStyle("-fx-background-color:  LIGHTBLUE");
        }
        else if ( clickedVBox == forecast_2) {
            this.dayCounter = 2;
            forecast_2.setStyle("-fx-background-color:  LIGHTBLUE");
        }
        else if ( clickedVBox == forecast_3) {
            this.dayCounter = 3;
            forecast_3.setStyle("-fx-background-color: LIGHTBLUE");
        }
        setHourForecast(this.cityName);
    }
    
    /**
     * Changes units from imperial to metric or vice versa. Changes are: 
     * Temperature between C and F,
     * wind velocity between m/s and mph, and
     * rain amount between mm and inch
     * 
     */
    public void changeAllUnits() {
        
        List<List<Label>> listOfLists = Arrays.asList(hourlyForecastLabels, dailyForecastLabels, currentWeatherLabels);
        
        for (List<Label> list : listOfLists) {
            for (Label label : list) {
                String string = "";
                
                if (label.getText().equals("")) {
                    continue;
                }
                if (label.getId().contains("temperature")){
                    double temp = Double.parseDouble(label.getText().split(" ")[0]);
                    
                    if (isSIunits) {
                        double tempInF = temp * 1.8 + 32;
                        string += String.format(Locale.US, "%.1f °F", tempInF);
                    } else {
                        double tempInC = (temp - 32)/1.8;
                        string += String.format(Locale.US, "%.1f °C", tempInC);
                    }
                    label.setText(string);
                }
                if (label.getId().contains("feelsLike")) {
                    double temp;
                    
                    if (label.getText().contains("Feels like")) {
                        temp = Double.parseDouble(label.getText().split(" ")[2]);
                    } else {
                        temp = Double.parseDouble(label.getText().split(" ")[0]);
                    }
                    
                    if (isSIunits) {
                        double tempInF = temp * 1.8 + 32;
                        string += String.format(Locale.US, "%.1f °F", tempInF);
                    } else {
                        double tempInC = (temp - 32)/1.8;
                        string += String.format(Locale.US, "%.1f °C", tempInC);
 
                    }
                    if (label.getText().contains("Feels like")) {
                        label.setText("Feels like " + string);
                    } else {
                        label.setText(string);
                    }
                }
                if (label.getId().contains("windVelo")) {
                    double velo = Double.parseDouble(label.getText().split(" ")[0]);
                    
                    if (isSIunits) {
                        double veloInMph = velo * 2.23694;
                        string += String.format(Locale.US, "%.1f mph", veloInMph);
                    } else {
                        double veloInSI = velo / 2.23694;
                        string += String.format(Locale.US, "%.1f m/s", veloInSI);
                    }
                    label.setText(string);
                }
                
                if (label.getId().contains("rainAmount")){
                  double rain;
                  
                    if (label.getText().contains("Rain")) {
                        rain = Double.parseDouble(label.getText().split(" ")[1]);
                    } else {
                        rain = Double.parseDouble(label.getText().split(" ")[0]);
                    }
                    
                    if (isSIunits){
                        double rainInImper = rain * 0.0393701;
                        string += String.format(Locale.US, "%.1f ''", rainInImper);
                    } else {
                        double rainInSI = rain / 0.0393701;
                        string += String.format(Locale.US, "%.1f mm", rainInSI);
                    }
                    label.setText(string);
                     
                } 
            }     
        }
        isSIunits = !isSIunits;
    }
    /**
     * Changes the units of a list of labels between metric and imperial. Changes are:
     * Temperature between C and F, 
     * Wind velocity between m/s and mph, and
     * Rainfall between mm and inch
     * @param list list of labels of which the units should be changed.
     */
    public void changeSpecificUnits(ArrayList<Label> list) {
        for (Label label : list) {
              String string = "";

            if (label.getText().equals("")) {
                continue;
            }

            if (label.getId().contains("temperature")){
                double temp = Double.parseDouble(label.getText().split(" ")[0]);

                // If currently in Celsius, switch to Fahrenheit, else the opposite
                // Same logic is used in other changable labels.
                if (label.getText().contains("C")) {
                    double tempInF = temp * 1.8 + 32;
                    string += String.format(Locale.US, "%.1f °F", tempInF);
                } else {
                    double tempInC = (temp - 32)/1.8;
                    string += String.format(Locale.US, "%.1f °C", tempInC);

                }
                label.setText(string);
            }
            if (label.getId().contains("feelsLike")) {
                double temp;

                if (label.getText().contains("Feels like")) {
                    temp = Double.parseDouble(label.getText().split(" ")[2]);
                } else {
                    temp = Double.parseDouble(label.getText().split(" ")[0]);
                }

                if (label.getText().contains("C")) {
                    double tempInF = temp * 1.8 + 32;
                    string += String.format(Locale.US, "%.1f °F", tempInF);
                } else {
                    double tempInC = (temp - 32)/1.8;
                    string += String.format(Locale.US, "%.1f °C", tempInC);;
                }
                if (label.getText().contains("Feels like")) {
                    label.setText("Feels like " + string);
                } else {
                    label.setText(string);
                }
            }
            if (label.getId().contains("windVelo")) {
                double velo = Double.parseDouble(label.getText().split(" ")[0]);

                if (label.getText().contains("m/s")) {
                    double veloInMph = velo * 2.23694;
                    string += String.format(Locale.US, "%.1f mph", veloInMph);
                } else {
                    double veloInSI = velo / 2.23694;
                    string += String.format(Locale.US,"%.1f m/s", veloInSI);

                }
                label.setText(string);
            }

            if (label.getId().contains("rainAmount")){
                double rain;

                if (label.getText().contains("Rain")) {
                    rain = Double.parseDouble(label.getText().split(" ")[1]);
                } else {
                    rain = Double.parseDouble(label.getText().split(" ")[0]);
                }

                if (label.getText().contains("mm")){
                    double rainInImper = rain * 0.0393701;
                    string += String.format(Locale.US,"%.1f ''", rainInImper);
                } else {
                    double rainInSI = rain / 0.0393701;
                    string += String.format(Locale.US,"%.1f mm", rainInSI);
                }
                label.setText(string);

            } 
        }
    }  
}
