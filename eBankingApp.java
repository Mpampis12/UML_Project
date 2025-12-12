import view.BankView;
import services.BankSystem;
import DAO.DaoHandler;

public class eBankingApp {
    public static void main(String[] args) {
        DaoHandler dao = new DaoHandler();
        dao.loadAllData();
        

        javax.swing.SwingUtilities.invokeLater(() -> {
            new BankView();
            dao.saveAllData();
        });
        
    }
}