package services.transfer;

import services.BankSystem;

public class InternalTransferMechanism implements TransferMechanism {
    @Override
    public void executeTransfer(double amount, String sourceIban, String targetIban, String name, String bic, String bankName, String address, String country) throws Exception {
        // Χρήση του TransactionManager για εσωτερική μεταφορά
        BankSystem.getInstance().getTransactionManager().transfer(sourceIban, targetIban, amount, "Internal Transfer", BankSystem.getInstance().getTimeSimulator().getCurrentDate());
    }
}