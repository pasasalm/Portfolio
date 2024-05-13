package fi.tuni.prog3.weatherapp;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.TreeMap;

/**
 * Class responsible for reading JSON data and organizing it into data structures.
 * Reads JSON data representing current weather and forecast data from OpenWeatherMap API.
 * Adds the data to appropriate data structures based on location and timestamp.
 * Supports retrieval of current weather data and weather forecast data.
 * class contains also data returning methods from data structures
 * 
 * @author Patrik Salmensaari
 */
public class WeatherDataMap {

    private static HashMap<String, HashMap<String, WeatherData>> currentWeatherMap = new HashMap<>();
    private static HashMap<String, HashMap<String, TreeMap<String, WeatherData>>> weatherForecastMap = new HashMap<>();

    /**
     * Adds current weather data to the map structure based on location and timestamp.
     * 
     * @param location     Place where weather data is recorded
     * @param timestamp    Timestamp indicating when the data was retrieved from OpenWeatherMap, day.month_hour.minute
     * @param weatherData  WeatherData object containing the weather information
     */
    public static void addWeatherData(String location, String timestamp, WeatherData weatherData) {
        
        HashMap<String, WeatherData> innerMap = currentWeatherMap.getOrDefault(location, new HashMap<>());
        innerMap.put(timestamp, weatherData);
        currentWeatherMap.put(location, innerMap);
    }
    
    
    /**
     * Adds weather forecast data to the map structure based on location and timestamp.
     * 
     * @param location           Place where weather forecast data is recorded
     * @param dayMonthTimestamp  Day.month timestamp indicating the date of the forecast
     * @param hourTimestamp      Hour.minute timestamp indicating the time of the forecast
     * @param weatherData        WeatherData object containing the forecast information
     */
    public static void addWeatherForecast(String location, String dayMonthTimestamp, String hourTimestamp, WeatherData weatherData) {
        
        HashMap<String, TreeMap<String, WeatherData>> innerMap = weatherForecastMap.getOrDefault(location, new HashMap<>());
        TreeMap<String, WeatherData> forecastInnerMap = innerMap.getOrDefault(dayMonthTimestamp, new TreeMap<>());

        forecastInnerMap.put(hourTimestamp, weatherData);
        innerMap.put(dayMonthTimestamp, forecastInnerMap);
        weatherForecastMap.put(location, innerMap);
    }
    
    /**
    * Retrieves the map structure containing current weather data.
    * 
    * @return The map structure containing current weather data
    */
    public static HashMap<String, HashMap<String, WeatherData>> getWeatherDataMap() {
        return currentWeatherMap;
    }
    
    /**
    * Retrieves the map structure containing weather forecast data.
    * 
    * @return The map structure containing weather forecast data
    */
    public static HashMap<String, HashMap<String, TreeMap<String, WeatherData>>> getWeatherForecastMap() {
        return weatherForecastMap;
    }
    
    
    /**
     * Retrieves current weather data for a specific location and timestamp.
     * 
     * @param location   Place where the weather data is recorded
     * @param timestamp  Timestamp indicating when the data was retrieved
     * @return           The WeatherData object containing the current weather information,
     *                   or null if the data is not available
     */
    public static WeatherData getCurrentData(String location, String timestamp) {
        
        if (!currentWeatherMap.containsKey(location)) {
          return null;
        }
        HashMap<String, WeatherData> locationInfo = currentWeatherMap.get(location);

        if (!locationInfo.containsKey(timestamp)) {
          return null;
        }
        return locationInfo.get(timestamp);
    }
    
    
    /**
     * Retrieves weather forecast data for a specific location, date, and time.
     * 
     * @param location          Place where the weather forecast data is recorded
     * @param dayMonthStamp     Day.month timestamp indicating the date of the forecast
     * @param hourMinuteStamp   Hour.minute timestamp indicating the time of the forecast
     * @return                  The WeatherData object containing the forecast information,
     *                          or null if the data is not available
     */
    public static WeatherData getForecastData(String location, String dayMonthStamp, String hourMinuteStamp) {
        
        if (!weatherForecastMap.containsKey(location)) {
            return null;
        }
        
        HashMap<String, TreeMap<String, WeatherData>> innerMap = weatherForecastMap.get(location);
        if (!innerMap.containsKey(dayMonthStamp)) {
            return null;
        }
        
        TreeMap<String, WeatherData> forecastInnerMap = innerMap.get(dayMonthStamp);
        WeatherData weatherData = forecastInnerMap.get(hourMinuteStamp);

        return weatherData;
    }
    
    
    /**
     * Converts a temperature in Kelvin to Celsius.
     *
     * @param kelvin The temperature in Kelvin (double).
     * @return The temperature in Celsius (double).
     */
    public static double kelvinToCelsius(double kelvin) {
        double celsius = kelvin - 273.15;
        return Math.round(celsius * 10) / 10.0; // Round to one decimal place
    }
    
