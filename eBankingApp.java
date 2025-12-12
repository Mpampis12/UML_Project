import view.BankView;
import services.BankSystem;
// ... imports για DAO ...

public class eBankingApp {
    public static void main(String[] args) {
        // 1. Φόρτωση Δεδομένων (αν έχεις φτιάξει το DaoHandler)
        // DaoHandler dao = new DaoHandler();
        // dao.loadAllData();
        
        // 2. Εκκίνηση GUI
        // Χρησιμοποιούμε SwingUtilities για thread safety
        javax.swing.SwingUtilities.invokeLater(() -> {
            new BankView();
        });
        
        // ... (Shutdown hook για αποθήκευση) ...
    }
}