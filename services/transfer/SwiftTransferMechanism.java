package services.transfer;

import services.BankSystem;
import services.BankApiService;

public class SwiftTransferMechanism implements TransferMechanism {
    @Override
    public void executeTransfer(double amount, String sourceIban, String targetIban, String name, String bic, String bankName, String address, String country) throws Exception {
        BankApiService api = BankSystem.getInstance().getBankApiService();
        BankApiService.ApiResponse response = api.sendSwiftTransfer(amount, name, targetIban, bic, bankName, address, country);
        
        BankSystem.getInstance().getTransactionManager().withdraw(sourceIban, amount, "SWIFT Transfer ID: " + response.transaction_id, BankSystem.getInstance().getTimeSimulator().getCurrentDate());
    }
}