    /**
    * Converts a temperature in Celsius to Kelvin.
    *
    * @param celsius The temperature in Celsius (double).
    * @return The temperature in Kelvin (double).
    */
    public static double celsiusToKelvin(double celsius) {
        double kelvin = celsius + 273.15;
        return Math.round(kelvin * 10) / 10.0; // Round to one decimal place
    }

    
    /**
    * Adds JSON data from the specified file to the map structure.
    * if fileName contains current it will add data to currentWeatherMap
    * if fileName contains forecast it will add data to weatherForecastMap.
    * 
    * @param fileName   The name of the file containing JSON data
    * @throws           Exception If an error occurs while processing the JSON data
    */
    public void addJsonDataToMap(String fileName) throws Exception {
        try {
            String[] parts = fileName.split("_");
            String location = parts[0];

            if (parts[1].equals("current")) {                
                String timestamp = parts[2] + "_" + parts[3].replace(".json", "");
                FileReader reader = new FileReader(fileName);
                JsonObject jsonObject = JsonParser.parseReader(reader).getAsJsonObject();

                String weatherMain = jsonObject.getAsJsonArray("weather").get(0).getAsJsonObject().get("main").getAsString();
                String weatherDescription = jsonObject.getAsJsonArray("weather").get(0).getAsJsonObject().get("description").getAsString();
                String weatherIcon = jsonObject.getAsJsonArray("weather").get(0).getAsJsonObject().get("icon").getAsString();
                double temperatureKelvin = jsonObject.getAsJsonObject("main").get("temp").getAsDouble();
                double feelsLikeKelvin = jsonObject.getAsJsonObject("main").get("feels_like").getAsDouble();
                double minTemperatureKelvin = jsonObject.getAsJsonObject("main").get("temp_min").getAsDouble();
                double maxTemperatureKelvin = jsonObject.getAsJsonObject("main").get("temp_max").getAsDouble();
                double windSpeed = jsonObject.getAsJsonObject("wind").get("speed").getAsDouble();
                int windDirection = jsonObject.getAsJsonObject("wind").get("deg").getAsInt();
                
                // In json there can be rain or snow, but in our code those are similar
                // and both will increase rainAmount
                // if rain is not found it will be zero
                int rainAmount = 0;
                if (jsonObject.has("rain")) {
                    try {
                        rainAmount = jsonObject.getAsJsonObject("rain").get("1h").getAsInt();
                    } catch (Exception e) {
                        throw new Exception("Error parsing rain amount: " + e.getMessage());
                    }
                }
                if (jsonObject.has("snow")) {
                    try {
                        rainAmount = jsonObject.getAsJsonObject("snow").get("1h").getAsInt();
                    } catch (Exception e) {
                        throw new Exception("Error parsing snow amount: " + e.getMessage());
                    }
                }
                
                // convert Json data from Kelvin to Celsius
                double temperature = kelvinToCelsius(temperatureKelvin);
                double feelsLike = kelvinToCelsius(feelsLikeKelvin);
                double minTemperature = kelvinToCelsius(minTemperatureKelvin);
                double maxTemperature = kelvinToCelsius(maxTemperatureKelvin);
                
                // create weatherData object
                WeatherData weatherData = new WeatherData(weatherMain, weatherDescription, weatherIcon, temperature, feelsLike,
                        minTemperature, maxTemperature, windSpeed, windDirection, rainAmount);

                addWeatherData(location, timestamp, weatherData);
                
            // almoust same than current, but do the same for every hour and multiple days
            } else if (parts[1].equals("forecast")) {
                FileReader reader = new FileReader(fileName);
                JsonObject jsonObject = JsonParser.parseReader(reader).getAsJsonObject();
                JsonArray forecastList = jsonObject.getAsJsonArray("list");
                
                for (int i = 0; i < forecastList.size(); i++) {                         
                    JsonObject forecastObj = forecastList.get(i).getAsJsonObject();
                    String fullTimestamp = forecastObj.get("dt_txt").getAsString();
                    String[] timesparts = fullTimestamp.split(" ");
                    String[] dateParts = timesparts[0].split("-");
                    String forecastTimestamp = dateParts[2] + "." + dateParts[1];
                    String hourTimestamp = timesparts[1].substring(0, 5);
                    
                    String weatherMain = forecastObj.getAsJsonArray("weather").get(0).getAsJsonObject().get("main").getAsString();
                    String weatherDescription = forecastObj.getAsJsonArray("weather").get(0).getAsJsonObject().get("description").getAsString();
                    String weatherIcon = forecastObj.getAsJsonArray("weather").get(0).getAsJsonObject().get("icon").getAsString();
                    double temperatureKelvin  = forecastObj.getAsJsonObject("main").get("temp").getAsDouble();
                    double feelsLikeKelvin = forecastObj.getAsJsonObject("main").get("feels_like").getAsDouble();
                    double minTemperatureKelvin = forecastObj.getAsJsonObject("main").get("temp_min").getAsDouble();
                    double maxTemperatureKelvin = forecastObj.getAsJsonObject("main").get("temp_max").getAsDouble();
                    double windSpeed = forecastObj.getAsJsonObject("wind").get("speed").getAsDouble();
                    int windDirection = forecastObj.getAsJsonObject("wind").get("deg").getAsInt();
                    
                    double rainAmount = 0;
                    if (forecastObj.has("rain")) {
                        try {
                            rainAmount = forecastObj.getAsJsonObject("rain").get("1h").getAsDouble();
                        } catch (Exception e) {
                            throw new Exception("Error parsing rain amount: " + e.getMessage());
                        }
                    }
                    if (forecastObj.has("snow")) {
                        try {
                            rainAmount = forecastObj.getAsJsonObject("snow").get("1h").getAsDouble();
                        } catch (Exception e) {
                            throw new Exception("Error parsing snow amount: " + e.getMessage());
                        }
                    }
                    
                    double temperature = kelvinToCelsius(temperatureKelvin);
                    double feelsLike = kelvinToCelsius(feelsLikeKelvin);
                    double minTemperature = kelvinToCelsius(minTemperatureKelvin);
                    double maxTemperature = kelvinToCelsius(maxTemperatureKelvin);
                    
                    WeatherData weatherData = new WeatherData(weatherMain, weatherDescription, weatherIcon, temperature, feelsLike,
                        minTemperature, maxTemperature, windSpeed, windDirection, rainAmount);

                    addWeatherForecast(location, forecastTimestamp, hourTimestamp, weatherData);
                }
            } else {
                throw new Exception("Invalid file type: " + parts[1]);
            }
        } catch (IOException e) {
            throw new Exception("Error reading file: " + e.getMessage());
        }
    }
    
