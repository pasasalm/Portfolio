package fi.tuni.prog3.weatherapp;

import java.util.List;
/**
 *Class to represent the coordinates in the Json file municipalityCoordinates.
 * Used by Gson to read the Json file.
 * 
 * @author Tommi Paavola
 */
public class Geometry {
    public String type;
    public List<Double> coordinates;
}
