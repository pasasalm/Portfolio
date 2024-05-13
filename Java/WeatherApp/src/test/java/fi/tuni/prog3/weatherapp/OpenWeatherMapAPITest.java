package fi.tuni.prog3.weatherapp;

import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test files can be runed in consol, when you write 'mvn test'
 * User may need to delete Favourites.json and History.json so test works
 * This is a test class testing iAPI focusing on OpenWeatherMapAPI
 * It will check that method works corretly
 * @author Patrik Salmensaari
 */
public class OpenWeatherMapAPITest {

    
    @Test
    /**
     * Tests the functionality of the lookUpLocationCoordinates method in the OpenWeatherMapAPI class.
     * It verifies the behavior with both existing and non-existing city names.
     */
    public void testLookUpLocationCoordinates() {
        iAPI.OpenWeatherMapAPI api = new iAPI.OpenWeatherMapAPI();
        
        // Test with existing city
        ArrayList<Double> coordinates = api.lookUpLocationCoordinates("Helsinki");
        assertNotNull(coordinates);
        assertTrue(coordinates.size() == 2);
        assertFalse(coordinates.get(0) == 0.0 && coordinates.get(1) == 0.0);
        
        // Test with non-existing city
        ArrayList<Double> invalidCoordinates = api.lookUpLocationCoordinates("NonExistingCity");
        assertNotNull(invalidCoordinates);
        assertTrue(invalidCoordinates.size() == 2);
        assertTrue(invalidCoordinates.get(0) == 0.0 && invalidCoordinates.get(1) == 0.0);
    }
    

    @Test
    /**
     * Tests the functionality of the lookUpLocationName method in the OpenWeatherMapAPI class.
     * It verifies the behavior with both existing and non-existing coordinates.
     */
    public void testLookUpLocationName() {
        iAPI.OpenWeatherMapAPI api = new iAPI.OpenWeatherMapAPI();
        
        // Test with existing coordinates
        String cityName = api.lookUpLocationName(60.16664, 24.94353);
        assertNotNull(cityName);
        assertEquals("Helsinki", cityName);
        
        // Test with non-existing coordinates
        String invalidCityName = api.lookUpLocationName(0.0, 0.0);
        assertNotNull(invalidCityName);
        assertEquals("Not found", invalidCityName);
    }

    @Test
    /**
     * Tests the functionality of the getCurrentWeather method in the OpenWeatherMapAPI class.
     * It verifies the behavior with both existing and non-existing coordinates.
     */
    public void testGetCurrentWeather() {
        iAPI.OpenWeatherMapAPI api = new iAPI.OpenWeatherMapAPI();
        
        // Test with existing coordinates
        String weatherData = api.getCurrentWeather(60.16664, 24.94353);
        assertNotNull(weatherData);
        // Assert other conditions as needed
        
        // Test with non-existing coordinates
        String invalidWeatherData = api.getCurrentWeather(0.0, 0.0);
        assertNull(invalidWeatherData);
    }

    @Test
    /**
    * Tests the functionality of the getForecast method in the OpenWeatherMapAPI class.
    * It verifies the behavior with both existing and non-existing coordinates.
    */
    public void testGetForecast() {
        iAPI.OpenWeatherMapAPI apiTest = new iAPI.OpenWeatherMapAPI();
        
        // Test with existing coordinates
        String forecastData = apiTest.getForecast(60.16664, 24.94353);
        assertNotNull(forecastData);
        // Assert other conditions as needed
        
        // Test with non-existing coordinates
        String invalidForecastData = apiTest.getForecast(0.0, 0.0);
        assertNull(invalidForecastData);
    }
}