    /**
     * Retrieves the current temperature for a specific location and timestamp.
     * 
     * @param location   Place where the weather data is recorded
     * @param timestamp  Timestamp indicating when the data was retrieved
     * @return           The current temperature in Celsius,
     *                   or Double. NaN if the data is not available
     */
    public double getCurrentTemperature(String location, String timestamp) {

        if (!currentWeatherMap.containsKey(location)) {
            return Double.NaN;
        }

        HashMap<String, WeatherData> locationInfo = currentWeatherMap.get(location);
        
        if (!locationInfo.containsKey(timestamp)) {
            return Double.NaN;
        }
        WeatherData weatherData = locationInfo.get(timestamp);

        return weatherData.getTemperature();
    }

    /**
     * Retrieves the current feelslike temperature for a specific location and timestamp.
     * 
     * @param location   Place where the weather data is recorded
     * @param timestamp  Timestamp indicating when the data was retrieved
     * @return           The current feelslike temperature in Celsius,
     *                   or Double. NaN if the data is not available
     */
    public double getCurrentFeelsLike(String location, String timestamp) {

        if (!currentWeatherMap.containsKey(location)) {
          return Double.NaN;
        }

        HashMap<String, WeatherData> locationInfo = currentWeatherMap.get(location);

        if (!locationInfo.containsKey(timestamp)) {
          return Double.NaN;
        }
        WeatherData weatherData = locationInfo.get(timestamp);
           
        return weatherData.getFeelsLike();  
    }

