package fi.tuni.prog3.weatherapp;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


/**
 * JavaFX Weather Application. This class extends the {@link javafx.application.Application} class
 * and serves as the entry point for the application. It initializes the primary stage and scene
 * using FXML and handles application lifecycle events.
 */
public class WeatherApp extends Application {

    private static Scene scene;
    
    /**
    * Initializes the JavaFX application and sets up the primary stage. 
    * This method is called by the JavaFX runtime when the application is launched.
    * If there is history available it shows the forecast and current weather to the latest city in history.
    * 
    * @param stage the primary stage provided by the JavaFX platform
    * @throws IOException if an error occurs while loading the FXML file
    */
    @Override
    public void start(Stage stage) throws IOException {
        // Show the weather and forecast for last city
        iReadAndWriteToFile reader = new iReadAndWriteToFile();
        List cities = reader.getHistory();
        String cityName ="";
        int size = cities.size();
        
        scene = new Scene(loadFXML("primary"), 640, 480);
                
        if (size <= 0){
            System.out.println("NO history available");
            stage.setScene(scene);
            stage.setTitle("WeatherApp");
            stage.show();
        } else {
            cityName = ((ArrayList<String>) cities).get(0);
        }

        
        FXMLLoader fxmlLoader = new FXMLLoader(WeatherApp.class.getResource("primary.fxml"));
        Parent parent = fxmlLoader.load();      
        PrimaryController primaryController = fxmlLoader.getController();
        primaryController.setCurrentWeather(cityName);
        primaryController.setForecast(cityName);
        primaryController.setHourForecast(cityName);
        primaryController.setDayForecast(cityName);
        scene.setRoot(parent);
        stage.setScene(scene);
        stage.setTitle("WeatherApp");
        stage.show();
    }
    

    /**
    * Updates the application's primary scene to display weather information for the specified city.
    * 
    * @param cityName the name of the city for which to display weather data
    * @throws IOException if an error occurs while loading the FXML file
    */
    static void setRootWithCity(String cityName) throws IOException{
        FXMLLoader fxmlLoader = new FXMLLoader(WeatherApp.class.getResource("primary.fxml"));
        Parent parent = fxmlLoader.load();
        cityName = cityName.substring(0, 1).toUpperCase() + cityName.substring(1).toLowerCase();       
        PrimaryController primaryController = fxmlLoader.getController();
        primaryController.setCurrentWeather(cityName);
        primaryController.setForecast(cityName);
        primaryController.setHourForecast(cityName);
        primaryController.setDayForecast(cityName);
        scene.setRoot(parent);
    }

    /**
     * Sets the root element of the scene to the FXML content loaded from the specified FXML file.
     * 
     * @param fxml  the name of the FXML file (without the ".fxml" extension)
     * @throws IOException if an error occurs while loading the FXML file
     */
    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    /**
     * Loads an FXML file and returns the corresponding FXML root element (Parent object).
     * 
     * @param fxml  the name of the FXML file (without the ".fxml" extension)
     * @return the FXML root element (Parent object) loaded from the FXML file
     * @throws IOException if an error occurs while loading the FXML file
     */
    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(WeatherApp.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }
    
    /**
     * The entry point of the application. This method starts the graphical user interface (GUI)
     * and performs cleanup tasks when the GUI is closed.
     * 
     * @param args command-line arguments (unused in this case)
     */
    public static void main(String[] args) {
        
        // At the time of closing the graphical user interface, this section will be performed.
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            deleteJsonWeatherDataFiles();
        }));

        // Launches the graphical user interface. 
        launch();
    }
    
    // The user may need to grant permission to access the files.
    // In windows it can be done by running this in WeatherApp directory
    // >icacls WeatherApp /grant Everyone:(OI)(CI)F /T
    
    /**
     * Deletes JSON weather data files from the current working directory.
     * Wont work if there are no permission to acces the files
     */
    private static void deleteJsonWeatherDataFiles() {
        // Get the current user's working directory location
        String currentDirectory = System.getProperty("user.dir");
        String directoryPath = currentDirectory;

        // Find .json files from the directory
        File directory = new File(directoryPath);
        if (directory.exists()) {
            // check that there is current or forecast in the name of the file and file is .json file
            File[] files = directory.listFiles((dir, name) -> 
                    name.toLowerCase().endsWith(".json") && (name.toLowerCase().contains("current") || name.toLowerCase().contains("forecast")));

            if (files != null) {
                for (File file : files) {
                    try {
                        // try to delete file
                        boolean deleted = file.delete();
                        
                        // print to output window every file which are deleted
                        if (deleted) {
                            System.out.println("Deleted file: " + file.getName());
                        } else {
                            System.out.println("Failed to delete file: " + file.getName());
                        }
                    // Error handler
                    } catch (SecurityException e) {
                        System.out.println("Security Exception: " + e.getMessage());
                    } catch (Exception e) {
                        System.out.println("An error occurred: " + e.getMessage());
                    }
                }
            }
        } else {
            System.out.println("Error: Directory " + directoryPath + " does not exist.");
        }
    }   
}