package fi.tuni.prog3.weatherapp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test files can be runed in consol, when you write 'mvn test'
 * User may need to delete Favourites.json and History.json so test works
 * Test class for the WeatherDataMap class, focusing on key methods such as map structure creation
 * and adding data to JSON.
 * 
 * Testing has not been done for the weatherForecast section because manually adding data would be very challenging,
 * bacause of that we cannot test the return values of forecasts. 
 * However, we are very confident that the methods work, as they have been extensively tested on the UI side."
 * @author Patrik Salmensaari
 */
public class WeatherDataMapTest {

    private WeatherDataMap weatherDataMap;

    /**
     * Initializes the WeatherDataMap object before each test.
     */
    @BeforeEach
    public void setUp() {
        weatherDataMap = new WeatherDataMap();
    }

    /**
     * Test if the addWeatherData method adds data correctly to the current weather map structure.
     */
    @Test
    public void testAddWeatherData() {
        WeatherData weatherData = new WeatherData("Clear", "Clear Sky", "01d", 2.0, 3.0, 4.0, 5.0, 6.0, 70, 8);
        weatherDataMap.addWeatherData("Helsinki", "22.04_10.30", weatherData);
        assertNotNull(weatherDataMap.getWeatherDataMap().get("Helsinki"));
        assertNotNull(weatherDataMap.getWeatherDataMap().get("Helsinki").get("22.04_10.30"));
        assertEquals(weatherData, weatherDataMap.getWeatherDataMap().get("Helsinki").get("22.04_10.30"));
    }
    
    /**
     * Test if the getCurrentData method retrieves correct current weather data.
     */
    @Test
    public void testGetCurrentData() {
        WeatherData weatherData = new WeatherData("Rain", "Light rain", "10d", 1.0, 2.0, 3.0, 4.0, 5.0, 60, 7);
        weatherDataMap.addWeatherData("Helsinki", "22.04_12.00", weatherData);
        assertEquals(weatherData, weatherDataMap.getCurrentData("Helsinki", "22.04_12.00"));
        assertNull(weatherDataMap.getCurrentData("Helsinki", "22.04_10.30"));
        assertNull(weatherDataMap.getCurrentData("Tampere", "22.04_12.00"));
    }
    
    
    
    /**
     * Test method for retrieving the current weather icon.
     */
    @Test
    public void testGetCurrentWeatherIcon() {
        // Add test data for a specific location and timestamp
        String location = "Helsinki";
        String timestamp = "22.04_12.30";
        WeatherData weatherData = new WeatherData("Clear", "Clear sky", "01d",
                15.0, 14.0, 13.0, 16.0, 5.0, 180, 0);

        WeatherDataMap.addWeatherData(location, timestamp, weatherData);

        // Retrieve the weather icon for the specified location and timestamp
        String iconUrl = weatherDataMap.getCurrentWeatherIcon(location, timestamp);

        // Verify that the icon URL is not null
        assertNotNull(iconUrl);

        // Verify that the icon URL format is correct
        assertTrue(iconUrl.startsWith("http://openweathermap.org/img/wn/"));
        assertTrue(iconUrl.endsWith(".png"));
    }

    /**
     * Test method for retrieving the current rain amount.
     */
    @Test
    public void testGetCurrentRainAmount() {
        // Add test data for a specific location and timestamp
        String location = "Tampere";
        String timestamp = "24.04_18.35";
        WeatherData weatherData = new WeatherData("Snow", "Light snow", "13d",
                15.0, 16.0, 17.0, 18.0, 19.2, 195, 19.0);

        WeatherDataMap.addWeatherData(location, timestamp, weatherData);
        
        
        // Retrieve the rain amount for the specified location and timestamp
        double rainAmount = weatherDataMap.getCurrentRainAmount(location, timestamp);

        // Verify that the rain amount is correct
        assertEquals(19.0, rainAmount);
    }
}