    /**
     * Retrieves the current wind direction for a specific location and timestamp.
     * 
     * @param location   Place where the weather data is recorded
     * @param timestamp  Timestamp indicating when the data was retrieved
     * @return           The current wind direction in number,
     *                   or -1 if the data is not available
     */
    public int getCurrentWindDir(String location, String timestamp) {

        if (!currentWeatherMap.containsKey(location)) {
          return -1;
        }
        
        HashMap<String, WeatherData> locationInfo = currentWeatherMap.get(location);

        if (!locationInfo.containsKey(timestamp)) {
          return -1;
        }
        WeatherData weatherData = locationInfo.get(timestamp);

        return weatherData.getWindDirection();
    }

    
    /**
     * Retrieves the current wind velocity for a specific location and timestamp.
     * 
     * @param location   Place where the weather data is recorded
     * @param timestamp  Timestamp indicating when the data was retrieved
     * @return           The current wind velocity in double meters per second,
     *                   or Double. NaN if the data is not available
     */
    public double getCurrentWindVelo(String location, String timestamp) {

        if (!currentWeatherMap.containsKey(location)) {
          return Double.NaN;
        }

        HashMap<String, WeatherData> locationInfo = currentWeatherMap.get(location);

        if (!locationInfo.containsKey(timestamp)) {
          return Double.NaN;
        }
        WeatherData weatherData = locationInfo.get(timestamp);

        return weatherData.getWindSpeed();
    }

    /**
     * Retrieves the current weather description for a specific location and timestamp.
     * 
     * @param location   Place where the weather data is recorded
     * @param timestamp  Timestamp indicating when the data was retrieved
     * @return           The current weather description in string,
     *                   or null if the data is not available
     */
    public String getCurrentDesc(String location, String timestamp) {

        if (!currentWeatherMap.containsKey(location)) {
          return null;
        }

        HashMap<String, WeatherData> locationInfo = currentWeatherMap.get(location);

        if (!locationInfo.containsKey(timestamp)) {
          return null;
        }
        WeatherData weatherData = locationInfo.get(timestamp);

        return weatherData.getWeatherDescription();
    }
    
    /**
     * Retrieves the current rain amount for a specific location and timestamp.
     * 
     * @param location   Place where the weather data is recorded
     * @param timestamp  Timestamp indicating when the data was retrieved
     * @return           The current rain amount in double,
     *                   or Double. NaN if the data is not available
     */
    public double getCurrentRainAmount(String location, String timestamp) {

        if (!currentWeatherMap.containsKey(location)) {
          return Double.NaN;
        }

        HashMap<String, WeatherData> locationInfo = currentWeatherMap.get(location);

        if (!locationInfo.containsKey(timestamp)) {
          return Double.NaN;
        }
        WeatherData weatherData = locationInfo.get(timestamp);

        return weatherData.getRainAmount();
    }

    /**
     * Retrieves the current rain amount for a specific location and timestamp.
     * weather icon will be later transfered into picture, but now we just get iconUrl
     * 
     * @param location   Place where the weather data is recorded
     * @param timestamp  Timestamp indicating when the data was retrieved
     * @return           The current weather iconurl in string,
     *                   or null if the data is not available
     */
    public String getCurrentWeatherIcon(String location, String timestamp) {

        if (!currentWeatherMap.containsKey(location)) {
          return null;
        }

        HashMap<String, WeatherData> locationInfo = currentWeatherMap.get(location);

        if (!locationInfo.containsKey(timestamp)) {
          return null;
        }
        WeatherData weatherData = locationInfo.get(timestamp);

        String iconUrl = "http://openweathermap.org/img/wn/" + weatherData.getWeatherIcon() + ".png";

        return iconUrl;
    }

    
    // Above current
    // Below forecast
    
    
    /**
     * Retrieves the temperature forecast for a specific location, day, and hour.
     * 
     * @param location   Place where the weather forecast data is recorded
     * @param labelDay   Day label for the forecast (relative to current date)
     * @param i          Hour index for the forecast (0-23)
     * @return           The forecasted temperature in Celsius,
     *                   or Double. NaN if the data is not available
     */
    public double getForecastTemp(String location, String labelDay, int i) {
        
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM");

        LocalDate targetDate = currentDate.plusDays(Long.parseLong(labelDay));
        String targetDay = targetDate.format(formatter);

        if (!weatherForecastMap.containsKey(location) || !weatherForecastMap.get(location).containsKey(targetDay)) {
            return Double.NaN;
        }

        TreeMap<String, WeatherData> forecastInnerMap = weatherForecastMap.get(location).get(targetDay);

        String hour = String.format("%02d:00", i);

        if (!forecastInnerMap.containsKey(hour)) {
            return Double.NaN;
        }

        WeatherData weatherData = forecastInnerMap.get(hour);

        return weatherData.getTemperature();
    }
                    
