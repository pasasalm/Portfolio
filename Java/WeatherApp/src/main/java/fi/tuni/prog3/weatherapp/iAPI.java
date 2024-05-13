package fi.tuni.prog3.weatherapp;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.FileWriter;
import java.io.IOException;
import java.io.FileReader;
import java.lang.reflect.Type;
import com.google.gson.reflect.TypeToken;
import com.google.gson.Gson;
import java.util.List;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Defines methods for interacting with an external weather API. These methods provide functionality
 * for retrieving location information and weather data based on coordinates.
 * 
 * @author Patrik Salmensaari, Tommi Paavola
 */
public interface iAPI {
    
    /**
    * Returns coordinates for a location. Fetches coordinates based on
    * municipality name from file municipalityCoordinates.json
    * @param loc Name of the location for which coordinates should be fetched. (Case-insensitive)
    * @return ArrayList of Double. first element is longitude, second latitude.
                Returns {0.0 , 0.0} if place not found.
    */
    ArrayList<Double> lookUpLocationCoordinates(String loc);
    
    /**
    * Gives a location name based on its coordinates. Searches the match
    * from the json File municipalityCoordinates.json. Considered a match
    * if the coordinates values until 5th decimal match.
    * @param lat Latitude as Double
    * @param lon Longitude as Double
    * @return Name of location as String. If not found, returns the string
    * "Not found".
    */
    String lookUpLocationName(double lat, double lon);
    
    /**
    * Fetches the current weather data for the given coordinates and saves the response as a formatted
    * JSON file.
    * 
    * @param lat The latitude of the location.
    * @param lon The longitude of the location.
    * @return The file name of the saved JSON file containing the current weather data. If an error
    *         occurs, returns null.
    */
    String getCurrentWeather(double lat, double lon);
    
    /**
    * Fetches a weather forecast for the given coordinates and saves the response as a formatted JSON
    * file.
    * 
    * @param lat The latitude of the location.
    * @param lon The longitude of the location.
    * @return The file name of the saved JSON file containing the forecast data. If an error occurs,
    *         returns null.
    */
    String getForecast(double lat, double lon);

    
    class OpenWeatherMapAPI implements iAPI {

        private String apiKey = "1a5d7f7739ee0d8f6ecac84076370966"; 
        

