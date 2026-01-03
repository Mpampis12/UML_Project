package services.transfer;

public abstract class TransferAbstraction {
    protected TransferMechanism mechanism;

    public TransferAbstraction(TransferMechanism mechanism) {
        this.mechanism = mechanism;
    }

    public abstract void makeTransfer(double amount, String source, String target, String name, String bic, String bank, String addr, String country) throws Exception;
}