    /**
     * Retrieves the maximum temperature forecast for a specific location, day, and hour.
     * 
     * @param location   Place where the weather forecast data is recorded
     * @param labelDay   Day label for the forecast (relative to current date)
     * @param i          Hour index for the forecast (0-23)
     * @return           The forecasted maximum temperature in Celsius,
     *                   or Double. NaN if the data is not available
     */
    public double getForecastMaxTemp(String location, String labelDay, int i) {

        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM");

        LocalDate targetDate = currentDate.plusDays(Long.parseLong(labelDay));
        String targetDay = targetDate.format(formatter);

        if (!weatherForecastMap.containsKey(location) || !weatherForecastMap.get(location).containsKey(targetDay)) {
            return Double.NaN; 
        }

        TreeMap<String, WeatherData> forecastInnerMap = weatherForecastMap.get(location).get(targetDay);

        String hour = String.format("%02d:00", i);

        if (!forecastInnerMap.containsKey(hour)) {
            return Double.NaN;
        }

        WeatherData weatherData = forecastInnerMap.get(hour);

        return weatherData.getMaxTemperature();
    }

    /**
     * Retrieves the minimum temperature forecast for a specific location, day, and hour.
     * 
     * @param location   Place where the weather forecast data is recorded
     * @param labelDay   Day label for the forecast (relative to current date)
     * @param i          Hour index for the forecast (0-23)
     * @return           The forecasted minimum temperature in Celsius,
     *                   or Double. NaN if the data is not available
     */
    public double getForecastMinTemp(String location, String labelDay, int i) {
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM");

        LocalDate targetDate = currentDate.plusDays(Long.parseLong(labelDay));
        String targetDay = targetDate.format(formatter);

        if (!weatherForecastMap.containsKey(location) || !weatherForecastMap.get(location).containsKey(targetDay)) {
            return Double.NaN; 
        }

        TreeMap<String, WeatherData> forecastInnerMap = weatherForecastMap.get(location).get(targetDay);

        String hour = String.format("%02d:00", i);

        if (!forecastInnerMap.containsKey(hour)) {
            return Double.NaN;
        }

        WeatherData weatherData = forecastInnerMap.get(hour);

        return weatherData.getMinTemperature();
    }

    /**
     * Retrieves the amount of rain forecast for a specific location, day, and hour.
     * 
     * @param location   Place where the weather forecast data is recorded
     * @param labelDay   Day label for the forecast (relative to current date)
     * @param i          Hour index for the forecast (0-23)
     * @return           The forecasted amount of rain in millimeters,
     *                   or Double. NaN if the data is not available
     */
    public double getForecastRainAmount(String location, String labelDay, int i) {
        
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM");

        LocalDate targetDate = currentDate.plusDays(Long.parseLong(labelDay));
        String targetDay = targetDate.format(formatter);

        if (!weatherForecastMap.containsKey(location) || !weatherForecastMap.get(location).containsKey(targetDay)) {
            return Double.NaN;
        }

        TreeMap<String, WeatherData> forecastInnerMap = weatherForecastMap.get(location).get(targetDay);

        String hour = String.format("%02d:00", i);

        if (!forecastInnerMap.containsKey(hour)) {
            return Double.NaN;
        }

        WeatherData weatherData = forecastInnerMap.get(hour);

        return weatherData.getRainAmount();
    }

