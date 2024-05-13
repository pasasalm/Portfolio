package fi.tuni.prog3.weatherapp;

/**
 * Class to represent the feature structure in the Json file municipalityCoordinates.
 * Used by Gson to read the Json file.
 * 
 * @author Tommi Paavola
 */
public class Feature {
    public String type;
    public Geometry geometry;
    public Properties properties;
}
