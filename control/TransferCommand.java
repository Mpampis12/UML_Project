package control;

import services.TransactionManager;

public class TransferCommand implements BankCommandPattern {

    private TransactionManager transactionManager;
    private String sourceIban;
    private String targetIban;
    private double amount;
    private String description;

    public TransferCommand(TransactionManager manager, String sourceIban, String targetIban, double amount, String description) {
        this.transactionManager = manager;
        this.sourceIban = sourceIban;
        this.targetIban = targetIban;
        this.amount = amount;
        this.description = description;
    }

    @Override
    public void execute() throws Exception {
        transactionManager.transfer(sourceIban, targetIban, amount, description);
    }
}