    /**
     * Retrieves the wind direction forecast for a specific location, day, and hour.
     * 
     * @param location   Place where the weather forecast data is recorded
     * @param labelDay   Day label for the forecast (relative to current date)
     * @param i          Hour index for the forecast (0-23)
     * @return           The forecasted wind direction in degrees,
     *                   or -1 if the data is not available
     */
    public int getForecastWindDir(String location, String labelDay, int i) {

        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM");

        LocalDate targetDate = currentDate.plusDays(Long.parseLong(labelDay));
        String targetDay = targetDate.format(formatter);

        if (!weatherForecastMap.containsKey(location) || !weatherForecastMap.get(location).containsKey(targetDay)) {
            return -1; 
        }

        TreeMap<String, WeatherData> forecastInnerMap = weatherForecastMap.get(location).get(targetDay);

        String hour = String.format("%02d:00", i);

        if (!forecastInnerMap.containsKey(hour)) {
            return -1; 
        }

        WeatherData weatherData = forecastInnerMap.get(hour);

        return weatherData.getWindDirection();
    }

    
    /**
     * Retrieves the wind speed forecast for a specific location, day, and hour.
     * 
     * @param location   Place where the weather forecast data is recorded
     * @param labelDay   Day label for the forecast (relative to current date)
     * @param i          Hour index for the forecast (0-23)
     * @return           The forecasted wind speed in meters per second,
     *                   or Double. NaN if the data is not available
     */
    public double getForecastWindVelo(String location, String labelDay, int i) {

        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM");

        LocalDate targetDate = currentDate.plusDays(Long.parseLong(labelDay));
        String targetDay = targetDate.format(formatter);

        if (!weatherForecastMap.containsKey(location) || !weatherForecastMap.get(location).containsKey(targetDay)) {
            return Double.NaN;
        }

        TreeMap<String, WeatherData> forecastInnerMap = weatherForecastMap.get(location).get(targetDay);

        String hour = String.format("%02d:00", i);

        if (!forecastInnerMap.containsKey(hour)) {
            return Double.NaN;
        }

        WeatherData weatherData = forecastInnerMap.get(hour);

        return weatherData.getWindSpeed();
    }

    /**
     * Retrieves the description of the weather forecast for a specific location, day, and hour.
     * 
     * @param location   Place where the weather forecast data is recorded
     * @param labelDay   Day label for the forecast (relative to current date)
     * @param i          Hour index for the forecast (0-23)
     * @return           The description of the weather forecast,
     *                   or an empty string if the data is not available
     */
    public String getForecastDesc(String location, String labelDay, int i) {

        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM");

        LocalDate targetDate = currentDate.plusDays(Long.parseLong(labelDay));
        String targetDay = targetDate.format(formatter);

        if (!weatherForecastMap.containsKey(location) || !weatherForecastMap.get(location).containsKey(targetDay)) {
            return ""; 
        }

        TreeMap<String, WeatherData> forecastInnerMap = weatherForecastMap.get(location).get(targetDay);

        String hour = String.format("%02d:00", i);

        if (!forecastInnerMap.containsKey(hour)) {
            return ""; 
        }

        WeatherData weatherData = forecastInnerMap.get(hour);

        return weatherData.getWeatherDescription();
    }  
    
    
    /**
     * Retrieves the URL of the weather icon for a specific location, day, and hour.
     * 
     * @param location   Place where the weather forecast data is recorded
     * @param labelDay   Day label for the forecast (relative to current date)
     * @param i          Hour index for the forecast (0-23)
     * @return           The URL of the weather icon,
     *                   or an empty string if the data is not available
     */
    public String getForecastWeatherIcon(String location, String labelDay, int i) {

        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM");

        LocalDate targetDate = currentDate.plusDays(Long.parseLong(labelDay));
        String targetDay = targetDate.format(formatter);

        if (!weatherForecastMap.containsKey(location) || !weatherForecastMap.get(location).containsKey(targetDay)) {
            return ""; 
        }

        TreeMap<String, WeatherData> forecastInnerMap = weatherForecastMap.get(location).get(targetDay);

        String hour = String.format("%02d:00", i);

        if (!forecastInnerMap.containsKey(hour)) {
            return ""; 
        }

        WeatherData weatherData = forecastInnerMap.get(hour);
        
        String iconUrl = "http://openweathermap.org/img/wn/" + weatherData.getWeatherIcon() + ".png";

        return iconUrl;
    } 
}
