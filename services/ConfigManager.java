package services;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigManager {
    private static ConfigManager instance;
    private Properties properties;

    // Ιδιωτικός κατασκευαστής (Singleton)
    private ConfigManager() {
        properties = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("services/bank_policy.properties")) {
            if (input == null) {
                System.out.println("Σφάλμα: Το αρχείο bank_policy.properties δεν βρέθηκε! Χρήση default τιμών.");
                 properties.setProperty("busness.fee.monthly", "0.05");
                properties.setProperty("yearly.interest.rate", "5.00");
                return; 

            }
            properties.load(input);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static ConfigManager getInstance() {
        if (instance == null) {
            instance = new ConfigManager();
        }
        return instance;
    }

    // Helper μέθοδος για να παίρνουμε τα νούμερα κατευθείαν ως double
    public double getPropertyAsDouble(String key) {
        String value = properties.getProperty(key);
        if (value != null) {
            return Double.parseDouble(value);
        }
        return 0.0; // Επιστροφή 0 αν δεν βρεθεί το κλειδί
    }
}