        @Override
        public ArrayList<Double> lookUpLocationCoordinates(String loc) {
            
            ArrayList<Double> coordinates = new ArrayList<>();
            try {
                FileReader reader = new FileReader("municipalityCoordinates.json");

                Type featureCollectionType = new TypeToken<List<FeatureCollection>>(){}.getType();
                List<FeatureCollection> featureCollections = new Gson().fromJson(reader, featureCollectionType);

                for (FeatureCollection featureCollection : featureCollections) {
                    for (Feature feature :featureCollection.features) {
                        if(feature.properties.name.equalsIgnoreCase(loc)) {
                            coordinates.add(feature.geometry.coordinates.get(1));
                            coordinates.add(feature.geometry.coordinates.get(0));
                            return coordinates;
                        }
                    }
                }
            }
            catch (IOException e){
                    e.printStackTrace();
            }
            coordinates.add(0.0);
            coordinates.add(0.0);
            return coordinates; 
        }
        
        
        @Override
        public String lookUpLocationName(double lat, double lon){
                       ArrayList<Double> coordinates = new ArrayList<>();
            try {
                FileReader reader = new FileReader("municipalityCoordinates.json");

                Type featureCollectionType = new TypeToken<List<FeatureCollection>>(){}.getType();
                List<FeatureCollection> featureCollections = new Gson().fromJson(reader, featureCollectionType);

                for (FeatureCollection featureCollection : featureCollections) {
                    for (Feature feature :featureCollection.features) {
                        if(Math.abs(feature.geometry.coordinates.get(1)- lat) < 0.00001
                            && (feature.geometry.coordinates.get(0)- lon) < 0.00001){
                           return feature.properties.name;
                        }
                    }
                }
            }
            catch (IOException e){
                    e.printStackTrace();
            }
            return "Not found"; 
        }
        
         
        @Override
        public String getCurrentWeather(double lat, double lon){
            try {
                // Constructs the URL for the OpenWeatherMap API request using the provided latitude, longitude, and API key
                String urlString = "http://api.openweathermap.org/data/2.5/weather?lat=" + lat + "&lon=" + lon + "&appid=" + apiKey;

                // Establishes an HTTP connection to the API endpoint            
                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                // Reads the response from the API endpoint and stores it in a StringBuilder          
                Scanner scanner = new Scanner(connection.getInputStream());
                StringBuilder response = new StringBuilder();
                while (scanner.hasNextLine()) {
                    response.append(scanner.nextLine());
                }
                scanner.close();

                // Parses the JSON response into a JsonObject           
                JsonObject jsonObject = JsonParser.parseString(response.toString()).getAsJsonObject();

                // Creates a Gson object with pretty printing enabled to format the JSON
                Gson prettyGson = new GsonBuilder().setPrettyPrinting().create();
                String prettyJson = prettyGson.toJson(jsonObject);

                // Generates a file name for the JSON file based on the location, current and timestamp
                // timestamp is day.month_hour.minute, uses LocalDateTime import
                DateTimeFormatter date_time_f = DateTimeFormatter.ofPattern("dd.MM_HH.mm");
                LocalDateTime time_now = LocalDateTime.now();
                String timestamp = date_time_f.format(time_now);
                String location = lookUpLocationName(lat, lon);
                
                // if somehow lookUpLocationName manage to return "Not found" and it hasn't appear before
                // do check that location is real location and lookUpLocationName has worked corretly
                // this is importat exspcially in Tests
                if (location == "Not found") {
                    return null;
                }
                
                String fileName = location + "_current_"  + timestamp + ".json";

                // Writes the formatted JSON data to a file
                try (FileWriter fileWriter = new FileWriter(fileName)) {
                    fileWriter.write(prettyJson);
                }
                return fileName;

            // Returns the formatted JSON string
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        
        @Override
        public String getForecast(double lat, double lon){
            try {
                // Constructs the URL for the OpenWeatherMap API request using the provided latitude, longitude, and API key
                String urlString = "https://pro.openweathermap.org/data/2.5/forecast/hourly?lat=" + lat + "&lon=" + lon + "&appid=" + apiKey;

                // Establishes an HTTP connection to the API endpoint            
                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                // Reads the response from the API endpoint and stores it in a StringBuilder          
                Scanner scanner = new Scanner(connection.getInputStream());
                StringBuilder response = new StringBuilder();
                while (scanner.hasNextLine()) {
                    response.append(scanner.nextLine());
                }
                scanner.close();

                // Parses the JSON response into a JsonObject           
                JsonObject jsonObject = JsonParser.parseString(response.toString()).getAsJsonObject();

                // Creates a Gson object with pretty printing enabled to format the JSON
                Gson prettyGson = new GsonBuilder().setPrettyPrinting().create();
                String prettyJson = prettyGson.toJson(jsonObject);
                
                // Generates a file name for the JSON file based on the location, forecast and timestamp
                // timestamp is day.month_hour.minute
                DateTimeFormatter date_time_f = DateTimeFormatter.ofPattern("dd.MM_HH.mm");
                LocalDateTime time_now = LocalDateTime.now();
                String timestamp = date_time_f.format(time_now);   
                String location = lookUpLocationName(lat, lon);
                
                // if somehow lookUpLocationName manage to return "Not found" and it hasn't appear before
                // do check that location is real location and lookUpLocationName has worked corretly
                // this is important especiially in tests
                if (location == "Not found") {
                    return null;
                }
                
                String fileName = location + "_forecast_" + timestamp + ".json";

                // Writes the formatted JSON data to a file
                try (FileWriter fileWriter = new FileWriter(fileName)) {
                    fileWriter.write(prettyJson);
                }
                return fileName;
            
            // Returns the formatted JSON string
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
    }
}

