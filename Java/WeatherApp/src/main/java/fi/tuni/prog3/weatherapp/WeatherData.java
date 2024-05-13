package fi.tuni.prog3.weatherapp;

/**
 * Class which is used to create weatherData objects from Json data
 * @author Patrik Salmensaari
 */
public class WeatherData {
    private String weatherMain;
    private String weatherDescription;
    private String weatherIcon;
    private double temperature;
    private double feelsLike;
    private double minTemperature;
    private double maxTemperature;
    private double windSpeed;
    private int windDirection;
    private double rainAmount;

    /**
     * Constructor creates a new WeatherData object.
     * 
     * @param weatherMain           Main weather phenomenon ("Rain", "Snow")
     * @param weatherDescription    Description of the weather
     * @param weatherIcon           Code of icon which describe the weather
     * @param temperature           Temperature in Kelvin
     * @param feelsLike             Feels like Temperature
     * @param minTemperature        Minumun temperature in Kelvin   
     * @param maxTemperature        Maximun temperature in Kelvin
     * @param windSpeed             Wind speed meters per second
     * @param windDirection         From which direction the wind is blowing
     * @param rainAmount            How much rain is expected to rain in one hour
     */
    public WeatherData(String weatherMain, String weatherDescription, String weatherIcon, double temperature, double feelsLike,
                       double minTemperature, double maxTemperature, double windSpeed, int windDirection, double rainAmount) {
        
        this.weatherMain = weatherMain;
        this.weatherDescription = weatherDescription;
        this.weatherIcon = weatherIcon;
        this.temperature = temperature;
        this.feelsLike = feelsLike;
        this.minTemperature = minTemperature;
        this.maxTemperature = maxTemperature;
        this.windSpeed = windSpeed;
        this.windDirection = windDirection;
        this.rainAmount = rainAmount;
    }

    /**
     * returns Main weather phenomenon
     * @return Main weather phenomenon
     */
    public String getWeatherMain() {
        return weatherMain;
    }

    /**
     * set weatherMain
     * @param weatherMain Main weather phenomenon
     */
    public void setWeatherMain(String weatherMain) {
        this.weatherMain = weatherMain;
    }

    /**
     * Returns description of the weather.
     * @return Description of weather
     */
    public String getWeatherDescription() {
        return weatherDescription;
    }

    /**
     * set weatherDescription 
     * @param weatherDescription Description of weather
     */
    public void setWeatherDescription(String weatherDescription) {
        this.weatherDescription = weatherDescription;
    }
    
    /**
     * Returns code for icon of weather description
     * @return Code of weather icon
     */
    public String getWeatherIcon() {
        return weatherIcon;
    }
    
    /**
     * Set code of weather icon
     * @param weatherIcon Code of weather icon.
     */
    public void setWeatherIcon(String weatherIcon) {
        this.weatherIcon = weatherIcon;
    }

    /**
     * returns temperature
     * @return Temperature
     */
    public double getTemperature() {
        return temperature;
    }

    /**
     * set temperature
     * @param temperature temperature
     */
    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    /**
     * return the "feels like" -temperature
     * @return "feels like" temperature
     */
    public double getFeelsLike() {
        return feelsLike;
    }

    /**
     * set "Feels like" temperature
     * @param feelsLike "feels like" temperature
     */
    public void setFeelsLike(double feelsLike) {
        this.feelsLike = feelsLike;
    }

    /**
     * returns hour's minimum temperature
     * @return hour's minimum temperature
     */
    public double getMinTemperature() {
        return minTemperature;
    }

    /**
     * set minimum temperature
     * @param minTemperature minimum temperature 
     */
    public void setMinTemperature(double minTemperature) {
        this.minTemperature = minTemperature;
    }

    /**
     * returns hour's maximum temperature
     * @return hour's maximum temperature
     */
    public double getMaxTemperature() {
        return maxTemperature;
    }

    /**
     * set maximum temperature
     * @param maxTemperature maximum temperature
     */
    public void setMaxTemperature(double maxTemperature) {
        this.maxTemperature = maxTemperature;
    }

    /**
     * returns hour's wind velocity
     * @return wind velocity
     */
    public double getWindSpeed() {
        return windSpeed;
    }

    /**
     * set windSpeed
     * @param windSpeed wind velocity
     */
    public void setWindSpeed(double windSpeed) {
        this.windSpeed = windSpeed;
    }

    /**
     * return hour's wind direction
     * @return wind direction as degrees
     */
    public int getWindDirection() {
        return windDirection;
    }

    /**
     * set windDirection
     * @param windDirection wind direction as degrees
     */
    public void setWindDirection(int windDirection) {
        this.windDirection = windDirection;
    }
    
    /**
     * return's hour's rain amount
     * @return amout of rain in mm
     */
    public double getRainAmount() {
        return rainAmount;
    }
    
    /**
     * set rainAmount
     * @param rainAmount amount of rain in mm
     */
    public void setRainAmount(double rainAmount) {
        this.rainAmount = rainAmount;
    }   
}