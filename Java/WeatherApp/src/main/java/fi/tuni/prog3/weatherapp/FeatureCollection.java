package fi.tuni.prog3.weatherapp;

import java.util.List;

/**Class to represent the feature collection structure in the Json file 
 * municipalityCoordinates. Used by Gson to read the Json file.
 *
 * @author Tommi Paavola
 */
public class FeatureCollection {
    public String type;
    public List<Feature> features;
}
