
/**
 *
 * @author patri
 */
import java.util.Scanner;

public class sandBox {

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        
        Scanner object = new Scanner(System.in);
        System.out.println("Enter numbers: ");
        
        String userInput = object.nextLine();
        
        String[] numbersAsString = userInput.split(" ");
        
        int inputLength = numbersAsString.length;
        
        double summa = 0;
        for (int i = 0; i < inputLength; i++) {
            try {
                double number = Double.parseDouble(numbersAsString[i]);
                summa += number;
            } catch (NumberFormatException e) {
                System.out.println("Invalid input");
            }
        }
        
        double mean = summa / inputLength; 
        
        System.out.println("Mean: " + mean);
        
        // TODO code application logic here
    }
}
