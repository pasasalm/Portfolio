package fi.tuni.prog3.weatherapp;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * A class for reading and writing data to JSON files.
 * 
 * @author Patrik Salmensaari
 */
public class iReadAndWriteToFile {
    
    
    private static final int MAX_FAVOURITES = 8;
    private static final String FAVOURITES_FILE = "Favourites.json";
    private static final int MAX_HISTORY = 20;
    private static final String HISTORY_FILE = "History.json";
    
    private List<String> favourites;
    private List<String> history;


    /**
     * Constructor for the iReadAndWriteToFile class.
     * Initializes the favourites and history lists by reading data from JSON files.
     */
    public iReadAndWriteToFile() {
        this.favourites = readFavouritesFromFile();
        this.history = readHistoryFromFile();
    }
    
    /**
     * Reads favourite cities from the Favourites.json file.
     * @return A list of favourite cities.
     */
    private List<String> readFavouritesFromFile() {
        List<String> favouritesList = new ArrayList<>();
        try {
            File file = new File(FAVOURITES_FILE);
            if (file.exists()) {
                JsonArray jsonArray = JsonParser.parseReader(new FileReader(file)).getAsJsonArray();
                for (JsonElement element : jsonArray) {
                    favouritesList.add(element.getAsString());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return favouritesList;
    }

    /**
     * Saves the list of favourite cities to the Favourites.json file.
     */
    private void saveFavouritesToFile() {
        JsonArray jsonArray = new JsonArray();
        for (String favourite : favourites) {
            jsonArray.add(favourite);
        }
        try (FileWriter writer = new FileWriter(FAVOURITES_FILE)) {
            writer.write(jsonArray.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Adds a city to the list of favourite cities.
     * @param cityName The name of the city to add.
     * @return True if the city was successfully added, false otherwise.
     */
    public boolean addFavourite(String cityName) {
        if (favourites.size() < MAX_FAVOURITES && !favourites.contains(cityName)) {
            favourites.add(cityName);
            saveFavouritesToFile();
            return true;
        }
        return false;
    }
    
    /**
     * Checks if the list of favourite cities is full.
     * 
     * @return true if the list is full, false otherwise.
     */
    public boolean isFavouriteFull() {
        return favourites.size() >= MAX_FAVOURITES;
    }

    
    /**
     * Checks if a city is already in the list of favourite cities.
     * 
     * @param cityName The name of the city to check.
     * @return true if the city is in the list, false otherwise.
     */
    public boolean isCityInFavourites(String cityName) {
        return favourites.contains(cityName);
    }

    
    /**
     * Removes a city from the list of favourite cities.
     * @param cityName The name of the city to remove.
     * @return True if the city was successfully removed, false otherwise.
     */
    public boolean removeFavourite(String cityName) {
        if (favourites.remove(cityName)) {
            saveFavouritesToFile();
            return true;
        }
        return false;
    }
    
    /**
     * Retrieves the list of favourite cities.
     * @return The list of favourite cities.
     */
    public List<String> getFavourites() {
        return favourites;
    }
    
    /**
     * Reads the history of searched cities from the History.json file.
     * @return A list of searched cities.
     */
    private List<String> readHistoryFromFile() {
        List<String> historyList = new ArrayList<>();
        try {
            File file = new File(HISTORY_FILE);
            if (file.exists()) {
                JsonArray jsonArray = JsonParser.parseReader(new FileReader(file)).getAsJsonArray();
                for (JsonElement element : jsonArray) {
                    historyList.add(element.getAsString());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return historyList;
    }

    /**
     * Saves the list of searched cities to the History.json file.
     */
    private void saveHistoryToFile() {
        JsonArray jsonArray = new JsonArray();
        for (String city : history) {
            jsonArray.add(city);
        }
        try (FileWriter writer = new FileWriter(HISTORY_FILE)) {
            writer.write(jsonArray.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Adds a city to the search history.
     * @param cityName The name of the city to add.
     * @return True if the city was successfully added to the history, false otherwise.
     */
    public boolean addToHistory(String cityName) {
        history.add(0, cityName);
        if (history.size() > MAX_HISTORY) {
            history.remove(MAX_HISTORY);
        }
        saveHistoryToFile();
        return true;
    }
    
    /**
     * Retrieves the search history.
     * @return The list of searched cities.
     */
    public List<String> getHistory() {
        return history;
    }
}
    