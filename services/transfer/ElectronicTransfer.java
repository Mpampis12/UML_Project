package services.transfer;

public class ElectronicTransfer extends TransferAbstraction {
    public ElectronicTransfer(TransferMechanism mechanism) {
        super(mechanism);
    }

    @Override
    public void makeTransfer(double amount, String source, String target, String name, String bic, String bank, String addr, String country) throws Exception {
        // Εδώ καλούμε την υλοποίηση
        mechanism.executeTransfer(amount, source, target, name, bic, bank, addr, country);
    }
}