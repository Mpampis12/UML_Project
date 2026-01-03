package services.transfer;

public interface TransferMechanism {
    void executeTransfer(double amount, String sourceIban, String targetIban, String name, String bic, String bankName, String address, String country) throws Exception;
}