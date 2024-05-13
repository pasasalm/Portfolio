package fi.tuni.prog3.weatherapp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

/** 
 * Test files can be runed in consol, when you write 'mvn test'
 * User may need to delete Favourites.json and History.json so test works
 * This is a test class for testing iReadAndWriteToFile
 * mainly focusing on the functionality related to favorites and history JSON files and their methods.
 * @author Patrik Salmensaari
 */
public class iReadAndWriteToFileTest {

    private iReadAndWriteToFile fileHandler;
    

    @BeforeEach
    /**
     * Sets up the test environment by initializing an instance of iReadAndWriteToFile
     * for testing.
     */
    public void setUp() {
        fileHandler = new iReadAndWriteToFile();
    }

    @Test
    /**
     * Test if the addFavourite method works as it should
     * Check also if isCityInFavourites works as it should
     */
    public void testAddFavourite() {
        assertTrue(fileHandler.addFavourite("Tampere"));
        assertTrue(fileHandler.isCityInFavourites("Tampere"));
    }

    @Test
    /**
     * Test if the addFavourite method works as it should
     * Check also if removeFavourite and isCityInFavourites works as they should
     */
    public void testRemoveFavourite() {
        fileHandler.addFavourite("TestCity");
        assertTrue(fileHandler.removeFavourite("TestCity"));
        assertFalse(fileHandler.isCityInFavourites("TestCity"));
    }

    @Test
    /**
     * Test if the addFavourite method works as it should
     * Check also does isFavouriteFull works correctly
     */
    public void testIsFavouriteFull() {
        // Lisätään 8 suosikkikaupunkia, tarkistetaan sitten, että lista on täynnä
        for (int i = 0; i < 8; i++) {
            fileHandler.addFavourite("CityIsFavouriteFull" + i);
        }
        assertTrue(fileHandler.isFavouriteFull());
    }

    @Test
    /**
     * Test if the addToHistory method works as it should
     */
    public void testAddToHistory() {
        fileHandler.addToHistory("TestAddToHistoryCity");
        List<String> history = fileHandler.getHistory();
        assertTrue(history.contains("TestAddToHistoryCity"));
    }
}
