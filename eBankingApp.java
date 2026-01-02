import view.BankView;
import services.BankSystem;
import services.TimeSimulator;
import DAO.DaoHandler;

public class eBankingApp {
    public static void main(String[] args) {
       DaoHandler.getInstance().loadAllData();;
         

        javax.swing.SwingUtilities.invokeLater(() -> {
            new BankView();
            DaoHandler.getInstance().saveAllData();
           
        });

        
    }
}